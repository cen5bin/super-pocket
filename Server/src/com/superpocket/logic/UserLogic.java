package com.superpocket.logic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.http.Cookie;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.superpocket.dao.DBConnector;
import com.superpocket.kit.SecureKit;

public class UserLogic {
	private static final Logger logger = LogManager.getLogger();
	/**
	 * 登录
	 * @param email 邮箱
	 * @param password 密码
	 * @return 0表示登录失败，否则返回uid
	 */
	public static int SignIn(String email, String password) {
		String sql = String.format("select uid, password, salt from user where email='%s' limit 1", email);
		ResultSet rs = DBConnector.query(sql);
		try {
			if (!rs.next() || !SecureKit.encryptPassword(password, rs.getString(3)).equals(rs.getString(2))) return 0;
			return rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 判断用户身份，返回的是用户uid，如果尚未登录则是0
	 * @param cookies
	 * @return
	 */
	public static int judgeUserStatus(Cookie[] cookies) {
		if (cookies == null) return 0;
		String account = "";
		String token = "";
		int uid = 0;
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("email")) account = cookie.getValue();
			else if (cookie.getName().equals("token")) token = cookie.getValue();
			else if (cookie.getName().equals("uid")) uid = Integer.parseInt(cookie.getValue());
		}
		logger.debug(account);
		if (account.equals("") | token.equals("")) return 0;
		if (SecureKit.encryptCookie(account).equals(token)) return uid;
		return 0;
	}
	
	/**
	 * 产生用户的token
	 * @param email
	 * @return
	 */
	public static String generateUserToken(String email) {
		String ret = SecureKit.encryptCookie(email);
		return ret;
	}
	
	/**
	 * 注册功能
	 * @param email 邮箱
	 * @param password 密码
	 * @return
	 */
	public static boolean SignUp(String email, String password) {
		String sql = String.format("select uid from user where email='%s' limit 1", email);
		ResultSet rs = DBConnector.query(sql);
		try {
			if (rs.next()) return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String salt = SecureKit.generateSalt(32);
		String encryptedPass = SecureKit.encryptPassword(password, salt);
		sql = String.format("insert into user(email, password, salt) values('%s', '%s', '%s')", email, encryptedPass, salt);
		return DBConnector.update(sql);
	}
	
	/**
	 * 获取uid
	 * @param email 用户邮箱
	 * @return
	 */
	public static int getUid(String email) {
		String sql = String.format("select uid from user where email='%s' limit 1", email);
		ResultSet rs = DBConnector.query(sql);
		try {
			if (!rs.next()) return 0;
			return rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 根据uid获取email
	 * @param uid
	 * @return
	 */
	public static String getEmail(int uid) {
		String sql = "select email from user where uid=? limit 1";
		ResultSet rs = DBConnector.query(sql, uid);
		try {
			if (!rs.next()) return null;
			return rs.getString(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取uid对应的用户的所有分类
	 * @param uid
	 * @return
	 */
	public static HashMap<String, Integer> getTagList(int uid) {
		HashMap<String, Integer> ret = new HashMap<String, Integer>();
		String sql = String.format("select tags from post where uid=? and flag=1");
		ResultSet rs = DBConnector.query(sql, uid);
		try {
			while (rs.next()) {
				String[] tags = rs.getString(1).split(",");
				for (String tag : tags)
					if (ret.get(tag) == null) ret.put(tag, 1);
					else {
						int c = ret.get(tag) + 1;
						ret.put(tag, c);
					}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 设置方法
	 * @param uid
	 * @param method_id
	 * @return
	 */
	public static boolean setMethod(int uid, int method_id) {
		String sql = "update setting set method_id=? where uid = ? limit 1";
		int id = DBConnector.update(sql, method_id, uid);
		if (id == -1) return false;
		return true;
	}
	
	public static int getMethod(int uid) {
		String sql = "select method_id from setting where uid = ? limit 1";
		ResultSet rs = DBConnector.query(sql, uid);
		try {
			if (rs.next()) return rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}
	
	
	public static void main(String[] args) {
		int uid = UserLogic.SignIn("love@gmail.com", "love77");
		if (uid > 0) logger.info("登录成功");
		else logger.info("登录失败");
		
		boolean ret = SignUp("cen5bin@163.com", "asd123");
		if (ret) logger.info("注册成功");
		else logger.info("注册失败");
		
		HashMap<String, Integer> tt = UserLogic.getTagList(2);
		for (String key : tt.keySet()) logger.debug(key+": " + tt.get(key));
	}
}
