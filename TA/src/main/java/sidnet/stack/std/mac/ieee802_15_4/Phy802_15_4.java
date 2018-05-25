/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

import jist.runtime.JistAPI;
import jist.swans.misc.Message;
import jist.swans.radio.RadioInterface;

/**
 *
 * @author Oliver
 */
public interface Phy802_15_4 extends RadioInterface{
  void transmit(Message msg);
  void PLME_GET_request(PPIBAenum PIBAttribute);
  void PLME_SET_TRX_STATE_request(PHYenum state);
  void PLME_SET_request(PPIBAenum PIBAttribute,PHY_PIB PIBAttributeValue);
  void PLME_ED_request();
  void sendOverHandler();
  public void cancelSendOverTimer();
  public void PLME_CCA_request();
  public void CCAHandler();
  public void EDHandler();
  public void TRXHandler();
  public void recvOverHandler(MacMessage_802_15_4 p);
  public void markWakes();
  public void markSleeps();
  void reset();
  public void setToSleepTimerPeriod(long toSleepTimerPeriod);
          
  
  boolean isSendOverTimerBusy() throws JistAPI.Continuation;
  boolean channelSupported(byte channel) throws JistAPI.Continuation;
  double trxTime(MacMessage_802_15_4 p, boolean phyPkt /* = false */) throws JistAPI.Continuation;
  double getRate_BitsPerSecond(char dataOrSymbol) throws JistAPI.Continuation;
  double getRate_BitsPerJistAtomicUnit(char dataOrSymbol) throws JistAPI.Continuation;
}
