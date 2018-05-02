/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

import jist.runtime.JistAPI;
import jist.swans.mac.MacInterface;
import jist.swans.misc.Message;

/**
 *
 * @author Oliver
 */
public interface Mac802_15_4 
extends MacInterface {
     public void PD_DATA_confirm(PHYenum status);
     public void PLME_CCA_confirm(PHYenum status);
     public void PLME_ED_confirm(PHYenum status, byte EnergyLevel);
     public void PLME_GET_confirm(PHYenum status,PPIBAenum PIBAttribute,PHY_PIB PIBAttributeValue);
     public void PLME_SET_TRX_STATE_confirm(PHYenum status);
     public void PLME_SET_confirm(PHYenum status,PPIBAenum PIBAttribute);
     public void receive(Message m );
     public void assoRspWaitHandler();
     public void beaconRxHandler();
     public void beaconSearchHandler();
     public void beaconTxHandler(boolean forTX);
     public void dataWaitHandler();
     public void extractHandler();
     public void rxEnableHandler();
     public void scanHandler();
     public void txOverHandler();
     public void txHandler();
     public void csmaca_backoffHandler();
     public void csmaca_bcnOtherHandler();
     public void csmaca_deferCCAHandler();
     public void startDevice();
     public void drop(MacMessage_802_15_4 p, String reason);
     
     // for Mac802_15_4Handler
     public void txBcnCmdDataHandler();
     public void IFSHandler();
     public void backoffBoundHandler();
     
     public int  get_sfSpec2_sd() throws JistAPI.Continuation;
     public byte get_sfSpec2_FinCAP() throws JistAPI.Continuation;
     public byte getMacBeaconOrder2()throws JistAPI.Continuation ;
     public double getMacBcnRxTime() throws JistAPI.Continuation;
     public byte getMpibMacBeaconOrder() throws JistAPI.Continuation; 
     public boolean sscs_neverAsso() throws JistAPI.Continuation;     
}
