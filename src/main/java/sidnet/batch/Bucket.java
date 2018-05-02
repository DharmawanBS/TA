package sidnet.batch;

public class Bucket {
	private Aggregator agg;
	private String catKey;
	
	public Bucket(Aggregator agg, String catKey) {
		this.agg = agg;
		this.catKey = catKey;
	}
	
	public Aggregator getAggregator() {
		return agg;
	}
}
