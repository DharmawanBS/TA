package sidnet.utilityviews.commons;

import sidnet.utilityviews.statscollector.ExperimentData;

public interface StatLogger {
    public void configureLogger(String optionalFileNamePrefix, 
    		                    long runId,
    		                    long repeatIndex,
    		                    long experimentId,
    		                    String experimentsTargetDirectory,
    		                    String optionalExperimentTag,
    		                    ExperimentData experimentData);
    public void appendToHeaderLog(String log);
    public void appendToDataTableHeader(String dataTableColumnHeader);
    public void commitHeaderLog();
    public void appendDataRow(String row);   
}
