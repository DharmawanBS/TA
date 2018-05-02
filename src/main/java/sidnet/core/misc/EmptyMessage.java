/*
 * EmptyMessage.java
 *
 * Created on November 4, 2005, 10:24 AM
 */

package sidnet.core.misc;

/**
 *
 * @author  Oliviu Ghica
 */
import jist.swans.misc.Message;

public class EmptyMessage implements Message
{   
    /** {@inheritDoc} */
    public int getSize() 
    { 
        return 0; 
    }
    /** {@inheritDoc} */
    public void getBytes(byte[] b, int offset)
    {
        throw new RuntimeException("not implemented");
    }
   
    /** Creates a new instance of EmptyMessage */
    public EmptyMessage() {
    }
    
} // class: EmptyMessage 
