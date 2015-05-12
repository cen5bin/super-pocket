package com.superpocket.logic;

import java.util.ArrayList;

import org.json.JSONArray;

import com.superpocket.dao.DBConnector;
import com.superpocket.kit.SettingKit;

public class ContentLogic {
	public static boolean saveData(int uid, String title, String tags, String content) {
		String sql = String.format("insert into post(uid, title, tags, content) values(%d, '%s', '%s', '%s')", 
				uid, title, tags, content);
		return DBConnector.update(sql);
	}
	
	/**
	 * 临时存储文章，flag置为0，等到客户端确认时再变成1即可
	 * @param uid  
	 * @param title 网页标题
	 * @param content 正文内容
	 * @return
	 */
	public static boolean tempSave(int uid, String title, String content) {
//		String sql1 = String.format("insert into post(uid, title, content) values(%d, '%s', '%s')", 
//				uid, title, content);
		String sql = "insert into post(uid, title, content) values(?, ?, ?)";
		return DBConnector.update(sql, uid, title, content);
	}
	
	/**
	 * 网页内容分类
	 * @param uid
	 * @param title
	 * @param content
	 * @return
	 */
	public static JSONArray classify(int uid, String title, String content) {
		ArrayList<String> labels = SettingKit.getClassifier(uid).classify(title, content);
		JSONArray ret = new JSONArray();
		ret.put(labels);
		return ret;
	}
}
