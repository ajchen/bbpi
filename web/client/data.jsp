<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.activ.bbpi.*" %>
<%@ page import="com.activ.bbpi.bean.*" %>
<%@ page import="com.activ.bbpi.test.*" %>
<%@ page import="com.activ.cdaj.data.*" %>
<%@ page import="java.util.*" %>
<%
  String context = request.getContextPath();
  ReqBean bean = (ReqBean)request.getAttribute(ReqBean.NAME);
%>
<html>
<head>
	<title>Test BB+ API</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <LINK href="<%=context %>/include/style.css" rel="stylesheet" type="text/css">
	<link rel="icon" href="<%=context %>/favicon.ico" type="image/x-icon"/>
</head>
<body>
<jsp:include page="/include/header.jsp"/>
<table cellspacing="0" cellpadding="0" border="0" width="100%">
<tr>
<!-- center column -->
<td align="center" valign="top" >
  <h1>Test BB+ API: Patient Data</h1> 
  <div align=center class="text-red"><%=bean.getMessage()%><p></div>

  <table cellspacing="5" cellpadding="5" border="0" width="100%" class="bg">
	  <tr>
			<td align=center class="slide-text24-white bg-dodgerblue">User</td>
			<td align=center class="slide-text24-white bg-lightblue">Provider</td>
			<td align=center class="slide-text24-white bg-wheat">Patient Data</td>
		</tr>	    
	  <tr>
	    <td width=100 align=center valign=top class="slide-text24-white bg-dodgerblue"><%=bean.userId %></td>
  	  <td valign=top >
		  <table cellspacing="10" cellpadding="10" border="0" width="100%" class="bg headtext">
		  <%for(String name:bean.providers.keySet()){ 
		  	Provider p = bean.providers.get(name);
		  %>
		  <tr>
		    <td width=200 align=left class="bg-lightblue">
		      <%=p.providerName %><p>
			    	<%if(p.accessToken == null){ %>
			    		Not authorized
			    	<%}else{ %>
							<form method="POST" action="<%=context %>/client" >
				    	<input type="hidden" name="provider" value="<%=name %>">
					    <button type="submit" name="act" value="list" class="bluebutton">List Records</button><br>		    
							</form>
				    	<%if(p.records != null) {%>
			  			<table cellspacing="5" cellpadding="2" border="0" width="100%" class="bg headtext">
			  			  <%for(Record r:p.records){ %>
							  <tr>
				  		  <td align=left class="bg"><%=Record.dateLabel.format(r.date) %></td>
				  		  <td align=left class="bg"><%=r.name %></td>
				  		  <td>
									<form method="POST" action="<%=context %>/client" >
						    	<input type="hidden" name="provider" value="<%=name %>">
						    	<input type="hidden" name="id" value="<%=r.id %>">
							    <button type="submit" name="act" value="summary" class="bluebutton">Summary</button><br>		    
									</form>
						    </td>
						    </tr>
					      <%} %>
							</table>    
							<%} %>
			    	<%} %>
		    </td>
		  </tr>		  
		  <%} %>
			</table>    
		  </td>
			<td align=center valign=top class="text bg-wheat">
			<%if(bean.dataSource != null){ %>
				Data from Provider: <%=bean.dataSource.providerName %><br>
				API endpoint: <%=bean.apiEndpoint %><br>
				Patient record id: <%=bean.recordId %><br>
			<%} %>
		  	<TEXTAREA name="text" rows="25" cols="80"><%=bean.data() %></TEXTAREA>  
			</td>
		</tr>
  </table>
<p>
<p>
</td>
</tr>	
</table>	

<jsp:include page="/include/footer.jsp"/>
</body>
</html>