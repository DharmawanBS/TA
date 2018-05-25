package sidnet.batch;

public class RawAggregator
implements Aggregator {
	private Object value = null;
	public void append(double value) 
	throws Exception {
		this.value = value;		
	}
	public Object getAggregate() {
		return value;
	}
	public void appendObject(Object object) 
	throws Exception {
		if (this.value == null)
			this.value = object;
	}
}
