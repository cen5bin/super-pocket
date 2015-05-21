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
import com.superpocket.kit.HtmlKit;
import com.superpocket.logic.NetLogic;
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
		
		HtmlKit.getHtmlHeader("http://blog.csdn.net/wangqiuyun/article/details/11483261");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		try {
			JSONObject retObj = new JSONObject();
			if (UserLogic.judgeUserStatus(request.getCookies()) != 0) {
				logger.debug("auto sign in success");
				retObj.put("success", "yes");
			}
			else {
				logger.debug("sign in");
				String email = "";
				String password = "";
				boolean fromWeb = false;
				if (false && request.getParameter("from-web").equals("1")) {
					logger.debug("zz");
					email = request.getParameter("email");
					password = request.getParameter("password");
					fromWeb = true;
				}
				else {
					String data = DataKit.getJsonData(request.getReader());
					logger.debug(data);
					JSONObject json = new JSONObject(data);
					logger.debug(json);
					email = json.getString("email");
					password = json.getString("password");
				}
				int uid = UserLogic.SignIn(email, password);
				if (uid > 0) {
					logger.debug("sign in success");
					NetLogic.addCookie(response, new Cookie("email", email), false);
					NetLogic.addCookie(response, new Cookie("token", UserLogic.generateUserToken(email)), true);
					NetLogic.addCookie(response, new Cookie("uid", uid + ""), true);
//					if (fromWeb) request.getRequestDispatcher("/personal.jsp").forward(request, response);
					retObj.put("success", "yes");
				}
				else {
//					if (fromWeb) request.getRequestDispatcher("/login.html").forward(request, response);
					logger.debug("sign in failed");
					retObj.put("success", "no");
				}
			}
			NetLogic.writeJson(response, retObj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}
