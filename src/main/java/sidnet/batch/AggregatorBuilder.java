package sidnet.batch;

import sidnet.batch.Constants.MATH;

public class AggregatorBuilder {
	public static Aggregator build(MATH math) {
		switch(math){
			case MIN: return new MinAggregator(); 
			case AVG: return new AvgAggregator(); 
			case MAX: return new MaxAggregator();
			case RAW: return new RawAggregator();
		}
		return null;
	}
}
