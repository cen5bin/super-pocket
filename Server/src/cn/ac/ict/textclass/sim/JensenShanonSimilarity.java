package cn.ac.ict.textclass.sim;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Jensen-Shanon divergence is defined as: JS(P||Q) = 1/2*KL(P||M) + 1/2*KL(Q||M), where M = 1/2( P + Q)
 * @author GuoTianyou
 * @email fortianyou@gmail.com
 * @version Create time: 2015��5��13�� ����11:50:03
 */
public class JensenShanonSimilarity implements Similarity{

	private Logger logger = LogManager.getLogger();
	/**
	 * @return negtive Jensen-Shanon divergence of p and q
	 */
	@Override
	public double getSimilarity(double[] p, double[] q) {
		if( p.length != q.length ){
			logger.error("The length of input vector is not consistent.");
			return 0;
		}
		
		double js = 0;
		double lambda = 0.5;
		for( int i = 0; i < p.length; ++ i ){
			double m = lambda * (q[i]+p[i]) ;
			js += lambda*Math.log(p[i]/ m)*p[i];
			js += lambda*Math.log(q[i]/m )*q[i];
		}
		
		return -js;
	}

}
