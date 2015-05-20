package com.superpocket.kit;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TimeKit {
	
	private static final Logger logger = LogManager.getLogger();
	
	/**
	 * 获取当前系统时间
	 * @return
	 */
	public static String now() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
	
	public static void main(String[] args) {
		logger.debug(now());
	}
}
