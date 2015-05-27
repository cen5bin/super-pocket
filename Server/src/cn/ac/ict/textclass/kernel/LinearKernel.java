package cn.ac.ict.textclass.kernel;

public class LinearKernel implements Kernel{

	@Override
	public double getKappa(double[] x, double[] y) {
		double res = 0;
		for( int i = 0; i < x.length; ++ i ){
			res += x[i]*y[i];
		}
		return res;
	}

}
