package com.superpocket.kit;

import java.util.ArrayList;
import java.util.Scanner;

import javax.script.*;

import org.python.core.Py;
import org.python.core.PyByteArray;
import org.python.core.PyFunction;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

public class JiebaKit {
	private static final String PATH_JYTHON_LIB = "/usr/local/jython/Lib";
	private static final String PATH_PYTHON_MODULE = "/usr/local/lib/python2.7/dist-packages/";
	private static final String PATH_PYTHON_MODULE_JIEBA = PATH_PYTHON_MODULE + "/jieba";
	private static final String PATH_EXE_JIEBA = "/home/wubincen/code/temp/Users/hugh_627/Desktop/super-pocket-master/Jieba/bin/Jieba.py";
	
	private static PythonInterpreter interpreter;
	private static PyFunction funcCut;
	private static PyFunction funcGetKeyWordsByTFIDF;
	private static PyFunction funcGetKeyWordsByTextRank;
	/*
	 * 初始化函数
	 */
	public int init() {
		int ret = 0;
		
		do {
			PySystemState sys = Py.getSystemState();
			sys.path.append(new PyString(this.PATH_JYTHON_LIB));
			sys.path.append(new PyString(this.PATH_PYTHON_MODULE));
			sys.path.append(new PyString(this.PATH_PYTHON_MODULE_JIEBA));
			
			this.interpreter = new PythonInterpreter();
			this.interpreter.execfile(this.PATH_EXE_JIEBA);
			this.funcCut = (PyFunction)this.interpreter.get("cut", PyFunction.class);
			this.funcGetKeyWordsByTFIDF = (PyFunction)this.interpreter.get("getKeyWordsByTFIDF", PyFunction.class);
			this.funcGetKeyWordsByTextRank = (PyFunction)this.interpreter.get("getKeyWordsByTextRank", PyFunction.class);
			
			this.cut("");
		} while (false);
		
		return ret;
	}
	
	/**
	 * 分词，用||划分
	 * @param sentence
	 * @return
	 */
	public static String divide(String sentence) {
		return jiebaKit.cut(sentence);
	}
	
	/**
	 * 分词，得到词数组
	 * @param content
	 * @return
	 */
	public static ArrayList<String> divide1(String content) {
		String[] words = divide(content).split("||");
		ArrayList<String> ret = new ArrayList<String>();
		for (String word : words) ret.add(word);
		return ret;
	}
	
	
	
	/*
	 * 分词函数
	 */
	public String cut(String sentence) {
		String ret = null;
		
		do {
			byte[] sentenceBytes;
			try {
				sentenceBytes = sentence.getBytes("utf-8");
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			PyByteArray sentencePyBytes = new PyByteArray(sentenceBytes);
			PyObject pyObj = this.funcCut.__call__(sentencePyBytes);
			ret = pyObj.toString();
		} while (false);
		
		return ret;
	}
	
	/*
	 * 抽取关键词 tf-idf
	 */
	public String getKeyWordsByTFIDF(String sentence, int topK) {
		String ret = null;
		
		do {
			byte[] sentenceBytes;
			try {
				sentenceBytes = sentence.getBytes("utf-8");
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			PyByteArray sentencePyBytes = new PyByteArray(sentenceBytes);
			PyInteger topKPyInteger = new PyInteger(topK);
			PyObject pyObj = this.funcGetKeyWordsByTFIDF.__call__(sentencePyBytes, topKPyInteger);
			ret = pyObj.toString();
		} while (false);
		
		return ret;
	}
	
	/*
	 * 抽取关键词 text-rank
	 */
	public String getKeyWordsByTextRank(String sentence, int topK) {
		String ret = null;
		
		do {
			byte[] sentenceBytes;
			try {
				sentenceBytes = sentence.getBytes("utf-8");
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			PyByteArray sentencePyBytes = new PyByteArray(sentenceBytes);
			PyInteger topKPyInteger = new PyInteger(topK);
			PyObject pyObj = this.funcGetKeyWordsByTextRank.__call__(sentencePyBytes, topKPyInteger);
			ret = pyObj.toString();
		} while (false);
		
		return ret;
	}
	
	static JiebaKit jiebaKit;
	
	static {
		jiebaKit = new JiebaKit();

		if (-1 == jiebaKit.init()) {
			System.out.println("[ERROR] jiebakit init meet error!");
		}
	}
	
	public static void main(String[] args) {
		
		Scanner in = new Scanner(System.in);
		while (in.hasNext()) {
			String s = in.nextLine();
			String words = jiebaKit.cut(s);
			if (null == words) {
				System.out.println("[ERROR] jiebakit cut meet error!");
				return;
			} else {
				System.out.println("[INFO] after cut : " + words);
			}
			
			String keyWords1 = jiebaKit.getKeyWordsByTFIDF(s, 5);
			if (null == keyWords1) {
				System.out.println("[ERROR] jiebakit cut meet error!");
				return;
			} else {
				System.out.println("[INFO] after getKeyWordsByTFIDF : " + keyWords1);
			}
			
			String keyWords2 = jiebaKit.getKeyWordsByTextRank(s, 5);
			if (null == keyWords2) {
				System.out.println("[ERROR] jiebakit cut meet error!");
				return;
			} else {
				System.out.println("[INFO] after getKeyWordsByTextRank : " + keyWords2);
			}
		}
		
		
	}

}
