/*
 * StatsCollector.java
 *
 * Created on July 29, 2005, 4:45 PM
 */

/**
 *
 * @author  Oliviu Ghica
 */
package sidnet.utilityviews.statscollector;

import javax.swing.*; // For JPanel, etc.
import java.awt.*;           // For Graphics, etc.
import java.util.LinkedList;
import jist.runtime.JistAPI;
import jist.swans.Constants;

import org.apache.log4j.*;

import sidnet.core.interfaces.SimulationTimeRunnable;
import sidnet.core.interfaces.UtilityView;
import sidnet.core.misc.*;
import sidnet.utilityviews.commons.StatLogger;
import sidnet.utilityviews.commons.StatLoggerImpl;
import sidnet.utilityviews.commons.StatLoggerUtils;

@SuppressWarnings("serial")
public class StatsCollector 
extends UtilityView
implements SimulationTimeRunnable {  	
	
	static Logger logger = null;		
	static {
		 logger = Logger.getLogger("StatCollectorLogger");
		 logger.setLevel((Level) Level.INFO);		 
	}
	
    private LinkedList<StatEntry> statEntryList = null;
    
    private JScrollPane jScrollPane;
    
    private static final int vertInitialPosition = 35;
    private static final int vertIncrement = 18;
    
    public boolean testMode = false;
    
    /** self-referencing proxy entity. */
    private Object self;
    
    long StartTimeStamp;
    long currentTimeStamp;
    double SimSpeed;        // Measures in x Times the RealTime
    
    long realTimeStart = 0;   //[ms]
    long realTimeCurrent = 0;
   
    public int sdCount;
    public int sampling;
    public String simName;
    
    Node[] nodes;
    
    private EventMonitor eventMonitor = null;
   
    /* Log services */
    static Logger log;
    private static long loggingInterval;
    private String headerLogHeading = "";
    private boolean commitedHeaderLog = false;
    
    private static StatLogger statLogger = null;
   
    /**
     * Creates a new instance of StatsCollector
     * Logging enabled
     */
    public StatsCollector(Node[] nodes, int batCapacity, ExperimentData experimentData) {
        statLogger = new StatLoggerImpl();
        statLogger.configureLogger( "StatCollector",
        							experimentData.getInt(ExperimentData.RUN_ID),
                         			experimentData.getInt(ExperimentData.REPEAT_INDEX),
                         			experimentData.getInt(ExperimentData.EXPERIMENT_ID),
                         			experimentData.getString(ExperimentData.EXPERIMENTS_TARGET_DIRECTORY), 
                         			experimentData.getString(ExperimentData.EXPERIMENT_TAG), 
                         			experimentData);
        
        StartTimeStamp  = 0;
        currentTimeStamp= 0;
        
        StatsCollector.loggingInterval = experimentData.getLong(experimentData.LOGGING_INTERVAL);
        
        this.nodes = nodes;
        
        eventMonitor = new EventMonitor();
        
        this.self = JistAPI.proxy(this, SimulationTimeRunnable.class);
               
        if (!testMode)
            ((SimulationTimeRunnable)self).run();
    }
    
    
     /**
     * Creates a new instance of StatsCollector
     * DO NOT USE THIS CONSTRUCTOR. ONLY FOR TESTING PURPOSES
     * Logging enabled
     */
     public StatsCollector(Node[] myNode, int areaLength, int batCapacity, long loggingInterval, boolean testMode) {
        
        StartTimeStamp  = 0;
        currentTimeStamp= 0;
        
        eventMonitor = new EventMonitor();
        
        this.self = JistAPI.proxy(this, SimulationTimeRunnable.class);
        
        StatsCollector.loggingInterval = loggingInterval;
        
        if (loggingInterval == 0) {
            System.err.println("[StatsCollector] - logging interval = 0, which is not allowed. Quitting");
            System.exit(1);
        }
        
        this.testMode = testMode;
        if (!testMode)
            ((SimulationTimeRunnable)self).run();
    }
     
    /* For testing purposes only */
    public String getValueOf(String key) {
    	for (StatEntry statEntry: statEntryList) {
    		if (statEntry.key.equals(key))
    			return statEntry.getValueAsString();
    	}
    	return null;
    }
    
    /**
     * Creates a new instance of StatsCollector
     * Logging disabled
     */
    public StatsCollector(Node[] nodes, int areaLength, int batCapacity, long loggingInterval) {
        
        StartTimeStamp  = 0;
        currentTimeStamp= 0;
        
        this.nodes = nodes;
        
        eventMonitor = new EventMonitor();
        
        this.self = JistAPI.proxy(this, SimulationTimeRunnable.class);
        
        StatsCollector.loggingInterval = loggingInterval;
        
        if (loggingInterval == 0) {
            System.err.println("[StatsCollector] - logging interval = 0, which is not allowed. Quitting");
            System.exit(1);
        }
        
        ((SimulationTimeRunnable)self).run();
    }
    
     public void excludeFromMonitoring(String key, int nodeid){
         for(StatEntry statEntry: statEntryList)
             if (statEntry.getKey() != null && statEntry.getKey().equals(key))
                if (statEntry instanceof ExclusionStatEntry)
                     ((ExclusionStatEntry)statEntry).excludeFromMonitoring(nodeid);
     }
     
     public void includeInMonitoring(String key, int nodeid){
         for(StatEntry statEntry: statEntryList)
             if (statEntry.getKey() != null && statEntry.getKey().equals(key))
                if (statEntry instanceof ExclusionStatEntry)
                     ((ExclusionStatEntry)statEntry).includeInMonitoring(nodeid);
     }

    public SimulationTimeRunnable getProxy(){
        return (SimulationTimeRunnable)self;
    }
    
    public StatEntry get(int i) {
        return statEntryList.get(i);
    }
    
  
    
    /* @override */
    public void configureGUI(JPanel hostPanel) {

        this.setOpaque(true);
        this.setBackground(Color.BLACK);
        this.setVisible(true);
                
        jScrollPane = new JScrollPane(this);
       
        jScrollPane.setBounds(0,0, hostPanel.getWidth(), hostPanel.getHeight());         
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        hostPanel.add(jScrollPane);        
    }
    
    /* @override */
    public void repaintGUI()
    {
//        if (!loggingEnabled)
//        {
//            update(JistAPI.getTime()/Constants.MILLI_SECOND, false);
//            //this.repaint();
//        }
    }
    
    public void enableEventMonitoring(String key)
    {
        if (eventMonitor != null)
            eventMonitor.enableMonitor(key);
    }
    
    public void disableEventMonitoring(String key) {
        if (eventMonitor != null)
            eventMonitor.disableMonitor(key);
    }
    
    public void markPacketSent(String key, long packetId) {        
        markEventOccurred(key, packetId);
    }    
    
    public void markEventOccurred(String key, long eventId) {
        eventMonitor.markEventOccurred(key, eventId, JistAPI.getTime());       
    }    
    
    public void markPacketReceived(String key, long packetId) {
        markEventDetected(key, packetId);
    }
    
    public void markEventDetected(String key, long eventId) {
        eventMonitor.markEventDetected(key, eventId, JistAPI.getTime()); 
    }
    
    public void incrementValue(String key, double incrementAmount) {
    	if (Double.isNaN(incrementAmount))
    		throw new RuntimeException("NaN value fed to the statCollector via incrementAmount()");
    	boolean found = false;
    	StatEntry entry = null;
    	for(StatEntry statEntry: statEntryList)
    		if (statEntry.getKey().equals(key)) {
    			entry = statEntry;
    			if (statEntry instanceof IncrementableStatEntry) {
    				found = true;
    				((IncrementableStatEntry)statEntry).increment(incrementAmount);
    				break;
    			}
    		}
        if (!found)
        	triggerError(key, entry, "incrementValue()");
    }
    
    public void appendValue(String key, double value) {
    	if (Double.isNaN(value))
    		throw new RuntimeException("NaN value fed to the statCollector via appendValue()");
    	boolean found = false;
    	StatEntry entry = null;
    	for(StatEntry statEntry: statEntryList)
    		if (statEntry.getKey().equals(key)) {
    			entry = statEntry;    			
    			if (statEntry instanceof AggregateStatEntry) {
    				found = true;
    				((AggregateStatEntry)statEntry).appendValue(value);
    				break;
    			}
    		}
        if (!found)
        	triggerError(key, entry, "appendValue()");
    }
    
    private void triggerError(String key, StatEntry entry, String caller) {
    	if (entry == null)
    		System.err.println("[StatCollector] <ERROR> StatEntry: '" + key + "' has not been registered with StatCollector in the driver file, or check the key for misspelling");
    	else {        		
    		System.err.println("[StatCollector] <ERROR> StatEntry: '" + key + "' does not support '" + caller + "'");
    		String str = "[StatCollector] <ERROR> StatEntry: '" + key + "' only supports ";
    		if (entry instanceof IncrementableStatEntry)
    			str += "'incrementValue(double incrementAmount)' method calls.";
    		if (entry instanceof AggregateStatEntry)
    			str += "'appendValue(double value)' method calls.";
    		if (entry instanceof EventBasedStatEntry)
    			str += "'markPacketSent(...), markPacketReceived(...), markEventOccurred(...), markEventDetected(...)' method calls.";
    		System.err.println(str);        				
    	}
    }
       
    public void run() {
        if (testMode)
            return;
        
        if(statLogger != null && !commitedHeaderLog) {
            statLogger.commitHeaderLog();
            commitedHeaderLog = true;
        }
        update(JistAPI.getTime() / Constants.MILLI_SECOND, true);
        repaint(); // this does also the update
        //???if (loggingEnabled)
            commitLog();
        
        resetBatchedStats();
        
        JistAPI.sleepBlock(loggingInterval);
        ((SimulationTimeRunnable)self).run();
    }
    
    public synchronized void update(long currentTime, boolean triggerError) {
        if (currentTimeStamp > currentTime) {
            if (triggerError) {
                System.err.println("<StatsCollector>[ERROR] Out of order time stamps in Update() method. CurrentTimeStamp = " + currentTimeStamp + " and received timestamp = " + currentTime);
                new Exception().printStackTrace();
                System.exit(1);
            }
        }
        
        currentTimeStamp = currentTime;
        
        if (realTimeStart == 0)
            realTimeStart = System.currentTimeMillis();
        else
            realTimeCurrent = System.currentTimeMillis() - realTimeStart;    
        
        updateCommonStats();                
    }
    
    private void resetBatchedStats() {
    	//System.out.println("++++++++++++++ RESET +++++++++++++++++++++");
    	if (statEntryList != null)
            for (StatEntry statEntry:statEntryList) {
            	if (statEntry instanceof StatEntry_GeneralPurposeBatchedRunningMeanAggregator) {
                	//System.out.println("BATCHED_AVG = " + ((AggregateStatEntry)statEntry).getValueAsString());

            	}
                if (statEntry instanceof AggregateStatEntry)
                	((AggregateStatEntry)statEntry).reset();                
            }
    }
    
    // Final update stats. To be executed towards the end of a simulation
    public void commitLog() {
    	if (statLogger != null) {    	
	    	logger.debug("commitLog()");
	    	
	        String row = StatLoggerImpl.ROW_TAG;
	       
	        if (statEntryList != null)
	            for (StatEntry statEntry:statEntryList) {
	                row += statEntry.getValueAsString() + "\t";
	            }
	        
        	statLogger.appendDataRow(row);
    	}        
        eventMonitor.resetEventLatencies();
    }
    
    public void updateCommonStats(){
        if (JistAPI.getTime() == 0)
            return;
              
        if (statEntryList != null)
        	for (StatEntry statEntry: statEntryList) {
        		if (statEntry instanceof NodeBasedStatEntry)
                    ((NodeBasedStatEntry)statEntry).update(nodes);
        	}
    }
    
    public void monitor(StatEntry statEntry) {
        if (statEntryList == null)
            statEntryList = new LinkedList<StatEntry>();
        statEntryList.add(statEntry);
        
        if (!testMode)
            if (statEntry instanceof ExclusionStatEntry)
                ((ExclusionStatEntry)statEntry).initialize(nodes.length);
        
        if (statEntry instanceof EventBasedStatEntry)
            ((EventBasedStatEntry)statEntry).setEventMonitor(eventMonitor);
        
        headerLogHeading += "" + statEntry.getHeader() + "\t";
        
        if (statLogger != null)
        	statLogger.appendToDataTableHeader(StatLoggerUtils.buildDataTableHeader(statEntry.key, statEntry.tag));
    }
    
    
   
    public double getFurthestNeighborDistanceNCS() {
        double max=0;
        double dist;
        for (int i=0; i < nodes.length; i++)
        {
            dist = nodes[i].neighboursList.getFurthestNodeDistanceNCS(nodes[i].getNCS_Location2D());
            if (max < dist)
                max = dist;
        }
        return max;
    }

    /* @override */
     public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        String s;
  
        g2d.setColor(Color.white);  
        s = "Elapsed Time: " ;
        s += StatEntry_Time.getFormatedTimeAsString(currentTimeStamp);
        
        g2d.drawString(s,10,15);
        
        int vertPosition = vertInitialPosition;
        int maxHorzSize = this.getPreferredSize().width;
        
        // Calculating the vertical and horizontal scroll dimensions
        if (statEntryList != null)
            for (StatEntry statEntry:statEntryList) {
                s = statEntry.getHeader() + ": " + statEntry.getValueAsString();
                g2d.drawString(s, 10, vertPosition);
                if (maxHorzSize < s.length() * 8) 
                    maxHorzSize = s.length() * 8; // establishing the horizontal scroll width
                vertPosition += vertIncrement;
            }                  
     
        // if dimensions changed
        if(this.getPreferredSize().getHeight() != vertPosition + 10 || 
           this.getPreferredSize().getWidth()  != maxHorzSize)
              this.setPreferredSize(new Dimension(maxHorzSize,vertPosition + 10));
            
        if (realTimeCurrent >= 1) {
            s = "Simulation Speed : "+(double)((int)(currentTimeStamp / realTimeCurrent * 10))/10+"x";
            g2d.drawString(s,10,vertPosition);
        }
     }
    
    protected void clear(Graphics g) {
        //super.paintComponent(g);
    }       
}
