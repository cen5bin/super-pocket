package com.superpocket.kit;

import com.superpocket.classifier.ClassifierInterface;
import com.superpocket.classifier.ClassifierSample;
import com.superpocket.classifier.NaiveBayesClassifier;

public class SettingKit {
	public static ClassifierInterface getClassifier(int uid) {
//		return new ClassifierSample();
		return new NaiveBayesClassifier();
	}
}
