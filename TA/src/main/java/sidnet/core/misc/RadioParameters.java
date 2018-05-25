/*
 * RadioParameters.java
 *
 * Created on May 14, 2006, 10:34 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.misc;

/**
 *
 * @author Oliviu Ghica
 */
public class RadioParameters {
    private int dataRate;
    private int packetLength; //[bits]
    private int transmissionTime; // [ms]
    
    /** Creates a new instance of RadioParameters */
    public RadioParameters(int dataRate, int packetLength) {
        this.dataRate = dataRate;
        this.packetLength = packetLength;
        transmissionTime = 1000 * packetLength / dataRate;
    }
    
    public int getDataRate()
    {
        return dataRate;
    }
    
    public int getPacketLength()
    {
        return packetLength;
    }
    
    public long getTransmissionTime()  // in [ms]
    {
        return transmissionTime;
    }
    
    public long getTransmissionTime(int packetSize)
    {
        return 1000 * packetSize / dataRate;
    }
}
