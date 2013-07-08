package com.activ.bbpi.auth.reg;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.activ.bbpi.auth.AuthUtils;

/** OAuth client registration */
public class OAuthReg {

  public static final String REGISTER_URL = "url";  	

  public static final class Request {
    public static final String CLIENT_NAME = "client_name";
    public static final String CLIENT_URI = "client_uri";
    public static final String LOGO_URI = "logo_uri";
    public static final String REDIRECT_URIS = "redirect_uris";
    public static final String RESPONSE_TYPES = "response_types";
    public static final String GRANT_TYPES = "grant_types";
    public static final String TOKEN_METHOD = "token_endpoint_auth_method";
    public static final String SCOPE = "scope";  	
  }

  public static final class Response {
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String ISSUED_AT = "client_id_issued_at";
    public static final String EXPIRES_AT = "client_secret_expires_at";
    public static final String REG_TOKEN = "registration_access_token";
    public static final String REG_URI = "registration_client_uri";
    public static final String CLIENT_NAME = "client_name";
    
    public static final String ERROR = "error";
    public static final String ERROR_DESC = "error_description";
  }
  
  //request params
	public String client_name;    //client app name
	public String client_uri;
	public String logo_uri;
	//public List<String> contacts = new LinkedList<String>();
	public String tos_uri;
	public Set<String> redirect_uris = new HashSet<String>();
	public Set<String> response_types = new HashSet<String>(); //"code"
	public Set<String> grant_types  = new HashSet<String>();   //["authorization_code"],
	public String token_endpoint_auth_method; //"client_secret_basic",
	//public String scope; // "summary search"
	public Set<String> scopes = new HashSet<String>(); // "summary search"

	//response params
	public String client_id;  //internal unique id
	public String client_secret;
	public long issued_at;
	public long expires_at;
	public String reg_uri;
	public String reg_token;

  //internal fields
	//public String _id;  
  public Date _create_time;
  public Date _update_time;

  /**required params in client request */
	public boolean validRequest(){
		return 
			client_name != null && client_name.length() > 0 
			&& client_uri != null && client_uri.length() > 0 
			&& redirect_uris != null && redirect_uris.size() > 0;
	}
	
	public boolean validRedirectUri(String uri){
		return redirect_uris.contains(uri);
	}
	
	/**to a string of scopes separated by space, used in Oauth2 registration request */
	public String toScopeString(){
		return AuthUtils.scopeSetToScopeString(scopes);
	}
	
	
	public boolean validScopes(Set<String> set){
		return scopes.containsAll(set);
	}
	
	public String toString(){
		return client_id+" | "+ client_name+" | "+token_endpoint_auth_method+" | "+grant_types +" | "+scopes;
	}
  	

}
