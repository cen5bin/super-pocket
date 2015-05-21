package com.superpocket.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.superpocket.logic.NetLogic;
import com.superpocket.logic.UserLogic;

/**
 * Servlet implementation class WebLogin
 */
@WebServlet("/WebLogin")
public class WebLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       private static final Logger logger = LogManager.getLogger();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WebLogin() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		int uid = UserLogic.judgeUserStatus(request.getCookies());
		if (uid != 0) {
			logger.debug("auto login success");
			HttpSession session = request.getSession();
			session.setAttribute("uid", uid);
			request.getRequestDispatcher("/personal.jsp").forward(request, response);
		}
		else {
			logger.debug("auto login failed");
			request.getRequestDispatcher("/login.html").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		logger.debug(email);
		logger.debug(password);
		int uid = UserLogic.SignIn(email, password);
		if (uid > 0) {
			logger.debug("sign in success");
			NetLogic.addCookie(response, new Cookie("email", email), false);
			NetLogic.addCookie(response, new Cookie("token", UserLogic.generateUserToken(email)), true);
			NetLogic.addCookie(response, new Cookie("uid", uid + ""), true);
			response.sendRedirect(request.getContextPath()+"/personal.jsp");
		}
		else {
			logger.debug("sign in failed");
			response.sendRedirect(request.getContextPath()+"/");
		}
	}

}
