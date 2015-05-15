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
	 * 正式保存文章，需要讲flag置为1
	 * @param pid
	 * @param tags
	 * @return
	 */
	public static boolean save(int pid, String tags) {
		String sql = "update post set flag=1, tags=? where pid=?";
		int ret = DBConnector.update(sql, tags, pid);
		return ret != -1;
	}
	
	/**
	 * 临时存储文章，flag置为0，等到客户端确认时再变成1即可
	 * @param uid  
	 * @param title 网页标题
	 * @param content 正文内容
	 * @return
	 */
	public static int tempSave(int uid, String title, String content) {
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
		for (String label : labels)
		ret.put(label);
		return ret;
	}
}
