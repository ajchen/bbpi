package com.activ.bbpi;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.activ.bbpi.test.ClientMgr;
import com.activ.cdaj.util.Config;

/** context listener, automatically called when contect starts and stops. */
public class CListener implements ServletContextListener {

  private static final Logger LOG = Logger.getLogger(CListener.class);
  
  /**
   * This method is called when the servlet context is initialized.
   */ 
  public void contextInitialized(ServletContextEvent event) {
    try{              
      //init configuration
    	//expecting bbpi config filepath provided as jvm argument
      String conf = System.getProperty("bbpi.conf");
      File conf_file = new File(conf);
      Config.init(conf_file);
      
      //start server
      Bbpi.init();
      
      //if client test is turned on, init client module
      if(Config.get().getBooleanValue("test.client.enabled", false)){
      	Config.get().loadModConfig("client");
      	ClientMgr.init();
      }

    }catch(Exception e){
      LOG.error("failed to initialize context!!! "+e,e);
      System.exit(1);
    }
  }
	
  /**
   * This method is invoked when the Servlet Context shuts down.
   */             
  public void contextDestroyed(ServletContextEvent event) {
    LOG.info("destroying context...");   
    //...
    LOG.info("DONE");
  }


}
