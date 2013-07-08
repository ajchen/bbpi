package com.activ.bbpi.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.activ.bbpi.api.ApiResponse;
import com.activ.bbpi.auth.reg.RegMgr;
import com.activ.bbpi.bean.ReqBean;
import com.activ.cdaj.util.Sreq;

/**
 * Servlet for user authorization, supporting oauth2.
 * 
 * @author aj
 *
 */
public class AuthServlet extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(AuthServlet.class);
  private static final long serialVersionUID = 1L;
  

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {    
    Sreq sreq = new Sreq(req);
    LOG.info("request from "+req.getRemoteAddr()+": "+sreq.url);
    
    try{
    	if("/authorize".equals(sreq.path)){  
      	authorize(req, resp, sreq);      		      			
    	}else if("/consent".equals(sreq.path)){  
    		userConsentForm(req, resp, sreq);      		      			
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
      if("/register".equals(sreq.path)){  
      	registerDynamic(req, resp, sreq);      		      			
    	}else if("/consent".equals(sreq.path)){  
    		if("accept".equals(sreq.act)){
    			userConsent(req, resp, sreq); 
    		}else if("deny".equals(sreq.act)){
    			userDeny(req, resp, sreq); 
    		}else{
      		userConsentForm(req, resp, sreq);      		      			    			
    		}
      }else if("/token".equals(sreq.path)){  
        token(req, resp, sreq);      		
      }
    }catch(Exception e){
    	LOG.error("failed "+sreq.url, e);
    	resp.sendRedirect(req.getContextPath()+"/error.jsp");
    }

  }
  
  //register client dynamically
  private void registerDynamic(HttpServletRequest req, HttpServletResponse resp, Sreq sreq) throws ServletException, IOException {    
  	RegMgr mgr = new RegMgr();
  	String rs = mgr.registerDynamic(sreq);
  	ApiResponse.respond(resp, rs, "json");
  }

  //TODO: register client manually on registration web page

  //process user authorization
  private void authorize(HttpServletRequest req, HttpServletResponse resp, Sreq sreq) throws ServletException, IOException {    
  	ReqBean bean = new ReqBean();
  	AuthMgr mgr = new AuthMgr();
  	mgr.authorize(sreq, bean);
  	if(bean.nextUrl != null){
  		//respond to client
  		resp.sendRedirect(bean.nextUrl);      			
  	}else{
  		//forward to user consent web page
			req.setAttribute(ReqBean.NAME, bean);
			req.getRequestDispatcher("/auth/user-consent.jsp").forward(req,resp);      	  			  		
		}
  }

  //show user consent form
  private void userConsentForm(HttpServletRequest req, HttpServletResponse resp, Sreq sreq) throws ServletException, IOException {    
  	ReqBean bean = new ReqBean();
  	AuthMgr mgr = new AuthMgr();
  	mgr.fillUserConsentForm(sreq, bean);
		req.setAttribute(ReqBean.NAME, bean);
		req.getRequestDispatcher("/auth/user-consent.jsp").forward(req,resp);      	  			  		
  }

  //process user consent
  private void userConsent(HttpServletRequest req, HttpServletResponse resp, Sreq sreq) throws ServletException, IOException {    
  	ReqBean bean = new ReqBean();
  	AuthMgr mgr = new AuthMgr();
  	mgr.userConsent(sreq, bean);
  	if(bean.nextUrl != null){
  		//callback client
  		resp.sendRedirect(bean.nextUrl);      			
  	}else{
  		//error, show consent form again
			req.setAttribute(ReqBean.NAME, bean);
			req.getRequestDispatcher("/auth/user-consent.jsp").forward(req,resp);      	  			  		
		}
  }

  //process user denial
  private void userDeny(HttpServletRequest req, HttpServletResponse resp, Sreq sreq) throws ServletException, IOException {    
  	ReqBean bean = new ReqBean();
  	AuthMgr mgr = new AuthMgr();
  	mgr.userDeny(sreq, bean);
  	//respond error
  	resp.sendRedirect(bean.nextUrl);      			
  }

  //process token request
  private void token(HttpServletRequest req, HttpServletResponse resp, Sreq sreq) throws ServletException, IOException {    
  	ReqBean bean = new ReqBean();
  	AuthMgr mgr = new AuthMgr();
  	String token = mgr.token(sreq, bean);
  	//respond to client
  	ApiResponse.respond(resp, token, "json");
  }


}
