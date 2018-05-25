package sidnet.batch;

public interface Aggregator {
	public void append(double value) throws Exception;
	public void appendObject(Object object) throws Exception;
	public Object getAggregate();
}
