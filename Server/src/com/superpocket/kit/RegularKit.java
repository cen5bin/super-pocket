package com.superpocket.kit;

import java.util.ArrayList;

public class RegularKit {
	
	
	private static int getClass1(String word) {
		if (word.equals("乒乓球") || word.equals("乒乓")) {
			return ClassKit.getId("乒乓球");
		}
		else if (word.equals("羽毛球")) {
			return ClassKit.getId("羽毛球");
		}
		return -1;
	}
	
	public static ArrayList<Integer> getClass(ArrayList<String> words) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (String word : words) {
			if (getClass1(word) != -1) ret.add(getClass1(word));
		}
		return ret;
	}
}
