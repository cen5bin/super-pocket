package com.superpocket.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DBController {
	private static Connection conn = null;
	private static String driver = "com.mysql.jdbc.Driver";
	private static String url = "jdbc:mysql://localhost/";
	private static String user = "root";
	private static String password = "asd123";
	
	private static final Logger logger = LogManager.getLogger();
	
	public static boolean connect(String db) {
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url+db, user, password);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		logger.info("zz");
		logger.debug("xx");
		logger.warn("xx");
		logger.error("zz");
		
		boolean ret = DBController.connect("super_pocket");
	}
}
