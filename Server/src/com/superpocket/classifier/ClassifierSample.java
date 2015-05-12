package com.superpocket.classifier;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClassifierSample implements ClassifierInterface{

	//实现接口方法，任何一个分类器必须实现这个方法
	@Override
	public ArrayList<String> classify(String title, String content) {
		// TODO Auto-generated method stub
		ArrayList<String> labels = new ArrayList<String>();
		labels.add("label1");
		labels.add("label2");
		return labels;
	}

	//以下部分为测试代码，需要自行编写
	private static Logger logger = LogManager.getLogger();
	public static void main(String[] args) {
		ClassifierInterface classifier = new ClassifierSample();
		ArrayList<String> labels = classifier.classify("test-title", "test-content");
		for (String label : labels) logger.debug(label);
	}

	
}
