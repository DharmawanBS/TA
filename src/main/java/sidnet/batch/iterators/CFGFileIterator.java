package sidnet.batch.iterators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CFGFileIterator 
implements ExperimentConfigurationIterator{

	private static final String EXPERIMENT_FILE_SUFIX     = ".cfg",
								CATEGORY_NAME_SEPARATOR   = "=",
								CATEGORY_PARAMS_SEPARATOR = ",";
	
	private Category[] configurationSpace; 
	private String[] headers;
	private int[] indexes;
	private boolean hasNext;
	
	public CFGFileIterator(String filename) 
	throws IOException, IncompatibleFileException {
		if (!filename.endsWith(EXPERIMENT_FILE_SUFIX))
			throw new IncompatibleFileException("Invalid file format: " + filename +
									   		    "! Only accepting " + EXPERIMENT_FILE_SUFIX + "files");
		
		// open file
		File cfgFile = new File(filename);
		
		configurationSpace = parse(cfgFile);
		
		headers = new String[configurationSpace.length];
		for (int i = 0; i < configurationSpace.length; i++)
			headers[i] = configurationSpace[i].getName();
		
		reset();
	}
	
	public void reset() {
		indexes = new int[configurationSpace.length]; // all zeros		
		
		hasNext = numberOfExperiments() > 0;
	}
	
	public int numberOfExperiments() {
		int numExp = 0;
		for (int i = 0; i < configurationSpace.length; i++)
			if (numExp == 0)
				numExp = configurationSpace[i].getParameters().size();
			else
				numExp *= configurationSpace[i].getParameters().size();
		
		return numExp;
	}
	
	public boolean hasNext() {
		return hasNext;
	}

	public Map<String, String> next() {	
		Map<String, String> cat = getIndexedConfiguration();
		
		if (hasNext())
			advanceIndex();
		
		return cat;
	}
	
	private void advanceIndex() {		
		for (int i = indexes.length - 1; i >= 0; i--) {
			if (indexes[i] < configurationSpace[i].getParameters().size() - 1) {
				indexes[i]++;
				return;
			}
			else
				if (i > 0)
					indexes[i] = 0;
				else
					hasNext = false;
		}
	}
	
	private Map<String, String> getIndexedConfiguration() {
		Map<String, String> keyvalue = new LinkedHashMap<String, String>(); // to maintain order
		for (int i = 0; i < indexes.length; i++)
			keyvalue.put(configurationSpace[i].getName(), configurationSpace[i].getParameters().get(indexes[i]));
	
		return keyvalue;
	}
	
	private static Category[] parse(File cfgFile)
	throws IOException {
		List<Category> configs = new LinkedList<Category>();
		try {
		      //use buffering, reading one line at a time
		      //FileReader always assumes default encoding is OK!
		      BufferedReader input =  new BufferedReader(new FileReader(cfgFile));
		      try {
		    	String line;
		        while (( line = input.readLine()) != null) {
		          Category config = parse(line);
		          if (config != null && 
		        	  config.getParameters().size() > 0)
		        	  configs.add(parse(line));
		        }
		      }
		      finally {
		        input.close();
		      }
		 } finally {
	     }
		 
		Category[] configArray = new Category[configs.size()];
		int index = 0;
		for (Category cat: configs)
			configArray[index++] = cat;
		
		return configArray;
	}

	private static Category parse(String configLine) {
		validate(configLine);
		
		configLine.trim(); // get rid of spaces
		
		if (configLine.length() == 0)
			return null; // empty space
		
		String[] tokens = configLine.split(CATEGORY_NAME_SEPARATOR);
		
		assert tokens.length == 2;
		
		String[] params =  tokens[1].split(CATEGORY_PARAMS_SEPARATOR);
		
		List<String> validParams = new LinkedList();
		for (int i = 0; i < params.length; i++) {
			params[i] = params[i].trim(); // get rid of spaces
			if (params[i] != null && params[i].length() > 0)
				validParams.add(params[i]);
		}
		
		return new Category(tokens[0].trim(), validParams);
	}
	
	private static void validate(String configLine) {
		// TODO;
	}

	public void remove() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Unimplemented method");
	}

	public String[] getHeaders() {
		return headers;
	}
}
