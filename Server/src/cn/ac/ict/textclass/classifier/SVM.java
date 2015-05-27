package cn.ac.ict.textclass.classifier;





import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.ac.ict.textclass.kernel.Kernel;
import cn.ac.ict.textclass.kernel.TanhKernel;

public class SVM {
	private Logger logger =  LogManager.getLogger();
	private double alpha[];
	private double C;
	private double tol;
	private double x[][];
	private double kappa[][];
	private int y[];
	private double E[];
	private double b;//the threshold
	private boolean bound[];
	private Kernel kernel;
	private int ITER = 1000;
	private double min_value = 0.00000000001; 
	
	public SVM(Kernel kernel){
		this.kernel = kernel;
	}
	private void config(double C,double tol,int iter){
		this.C = C;
		this.tol = tol;
		this.ITER = iter;
	}
	
	private double function(int I){
		double res = 0;
		for( int i = 0; i < alpha.length; ++ i ){
			res += alpha[i]*y[i]*kappa[i][I];
		}
		res -= b;
		return res;
	}
	
	private double getE(int I){
		if( bound[I] ){//cached non-bound error
			E[I] = function(I) - y[I];
		}
		
		return E[I]; 
	}
	private void init(double [][]x ,int []y){
		this.x = x;
		this.y = y;
		int len = y.length;
		alpha = new double[len];
		E = new double[len];
		bound = new boolean[len];
		kappa = new double[len][len];
		b = 0;
		for( int i = 0; i < len; ++ i ){
			alpha[i] = 0;
			bound[i] = true;
			E[i] = 0;
		}
		
		for( int i = 0; i < len; ++ i ){
			for( int j = 0; j <= i; ++ j){
				kappa[i][j] = kernel.getKappa(x[i], x[j]);
				kappa[j][i] = kappa[i][j];
				System.out.print(kappa[i][j] + " ");
			}
			System.out.println();
		}
		
	}
	
	/**
	 * 
	 * @param i1
	 * @param i2
	 * @return
	 */
	private boolean takeStep(int i1,int i2){	
		if( i1 == i2 ){
			return false;
		}
		
		int s = y[i1]*y[i2];
		double E1 = getE(i1);
		double E2 = getE(i2);
		double L,H;//for alpha[i2]
		if( s != 1 ){
			L = Math.max(0, alpha[i2] - alpha[i1] );
			H = Math.min(C, C + alpha[i2] - alpha[i1]);
		}else{
			L = Math.max(0, alpha[i2] + alpha[i1] - C );
			H = Math.min(C, alpha[i2] + alpha[i1]);
		}
		if( Math.abs(L -H) < min_value ){
			logger.info( " L == H , return false");
			return false;
		}
		
		double k12 = kappa[i1][i2];
		double k11 = kappa[i1][i1];
		double k22 = kappa[i2][i2];
		double eta = 2*k12 - k11 - k22;
		double new_alpha_2 = 0;
		if( eta < 0 ){//assert eta <= 0
			new_alpha_2 = alpha[i2] - y[i2]*(E1-E2)/eta;
//			logger.info("new_alpha_2 = " + new_alpha_2 +", E1 = " +E1+", E2 = "+E2 +", L = " + L +", H = "+H);
			if( new_alpha_2 < L ) new_alpha_2 = L;
			else if( new_alpha_2 > H ) new_alpha_2 = H;
		}else{// eta == 0 in this case, which doesn't make positive progress.
			/*
			Under unusual circumstances, eta will not be negative. A zero eta can occur if more than one training
			example has the same input vector X. If eta==0, we need to evaluate the objective function at the two 
			endpoints, i.e. at L and H, and set a2(第二个乘子的新值) to be the one with larger objective function 
			value. The objective function is: obj=eta*a2^2/2 + (y2*(E1-E2)-eta*alph2)*a2+const
			*/
			double c1 = eta/2;
			double c2 = y[i2]*(E1-E2) - eta*alpha[i2];
			double Lobj = c1 *L*L + c2*L;// objective function at alpha_j = L
			double Hobj = c1 *H*H + c2*H;// objective function at alpha_j = H
			if( Lobj > Hobj + min_value ){
				new_alpha_2 = L;
			}else if( Lobj < Hobj - min_value ){
				new_alpha_2 = H;
			}else{
				new_alpha_2 = alpha[i2];
			}
			//return false;
		}
//		logger.info("na2 = " + new_alpha_2 );
		if( new_alpha_2 < min_value ){
			new_alpha_2 = 0;
		}else if( new_alpha_2 > C - min_value ){
			new_alpha_2 = C;
		}
		
		//doesn't make positive progress
		if( Math.abs(new_alpha_2 - alpha[i2] ) < min_value ){
			logger.info("doesn't make positive progress, return false");
			return false;
		}
		
		double new_alpha_1 = alpha[i1] + s*(alpha[i2] - new_alpha_2);
		if( 0 < new_alpha_1 && new_alpha_1<  C  ){
			bound[i1] = false;
		}else{
			bound[i1] = true;
		}
		if( 0 < new_alpha_2 && new_alpha_2<  C  ){
			bound[i2] = false;
		}else{
			bound[i2] = true;
		}
		/*update parameters*/ 
		double b1 = b + E1 + y[i1]*( new_alpha_1 - alpha[i1])*k11 + y[i2]*(new_alpha_2 - alpha[i2])*k12;
		double b2 = b + E2 + y[i1]*( new_alpha_1 - alpha[i1])*k12 + y[i2]*(new_alpha_2 - alpha[i2])*k22;
		double nb;
		if( !bound[i1] ){
			nb = b1;
		}else if( !bound[i2] ){
			nb = b2;
		}else{
			nb = (b1+b2)/2;
		}
		
		/*update all cache error for non-bound examples*/
		for( int k = 0; k < E.length; ++ k ){
			if( !bound[k]){
				E[k] = E[k] + y[i1]*(new_alpha_1 - alpha[i1])*kappa[k][i1] + y[i2]*(new_alpha_2 - alpha[i2])*kappa[k][i2] + b - nb;
			}
		}
		
		b = nb;
		alpha[i1] = new_alpha_1;
		alpha[i2] = new_alpha_2;
//		logger.info("a1 = " + alpha[i1] +", a2 = " + alpha[i2]);
		
		for( int k = 0; k < E.length; ++ k ){
				double tE = function(k) - y[k];
				System.out.print(tE +" ");
		}
		System.out.println();
		for( int i = 0; i < alpha.length; ++ i ){
			System.out.print( alpha[i] +" ");
		}
		System.out.println(b);
		for( int i = 0; i < bound.length; ++ i ){
			System.out.print( bound[i] +" ");
		}
		System.out.println();
		return true;
	}
	
	/**
	 * Examine whether example I violate KKT condition. If yes, update it. 
	 * @param I example I
	 * @return if update success return true, else false.
	 */
	private boolean examine(int I){
		double EI = getE(I);
		double r = EI*y[I];
		logger.info(" EI = "+EI+", r = "+r);
		/*
		 * KKT condition
		 * alpha == 0  <==> yi*f(xi) >= 1 <==> yi*Ei >= 0 
		 * 0< alpha <C <==> yi*f(xi) == 1 <==> yi*Ei == 0 ( support vector )
		 * alpha == C  <==> yi*f(xi) <= 1 <==> yi*Ei <= 0 
		 * 
		 * It obeys KKT, if r < 0 and alpha < C;
		 * Because alpha == C when r < 0 under KKT condition.
		 * Also it obeys KKT, if r > 0 and alpha > 0;
		 * Because alpha == 0 when r > 0 under KKT condition. 
		 */
		if( ( r < -tol && alpha[I] < C ) || ( r > tol && alpha[I] > 0 ) )//obey KKT condition
		{
			logger.info("sample " + I +" violates KKT.");
			double max = -1;
			int J = -1;
			/*Hierarchy 1: select alpha which maximizes |E1-E2| from non-bound alphas*/
			for( int j = 0; j < bound.length; ++ j ){//select 
				if( I == j || bound[j]) continue;
				double Ej = getE(j);
				if( Math.abs( EI - Ej ) > max ){
					max = Math.abs(EI - Ej);
					J = j;
				}
			}
			
			if( J >= 0){
				logger.info("Hierarchy 1: J = " + J );
				if(takeStep(J,I))return true;
			}
			/*Hirearchy 2: scan non-bound subset started at random location until it makes positive progress*/
			int rand = (int)Math.random()*bound.length;
			for( int i = 0 ; i < bound.length; ++ i){
				int j = (rand+i)%bound.length;
				if( I == j || bound[j] || j == J) continue;
				logger.info("Hierarchy 2: J = " + j );
				if( takeStep(j,I) ) return true;
			}
			/*Hierarchy 3: scan the entire train set( exclusive non-bound ) started at random location until it makes positive progress*/
			rand = (int)Math.random()*bound.length;
			for( int i = 0; i < y.length; ++ i ){
				int j = (rand+i)%bound.length;
				if( I == j || !bound[j]) continue;
				logger.info("Hierarchy 3: J = " + j );
				if( takeStep(j,I)) return true;
			}
		}
		
		return false;
	}
	
	public void train(double x[][],int y[]){
		if( x.length != y.length ){
			System.out.println( "The input vectors' length are not the same.");
		}
		
		init(x,y);

		int numChanged = 0;
		boolean examineAll = true;
		
		int iter = 0;
		/*
		while( iter ++ < ITER ){
			for( int i = 0; i < alpha.length; ++ i ){
				examine( i );
			}
		}*/
		while( iter++ < ITER && (numChanged>0 || examineAll )){
			numChanged = 0;
			if( examineAll ){
				//loop I over all training examples
				for( int i = 0; i < alpha.length; ++ i ){
					
					if( examine(i) )
						numChanged += 1;
				}
			}else{
				//loop I over examples where alpha is not 0 & not C, non-bound subset
				for( int i = 0; i < alpha.length; ++ i ){
					if( !bound[i] ){
						if( examine(i) )
							numChanged += 1;
					}
				}
			
			}
//			logger.info("numChanged = " + numChanged);
			if( examineAll ) examineAll = false;
			else if( numChanged == 0 ){
				examineAll = true;
			}
		}
		
		System.out.println( "iter " + iter );
	}
	
	public double predict(double x[]){
		double res = 0;
		for( int i = 0; i < x.length; ++ i ){
			if( alpha[i] < min_value )continue;
			res += y[i] * alpha[i]* kernel.getKappa(x, this.x[i]);
		}
//		System.out.println(b);
		res -= b;
		return res;
	}
	
	public void printParameters(){
		
	}
	public static void main(String args[]){
		int y[] = {-1,1,1,1,-1,1,-1};
		double x[][] = {
				{4,4,4,4},
				{1,1,1,1},
				{2,2,2,2},
				{3,3,3,3},
				{6,6,6,6},
				{0,0,0,0},
				{5,5,5,5}
		};
		
		double xt [][] ={
				{4,4,4,4},
				{1,1,1,1},
				{2,2,2,2},
				{3,3,3,3},
				{6,6,6,6},
				{0,0,0,0},
				{5,5,5,5}
		};
//		Kernel kernel = new LinearKernel();
//		Kernel kernel = new RBFKernel();
		TanhKernel kernel = new TanhKernel();
		kernel.config(0.01, 1);
		SVM svm = new SVM(kernel);
		svm.config(10, 0.001,100);
		svm.train(x, y);
		for( double t [] : xt ){
			System.out.println(""+ svm.predict(t) + " " + (svm.predict(t) > 0 ? 1 : - 1));
		}
	}
}
