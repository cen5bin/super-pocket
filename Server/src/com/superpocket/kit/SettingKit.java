package com.superpocket.kit;

import com.superpocket.classifier.ClassifierInterface;
import com.superpocket.classifier.ClassifierSample;

public class SettingKit {
	public static ClassifierInterface getClassifier(int uid) {
		return new ClassifierSample();
	}
}
