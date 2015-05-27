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
import com.superpocket.logic.NetLogic;
import com.superpocket.logic.UserLogic;

/**
 * Servlet implementation class SignUp
 */
@WebServlet("/Secure/SignUp")
public class SignUp extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger();   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SignUp() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		String s = request.getParameter("tag");
		s = new String(s.getBytes("ISO8859_1"), "utf-8");
		out.print(s);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String data = DataKit.getJsonData(request.getReader());
		try {
			JSONObject json = new JSONObject(data);
			String email = json.getString("email");
			String password = json.getString("password");
			boolean ret = UserLogic.SignUp(email, password);
			JSONObject retObj = new JSONObject();
			if (ret) {
				logger.debug("sign up success");
				NetLogic.addCookie(response, new Cookie("email", email), false);
				NetLogic.addCookie(response, new Cookie("token", UserLogic.generateUserToken(email)), true);
				int uid = UserLogic.getUid(email);
				NetLogic.addCookie(response, new Cookie("uid", uid + ""), true);
				retObj.put("success", "yes");
			}
			else {
				logger.debug("sign in failed");
				retObj.put("success", "no");
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
