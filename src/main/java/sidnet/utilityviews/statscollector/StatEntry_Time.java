/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.utilityviews.statscollector;

import jist.runtime.JistAPI;
import jist.swans.Constants;
import sidnet.core.misc.Node;

/**
 *
 * @author Oliver
 */
public class StatEntry_Time extends StatEntry{
    private static final String TAG = "Time";
    public enum TIMEBASE {HOURS, MINUTES};
    private TIMEBASE timebase;
    
    public StatEntry_Time()
    {
        this(TIMEBASE.HOURS); // default                
    }
    
    public StatEntry_Time(TIMEBASE timebase)
    {
        super("", TAG);        
        this.timebase = timebase;        
    }
        
   
    /**
     * @inheridoc
     */
    public String getValueAsString()
    {
        if (timebase == TIMEBASE.HOURS)
            return "" + Utils.roundOneDecimals(((double)JistAPI.getTime()) / Constants.HOUR);
        else
            return "" + (JistAPI.getTime()) / Constants.MINUTE;
    }
    
    public static String getFormatedTimeAsString(long currentTimeStamp)
    {
        String s = "";
        if (currentTimeStamp < 1000)
            s = s+0+" m . "+0+" s . "+(int)currentTimeStamp+" ms";
        if (currentTimeStamp >= 1000 && currentTimeStamp < 60*1000)
            s = s+0+" h . "+0+" m . "+(int)currentTimeStamp/1000+" s . "+(int)(currentTimeStamp-((int)currentTimeStamp/1000)*1000)+ " ms";
        if (currentTimeStamp >= 60*1000 && currentTimeStamp < 60*60*1000)
            s = s+0+" h . "+(int)currentTimeStamp/(60*1000)+" m . "+(int)(currentTimeStamp-((int)currentTimeStamp/(60*1000))*60*1000)/1000 + " s";
        if (currentTimeStamp >= 60*60*1000 )
            s = s+(int)currentTimeStamp/(60*60*1000)+" h. "+(int)(currentTimeStamp-((int)currentTimeStamp/(60*60*1000))*60*60*1000)/(60*1000)+" m";
        
        return s;
    }
    
    /**
     * @inheridoc
     */
   public void update(Node[] nodes)
   {
       // DO NOTHING
   }
}
