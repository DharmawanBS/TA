/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.core.interfaces;

/**
 *
 * @author Oliver
 */
public interface Payload extends jist.runtime.JistAPI.Timeless
{
 /**
   * Return packet size or Constants.ZERO_WIRE_SIZE.
   *
   * @return packet size [in bytes]
   */
  int getSize();

  /**
   * Store packet into byte array.
   *
   * @param msg destination byte array
   * @param offset byte array starting offset
   */
  void getBytes(byte[] msg, int offset);
}
