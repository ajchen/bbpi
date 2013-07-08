<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.activ.bbpi.*" %>
<%@ page import="com.activ.bbpi.auth.*" %>
<%@ page import="com.activ.bbpi.bean.*" %>
<%@ page import="java.util.*" %>
<%
  String context = request.getContextPath();
  ReqBean bean = (ReqBean)request.getAttribute(ReqBean.NAME);
%>
<html>
<head>
	<title>User Consent</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <LINK href="<%=context %>/include/style.css" rel="stylesheet" type="text/css">
	<link rel="icon" href="<%=context %>/favicon.ico" type="image/x-icon"/>
</head>
<body>
<jsp:include page="/include/header.jsp" />
<table cellspacing="0" cellpadding="0" border="0" width="100%">
<tr>
<!-- left column -->
<td width="150" align="left" valign="top">&nbsp;</td>
<!-- center column -->
<td align="center" valign="top" >
  <h1>User Consent</h1> 
  <div align=center class="text-red"><%=bean.getMessage()%><p></div>

	<form method="POST" action="<%=context %>/consent" >
		<%if(bean.clientId != null) {%>
		<input type="hidden" name="<%=OAuth.OAUTH_CLIENT_ID %>" value="<%=bean.clientId %>">
		<%} %>
		<%if(bean.redirectUri != null) {%>
		<input type="hidden" name="<%=OAuth.OAUTH_REDIRECT_URI %>" value="<%=bean.redirectUri %>">
		<%} %> 
		<%if(bean.scope != null) {%>
		<input type="hidden" name="<%=OAuth.OAUTH_SCOPE %>" value="<%=bean.scope %>">
		<%} %>
		<%if(bean.state != null) {%>
		<input type="hidden" name="<%=OAuth.OAUTH_STATE %>" value="<%=bean.state %>">
		<%} %>
  <table cellspacing="0" cellpadding="10" border="0" width="400" class="bg">
		  <tr><td align=left class="text">
		  This application (<%=bean.clientName %>) would like to access your patient data.<p>
		  Access scope: <%=bean.scope %>. <p> 
		  If you want to grant permission, enter your patient portal user id and password and then Accept. 
		  </td>
		  </tr>
		  <tr>
		    <td align=left class="headtext">User Id
		    <br><input type="text" size="40" name="userid" value="<%=bean.userId() %>">
		    </td>
		  </tr>
		  <tr>
		    <td align=left class="headtext">Password
		    <br><input type="password" size="40" name="password" value="">
		    </td>
		  </tr>
		  <tr>
		    <td align=left >
		    <button type="submit" name="act" value="accept" class="bluebutton">Accept</button>&nbsp;&nbsp;&nbsp;
		    <button type="submit" name="act" value="deny" class="bluebutton">Deny</button>
		    </td>
		  </tr>		  
  </table>
	</form>
<p>
<p>
</td>
</tr>	
</table>	

<jsp:include page="/include/footer.jsp"/>
</body>
</html>