package sidnet.batch.iterators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CSVFileIterator 
implements ExperimentConfigurationIterator {

	private static final String EXPERIMENT_FILE_SUFIX     = ".csv",
								SEPARATOR   = ",";
	
	private File csvFile = null;
	private BufferedReader csvInput = null;
	private String currentParamLine = null;
	
	private String[] categories;
	
	public CSVFileIterator(String filename) 
	throws IOException, IncompatibleFileException {
		if (!filename.endsWith(EXPERIMENT_FILE_SUFIX))
			throw new IncompatibleFileException("Invalid file format: " + filename +
									   			"! Only accepting " + EXPERIMENT_FILE_SUFIX + "files");
		
		// open file
		csvFile = new File(filename);
		
		categories = extractCategories(csvFile);

		reset();
	}
	
	public void reset() {
		// TODO Auto-generated method stub
		try {
			csvInput =  new BufferedReader(new FileReader(csvFile));		
			csvInput.readLine(); // header;
			currentParamLine = csvInput.readLine();// first experiment
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}	
	
	public boolean hasNext() {
		return currentParamLine != null;
	}

	public Map<String, String> next() {
		if (!hasNext())
			return null;
		
		Map<String, String> returnable = getIndexedConfiguration(categories, currentParamLine);
		
		try {
			currentParamLine = csvInput.readLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return returnable;
	}

	public int numberOfExperiments() {
		int num = -1; // we don't count the header
		try {
		      //use buffering, reading one line at a time
		      //FileReader always assumes default encoding is OK!
		      BufferedReader input;
			try {
				input = new BufferedReader(new FileReader(csvFile));
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		      try {
		    	String line;
		        try {
					while (( line = input.readLine()) != null)
					  num++;
				} catch (IOException e) {					
					throw new RuntimeException(e);
				}
		      }
		      finally {
		        try {
					input.close();
				} catch (IOException e) {				
					throw new RuntimeException(e);
				}
		      }
		 } finally {
	     }
		 
		 return num;
	}

	public void remove() {
		// TODO Auto-generated method stub
		
	}
	
	private static Map<String, String> getIndexedConfiguration(String[] categories, String currentParamLine) {
		Map<String, String> keyvalue = new LinkedHashMap<String, String>(); // to maintain order
		String[] params = currentParamLine.split(SEPARATOR);
		
		if (categories.length != params.length)
			throw new RuntimeException("Number of parameters does not match the number of headers (columns) in CSV file");
		
		for (int i = 0; i < params.length; i++)
			params[i] = params[i].trim();		
		
		for (int i = 0; i < categories.length; i++)			
			keyvalue.put(categories[i], params[i]);
	
		return keyvalue;
	}
	
	private static String[] extractCategories(File csvFile) 
	throws IOException {
		String[] categories = null;
		try {
		      BufferedReader input =  new BufferedReader(new FileReader(csvFile));
		      try {
		    	String line = input.readLine(); // first line contains categories
		    	if (line == null)
		    		throw new RuntimeException("Empty CSV file!");
		    	categories = line.split(SEPARATOR);
		    	for (int i = 0; i < categories.length; i++)
		    		categories[i] = categories[i].trim();
		      }
		      finally {
		        input.close();
		      }
		 } finally {
	     }
		 
		 return categories;
	}

	public String[] getHeaders() {
		return categories;
	}	
}
