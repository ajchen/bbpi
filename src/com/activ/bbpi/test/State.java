package com.activ.bbpi.test;

/**
 * For tracking state during oauth calls.
 * 
 * @author aj
 *
 */
public class State {

	public int id;             //unique is
	public String userId;
	public String provider;
	public String clientId;
	public String redirectUri;
	public String scope;
	
	/**generate id for this oauth state */
	public void setId(){
		StringBuffer sb = new StringBuffer();
		sb.append(userId).append(provider).append(clientId).append(redirectUri).append(scope);
		id = sb.toString().hashCode();
	}

}
