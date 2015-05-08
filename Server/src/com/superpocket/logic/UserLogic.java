package com.superpocket.logic;

import java.sql.ResultSet;
import java.sql.SQLException;

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
	 * @return
	 */
	public static boolean SignIn(String email, String password) {
		String sql = String.format("select password, salt from user where email='%s' limit 1", email);
		ResultSet rs = DBConnector.query(sql);
		try {
			if (!rs.next() || !SecureKit.encryptPassword(password, rs.getString(2)).equals(rs.getString(1))) return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public static String judgeUserStatus(Cookie[] cookies) {
		if (cookies == null) return null;
		String account = "";
		String token = "";
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("email")) account = cookie.getValue();
			else if (cookie.getName().equals("token")) token = cookie.getValue();
		}
		if (account.equals("") | token.equals("")) return null;
		if (SecureKit.encryptCookie(account).equals(token)) return account;
		return null;
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
	
	public static void main(String[] args) {
		boolean ret = UserLogic.SignIn("love@gmail.com", "love77");
		if (ret) logger.info("登录成功");
		else logger.info("登录失败");
		
		ret = SignUp("cen5bin@163.com", "asd123");
		if (ret) logger.info("注册成功");
		else logger.info("注册失败");
	}
}
