package sidnet.batch;

public class MinAggregator
implements Aggregator {
	private double min = Double.POSITIVE_INFINITY;
	
	public void append(double value) {
		if (min > value)
			min = value;
		
	}
	public Object getAggregate() {
		return min;
	}
	public void appendObject(Object object) 
	throws Exception {
		throw new Exception("Not implemented");			
	}
}
