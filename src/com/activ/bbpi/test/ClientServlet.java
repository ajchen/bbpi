package com.activ.bbpi.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.activ.bbpi.bean.ReqBean;
import com.activ.cdaj.util.Sreq;

/**
 * Servlet for client test application.
 * 
 * @author aj
 *
 */
public class ClientServlet extends HttpServlet {
		private static final Logger LOG = Logger.getLogger(ClientServlet.class);
	  private static final long serialVersionUID = 1L;
	  

	  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {    
	    Sreq sreq = new Sreq(req);
	    LOG.info("request from "+req.getRemoteAddr()+": "+sreq.url);
	    
	    try{
	      if("/client-register".equals(sreq.path)){  
	      	registerPage(req, resp, sreq, null);      		      			
	      }else if("/client-oauth".equals(sreq.path)){  
	      	oauthPage(req, resp, sreq, null);      		      			
	      }else if("/client-oauth-callback".equals(sreq.path)){  
	      	oauthCallback(req, resp, sreq);      		      			
	      }else if("/client-data".equals(sreq.path)){  
	      	dataPage(req, resp, sreq, null);      		      			
	      }else{
	      	oauthPage(req, resp, sreq, null);      		      				      	
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
	      if("/client".equals(sreq.path)){  
	      	if("register".equals(sreq.act)){
	      		register(req, resp, sreq);   
	      	}else if("oauth".equals(sreq.act)){
	      		authorize(req, resp, sreq);   
	      	}else if("list".equals(sreq.act)){
	      		pullData(req, resp, sreq, "list");   
	      	}else if("summary".equals(sreq.act)){
	      		pullData(req, resp, sreq, "summary");   
	      	}else{
		      	oauthPage(req, resp, sreq, null);      		      			
	      	}
	      }
	    }catch(Exception e){
	    	LOG.error("failed "+sreq.url, e);
	    	resp.sendRedirect(req.getContextPath()+"/error.jsp");
	    }

	  }
	  
	  //show client registration page
	  private void registerPage(HttpServletRequest req, HttpServletResponse resp, Sreq sreq, ReqBean bean) throws ServletException, IOException {    
	  	if(bean == null){
	  		bean = new ReqBean();
	  	}
	  	ClientMgr.get().fillClientRegistration(sreq, bean);
			req.setAttribute(ReqBean.NAME, bean);
			req.getRequestDispatcher("/client/register.jsp").forward(req,resp);      
	  }

	  //do client registration dynamically
	  private void register(HttpServletRequest req, HttpServletResponse resp, Sreq sreq) throws ServletException, IOException {    
	  	ReqBean bean = new ReqBean();
	  	ClientMgr.get().registerClient(bean);
	  	//done, show reg page
	  	registerPage(req, resp, sreq, bean);
	  }

	  //show oauth page
	  private void oauthPage(HttpServletRequest req, HttpServletResponse resp, Sreq sreq, ReqBean bean) throws ServletException, IOException {    
	  	if(bean == null){
	  		bean = new ReqBean();
	  	}
	  	ClientMgr.get().fillOauth(sreq, bean);
			req.setAttribute(ReqBean.NAME, bean);
			req.getRequestDispatcher("/client/oauth.jsp").forward(req,resp);      
	  }

	  //start oauth flow
	  private void authorize(HttpServletRequest req, HttpServletResponse resp, Sreq sreq) throws ServletException, IOException {    
	  	ReqBean bean = new ReqBean();
	  	ClientMgr.get().startOAuth(sreq, bean);
	  	if(bean.nextUrl != null){	  		
	  		resp.sendRedirect(bean.nextUrl);
	  	}else{
		  	oauthPage(req, resp, sreq, bean);
	  	}
	  }

	  //process callback from oauth API server
	  private void oauthCallback(HttpServletRequest req, HttpServletResponse resp, Sreq sreq) throws ServletException, IOException {    
	  	ReqBean bean = new ReqBean();
	  	//process callback, make call2, process response from /token
	  	ClientMgr.get().callback(sreq, bean);
	  	//done, show oauth page
	  	oauthPage(req, resp, sreq, bean);
	  }

	  //show patient data page
	  private void dataPage(HttpServletRequest req, HttpServletResponse resp, Sreq sreq, ReqBean bean) throws ServletException, IOException {    
	  	if(bean == null){
	  		bean = new ReqBean();
	  	}
	  	ClientMgr.get().fillData(sreq, bean);
			req.setAttribute(ReqBean.NAME, bean);
			req.getRequestDispatcher("/client/data.jsp").forward(req,resp);      
	  }

	  //pull data from API server
	  private void pullData(HttpServletRequest req, HttpServletResponse resp, Sreq sreq, String scope) throws ServletException, IOException {    
	  	ReqBean bean = new ReqBean();
	  	ClientMgr.get().getData(sreq, bean, scope);
			req.setAttribute(ReqBean.NAME, bean);
			req.getRequestDispatcher("/client/data.jsp").forward(req,resp);      
	  }
}
