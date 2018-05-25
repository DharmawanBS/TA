package sidnet.batch;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public class ExtractData {
	private static Configuration config;
	private static DataManager dataManager;
	public static Map<String, Object[][]> outputDataTables;
	
	public static void main(String[] cmdLineArgs) 
	throws Exception {
		  // Configure
		  config = new Configuration();	
		  CommandLineParser.parse(cmdLineArgs, config);		  
		  config.displayParams();
		  		  
 		  // retrieve the list of files that will be processed
	      List<File> fileList = FileUtil.buildInputLogFileList(new File(config.inputFilePath));	   
	      outputDataTables = processFiles(fileList);
		  
		  // Write results to media				  
		  ArrayWriter.write("" + config.inputFilePath + "\\", config.outputFileName, outputDataTables);
		  
	      System.out.println("\n[ExtractData] Completed succesfully!");
	}
	  
	private static Map<String, Object[][]> processFiles(List<File> fileList)
	throws Exception {
		// Init data core
		dataManager = new DataManager(config);
		
		for (File file: fileList)
			loadFromFileAndProcess(dataManager, file); 
		
		// at this point, data should be loaded and processed in the dataManager
		Map<String, Object[][]> dataTables = dataManager.getProcessedDataTables();
		return dataTables;
	}	
	
	private static void loadFromFileAndProcess(DataManager dataManager, File inputFile) 
	throws FaultyExperimentException, FileNotFoundException, Exception {
        System.out.println("\n[ExtractData] Processing file: " + inputFile);
        
        DataBean dataBean = DataLoader.load(inputFile, config);             
              
        if (!passesWhereClauseFilter(dataBean, config))
        	return;
        
        System.out.println("\t#<row>s: " + dataBean.xyList.size());
        
        final String catKey = dataBean.catKey;

        if (catKey != null)
            System.out.println("\tcatKey = " + catKey);
        
        dataManager.append(dataBean);                
	}	
	
	private static boolean passesWhereClauseFilter(DataBean dataBean, Configuration config) {
		for (String variable: config.whereClauses.keySet())
			if (!dataBean.tagsInfo.containsKey(variable) ||
				!dataBean.tagsInfo.get(variable).equals(config.whereClauses.get(variable)))
				return false;
		return true;
	}
}
