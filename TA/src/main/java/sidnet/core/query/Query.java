/*
 * Query.java
 *
 * Created on November 4, 2005, 11:00 AM
 */

package sidnet.core.query;

import sidnet.core.misc.*;
import jist.swans.net.NetAddress;

public class Query {
    /** constants */
    public static final int NEVER     = -1; /** internal constants */
    public static final int ANYTIME   =  0; /** internal constants */
    
    /* SELECT clause arguments */
    public static final byte ALL       = 0; /** SELECT clause arguments */
    public static final byte AVG       = 1; /** SELECT clause arguments */
    public static final byte MIN       = 2; /** SELECT clause arguments */
    public static final byte MAX       = 3; /** SELECT clause arguments */
    
    /** Query Parameters */
    private int id;                    /** Unique query identifier */
    private NetAddress sinkIP;         /** The IP of the node through which the query has been posted and which aggregates the results */
    private NCS_Location2D sinkNCS_2D; /** The Location of the node through which the query has been posted and which aggregates the results */
    private int destID;                /** The ID of the node through which the query has been posted and which aggregates the results */
    private NetAddress destIP;         /** The IP of the node through which the query has been posted and which aggregates the results */
    private byte aggregationType;      /** SELECT clause argument: (ALL), average(AVG), min(MIN), max(MAX) */
    private long samplingInterval;     /** in [atomic times units]. The SI clause */
    private long startTime = -1;       /** in [atomic times units]. Indicates the time at which the query has been posted. */
    private long endTime   = -1;       /** in [atomic times units]. Indicates the simulation time the query becomes obsolete. Comes from the FOR clause*/
    
    /** WHERE Clause */
    private WhereClause whereClause = null;
    
    private Region region;             /** The bounds of the area that defines the scope of the query / it can be a single point*/
    private boolean dispatched = false;/** Keep track weather the query has been dispatched or not */
    
    /** internals */
    private static int id_pool    = 0; /** The generator of id's */
    
    public Query()
    {
        ;
    }
    
    // copy constructor
    public Query(Query query)
    {
        this.id               = query.id;
        this.sinkIP           = query.sinkIP;
        this.sinkNCS_2D       = query.sinkNCS_2D;
        this.destID           = query.destID;
        this.destIP           = query.destIP;
        this.aggregationType  = query.aggregationType;
        this.samplingInterval = query.samplingInterval;
        this.startTime        = query.startTime;
        this.endTime          = query.endTime;
        this.whereClause      = query.whereClause;
        this.region           = new Region(query.region);
        this.dispatched       = query.dispatched;
        this.id_pool          = query.id_pool; 
    }
    
    /** Creates a new instance of Query */
    public Query(NetAddress sinkIP, NCS_Location2D sinkNCS_2D, NetAddress destIP, byte aggregationType, long samplingInterval, long startTime, long endTime, Region region, WhereClause whereClause) {
        id                    = getNextID();
        this.sinkIP           = sinkIP;
        this.sinkNCS_2D       = sinkNCS_2D;
        this.aggregationType  = aggregationType;
        this.samplingInterval = samplingInterval;
        this.startTime        = startTime;
        this.endTime          = endTime;
        this.destIP           = destIP;
        this.region           = region;
        this.whereClause      = whereClause;
        
         if (startTime > endTime)
            throw new IllegalArgumentException("[Query] - Invalid Query time setup!");
    }
    
    
        public Query(int queryID, NetAddress sinkIP, NCS_Location2D sinkNCS_2D, NetAddress destIP, byte aggregationType, long samplingInterval, long startTime, long endTime, Region region, WhereClause whereClause) {
        this.id               = queryID;    
        this.sinkIP           = sinkIP;
        this.sinkNCS_2D       = sinkNCS_2D;
        this.aggregationType  = aggregationType;
        this.samplingInterval = samplingInterval;
        this.startTime        = startTime;
        this.endTime          = endTime;
        this.destIP           = destIP;
        this.region           = region;
        this.whereClause      = whereClause;
        
        if (startTime > endTime)
            throw new IllegalArgumentException("[Query] - Invalid Query time setup!");
    }
        
    public void setSamplingInterval(long samplingInterval)
    {
        this.samplingInterval = samplingInterval;
    }
        
    public long getSamplingInterval()
    {
        return samplingInterval;
    }
    
    public void setAggregationType(byte aggregationType)
    {
        this.aggregationType = aggregationType;
    }
    
    /** SELECT clause argument: ALL, MIN, MAX, AVG */
    public byte getAggregationType()
    {
        return aggregationType;
    }
    
    public void setStartTime(long startTime)
    {
        if (startTime != -1 && endTime != -1 && startTime > endTime)
            throw new IllegalArgumentException("[Query] - Invalid Query time setup!");
        this.startTime = startTime;
    }
    
    public long getStartTime()
    {
        return startTime;
    }
    
    public void setEndTime(long endTime)
    {
        if (startTime != -1 && endTime != -1 && startTime > endTime)
            throw new IllegalArgumentException("[Query] - Invalid Query time setup!");
        this.endTime = endTime;
    }
    
    public long getEndTime()
    {
        return endTime;
    }
    
    public WhereClause getWhereClause()
    {
        return whereClause;
    }
    
    public boolean isDispatched()
    {
        return dispatched;
    }
    
    public void dispatched(boolean val)
    {
        dispatched = val;
    }
    
    public void setRegion(Region region)
    {
        this.region = region;
    }
    
    public Region getRegion()
    {
        return region;
    }

   
    
    public void setID(int id) /** set Query unique ID */
    {
        this.id = id;
    }
    
    public int getID() /** get Query unique ID */
    {
        return id;
    }
   
    public void setSinkIP(NetAddress sinkIP)
    {
        this.sinkIP = sinkIP;
    }
    
    public NetAddress getSinkIP()
    {
        return sinkIP;
    }
    
    public void setSinkNCSLocation2D(NCS_Location2D sinkNCS_2D)
    {
        this.sinkNCS_2D = sinkNCS_2D;
    }
    
    public NCS_Location2D getSinkNCSLocation2D()
    {
        return sinkNCS_2D;
    }
    
     public NetAddress getDestIP()
    {
        return destIP;
    }
    
    
   
    
    /* Private members */
    private int getNextID()
    {
        id_pool++;
        return id_pool;
    }
    
    public int getAsMessageSize()
    {
        int size = 0;
        size += 2; // int           id;
        size += 4; // NetAddress.Ip sinkIp;
        size += 4; // Location2D    sinkLoc (x, y)
        size += 4; // NetAddress.Ip sourceIp;
        size += 4; // Location2D    sourceLoc (x, y)
        size += 1; // byte          aggregationType;
        size += 4; // long          samplingInterval;
        size += 4; // long          startTime;
        size += 4; // long          endTime;
        size += region.getAsMessageSize();  // Region region;
        size += 1; // boolean       dispatched  
        return size;
    }
    
    
}
