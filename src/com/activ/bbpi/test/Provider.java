package com.activ.bbpi.test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.activ.cdaj.data.Record;

/**
 * Representing care provider and user authorization for data access.
 * 
 * @author aj
 *
 */
public class Provider {

	public String providerName;
	
	//user login to provider's patient portal
	public String userId;         //EHR user id  
	public String userPasswaord;  //encrypted
	
	//user authorization for access data in provider's EHR
	public String accessToken;
	public Set<String> scopes = new HashSet<String>(); 
	
	//list of patient records
	public List<Record> records;
}
