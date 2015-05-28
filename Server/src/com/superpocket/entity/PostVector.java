package com.superpocket.entity;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PostVector {
	private ArrayList<Integer> termIdList = new ArrayList<Integer>();
	private ArrayList<Double> tfidf = new ArrayList<Double>();
	private ArrayList<String> tags = new ArrayList<String>();
	public ArrayList<Integer> getTermIdList() {
		return termIdList;
	}
	public void setTermIdList(ArrayList<Integer> termIdList) {
		this.termIdList = termIdList;
	}
	public ArrayList<Double> getTfidf() {
		return tfidf;
	}
	public void setTfidf(ArrayList<Double> tfidf) {
		this.tfidf = tfidf;
	}
	public ArrayList<String> getTags() {
		return tags;
	}
	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}
	public PostVector(ArrayList<Integer> termIdList, ArrayList<Double> tfidf,
			ArrayList<String> tags) {
		super();
		this.termIdList = termIdList;
		this.tfidf = tfidf;
		this.tags = tags;
	}
	
	
	private static final Logger logger = LogManager.getLogger();
	public static double calSimilarity(PostVector v1, PostVector v2) {
		HashMap<Integer, Double> tmp = new HashMap<Integer, Double>();
		ArrayList<Integer> ids = v1.getTermIdList();
		ArrayList<Double> tfidf = v1.getTfidf();
		logger.debug(ids.size());
		logger.debug(tfidf.size());
		for (int i = 0; i < ids.size(); ++i) tmp.put(ids.get(i), tfidf.get(i));
		ids = v2.getTermIdList();
		tfidf = v2.getTfidf();
		double ret = 0;
		for (int i = 0; i < ids.size(); ++i) {
			Double tt = tmp.get(ids.get(i));
			if (tt == null) continue;
			ret += tt * tfidf.get(i);
		}
		return ret;
	}
	
}
