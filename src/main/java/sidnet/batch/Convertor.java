package sidnet.batch;

import java.util.Map;

public class Convertor {
	public static Object[][] convert(Map<String, Map<String, Object>> input) 
	throws Exception {		
		
		if (input == null || input.size() == 0)
			return null;
		
		int rows = getRows(input);
		int cols = getCols(input);
		
		Object[][] output = new Object[rows][cols];
		int i = 0, j = 0;
		
		for (Map<String, Object> row: input.values()) {
			for(String key: row.keySet()) {				
				output[i][j] = row.get(key);
				j++;
			}
			j=0;
			i++;
		}
		return output;
	}
	
	private static int getCols(Map<String, Map<String, Object>> input) 
	throws Exception {
		int width = -1;
		
		for (Map<String, Object> row: input.values())
			if (width == -1)
				width = row.size();
			else 
				if (width != row.size())
					throw new Exception("[ExtractData.Convertor][ERROR] - data table contains row of different sizes.");
		
		if (width == -1)
			throw new Exception("[ExtractData.Convertor][ERROR] - Unable to determine size of the core data map");
		return width;
	}
	
	private static int getRows(Map<String, Map<String, Object>> input) {
		return input.size();
	}
}
