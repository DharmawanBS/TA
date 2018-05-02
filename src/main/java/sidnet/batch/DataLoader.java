package sidnet.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DataLoader {
	public static DataBean load(File file, Configuration config) 
	throws FaultyExperimentException, FileNotFoundException {
		int xAxisIndex = 0, yAxisIndex = 0;
	            
	    String nextLine;
	    List<Pair> rowList = new LinkedList<Pair>();
	    Pair xyHeader = new Pair();
	    
	    Map<String, Object> groupByInfo = new LinkedHashMap<String, Object>();
	    Map<String, String> tagsInfo = new LinkedHashMap<String, String>();
	    String catKey = retrieveCatKeyAndPopulateGroupInfo(file, groupByInfo, tagsInfo, config.groupByCriteriaList, config.xAxisTagName, config.yAxisTagName);	    	    
	    
	    String[] rowEntries = null; 
	    
	    BufferedReader input = null;  
	    try{
	         input = new BufferedReader(new FileReader(file));
	    } catch(FileNotFoundException e){e.printStackTrace();System.exit(1);};
	    
	    try {
	        while((nextLine = input.readLine()) != null) {            	  
	      	  // Scan the header to find xAxis and yAxis tags
	            if (nextLine.toLowerCase().contains("<header>")) {
	                nextLine = nextLine.replaceAll("\t", " ");
	                String[] header = nextLine.split(" ");      
	                int contor = 0; // needed to avoid counting empty strings
	                for (int i = 0; i < header.length; i++) {// start at 1 since the 0th is <header>
	                    if (header[i].length() == 0)
	                        continue;
	                    
	                    if (xAxisIndex == 0 && header[i].trim().toLowerCase().equals(config.xAxisTagName.toLowerCase()))
	                        xAxisIndex = contor;
	                    if (yAxisIndex == 0 && header[i].trim().toLowerCase().equals(config.yAxisTagName.toLowerCase()))
	                        yAxisIndex = contor;
	                    contor ++;
	                }
	
	                if (xAxisIndex == 0)
	              	  xAxisIndex = checkInGroupByTags(config.xAxisTagName, config.groupByCriteriaList);                      
	                if (xAxisIndex == 0)
	          		  throw new FaultyExperimentException("[ExtractData] ERROR: xAxisTagName (\"" + config.xAxisTagName + "\") has not been found ");
	                if (yAxisIndex == 0)
	              	  yAxisIndex = checkInGroupByTags(config.yAxisTagName, config.groupByCriteriaList);                                            
	                if (yAxisIndex == 0)
	          		  throw new FaultyExperimentException("[ExtractData] ERROR: yAxisTagName (\"" + config.yAxisTagName + "\") has not been found "); 	  
	                if (xAxisIndex == yAxisIndex)
	                    throw new FaultyExperimentException("[ExtractData] ERROR: xAxisTagName (\"" + config.xAxisTagName + "\") cannot have the same name with yAxisTagName (\"" + config.yAxisTagName + "\")");
	                //System.out.println("xAxisIndex = " + xAxisIndex);
	                //System.out.println("yAxisIndex = " + yAxisIndex);
	            }
	            if (nextLine.contains("<row>")) {
	                if(xAxisIndex == 0 || yAxisIndex == 0)
	                    throw new FaultyExperimentException("[ExtractData] ERROR: <row> element found before the <header> element. I don't know where to place the data. Aborting!");
	                
	                nextLine = nextLine.replaceFirst(" ","");
	                nextLine = nextLine.replaceAll("\t"," ");
	                
	                rowEntries = nextLine.split(" ");                                         
	                
	                Pair pair = new Pair();
	                if (xAxisIndex > 0)
	              	  pair.x = rowEntries[xAxisIndex].trim();
	                else
	              	  pair.x = groupByInfo.get(config.xTag); // this is a group data
	                if (yAxisIndex > 0)
	              	  pair.y = Double.parseDouble(rowEntries[yAxisIndex].trim());
	                else
	              	  throw new FaultyExperimentException("ExtractData] ERROR - Invalid Y-Axis tag; Keep in mind that only the X-axis can be a group-by identifier");
	                
	          	    if (config.timeAtMode)
	          	    	if (config.timeAtThreshold != Double.parseDouble(rowEntries[1].trim()))
	          	    		continue;
	          	  
	                // If timelimit_Mode enabled, check the timelimit condition
	                if (config.timeLimitMode) {
	              	  double timevalue = Double.parseDouble(rowEntries[0].trim());
	                	  if (config.timeLimit < timevalue)
	                		  break;
	                }	               	                
	                rowList.add(pair);
	            } 	            
	        }
	        
	    } catch(IOException ioe){ioe.printStackTrace(); System.exit(1);};
	    
	    if (rowList.size() == 0)
	    	if (config.timeAtMode)
	    		if (config.timeAtThreshold > Double.parseDouble(rowEntries[1].trim()))
	    			throw new FaultyExperimentException(
  	    			"The seeked time-At <row> is never reached " +
  	    			"either because the experiment is too short or " +
  	    			"because no sample has been taken at the exact <timeAt>=" +
  	    			config.timeAtThreshold + " timestamp");
	    
	    if (rowList.size() == 0)
	        return null;
	    
	    return new DataBean(config.xAxisTagName, config.yAxisTagName, rowList, groupByInfo, tagsInfo, catKey);
	}
	
	public static String retrieveCatKeyAndPopulateGroupInfo(File file, Map<String, Object> groupByInfo, Map<String, String> tagsInfo, List<String> groupByCriteriaList, String xAxisTag, String yAxisTag)
    throws FaultyExperimentException, FileNotFoundException {
        BufferedReader input = null;    
        String nextLine;
        String catKey = "";
        int matchesContor = 0;
        
        // Open File
        input = new BufferedReader(new FileReader(file));
         
        List<String> matchedGroupByList = new LinkedList<String>();
         
        Map<String, String> catKeyBits = new HashMap<String, String>();
        
        
        try {        	 
        	 for (String groupByItem: groupByCriteriaList)
        		 matchedGroupByList.add(groupByItem);
        	 
             while((nextLine = input.readLine()) != null) {
                 nextLine = nextLine.replaceAll(" ", "");
                 nextLine = nextLine.replaceAll("\t", "");
                 // Look for the <groupByTag> and return its value (right hand side)
                 // e.q.
                 // <SimulationTag> Bezier
                 // for groupByTag = SimulationTag will return "Bezier"
                 // Notice that we may have multiple groupBy tags specified
                 // in which case ALL must match
                 
                 // for the where clauses
                 if (nextLine.toLowerCase().contains("<") &&
                     nextLine.toLowerCase().contains(">")) {
                	 String variable = nextLine.split(">")[0];
                	 variable = variable.trim(); // get rid of spaces
                	 variable = variable.substring(1); // get rid of leading "<"
                	 tagsInfo.put(variable, nextLine.split(">")[1].trim());
                 }
                 
                 for (String groupByItem: groupByCriteriaList)
                     if (nextLine.toLowerCase().contains("<"+groupByItem.toLowerCase()+">")) {
                         System.out.println(nextLine);                         
                         matchedGroupByList.remove(groupByItem);
                         
                        // if (!groupByInfo.get(groupByItem).contains(nextLine.split(">")[1].trim()))
                       	   //groupByInfo.get(groupByItem).add(nextLine.split(">")[1].trim());
                         groupByInfo.put(groupByItem, nextLine.split(">")[1].trim());                         
                         
                         /*if (catKey.length() != 0) {
                        	 String newCatKey = nextLine.split(">")[1].trim();
                             catKey += "-" + newCatKey;
                         }
                         else
                           catKey += "" + nextLine.split(">")[1].trim();*/
                         if (catKeyBits.size() != 0) {
                        	 String newCatKey = nextLine.split(">")[1].trim();
                             catKeyBits.put(groupByItem, newCatKey);
                         }
                         else
                        	 catKeyBits.put(groupByItem, "" + nextLine.split(">")[1].trim());
                         
                         matchesContor++;
                     }
             }
         }
         catch(IOException ioe){ioe.printStackTrace(); System.exit(1);};
         
         // build the cat-key with tags in the order of the group-by items
         for (String groupByItem: groupByCriteriaList) {
        	String tag = catKeyBits.get(groupByItem);
        	if (tag != null) {
        		if (catKey.length() == 0)
        			catKey += tag;
        		else
        			catKey += "-" + tag;
        	}
         }
         
         if (matchesContor == groupByCriteriaList.size())
             return catKey + "-";
         else {
	       	  if (matchesContor > groupByCriteriaList.size())
	       		  throw new FaultyExperimentException("[ERROR]<ExtractData> Experiment file: " 
	       		  			+ file.getName() + " contains duplicate <tags>. This is not allowed! Verify your ExperimentData.java file! Exiting.");
	       	  
	       	  String msg = "[ERROR]<ExtractData> Experiment file: " 
	       		  			+ file.getName() 
	       		  			+ " does not contain all the groupby tags. "
	       		  			+ " Unable to find the following tags: ";
	       	  for (String missingTag: matchedGroupByList)
	       		  msg += "<" + missingTag + ">";
	             throw new FaultyExperimentException(msg);
         }
    } 
	
	 private static int checkInGroupByTags(String tag, List<String> groupByCriteriaList) {
    	 int index = -1;
    	 for (String groupByTag: groupByCriteriaList)
    		 if (groupByTag.toLowerCase().equals(tag.toLowerCase()))
    	 		return index;
    	 	 else
    	 		index--;
    	 return 0;
     }    
}
