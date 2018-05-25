package sidnet.batch.formatters;

import java.util.Map;

import sidnet.batch.Configuration;

public interface DataFormatter {
	public Object[][] convert(Map<String, Map<String, Object>> outputData,
							  Map<String, Object> originalHeader,
							  Configuration config) throws Exception;
}
