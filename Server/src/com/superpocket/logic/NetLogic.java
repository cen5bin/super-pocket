package com.superpocket.logic;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public class NetLogic {
	/**
	 * 向客户端写json数据
	 * @param response
	 * @param json
	 */
	public static void writeJson(HttpServletResponse response, JSONObject json) {
		try {
			response.setContentType("application/json; charset=utf-8");
			PrintWriter out = response.getWriter();
			out.print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 向客户端写cookie
	 * @param response
	 * @param cookie
	 * @param httpOnly 是否是httponly属性
	 */
	public static void addCookie(HttpServletResponse response, Cookie cookie, boolean httpOnly) {
		cookie.setMaxAge(Integer.MAX_VALUE);
		if (httpOnly) cookie.setHttpOnly(true);
		response.addCookie(cookie);
	}
}
