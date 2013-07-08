package com.activ.bbpi.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.activ.cdaj.util.Sreq;
import com.activ.cdaj.util.Utils;

/**
 * Servlet for data API.
 * 
 * @author aj
 *
 */
public class ApiServlet extends HttpServlet {
		private static final Logger LOG = Logger.getLogger(ApiServlet.class);
	  private static final long serialVersionUID = 1L;
	  
	  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {    
	    Sreq sreq = new Sreq(req);
	    //Utils.printServletInfo(sreq.req);
	    LOG.info("request from "+req.getRemoteAddr()+": "+sreq.url);
	    
	    try{
	      if("/record/summary".equals(sreq.path)){  
	      	record(req, resp, sreq, Utils.DataScope.SUMMARY); 
	      }else if(sreq.path.startsWith("/record/search")){  
	      	record(req, resp, sreq, Utils.DataScope.SEARCH);      		      			
	      }else if("/record".equals(sreq.path)){  
	      	record(req, resp, sreq, Utils.DataScope.LIST);      		      			
	      }else{ //default
	      	record(req, resp, sreq, Utils.DataScope.LIST);      		      				      	
	      }
	    }catch(Exception e){
	    	LOG.error("failed "+sreq.url, e);
	    	resp.sendRedirect(req.getContextPath()+"/error.jsp");
	    }
	  }
	  
	  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {    
	    Sreq sreq = new Sreq(req);
	    LOG.info("request from "+req.getRemoteAddr()+": "+sreq.url);

	    try{
	      //if("/".equals(sreq.path)){  
	      //}
	    }catch(Exception e){
	    	LOG.error("failed "+sreq.url, e);
	    	resp.sendRedirect(req.getContextPath()+"/error.jsp");
	    }
	  }
	  
	  //process request to access patient records
	  //scope: summary, list, search
	  private void record(HttpServletRequest req, HttpServletResponse resp, Sreq sreq, String scope) throws ServletException, IOException {    
	  	ApiMgr mgr = new ApiMgr();	  	
	  	ApiResponse api = mgr.getRecord(resp, sreq, scope);
	  	api.respond();
	  }
	  

}
