package sidnet.utilityviews.statscollector;

public abstract class AggregateStatEntry
extends StatEntry {
	protected double value = 0;
	
	public AggregateStatEntry(String key, String tag) {
		super(key, tag);
	}
	
	public void reset() {
		// to be implemented by superclass
	}
	
	public void appendValue(double value){
		this.value = value;
	}
}
