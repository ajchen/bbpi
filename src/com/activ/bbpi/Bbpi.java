package com.activ.bbpi;

import org.apache.log4j.Logger;

import com.activ.bbpi.auth.AuthCache;
import com.activ.cdaj.EhrDataService;

/**
 * BBPI server. 
 */
public class Bbpi {
	private static final Logger LOG = Logger.getLogger(Bbpi.class);
	
	public static String STATUS_UP = "up";
	public static String STATUS_DOWN = "down";
	
	//server status
	private String status = STATUS_DOWN;

	//transient storage of authcode	
	private AuthCache authCache;

	private static Bbpi _server;
	
	private Bbpi() throws Exception {
    LOG.info("starting server ...");
		try{
			//start ehr data service
			EhrDataService.init();

			//start oauth cache
			authCache = new AuthCache();
			
			status = STATUS_UP;
    }catch(Exception e){
    	LOG.error(e, e);
    	throw new RuntimeException("init fails!!! ");
    }
	}
	
	public static void init() throws Exception {
		if(_server == null){
			_server = new Bbpi();
		}
	}

	/**get singleton server */
	public static Bbpi get(){
		return _server;
	}
	
	public String status(){return status;}
	
	public AuthCache authCache(){ return authCache;}
	
}
