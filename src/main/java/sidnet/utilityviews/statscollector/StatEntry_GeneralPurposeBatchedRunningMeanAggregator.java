package sidnet.utilityviews.statscollector;

public class StatEntry_GeneralPurposeBatchedRunningMeanAggregator 
extends StatEntry_GeneralPurposeRunningMeanAggregator {
		
	public StatEntry_GeneralPurposeBatchedRunningMeanAggregator(String key) {
		super(key);
	}
	
	public void reset() {
		sum = 0;
		count = 0;
		value = 0;
	}

	public void appendValue(double value){
		sum += value;
		count++;		
		this.value = sum/count;
	}
	
	public String getValueAsString() {
        return "" + value;
    }
}
