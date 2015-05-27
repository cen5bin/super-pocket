package com.superpocket.classifier;


import java.io.BufferedReader;  
import java.io.FileInputStream;  
import java.io.FileWriter;
import java.io.InputStreamReader;  
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

import jnr.ffi.Struct.int16_t;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.superpocket.conf.FileConf;
import com.superpocket.kit.ClassKit;
import com.superpocket.kit.PostKit;



public class NaiveBayesClassifier implements ClassifierInterface{
	private static final long WORD_ID_OFFSET = 1000;
	
	private static double[] categoriesPro = null;
	private static double[][] wordsPro = null;
	private static int categoriesNum = 0;
	private static int wordsNum = 0;
	
	private static final Map<Integer, Double> proCategory = new HashMap<Integer, Double>();
	private static final Map<Long, Double> proWordsInCategory = new HashMap<Long, Double>();
	
	public static int train(String trainFile, String modelFile, String type, double lambda) {
		int ret = 0;
		
		do {
			switch (type) {
				case "MD" :
					NaiveBayesClassifier.trainMD(trainFile, modelFile, lambda);
					break;
				default :
					ret = -1;
					break;	
			}
			if (-1 == ret) {
				logger.error("illegal training type!");
				break;
			}
			
			
		} while(false);
		
		return ret;
	}
	
	private static int trainMD(String trainFile, String modelFile, double lambda) {
		int ret = 0;
		
		do {
			int docNum = 0;
			
			Map<Long, Integer> wordNumPerWordPerCategory = new HashMap<Long, Integer>();
			Map<Integer, Integer> wordNumPerCategory = new HashMap<Integer, Integer>();
			Map<Integer, Integer> docNumPerCategory = new HashMap<Integer, Integer>();
			
			Set<Integer> words = new HashSet<Integer>();
			Set<Integer> categories = new HashSet<Integer>();
			
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(trainFile)));
				br.readLine();
				br.readLine();
				String line;
				for (line = br.readLine(); line != null; line = br.readLine()) {
					if (line.trim() == "") {
						continue;
					} else {
						String[] subLine = line.split(" ");
						if (0 == subLine.length) {
							continue;
						} else {
							int categoryId, wordId;
							
							++docNum;
							
							categoryId = Integer.parseInt(subLine[0]);
							if (!wordNumPerCategory.containsKey(categoryId)) {
								wordNumPerCategory.put(categoryId, subLine.length - 1);
							} else {
								wordNumPerCategory.put(categoryId, (Integer)(wordNumPerCategory.get(categoryId)) + subLine.length - 1);
							}
							if (!docNumPerCategory.containsKey(categoryId)) {
								docNumPerCategory.put(categoryId, 1);
							} else {
								docNumPerCategory.put(categoryId, (Integer)(docNumPerCategory.get(categoryId)) + 1);
							}
							
							categories.add(categoryId);
							
							for (int index = 1; index < subLine.length; ++index) {
								wordId = Integer.parseInt(subLine[index]);
								
								long key = wordId * WORD_ID_OFFSET + categoryId;
								if (!wordNumPerWordPerCategory.containsKey(key)) {
									wordNumPerWordPerCategory.put(key, 1);
								} else {
									wordNumPerWordPerCategory.put(key, (Integer)(wordNumPerWordPerCategory.get(key)) + 1);
								}
								
								words.add(wordId);
							}
						}
					}
				}
				br.close();
				
				logger.debug("count(category) = " + docNumPerCategory.size());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("read train file meet error!");
				ret = -1;
				break;
			}
			
			// Laplace Smoothing 
			for (Integer wordId : words) {
				for (Integer categoryId : categories) {
					Long wordCategoryId = wordId * WORD_ID_OFFSET + categoryId;
					if (wordNumPerWordPerCategory.containsKey(wordCategoryId)) {
						wordNumPerWordPerCategory.put(wordCategoryId, wordNumPerWordPerCategory.get(wordCategoryId) + 1);
					} else {
						wordNumPerWordPerCategory.put(wordCategoryId, 1);
					}
				}
			}
			for (Integer categoryId : categories) {
				wordNumPerCategory.put(categoryId, wordNumPerCategory.get(categoryId) + words.size());
			}
			
			try {
				FileWriter fw = new FileWriter(modelFile, false);
				
				fw.write(docNumPerCategory.size() + "\n");
				for (Object key : docNumPerCategory.keySet()) {
					int categoryId = (Integer)key;
					double categoryPro = (Integer)docNumPerCategory.get(key) * 1.0 / docNum;
					fw.write(categoryId + " " + categoryPro + "\n");
				}
				
				fw.write(words.size() + "\n");
				for (Object key : wordNumPerWordPerCategory.keySet()) {
					long wordPerCategoryId = (Long)key;
					int categoryId = (int)(wordPerCategoryId % WORD_ID_OFFSET);
					double wordPerCategoryPro = ((Integer)wordNumPerWordPerCategory.get(key) * 1.0) / ((Integer)wordNumPerCategory.get(categoryId));
					fw.write(wordPerCategoryId / WORD_ID_OFFSET + " " + categoryId + " " + wordPerCategoryPro + "\n");
				}
				
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("write model file meet error!");
				ret = -1;
				break;
			}
		} while (false);
		
		return ret;
	} 
	
	public static int loadModel(String modelFile) {
		int ret = 0;
		
		do {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(modelFile)));
				
				String line = br.readLine();
				NaiveBayesClassifier.categoriesNum = Integer.parseInt(line);
				NaiveBayesClassifier.categoriesPro = new double[NaiveBayesClassifier.categoriesNum];
				for (int index = 0; index < NaiveBayesClassifier.categoriesNum; ++index) {
					line = br.readLine();
					String[] subLine = line.split(" ");
					int categoryId = Integer.parseInt(subLine[0]);
					double categoryPro = Double.parseDouble(subLine[1]);
					NaiveBayesClassifier.categoriesPro[categoryId] = categoryPro;
				}
				
				line = br.readLine();
				NaiveBayesClassifier.wordsNum = Integer.parseInt(line);
				logger.debug(NaiveBayesClassifier.wordsNum);
				logger.debug(NaiveBayesClassifier.categoriesNum);
				NaiveBayesClassifier.wordsPro = new double[NaiveBayesClassifier.wordsNum][NaiveBayesClassifier.categoriesNum];
				for (int index = 0; index < NaiveBayesClassifier.categoriesNum * NaiveBayesClassifier.wordsNum; ++index) {
					line = br.readLine();
					String[] subLine = line.split(" ");
					int wordId = Integer.parseInt(subLine[0]);
					int categoryId = Integer.parseInt(subLine[1]);
					double wordPro = Double.parseDouble(subLine[2]);
					NaiveBayesClassifier.wordsPro[wordId][categoryId] = wordPro;
					//Log.Log("INFO", wordId + " " + categoryId + " " + wordPro);
				
					if (index % 10000 == 0) logger.debug(index / 10000);
				}
				
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("read model file meet error!");
				ret = -1;
				break;
			}
		} while (false);
		
		return ret;
	}
	
	private static Map<Integer, Double> calculatePro(ArrayList<Integer> document) {
		Map<Integer, Double> pro = new HashMap<Integer, Double>();
		
		do {
			/* calculate probability */
			for (int categoryId = 0; categoryId < NaiveBayesClassifier.categoriesNum; ++categoryId) {
				double tmp = 0;
				tmp += Math.log(NaiveBayesClassifier.categoriesPro[categoryId]);
				for (int index = 0; index < document.size(); ++index) {
					if (document.get(index) < NaiveBayesClassifier.wordsNum) {
						tmp += Math.log(NaiveBayesClassifier.wordsPro[document.get(index)][categoryId]);
					}
				}
				pro.put(categoryId, tmp);
			}
			logger.debug("the size of proMap before sort = " + pro.size());
			
			/* sort */
			pro = sortPro(pro);
			if (null == pro) {
				logger.debug("sort pro meet error!");
				break;
			}
			logger.debug("the size of proMap after sort = " + pro.size());
		} while(false);
		
		return pro;
	}
	
	public static Integer[] classify(ArrayList<Integer> document, int topK) {
		Vector<Integer> ret = new Vector<Integer>();
		
		do {
			Map<Integer, Double> pro = calculatePro(document);
			if (pro == null) {
				logger.error("calculate pro meet error!");
				ret = null;
				break;
			}
			
			int index = 0;
			for (Map.Entry<Integer, Double> entry : pro.entrySet()) {
				if (index++ == topK) {
					break;
				}
				ret.add(entry.getKey());
			}
		} while (false);
		
		return (Integer[])ret.toArray(new Integer[ret.size()]);
	}
	
	public static ArrayList<Integer> classify(ArrayList<Integer> document, int topK, double threshold) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		
		do {
			Map<Integer, Double> pro = calculatePro(document);
			if (pro == null) {
				logger.error("calculate pro meet error!");
				ret = null;
				break;
			}
			
			int index = 0;
			double proMax = 0;
			for (Map.Entry<Integer, Double> entry : pro.entrySet()) {
				if (index++ == topK) {
					break;
				}
				
				if (1 == index) {
					proMax = entry.getValue();
				}
				logger.debug("diff rate = " + Math.abs((proMax - entry.getValue()) / proMax));
				if (threshold >= Math.abs((proMax - entry.getValue()) / proMax)) {
					ret.add(entry.getKey());
				}
			}
		} while (false);
		
		return ret;
	}
	
	private static Map<Integer, Double> sortPro(Map<Integer, Double> pro) {
		Map<Integer, Double> sortedPro = new LinkedHashMap<Integer, Double>();
		
		do {
			if (pro == null | pro.isEmpty()) {
				return null;
			}
			
			List<Map.Entry<Integer, Double>> entryList = new ArrayList<Map.Entry<Integer, Double>>(pro.entrySet());
			logger.debug("the size of entryList = " + entryList.size());
			Collections.sort(entryList, new Comparator<Map.Entry<Integer, Double>>() {
				public int compare(Entry<Integer, Double> a, Entry<Integer, Double> b) {
					return b.getValue().compareTo(a.getValue());
				}
			});
			
			Iterator<Map.Entry<Integer, Double>> iter = entryList.iterator();
			Map.Entry<Integer, Double> entry = null;
			while (iter.hasNext()) {
				entry = iter.next();
				sortedPro.put(entry.getKey(), entry.getValue());
			}
		} while (false);
		
		return sortedPro;
	}
	
	public static void main(String[] args) {
//		logger.debug("asd".substring(0, 300));
		ClassifierInterface classifier = new NaiveBayesClassifier();
		logger.debug("main");
		Scanner in = new Scanner(System.in);
		StringBuilder sb = new StringBuilder();
		while (in.hasNext()) {
			String s = in.nextLine();
			if (s.equals("#END#")) {
				logger.debug("开始分类");
				classifier.classify("", sb.toString());
				sb = new StringBuilder();
			}
			else sb.append(s);
		}
		
		
		
//		new NaiveBayesClassifier().classify("", "詹姆斯邓肯科比布莱恩特");
		
		
//		Integer[] document = {0, 0, 0, 4, 5};
//		int category = NaiveBayesClassifier.classify(document);
//		if (-1 == category) { 
//			logger.error("Naive Bayes Classifier classify meet error!");
//		} else {
//			logger.debug("Naive Bayes Classifier classify = " + category);
//		}
	}

	private static final Logger logger = LogManager.getLogger();
	
	static {
		if (-1 == NaiveBayesClassifier.loadModel(FileConf.BAYES_MODE_PATH)) {
			logger.error("Naive Bayes Classifier train failed!");
		} else {
			logger.debug("Naive Bayes Classifier train finished.");
		}

	}
	
	
	@Override
	public ArrayList<String> classify(String title, String content) {
		// TODO Auto-generated method stub
		
		ArrayList<Integer> termIdList = PostKit.getTermIdList(title, content);
//		int[] document;
		ArrayList<Integer> ret = NaiveBayesClassifier.classify(termIdList, 3, 0.1);
		logger.debug(ret);
		ArrayList<String> res = new ArrayList<String>();
		
		for (Integer id : ret)
			res.add(ClassKit.getClass(id));
		
//		res.add("label");
		return res;
	}
	
	
}
