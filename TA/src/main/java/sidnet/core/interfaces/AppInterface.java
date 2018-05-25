/*
 * AppInterface.java
 *
 * Created on February 7, 2008, 3:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.interfaces;

import java.util.List;
import jist.runtime.JistAPI;
import jist.swans.mac.MacAddress;
import jist.swans.misc.Message;
import jist.swans.net.NetAddress;

/**
 *
 * @author Oliver
 */
public interface AppInterface extends JistAPI.Proxiable
{

  /**
   * Run application.
   */
  void run();

  /**
   * Run application.
   *
   * @param args command-line parameters
   */
  void run(String[] args);

  void sensing(List params);
  
    /**
     * Receive a message from network layer.
     *
     * @param msg message received
     * @param src source network address
     * @param lastHop source link address
     * @param macId incoming interface
     * @param dst destination network address
     * @param priority packet priority
     * @param ttl packet time-to-live
     */
    void receive(Message msg, NetAddress src, MacAddress lastHop, 
        byte macId, NetAddress dst, byte priority, byte ttl);
}
