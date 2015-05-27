package cn.ac.ict.lda;
import java.io.*;
import java.util.Random;




import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.ac.ict.time.TimeCounter;


/**
 * LDAGibbsSampler implements a algorithm with complexity of O( iteration*K*corpus size)
 * where K is the topic number, corpus size is the total word number of the corpus.
 * @author Administrator
 */
public class LDAGibbsSampler {
	private static Logger logger = LogManager.getLogger();
	public LDAGibbsSampler(int [][]documents,int V ){
//		rand.setSeed(0);
		this.docs = documents;
		this.D = docs.length;
		this.V = V;
	}
	
	/**
	 * dump probability to file
	 * @param filename The dump file name.
	 * @param proba A K*V matrix denotes parameter pzw or pwz. 
	 * 	proba[k][w] = prob( w | zk) or proba[k][w] = prob(zk|w)
	 */
	public static void dumpProba(String filename, double [][]proba){
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(filename),"utf-8" ) );
			int K = proba.length;
			int V = 0;
			if( K > 0 ) V = proba[0].length;
			bw.append(V +" " + K );
			bw.newLine();
			for( int w = 0; w < V; ++ w ){
				bw.append(""+proba[0][w]);
				for( int k = 1; k < K; ++ k ){
					bw.append(" " + proba[k][w]);
				}
				bw.newLine();
			}
			
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Configure the lda gibbs sampler
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

		nzw = new int[K][V];
		nzd = new int[K][D];
		cnz = new int[K];
		cnd = new int[D];
		cnw = new int[V];
		
		pzdsum = new double[K][D];
		pwzsum = new double[V][K];
		pzwsum = new double[K][V];
		
		numstat = 0;
	}
	
	/**
	 * Sample a topic z_i from the full conditional distribution: p(z_i = j | 
     * z_-i, w) = (n_-i,j(w_i) + beta)/(n_-i,j(.) + W * beta) * (n_-i,j(d_i) + 
     * alpha)/(n_-i,.(d_i) + K * alpha) 
	 * @param m document
	 * @param n the n-th word in the document
	 * @return
	 */
	public int sampleFullConditional(int m,int n){
		//remove zi from the time count variables
		int z = topics[m][n];
		int w = docs[m][n];
		nzw[z][w] -- ;
		nzd[z][m] -- ;
		cnz[z] --;
		cnd[m] --;
		
		// do multinomial sampling via cumulative method:
		double [] p = new double[K];
		for( int k = 0; k < K; ++ k ){
			p[k] = (( nzw[k][w] + beta)/( cnz[k] + V*beta ))*((nzd[k][m] + alpha )/( cnd[m] + K*alpha)); 
		}
		
		for( int k = 1 ; k < K; ++ k ){
			p[k] += p[k-1];//cdf
		}
//		double r = Math.random()*p[K-1];
		double r = rand.nextDouble()*p[K-1];
		for( z = 0; z < K - 1; ++ z ){
			if( r < p[z] ){
				break;
			}
		}
		//add new topic z_i to count variables
		nzw[z][w] ++;
		nzd[z][m] ++;
		cnz[z] ++;
		cnd[m] ++;
		
		return z;
	}
	
	/**
	 * Main method: 
	 * 1. Select initial state 
	 * 2. Repeat a large number of times: 
	 * 	(a). Select an element 
	 * 	(b)  Update conditional on other elements.
	 * If appropriate, output summary for each run. 
	 */
	public void gibbsSample(double alpha,double beta){
		this.alpha = alpha;
		this.beta = beta;
		TimeCounter.updateTimeStamp(this);
		initSampleTopics();
		doTopicCounting();
		for( int i = 0; i < ITERATION; ++ i ){
			
			for( int m = 0; m < D; ++ m ){
				for( int n = 0; n < docs[m].length; ++ n){
					int topic = sampleFullConditional(m,n);
					topics[m][n] = topic;
				}
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
	
	/**
	 * When a new sample is sampled add it to statistics variables.
	 */
	public void addSampleStatistics(){
		for( int k = 0; k < K; ++ k ){
			for( int m = 0; m < D; ++ m ){
				pzdsum[k][m] += (nzd[k][m] + alpha )/( cnd[m] + K*alpha);
			}
		}
		
		for( int w = 0; w < V; ++ w){
			for( int k = 0; k < K; ++ k){
				pwzsum[w][k] += ( nzw[k][w] + beta)/( cnz[k] + V*beta );
			}
		}
		
		for( int w = 0; w < V; ++ w){
			for( int k = 0; k < K; ++ k){
				/* p(z|w) = ( n(z,w) + beta )/( n(w) + K*beta )*/
				pzwsum[k][w] += ( nzw[k][w] + beta )/( cnw[w] + K*beta );
			}
		}
		numstat ++;
	}
	
	/**
	 * 
	 * @return a matrix of K*D represents theta
	 */
	public double [][] normalizeTheta(){
		double [][] theta = new double[K][D];
		for( int k = 0; k < K; ++ k ){
			for(int m = 0; m < D; ++ m ){
				theta[k][m] = pzdsum[k][m]/numstat;
			}
		}
		return theta;
	}
	
	/**
	 * normalize the probability of p(word|topic)
	 * @return a matrix of K*V represents phi, phi[ topic ][ word ] = p(word|topic)
	 */
	public double[][] normalizePhi(){
		double [][] phi = new double[K][V];
		for( int k = 0; k < K; ++ k ){
			for(int w = 0; w < V; ++ w ){
				phi[k][w] = pwzsum[w][k]/numstat;
			}
		}
		return phi;
	}
	
	/**
	 * normalize the probability of p(topic|word)
	 * @return a matrix of K*V represents pzw, pzw[ topic ][ word ] = p(topic|word)
	 */
	public double[][] normalizePZW(){
		double [][] pzw = new double[K][V];
		for( int k = 0; k < K; ++ k ){
			for(int w = 0; w < V; ++ w ){
				pzw[k][w] = pzwsum[k][w]/numstat;
			}
		}
		return pzw;
	}
	
	/**
	 * count the number of each counting variable.
	 */
	public void doTopicCounting(){
		for( int i = 0; i < D; ++ i ){
			for( int j = 0; j < docs[i].length; ++ j ){
				//w is the word id
				int w = docs[i][j];
				nzw[topics[i][j]][w] ++;
				nzd[topics[i][j]][i] ++;
				//count word frequent
				cnw[w]++;
			}
		}
		
		for( int k = 0; k < K; ++ k ){
			cnz[k] = 0;
			/*cumulative sum of nzw[k][w] on w*/
			for( int w = 0; w < V; ++ w ){
				cnz[k] += nzw[k][w];
			}
//			System.out.println("cummulative nzw["+k+"][*] = " + cnz[k]);
		}
		
		for( int m = 0; m < D; ++ m ){
			/*cumulative sum of nzd[k][m] on k*/
			cnd[m] = docs[m].length;
		}
		
	}
	
	/**
	 * random assign topic to each word using multinomial
	 */
	public void initSampleTopics(){
		
		topics = new int[D][];
		for( int i = 0; i < D; ++ i ){
			topics[i] = new int[docs[i].length];
			for( int j = 0; j < docs[i].length; ++ j ){
				/** initially multinomial distribution for all topic*/
				topics[i][j] = (int)(rand.nextDouble()*K);
//				topics[i][j] = (int)(Math.random()*K);
			}
		}
		
	}
	public static void main(String args[]){
		// words in documents  
        int[][] documents = { {1, 4, 3, 2, 3, 1, 4, 3, 2, 3, 1, 4, 3, 2, 3, 6},  
            {2, 2, 4, 2, 4, 2, 2, 2, 2, 4, 2, 2},  
            {1, 6, 5, 6, 0, 1, 6, 5, 6, 0, 1, 6, 5, 6, 0, 0},  
            {5, 6, 6, 2, 3, 3, 6, 5, 6, 2, 2, 6, 5, 6, 6, 6, 0},  
            {2, 2, 4, 4, 4, 4, 1, 5, 5, 5, 5, 5, 5, 1, 1, 1, 1, 0},  
            {5, 4, 2, 3, 4, 5, 6, 6, 5, 4, 3, 2}};  
        int M = documents.length;
        int K = 2;
        int V = 7;
        LDAGibbsSampler lda = new LDAGibbsSampler(documents,V);
        lda.config(K,2000,10000,10,1000);
        
        lda.gibbsSample(2, .5);
        //���ģ�Ͳ�����������ʽ ��81���루82��  
        double[][] theta = lda.normalizeTheta();
        double[][] phi = lda.normalizePhi();  
        System.out.println("theta: ");
        for( int i = 0; i < M; ++ i ){
        	for( int k = 0; k < K; ++ k ){
        		System.out.print(theta[k][i] + " ");
        	}
        	System.out.println();
        }
        
        
        System.out.println("phi: " );
        for( int k = 0; k < K; ++ k ){
        	for( int j = 0 ;  j < V; ++ j ){
        		System.out.print(phi[j][k] + " ");
        	}
        	System.out.println();
        }
	}
	
	
	/**
	 * topic number
	 * */
	protected int K;
	/**
	 * vocabulary size
	 */
	protected int V;
	/**
	 * document number
	 */
	protected int D;
	
	protected double alpha = 0;
	protected double beta = 0;
	protected int BURN_IN = 2000;
	protected int ITERATION = 10000;
	protected int SAMPLE_GAP = 10;
	/**
	 * topics[m][n] means the sample topic of n-th word in document m
	 */
	protected int topics[][];
	/**
	 * nzw[z][w] times topic z was assigned to word w
	 */
	protected int nzw[][];
	/**
	 * nzd[z][m] times topic z was assigned to word in document m 
	 */
	protected int nzd[][];
	/**
	 * cnz[]: cumulative sum of nzw on word, the total count of topic z
	 */
	protected int cnz[];
	/**
	 * cnd[m]: cumulative sum of nzd on topic, the total word count of doc
	 */
	protected int cnd[];
	/**
	 * cnw count of each word in corpus
	 */
	protected int cnw[];

	
	/**
	 * the sum probability of p(topic|doc) of all sample
	 */
	protected double pzdsum[][];
	/**
	 * the sum probability of p(word|topic) of all sample
	 */
	protected double pwzsum[][];
	/**
	 * the sum probability of p(topic|word) of all sample
	 */
	protected double pzwsum[][];
	
	protected int numstat;
	
	protected int docs[][];

	protected int TIME_GAP = 10;
	protected Random rand = new Random();
}
