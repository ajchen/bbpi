<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isErrorPage="true" %>
<%
	String context = request.getContextPath(); 
%>
<html>
<head> 
	<title>Error Message</title>
 	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="icon" href="favicon.ico" type="image/x-icon"/>
	<jsp:include page="/include/style.css" />
</head>
<body>
<jsp:include page="/include/header.jsp" />
<table cellspacing="20" cellpadding="20" border="0" width="100%" class="headtext">
<tr><td align=center>
<p>
<p>
Oops, can't find the page. 
<p>
You may start from the <a href="<%=context %>">home page</a> again.
<p>
<p>
</td></tr>
</table>
<p>
<jsp:include page="/include/footer.jsp"/>
</body>
</html>