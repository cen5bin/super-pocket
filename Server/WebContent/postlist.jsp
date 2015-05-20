<%@page import="com.superpocket.entity.PostItem"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.superpocket.logic.ContentLogic"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
<meta charset="UTF-8">
<title></title>
<link rel="stylesheet" href="css/main.css">
<script src="js/jquery.js"></script>
<style>
html, body {
	overflow-y: auto;
}
</style>
</head>
<body>
	<% ArrayList<PostItem> items = null;
	ContentLogic.getAllPost(2); 
	String tag = request.getParameter("tag");
	int uid = 2;
	if (tag != null) {
		tag = new String(tag.getBytes("ISO8859_1"), "utf-8");
		items = ContentLogic.getPostListByTag(uid, tag);
	}
	else {
		items = ContentLogic.getAllPost(uid);
		tag = "全部文章";
	}
	%>
	<div id="sp-post-list-header">
		<p><%=tag %></p>
		<p style="font-size: 14px; color: #cccccc;"><%=items.size() %> 篇文章</p>
	</div>

	<div id="sp-post-list-header-tt"></div>

	<div id="sp-post-list-container">

	<%for (PostItem item : items) {%>

		<div class="sp-post-list-item-container">
		<a href="#" onclick="show_post_content(<%=item.getPid() %>)">
			<div class="sp-post-list-item">
				<div class="sp-post-title"><%=item.getTitle() %></div>
				<div class="sp-post-content"><%=item.getPlainText() %></div>
			</div>
			</a>
		</div>
		
		<% }%>


	</div>

	<script>
    $(document).ready(function(){
        $(document).click(function(){
            parent.hide_classes();
        });
    });
    
    function show_post_content(pid) {
    	parent.show_post_content(pid);
    }
</script>
</body>
</html>