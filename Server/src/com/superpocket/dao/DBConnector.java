package com.superpocket.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DBConnector {
	private static Connection conn = null;
	private static Statement st = null;
	private static String driver = "com.mysql.jdbc.Driver";
	private static String url = "jdbc:mysql://localhost/";
	private static String user = "root";
	private static String password = "asd123";
	private static String db = "super_pocket";
	
	private static final Logger logger = LogManager.getLogger();
	
	static {
		boolean ret = DBConnector.connect(db);
		if (!ret) logger.error("数据库连接失败");
	}
	
	/**
	 * 数据库连接
	 * @param db 数据库名称
	 * @return
	 */
	public static boolean connect(String db) {
		if (conn != null) return true;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url+db, user, password);
			st = conn.createStatement();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static ResultSet query(String sql) {
		ResultSet ret = null;
		try {
			ret = st.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 查询数据的高级用法
	 * @param sql
	 * @param values
	 * @return
	 */
	public static ResultSet query(String sql, Object ...values) {
		ResultSet rs = null;
		try {
			PreparedStatement pst = conn.prepareStatement(sql);
			for (int i = 0; i < values.length; ++i)
				pst.setObject(i+1, values[i]);
			rs = pst.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	
	
	public static boolean update(String sql) {
		try {
			int ret = st.executeUpdate(sql);
			logger.info(ret);
			if (ret == -1) return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * 插入数据的高级用法，可以防止注入，对于那些复杂的数据也可以用这个，比如说包含引号
	 * @param sql
	 * @param values
	 * @return
	 */
	public static int update(String sql, Object ...values) {
		int ret = 0;
		try {
			PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			for (int i = 0; i < values.length; ++i)
				pst.setObject(i+1, values[i]);
			ret = pst.executeUpdate();
			if (ret == -1) return -1;
			ResultSet keys = pst.getGeneratedKeys();
			if (keys.next())
				ret = keys.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public static void main(String[] args) {
		ResultSet rs = DBConnector.query("select * from user");
		try {
			while (rs.next()) {
				logger.info(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
}
