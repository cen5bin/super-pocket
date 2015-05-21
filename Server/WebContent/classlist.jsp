<%@page import="com.superpocket.logic.UserLogic"%>
<%@page import="java.util.HashMap"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <title></title>
    <link rel="stylesheet" href="css/main.css">
    <script src="js/jquery.js"></script>
</head>
<body>
<div id="sp-class-list-header">
    <p>全部分类</p>
    <div id="sp-class-filter"><input type="text" placeholder="输入分类名称"></div>
</div>
<div id="sp-class-list-container">

<% 
if (request.getParameter("uid") == null) {
	response.sendRedirect(request.getContextPath()+"/");
	return;
}
int uid = Integer.parseInt(request.getParameter("uid"));

HashMap<String, Integer> labels = UserLogic.getTagList(uid);
	for (String key : labels.keySet()) {
%>
    <div class="sp-class-list-item-container">
        <a href="#" onclick="show_posts_in('<%=key %>')">
        <div class="sp-class-list-item">
        <div class="sp-class-name"><%=key %></div>
        <div class="sp-class-desc"><%=labels.get(key) %> 篇文章</div>
        </div>
        </a>
    </div>
    <%} %>

</div>
<script>
    $(document).ready(function(){
        $('#sp-class-list-container').on('mousewheel', function(e) {
            var oEvent = e.originalEvent,
                    delta  = oEvent.deltaY || oEvent.wheelDelta;
        });
    });
    
    function show_posts_in(tag) {
    	parent.show_posts_in(tag);
    }

</script>
</body>
</html>