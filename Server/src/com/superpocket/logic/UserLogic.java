package com.superpocket.logic;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.superpocket.dao.DBConnector;

public class UserLogic {
	private static final Logger logger = LogManager.getLogger();
	/**
	 * 登录
	 * @param email 邮箱
	 * @param password 密码
	 * @return
	 */
	public static boolean SignIn(String email, String password) {
		String sql = String.format("select password from user where email='%s' limit 1", email);
		ResultSet rs = DBConnector.query(sql);
		try {
			if (!rs.next() || !rs.getString(1).equals(password)) return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
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
		sql = String.format("insert into user(email, password) values('%s', '%s')", email, password);
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
