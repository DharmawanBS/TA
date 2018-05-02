package sidnet.batch.iterators;

import java.util.Iterator;
import java.util.Map;

public interface ExperimentConfigurationIterator 
extends Iterator {
	public void reset();
	public boolean hasNext();
	public Map<String, String> next();
	public int numberOfExperiments();
	public String[] getHeaders();
}
