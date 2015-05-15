package com.superpocket.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.superpocket.dao.DBConnector;
import com.superpocket.kit.RegexKit;
import com.superpocket.kit.SettingKit;

public class ContentLogic {
	
	private static final Logger logger = LogManager.getLogger();
	
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
	 * @param head 网页head
	 * @return
	 */
	public static int tempSave(int uid, String title, String content, String head) {
		String sql = "insert into post(uid, title, content, head) values(?, ?, ?, ?)";
		return DBConnector.update(sql, uid, title, content, head);
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
	
	/**
	 * 获取文章内容
	 * @param pid
	 * @return
	 */
	public static JSONObject getPost(int pid) {
		String sql = "select title, tags, content, head from post where pid=? limit 1";
		ResultSet rs = DBConnector.query(sql, pid);
		JSONObject json = new JSONObject();
		try {
			if (rs.next())
			json.put("title", rs.getString(1))
			.put("tags", rs.getString(2))
			.put("head", rs.getString(4))
			.put("content", rs.getString(3))
			;
			logger.debug(json.get("head"));
		} catch (JSONException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	
	public static String getHtmlHeader(String htmlUrl) {
		logger.debug("zzzz");
		try {
			URL url = new URL(htmlUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
			
			int res = connection.getResponseCode();
			logger.debug(res);
			if (res == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String string = "";
				StringBuilder sb = new StringBuilder();
				while ((string = in.readLine()) != null) sb.append(string);
				in.close();
				return RegexKit.getHeader(sb.toString());
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
