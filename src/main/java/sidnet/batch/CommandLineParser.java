package sidnet.batch;

import sidnet.batch.Constants.MATH;

public class CommandLineParser {
	
	private static final String TIME_TAG = "Time";
	
	public static void parse(String[] cmdLineArgs, Configuration config)
	throws Exception {
		  config.reset();
		
		  config.inputFilePath  = cmdLineArgs[0];
		  config.outputFileName = cmdLineArgs[1];
		  config.xAxisTagName   = cmdLineArgs[2];
		  config.xTag           = config.xAxisTagName;
		  config.yAxisTagName   = cmdLineArgs[3];
		
		  processMathArgument(cmdLineArgs, config);
	      processGroupByArguments(cmdLineArgs, config);
	      processWhereClauses(cmdLineArgs, config);
	      processTimeOfArgument(cmdLineArgs, config);
	      processTimeAtArgument(cmdLineArgs, config );
	      processTimeLimitArgument(cmdLineArgs, config);
	      processNonZeroArgument(cmdLineArgs, config);
	      processIsolateFaultyExperimentsArgument(cmdLineArgs, config);

	      if (config.xAxisTagName.equals(TIME_TAG) && config.timeAtMode)
	    	  config.barplot = true;
	      
	      if (!config.catplot && !config.xAxisTagName.equals(TIME_TAG))
	    	  config.scatterplot = true;
	}
	
	public static void processMathArgument(String[] cmdLineArgs, Configuration config) 
    throws Exception {
        // First, let's check that a single math= operation is specified
        int contor = 0;
        for(int i = 0; i < cmdLineArgs.length; i++)
            if (cmdLineArgs[i].toLowerCase().contains(Constants.MATH_PARAM))
                contor++;
        
        if (contor > 1) {
            throw new Exception("[ERROR]<ExtractData> - Invalid argument: multiple '" + Constants.MATH_PARAM +"' found. Maximum allowed is 1");
        }
        
        if (contor == 0) {
            throw new Exception("[ERROR]<ExtractData> - Invalid argument: No '" + Constants.MATH_PARAM +"' found. Required 1");
        }
        
        for(int i = 0; i < cmdLineArgs.length; i++)
            if (cmdLineArgs[i].toLowerCase().contains(Constants.MATH_PARAM)) {
               String[] args = cmdLineArgs[i].toLowerCase().split("=");
               String argument = args[1].trim();
            
               if (argument.equals("average") || argument.equals("avg"))
                   config.mathOperation  = MATH.AVG;
               if (argument.equals("minimum") || argument.equals("min"))
                   config.mathOperation  = MATH.MIN;
               if (argument.equals("maximum") || argument.equals("max"))
                   config.mathOperation  = MATH.MAX;
            }
    }
	
	 public static void processGroupByArguments(String[] cmdLineArgs, Configuration config) {
         for (int i = 0; i < cmdLineArgs.length; i++)
             if (cmdLineArgs[i].toLowerCase().contains(Constants.GROUPBY_PARAM)) {
                 String args[] = cmdLineArgs[i].split("=");
                 config.groupByCriteriaList.add(args[1]);
                 if (args[1].equals(config.xTag))
                	 config.catplot = true;
             }
     }
	 
	 public static void processWhereClauses(String[] cmdLineArgs, Configuration config) {
         for (int i = 0; i < cmdLineArgs.length; i++)
             if (cmdLineArgs[i].toLowerCase().contains(Constants.WHERE_PARAM)) {
                 String args[] = cmdLineArgs[i].split(":");
                 String rightHandSide = args[1];
                 String variable = rightHandSide.split("=")[0];
                 String value    = rightHandSide.split("=")[1];
                 config.whereClauses.put(variable, value);              
             }
     }
        
	
	public static void processTimeOfArgument(String[] cmdLineArgs, Configuration config) 
    throws Exception {
        int contor = 0;
        for(int i = 0; i < cmdLineArgs.length; i++)
            if (cmdLineArgs[i].toLowerCase().contains(Constants.TIMEOF_PARAM))
                contor++;
        
        if (contor > 1) {
            throw new Exception("[ERROR]<ExtractData> - Invalid argument: multiple '" + Constants.TIMEOF_PARAM +"' found. Maximum allowed is 1");
        }
        
        for(int i = 0; i < cmdLineArgs.length; i++)
            if (cmdLineArgs[i].toLowerCase().contains(Constants.TIMEOF_PARAM)) {
               String[] args = cmdLineArgs[i].toLowerCase().split("=");
               config.timeOfThreshold = Double.parseDouble(args[1].trim());
               config.timeOfMode = true;
               break;
            }
    }
    
    public static void processTimeAtArgument(String[] cmdLineArgs, Configuration config) 
    throws Exception {
        int contor = 0;
        for(int i = 0; i < cmdLineArgs.length; i++)
            if (cmdLineArgs[i].toLowerCase().contains(Constants.TIMEAT_PARAM))
                contor++;
        
        if (contor > 1)
            throw new Exception("[ERROR]<ExtractData> - Invalid argument: multiple '" + Constants.TIMEAT_PARAM +"' found. Maximum allowed is 1");
        
        for(int i = 0; i < cmdLineArgs.length; i++)
            if (cmdLineArgs[i].toLowerCase().contains(Constants.TIMEAT_PARAM)) {
               String[] args = cmdLineArgs[i].toLowerCase().split("=");
               config.timeAtThreshold = Double.parseDouble(args[1].trim());
               config.timeAtMode = true;
               break;
            }
    }
    
    public static void processTimeLimitArgument(String[] cmdLineArgs, Configuration config)
    throws Exception {
        int contor = 0;
        for(int i = 0; i < cmdLineArgs.length; i++)
            if (cmdLineArgs[i].toLowerCase().contains(Constants.TIME_LIMIT))
                contor++;
        
        if (contor > 1)
            throw new Exception("[ERROR]<ExtractData> - Invalid argument: multiple '" + Constants.TIME_LIMIT +"' found. Maximum allowed is 1");
        
        for(int i = 0; i < cmdLineArgs.length; i++)
            if (cmdLineArgs[i].toLowerCase().contains(Constants.TIME_LIMIT)) {
               String[] args = cmdLineArgs[i].toLowerCase().split("=");
               config.timeLimitMode = true;
               config.timeLimit = Double.parseDouble(args[1].trim());               
               break;
            }
    }
    
    public static void processNonZeroArgument(String[] cmdLineArgs, Configuration config) 
    throws Exception {
        int contor = 0;
        for(int i = 0; i < cmdLineArgs.length; i++)
            if (cmdLineArgs[i].toLowerCase().contains(Constants.NON_ZERO))
                contor++;
        
        if (contor > 1)
            throw new Exception("[ERROR]<ExtractData> - Invalid argument: multiple '" + Constants.NON_ZERO +"' found. Maximum allowed is 1");
        
        for(int i = 0; i < cmdLineArgs.length; i++)
            if (cmdLineArgs[i].toLowerCase().contains(Constants.NON_ZERO)) {
               config.nonZeroMode = true;
               break;
            }
    }
    
    public static void processIsolateFaultyExperimentsArgument(String[] cmdLineArgs, Configuration config) 
    throws Exception {
        int contor = 0;
        for(int i = 0; i < cmdLineArgs.length; i++)
            if (cmdLineArgs[i].toLowerCase().contains(Constants.ISOLATE_FAULTY_EXPERIMENTS.toLowerCase()))
                contor++;
        
        if (contor > 1)
            throw new Exception("[ERROR]<ExtractData> - Invalid argument: multiple '" + Constants.ISOLATE_FAULTY_EXPERIMENTS +"' found. Maximum allowed is 1");
        
        if (contor == 1)
       	 config.isolateFaultyExperiments = true;
    }
}
