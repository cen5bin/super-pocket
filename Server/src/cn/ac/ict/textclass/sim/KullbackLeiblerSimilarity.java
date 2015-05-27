package cn.ac.ict.textclass.sim;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * KL measures the expectation of log-difference of p and q,
 * where KL(p||q) = sum_i(ln(pi/qi)*pi). Note that KL measure isn't symmetric.
 * If you want symmetric one, you may try Jensen-Shannon divergence.
 * @author GuoTianyou
 * @email fortianyou@gmail.com
 * @version Create time: 2015��5��13�� ����11:48:27
 */
public class KullbackLeiblerSimilarity implements Similarity{
	private Logger logger = LogManager.getLogger();
	/**
	 * @param p is a probability distribution, that means the summation of each element of p is 1.
	 * @param q is a probability distribution, that means the summation of each element of q is 1.
	 * @return -KL as similarity
	 */
	@Override
	public double getSimilarity(double[] p, double[] q) {
		if( p.length != q.length ){
			logger.error("The length of input vector is not consistent.");
			return 0;
		}
		double kl = 0;
		for( int i = 0; i < p.length; ++ i ){
			kl += Math.log(p[i]/q[i])*p[i];
		}
		return -kl;
	}

}
