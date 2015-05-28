package com.superpocket.classifier;

import java.util.ArrayList;
import java.util.HashMap;

import com.superpocket.entity.PostVector;
import com.superpocket.kit.ClassKit;
import com.superpocket.kit.PostKit;
import com.superpocket.kit.WordKit;

public class CustomClassifier implements ClassifierInterface{

	private static final int K = 10;
	
	private static ArrayList<String> KNN(PostVector v1, ArrayList<PostVector> vectors) {
		ArrayList<String> tags = new ArrayList<String>();
		ArrayList<Double> values = new ArrayList<Double>();
		for (PostVector v : vectors) {
			double similarity = PostVector.calSimilarity(v1, v);
			for (String tag : v.getTags()) {
				tags.add(tag);
				values.add(similarity);
			}
		}
		for (int i = 0; i < tags.size(); ++i)
			for (int j = i + 1; j < tags.size(); ++j)
				if (values.get(i) < values.get(j)) {
					String tmp = tags.get(i);
					tags.set(i, tags.get(j));
					tags.set(j, tmp);
					Double tt = values.get(i);
					values.set(i, values.get(j));
					values.set(j, tt);
				}
		
		HashMap<String, Integer> tmp = new HashMap<String, Integer>();
		for (int i = 0; i < K && i < tags.size(); ++i) {
			Integer ii = tmp.get(tags.get(i));
			if (ii == null) ii = 1;
			else ii++;
			tmp.put(tags.get(i), ii);
		}
		
		tags = new ArrayList<String>();
		ArrayList<Integer> values1 = new ArrayList<Integer>();
		for (String key : tmp.keySet()) {
			tags.add(key);
			values1.add(tmp.get(key));
		}
		
		for (int i = 0; i < tags.size(); ++i)
			for (int j = i + 1; j < tags.size(); ++j)
				if (values1.get(i) < values1.get(j)) {
					String tmp1 = tags.get(i);
					tags.set(i, tags.get(j));
					tags.set(j, tmp1);
					Integer tt = values1.get(i);
					values1.set(i, values1.get(j));
					values1.set(j, tt);
				}
		
		ArrayList<String> ret = new ArrayList<String>();
		for (int i = 0; i < 3 && i < tags.size(); ++i)
			ret.add(tags.get(i));
		return ret;
		
	}
	
	
	public static ArrayList<String> classify(int uid, String title, String content) {
		ArrayList<PostVector> vectors = PostKit.getPostVectors(uid);
//		ArrayList<Integer> termIds = PostKit.getTermIdList(title, content);
		ArrayList<String> ret = KNN(PostKit.calculatePostVector(title, content), vectors);
		return ret;
	}
	
	@Override
	public ArrayList<String> classify(String title, String content) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
