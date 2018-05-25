/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.stack.users.dharmawan_route.driver;

/**
 *
 * @author invictus
 */
public class DeliveryRatioCounter {
    Long sended;
    Long received;

    public DeliveryRatioCounter() {
        this.sended = new Long(0);
        this.received = new Long(0);
    }

    public void incrementSended() {
        this.sended++;
    }

    public void messageReceived(long msgCounter) {
        this.received = this.received + msgCounter;
    }

    public String percentageDeliveryRatio() {
        double x = (received.doubleValue() / sended.doubleValue()) * 100;
        return String.format("%.2f", x);
    }

    public String getAllStatsString() {
        return "SEND:" + String.valueOf(this.sended) + " RECV:" + String.valueOf(this.received) + " RATIO:" + percentageDeliveryRatio() + "%";
    }

}