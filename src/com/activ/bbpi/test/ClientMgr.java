package com.activ.bbpi.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.activ.bbpi.auth.AuthUtils;
import com.activ.bbpi.auth.OAuthToken;
import com.activ.bbpi.auth.reg.OAuthReg;
import com.activ.bbpi.bean.ReqBean;
import com.activ.cdaj.data.Record;
import com.activ.cdaj.util.Config;
import com.activ.cdaj.util.Sreq;
import com.activ.cdaj.util.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Managing client requests in client test application. 
 *   
 * @author aj
 *
 */
public class ClientMgr {
	private static final Logger LOG = Logger.getLogger(ClientMgr.class);
	
	//client user
	private String userId;
	//tracking provider user authorization: provider name => providers
	private Map<String,Provider> providers = new LinkedHashMap<String,Provider>();
	
	//api endpoints
	private String reg_url = "http://localhost:8080/bbpi/register";
	private String auth_url = "http://localhost:8080/bbpi/authorize";
	private String token_url = "http://localhost:8080/bbpi/token";
	private String summary_url = "http://localhost:8080/bbpi/record/summary";
	private String list_url = "http://localhost:8080/bbpi/record";

	//track states: state id => state
	private Map<String,State> stateMap = new HashMap<String,State>();
	
	private static ClientMgr _instance;
	
	private ClientMgr(){
		fillProviderUsers();
		reg_url = Config.get().getValue("client", "reg.url");
		auth_url = Config.get().getValue("client", "auth.url");
		token_url = Config.get().getValue("client", "auth.token.url");
		summary_url = Config.get().getValue("client", "data.summary.url");
		list_url = Config.get().getValue("client", "data.list.url");
	}
	
	//get client user and providers for testing
	private void fillProviderUsers(){
		//user
		userId = Config.get().getValue("client", "user.id");
		
		//providers
		Properties props = Config.get().getModProperties("client");
		int n = Config.get().getIntValue("test.provider.count", 1);
		for(int i=1; i<=n; i++){
			Provider p = new Provider();
			p.providerName = props.getProperty("provider."+i+".name");
			p.userId = props.getProperty("provider."+i+".user.id");
			p.userPasswaord = props.getProperty("provider."+i+".user.password");
			providers.put(p.providerName, p);
		}
	}

	public static void init(){
		if(_instance == null){
			_instance = new ClientMgr();
		}
	}
	public static ClientMgr get(){
		return _instance;
	}
	
	/** fill bean with client registration info to display. */
	public void fillClientRegistration(Sreq sreq, ReqBean bean){
		//get registration parameters from config
		Properties props = Config.get().getModProperties("client");
		bean.registerUrl = props.getProperty("reg.url");
		String client_name = props.getProperty("reg.client_name");
    String client_uri = props.getProperty("reg.client_uri");
    bean.params.put(OAuthReg.Request.CLIENT_NAME, client_name);
    bean.params.put(OAuthReg.Request.CLIENT_URI, client_uri);
    bean.params.put(OAuthReg.Request.TOKEN_METHOD, props.getProperty("reg.token_endpoint_auth_method")); //client_secret_basic
    bean.params.put(OAuthReg.Request.REDIRECT_URIS, props.getProperty("reg.redirect_uris"));
    bean.params.put(OAuthReg.Request.RESPONSE_TYPES, props.getProperty("reg.response_types")); //code
    bean.params.put(OAuthReg.Request.GRANT_TYPES, props.getProperty("reg.grant_types")); //authorization_code
    bean.params.put(OAuthReg.Request.SCOPE, props.getProperty("reg.scope"));
    
    //get registration result if any
		OAuthReg reg = AuthUtils.getOAuthRegByName(client_name, client_uri);
		if(reg != null){
			bean.registered = true;
			bean.params.put(OAuthReg.Response.CLIENT_ID, reg.client_id);
			bean.params.put(OAuthReg.Response.CLIENT_SECRET, reg.client_secret);
		}

	}
	
	/**
	 * register client dynamically, implementing BB+ open registration specs. 
	 */
	public void registerClient(ReqBean bean){
		// using httpclient4 
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(reg_url);
		try {
			//get registration parameters from config
			Properties props = Config.get().getModProperties("client");
			
			//create request in json
      ObjectMapper obj_mapper = new ObjectMapper();            
      Map<String,Object> root = new LinkedHashMap<String,Object>();
      root.put(OAuthReg.Request.CLIENT_NAME, props.getProperty("reg.client_name").trim());
      root.put(OAuthReg.Request.CLIENT_URI, props.getProperty("reg.client_uri").trim());
      root.put(OAuthReg.Request.TOKEN_METHOD, props.getProperty("reg.token_endpoint_auth_method").trim()); //client_secret_basic
   
      List<String> redirects = new ArrayList<String>();
      redirects.add(props.getProperty("reg.redirect_uris").trim());
      root.put(OAuthReg.Request.REDIRECT_URIS, redirects);
      
      List<String> rstypes = new ArrayList<String>();
      rstypes.add(props.getProperty("reg.response_types").trim());
      root.put(OAuthReg.Request.RESPONSE_TYPES, rstypes); //code
      
      List<String> gtypes = new ArrayList<String>();
      gtypes.add(props.getProperty("reg.grant_types").trim());
      root.put(OAuthReg.Request.GRANT_TYPES, gtypes); //authorization_code

      root.put(OAuthReg.Request.SCOPE, props.getProperty("reg.scope").trim());
      String json = obj_mapper.writeValueAsString(root); 
      LOG.debug("registration request to "+reg_url+"\n"+json);
      
      StringEntity rq_entity = new StringEntity(json);
      rq_entity.setContentType("application/json");
      httpPost.setEntity(rq_entity);      
      httpPost.addHeader("Accept" , "application/json");
      
			HttpResponse response = httpclient.execute(httpPost);
			StatusLine sl = response.getStatusLine();
			LOG.debug("registration response: "+sl);
			
			//check response status
	    if(sl.getStatusCode() == 200){
		    HttpEntity rs_entity = response.getEntity();
		    String type = rs_entity.getContentType().toString();
	      LOG.debug(type);
	      String body = EntityUtils.toString(rs_entity);
	      LOG.debug("response body:\n"+body);
	    	
	      parseRegistrationResponse(body, bean);
	      
		    // and ensure it is fully consumed
		    EntityUtils.consume(rs_entity);    
	    }else{
	    	bean.appendErrorMessage("Client registration failed. ");
	    }
		}catch(Exception e){
			LOG.error(e,e);
		} finally {
      httpclient.getConnectionManager().shutdown();
		}
	}
	
	//parse json response from registration
	private void parseRegistrationResponse(String body, ReqBean bean) throws Exception {
		ObjectMapper m = new ObjectMapper();
		JsonNode root = m.readTree(body);
		if(root.get("error") != null){
			String error = root.get("error").textValue();
			String desc = root.get("error_description").textValue();
			bean.appendErrorMessage("Error: "+error+". "+desc);
		}else{
			String secret = root.get(OAuthReg.Response.CLIENT_SECRET).textValue();
			String id = root.get(OAuthReg.Response.CLIENT_ID).textValue();
			LOG.debug("client_id: "+id+" client_secret: "+secret);

			//TODO: client to track registration
		}
	}
	
	/** fill bean with user authorization */
	public void fillOauth(Sreq sreq, ReqBean bean){
		bean.userId = userId;
		bean.providers = providers;
	}


	/** start oauth flow, make oauth call-1 */
	public void startOAuth(Sreq sreq, ReqBean bean){
		String sel_provider = sreq.req.getParameter("provider");
		
		//get client registration
		String client_name = Config.get().getValue("client", "reg.client_name").trim();
		String client_uri = Config.get().getValue("client", "reg.client_uri").trim();
		OAuthReg reg = AuthUtils.getOAuthRegByName(client_name, client_uri);
		if(reg == null){
			//check client
			bean.appendErrorMessage("Client application is not registered yet.");
		}else{
			//get oauth params from configuration
			String redirect_uri = Config.get().getValue("client", "auth.redirect_uri").trim();
			String scope = Config.get().getValue("client", "auth.scope").trim();
			String type = Config.get().getValue("client", "auth.response_type").trim();
			
			//create state
			State state = new State();
			state.clientId = reg.client_id;
			state.provider = sel_provider;
			state.redirectUri = redirect_uri;
			state.scope = scope;
			state.userId = userId;
			state.setId();
			//track state in memory for now
			stateMap.put(String.valueOf(state.id), state);
			
			//make oauth call-1
			//example: /authorize?response_type=code&client_id=appid&redirect_uri=urlencoded&scope=search&state=key
			String url = auth_url
				+"?client_id="+reg.client_id
				+"&response_type="+type
				+"&redirect_uri="+ AuthUtils.encodeUrl(redirect_uri)
				+"&scope="+ scope
				+"&state="+state.id;
			bean.nextUrl = url;
		}		
	}
	
	/**process callback, if good, make call-2 */
	public void callback(Sreq sreq, ReqBean bean){
		bean.userId = userId;

		try{
			//ezample callback request: /after-oauth?code=auth-code&state=key
			String code = sreq.req.getParameter("code");
			String error = sreq.req.getParameter("error");
			String state_id = sreq.req.getParameter("state");
			//find state
			State state = stateMap.get(state_id);

			if(error == null){
				if(state != null){
					//make oauth call-2
					OAuthToken token = oauthCall2("authorization_code", code, state.clientId, state.redirectUri);
					
					//TODO: save token with patient and provider					
					Provider p = providers.get(state.provider);
					p.accessToken = token.accessToken;
					p.scopes = token.scopes;
				}else{
					bean.appendErrorMessage("Lost track of state");
				}
			}else{
				bean.appendErrorMessage("Failed authorization at provider "+state.provider+". code="+ error);
			}
		}catch(Exception e){
			LOG.error(e,e);
			bean.appendErrorMessage("Failed to authorize");
		}
	}
	
	/**make oauth call-2 to exchange authcode for token*/
	public OAuthToken oauthCall2(String type, String code, String client_id, String redirect_uri){
		OAuthToken token = new OAuthToken();
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(token_url);
		try{
			//post form parameters
			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			nvps.add(new BasicNameValuePair("grant_type", type));
			nvps.add(new BasicNameValuePair("code", code));
			nvps.add(new BasicNameValuePair("redirect_uri", redirect_uri));
			nvps.add(new BasicNameValuePair("client_id", client_id));
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			
			HttpResponse response = httpclient.execute(httpPost);
			StatusLine sl = response.getStatusLine();
			LOG.debug("token response: "+sl);
	    if(sl.getStatusCode() == 200){
		    HttpEntity rs_entity = response.getEntity();
	      String body = EntityUtils.toString(rs_entity);
	      LOG.debug("token response body:\n"+body);
	    	
	      parseTokenResponse(body, token);
	      
				// and ensure it is fully consumed
		    EntityUtils.consume(rs_entity);    
			}else{
				LOG.warn("failed oauth call-2: "+ sl);
			}
		}catch(Exception e){
			LOG.error(e,e);
		} finally {
      httpclient.getConnectionManager().shutdown();
		}

		return token;
	}

	//parse token response for access token
	private void parseTokenResponse(String body, OAuthToken token) throws Exception {
		ObjectMapper m = new ObjectMapper();
		JsonNode root = m.readTree(body);
		token.accessToken = root.get("access_token").textValue();
		token.expiresIn = root.get("expires_in").longValue();
		String scope = root.get("scope").textValue();
		token.scopes = AuthUtils.scopeStringToSet(scope);
		LOG.debug("token: "+token);
	}
	
	/**fill bean with available data to dispaly */
	public void fillData(Sreq sreq, ReqBean bean){
		bean.userId = userId;
		bean.providers = providers;
	}

	/** pull data from API, fill bean with available data to display */
	public void getData(Sreq sreq, ReqBean bean, String scope){
		bean.userId = userId;
		bean.providers = providers;
		
		//check provider
		String sel_provider = sreq.req.getParameter("provider");
		Provider p = providers.get(sel_provider);
		bean.dataSource = p;
		//check access token
		if(p == null || p.accessToken == null){
			bean.appendErrorMessage("Patient has not authorized to access data at provider: "+sel_provider);
		}else{
			//use saved access token to call data api
			String url = null;
			if("list".equals(scope)){
				//list patient records, in json
				url = list_url;
				bean.data = callRecordApi(p.accessToken, url, null);
				bean.apiEndpoint = url;
				//save records for this provider
				p.records = parseRecordsResponse(bean.data);
			}else if("summary".equals(scope)){
				//get summary of patient visit or lab results, in C-CDA
				url = summary_url;
				Map<String, String> params = new HashMap<String,String>();
				//check patient record id
				String id = sreq.req.getParameter("id");
				if(id != null){
					//set record id for specific patient record to pull
					params.put("id", id);
				}
				bean.data = callRecordApi(p.accessToken, url, params);
				bean.apiEndpoint = url;
				bean.recordId = id;
			}else if("search".equals(scope)){
				//todo: BB+ specs needs to be refined
			}
			if(bean.data == null){
				bean.appendErrorMessage("failed to get data from provider: "+sel_provider);
			}
		}
		
	}
	
	/**call API for patient records, return response body */
	public String callRecordApi(String token, String url, Map<String, String> params){
		String body = null;
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		try{
			httpGet.setHeader("Accept", Utils.ContentType.XML);
			httpGet.setHeader("Authorization", token);
			//additional params
			if(params != null){
				for(String name:params.keySet()){
					httpGet.setHeader(name, params.get(name));
				}				
			}
			
			HttpResponse response = httpclient.execute(httpGet);
			StatusLine sl = response.getStatusLine();
			LOG.debug("record response: "+sl);

	    if(sl.getStatusCode() == HttpStatus.SC_OK){
		    HttpEntity rs_entity = response.getEntity();
	      body = EntityUtils.toString(rs_entity);
	      LOG.debug("response body:\n"+body);

	      // and ensure it is fully consumed
		    EntityUtils.consume(rs_entity);   
			}else{
				LOG.warn("failed to get record: "+ sl);
			}
		}catch(Exception e){
			LOG.error(e,e);
		} finally {
      httpclient.getConnectionManager().shutdown();
		}
		return body;
	}

	//parse response from "list" or  "search" operation
	private List<Record> parseRecordsResponse(String json){
		List<Record> records = new ArrayList<Record>();
		try{
			ObjectMapper m = new ObjectMapper();
			JsonNode root = m.readTree(json);
		  for (JsonNode node : root.path("records")) {
		  	String id = node.get("id").textValue();
		  	String type = node.get("type").textValue();
		  	String name = node.get("name").textValue();
		  	long d = node.get("date").longValue();
		  	Calendar cal = Calendar.getInstance();
		  	cal.setTimeInMillis(d);
		  	Record r = new Record();
		  	r.id = id;
		  	r.type = type;
		  	r.name = name;
		  	r.date = cal.getTime();
		  	records.add(r);
		  }			
		}catch(Exception e){
			LOG.error(e,e);
		}
		return records;
	}


}
