package cn.ac.ict.textclass.classifier;

import java.util.List;

import cn.ac.ict.textclass.sim.CosineSimilarity;
import cn.ac.ict.textclass.sim.Similarity;

/**
 * Nearest centroid classifier is known as the Rocchio classifier because of its similarity
 *  to the Rocchio algorithm for relevance feedback.
 * @author GuoTianyou
 * @email fortianyou@gmail.com
 * @version Create time: 2015年5月13日 下午7:55:07
 */
public class Rocchio {
	private Similarity similarity;
	public Rocchio(Similarity similarity){
		this.similarity = similarity;
	}
	/**
	 * 
	 * @param refer a list of documents' vectors of different classes
	 * @param vec the vector of specify document witch need to be classified.
	 */
	public int run(List<double [][]> refer,double vec[]){
		double proto[][] = new double[refer.size()][];
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
		int K = refer.size();
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
}
 