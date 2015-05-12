package com.superpocket.classifier;

import java.util.ArrayList;


public interface ClassifierInterface {
	/**
	 * 接口方法，分类函数。所有分类器必须实现这个方法
	 * @param title
	 * @param content
	 * @return
	 */
	public ArrayList<String> classify(String title, String content);
}
