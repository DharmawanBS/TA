
package sidnet.models.probabilistic;

import java.util.Random;

public class Gaussian {
	private double mean;
	private double stdev;

	private Random random;

	public Gaussian(double mean, double stdev) {
		this.mean = mean;
		this.stdev = stdev;
		
		random = new Random();
	}
	
	public Gaussian(double mean, double stdev, long seed){
		this.mean = mean;
		this.stdev = stdev;
		
		random = new Random(seed);
	}
	
	public double nextValue() {
		return mean + random.nextGaussian() * stdev;
	}
}
