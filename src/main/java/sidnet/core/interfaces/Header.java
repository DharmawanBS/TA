/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.core.interfaces;

/**
 *
 * @author Oliver
 */
public interface Header extends jist.runtime.JistAPI.Timeless
{

  /**
   * Return header size or Constants.ZERO_WIRE_SIZE.
   *
   * @return header size [in bytes]
   */
  int getSize();

  /**
   * Store header into byte array.
   *
   * @param msg destination byte array
   * @param offset byte array starting offset
   */
  void getBytes(byte[] hdr, int offset);

} // interface Message
