<%@page import="org.json.JSONObject"%>
<%@page import="com.superpocket.logic.ContentLogic"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="css/style.css">
<script src="js/jquery.js"></script>
<!-- <title>Insert title here</title> -->
<%
	
	int pid = Integer.parseInt(request.getParameter("pid"));
	JSONObject json = ContentLogic.getPost(pid);
	%>
<%=json.get("head") %>
</head>
<body>
<div id="sp-main-container">

<!-- <div id="sp-content-title"> -->
<!-- asadda -->
<!-- </div> -->

<%-- 	<h1> <%=json.get("title") %></h1> --%>
	<%=json.get("content") %>
	<% 
%>
</div>
<script type="text/javascript">
$(document).ready(function(){
    $(document).click(function(){
        parent.hide_classes();
    });
});
</script>
</body>
</html>