package com.superpocket.kit;

import cn.ac.ict.textclass.sim.CosineSimilarity;
import cn.ac.ict.textclass.usage.RocchioLDAClassifier;
import cn.ac.ict.textclass.usage.SoftmaxLDAClassifier;

import com.superpocket.classifier.ClassifierInterface;
import com.superpocket.classifier.ClassifierSample;
import com.superpocket.classifier.CustomClassifier;
import com.superpocket.classifier.NaiveBayesClassifier;
import com.superpocket.logic.UserLogic;

public class SettingKit {
	private static ClassifierInterface[] classifiers = {new NaiveBayesClassifier(),
		new RocchioLDAClassifier(new CosineSimilarity()), new SoftmaxLDAClassifier(),
		new CustomClassifier()};
	
	/**
	 * 获取个性化分类器
	 * @return
	 */
	public static ClassifierInterface getCustomClassfier() {
		return classifiers[3];
	}
	
	
	/**
	 * 获取用户设置的分类器
	 * @param uid 
	 * @return
	 */
	public static ClassifierInterface getClassifier(int uid) {
//		return new ClassifierSample();
//		return new NaiveBayesClassifier();
		int method_id = UserLogic.getMethod(uid);
		return classifiers[method_id - 1];
		
//		return new RocchioLDAClassifier(new CosineSimilarity());
		
	}
}
