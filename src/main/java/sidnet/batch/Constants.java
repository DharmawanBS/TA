package sidnet.batch;

public class Constants {
	enum MATH {RAW, AVG,MIN,MAX};
	 
	public static final String DEFAULT_FILE_EXTENSION = ".dat";
	
	public static final String SIDNET_DRIVER_TAG = "*SIDnetDriver",
			 				   OPTIONAL_INLINE_TAG1 = "[",
			 				   OPTIONAL_INLINE_TAG2 = "]";
	
	public static final String ISOLATION_FOLDER = "/FaultyExperiments",	
    						   FILE_PREFIX_NAME1 ="run",
    						   FILE_PREFIX_NAME2 ="StatCollector-run",
    						   FILE_EXTENSION   =".log",
    						   OUTPUT_DELIMITER ="\t", // for tab separated output files
    						   GROUPBY_PARAM    = "groupby=",
    						   WHERE_PARAM      = "where:",
    						   MATH_PARAM       = "math=",
    						   TIMEAT_PARAM     = "timeat=",
    						   TIMEOF_PARAM     = "timeof=",
    						   TIME_LIMIT       = "timelimit=",
    						   NON_ZERO         = "nonzero",
    						   ISOLATE_FAULTY_EXPERIMENTS = "isolateFaultyExperiments";
}
