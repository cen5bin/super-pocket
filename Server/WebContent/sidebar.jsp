<%@page import="com.superpocket.logic.UserLogic"%>
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

<div id="sp-side-bar-container">

    <img src="img/logo.png" id="sp-logo">
    <!--<p></p>-->


    <div id="sp-side-bar-item-container">
        <div class="sp-side-bar-item">
            <a href="#" func="show_classes()">
                <img class="sp-float-left" src="img/notebook.png">
                <div class="sp-float-left">全部分类</div>

            </a>

        </div>
        <div class="sp-side-bar-item">
            <a href="#" func="show_all_post()">
            <img class="sp-float-left" src="img/note.png"> <div class="sp-float-left">全部文章</div>
            </a>
        </div>

    </div>
    
    <%
    if (request.getParameter("uid") == null) {
		response.sendRedirect(request.getContextPath()+"/");
		return;
	}
	int uid = Integer.parseInt(request.getParameter("uid"));
    %>
    
<div id="sp-account">
    <img src="img/man.png">
    
    <%=UserLogic.getEmail(uid) %> &nbsp;
    <a href="#" style="text-decoration:underline; color:rgb(0,190,107); font-size:12px;">注销</a></div>
</div>

<script>
    $(document).ready(function(){
        $('.sp-side-bar-item').each(function(){

            $($(this).find('a')).click(function(){
//                alert('zz');
                eval('parent.' + $(this).attr('func'))
//                parent.show_classes();
            });

            $(this).mouseenter(function(){
                $(this).css('color', 'rgb(0, 190, 107)');
                console.log($(this).find('img')[0]);

            }).mouseleave(function(){
                $(this).css('color', '#cccccc');
            });
        });
    });
</script>

</body>
</html>