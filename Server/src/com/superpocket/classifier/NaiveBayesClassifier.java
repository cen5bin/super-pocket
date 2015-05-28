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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.superpocket.conf.FileConf;
import com.superpocket.kit.ClassKit;
import com.superpocket.kit.Log;
import com.superpocket.kit.PostKit;
import com.superpocket.kit.RegularKit;



public class NaiveBayesClassifier implements ClassifierInterface{
private static final long WORD_ID_OFFSET = 1000;
	
	private static boolean[][] wordsAt = null;
	private static double[] categoriesPro = null;
	private static double[][] wordsPro = null;
	private static int categoriesSum = 0;
	private static int wordsSum = 0;
	private static int docSum = 0;
	
	private static int[][] wordsNumPerWordPerCategory = null;
	private static int[] wordsNumPerCategory = null;
	private static int[] categoriesNum = null;
	private static int[] categoriesNumPerWord = null;
	
	private static final Map<Integer, Double> proCategory = new HashMap<Integer, Double>();
	private static final Map<Long, Double> proWordsInCategory = new HashMap<Long, Double>();
	
	public static int train(String trainFile, String modelFile, String type) {
		return NaiveBayesClassifier.train(trainFile, modelFile, type, 1);
	}
	
	public static int train(String trainFile, String modelFile, String type, double lambda) {
		int ret = 0;
		
		do {
			switch (type) {
				case "MD" :
					NaiveBayesClassifier.trainMD(trainFile, modelFile, lambda);
					break;
				case "TF-IDF" :
					NaiveBayesClassifier.trainTFIDF(trainFile, modelFile);
					break;
				default :
					ret = -1;
					break;	
			}
			if (-1 == ret) {
				Log.Log("ERROR", "illegal training type!");
				break;
			}
			
		} while(false);
		
		return ret;
	}
	
	private static int countFrequency(String trainFile) {
		int ret = 0;
		
		do {
			NaiveBayesClassifier.docSum = 0;
			
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(trainFile)));
				String line = null;
				
				line = br.readLine();
				NaiveBayesClassifier.categoriesSum = Integer.parseInt(line);
				Log.Log("INFO", "categories sum = " + NaiveBayesClassifier.categoriesSum);
				
				line = br.readLine();
				NaiveBayesClassifier.wordsSum = Integer.parseInt(line);
				Log.Log("INFO", "words sum = " + NaiveBayesClassifier.wordsSum);
				
				NaiveBayesClassifier.wordsNumPerWordPerCategory = new int[NaiveBayesClassifier.wordsSum][NaiveBayesClassifier.categoriesSum];
				NaiveBayesClassifier.wordsNumPerCategory = new int[NaiveBayesClassifier.categoriesSum];
				NaiveBayesClassifier.categoriesNum = new int[NaiveBayesClassifier.categoriesSum];
				NaiveBayesClassifier.wordsAt = new boolean[NaiveBayesClassifier.wordsSum][NaiveBayesClassifier.categoriesSum];
				NaiveBayesClassifier.categoriesNumPerWord = new int[NaiveBayesClassifier.wordsSum];
				
				for(line = br.readLine(); line != null; line = br.readLine()) {
					if (line.trim().equals("")) {
						continue;
					}
					++NaiveBayesClassifier.docSum;
					
					int categoryId, wordId;
					String[] subLine = line.split(" ");
					categoryId = Integer.parseInt(subLine[0]);
					NaiveBayesClassifier.wordsNumPerCategory[categoryId] += subLine.length - 1;
					NaiveBayesClassifier.categoriesNum[categoryId] += 1;
					
					for (int index = 1; index < subLine.length; ++index) {
						wordId = Integer.parseInt(subLine[index]);
						NaiveBayesClassifier.wordsNumPerWordPerCategory[wordId][categoryId] += 1;
						NaiveBayesClassifier.wordsAt[wordId][categoryId] = true;
					}
				}
				
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
				Log.Log("ERROR", "open train file meet error!");
				ret = -1;
				break;
			}
			
			for (int wordId = 0; wordId < NaiveBayesClassifier.wordsSum; ++wordId) {
				for (int categoryId = 0; categoryId < NaiveBayesClassifier.categoriesSum; ++categoryId) {
					if (NaiveBayesClassifier.wordsAt[wordId][categoryId]) {
						++NaiveBayesClassifier.categoriesNumPerWord[wordId];
					}
				}
			}
			
			for (int i = 0; i < NaiveBayesClassifier.categoriesSum; ++i) {
				Log.Log("INFO", "category id = " + i + ", num = " + NaiveBayesClassifier.categoriesNum[i]);
			}
			for (int i = 0; i < NaiveBayesClassifier.categoriesSum; ++i) {
				Log.Log("INFO", "category id = " + i + ", words sum = " + NaiveBayesClassifier.wordsNumPerCategory[i]);
			}
			for (int i = 0; i < NaiveBayesClassifier.categoriesSum; ++i) {
				for (int j = 0; j < NaiveBayesClassifier.wordsSum; ++j) {
					Log.Log("INFO", "word id = " + j + ", category id = " + i + ", num = " + NaiveBayesClassifier.wordsNumPerWordPerCategory[j][i]);
				}
			}
			for (int i = 0; i < NaiveBayesClassifier.wordsSum; ++i) {
				Log.Log("INFO", "word id = " + i + ", categories num = " + NaiveBayesClassifier.categoriesNumPerWord[i]);
			}
			
			// smooth
		} while (false);
		
		return ret;
	}
	
	private static int trainTFIDF(String trainFile, String modelFile) {
		int ret = 0;
		
		do {
			if (-1 == countFrequency(trainFile)) {
				Log.Log("ERROR", "count frequency meet error!");
			} else {
				Log.Log("INFO", "count frequency finished.");
			}
			
			try {
				FileWriter fw = new FileWriter(modelFile, false);
				
				fw.write(NaiveBayesClassifier.categoriesSum + "\n");
				for (int categoryId = 0; categoryId < NaiveBayesClassifier.categoriesSum; ++categoryId) {
					fw.write(categoryId + " 1\n");
					//fw.write(1.0 * NaiveBayesClassifier.categoriesNum[categoryId] / NaiveBayesClassifier.docSum + "\n");
				}
				
				fw.write(NaiveBayesClassifier.wordsSum + "\n");
				for (int wordId = 0; wordId < NaiveBayesClassifier.wordsSum; ++wordId) {
					for (int categoryId = 0; categoryId < NaiveBayesClassifier.categoriesSum; ++categoryId) {
						double tf = 1.0 * NaiveBayesClassifier.wordsNumPerWordPerCategory[wordId][categoryId] / NaiveBayesClassifier.wordsNumPerCategory[categoryId];
						double idf = Math.log(1.0 * NaiveBayesClassifier.categoriesSum / NaiveBayesClassifier.categoriesNumPerWord[wordId]);
						fw.write(wordId + " " + categoryId + " " + (tf * idf) + "\n");
					}
				}
				
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
				Log.Log("ERROR", "write model file meet error!");
				ret = -1;
				break;
			}
			Log.Log("INFO", "write model file finished.");
			
			if (-1 == NaiveBayesClassifier.loadModel(modelFile)) {
				Log.Log("ERROR", "load model file meet error!");
				ret = -1;
				break;
			} else {
				Log.Log("INFO", "load model file finished.");
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
				
				Log.Log("INFO", "count(category) = " + docNumPerCategory.size());
			} catch (Exception e) {
				e.printStackTrace();
				Log.Log("ERROR", "read train file meet error!");
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
				Log.Log("INFO", "doc num = " + docNum);
				for (Object key : docNumPerCategory.keySet()) {
					int categoryId = (Integer)key;
					double categoryPro = (Integer)docNumPerCategory.get(key) * 1.0 / docNum;
					fw.write(categoryId + " " + categoryPro + "\n");
					Log.Log("INFO", "category id = " + categoryId + ", doc num = " + (Integer)docNumPerCategory.get(key));
				}
				
				fw.write(words.size() + "\n");
				for (Object key : wordNumPerWordPerCategory.keySet()) {
					long wordPerCategoryId = (Long)key;
					int categoryId = (int)(wordPerCategoryId % WORD_ID_OFFSET);
					double wordPerCategoryPro = ((Integer)wordNumPerWordPerCategory.get(key) * 1.0) / ((Integer)wordNumPerCategory.get(categoryId));
					fw.write(wordPerCategoryId / WORD_ID_OFFSET + " " + categoryId + " " + wordPerCategoryPro + "\n");
					Log.Log("INFO", "word id = " + ((Long)key / WORD_ID_OFFSET) + ", category id = " + ((Long)key % WORD_ID_OFFSET) + ", num = " + (Integer)wordNumPerWordPerCategory.get(key) + ", num per category = " + (Integer)wordNumPerCategory.get(categoryId));
				}
				
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
				Log.Log("ERROR", "write model file meet error!");
				ret = -1;
				break;
			}
			
			if (-1 == NaiveBayesClassifier.loadModel(modelFile)) {
				Log.Log("ERROR", "load model meet error!");
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
				NaiveBayesClassifier.categoriesSum = Integer.parseInt(line);
				NaiveBayesClassifier.categoriesPro = new double[NaiveBayesClassifier.categoriesSum];
				for (int index = 0; index < NaiveBayesClassifier.categoriesSum; ++index) {
					line = br.readLine();
					String[] subLine = line.split(" ");
					int categoryId = Integer.parseInt(subLine[0]);
					double categoryPro = Double.parseDouble(subLine[1]);
					NaiveBayesClassifier.categoriesPro[categoryId] = categoryPro;
				}
				
				line = br.readLine();
				NaiveBayesClassifier.wordsSum = Integer.parseInt(line);
				NaiveBayesClassifier.wordsPro = new double[NaiveBayesClassifier.wordsSum][NaiveBayesClassifier.categoriesSum];
				Log.Log("INFO", "wordsSum = " + wordsSum + ", categorySum = " + categoriesSum);
				for (int index = 0; index < NaiveBayesClassifier.categoriesSum * NaiveBayesClassifier.wordsSum; ++index) {
					line = br.readLine();
					String[] subLine = line.split(" ");
					int wordId = Integer.parseInt(subLine[0]);
					int categoryId = Integer.parseInt(subLine[1]);
					double wordPro = Double.parseDouble(subLine[2]);
					NaiveBayesClassifier.wordsPro[wordId][categoryId] = wordPro;
					if (index % 10000 == 0) logger.debug(index / 10000);
				}
				
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
				Log.Log("ERROR", "read model file meet error!");
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
			Set<Integer> words = new HashSet<Integer>();
			for (int categoryId = 0; categoryId < NaiveBayesClassifier.categoriesSum; ++categoryId) {
				words.clear();
				double tmp = 0;
				tmp += Math.log(NaiveBayesClassifier.categoriesPro[categoryId]);
				for (int index = 0; index < document.size(); ++index) {
					if (document.get(index) < NaiveBayesClassifier.wordsSum) {
						if (!words.contains(document.get(index))) {
							tmp += NaiveBayesClassifier.wordsPro[document.get(index)][categoryId];
							words.add(document.get(index));
						}
					}
				}
				pro.put(categoryId, tmp);
			}
			Log.Log("INFO", "the size of proMap before sort = " + pro.size());
			
			/* sort */
			pro = sortPro(pro);
			if (null == pro) {
				Log.Log("ERROR", "sort pro meet error!");
				break;
			}
			Log.Log("INFO", "the size of proMap after sort = " + pro.size());
		} while(false);
		
		return pro;
	}
	
	public static Integer[] classify(ArrayList<Integer> document, int topK) {
		Vector<Integer> ret = new Vector<Integer>();
		
		do {
			Map<Integer, Double> pro = calculatePro(document);
			if (pro == null) {
				Log.Log("ERROR", "calculate pro meet error!");
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
				Log.Log("ERROR", "calculate pro meet error!");
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
				Log.Log("INFO", "diff rate = " + Math.abs((proMax - entry.getValue()) / proMax));
				if (threshold >= Math.abs((proMax - entry.getValue()) / proMax)) {
					ret.add(entry.getKey());
				}
			}
		} while (false);
		
		return ret;//(Integer[])ret.toArray(new Integer[ret.size()]);
	}
	
	private static Map<Integer, Double> sortPro(Map<Integer, Double> pro) {
		Map<Integer, Double> sortedPro = new LinkedHashMap<Integer, Double>();
		
		do {
			if (pro == null | pro.isEmpty()) {
				return null;
			}
			
			List<Map.Entry<Integer, Double>> entryList = new ArrayList<Map.Entry<Integer, Double>>(pro.entrySet());
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
		in.close();
		
		
		
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
		logger.debug(termIdList);
		int[] document;
		
		ArrayList<String> words = PostKit.getWords(title, content);
		ArrayList<Integer> regular = RegularKit.getClass(words);
		
		ArrayList<Integer> ret = NaiveBayesClassifier.classify(termIdList, 3, 0.1);
		if (regular.size() > 0) {
//			for (Integer id : regular) ret.add(id);
		}
		logger.debug(ret);
		ArrayList<String> res = new ArrayList<String>();
		
		for (Integer id : ret)
			res.add(ClassKit.getClass(id));
		
		logger.debug(res);
		
		
//		res.add("label");
		return res;
	}
	
	
}
