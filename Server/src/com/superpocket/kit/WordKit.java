package com.superpocket.kit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.superpocket.conf.FileConf;

public class WordKit {
	
	private static final Logger logger = LogManager.getLogger();
	
	static HashMap<String, Integer> vocabulary = new HashMap<String, Integer>();
	static double[] idf = null;
	
	
	static {
		try {
			BufferedReader in = new BufferedReader(new FileReader(FileConf.VOCABULARY_PATH));
			String line = null;
			while ((line = in.readLine()) != null) {
//				logger.debug(line);
				String[] ss = line.split(" ");
				if (ss[1].length() == 0) continue;
				if (ss.length != 2) vocabulary.put(" ", Integer.parseInt(ss[1]));
				else vocabulary.put(ss[0], Integer.parseInt(ss[1]));
			}
			in.close();
			
			idf = new double[vocabulary.size()];
			in = new BufferedReader(new FileReader(FileConf.IDF_PATH));
			while ((line = in.readLine()) != null) {
				String[] ss = line.split(" ");
				if (ss.length == 2)
				idf[Integer.parseInt(ss[0])] = Double.parseDouble(ss[1]);
			}
			in.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int getTermId(String term) {
		Integer ret = vocabulary.get(term);
		if (ret != null) return ret;
		return -1;
	}
	
	public static double getIdf(int termId) {
		return idf[termId];
	}
	
	
	public static void main(String[] args) {
		logger.debug(vocabulary.get(" "));
		logger.debug(vocabulary.get("劳动成果"));
		logger.debug(idf[2]);
	}
}
