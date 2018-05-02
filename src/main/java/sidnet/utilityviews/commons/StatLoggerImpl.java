package sidnet.utilityviews.commons;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import sidnet.utilityviews.statscollector.ExperimentData;

public class StatLoggerImpl 
implements StatLogger {

	public static final String HEADER_TAG = " <header> ";
	public static final String ROW_TAG = " <row> ";
	public static final String RUN_ID_TAG = "<runId>";
	public static final String EXPERIMENT_ID_TAG = "<experimentId>";
	
	private Logger logger = null;	
	
	private String headerLog, dataTableHeader;
	private boolean committedHeaderLog;
	private ExperimentData experimentData;
	
	// for testing
	protected String loggerTime;
	
	public StatLoggerImpl() {
		headerLog = "";		
		dataTableHeader = "";
		committedHeaderLog = false;
	}

	public void configureLogger(String optionalFileNamePrefix, 
								long runId,
								long repeatIndex,
								long experimentId,
								String experimentsTargetDirectory,
								String optionalExperimentTag,
								ExperimentData experimentData) {
		
		this.experimentData = experimentData;
		
		loggerTime = StatLoggerUtils.getDateTime();
		headerLog = "" + optionalFileNamePrefix + " Log Time: " + loggerTime + "\n\n";	
		
         /* logging services */
        String fileName = "run" + runId + "-rpt" + repeatIndex + "-exp" + experimentId + "-";
        
        if (optionalExperimentTag != null && optionalExperimentTag.length() > 0)
            fileName += optionalExperimentTag;
        
        if (optionalFileNamePrefix != null && optionalFileNamePrefix.length() > 0)
            fileName = optionalFileNamePrefix + "-" + fileName;

        // Oliver: 2010-09-15
        //fileName += "-log" + StatLoggerUtils.getDateTime() + ".log";
        fileName += ".log";
        
        FileAppender appender; 
        try {
            PatternLayout layout = new PatternLayout("%m %n");            
            if (experimentsTargetDirectory!= null) {
            	String expTargetDirectory;
            	if (optionalFileNamePrefix != null)
            		expTargetDirectory = experimentsTargetDirectory + File.separator + optionalFileNamePrefix;
            	else
            		expTargetDirectory = experimentsTargetDirectory;
            	System.out.println("experimentsTargetDirectory = " + expTargetDirectory);
            	System.out.println("AbsolutePath: " + (new File(expTargetDirectory).getAbsolutePath()));
            	
            	// Create the experiments target directory, if not existent
                if (!new File(expTargetDirectory).exists())
                	(new File(expTargetDirectory)).mkdir();
            	
                appender = new FileAppender(layout, expTargetDirectory + File.separator + fileName, false);
            }
            else
                appender = new FileAppender(layout, fileName, false);
        }
        catch(IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to load logging property");
        }

        if (logger == null) {
        	logger = Logger.getLogger(fileName);
        	logger.addAppender(appender);
        	logger.setLevel((Level)Level.INFO);      
        	logger.setAdditivity(false);
        }
	}
	
	public void appendToHeaderLog(String log) {
		headerLog = headerLog + "\n" + log;
	}
	
	public void appendToDataTableHeader(String dataTableHeader) {
		this.dataTableHeader += "" + dataTableHeader + "\t";
	}
	
	public void appendDataRow(String row) {
		logger.info(row);
	}


	 public void commitHeaderLog() {
        if(!committedHeaderLog) {
            committedHeaderLog = true;       
            
            if (experimentData != null)
                appendToHeaderLog(experimentData.getDataSummary());
            
            logger.info(headerLog);            
            
            String str= "\n\n" + HEADER_TAG + "\t" + dataTableHeader;        
        
            logger.info(str);
        } else
        	throw new RuntimeException("[StatLoggerImpl] - logger header already commited, but attempted to recommit!");
    }
}
