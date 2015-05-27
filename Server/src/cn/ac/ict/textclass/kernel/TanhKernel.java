package cn.ac.ict.textclass.kernel;

public class TanhKernel implements Kernel{

	private double gamma = 0.1;
	private double r = 0.1;
	@Override
	public double getKappa(double[] x, double[] y) {
		double res = 0;
		for( int i = 0; i < x.length; ++ i  ){
			res += x[i] * y[i];
		}
		res = gamma*res + r;
		return Math.tanh(res);
	}

	public void config( double gamma,double r){
		this.gamma = gamma;
		this.r = r;
	}
}
