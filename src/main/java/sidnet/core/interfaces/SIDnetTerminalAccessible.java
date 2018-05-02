/*
 * SIDnetTerminalAccessible.java
 *
 * Created on October 23, 2007, 8:28 PM
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
public interface SIDnetTerminalAccessible {
    public void dataExchange(TerminalDataSet terminalDataSet, NodeHardwareInterface hostNode, TerminalCallbackInterface hostNodeCallback);
    public boolean appendConsoleText(TerminalDataSet terminalDataSet, String s); // returns FALSE if the terminal is not currently open for the calling node
}
