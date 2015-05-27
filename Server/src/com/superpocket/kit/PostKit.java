package com.superpocket.kit;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
		for (String term : terms) ret.add(WordKit.getTermId(term));
		return ret;
	}
}
