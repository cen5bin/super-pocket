package com.superpocket.classifier;


import java.io.BufferedReader;  
import java.io.FileInputStream;  
import java.io.FileWriter;
import java.io.InputStreamReader;  
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import jnr.ffi.Struct.int16_t;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.superpocket.kit.PostKit;



public class NaiveBayesClassifier implements ClassifierInterface{
	private static final long WORD_ID_OFFSET = 1000;
	
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
				
				fw.write(wordNumPerWordPerCategory.size() + "\n");
				for (Object key : wordNumPerWordPerCategory.keySet()) {
					long wordPerCategoryId = (Long)key;
					int categoryId = (int)(wordPerCategoryId % WORD_ID_OFFSET);
					double wordPerCategoryPro = ((Integer)wordNumPerWordPerCategory.get(key) * 1.0) / ((Integer)wordNumPerCategory.get(categoryId));
					fw.write(wordPerCategoryId + " " + wordPerCategoryPro + "\n");
				}
				
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("write model file meet error!");
				ret = -1;
				break;
			}
			
			if (-1 == NaiveBayesClassifier.loadModel(modelFile)) {
				logger.error("load model meet error!");
				ret = -1;
				break;
			}
		} while (false);
		
		return ret;
	} 
	
	public static int loadModel(String modelFile) {
		int ret = 0;
		
		do {
			NaiveBayesClassifier.proCategory.clear();
			NaiveBayesClassifier.proWordsInCategory.clear();
			
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(modelFile)));
				
				String line = br.readLine();
				int categoryNum = Integer.parseInt(line);
				for (int index = 0; index < categoryNum; ++index) {
					line = br.readLine();
					String[] subLine = line.split(" ");
					int categoryId = Integer.parseInt(subLine[0]);
					double categoryPro = Double.parseDouble(subLine[1]);
					NaiveBayesClassifier.proCategory.put(categoryId, categoryPro);
					
				}
				
				line = br.readLine();
				int wordCategoryNum = Integer.parseInt(line);
				for (int index = 0; index < wordCategoryNum; ++index) {
					line = br.readLine();
					String[] subLine = line.split(" ");
					long wordCategoryId = Long.parseLong(subLine[0]);
					double wordCategoryPro = Double.parseDouble(subLine[1]);
					NaiveBayesClassifier.proWordsInCategory.put(wordCategoryId, wordCategoryPro);
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
	
	public static int classify(ArrayList<Integer> document) {
		int ret = 0;
		
		do {
			double pro = Double.MAX_VALUE * -1.0;
			ret = -1;
			
			for (Integer categoryId : NaiveBayesClassifier.proCategory.keySet()) {
				double tmp = 0;
				tmp += Math.log(NaiveBayesClassifier.proCategory.get(categoryId));
				for (int index = 0; index < document.size(); ++index) {
					long wordCategoryId = document.get(index) * WORD_ID_OFFSET + categoryId;
					if (proWordsInCategory.containsKey(wordCategoryId)) {
						tmp += Math.log(NaiveBayesClassifier.proWordsInCategory.get(document.get(index) * WORD_ID_OFFSET + categoryId));
					}
				}
				logger.debug("category id = " + categoryId + ", pro = " + tmp);
				if (tmp > pro) {
					pro = tmp;
					ret = categoryId;
				}
			}
			
		} while (false);
		
		return ret;
	}
	
	public static void main(String[] args) {
		
		ClassifierInterface classifier = new NaiveBayesClassifier();
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
		if (-1 == NaiveBayesClassifier.loadModel("data/bayes/model.data")) {
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
		int ret = NaiveBayesClassifier.classify(termIdList);
		logger.debug(ret);
		return null;
	}
	
	
}
