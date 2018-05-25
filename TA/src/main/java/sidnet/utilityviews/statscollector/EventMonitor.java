/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.utilityviews.statscollector;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import jist.runtime.JistAPI; 
import jist.swans.Constants;

/**
 *
 * @author Oliver
 */
class EventMonitor 
implements JistAPI.DoNotRewrite {
	static Logger logger;	
	static {
		 logger = Logger.getLogger("EventMonitorLogger");
		 logger.setLevel((Level) Level.INFO);		 
	}
	
    /* ***************************************** */
    private static final long    UNMATCHED_EVENTS_EVICTION_TIME = 10 * Constants.MINUTE;
    /* ***************************************** */
    
    private Map<String, Boolean> enabledMap = null;
    
    private Map<String, LinkedList<Pair>> eventMonitoringListMap = null;
    private Map<String, Long> eventOccurredCountMap = null; // e.q. packetSent
    private Map<String, Long> eventDetectedCountMap = null;  // e.q. packetReceived
    
    private Map<String, Long> cummulativeDeliveryLatencyMap = null; // for AVG
    private Map<String, Long> minimumDeliveryLatencyMap = null;
    private Map<String, Long> maximumDeliveryLatencyMap = null;
    
    /* TODO
    public static enum PREDICATE
    {
        LESS_THAN,
        LESS_THAN_OR_EQUAL,
        EQUALS,
        GREATER_THAN_OR_EQUAL,
        GREATER_THAN;
    }*/
    
    public EventMonitor() {
        enabledMap                    = new HashMap<String, Boolean>();
        eventMonitoringListMap        = new HashMap<String, LinkedList<Pair>>();
        eventOccurredCountMap         = new HashMap<String, Long>();
        eventDetectedCountMap         = new HashMap<String, Long>();
        cummulativeDeliveryLatencyMap = new HashMap<String, Long>();
        minimumDeliveryLatencyMap     = new HashMap<String, Long>();
        maximumDeliveryLatencyMap     = new HashMap<String, Long>();
    }
   
    
     /* *************************************************************************
     * Enabling mode allows the EventMonitor to record occurred/detected events
     * ONLY when certain conditions are met, which are notified through the 
     * enable()/disable() function
     * Technically, when disabled(), only the EventOccurrance are discarded, not 
     * the event detection, which could have been detected before the monitor
     * is disabled()
     * ************************************************************************ */
    public void enableMonitor(String key) {
        logger.debug("[DEBUG][EventMonitor].enableMonitor(" + key + ")");
        enabledMap.put(key, true);
    }
    
    public void disableMonitor(String key) {
        logger.debug("[DEBUG][EventMonitor].disableMonitor(" + key + ")");
        enabledMap.put(key, false);
    }
    
    private boolean isMonitorEnabled(String key) {
        if (!enabledMap.containsKey(key)) {
            enabledMap.put(key, true); // default is true/enabled
            return true;
        }
        return enabledMap.get(key);
    }

    public long removeEventOccurrenceTimeStamp(String key, long eventId) {
        if (eventMonitoringListMap.containsKey(key))
            for (Pair pair: eventMonitoringListMap.get(key))
                if (pair.eventId == eventId) {   
                    eventMonitoringListMap.get(key).remove(pair);
                    return pair.eventTimeStamp;
                }
        return -1;
    }
       
    
    private void updateCummulative(Map<String, Long> contorMap, String key, Long newValue) {
        if (!contorMap.containsKey(key))
            contorMap.put(key, new Long(newValue));
        else {
            long value = contorMap.get(key);
            contorMap.put(key, value + newValue);
        }
    }
    
    private void updateMin(Map<String, Long> contorMap, String key, Long newValue) {
        if (!contorMap.containsKey(key))
            contorMap.put(key, new Long(newValue));
        else
        {
            long minValue = contorMap.get(key);
            if (minValue > newValue)
                contorMap.put(key, newValue);
        }
    }
    
     private void updateMax(Map<String, Long> contorMap, String key, Long newValue) {
        if (!contorMap.containsKey(key))
            contorMap.put(key, new Long(newValue));
        else
        {
            long maxValue = contorMap.get(key);
            if (maxValue < newValue)
                contorMap.put(key, newValue);
        }
    }
    
    private void increment(Map<String, Long> contorMap, String key) {
        if (!contorMap.containsKey(key))
            contorMap.put(key, new Long(0));
        long value = contorMap.get(key);
        value ++;
        contorMap.put(key, new Long(value));            
    }
    
    public void resetEventLatencies() {
        //minimumDeliveryLatencyMap.clear(); TODO  
        //maximumDeliveryLatencyMap.clear(); TODO
        //cummulativeDeliveryLatencyMap.clear(); TODO
    }
    
    private void cleanUp(String key, long currentTimeStamp_ms) {
        LinkedList<Pair> list = eventMonitoringListMap.get(key);
        if (list == null)
            return;
        
        LinkedList<Pair> evictionList = new LinkedList<Pair>();
        
        for (Pair pair: list) {
            if (currentTimeStamp_ms - pair.eventTimeStamp > UNMATCHED_EVENTS_EVICTION_TIME )
                evictionList.add(pair);
            else
                break; // subsequent once are surely within the time limits because the list is time-ordered
        } 
        
        if (evictionList.size() > 0)
            logger.debug("[DEBUG][EventMonitor].cleanUp: eviction list.size() = " + evictionList.size() + "\n");
        
        for (Pair pair: evictionList)
            list.remove(pair);
    }

    public void markEventDetected(String key, long eventId, long eventDetectedTimeStamp) {
        logger.debug("[DEBUG][EventMonitor].markEventDetected(" + key + "," + eventId +")\n");
        if (!isMonitorEnabled(key))
            return;
        long eventOccurrenceTimeStamp = this.removeEventOccurrenceTimeStamp(key, eventId);
        if (eventOccurrenceTimeStamp != -1)
        {
            long latencyMS =  (eventDetectedTimeStamp - eventOccurrenceTimeStamp)/Constants.MILLI_SECOND;
            updateMin(minimumDeliveryLatencyMap, key, latencyMS);
            updateMax(maximumDeliveryLatencyMap, key, latencyMS);  
            updateCummulative(cummulativeDeliveryLatencyMap, key, latencyMS); 
            increment(eventDetectedCountMap, key);
        }           
    }
    
     // aka markPacketSent
    public void markEventOccurred(String key, long eventId, long eventTimeStamp) {
        logger.debug("[DEBUG][EventMonitor].markEventOccurred(" + key + "," + eventId +")");
        if (isMonitorEnabled(key)) {
            if (!eventMonitoringListMap.containsKey(key))
                eventMonitoringListMap.put(key , new LinkedList<Pair>());
            
                Pair newPair = new Pair(eventId, eventTimeStamp);
                if (!pairExists(key, eventMonitoringListMap.get(key), newPair)) {
                    eventMonitoringListMap.get(key).add(newPair);
                    increment(eventOccurredCountMap, key);
                }            
        }

        cleanUp(key, eventTimeStamp);
    }
    
    private boolean pairExists(String key, LinkedList<Pair> pairList, Pair pair) {
        if (pairList == null)
            return false;
        
        for (Pair pairItem: pairList) {
            if (pairItem.eventId == pair.eventId) {                                
                throw new RuntimeException("[ERROR] - Attempting to monitor an event (packet) (key = " + key +") with the same id (sequence) of an already existing monitoring event(" + pair.eventId +")\n" +
                                           "          Each event (packet) must be uniquelly identified (for a packet, you have a sequenceNumber)\n" + 
                                           "          This error happens when you attempt to mark an event (send a packet, for example) with an id (sequenceNumber)\n" +
                                           "          that a previous event (packet) has used. Are you having duplicate events (packets)? Check the id (sequenceNumber) of the marked events (sent packets)\n" +
                                           "          This error is reported on the event occurrance (packet sent) side, not on the event detect (packet received) side\n");
                //return true;                                
            }
        }
        return false;
    }
    
    public double eventMissedRatio(String key)
    {
        if (getEventOccurredCount(key) == 0)
            return 0.0;
        
        //logger.debug("DetectedCount(" + key + ") = " + getEventDetectedCount(key));
        //logger.debug("OccurredCount(" + key + ") = " + getEventOccurredCount(key));
        //logger.debug("MissedRatio  (" + key + ") = " + ((double)(getEventOccurredCount(key) - getEventDetectedCount(key)))/getEventOccurredCount(key));
        
        return ((double)(getEventOccurredCount(key) - getEventDetectedCount(key)))/getEventOccurredCount(key);
    }

    private class Pair
    {
        long eventId;
        long eventTimeStamp;

        public Pair(long eventId, long eventTimeStamp)
        {
            this.eventId = eventId;
            this.eventTimeStamp = eventTimeStamp;
        }
    }
    
    public long getNumberOfUnmatchedEvents(String key)
    {
        if (eventMonitoringListMap.get(key) == null)
            return 0;
        else
            return eventMonitoringListMap.get(key).size();
    }
       
    public long getEventOccurredCount(String key)
    {
        if (eventOccurredCountMap.containsKey(key))
            return eventOccurredCountMap.get(key);
        return 0;
    }
    
    public long getEventDetectedCount(String key)
    {
        if (eventDetectedCountMap.containsKey(key))
            return eventDetectedCountMap.get(key);
        return 0;
    }
    
    public long getMinimumDeliveryLatency(String key)
    {
        if (minimumDeliveryLatencyMap.containsKey(key))
            return minimumDeliveryLatencyMap.get(key);
        return 0;
    }
    
    public long getMaximumDeliveryLatency(String key)
    {
        if (maximumDeliveryLatencyMap.containsKey(key))
            return maximumDeliveryLatencyMap.get(key);
        return 0;
    }
    
    public long getAverageDeliveryLatency(String key)
    {
        if (cummulativeDeliveryLatencyMap.containsKey(key) &&
            eventDetectedCountMap.containsKey(key))
        {
            long sum   = cummulativeDeliveryLatencyMap.get(key);
            long count = eventDetectedCountMap.get(key);
            return sum/count;
        }
        return 0;
    }
   
    // TODO
    /* public boolean passesConditionalEvaluation(ITEM item, long currentValue)
    {
        boolean result = false;
        for (StatEntryDeprecated statEntry:monitoringList)
        {
            if (statEntry.isConditional() && statEntry.getMethod() == item)
            {
                switch(statEntry.getPredicate())
                {
                    case LESS_THAN:             result = currentValue <  statEntry.getThreshold(); break;
                    case LESS_THAN_OR_EQUAL:    result = currentValue <= statEntry.getThreshold(); break;
                    case EQUALS:                result = currentValue == statEntry.getThreshold(); break;
                    case GREATER_THAN_OR_EQUAL: result = currentValue >= statEntry.getThreshold(); break;
                    case GREATER_THAN:          result = currentValue >  statEntry.getThreshold();break;
                }
            }
        }
        return result;
    }
     * */
}