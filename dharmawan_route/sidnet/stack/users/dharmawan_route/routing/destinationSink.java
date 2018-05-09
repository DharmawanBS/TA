/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sidnet.stack.users.dharmawan_route.routing;

import jist.swans.net.NetAddress;
import sidnet.core.misc.NCS_Location2D;

/**
 *
 * @author dharmawan
 */
//hashmap key:queryid item:sinkIP,sinkLocation
public class destinationSink {
    public NetAddress sinkIP;
    public NCS_Location2D sinkLocation;
    public int regionProcessed;

    public destinationSink (NetAddress sinkIP, NCS_Location2D sinkLocation, int regionProcessed) {
        this.sinkIP = sinkIP;
        this.sinkLocation = sinkLocation;
        this.regionProcessed = regionProcessed;
    }
}
