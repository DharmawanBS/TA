/*
 * TerminalCallbackInterface.java
 *
 * Created on October 25, 2007, 8:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.interfaces;

import sidnet.core.terminal.TerminalDataSet;

/**
 *
 * @author Oliver
 */
public interface TerminalCallbackInterface {
    public void dataExchange(TerminalDataSet terminalDataSet);
}
