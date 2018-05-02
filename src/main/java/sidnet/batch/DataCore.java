package sidnet.batch;

import java.util.Map;
import java.util.TreeMap;

import sidnet.batch.Constants.MATH;
import sidnet.batch.formatters.GnuplotBarChartFormatter;
import sidnet.batch.formatters.GnuplotXYChartFormatter;


public class DataCore {
	private Map<String, Map<String, Bucket>> dataTable;
	private Configuration config;
	private Map<String, Object> header;
	private long globalcontor = 0;
	
	public DataCore(Configuration config) {
		dataTable = new TreeMap<String, Map<String, Bucket>>(new NumericComparator());
		header = new TreeMap<String, Object>(new NumericComparator());
		this.config = config;		
	}
	
	public void append(DataBean inputDataBean)
	throws Exception {    	       
		Object partialRowKey = null;
		for(Pair row: inputDataBean.xyList) {
			
			Aggregator aggX = retrieveAggregator(
					row,					
					(config.scatterplot ? ("" + (++globalcontor)) : row.x.toString()),
					"",
					Constants.MATH.RAW,
					inputDataBean.groupInfo);			
			aggX.appendObject(row.x);
			
			Aggregator aggY = retrieveAggregator(
					row,
					(config.scatterplot ? ("" + (globalcontor)) : row.x.toString()),
					inputDataBean.catKey,
					config.mathOperation,
					inputDataBean.groupInfo);
			aggY.append(row.y);		

			String newCatKey = inputDataBean.catKey;
			if (config.catplot) {
				if (newCatKey.contains(row.x.toString() + "-"))
					newCatKey = newCatKey.replaceFirst((row.x.toString() + "-"), "");				
			}
			// get rid of trailing "-"
			if (newCatKey.length() > 0)
				newCatKey = newCatKey.substring(0, newCatKey.length()-1);
			if (newCatKey.length() == 0)
				newCatKey = "-";
			header.put(newCatKey, newCatKey);
		}        
     }
	
	@SuppressWarnings("unchecked")
	private Aggregator retrieveAggregator(Pair row, String rowKey, String catKey, MATH operation, Map<String, Object> groupInfo) {
		Map<String, Bucket> coreRow;
		Bucket bucket;
		
		if (dataTable.containsKey(rowKey))
			coreRow = dataTable.get(rowKey);
		else {
			coreRow = new TreeMap<String, Bucket>(new NumericComparator());
			dataTable.put(rowKey, coreRow);
		}
		
		if (coreRow.containsKey(catKey))
			bucket = coreRow.get(catKey);
		else {
			bucket = new Bucket(AggregatorBuilder.build(operation), catKey);
			coreRow.put(catKey, bucket);
		}
		
		return bucket.getAggregator();
	}
	
	public Object[][] getDataTable()
	throws Exception {
		Map<String, Map<String, Object>> outputData = getDataMap();
		if (config.barplot)
			return (new GnuplotBarChartFormatter()).convert(outputData, header, config);
		else
			return (new GnuplotXYChartFormatter()).convert(outputData, header, config);
	}
	
	private Map<String, Map<String, Object>> getDataMap() {	
		Map<String, Map<String, Object>> outputData;
		
		outputData = new TreeMap<String, Map<String, Object>>(new NumericComparator());
		
		// plug in data
		for (String rowKey: dataTable.keySet()) {
			Map<String, Bucket> dataRow = dataTable.get(rowKey);
			Map<String, Object> outputRow;

			outputRow = new TreeMap<String, Object>(new NumericComparator());
			for(String bucketKey : dataRow.keySet()) {
				Object entry = dataRow.get(bucketKey).getAggregator().getAggregate();
				outputRow.put(bucketKey, entry);
			}
			outputData.put(rowKey, outputRow);
		}
		return outputData;			
	}
}
