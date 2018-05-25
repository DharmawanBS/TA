package sidnet.batch.iterators;

import java.io.IOException;

public class ExperimentConfigurationIteratorBuilder {
	
	public static ExperimentConfigurationIterator build(String experimentFilename) 
	throws IOException {
		ExperimentConfigurationIterator iterator = null;
		
		if (iterator == null) {
			try{
				iterator = new CFGFileIterator(experimentFilename);
			} catch (IncompatibleFileException e){;};
		}
		
		if (iterator == null) {
			try{
				iterator = new CSVFileIterator(experimentFilename);
			} catch (IncompatibleFileException e){;};
		}
		
		return iterator;
	}
}
