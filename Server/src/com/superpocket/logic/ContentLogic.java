package com.superpocket.logic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.superpocket.dao.DBConnector;
import com.superpocket.entity.PostItem;
import com.superpocket.kit.PostKit;
import com.superpocket.kit.SettingKit;
import com.superpocket.kit.TimeKit;

public class ContentLogic {
	
	private static final Logger logger = LogManager.getLogger();
	
	public static boolean saveData(int uid, String title, String tags, String content) {
		String sql = String.format("insert into post(uid, title, tags, content) values(%d, '%s', '%s', '%s')", 
				uid, title, tags, content);
		return DBConnector.update(sql);
	}
	
	/**
	 * 正式保存文章，需要讲flag置为1
	 * @param uid
	 * @param pid
	 * @param tags
	 * @return
	 */
	public static boolean save(int uid, int pid, String tags) {
		String sql = "update post set flag=1, tags=?, time=? where pid=? and uid=?";
		int ret = DBConnector.update(sql, tags, TimeKit.now(), pid, uid);
		if (ret == -1) return false;
		String tmp = new String("insert into pt(uid, pid, tag) values(?,?,?)");
		String[] tt = tags.split(",");
		for (String s : tt) {
			ret = DBConnector.update(tmp, uid, pid, s);
			if (ret == -1) return false;
		}
		return ret != -1;
	}
	
	/**
	 * 临时存储文章，flag置为0，等到客户端确认时再变成1即可
	 * @param uid  
	 * @param title 网页标题
	 * @param content 正文内容
	 * @param head 网页head
	 * @param plain 去掉标签的正文
	 * @return
	 */
	public static int tempSave(int uid, String title, String content, String head, String plain) {
		String sql = "insert into post(uid, title, content, head, plain, vector) values(?, ?, ?, ?, ?, ?)";
		return DBConnector.update(sql, uid, title, content, head, plain, PostKit.calculateVector(title, plain));
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
//			logger.debug(json.get("head"));
		} catch (JSONException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 获取uid分类为tag的所有文章
	 * @param uid 
	 * @param tag
	 * @return
	 */
	public static ArrayList<PostItem> getPostListByTag(int uid, String tag) {
		ArrayList<PostItem> ret = new ArrayList<PostItem>();
		String sql = "select title, post.pid, tags, plain, time from post inner join (select distinct pid from pt where uid=? and tag=?) as a1 on a1.pid=post.pid order by time desc";
		ResultSet rs = DBConnector.query(sql, uid, tag);
		try {
			while (rs.next()) {
				PostItem item = new PostItem(rs.getString(1), rs.getInt(2), 
						rs.getString(3), rs.getString(4), rs.getString(5));
				ret.add(item);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	
	/**
	 * 获取uid的全部文章
	 * @param uid
	 * @return
	 */
	public static ArrayList<PostItem> getAllPost(int uid) {
		ArrayList<PostItem> ret = new ArrayList<PostItem>();
		String sql = "select title, pid, tags, plain, time from post where uid=? and flag = 1 order by time desc";
		ResultSet rs = DBConnector.query(sql, uid);
		try {
			while (rs.next()) {
				PostItem item = new PostItem(rs.getString(1), rs.getInt(2), 
						rs.getString(3), rs.getString(4), rs.getString(5));
				ret.add(item);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	public static void main(String[] args) {
		ArrayList<PostItem> ret = getAllPost(2);
		for (PostItem item : ret) logger.debug(item.getTitle() + ": " + item.getPid() + "， " + item.getTags());
		
	}
}
