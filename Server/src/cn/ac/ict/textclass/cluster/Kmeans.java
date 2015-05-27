package cn.ac.ict.textclass.cluster;

import java.util.Random;

import cn.ac.ict.textclass.sim.CosineSimilarity;
import cn.ac.ict.textclass.sim.Similarity;

public class Kmeans {
	private Similarity similarity;
	public Kmeans(Similarity similarity){
		this.similarity = similarity;
	}
	
	/**
	 * 
	 * @param K The algorithm parameter K
	 * @param vecs Documents' vector representations.
	 * @return The class of each document.
	 */
	public int [] run(int K,double [][]vecs){
		if( vecs.length < K ){
			return null;
		}
		int ITER = 100;
		double [][] means = initCenter(K,vecs);
		int [] cluster = null;
		for( int i = 0; i < ITER; ++ i){
			cluster = getCluster(means,vecs);
			means = calcMeans(K,cluster,vecs);
		}
		
		return cluster;
	}
	
	/**
	 * get the class by finding out the nearest cluster center for each document.
	 * @param means the cluster centers.
	 * @param vecs documents' vector representations.
	 * @return The class of each document.
	 */
	private int[] getCluster(double [][]means, double [][]vecs){
		int cluster[] = new int[vecs.length];
		
		for( int i = 0; i < vecs.length; ++ i ){
			int max = 0;
			double score = 0;
			for( int k = 0; k < means.length ; ++ k ){
				double s = similarity.getSimilarity(vecs[i],means[k]);
				if( k == 0 ){
					max = 0;
					score = s;
				}else if( score < s ){
//					System.out.println(s + " ");
					max = k;
					score = s;
				}
				
			}
			cluster[i] = max;
//			System.out.print(max+" ");
		}
		
//		System.out.println();
		return cluster;
	}
	
	private double [][] calcMeans(int K,int []cluster,double [][]vecs){
		
		int L = vecs.length;
		if( K == 0 || K > L ) return null;
		
		int D = vecs[0].length;
		double [][]means = new double[K][D];
		int count[] = new int[K];
		for( int i = 0; i < L; ++ i ){
			int k = cluster[i];
			count[k] ++;
			for( int j = 0; j < D; ++ j ){
				means[k][j] += vecs[i][j]; 
			}
			
		}
		
		for( int k = 0; k < K; ++ k){
			for(int j = 0; j < D; ++ j ){
				means[k][j] /= count[k];
			}
		}
		return means;
	}
	
	private double [][] initCenter(int K, double [][] vecs){
		int L = vecs.length;
		System.out.println(K + " "+ L);
		if( K == 0 || K > L ) return null;
		int D = vecs[0].length;
		
		int []dice = new int[L];
		for( int i = 0; i < L; ++ i  )
			dice[i] = i;
		
		int centers[] = new int[K];
		for( int i = 0; i < K; ++ i ){
			int r = rand.nextInt(L-i);
			centers[i] = dice[r];
			dice[r] = dice[L-i-1];
			System.out.print( centers[i] +" ");
		}
		System.out.println();
		double [][] means = new double[K][D];
		for( int k = 0; k < K; ++ k ){
			
			for( int i = 0; i < D; ++ i ){
				means[k][i] = vecs[ centers[k] ][i];
			}
		}
		return means;
	}
	
	static Random rand = new Random();
	
	public static void main(String args[]){
		Similarity similarity = new CosineSimilarity();
		Kmeans kmeans = new Kmeans(similarity);
//		int T= 1000000;
//		int mcount = 0;
//		while( T -- > 0 ){
//			if( rand.nextInt(100) == 100 )
//				mcount ++;
//		}
//		System.out.println(mcount);
		double [][]vecs = {{1,2,3,4},{1,2,4,3},{2,4,3,4},{1,5,3,4},{1,2,3,4},{1,2,4,3},{2,4,3,4},{1,5,3,4}};
		for( double []vec: vecs ){
			double sum = 0;
			for( int i = 0; i < vec.length; ++ i ){
				sum += vec[i];
			}
			
			for( int i = 0; i < vec.length; ++ i ){
				vec[i] /= sum;
			}
		}
		kmeans.run(3, vecs);
	}
}
