package sidnet.batch.formatters;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import sidnet.batch.Configuration;
import sidnet.batch.Convertor;
import sidnet.batch.NumericComparator;

public class GnuplotBarChartFormatter 
implements DataFormatter {
	
	static Logger logger;
	static {
		logger = Logger.getLogger("GnuplotBarChartFormatter");
		logger.setLevel(Level.INFO);
		logger.setAdditivity(false);
		logger.addAppender(new ConsoleAppender(new PatternLayout("\t%-5p %c *** %m \n\n")));
	}

	public Object[][] convert(Map<String, Map<String, Object>> outputData, Map<String, Object> originalHeader, Configuration config)
	throws Exception {		
		
		logger.info("\n*** Convert data according to Gnuplot Bar-Chart format *** ");
		
		//SortedMap<String, Object> header = buildHeader(outputData, originalHeader, config);
		List<String> primaryHeaderList = getHeaderTagList(originalHeader, 0);
		List<String> secondaryHeaderList = getHeaderTagList(originalHeader, 1);
		
		int rows = 1 + // main header
		           1 + // leading gap
						 primaryHeaderList.size() + // secondary headers   # B
						 primaryHeaderList.size() + // gaps                x 0 0 0
						 primaryHeaderList.size() * secondaryHeaderList.size() ; // actual data
		int cols = 1 + // column count
						 secondaryHeaderList.size(); // actual data
		
		Object[][] output = new Object[rows][cols];
		
		int i = 0;
		for (String key: outputData.keySet()) {
			logger.debug("outputData - column: " + (i++) + " keyset size = " + outputData.get(key).keySet().size());
			for (String key2: outputData.get(key).keySet())
				logger.debug("\toutputData - key: " + key2 + "");
		}
		
		Object[][] headerlessData = Convertor.convert(outputData);
		
		// put header
		output[0][0] = "#" + config.yAxisTagName + ":";
		int col = 1;
		for (String header: secondaryHeaderList)
			output[0][col++] = "(" + header + ")";
		
		int rowindex = 1;
		
		// write separator
		output[1][0] = rowindex++;
		col = 1;
		for (String header: secondaryHeaderList)
			output[1][col++] = "0";
		
		// put data block
		int row = 2;
		
		// The primary header is the one represented top-down (vertically) in .dat files
		for (String primaryHeader: primaryHeaderList) {
			
			logger.debug("primaryHeader = " + primaryHeader);
			
			// write primary header
			output[row][0] = "#(" + primaryHeader + ")";
			col = 1;
			for (String header: secondaryHeaderList)
				output[row][col++] = "-";
			
			List<String> originalHeaderList = new LinkedList<String>();
			i = 0;
			for (String key: originalHeader.keySet()) {
				logger.debug("originalHeader[" + (i++) +"] = " + key);
			}
			
			// write data	
			row++;
			int colIndicator = 1;
			// The secondary header is the one represented left-right (horizontally) in .dat files
			for (String secondaryHeader: secondaryHeaderList) {
				output[row][0] = rowindex++;
				for (col = 1; col <= secondaryHeaderList.size(); col++) {
					if (col == colIndicator) {
						String key = primaryHeader + "-" + secondaryHeader;
						// try to find the index of the seeked data in the original header
						int index = 1;
						for (Object headkey: originalHeader.keySet())
							if (((String)headkey).equals(key))
								break;
							else
								index++;
						Object value;
						try {
							value = headerlessData[0][index]; // it will have a single row due to -timeat
						} catch (Exception e) {
							logger.debug("out of bounds index = " + index + " for: row = " + row + " column = " + col);
							throw new RuntimeException("Unable to find data for: Primary Header (" + primaryHeader + "), Secondary Header (" + secondaryHeader + ")");
						}
						output[row][col] = value; // data is in the headerless data
						logger.debug("output[" + row + "][" + col + "] = " + value);
					}
					else
						output[row][col] = "0";
				}
				colIndicator++;	
				row++;
			}				
			
			// write separator
			output[row][0] = rowindex++;
			col = 1;
			for (String header: secondaryHeaderList)
				output[row][col++] = "0";
			row++;
		}
		
		return output;
	}
	
	private List<String> getHeaderTagList(Map<String, Object> originalHeader, int index) {
		List<String> list = new LinkedList<String>();
		TreeMap<String, String> map = new TreeMap(new NumericComparator());		
		
		for (String key: originalHeader.keySet()) {
			String[] tokens = key.split("-");
			if (!map.containsKey(tokens[index]))
				map.put(tokens[index], tokens[index]);
		}
		
		for (String key: map.keySet())
			list.add(key);
		return list;
	}	
}
