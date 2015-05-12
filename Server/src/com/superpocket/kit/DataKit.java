package com.superpocket.kit;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataKit {
	/**
	 * 获取客户端传来的json
	 * @param reader
	 * @return
	 */
	private static final Logger logger = LogManager.getLogger();
	public static String getJsonData(BufferedReader reader) {
		StringBuilder sb = new StringBuilder();
		try {
			String s = null;
			while ((s = reader.readLine()) != null) {
				sb = sb.append(s);
			}
			logger.debug(sb);
			return sb.toString();//new String(sb.toString().getBytes("ISO8859_1"), "utf-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
