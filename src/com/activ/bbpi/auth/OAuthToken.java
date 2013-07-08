package com.activ.bbpi.auth;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * OAuth2 access token object.
 * 
 * @author aj
 *
 */
public class OAuthToken {

  public String accessToken;  //used as internal id
  public Long expiresIn;
  public String refreshToken;

  //access scope
  //public String scope;  //scope string, eg: "summary search"
	public Set<String> scopes = new HashSet<String>(); 

  //for user and client
	public String userId;
	public String clientId;
	public String redirectUri;

  //internal fields
  public Date _create_time;
  public Date _update_time;
  
  public String toString(){
  	return accessToken+" | "+expiresIn+" | "+scopes;
  }
}
