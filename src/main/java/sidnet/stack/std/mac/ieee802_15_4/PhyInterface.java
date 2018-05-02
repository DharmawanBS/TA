/*
 * PhyInterface.java
 *
 * Created on July 15, 2008, 4:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

import jist.runtime.JistAPI;
import jist.swans.mac.MacAddress;
import jist.swans.misc.Message;

/**
 *
 * @author Oliver
 * Java adaptation after NS-2 C++ implementation
 */
/*
 * Copyright (c) 2003-2004 Samsung Advanced Institute of Technology and
 * The City University of New York. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *	This product includes software developed by the Joint Lab of Samsung 
 *      Advanced Institute of Technology and The City University of New York.
 * 4. Neither the name of Samsung Advanced Institute of Technology nor of 
 *    The City University of New York may be used to endorse or promote 
 *    products derived from this software without specific prior written 
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE JOINT LAB OF SAMSUNG ADVANCED INSTITUTE
 * OF TECHNOLOGY AND THE CITY UNIVERSITY OF NEW YORK ``AS IS'' AND ANY EXPRESS 
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN 
 * NO EVENT SHALL SAMSUNG ADVANCED INSTITUTE OR THE CITY UNIVERSITY OF NEW YORK 
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
public interface PhyInterface extends JistAPI.Proxiable
{

  //////////////////////////////////////////////////
  // from radio layer
  //

  /**
   * Update phy regarding new mode of its radio.
   *
   * @param mode new radio mode
   */
  void setRadioMode(byte mode);

  /**
   * Radio has locked onto a packet signal; phy may have a peek.
   *
   * @param msg packet currently in flight
   */
  void peek(Message msg);

  /**
   * Radio has received a packet for phy to process.
   *
   * @param msg packet received
   */
  void receive(Message msg);

  //////////////////////////////////////////////////
  // from network layer
  //

  /**
   * MAC layer would like to send the following packet. Should be called
   * only after Phy has notified that it is wants a packet.
   *
   * @param msg packet to send
   */
  void send(Message msg);


  //////////////////////////////////////////////////
  // 802.15.4 interface
  //

  /**
   * Extends the default Phy interface with 802_15_4 functions.
   *
   * @author OLIVER &lt;oliver@northwestern.edu&gt;
   * @since SIDnet 1.5 (SWANS 1.0)
   */
  public static interface Phy802_15_4 extends PhyInterface
  {
  
  }
}