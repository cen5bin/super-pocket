<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <title></title>
    <link rel="stylesheet" href="css/main.css">
    <script src="js/jquery.js"></script>
    <script src="js/main.js"></script>
</head>
<body>

<% if (session.getAttribute("uid") == null) {
	response.sendRedirect(request.getContextPath()+"/"); 
	return;
}
	int uid = Integer.parseInt(session.getAttribute("uid").toString());
%>

<input type="hidden" value="<%=uid %>" id="sp-admin-uid">

<div id="sp-admin-container">
    <iframe  id="sp-class-list"src="classlist.jsp?uid=<%=uid%>"></iframe>
    <iframe id="sp-side-bar" class="sp-admin-iframe" src="sidebar.jsp?uid=<%=uid%>"></iframe>
    <!--<iframe id="sp-post-list" class="sp-admin-iframe"></iframe>-->
    <iframe id="sp-admin-content" class="sp-admin-iframe" src="postlist.jsp?uid=<%=uid %>"></iframe>
</div>

<!--     <form method="post" id="sp-signout-form" action="SignOut"></form> -->


<script>
    $(document).ready(function(){
        $('#sp-admin-content').click(function(){
            hide_classes();
        });
    });

</script>
</body>
</html>