package com.activ.bbpi.test;

import java.io.File;

import org.apache.log4j.Logger;

import com.activ.bbpi.bean.ReqBean;
import com.activ.cdaj.util.Config;

/**
 * Tester for client.
 * 
 * @author aj
 *
 */
public class ClientTest {
	private static final Logger LOG = Logger.getLogger(ClientTest.class);
	
	/**dynamically register client on api server, using httpclient4 */
	public void register(){
		ClientMgr.get().registerClient(new ReqBean());
	}

	
	public static void main(String[] args){
    String usage = "usage: ClientTest ";
    try{
    	//String url = null;
      for(int i=0; i<args.length; i++){
      	if(args[i].equals("-url")){
      		//url = args[++i];
      	}
      }
      
      String conf = System.getProperty("bbpi.conf");
      Config.init(new File(conf));
      Config.get().loadModConfig("client");
      ClientTest client = new ClientTest();
      client.register();
      System.exit(0);
    }catch(Exception e){
    	LOG.error(e,e);
      System.err.println(usage);
      System.exit(1);
    }
  }

}
