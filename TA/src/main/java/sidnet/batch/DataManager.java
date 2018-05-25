package sidnet.batch;

import java.util.Map;
import java.util.TreeMap;

public class DataManager {
	private Map<String, DataCore> dataCores;
	private Configuration config;
	
	public DataManager(Configuration config) {

		dataCores = new TreeMap<String, DataCore>();
		this.config = config;
	}
	
	public void append(DataBean inputDataBean) 
	throws Exception {
		if (config.scatterplot) // scatterplot produces multiple tables
			handleOver(removeTrailingDash(inputDataBean.catKey), inputDataBean);
		else 
			handleOver("", inputDataBean); // this produces a single table indexed by ""
	}
	
	private String removeTrailingDash(String str) {
		String newStr = str.toString();
		if (newStr.endsWith("-"))
			newStr = newStr.substring(0, str.length()-1);
		return newStr;		
	}
	
	private void handleOver(String tableTag, DataBean inputDataBean) 
	throws Exception {
		DataCore targetCore = dataCores.get(tableTag);
		if (targetCore == null) {
			targetCore = new DataCore(config);
			dataCores.put(tableTag, targetCore);
		}
		targetCore.append(inputDataBean);		
	}
	
	public Map<String, Object[][]> getProcessedDataTables() 
	throws Exception {
		Map<String, Object[][]> outputTables;
		
		outputTables = new TreeMap<String, Object[][]>();
			
		for (String tableTag: dataCores.keySet())
			outputTables.put(tableTag, dataCores.get(tableTag).getDataTable());
		
		return outputTables;			
	}
}
