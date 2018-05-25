package sidnet.batch.formatters;

import java.util.Map;
import java.util.SortedMap;

import sidnet.batch.Configuration;
import sidnet.batch.Convertor;

public class GnuplotXYChartFormatter 
implements DataFormatter {

	public Object[][] convert(Map<String, Map<String, Object>> outputData, Map<String, Object> originalHeader, Configuration config)
	throws Exception {
		
		Map<String, Object> header = buildHeader(outputData, originalHeader, config);
		
		Object[][] headerlessData = Convertor.convert(outputData);
		
		Object[][] fullData = new Object[headerlessData.length + 1][headerlessData[0].length];
		
		Object[] headerArray = new Object[header.size()];
		
		int k = 0;
		for (String key: header.keySet())
			headerArray[k++] = header.get(key); 
		
		fullData[0] = headerArray;
		
		for (int i = 1; i < fullData.length; i++)
			for (int j = 0; j < fullData[0].length; j++)
				fullData[i][j] = headerlessData[i-1][j];
		
		return fullData;		
	}
	
	private Map<String, Object> buildHeader(Map<String, Map<String, Object>> headerlessData, Map<String, Object> originalHeader, Configuration config) {	
		for (String key: originalHeader.keySet()) {
			String tag = config.yAxisTagName;
			if (!key.equals("-")) {
				tag += "(" + originalHeader.get(key) + ")";
			} 
			originalHeader.put(key, tag);
		}
		
		originalHeader.put( "#" + config.xAxisTagName, "#" + config.xAxisTagName);
		
		return originalHeader;
	}
}
