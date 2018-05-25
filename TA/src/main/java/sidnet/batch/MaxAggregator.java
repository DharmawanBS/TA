package sidnet.batch;

public class MaxAggregator
implements Aggregator {
	private double max = Double.NEGATIVE_INFINITY;
	
	public void append(double value) {
		if (max < value)
			max = value;
		
	}
	public Object getAggregate() {
		return max;
	}
	public void appendObject(Object object)
	throws Exception {
		throw new Exception("Not implemented");		
	}
}
