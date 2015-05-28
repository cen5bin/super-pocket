package cn.ac.ict.textclass.classifier;

import java.util.List;

import cn.ac.ict.textclass.algorithm.TopKSelector;
import cn.ac.ict.textclass.sim.CosineSimilarity;
import cn.ac.ict.textclass.sim.Similarity;

/**
 * Nearest centroid classifier is known as the Rocchio classifier because of its similarity
 *  to the Rocchio algorithm for relevance feedback.
 * @author GuoTianyou
 * @email fortianyou@gmail.com
 */
public class Rocchio {
	private Similarity similarity;
	private double proto[][] = null;
	private int K;
	public Rocchio(Similarity similarity){
		this.similarity = similarity;
	}
	
	public void setProto(double [][]proto){
		K = proto.length;
		this.proto = proto;
	}
	/**
	 * 
	 * @param refer a list of documents' vectors of different classes
	 * 
	 */
	public void train(List<double[][]> refer){
		K = refer.size();
		proto = new double[K][];
		int i = 0;
		/**get prototype**/
		for( double[][] v: refer){
			if( v.length == 0 ){
				proto[i++] = null;
				continue;
			}
			
			proto[i] = new double[v[0].length];
			for( int j = 0; j < v.length; ++ j ){
				//sum up all sample's vector one by one
				for( int k = 0; k < v[j].length; ++ k )
					proto[i][k] += v[j][k];
			}
			for( int k = 0; k < v[0].length; ++ k ){
				proto[i][k] /= v.length;//normalize
//				System.out.print(proto[i][k] + " ");
			}
//			System.out.println();
			i ++;
		}
		
	}
	
	/**
	 * @param vec the vector of specify document witch need to be classified.
	 * @param vec
	 * @return
	 */
	public int classify(double vec[]){
		
		int max = K;
		
		double score = 0;
		for( int k = 0; k < K; ++ k ){
			/**if use KL the reference should be the second parameter**/
			double s = similarity.getSimilarity(vec,proto[k]);
			if( k == 0 ){
				max = 0;
				score = s;
			}else if( score < s ){
				max = k;
				score = s;
			}
		}
		
		return max;
	}
	
	public int[] classify(double vec[],int topK){
		TopKSelector selector = new TopKSelector(topK);
		double sim[] = new double[K];
		for( int k = 0; k < K; ++ k ){
			/**if use KL the reference should be the second parameter**/
			sim[k] = similarity.getSimilarity(vec,proto[k]);
		}
		
		int[] topks = selector.getTopK(sim);
		return topks;
	}
	
}
 