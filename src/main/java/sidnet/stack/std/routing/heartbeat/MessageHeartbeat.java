/*
 * MessageHeartbeat.java
 *
 * Created on November 3, 2005, 10:20 AM
 */

/**
 *
 * @author  Oliviu Ghica
 */
/**
* Heartbeat packet.
*/
package sidnet.stack.std.routing.heartbeat;

import jist.swans.misc.Message;
import sidnet.core.misc.NCS_Location2D;

public class MessageHeartbeat implements Message
{
    public static final boolean UNREGISTER = true;
    
    private boolean unregister = false;
    
    private NCS_Location2D loc = null;
    
    public NCS_Location2D getNCS_Location(){ return loc;};
    public void setNCS_Location(NCS_Location2D loc){ this.loc = loc;};
    
    /** {@inheritDoc} */
    public int getSize() 
    { 
        return 8; 
    }
    
    /** {@inheritDoc} */
    public void getBytes(byte[] b, int offset)
    {
        throw new RuntimeException("not implemented");
    }
   
    /** Creates a new instance of MessageHeartbeat */
    public MessageHeartbeat(boolean unregister)
    {
        this.unregister = unregister;
    }
    
    public MessageHeartbeat() {
    }
    
    public boolean isUnregistering()
    {
        return unregister;
    }
    
} // class: MessageHeartbeat 
