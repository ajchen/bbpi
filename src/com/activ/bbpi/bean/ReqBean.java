package com.activ.bbpi.bean;

import java.util.HashMap;
import java.util.Map;

import com.activ.bbpi.test.Provider;
import com.activ.cdaj.user.User;

/** Bean for responding to the user request */
public class ReqBean extends Bean {

	public static String NAME = "req-bean";  //bean name
	
	//url to go next
	public String nextUrl;

	//for client registration
	public String registerUrl;
	public boolean registered;
	
	//general parameter map: name => value
	public Map<String, String> params = new HashMap<String, String>();

	//user in client app
	public String userId = "";

	//providers and users (=patients)
	public Map<String,Provider> providers;
	
	//for authorization request 
	public String redirectUri;
	public String clientId;
	public String scope;
	public String state;
	public String clientName;
	
	//for user consent 
	public User user;
	
	//for patient data
	public String data;
	public Provider dataSource;
	public String apiEndpoint;
	public String recordId; 
	
	public ReqBean(){
	}

	/**get parameter */
	public String param(String name){
		return params.containsKey(name)? params.get(name):"";
	}
	
	public String userId(){
		return user == null? "":user.userId;
	}

	public String data(){
		return data == null? "":data;
	}
}
