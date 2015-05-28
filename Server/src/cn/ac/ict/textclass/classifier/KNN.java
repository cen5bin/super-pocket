package cn.ac.ict.textclass.classifier;

import java.util.ArrayList;
import java.util.List;

import cn.ac.ict.textclass.algorithm.TopKSelector;
import cn.ac.ict.textclass.sim.Similarity;

public class KNN {
	private Similarity similarity;
	private int K;
	private List<double[][]> class_vecs = null;
	public KNN(Similarity similarity){
		this.similarity = similarity;
	}
	
	/**
	 * config KNN
	 * @param K algorithm parameter K
	 * @param class_vecs A list where the element type is double[][].
	 * Each element (double [][] vecs) refers to a specific class 
	 * and each row of vecs represent a sample.
	 */
	public void config(int K,List<double[][]> class_vecs){
		this.K = K;
		this.class_vecs = class_vecs;
	}
	/**
	 * classify input document
	 * 
	 * @param doc_vec The vector representation of input document.
	 * @return the class of document ( A integer begin from 0 ).
	 */
	public int run(double[] doc_vec){
		List<Integer> type = new ArrayList<Integer>();
		List<Double> dist_list = new ArrayList<Double>();

		int tp = 0;
		for( double [][] vecs: class_vecs ){
			for( double[] vec: vecs){
				/** if use KL the reference vector should be the second parameter*/
				dist_list.add( similarity.getSimilarity(doc_vec, vec) );
				type.add(tp);
			}
			tp ++;
		}
		
		double [] dists = new double[dist_list.size()];
		int i = 0;
		for( double d: dist_list)
			dists[i++] = d;
		
		//select the top K nearest node.
		TopKSelector selector = new TopKSelector(K);
		int []top = selector.getTopK(dists);
		int C[] = new int[tp];
		for( int x : top){
			C[ type.get(x) ] ++;
		}
		//select the type with maximum C
		int max = 0;
		for( i = 1; i < C.length; ++ i )
		{
			if( C[max] < C[i]) max = i;
		}
		return max;
	}
}
