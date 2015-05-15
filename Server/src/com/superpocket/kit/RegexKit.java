package com.superpocket.kit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;

public class RegexKit {
	private static final Logger logger = LogManager.getLogger();
	
	private static Pattern headPattern = Pattern.compile("(?<=<head.{0,100}>).+(?=</head>)", Pattern.DOTALL);
	public static String getHeader(String s) {
		Matcher ma = headPattern.matcher(s);
		if (ma.find())
		return ma.group();
		return "";
	}
	
	public static void main(String[] args) {
		logger.debug(getHeader("<head>asdasd</head>asdada"));
	}
}
