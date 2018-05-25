package sidnet.utilityviews.statscollector;

public class StatEntry_GeneralPurposeRunningMeanAggregator
extends AggregateStatEntry {
	private static final String TAG = "RunningMean";
	protected double sum = 0;
	protected long count = 0;
	
	public StatEntry_GeneralPurposeRunningMeanAggregator(String key) {
		super(key, TAG);
	}

	public void appendValue(double value){
		sum += value;
		count++;
		this.value = sum/count;
	}
	
	public void reset() {
		// no reset;
	}
	
	public String getValueAsString() {
        return "" + value;
    }
}
