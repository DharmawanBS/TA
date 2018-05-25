package sidnet.batch;

public class AvgAggregator 
implements Aggregator {
	private double sum;
	private double count;
	public void append(double value) {
		sum += value;
		count++;		
	}
	
	public Object getAggregate() {
		return sum/count;
	}
	
	public void appendObject(Object object)
	throws Exception {
		throw new Exception("Not implemented");		
	}
}
