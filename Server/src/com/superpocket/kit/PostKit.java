package com.superpocket.kit;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.superpocket.entity.PostVector;
import com.superpocket.logic.ContentLogic;

public class PostKit {
	private static final Logger logger = LogManager.getLogger();
	
	/**
	 * 获取文章的词项ID数组
	 * @param title 标题
	 * @param content 内容
	 * @return
	 */
	public static ArrayList<Integer> getTermIdList(String title, String content) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		ArrayList<String> terms = JiebaKit.divide1(content);
		for (String term : terms) {
			int id = WordKit.getTermId(term);
			if (id == -1) continue;
			ret.add(id);
		}
		return ret;
	}
	
	/**
	 * 计算文档向量，得到一个字符串，用于存数据库
	 * @param title
	 * @param content
	 * @return
	 */
	public static String calculateVector(String title, String content) {
		StringBuilder sb = new StringBuilder();
		ArrayList<Integer> termIdList = getTermIdList(title, content);
		HashMap<Integer, Double> vec = new HashMap<Integer, Double>();
		for (Integer termId : termIdList) {
			Double tfidf = vec.get(termId);
			if (tfidf == null) tfidf = Double.valueOf(0);
			tfidf += WordKit.getIdf(termId);
			vec.put(termId, tfidf);
		}
		
		double len = 0;
		for (Integer key : vec.keySet()) {
			len += vec.get(key) * vec.get(key);
		}
		len = Math.sqrt(len);
		
		int cnt = 0;
		for (Integer key : vec.keySet()) {
			if (cnt++ > 0) sb.append("#");
			sb.append(key + "," + (vec.get(key) / len));
		}
		return sb.toString();
	}
	
	private static HashMap<Integer, ArrayList<PostVector>> postVectorMap = new HashMap<Integer, ArrayList<PostVector>>();

	/**
	 * 获取uid的所有文档向量
	 * @param uid
	 * @return
	 */
	public static ArrayList<PostVector> getPostVectors(int uid) {
		ArrayList<PostVector> ret = postVectorMap.get(uid);
		if (ret != null) return ret;
		ret = ContentLogic.getPostVectors(uid);
		postVectorMap.put(uid, ret);
		return ret;
	}
	
	
}
