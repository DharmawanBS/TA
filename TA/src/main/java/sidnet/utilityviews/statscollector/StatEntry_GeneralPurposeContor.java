/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.utilityviews.statscollector;

/**
 *
 * @author Oliver
 */
public class StatEntry_GeneralPurposeContor 
extends IncrementableStatEntry{
    private static final String TAG = "Contor";

    public StatEntry_GeneralPurposeContor(String key) {
        super(key, TAG);
    }
              
    /**
     * @inheridoc
     */
    public String getValueAsString() {
        return "" + value;
    }
}
