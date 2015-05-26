package com.superpocket.kit;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class IRDevideWord {
	public static ArrayList<String> work(String s) {
		ArrayList<String> ret = new ArrayList<String>();
		StringReader sr = new StringReader(s);
		IKSegmenter ik = new IKSegmenter(sr, true);
		Lexeme le  = null;
		try {
			while ((le = ik.next()) != null) ret.add(le.getLexemeText());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	private static final Logger logger = LogManager.getLogger();
	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		while (in.hasNext()) {
			String s1 = in.nextLine();
			ArrayList<String> ret = work(s1);
			for (String s : ret) logger.debug(s);
		}
		
	}
	
}
