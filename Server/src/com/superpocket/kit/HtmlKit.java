package com.superpocket.kit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HtmlKit {
	private static final Logger logger = LogManager.getLogger();
	
	private static Pattern headPattern = Pattern.compile("(?<=<head.{0,100}>).+(?=</head>)", Pattern.DOTALL);
	/**
	 * 获取html的head部分
	 * @param s html文本
	 * @return
	 */
	public static String getHeader(String s) {
		Matcher ma = headPattern.matcher(s);
		if (ma.find())
		return ma.group();
		return "";
	}
	
	/**
	 * 去掉html标签
	 * @param htmlStr html文本
	 * @return
	 */
	public static String deleteHTMLTag(String htmlStr){ 
        String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式 
        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式 
        String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式 
         
        Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE); 
        Matcher m_script=p_script.matcher(htmlStr); 
        htmlStr=m_script.replaceAll(""); //过滤script标签 
         
        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE); 
        Matcher m_style=p_style.matcher(htmlStr); 
        htmlStr=m_style.replaceAll(""); //过滤style标签 
         
        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE); 
        Matcher m_html=p_html.matcher(htmlStr); 
        htmlStr=m_html.replaceAll(""); //过滤html标签 

        return htmlStr.trim(); //返回文本字符串 
    } 
	
	/**
	 * 使html紧凑
	 * @param s html文本
	 * @return
	 */
	public static String compactHtml(String s) {
		String ret = s.replaceAll("\r\n", " ");
		ret = ret.replaceAll("\n", " ");
		ret = ret.replaceAll("\t", " ");
		ret = ret.replaceAll(" {2,}", " ");
		return ret;
	}
	
	/**
	 * 获取html纯文本
	 * @param s html原始文本
	 * @param len 限制长度
	 * @return
	 */
	public static String getPlainHtml(String s, int len) {
		String ret = compactHtml(deleteHTMLTag(s));
		if (ret.length() > len) return ret.substring(0, len);
		return ret;
	}
	
	
	public static String getHtmlHeader(String htmlUrl) {
		try {
			URL url = new URL(htmlUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
			
			int res = connection.getResponseCode();
			logger.debug(res);
			if (res == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String string = "";
				StringBuilder sb = new StringBuilder();
				while ((string = in.readLine()) != null) sb.append(string);
				in.close();
				return HtmlKit.getHeader(sb.toString());
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public static void main(String[] args) {
		String html = "<head>asd<asd</head>asdada";
		logger.debug(getHeader("<head>asdasd</head>asdada"));
		logger.debug(deleteHTMLTag("<head>asdasd</head>asdada"));
//		logger.debug(deleteLabel(html));
		logger.debug(compactHtml("asda asdsasd asdd     asdadas"));
	}
}
