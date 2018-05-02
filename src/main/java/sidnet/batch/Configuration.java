package sidnet.batch;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sidnet.batch.Constants.MATH;

public class Configuration {
	public String inputFilePath;
	public String outputFileName;
	
	public String xAxisTagName;
	public String yAxisTagName;
	
	public Map<String, String> whereClauses;
	
	public MATH mathOperation;
	
	public boolean timeAtMode;
	public double timeAtThreshold;	
	
	public boolean timeOfMode;
    public double timeOfThreshold;
    
    public boolean timeLimitMode;
    public double timeLimit;
    
    public boolean nonZeroMode;
    
    public boolean scatterplot;
    public boolean catplot;
    
    public boolean isolateFaultyExperiments;
    
    public List<String> groupByCriteriaList;
    
    public String xTag;
    
    public boolean barplot;
	
	public Configuration() {
		reset();
	}
	
	public void reset() {
		inputFilePath = null;
		outputFileName = null;		
		
		timeAtMode = false;
		timeAtThreshold = -1;
		
		timeOfMode = false;
		timeOfThreshold = -1;
		
		timeLimitMode = false;
		timeLimit = -1;
		
		nonZeroMode = false;
		scatterplot = false;
		catplot     = false;
		barplot     = false;
		
		isolateFaultyExperiments = false;
		
		groupByCriteriaList = new LinkedList<String>();
		
		whereClauses = new LinkedHashMap<String, String>();
		
		mathOperation = MATH.RAW;
	}
	
	 public void displayParams() { 
        System.out.println("----------------------------------- ");
        System.out.println("inputFilePath = " + inputFilePath);
        System.out.println("outputFileName = " + outputFileName);
        System.out.println("X-Axis Tag Name = " + xAxisTagName);
        System.out.println("Y-Axis Tag Name = " + yAxisTagName);
        for (String groupBy: groupByCriteriaList)
            System.out.println("Group by       = " + groupBy);
        System.out.println("Math Operation  = " + mathOperation.toString());
        if (timeOfMode)
            System.out.println("timeof = " + timeOfThreshold);
        if (timeLimitMode)
            System.out.println("timelimit = " + timeLimit);
        if (barplot)
        	System.out.println("Plot type = bar-plot");
        else
        	System.out.println("Plot type = xy-plot");
        if (whereClauses.size() > 0) {
        	System.out.println("where: ");
        	for (String key: whereClauses.keySet())
        		System.out.println("\t" + key + " = " + whereClauses.get(key));
        }        	
        System.out.println("----------------------------------- ");
     }
}
