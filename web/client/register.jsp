<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.activ.bbpi.*" %>
<%@ page import="com.activ.bbpi.bean.*" %>
<%@ page import="com.activ.bbpi.auth.reg.*" %>
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
  <h1>Test BB+ API: Client Registration</h1> 
  <div align=center class="text-red"><%=bean.getMessage()%><p></div>

	<form method="POST" action="<%=context %>/client" >
  <table cellspacing="5" cellpadding="5" border="0" width="100%" class="bg headtext">
	  <tr>
	    <td width=200 align=left class="slide-text24-white bg-skyblue">Provider BB+ API</td>
	    <td width=150 align=right >Register URL</td>
	    <td align=left class="bg-lightblue"><%=bean.registerUrl %></td>
	  </tr>
	  <tr>
	    <td rowspan=8 align=left class="slide-text24-white bg-wheat">Client Information</td>
	    <td align=right >Client name</td>
	    <td align=left class="bg-wheat"><%=bean.param(OAuthReg.Request.CLIENT_NAME) %></td>
	  </tr>
	  <tr>
	    <td align=right >Client URI</td>
	    <td align=left class="bg-wheat"><%=bean.param(OAuthReg.Request.CLIENT_URI) %></td>
	  </tr>
	  <tr>
	    <td align=right >Redirect URI</td>
	    <td align=left class="bg-wheat"><%=bean.param(OAuthReg.Request.REDIRECT_URIS) %></td>
	  </tr>
	  <tr>
	    <td align=right >Response type</td>
	    <td align=left class="bg-wheat"><%=bean.param(OAuthReg.Request.RESPONSE_TYPES) %></td>
	  </tr> 
	  <tr>
	    <td align=right >Grant type</td>
	    <td align=left class="bg-wheat"><%=bean.param(OAuthReg.Request.GRANT_TYPES) %></td>
	  </tr>
	  <tr>
	    <td align=right >Token method</td>
	    <td align=left class="bg-wheat"><%=bean.param(OAuthReg.Request.TOKEN_METHOD) %></td>
	  </tr>
	  <tr>
	    <td align=right >Scope</td>
	    <td align=left class="bg-wheat"><%=bean.param(OAuthReg.Request.SCOPE) %></td>
	  </tr>
	  <tr>
	    <td align=right >&nbsp;</td>
	    <td>
	    <button type="submit" name="act" value="register" class="bluebutton">Register Client</button>
	    </td>
	  </tr>		  
	  <tr>
	  </tr>
	  <tr>
	    <td rowspan=2 align=left class="slide-text24-white bg-sandybrown">Registration</td>
	    <td align=right >Client ID</td>
	    <td align=left class="bg-sandybrown">
	    <%if(!bean.registered){ %>
	      Not Registered Yet
	    <%} %>
	    <%=bean.param(OAuthReg.Response.CLIENT_ID) %>
	    </td>
	  </tr>
	  <tr>
	    <td align=right >Client secret</td>
	    <td align=left class="bg-sandybrown"><%=bean.param(OAuthReg.Response.CLIENT_SECRET) %></td>
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