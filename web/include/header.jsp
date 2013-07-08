<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.activ.bbpi.*" %>
<%@ page import="java.util.*" %>
<% 
	String context = request.getContextPath(); 
%> 
 
<table cellspacing="0" cellpadding="10" border="0" width="100%" class="headmenu">
	<tr>
	<td width=50 align=center valign=top ><a href="<%=context %>/client">
		<img src="<%=context %>/bluebutton.jpg" alt="BB+ API Client" width="50" border="0" /></a>
	</td>
	<td align=left class=""><a href="<%=context %>/client">BBPI</a></td>
	<td align=center><a href="<%=context %>/client-register">Client Registration</a>
	</td>
	<td align=center><a href="<%=context %>/client-oauth">Patient Authorization</a>
	</td>
	<td align=center><a href="<%=context %>/client-data">Patient Data</a>
	</td>
	</tr>
</table>

