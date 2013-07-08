package com.activ.bbpi.auth.reg;

import java.util.Calendar;

import org.apache.log4j.Logger;

import com.activ.bbpi.auth.AuthUtils;
import com.activ.cdaj.util.Config;
import com.activ.cdaj.util.Sreq;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/*
 * client registration manager.
 */
public class RegMgr {

	public static final Logger LOG = Logger.getLogger(RegMgr.class);
	
	/**
	 *  process dynamic client registration request.
	 *  implementing BB+ open registration specs.
	 */
	public String registerDynamic(Sreq sreq){
		try{
			String type = sreq.req.getContentType();
			//expecting request in json
			if(Sreq.CONTENT_TYPE_JSON.equalsIgnoreCase(type)){
				//parse json request
				OAuthReg regi = parseRequestJson(sreq);
				if(regi.validRequest()){
					//check if client has already registered
					OAuthReg old_reg = AuthUtils.getOAuthRegByName(regi.client_name, regi.client_uri);
					if(old_reg != null){
						if(!Config.get().getBooleanValue("client.reg.dynamic.update", false)){
							//error if client exists and dynamic update is not allowed
							return createErrorResponse("Invalid request", "Client application was already registered.");
						}
					}
					
					//create ids
					regi.client_secret = AuthUtils.generateUUID();
					Calendar c = Calendar.getInstance();
					regi.issued_at = c.getTime().getTime();
					//add 1 day for now
					//todo: make it configurable
					c.add(Calendar.DAY_OF_MONTH, 1);  
					regi.expires_at = c.getTime().getTime();
					//regi.reg_token = AuthUtils.generateUUID();

					boolean saved = false;
					if(old_reg == null){
						//create new client 
						saved = AuthUtils.createOAuthReg(regi);
					}else{
						//update existing client
						regi.client_id = old_reg.client_id;
						saved = AuthUtils.updateOAuthReg(regi);
					}

					//respond to client
					if(saved){
						return createResponse(regi);
					}
				}else{
					return createErrorResponse("Invalid request", null);
				}
			}else{
				return createErrorResponse("Invalid request", "Invalid request content_type: "+type);
			}
		}catch(Exception e){
			LOG.error(e,e);
		}
		return createErrorResponse("Error", "Registration failed");
	}

	/*
	 * parse request in json to OAuthReg. example request:
	 "client_name": "Blood Pressure Grapher",
	 "client_uri": "https://bpgrapher.org",
	 "logo_uri": "http://bpgrapher.org/images/logo.png",
	 "contacts": ["plot-master@bpgrapher.org" ],
	 "tos_uri": "https://bpgrapher.org/tos",
	 "redirect_uris": [ "https://bpgrapher.org/after-auth"],
	 "response_types": ["code"],
	 "grant_types": ["authorization_code"],
	 "token_endpoint_auth_method": "client_secret_basic",
	 "scope":  "summary"
  */
	private OAuthReg parseRequestJson(Sreq sreq){
		OAuthReg client = new OAuthReg();
		try{
			String content = sreq.getStreamAsString();
			ObjectMapper m = new ObjectMapper();
			JsonNode json = m.readTree(content);
			client.client_name= json.get(OAuthReg.Request.CLIENT_NAME).textValue();
			client.client_uri = json.get(OAuthReg.Request.CLIENT_URI).textValue();
			client.token_endpoint_auth_method = json.get(OAuthReg.Request.TOKEN_METHOD).textValue();
			
		  for (JsonNode node : json.path(OAuthReg.Request.REDIRECT_URIS)) {
		  	client.redirect_uris.add(node.textValue());
		  }

		  for (JsonNode node : json.path(OAuthReg.Request.GRANT_TYPES)) {
		  	client.grant_types.add(node.textValue());
		  }

		  for (JsonNode node : json.path(OAuthReg.Request.RESPONSE_TYPES)) {
		  	client.response_types.add(node.textValue());
		  }

			String scope = json.get(OAuthReg.Request.SCOPE).textValue();
			client.scopes = AuthUtils.scopeStringToSet(scope);
			
		}catch(Exception e){
			LOG.error(e,e);
		}
		LOG.debug("client reg: "+client);
		return client;
	}
	
	/*
	 * create response in json. example: 
    "registration_access_token": "reg-23410913-abewfq.123483",
    "registration_client_uri": "https://server.example.com/register/s6BhdRkqt3",
    "client_id":"s6BhdRkqt3",
    "client_secret": "cf136dc3c1fc93f31185e5885805d",
    "client_id_issued_at":2893256800
    "client_secret_expires_at":2893276800
    "client_name":"My Example Client",
    "client_name#ja-Jpan-JP": "\u30AF\u30E9\u30A4\u30A2\u30F3\u30C8\u540D",
    "redirect_uris":["https://client.example.org/callback", "https://client.example.org/callback2"]
    "scope": "read write dolphin",
    "grant_types": ["authorization_code", "refresh_token"]
    "token_endpoint_auth_method": "client_secret_basic",
    "logo_uri": "https://client.example.org/logo.png",
    "jwks_uri": "https://client.example.org/my_public_keys.jwks"
	 */
	private String createResponse(OAuthReg regi) throws Exception {
		ObjectMapper obj_mapper = new ObjectMapper();            
		ObjectNode root = obj_mapper.createObjectNode();
		root.put(OAuthReg.Response.CLIENT_NAME, regi.client_name);
		root.put(OAuthReg.Response.CLIENT_ID, regi.client_id);
		root.put(OAuthReg.Response.CLIENT_SECRET, regi.client_secret);
		root.put(OAuthReg.Response.REG_TOKEN, regi.reg_token);
		return obj_mapper.writeValueAsString(root);			
	}

	/**create error response in json */
	private String createErrorResponse(String error, String desc) {
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
		return "";
	}

}
