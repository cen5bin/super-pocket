package com.superpocket.entity;

import java.util.ArrayList;

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
	
	
	
}
