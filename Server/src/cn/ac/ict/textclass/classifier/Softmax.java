package cn.ac.ict.textclass.classifier;

public class Softmax {

	private double alpha;
	private double lambda = 2;
	private double [][]theta;// K*M, M is the length of feature vector
	private int K;
	private int M;
	private int ITER = 100;
	public void config(int K,int M,int iter,double alpha,double lambda){
		this.K = K  ;
		this.alpha = alpha;
		this.lambda = lambda;
		this.M = M;
		this.ITER = iter;
	}
	public double[][] getTheta(){
		return theta;
	}
	public void setTheta(double [][] theta){
		this.theta = theta;
	}
	public double[] calcProb(double []x){
		double p[] = new double[K];
		double sum = 1;
		for( int k = 0; k < theta.length; ++ k ){
			
			p[k] = 0;
			for( int i = 0; i < x.length; ++ i ){
				p[k] += theta[k][i]*x[i];
			}
			p[k] = Math.exp(p[k]);
			sum += p[k];
		}
		
		for( int k = 0; k < theta.length; ++ k ){
			p[k] /= sum;
//			System.out.print( p[k] + " ");
		}
//		System.out.println();
		return p;
	}
	
	public void stochostic(double [][]samples, int []y){
		int n = samples.length;
		for( int k = 0; k < K; ++ k ){
			for( int j = 0; j < theta[k].length; ++ j ){
				theta[k][j] -= alpha*lambda*theta[k][j];
			}
		}
		for( int i = 0; i < n; ++ i ){
			double []p = calcProb(samples[i]);
			for( int k = 0; k < K; ++ k ){
				for( int j = 0; j < samples[i].length; ++ j )
				{
					theta[k][j] += alpha* samples[i][j]*(( y[i] == k ? 1: 0) - p[k] )/n;
				}	
			}
		}
	}
	public void train(double [][]samples, int []y){
		theta = new double[K][M];
		for( int iter = 0; iter < ITER; ++ iter){
			stochostic( samples, y);
//			System.out.println("====================================");
//			for( int k = 0 ; k < K; ++ k){
//				for( int m = 0; m < M; ++ m ){
//					System.out.print(theta[k][m] + " ");
//				}
//				System.out.println();
//			}
//			System.out.println("====================================");
		}
	}
	
	public double [] predict(double x[]){
		return calcProb(x);
	}
	
	public static void main(String args[]){
		double x[][]={
				{1,47,76,24}, //include x0=1  
			    {1,46,77,23},  
			    {1,48,74,22},  
			    {1,34,76,21},  
			    {1,35,75,24},  
			    {1,34,77,25},  
			    {1,55,76,21},  
			    {1,56,74,22},  
			    {1,55,72,22},  
			};
		int y[]={0, 0, 0,1,1,1,2,2,2};
		Softmax softmax = new Softmax();
		softmax.config(2,4,10000, 0.01,2);
		softmax.train(x, y);
		
		for( double []pre2 : x){
			double p[] = softmax.predict(pre2);
			double sum = 0;
			for( double d :p ){
				sum += d;
				System.out.print(d +" ");
			}
			System.out.println(1 - sum);
		}
		
		double pre[] ={1,20, 80, 50};
		double p[] = softmax.predict(pre);
		double sum = 0;
		for( double d :p ){
			sum += d;
			System.out.print(d +" ");
		}
		System.out.println(1-sum);
	}
}
