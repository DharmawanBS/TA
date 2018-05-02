/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

import jist.runtime.JistAPI;
import jist.swans.Constants;

/**
 *
 * @author Oliver
 */
public class Mac802_15_4Handler implements JistAPI.Entity/* Handler extends Handler */
{
    private Mac802_15_4 macEntity;
    private byte type;
    //private Event nullEvent;
    
    public Mac802_15_4Handler(Mac802_15_4 macEntity, byte tp)
    {
        //super.handler();
        this.macEntity = macEntity;
        type = tp;
        //nullEvent.uid_ = 0;
    }
    
    public void executeLater(/* Event e */ double delay /* seconds */)
    {
        JistAPI.sleepBlock((long)(delay * Constants.SECOND));
        
        //nullEvent.uid_ = 0;
	if (type == Mac802_15_4Impl.macTxBcnCmdDataHType)
            macEntity.txBcnCmdDataHandler();
	else if (type == Mac802_15_4Impl.macIFSHType)
            macEntity.IFSHandler();
	else if (type == Mac802_15_4Impl.macBackoffBoundType)
            macEntity.backoffBoundHandler();
	//else	
          //  assert(false);
    }
};
