package com.superpocket.kit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.superpocket.conf.FileConf;

public class ClassKit {
	static HashMap<Integer, String> hash = new HashMap<Integer, String>();
	static HashMap<String, Integer> hash1 = new HashMap<String, Integer>();
	static {
		try {
			BufferedReader in = new BufferedReader(new FileReader(FileConf.CATEGORIES_ID_PATH));
			String s = null;
			while ((s = in.readLine()) != null) {
				if (s.split(" ").length != 2) continue;
				hash.put(Integer.parseInt(s.split(" ")[1]), s.split(" ")[0]);
				hash1.put(s.split(" ")[0], Integer.parseInt(s.split(" ")[1]));
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getClass(int id) {
		return hash.get(id);
	}
	
	public static int getId(String className) {
		return hash1.get(className);
	}
}
