package com.activ.bbpi.api;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.activ.bbpi.auth.AuthUtils;
import com.activ.bbpi.auth.OAuthToken;
import com.activ.cdaj.EhrDataService;
import com.activ.cdaj.util.Sreq;

/**
 * Managing data API requests.
 * 
 * @author aj
 *
 */
public class ApiMgr {

	private static final Logger LOG = Logger.getLogger(ApiMgr.class);

	/**
	 * Get patient data from EHR.  
	 * TODO: update BB+ specs to include definition of error response, error codes.  
	 * For now, respond error in plain text
	 * @param resp
	 * @param sreq
	 * @param scope  summary, search, list, etc
	 * @return Response for API containing content (xml or json) or error message (plain text).
	 */
	public ApiResponse getRecord(HttpServletResponse resp, Sreq sreq, String scope){
		ApiResponse api = new ApiResponse(resp);
		try{
			//expecting parameters defined BB+ specs
			String type = sreq.req.getHeader("Accept"); //response format 
			String token = sreq.req.getHeader("Authorization"); //access token
			//additional params to support more use cases
			String id = sreq.req.getHeader("id");  //patient record id
			
			api.setContentType(type);
			
			OAuthToken oauth = AuthUtils.getOAuthTokenById(token);
			//check token
			if(oauth == null){
				api.appendError("invalid token ");
				
			//todo: check token expiration
				
			//check scope
			}else if(!oauth.scopes.contains(scope)){
				api.appendError("scope not authorized: "+scope);			
			
			}else if(oauth.userId != null){
				//get data for the patient, depending on the scope and record id
				//type determines the format of response content
				//use cases:
				//1. scope=list: list recent patient records (no id), in json
				//2. scope=summary and id is present: return summary of a specific patient record 
				//   like doctor visit or lab test, in C-CDA
				//3. scope=summary and no id: return summary of latest doctor visit as CCD in C-CDA (TODO)
				//4. scope=search: search patient records and return a list of records, in json. 
				//more use cases to come
				String content = EhrDataService.getDataForApi(oauth.userId, scope, id, type);
				api.setContent(content);
			}else{
				api.appendError("server error");	
			}
		}catch(Exception e){
			api.appendError("server error");			
			LOG.error(e,e);
		}
		return api;
	}		

	
}
