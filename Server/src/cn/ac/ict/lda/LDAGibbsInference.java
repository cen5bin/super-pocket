package cn.ac.ict.lda;

import java.util.Random;
import java.io.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.ac.ict.time.TimeCounter;

public class LDAGibbsInference {
	private static Logger logger = LogManager.getLogger();
	
	public LDAGibbsInference(int doc[],int V){
		this.doc = doc;
	}
	
	public static double [][] loadProba(String filename){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"utf-8"));
			String line = br.readLine();
			if( line == null ) return null;
			String arr[] = line.split(" ");
			int V = Integer.parseInt(arr[0]);
			int K = Integer.parseInt(arr[1]);
			double proba[][] = new double[K][V];
			for(int w = 0; w < V && (line = br.readLine() ) != null; ++ w ){
				arr = line.split(" ");
				for( int k = 0; k < K; ++ k ){
					proba[k][w] = Double.parseDouble(arr[k]);
				}
			}
			
			br.close();
			return proba;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Configure the lda gibbs inference
	 * @param k Topic number
	 * @param burn_in The iteration number of burn-in process
	 * @param iteration The total iteration number
	 * @param sample_gap The gap of samples
	 */
	public void config(int k,int burn_in,int iteration,int sample_gap,int time_gap){
		this.BURN_IN = burn_in;
		this.ITERATION = iteration;
		this.SAMPLE_GAP = sample_gap;
		this.K = k;
		this.TIME_GAP = time_gap;
		nzd = new int[K];
		pzdsum = new double[K];
		
		numstat = 0;
	}
	
	protected void initSampleTopics(){
		topics = new int[doc.length];
		for( int i = 0; i < doc.length; ++ i){
			topics[i] = (int)(rand.nextDouble()*K);
		}
	}
	
	protected void doTopicCounting(){
		for( int i = 0; i < doc.length; ++ i ){
			nzd[ topics[i] ] ++;
		}
		this.cnd = doc.length;
	}
	
	protected int sampleFullConditional(int n){
		int z = topics[n];
		int w = doc[n];
		nzd[ z ] --;
		cnd --;
		double p[] = new double[K];
		for( int k = 0; k < K; ++ k ){
			p[k] = phi[ k ][ w ]*( nzd[ k ] + alpha )/( cnd + K*alpha );
		}
		
		
		for( int k = 1; k < K; ++ k ){
			p[k] += p[k-1];
		}
		
		double r = rand.nextDouble()*p[K-1];
		for( int k = 0; k < K-1; ++ k ){
			if( r < p[k] ){
				z = k;break;
			}
		}
		
		nzd[z] ++;
		cnd ++;
		return z;
	}
	
	public void addSampleStatistics(){
		
		for( int k = 0; k < K; ++ k ){
			pzdsum[k] += (nzd[k]+alpha)/(cnd + K*alpha);
		}
		numstat ++;
	}
	
	/**
	 * 
	 * @return a vector of p( z_k | doc)
	 */
	public double [] normalizeTheta(){
		double [] theta = new double[K];
		for( int k = 0; k < K; ++ k ){
			theta[k] = pzdsum[k]/numstat;
		}
		return theta;
	}
	
	
	/**
	 * Main method: 
	 * 1. Select initial state 
	 * 2. Repeat a large number of times: 
	 * 	(a). Select an element 
	 * 	(b)  Update conditional on other elements.
	 * If appropriate, output summary for each run. 
	 */
	public void gibbsSample(double alpha,double [][]phi){
		this.alpha = alpha;
		this.phi = phi;
		TimeCounter.updateTimeStamp(this);
		initSampleTopics();
		doTopicCounting();
		for( int i = 0; i < ITERATION; ++ i ){
			
			for( int n = 0; n < doc.length; ++ n){
				int topic = sampleFullConditional(n);
				topics[n] = topic;
			}
		
			if( i % TIME_GAP == 0 ){
				logger.info("Running Time:  " + TimeCounter.getTimeDiff(this));
			}
			if( ( i > this.BURN_IN ) && ( this.SAMPLE_GAP > 0 ) && ( i % this.SAMPLE_GAP  == 0)){
				addSampleStatistics();
			}
		}
		
		TimeCounter.removeTimeStamp(this);
	}
	protected double alpha;

	protected int BURN_IN;
	protected int ITERATION;
	protected int SAMPLE_GAP;
	
	/**
	 * topic number
	 * */
	protected int K;
	/**
	 * vocabulary size
	 */
	protected int V;
	
	/**
	 * topics[n] means the sample topic of n-th word in the document
	 */
	protected int topics[];
	
	/**
	 * nzd[z] times topic z was assigned to word in the document
	 */
	protected int nzd[];

	/**
	 * cnd: cumulative sum of nzd on topic, the total word count of doc
	 */
	protected int cnd;
	
	/**
	 * the sum probability of p(topic|doc) of all sample
	 */
	protected double pzdsum[];
	
	/**
	 * a matrix of K*V represents phi, phi[ topic ][ word ] = p(word|topic)
	 */
	protected double phi[][];
	
	protected int numstat;
	
	protected int doc[];
	
	protected int TIME_GAP = 10;
	protected Random rand = new Random();
}
