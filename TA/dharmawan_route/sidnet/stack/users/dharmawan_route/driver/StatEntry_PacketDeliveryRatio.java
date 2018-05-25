/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.stack.users.dharmawan_route.driver;

import sidnet.core.misc.Node;
import sidnet.utilityviews.statscollector.NodeBasedStatEntry;
import sidnet.utilityviews.statscollector.StatEntry;
import sidnet.utilityviews.statscollector.StatEntry_GeneralPurposeContor;

/**
 *
 * @author invictus
 */
public class StatEntry_PacketDeliveryRatio extends StatEntry implements NodeBasedStatEntry {
    protected double average = 0;
    private static final String TAG = "Contor";

    private StatEntry_GeneralPurposeContor sended;
    private StatEntry_GeneralPurposeContor receive;

    public StatEntry_PacketDeliveryRatio(String key, StatEntry_GeneralPurposeContor sendedCounter, StatEntry_GeneralPurposeContor receiveCounter) {
        super(key, TAG);
        this.sended = sendedCounter;
        this.receive = receiveCounter;
    }

    @Override
    public String getValueAsString() {
        return "" + String.format("%.2f", average) + "%";
    }

    public void update(Node[] nodes) {
        this.average = (this.receive.getValue() / this.sended.getValue()) * 100;
    }

}