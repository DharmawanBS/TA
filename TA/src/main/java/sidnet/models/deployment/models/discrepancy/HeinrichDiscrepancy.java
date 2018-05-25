/*
 * HeinrichDiscrepancy.java
 * 
 * Northwestern University @2009
 */

package sidnet.models.deployment.models.discrepancy;

import java.util.List;
import sidnet.core.misc.NCS_Location2D;

/**
 * Heinrich Algorithm for computing the discrepancy of a 2D point-set
 * 
 * @version 1.0
 * @date 10/01/2009
 * @author Oliviu C. Ghica
 */
public class HeinrichDiscrepancy {
	/**
	 * Calculates the discrepancy of a 2D point-set based on 
	 * the Heinrich method
	 * 
	 * @param A - list of 2D points (x, y) \in [0, 1)
	 * 
	 * @return - the computed discrepancy
	 */
	public static double calculateDiscrepancy(List<NCS_Location2D> A) {
		int d = 2; 		  // dimension
		int m = A.size(); 
		
		double[] v = new double[m];
		
		for (int i = 0; i < m; i++) {
			v[i] = 1.0/m;
		}
		
		double disc = Math.pow(3, -d);
		
		double sum1 = 0;
		for (int i = 0; i < m ; i++) {
			sum1 += v[i] * (1 - Math.pow(A.get(i).getX(), 2)) 
		 			     * (1 - Math.pow(A.get(i).getY(), 2));
		}		
		
		disc += - Math.pow(2, 1-d) * sum1;
		
		double sum2 = 0;
		for (int i = 0; i < m ; i++)
			for (int j = 0; j < m; j++) {
			   sum2 += v[i] * v[j] 
				            * ( 1 - Math.max(A.get(i).getX(), A.get(j).getX()))
							* ( 1 - Math.max(A.get(i).getY(), A.get(j).getY()));
			}
		
		disc += sum2;
		
		return Math.sqrt(disc);
	}
}
