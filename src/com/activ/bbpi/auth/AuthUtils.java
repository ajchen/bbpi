package com.activ.bbpi.auth;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.activ.bbpi.auth.reg.OAuthReg;
import com.activ.cdaj.db.OAuthClient;
import com.activ.cdaj.db.OAuthClientDao;
import com.activ.cdaj.db.OAuthUser;
import com.activ.cdaj.db.OAuthUserDao;

/**
 * Utilities for oauth2 implementation.
 * 
 * @author aj
 *
 */
public class AuthUtils {
	private static final Logger LOG = Logger.getLogger(AuthUtils.class);
		
	public static String ENCODING = "UTF-8";
	
	/**to a string of scopes separated by space, used in Oauth2 registration request */
	public static String scopeSetToScopeString(Set<String> scopes){
		StringBuffer sb = new StringBuffer();
		for(String s:scopes){
			if(sb.length() == 0){
				sb.append(s);
			}else{
				sb.append(" "+s);
			}
		}
		return sb.toString();
	}
	
	/**convert scope string (separated by space) from request to a set */
	public static Set<String> scopeStringToSet(String scope_str){
		Set<String> set = new HashSet<String>();
		if(scope_str != null){
			String[] toks = scope_str.split("\\s+");
			for(String t:toks){
				set.add(t);
			}
		}
		return set;
	}

	/**generate random UUID */
  public static String generateUUID() {
    return generateUUID(UUID.randomUUID().toString());
	} 
	
	public static String generateUUID(String param) {
	    return UUID.fromString(UUID.nameUUIDFromBytes(param.getBytes()).toString()).toString();
	}

  public static String generateMD5() {
      return generateMD5(UUID.randomUUID().toString());
  }

  public static String generateMD5(String param) {
      try {
          MessageDigest algorithm = MessageDigest.getInstance("MD5");
          algorithm.reset();
          algorithm.update(param.getBytes());
          byte[] messageDigest = algorithm.digest();
          StringBuffer hexString = new StringBuffer();
          for (int i = 0; i < messageDigest.length; i++) {
              hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
          }
          return hexString.toString();
      } catch (Exception e) {
      	LOG.error("OAuth Token cannot be generated. "+e.getMessage());
      }
      return null;
  }
	
  public static String encodeUrl(String url){
  	try{
  		return URLEncoder.encode(url, ENCODING);
  	}catch(Exception e){
  		LOG.error(e);
  	}
  	return url;
  }
  
  public static String decodeUrl(String url){
  	try{
  		return URLDecoder.decode(url, ENCODING);
  	}catch(Exception e){
  		LOG.error(e);
  	}
  	return url;
  }
  
  //////database access ///////

  /**
   * get oauth client registration by client id. 
   */
	public static OAuthReg getOAuthRegById(String client_id){
		OAuthClient client = new OAuthClientDao().getClientRegById(client_id);
		if(client != null){
			return toOAuthReg(client);
		}
		return null;
	}

  /**
   * get oauth client registration by client name and uri.
   */
	public static OAuthReg getOAuthRegByName(String client_name, String client_uri){
		OAuthClient client = new OAuthClientDao().getClientRegByName(client_name, client_uri);
		if(client != null){
			return toOAuthReg(client);
		}
		return null;
	}
	
	//convert client to reg
	private static OAuthReg toOAuthReg(OAuthClient client){
		OAuthReg reg = new OAuthReg();
		reg.client_id = client.client_id;
		reg.client_name = client.client_name;
		reg.client_secret = client.client_secret;
		reg.client_uri = client.client_uri;
		reg.expires_at = client.expires_at;
		reg.grant_types = client.grant_types;
		reg.issued_at = client.issued_at;
		reg.redirect_uris = client.redirect_uris;
		reg.response_types = client.response_types;
		reg.scopes = client.scopes;
		reg.token_endpoint_auth_method = client.token_endpoint_auth_method;
		reg._create_time = client._create_time;
		reg._update_time = client._update_time;
		return reg;		
	}
	
	/**
	 * create OAuthReg for client in database.
	 * @param reg
	 * @return true if created
	 */
	public static boolean createOAuthReg(OAuthReg reg){
		OAuthClient client = toClient(reg);
		boolean ok = new OAuthClientDao().createClient(client);
		if(ok){
			//client id is created from database as unique id
			reg.client_id = client.client_id;
		}
		return ok;
	}

	/**
	 * update OAuthReg in database.
	 * @param reg
	 * @return true if updated.
	 */
	public static boolean updateOAuthReg(OAuthReg reg){
		OAuthClient client = toClient(reg);
		client.client_id = reg.client_id;
		return new OAuthClientDao().updateClient(client);
	}

	//convert OAuthReg to OAuthClient
	private static OAuthClient toClient(OAuthReg reg){
		OAuthClient client = new OAuthClient();
		client.client_name = reg.client_name;
		client.client_uri = reg.client_uri;
		client.redirect_uris = reg.redirect_uris;
		client.response_types = reg.response_types;
		client.grant_types = reg.grant_types;
		client.token_endpoint_auth_method = reg.token_endpoint_auth_method;
		client.scopes = reg.scopes;
		client.client_secret = reg.client_secret;
		client.issued_at = reg.issued_at;
		client.expires_at = reg.expires_at;
		return client;
	}
	

  /**
   * create oauth token in database. return true if succeeded.
   */
	public static boolean createOAuthToken(OAuthToken token){
		OAuthUser user = new OAuthUser();
		user._create_time = token._create_time;
		user._update_time = token._update_time;
		user.clientId = token.clientId;
		user.expiresIn = token.expiresIn;
		user.redirectUri = token.redirectUri;
		user.refreshToken = token.refreshToken;
		user.scopes = token.scopes;
		user.userId = token.userId;
		boolean ok = new OAuthUserDao().createUserOAuth(user);
		if(ok){
			//access token is created from database as unique id
			token.accessToken = user.accessToken;
		}
		return ok;
	}

	/**
	 * get oauth token from database.
	 * @param _id  access token
	 * @return  OAuthToken
	 */
	public static OAuthToken getOAuthTokenById(String _id){
		OAuthUser user = new OAuthUserDao().getUserOAuthById(_id);
		if(user != null){
			OAuthToken token = new OAuthToken();
			token._create_time = user._create_time;
			token._update_time = user._update_time;
			token.accessToken = user.accessToken;
			token.clientId = user.clientId;
			token.expiresIn = user.expiresIn;
			token.redirectUri = user.redirectUri;
			token.refreshToken = user.refreshToken;
			token.scopes = user.scopes;
			token.userId = user.userId;
			return token;
		}
		return null;
	}

}
