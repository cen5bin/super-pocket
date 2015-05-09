package com.superpocket.logic;

import com.superpocket.dao.DBConnector;

public class ContentLogic {
	public static boolean saveData(int uid, String title, String tags, String content) {
		String sql = String.format("insert into post(uid, title, tags, content) values(%d, '%s', '%s', '%s')", 
				uid, title, tags, content);
		return DBConnector.update(sql);
	}
}
