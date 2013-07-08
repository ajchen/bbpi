package com.activ.bbpi.auth;

import java.util.Set;

import org.apache.log4j.Logger;

import com.activ.bbpi.Bbpi;
import com.activ.bbpi.auth.reg.OAuthReg;
import com.activ.bbpi.bean.ReqBean;
import com.activ.cdaj.EhrDataService;
import com.activ.cdaj.user.User;
import com.activ.cdaj.util.Config;
import com.activ.cdaj.util.Sreq;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Managing user authorization, implementing oauth2.
 * 
 * @author aj
 *
 */
public class AuthMgr {
	public static final Logger LOG = Logger.getLogger(AuthMgr.class);
	
	/**authorize user, requiring user consent */
	public void authorize(Sreq sreq, ReqBean bean){	
		String error_code = null;

		//check required params from oauth call-1
		//example: /authorize?response_type=code&client_id=appid&redirect_uri=urlencoded&scope=search&state=key
		String type = sreq.req.getParameter(OAuth.OAUTH_RESPONSE_TYPE);
		String client_id = sreq.req.getParameter(OAuth.OAUTH_CLIENT_ID);
		String uri = sreq.req.getParameter(OAuth.OAUTH_REDIRECT_URI);
		String scope = sreq.req.getParameter(OAuth.OAUTH_SCOPE);
		String state = sreq.req.getParameter(OAuth.OAUTH_STATE); 

		//check response type
		if(!OAuth.ResponseType.CODE.equals(type)){
			error_code = OAuthError.CodeResponse.UNSUPPORTED_RESPONSE_TYPE;
			bean.appendErrorMessage("Invalid response type: "+type);
			
		//check required params
		}else if(client_id != null && uri != null && scope != null && state != null){
			
			// check client registration
			OAuthReg reg = AuthUtils.getOAuthRegById(client_id);
			if(reg != null){
				Set<String> scopes = AuthUtils.scopeStringToSet(scope);
				
				//check redirect uri
				if(!reg.validRedirectUri(uri)){
					error_code = OAuthError.CodeResponse.INVALID_REQUEST;
					bean.appendErrorMessage("Invalid redirect_uri: "+uri);					

				//check scope
				}else if(!reg.validScopes(scopes)){
					error_code = OAuthError.CodeResponse.INVALID_SCOPE;
					bean.appendErrorMessage("Invalid scope: "+scope);					
				}else{
					
					//good, forward to user consent form, pass along request params
					bean.clientId = client_id;
					bean.redirectUri = uri;
					bean.scope = scope;
					bean.state = state;					
					//show client name
					bean.clientName = reg.client_name;
				}
			}else{
				error_code = OAuthError.CodeResponse.UNAUTHORIZED_CLIENT;
				bean.appendErrorMessage("Invalid client id: "+client_id);
			}
		}else{
			error_code = OAuthError.CodeResponse.INVALID_REQUEST;
			bean.appendErrorMessage("Invalid request. ");
		}
		
		//error, callback client with error response
		if(bean.error){
			bean.nextUrl = uri+"?error="+error_code+"&state="+state;			
		}
	}
	
	/**fill bean with client request info for user consent */ 
	public void fillUserConsentForm(Sreq sreq, ReqBean bean){	
		//get parameters from oauth call-1
		String client_id = sreq.req.getParameter(OAuth.OAUTH_CLIENT_ID);
		String uri = sreq.req.getParameter(OAuth.OAUTH_REDIRECT_URI);
		String scope = sreq.req.getParameter(OAuth.OAUTH_SCOPE);
		bean.clientId = client_id;
		bean.redirectUri = uri;
		bean.scope = scope;
		
		// check client registration
		OAuthReg reg = AuthUtils.getOAuthRegById(client_id);
		if(reg != null){
			bean.clientName = reg.client_name;
		}

	}
	
	/**
	 * process user consent: 
	 * if user is authenticated, create auth code and callback client with code.
	 * if user not authenticated, callback client with error.
	 */
	public void userConsent(Sreq sreq, ReqBean bean){	
		//get parameters from oauth call-1, based on BB+ specs
		String client_id = sreq.req.getParameter(OAuth.OAUTH_CLIENT_ID);
		String uri = sreq.req.getParameter(OAuth.OAUTH_REDIRECT_URI);
		String scope = sreq.req.getParameter(OAuth.OAUTH_SCOPE);
		String state = sreq.req.getParameter(OAuth.OAUTH_STATE); 
		bean.clientId = client_id;
		bean.redirectUri = uri;
		bean.scope = scope;
		bean.state = state;
		
		//check user, i.e. authenticate patient through EHR   
		//expecting patient login used on EHR's patient portal
		String userid = sreq.req.getParameter("userid").trim();   
		String pwd = sreq.req.getParameter("password").trim();
		User user = EhrDataService.authenticateUser(userid, pwd);
		bean.user = user;
		if(user == null) { 
			LOG.info("failed to authenticate user: "+userid);
			bean.appendErrorMessage("Invalid user id or password. Please try again.");
		}else{
			// with patient consent, create auth code			
			String code = AuthUtils.generateUUID();
			OAuthCode ac = new OAuthCode();
			ac.authCode = code;
			ac.clientId = client_id;
			ac.redirectUri = uri;
			ac.scope = scope;
			ac.state = state;
			ac.userId = user.userId;
			//track authcode in memory
			Bbpi.get().authCache().addAuthcode(ac);
			
			//good, callback client with code
			bean.nextUrl = uri+"?code="+code+"&state="+state;
		}
	}

	/**process user consent: respond error because user denies access */
	public void userDeny(Sreq sreq, ReqBean bean){	
		String uri = sreq.req.getParameter(OAuth.OAUTH_REDIRECT_URI);
		String state = sreq.req.getParameter(OAuth.OAUTH_STATE); 
		//callback client with error
		String error_code = OAuthError.CodeResponse.ACCESS_DENIED;
		bean.nextUrl = uri+"?error="+error_code+"&state="+state;			
	}

	/**process access token request */
	public String token(Sreq sreq, ReqBean bean){
		String json = "";
		String error_code = null;
		try{
			//expecting params based on BB+ specs
			String type = sreq.req.getParameter("grant_type");
			String code = sreq.req.getParameter("code");
			String client = sreq.req.getParameter("client_id");
			String uri = sreq.req.getParameter("redirect_uri");

			//recall authcode saved in oauth call-1
			OAuthCode ac = Bbpi.get().authCache().getAuthcode(code);
			if(ac == null){				
				//check auth code
				error_code = OAuthError.TokenResponse.INVALID_GRANT;
				bean.appendErrorMessage("invalid auth code: "+code);
			}else if(!OAuth.GrantType.AUTH_CODE.equals(type)){
				//check auth type
				error_code = OAuthError.TokenResponse.INVALID_GRANT;
				bean.appendErrorMessage("invalid grant type: "+type);				
			}else if( (client == null || !client.equals(ac.clientId)) 
				|| (uri == null || !uri.equals(ac.redirectUri)) ){
				//check client against auth code created in step 1
				error_code = OAuthError.TokenResponse.INVALID_CLIENT;		
				bean.appendErrorMessage("invalid client: "+client+" "+uri);
			}else{
				//ok, create and track token 
				OAuthToken token = new OAuthToken();
				token.expiresIn = Config.get().getLongValue("auth.token.expires_in", 3600);
				token.scopes = AuthUtils.scopeStringToSet(ac.scope);
				token.clientId = ac.clientId;
				token.redirectUri = ac.redirectUri;
				token.userId = ac.userId;				
				boolean created = AuthUtils.createOAuthToken(token);								
				
				if(created){
					//create json response
					ObjectMapper obj_mapper = new ObjectMapper();            
					ObjectNode root = obj_mapper.createObjectNode();
					root.put("access_token", token.accessToken);
					root.put("token_type", OAuth.TokenType.BEARER);
					root.put("expires_in", token.expiresIn);
					root.put("scope", ac.scope);
					json = obj_mapper.writeValueAsString(root);					
				}else{
					error_code = OAuthError.CodeResponse.SERVER_ERROR;
					bean.appendErrorMessage("failed to create token ");									
				}
			}
		}catch(Exception e){
			LOG.error(e,e);
			error_code = OAuthError.CodeResponse.SERVER_ERROR;
			bean.appendErrorMessage("failed to create token ");									
		}
		
		if(bean.error){
			createErrorResponseJson(error_code, bean.getMessage());
		}
		return json;
	}
	
	/**create error response content in json */
	public static String createErrorResponseJson(String error, String desc) {
		try{
			ObjectMapper obj_mapper = new ObjectMapper();            
			ObjectNode root = obj_mapper.createObjectNode();
			root.put(OAuthReg.Response.ERROR, error);
			if(desc != null){
				root.put(OAuthReg.Response.ERROR_DESC, desc);
			}
			return obj_mapper.writeValueAsString(root);
		}catch(Exception e){
			LOG.error(e,e);
		}
		return "error";
	}

}
