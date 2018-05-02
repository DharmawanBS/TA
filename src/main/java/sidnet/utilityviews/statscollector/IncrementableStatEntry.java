package sidnet.utilityviews.statscollector;

public class IncrementableStatEntry
extends StatEntry {
	
	protected double value = 0;
	
	public IncrementableStatEntry(String key, String tag) {
		super(key, tag);
	}
	public void increment(double incrementAmount){
		value += incrementAmount;
	}
	@Override
	public String getValueAsString() {
		return "" + value;
	}
        
        //ditambah dharmawan start
        public double getValue() {
            return value;
        }
        //ditambah dharmawan end
}
