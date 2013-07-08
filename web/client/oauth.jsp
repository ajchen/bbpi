<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.activ.bbpi.*" %>
<%@ page import="com.activ.bbpi.bean.*" %>
<%@ page import="com.activ.bbpi.test.*" %>
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
  <h1>Test BB+ API: Patient Authorization</h1> 
  <div align=center class="text-red"><%=bean.getMessage()%><p></div>

  <table cellspacing="5" cellpadding="5" border="0" width="100%" class="bg">
	  <tr>
	    <td width=100 align=center class="slide-text24-white bg-dodgerblue"><%=bean.userId %></td>
  	  <td>
		  <table cellspacing="10" cellpadding="10" border="0" width="100%" class="bg headtext">
		  <tr>
				<td align=center class="slide-text24-white bg-lightblue">Provider</td>
				<td align=center class="slide-text24-white bg-wheat">Patient ID / Authorization</td>
				<td align=center class="slide-text24-white bg-lightgray">Action</td>
			</tr>	    
		  <%for(String name:bean.providers.keySet()){ 
		  	Provider p = bean.providers.get(name);
		  %>
		  <tr>
		    <td align=left class="bg-lightblue"><%=p.providerName %></td>
	    	<%if(p.accessToken == null){ %>
		    <td align=left class="bg-wheat"><%=p.userId %>
		    	<br> Not authorized
	    	<%}else{ %>
		    <td align=left class="headtext-orange bg-wheat"><%=p.userId %>
		    	<br>Authorized for scopes <%=p.scopes %>		    	
	    	<%} %>
		    </td>
		    <td align=center class="bg-lightgray" >
				<form method="POST" action="<%=context %>/client" >
		    	<input type="hidden" name="provider" value="<%=name %>">
			    <button type="submit" name="act" value="oauth" class="bluebutton">Authorize</button>
				</form>
		    </td>
		  </tr>		  
		  <%} %>
			</table>    
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