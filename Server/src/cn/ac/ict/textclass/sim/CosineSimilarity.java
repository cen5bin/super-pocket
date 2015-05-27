package cn.ac.ict.textclass.sim;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CosineSimilarity implements Similarity{
	private static Logger logger =  LogManager.getLogger();
	
	/**
	 * calculate the consine similarity of two vector
	 * @param x
	 * @param y
	 * @return
	 */
	public double getSimilarity(double []x,double []y){
		double res = 0;
		double sx = 0;
		double sy = 0;
		if( x.length != y.length ){
			logger.error("The length of input vector is not consistent.");
			return 0;
		}
		
		for( int i = 0; i < x.length; ++ i ){
			res += x[i]*y[i];
			sx += x[i]*x[i];
			sy += y[i]*y[i];
		}
		
		
		return res/(Math.sqrt(sx)*Math.sqrt(sy));
	}
	
}
