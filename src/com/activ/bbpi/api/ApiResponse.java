package com.activ.bbpi.api;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.activ.cdaj.util.Utils;

/**
 * API response.
 * 
 * @author aj
 *
 */
public class ApiResponse {
	public static final Logger LOG = Logger.getLogger(ApiResponse.class);

	public static String FORMAT_XML = "xml";   //default
	public static String FORMAT_JSON = "json";
	
	private HttpServletResponse resp;
	private String content;
	private StringBuffer error = new StringBuffer();
	
	/** create API response */
	public ApiResponse(HttpServletResponse resp){
		this.resp = resp;
	}
	
	/**set content type for response */
	public void setContentType(String type){
		resp.setContentType(type);
	}
	
	/**set response content */
	public void setContent(String content){
		this.content = content;
	}
	
	/**append error */
	public void appendError(String e){
		error.append(e);
	}

	/**send response */
	public void respond(){
		respond(content);
	}
	
	/**send response with the given content */
	public void respond(String content){
		try{
			PrintWriter out = resp.getWriter();
			out.println(content);
			out.flush();
			out.close();
		}catch(Exception e){
			LOG.error("failed to send api response; "+e.getMessage());
		}
	}
	
  /**send response with the given content, in xml or json format */
	public static void respond(HttpServletResponse resp, String content, String format){
		try{
			if(Utils.ContentType.JSON.equals(format) || FORMAT_JSON.equals(format)){
    		resp.setContentType("application/json");				
			}else{
    		resp.setContentType("application/xml");
			}
			PrintWriter out = resp.getWriter();
			out.println(content);
			out.flush();
			out.close();
		}catch(Exception e){
			LOG.error("failed to send api response; "+e.getMessage());
		}
	}


}
