package com.activ.bbpi.auth;

import java.util.HashMap;
import java.util.Map;

/**transient storage of authorization code */
public class AuthCache {

	//tracking authcode: authcode => code
	private Map<String,OAuthCode> authcodeMap = new HashMap<String,OAuthCode>();
	

	public OAuthCode getAuthcode(String code){
		return authcodeMap.get(code);
	}

	public void addAuthcode(OAuthCode authcode){
		authcodeMap.put(authcode.authCode, authcode);
	}
}
