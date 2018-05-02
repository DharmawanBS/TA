/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.utilityviews.statscollector;

import java.text.DecimalFormat;

/**
 *
 * @author Oliver
 */
public class Utils {
    static double roundTwoDecimals(double d) {
                    DecimalFormat twoDForm = new DecimalFormat("#.##");
                    if (!new Double(d).equals(Double.NaN))
                        return Double.valueOf(twoDForm.format(d));
                    else 
                        return d;
    }
    
    static double roundOneDecimals(double d) {
                    DecimalFormat twoDForm = new DecimalFormat("#.#");
                    if (!new Double(d).equals(Double.NaN))
                        return Double.valueOf(twoDForm.format(d));
                    else 
                        return d;
    }
}
