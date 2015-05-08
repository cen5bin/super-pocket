package com.superpocket.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.superpocket.kit.DataKit;
import com.superpocket.logic.UserLogic;

/**
 * Servlet implementation class SignIn
 */
@WebServlet("/Secure/SignIn")
public class SignIn extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger();   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SignIn() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		out.print("asd");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			JSONObject retObj = new JSONObject();
			if (UserLogic.judgeUserStatus(request.getCookies()) != null) {
				logger.debug("auto sign in success");
				retObj.put("success", "yes");
			}
			else {
				logger.debug("sign in");
				String data = DataKit.getJsonData(request.getReader());
				JSONObject json = new JSONObject(data);
				String email = json.getString("email");
				String password = json.getString("password");
				boolean ret = UserLogic.SignIn(email, password);
				if (ret) {
					logger.debug("sign in success");
					Cookie emailCookie = new Cookie("email", email);
					Cookie tokenCookie = new Cookie("token", UserLogic.generateUserToken(email));
					emailCookie.setMaxAge(Integer.MAX_VALUE);
					tokenCookie.setMaxAge(Integer.MAX_VALUE);
					response.addCookie(emailCookie);
					response.addCookie(tokenCookie);
					retObj.put("success", "yes");
				}
				else {
					logger.debug("sign in failed");
					retObj.put("success", "no");
				}
			}
			response.setContentType("application/json; charset=utf-8");
			PrintWriter out = response.getWriter();
			out.print(retObj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}
