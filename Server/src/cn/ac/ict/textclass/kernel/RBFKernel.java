package cn.ac.ict.textclass.kernel;

public class RBFKernel implements Kernel{

	@Override
	public double getKappa(double[] x, double[] y) {
		double res = 0;
		for( int i = 0; i < x.length; ++ i ){
			x[i] -= y[i];
			res += x[i]*x[i];
		}

		return Math.exp(-res);
	}

	public static void main(String args[]){
		System.out.println( Math.exp(1));
	}
	
}
