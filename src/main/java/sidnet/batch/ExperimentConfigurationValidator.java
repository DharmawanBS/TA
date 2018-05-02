package sidnet.batch;

import java.util.List;
import java.util.Map;

public class ExperimentConfigurationValidator {
	private Map<String, String> configurationData;
	private List<String> optionalInlineExperimentsTags;
	
	public ExperimentConfigurationValidator(Map<String, String> configurationData) 
	throws InvalidConfiguration {
		checkMinimumRequirements(configurationData);
		this.configurationData = configurationData;
	}
	
	private void checkMinimumRequirements(Map<String, String> configurationData) 
	throws InvalidConfiguration {
		// Must contain a SIDNET_DRIVER_TAG
		if (!configurationData.containsKey(Constants.SIDNET_DRIVER_TAG))
			throw new InvalidConfiguration("Missing the required " + Constants.SIDNET_DRIVER_TAG + " = ... tag. Check file and/or spelling/casing");
	}
	
	public String getSIDnetDriver() {
		return configurationData.get(Constants.SIDNET_DRIVER_TAG);
	}
	
	public Map<String, String> getConfigurationData() {
		return configurationData;
	}
}
