package com.superpocket.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
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
 * Servlet implementation class ChooseMethod
 */
@WebServlet("/ChooseMethod")
public class ChooseMethod extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static final Logger logger = LogManager.getLogger();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChooseMethod() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String data = DataKit.getJsonData(request.getReader());

		try {
			JSONObject json = new JSONObject(data);
			logger.debug(json);
			int uid = UserLogic.judgeUserStatus(request.getCookies());
			JSONObject retObj = new JSONObject();
			if (uid == 0) {
				retObj.put("success", "no");
			}
			else {
				int method_id = Integer.parseInt(json.getString("method_id"));
				boolean ret = UserLogic.setMethod(uid, method_id);
				if (ret) retObj.put("success", "yes");
				else retObj.put("success", "no");
			}
			NetLogic.writeJson(response, retObj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
