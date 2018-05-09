/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sidnet.stack.users.dharmawan_route.routing;

/**
 *
 * @author dharmawan
 */
public class poolReceivedItem {
    public double dataValue;
    public String tipeSensor;
    public int fromRegion;
    public double maxValue;
    public double minValue;
    public double averageValue;
    public int priorityLevel;
    public int queryID;

    public poolReceivedItem() {
        this.dataValue = 0;
        this.maxValue = -9999;
        this.minValue = -9999;
        this.averageValue = -9999;
        this.priorityLevel = 0;
        this.queryID = 0;
    }

    public void putMaxValue (double MaxValue) {
        if (this.maxValue < MaxValue) {
            this.maxValue = MaxValue;
        }
        if (this.maxValue == -9999) {
            this.maxValue = MaxValue;
        }
    }

    public void putMinValue (double MinValue) {
        if (this.minValue > MinValue) {
            this.minValue = MinValue;
        }

        if (this.minValue == -9999) {
            this.minValue = MinValue;
        }
    }

    public void putAverageValue (double AverageValue) {
        if (this.averageValue == -9999) {
            this.averageValue = AverageValue;
        }
        else {
            double tmpAvg = this.averageValue + AverageValue;
            tmpAvg = tmpAvg / 2;
            this.averageValue = tmpAvg;
        }
    }
}