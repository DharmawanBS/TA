/*
 * Phy802_15_4Impl.java
 *
 * Created on July 1, 2008, 11:20 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import jist.runtime.JistAPI; 
import jist.swans.Constants;
import jist.swans.mac.MacAddress;
import jist.swans.field.FieldInterface;
import jist.swans.misc.Message;
import jist.swans.radio.RadioInfo;
import sidnet.core.misc.Node;
import sidnet.models.energy.energyconsumptionmodels.EnergyConsumptionModel;
import sidnet.models.energy.energyconsumptionmodels.EnergyManagement;

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
//PHY PIB attributes (Table 19)
enum PPIBenum
{
    phyCurrentChannel, /* = 0x00; */
    phyChannelsSupported,
    phyTransmitPower,
    phyCCAMode
}













public class Phy802_15_4Impl 
implements Phy802_15_4 {/*implements PhyInterface.Phy802_15_4/* : public WirelessPhy */
    //Non members in NS-2
    public final static int phyCCAHType = 1;
    public final static int phyEDHType  = 2;
    public final static int phyTRXHType = 3;
    public final static int phyRecvOverHType = 4;
    public final static int phySendOverHType = 5;
    
    private long lastTimestampOfPacketForThisNode;
    
    private EnergyManagement energyManagementUnit;
    private EnergyConsumptionModel energyConsumptionModel;  
    // ---
    
    private PHY_PIB ppib;
    private PHYenum trx_state;		//tranceiver state: TRX_OFF/TX_ON/RX_ON
    private PHYenum trx_state_defer_set;	//defer setting tranceiver state: TX_ON/RX_ON/TRX_OFF/IDLE (IDLE = no defer pending)
    private PHYenum trx_state_turnaround;	//defer setting tranceiver state in case Tx2Rx or Rx2Tx
    private PHYenum tx_state;		//transmitting state: IDLE/BUSY
    private MacMessage_802_15_4 rxPkt;	//the packet meets the following conditions:
					// -- on the current channel
					// -- for this node (not interference)
					// -- with the strongest receiving power among all packets that are for this node and on the current channel
    private MacMessage_802_15_4 txPkt;			//the packet being transmitted
    private MacMessage_802_15_4 txPktCopy;		//the copy of the packet being transmitted
    private double[] rxTotPower = new double[27];		
    private double rxEDPeakPower;		
    private int[] rxTotNum = new int[27];		
    private int[] rxThisTotNum = new int[27];	
    private TimerInterface802_15_4 CCAH;         //private Phy802_15_4Timer CCAH;
    private TimerInterface802_15_4 EDH;          //private Phy802_15_4Timer EDH;
    private TimerInterface802_15_4 TRXH;         //private Phy802_15_4Timer TRXH;
    private TimerInterface802_15_4 recvOverH;    //private Phy802_15_4Timer recvOverH;
    private TimerInterface802_15_4 sendOverH;    //protected Phy802_15_4Timer sendOverH;
    private TimerInterface802_15_4 toSleepTimer; //protected ToSleepTimer toSleepTimer;
    private TimerInterface802_15_4 toSleepTimer2;
    
    /** Not in NS-2: mac address of this interface. */
    protected MacAddress localAddr;
    
    private static byte customDataRate_kbps = -1; // kbps
    
    /* START ::: OSI Stack Hookup methods +++++++++++++++++++ */
   
    // entity hookup
    /** MAC upcall entity reference */
    private Mac802_15_4 macEntity;
    
    /** Self-referencing mac entity reference. */
    private Phy802_15_4 self;
    
    /** radio properties */
    protected RadioInfo radioInfo;
    
    /** Field downcall entity reference. */
    private FieldInterface fieldEntity;
    
    private long toSleepTimerPeriod;
    private ColorProfile802_15_4 colorProfile802_15_4 = new ColorProfile802_15_4();
    private Node myNode;
    
    static Logger pdLogger, plmeLogger;
    static {
    	pdLogger = Logger.getLogger("Phy802_15_4.pdLogger");
    	pdLogger.setLevel(Level.INFO);
    	pdLogger.setAdditivity(false);
    	pdLogger.addAppender(new ConsoleAppender(new PatternLayout("\t\t\t%-5p %c *** %m \n")));
    	
    	plmeLogger = Logger.getLogger("Phy802_15_4.plmeLogger");
    	plmeLogger.setLevel(Level.INFO);
    	plmeLogger.setAdditivity(false);
    	plmeLogger.addAppender(new ConsoleAppender(new PatternLayout("\t\t\t%-5p %c *** %m \n")));
    }
    
    /**
     * Hook up with the mac entity.
     *
     * @param mac mac entity
     */
    public void setMacEntity(Mac802_15_4 mac)
    {
        if(!JistAPI.isEntity(mac)) throw new IllegalArgumentException("expected entity");
        this.macEntity  =  mac;
    }
    
    /**
     * Return proxy entity of this phy layer.
     *
     * @return self-referencing proxy entity.
     */
    public Phy802_15_4 getProxy()
    {
        return self;
    }
    
    
    /**
     * Hook up with the field entity.
     *
     * @param field field entity
     */
    public void setFieldEntity(FieldInterface field) {
        if(!JistAPI.isEntity(field)) throw new IllegalArgumentException("expected entity");
        this.fieldEntity = field;
    }
     /* END ::: OSI Stack Hookup methods +++++++++++++++++++ */

    public Phy802_15_4Impl(int id,
    					   RadioInfo.RadioInfoShared sharedInfo,
    					   EnergyManagement energyManagementUnit,
    					   Node myNode,
    					   long toSleepTimerPeriod) {
        this(new PHY_PIB(), id, sharedInfo, energyManagementUnit, myNode, toSleepTimerPeriod);
    }
    
    public boolean isSendOverTimerBusy() 
    throws JistAPI.Continuation {
        return sendOverH.bussy();
    }
    
    public void cancelSendOverTimer() {
        sendOverH.cancel();
        energyConsumptionModel.simulatePacketEndsTransmitting();
    }
    
    public void setToSleepTimerPeriod(long toSleepTimerPeriod) {
        this.toSleepTimerPeriod = toSleepTimerPeriod;
        toSleepTimer2.stopTimerr();
        toSleepTimer2 = new ToSleepTimer2(localAddr.hashCode(), ((double)toSleepTimerPeriod)/Constants.SECOND, self).getProxy();
        energyConsumptionModel.simulateRadioWakes();
    }
    
    public void reset() {
        
    }
    
    public Phy802_15_4Impl(PHY_PIB pp, int id, RadioInfo.RadioInfoShared sharedInfo, EnergyManagement energyManagementUnit, Node myNode, long toSleepTimerPeriod) {
         // proxy
        self = (Phy802_15_4)JistAPI.proxy(this, Phy802_15_4.class);
        
        this.myNode = myNode;
        
        radioInfo = new RadioInfo(new RadioInfo.RadioInfoUnique(id), sharedInfo);   
        if (radioInfo.getShared().getBandwidth() > 0)
        	customDataRate_kbps = (byte) (radioInfo.getShared().getBandwidth()/1024); // bps to kbps
        
        this.toSleepTimerPeriod = toSleepTimerPeriod;
        
        int i;
		ppib = pp;
		trx_state = PHYenum.p_RX_ON;
		trx_state_defer_set = PHYenum.p_IDLE;
		tx_state = PHYenum.p_IDLE;
		rxPkt = null;
		for (i=0;i<27;i++) {
			rxTotPower[i] = 0.0;
			rxTotNum[i] = 0;
			rxThisTotNum[i] = 0;
		}
        
        this.energyManagementUnit = energyManagementUnit;
        if (energyManagementUnit != null)
            this.energyConsumptionModel = energyManagementUnit.getEnergyConsumptionModel();
        
        this.localAddr = new MacAddress(id);
        
        if (energyConsumptionModel != null)
            energyConsumptionModel.setCPUDutyCycle(5);
        
        toSleepTimer = new ToSleepTimer(id, ((double)toSleepTimerPeriod)/Constants.SECOND, this.energyConsumptionModel).getProxy();
        toSleepTimer2 = new ToSleepTimer2(id, ((double)toSleepTimerPeriod)/Constants.SECOND, self).getProxy();
        CCAH = new Phy802_15_4Timer(self, phyCCAHType, id, null, null).getProxy();
        EDH  = new Phy802_15_4Timer(self, phyEDHType, id, null, null).getProxy();
        TRXH = new Phy802_15_4Timer(self, phyTRXHType, id, null, null).getProxy();
        //recvOverH = new Phy802_15_4Timer(self, phyRecvOverHType, id, energyManagementUnit, toSleepTimer2).getProxy();
        recvOverH = new WrapperPhy802_15_4Timer(self, phyRecvOverHType, id, energyManagementUnit, toSleepTimer2).getProxy();
        sendOverH = new Phy802_15_4Timer(self, phySendOverHType, id, energyManagementUnit, toSleepTimer2).getProxy();                
    }
    
    
     public void markWakes()
    {
        if (toSleepTimerPeriod != 0)
        { 
           // if (localAddr.hashCode() == 588 || localAddr.hashCode() == 748)
             //   System.out.println("#" + localAddr.hashCode() + ": PHY.markWakes()");
           
            myNode.getNodeGUI().colorCode.mark(colorProfile802_15_4, ColorProfile802_15_4.RADIO_SLEEPS, ColorProfile802_15_4.CLEAR);
            //myNode.getNodeGUI().repaint();
            if (toSleepTimer2 != null)
                toSleepTimer2.cancel();
            energyManagementUnit.getEnergyConsumptionModel().simulateRadioWakes();
            energyManagementUnit.getEnergyConsumptionModel().setCPUDutyCycle(100);
            toSleepTimer2.resetTimer();
            //toSleepTimer2 = new ToSleepTimer2(localAddr.hashCode(), ((double)toSleepTimerPeriod)/Constants.SECOND/*s*/, self).getProxy();                        
            //toSleepTimer2.cancel();
            toSleepTimer2.start();            
        }
    }
    
    public void markSleeps()
    {
        //if (localAddr.hashCode() == 588 || localAddr.hashCode() == 748)
          //  System.out.println("#" + localAddr.hashCode() + ": PHY.markSleeps()");
        myNode.getNodeGUI().colorCode.mark(colorProfile802_15_4, ColorProfile802_15_4.RADIO_SLEEPS, ColorProfile802_15_4.FOREVER);
        //myNode.getNodeGUI().repaint();
        energyManagementUnit.getEnergyConsumptionModel().simulateRadioGoesToSleep();
        energyManagementUnit.getEnergyConsumptionModel().setCPUDutyCycle(5);
        toSleepTimer.cancel();
        
    }        
    
    
    // Not in NS-2. Part of the RadioInterface
    public void setSleepMode(boolean sleep)
    {
        //setMode(sleep ? Constants.RADIO_MODE_SLEEP : Constants.RADIO_MODE_IDLE);
        trx_state = sleep ? PHYenum.p_TRX_OFF : PHYenum.p_RX_ON; // ??? p_RX_ON or p_TX_ON
//        if (sleep && energyConsumptionModel != null)
//            energyConsumptionModel.simulateRadioGoesToSleep();
//        else
//            energyConsumptionModel.simulateRadioWakes();
    }
    
    public RadioInfo getRadioInfo() {
        return radioInfo;
    }
    
    
    public boolean channelSupported(byte channel) throws JistAPI.Continuation
    {
        return ((ppib.phyChannelsSupported & (1 << channel)) != 0);
    }
    
    public double getRate_BitsPerJistAtomicUnit(char dataOrSymbol) 
    throws JistAPI.Continuation {
    	return getRate_BitsPerSecond(dataOrSymbol) / Constants.SECOND;
    }
    
    public double getRate_BitsPerSecond(char dataOrSymbol) 
    throws JistAPI.Continuation {/* bits/s */
        double rate = 0.0;
	
        if (customDataRate_kbps != -1)
        	rate = customDataRate_kbps;
        else
			if (ppib.phyCurrentChannel == 0) {
				if (dataOrSymbol == 'd')
					rate = Def.BR_868M;
				else
					rate = Def.SR_868M;
			} else if (ppib.phyCurrentChannel <= 10) {
				if (dataOrSymbol == 'd')
					rate = Def.BR_915M;
				else
					rate = Def.SR_915M;
			} else {
				if (dataOrSymbol == 'd')
					rate = Def.BR_2_4G;
				else
					rate = Def.SR_2_4G;
			}
        
	    return (double)((rate * 1000)); // kbps to bits/second
    }
    
    public double trxTime(MacMessage_802_15_4 p, boolean phyPkt /* = false */) throws JistAPI.Continuation// expressed in seconds
    {
        int phyHeaderLen;
	double trx = 0.0;
	hdr_cmn ch = p.HDR_CMN();
	
	phyHeaderLen = (phyPkt) ? 0 : Def.defPHY_HEADER_LEN;
	trx = (double)((ch.size() + phyHeaderLen) * 8 /* bytes to bits */ / getRate_BitsPerSecond('d'));
        if (Def.DEBUG802_15_4_packetsize && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
        {
            System.out.println("[" + JistAPI.getTime() + "][#" + localAddr + "][Phy802_15_4.trxTime] - PHY header size (bytes): " + phyHeaderLen);
            System.out.println("[" + JistAPI.getTime() + "][#" + localAddr + "][Phy802_15_4.trxTime] - TOTAL (MAC+PHY+payload) packet size (bytes): " + (ch.size() + phyHeaderLen));
        }
        if (Def.DEBUG802_15_4_transmissiontime && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
        {
            System.out.println("transmission rate bits/second " + getRate_BitsPerSecond('d'));
            System.out.println("trx(transmission time required to send the packet of size " + ch.size() + " is = " + trx);
        }
	return trx;
    }
    
    public void construct_PPDU(int psduLength,MacMessage_802_15_4 psdu) {
        //not really a new packet in simulation, but just update some packet header fields.
		hdr_lrwpan wph = psdu.HDR_LRWPAN();
		hdr_cmn ch = psdu.HDR_CMN();
		
		wph.SHR_PreSeq = Def.defSHR_PreSeq;
		wph.SHR_SFD = Def.defSHR_SFD;
		wph.PHR_FrmLen = psduLength;
		//also set channel (for filtering in simulation)
		wph.phyCurrentChannel = ppib.phyCurrentChannel;
		ch.setSize(psduLength + Def.defPHY_HEADER_LEN);
		ch.setTxtime(trxTime(psdu,true));
    }
    
    public void PD_DATA_request(int psduLength, MacMessage_802_15_4 psdu) {

    	pdLogger.debug("[" +JistAPI.getTime()/Constants.MILLI_SECOND + "ms]["+localAddr+"][PHY.PD_DATA_request()]");
        
        hdr_cmn ch = psdu.HDR_CMN();

		// check packet length; should be smallest than the maximum admissible by MAC802_15_4 standard.
		if (psduLength > Const.aMaxPHYPacketSize) {
	        System.out.println("[" + JistAPI.getTime() + "]<PHY>(node" + localAddr + ") Invalid PSDU/MPDU length - packet size exceeds maximum allowed physical frame size of " + Const.aMaxPHYPacketSize + " bytes; dropping; type = " + Trace.wpan_pName(psdu) +", src = " + Trace.p802_15_4macSA(psdu) + ", dst = " + Trace.p802_15_4macDA(psdu) + ", uid = " + ch.uid() + ", mac_uid = " + psdu.HDR_LRWPAN().uid + ", size = " + ch.size());
	        macEntity.PD_DATA_confirm(PHYenum.p_UNDEFINED);
	        return;
		}
	
		if (trx_state == PHYenum.p_TX_ON) {
			if (tx_state == PHYenum.p_IDLE) {
                if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                    System.out.println("[" + JistAPI.getTime() + "]<PHY>(node" + localAddr + ") sending pkt: type = " + Trace.wpan_pName(psdu) +", src = " + Trace.p802_15_4macSA(psdu) + ", dst = " + Trace.p802_15_4macDA(psdu) + ", uid = " + ch.uid() + ", mac_uid = " + psdu.HDR_LRWPAN().uid + ", size = " + ch.size());
            
                //construct a PPDU packet (not really a new packet in simulation, but still <psdu>)
                construct_PPDU(psduLength,psdu);
                //somehow the packet size is set to 0 after sendDown() -- ok, the packet is out and anything can happen (we shouldn't care once it's out)
                //so we have to calculate the transmission time before sendDown()
                double trx_time = trxTime(psdu,true);
                //send the packet to Radio (channel target) for transmission
                txPkt = psdu;
                txPktCopy = psdu.copy();	//for debug purpose, we still want to access the packet after transmission is done
                
                //sendDown(psdu);			//WirelessPhy::sendDown()
                psdu.HDR_CMN().direction_ = hdr_cmn.dir_t.UP;  // Not in NS-2
                //energyConsumptionModel.simulateRadioWakes();
                //energyConsumptionModel.simulatePacketStartsTransmitting();
                fieldEntity.transmit(radioInfo, psdu, (long)(trx_time * Constants.SECOND));
                //System.out.println("trx_time = " + trx_time);
                
                tx_state = PHYenum.p_BUSY;		//for carrier sense
                
                energyConsumptionModel.simulateRadioWakes();
                energyConsumptionModel.simulatePacketStartsTransmitting();  
                energyManagementUnit.getEnergyConsumptionModel().setCPUDutyCycle(100);
                //if (sendOverH.bussy())
                  //  sendOverH = new Phy802_15_4Timer(self, phySendOverHType, localAddr.hashCode(), energyManagementUnit, toSleepTimer2).getProxy();
                sendOverH.startTimer(trx_time);
			}
			else	//impossible
				assert (false);	            
		}
		else {
            if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                System.out.println("[D][TRX][" + /* __FILE__ + */ " :: " + /* __FUNCTION__ + */ "][" + JistAPI.getTime() + "]<PHY>(node" + localAddr + ") dropping pkt: type = " + Trace.wpan_pName(psdu) +", src = " + Trace.p802_15_4macSA(psdu) + ", dst = " + Trace.p802_15_4macDA(psdu) + ", uid = " + ch.uid() + ", mac_uid = " + psdu.HDR_LRWPAN().uid + ", size = " + ch.size());
	
			macEntity.PD_DATA_confirm(trx_state);
		}
    }
    
    public void PD_DATA_indication(byte psduLength,MacMessage_802_15_4 psdu,byte ppduLinkQuality)
    {
        //refer to sec 6.7.8 for LQI details
	hdr_lrwpan wph = psdu.HDR_LRWPAN();

	wph.ppduLinkQuality = ppduLinkQuality;

	if (sendUp(psdu) == 0)
        {
            psdu = null;
        }
	//else // ???
            //uptarget_.recv(psdu, (Handler) 0);
            //macEntity.receive(psdu); //???
    }
    
    public void PLME_CCA_request() {
    	        
        plmeLogger.debug("[" +JistAPI.getTime()/Constants.MILLI_SECOND + "]["+localAddr+"][PHY.PLME_CCA_request()]");
         
        if (trx_state == PHYenum.p_RX_ON) {
			//perform CCA
			//refer to sec 6.7.9 for CCA details
			//we need to delay 8 symbols
			CCAH.startTimer(8/getRate_BitsPerSecond('s'));
		}
		else
			macEntity.PLME_CCA_confirm(trx_state);
    }
    
    public void PLME_ED_request()
    {
        if (trx_state == PHYenum.p_RX_ON)
	{
		//perform ED
		//refer to sec 6.7.7 for ED implementation details
		//we need to delay 8 symbols
		rxEDPeakPower = rxTotPower[ppib.phyCurrentChannel];
		EDH.startTimer(8/getRate_BitsPerSecond('s'));
	}
	else
		macEntity.PLME_ED_confirm(trx_state, (byte)0);
    }
    
    public void PLME_GET_request(PPIBAenum PIBAttribute)
    {
        PHYenum t_status;
	
	switch(PIBAttribute)
	{
		case phyCurrentChannel:
		case phyChannelsSupported:
		case phyTransmitPower:
		case phyCCAMode:
			t_status = PHYenum.p_SUCCESS;
			break;
		default:
			t_status = PHYenum.p_UNSUPPORT_ATTRIBUTE;
			break;
	}
	macEntity.PLME_GET_confirm(t_status,PIBAttribute,/* & */ppib);
    }
    
    public void PLME_SET_TRX_STATE_request(PHYenum state)
    {
        boolean delay;
	PHYenum t_status;
        
    	if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
    		System.out.println("[" + JistAPI.getTime() + "][PHY.PLME_SET_TRX_STATE_request()]");
	
	//ignore any pending request
	if (trx_state_defer_set != PHYenum.p_IDLE)
        {
		trx_state_defer_set = PHYenum.p_IDLE;
                //energyConsumptionModel.simulateRadioForcedToIdle();
        }
	else if (TRXH.bussy())
	{
		TRXH.cancel();
	}

	t_status = trx_state;
	if (state != trx_state)
	{
		delay = false;
		if (((state == PHYenum.p_RX_ON)||(state == PHYenum.p_TRX_OFF))&&(tx_state == PHYenum.p_BUSY))
		{
			t_status = PHYenum.p_BUSY_TX;
			trx_state_defer_set = state;
		}
    		// Rey: according to specification (IEEE Std 802.15.4-2006,p40) only TRX_OFF will defer the state change on successful SFD reception, TX_ON on the other hand will override
    		// this is obviously a known bug in the ns-2 implementation (see http://mailman.isi.edu/pipermail/ns-users/2007-October/061509.html)
    		//else if (((state == PHYenum.p_TX_ON)||(state == PHYenum.p_TRX_OFF))
    		else if ((state == PHYenum.p_TRX_OFF))
    		//&&(rxPkt != null)&&(rxPkt.HDR_CMN().error() == 0 ))			//if after received a valid SFD
    		{
    			t_status = PHYenum.p_BUSY_RX;
    			trx_state_defer_set = state;
    		}
    		// Rey: added correct TX_ON handling here
    		else if ((state == PHYenum.p_TX_ON))
    		{
    			t_status = (trx_state == PHYenum.p_TX_ON) ? PHYenum.p_TX_ON : PHYenum.p_SUCCESS;
    			//t_status = PHYenum.p_SUCCESS;
   			trx_state = PHYenum.p_TX_ON;
                        //energyConsumptionModel.simulateRadioWakes();
                        //energyConsumptionModel.simulatePacketStartsTransmitting();
    			//terminate reception if needed
   				
    			if (rxPkt != null)
    			{   
    				if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
    				{ 
    					hdr_cmn ch = rxPkt.HDR_CMN();
    					hdr_lrwpan wph = rxPkt.HDR_LRWPAN();
    					System.out.println("[" + JistAPI.getTime() + "]<PHY>(node" + localAddr + ") TX_ON sets error bit for incoming pkt: type = " + Trace.wpan_pName(rxPkt) +", src = " + Trace.p802_15_4macSA(rxPkt) + ", dst = " + Trace.p802_15_4macDA(rxPkt) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());
    				}
    				rxPkt.HDR_CMN().setError(1);	//incomplete reception -- force packet discard
    				
    			}
    			//terminate transmission if needed
    			if (tx_state == PHYenum.p_BUSY)
    			{
                            
    				if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
    				{ 
    					hdr_cmn ch = txPkt.HDR_CMN();
    					hdr_lrwpan wph = txPkt.HDR_LRWPAN();
    					System.out.println("[" + JistAPI.getTime() + "]<PHY>(node" + localAddr + ") TX_ON sets error bit for outgoing pkt: type = " + Trace.wpan_pName(txPkt) +", src = " + Trace.p802_15_4macSA(txPkt) + ", dst = " + Trace.p802_15_4macDA(txPkt) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());
    				}

    				txPkt.HDR_CMN().setError(1);
    				sendOverH.cancel();
                                energyConsumptionModel.simulatePacketEndsTransmitting();
                                
    				//MacMessage_802_15_4::free(txPktCopy);
    				tx_state = PHYenum.p_IDLE;
    				macEntity.PD_DATA_confirm(PHYenum.p_TX_ON);
                                //energyConsumptionModel.simulateRadioGoesToSleep();
    				if (trx_state_defer_set != PHYenum.p_IDLE)
    					trx_state_defer_set = PHYenum.p_IDLE;
                               
    			}

    		}
    		
    		/*
    		else if (((state == PHYenum.p_TX_ON)||(state == PHYenum.p_TRX_OFF))
        		&&(rxPkt != null)&&(rxPkt.HDR_CMN().error() == 0  no error ))			//if after received a valid SFD
        		{
        			t_status = PHYenum.p_BUSY_RX;
        			trx_state_defer_set = state;
        		}*/
		else if (state == PHYenum.p_FORCE_TRX_OFF)
		{
			t_status = (trx_state == PHYenum.p_TRX_OFF) ? PHYenum.p_TRX_OFF : PHYenum.p_SUCCESS;
			trx_state = PHYenum.p_TRX_OFF;
			//terminate reception if needed
			if (rxPkt != null)
			{   
                            //energyConsumptionModel.simulatePacketEndsReceiving();    
                            
                            if(Def.DEBUG802_15_4)
                            { 
				hdr_cmn ch = rxPkt.HDR_CMN();
				hdr_lrwpan wph = rxPkt.HDR_LRWPAN();
                                System.out.println("[" + JistAPI.getTime() + "]<PHY>(node" + localAddr + ") FORCE TRX OFF sets error bit for incoming pkt: type = " + Trace.wpan_pName(rxPkt) +", src = " + Trace.p802_15_4macSA(rxPkt) + ", dst = " + Trace.p802_15_4macDA(rxPkt) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());
                            }
				rxPkt.HDR_CMN().setError(1);	//incomplete reception -- force packet discard
                            
			}
			//terminate transmission if needed
			if (tx_state == PHYenum.p_BUSY)
			{
                           
                                if(Def.DEBUG802_15_4)
                                { 
                                    hdr_cmn ch = txPkt.HDR_CMN();
                                    hdr_lrwpan wph = txPkt.HDR_LRWPAN();
                                    System.out.println("[" + JistAPI.getTime() + "]<PHY>(node" + localAddr + ") FORCE TRX OFF sets error bit for outgoing pkt: type = " + Trace.wpan_pName(txPkt) +", src = " + Trace.p802_15_4macSA(txPkt) + ", dst = " + Trace.p802_15_4macDA(txPkt) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());
                                }

				txPkt.HDR_CMN().setError(1);
				sendOverH.cancel();
                                energyConsumptionModel.simulatePacketEndsTransmitting();
				//MacMessage_802_15_4::free(txPktCopy);
				tx_state = PHYenum.p_IDLE;
				macEntity.PD_DATA_confirm(PHYenum.p_TRX_OFF);
                                //energyConsumptionModel.simulateRadioGoesToSleep();
				if (trx_state_defer_set != PHYenum.p_IDLE)
					trx_state_defer_set = PHYenum.p_IDLE;
			}
		}
		else
		{
			t_status = PHYenum.p_SUCCESS;
			if (((state == PHYenum.p_RX_ON)&&(trx_state == PHYenum.p_TX_ON))
			  ||((state == PHYenum.p_TX_ON)&&(trx_state == PHYenum.p_RX_ON)))
			{
				trx_state_turnaround = state;
				delay = true;
			}
			else
				trx_state = state;
		}
		//we need to delay <aTurnaroundTime> symbols if Tx2Rx or Rx2Tx
		if (delay)
		{
			trx_state = PHYenum.p_TRX_OFF;	//should be disabled immediately (further transmission/reception will not succeed)
                        //System.out.println("delay");
			TRXH.startTimer(Const.aTurnaroundTime/getRate_BitsPerSecond('s'));     
		}
		else
			macEntity.PLME_SET_TRX_STATE_confirm(t_status);
    		if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
    			System.out.println("[" + JistAPI.getTime() + "]<PHY>(node" + localAddr + ") SET TRX: old = " 
    					+ 
    					((trx_state == PHYenum.p_RX_ON)?"RX_ON":
    						(trx_state == PHYenum.p_TX_ON)?"TX_ON":
    							(trx_state == PHYenum.p_TRX_OFF)?"TRX_OFF":"???") 
    							+
    							" req = "
    							+ 
    							((state == PHYenum.p_RX_ON)?"RX_ON":
    								(state == PHYenum.p_TX_ON)?"TX_ON":
    									(state == PHYenum.p_TRX_OFF)?"TRX_OFF":
    										(state == PHYenum.p_FORCE_TRX_OFF)?"FORCE_TRX_OFF":"???") 
    										+
    										" ret = "
    										+ 
    										((t_status == PHYenum.p_RX_ON)  ? "RX_ON"   :
    											(t_status == PHYenum.p_TX_ON)  ? "TX_ON"   :
    												(t_status == PHYenum.p_TRX_OFF)? "TRX_OFF" :
    													(t_status == PHYenum.p_BUSY_TX)? "BUSY_TX" :
    														(t_status == PHYenum.p_BUSY_RX)? "BUSY_RX" :
    															(t_status == PHYenum.p_SUCCESS)? "SUCCESS" : "???"));
	}
	else
        {
		macEntity.PLME_SET_TRX_STATE_confirm(t_status);
                if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                     System.out.println("[" + JistAPI.getTime() + "]<PHY>(node" + localAddr + ") SET TRX: old = " 
                            + 
                            ((trx_state == PHYenum.p_RX_ON)?"RX_ON":
                            (trx_state == PHYenum.p_TX_ON)?"TX_ON":
                            (trx_state == PHYenum.p_TRX_OFF)?"TRX_OFF":"???") 
                            +
                            " req = "
                            + 
                            ((state == PHYenum.p_RX_ON)?"RX_ON":
                            (state == PHYenum.p_TX_ON)?"TX_ON":
                            (state == PHYenum.p_TRX_OFF)?"TRX_OFF":
                            (state == PHYenum.p_FORCE_TRX_OFF)?"FORCE_TRX_OFF":"???") 
                            +
                            " ret = "
                            + 
                            ((t_status == PHYenum.p_RX_ON)  ? "RX_ON"   :
                            (t_status == PHYenum.p_TX_ON)  ? "TX_ON"   :
                            (t_status == PHYenum.p_TRX_OFF)? "TRX_OFF" :
                            (t_status == PHYenum.p_BUSY_TX)? "BUSY_TX" :
                            (t_status == PHYenum.p_BUSY_RX)? "BUSY_RX" :
                            (t_status == PHYenum.p_SUCCESS)? "SUCCESS" : "???"));
        }
    }
    
    public void PLME_SET_request(PPIBAenum PIBAttribute,PHY_PIB PIBAttributeValue)
    {
        PHYenum t_status;
	
	t_status = PHYenum.p_SUCCESS;
	switch(PIBAttribute)
	{
		case phyCurrentChannel:
			if (!channelSupported(PIBAttributeValue.phyCurrentChannel))
				t_status = PHYenum.p_INVALID_PARAMETER;
			if (ppib.phyCurrentChannel != PIBAttributeValue.phyCurrentChannel)
			{
				//any packet in transmission or reception will be corrupted
				if (rxPkt != null)
				{
                                     if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                                     { 
                                            hdr_cmn ch = rxPkt.HDR_CMN();
                                            hdr_lrwpan wph = rxPkt.HDR_LRWPAN();
                                            System.out.println("[" + JistAPI.getTime() + "]<PHY>(node" + localAddr + ") SET phy channel sets error bit for incoming pkt: type = " + Trace.wpan_pName(rxPkt) +", src = " + Trace.p802_15_4macSA(rxPkt) + ", dst = " + Trace.p802_15_4macDA(rxPkt) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());
                                     }
                                     rxPkt.HDR_CMN().setError(1);
				}
				if (tx_state == PHYenum.p_BUSY)
				{
                                     if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                                     { 
                                            hdr_cmn ch = txPkt.HDR_CMN();
                                            hdr_lrwpan wph = txPkt.HDR_LRWPAN();
                                            System.out.println("[" + JistAPI.getTime() + "]<PHY>(node" + localAddr + ") SET phy channel sets error bit for outgoing pkt: type = " + Trace.wpan_pName(txPkt) +", src = " + Trace.p802_15_4macSA(txPkt) + ", dst = " + Trace.p802_15_4macDA(txPkt) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());
                                     }

                                    txPkt.HDR_CMN().setError(1);
                                    sendOverH.cancel();
                                    energyConsumptionModel.simulatePacketEndsTransmitting();
                                    //energyConsumptionModel.simulateRadioForcedToIdle();
                                    //MacMessage_802_15_4::free(txPktCopy);
                                    tx_state = PHYenum.p_IDLE;
                                    //energyConsumptionModel.simulateRadioGoesToSleep();
                                    macEntity.PD_DATA_confirm(PHYenum.p_TRX_OFF);
                                    
                                    if (trx_state_defer_set != PHYenum.p_IDLE)
                                        trx_state_defer_set = PHYenum.p_IDLE;
				}
				ppib.phyCurrentChannel = PIBAttributeValue.phyCurrentChannel;
			}
			break;
		case phyChannelsSupported:
			if ((PIBAttributeValue.phyChannelsSupported&0xf8000000) != 0)	//5 MSBs reserved
				t_status = PHYenum.p_INVALID_PARAMETER;
			else
				ppib.phyChannelsSupported = PIBAttributeValue.phyChannelsSupported;
			break;
		case phyTransmitPower:
			if (PIBAttributeValue.phyTransmitPower > 0xbf)
				t_status = PHYenum.p_INVALID_PARAMETER;
			else
				ppib.phyTransmitPower = PIBAttributeValue.phyTransmitPower;
			break;
		case phyCCAMode:
			if ((PIBAttributeValue.phyCCAMode < 1)
			 || (PIBAttributeValue.phyCCAMode > 3))
				t_status = PHYenum.p_INVALID_PARAMETER;
			else
				ppib.phyCCAMode = PIBAttributeValue.phyCCAMode;
			break;
		default:
			t_status = PHYenum.p_UNSUPPORT_ATTRIBUTE;
			break;
	}
	macEntity.PLME_SET_confirm(t_status,PIBAttribute);
    }
    
    public byte measureLinkQ(MacMessage_802_15_4 p)
    {
        /* NS-2:
        //Link quality measurement is somewhat simulation/implementation specific;
	//here's our way:
	int lq,lq2;

	//consider energy
	// Linux floating number compatibility
	//lq = (int)((p.txinfo_.RxPr/RXThresh_)*128);
	///
	{
	double tmpf;
	tmpf = p.txinfo_.RxPr / RXThresh_;
	lq = (int)(tmpf * 128);
	}
	if (lq > 255) lq = 255;

	//consider signal-to-noise
	// Linux floating number compatibility
	//lq2 = (int)((p.txinfo_.RxPr/HDR_LRWPAN(p).rxTotPower)*255);
	//
	{
	double tmpf;
	tmpf = p.txinfo_.RxPr/p.HDR_LRWPAN().rxTotPower;
	lq2 = (int)(tmpf * 255);
	}
	
	if (lq > lq2) lq = lq2;		//use worst value
		
	return (byte) lq;
        */
          
        return (byte)255; // ??? OLIVER: implement here, don't let 255 which means perfect quality
    }
    
    /* peek */
    public void peek(Message msg)
    {
        // DO NOT IMPLEMENT
    }
    
    public void transmit(Message msg, long delay, long duration)
    {
        // TODO
        //fieldEntity.transmit(radioInfo, msg, duration);
        transmit(msg); // OLIVER: duration will be computed at PHY Layer ... all mac values will be ignored
    }
    
    public void transmit(Message msg)
    {
        recv(msg, 0);
    }
    
    
    public void endTransmit()
    {
        //System.out.println("Transmission Canceled");
        //energyConsumptionModel.simulatePacketEndsTransmitting();
    }
    /* Upward call from Field. */
     /* Not in NS-2. Implemented from RadioNoiseIndep */
    public void receive(Message msg, Double powerObj_mW, Long durationObj)
    {
        recv(msg, powerObj_mW); // OLIVER: durationObj is ignored as it is recomputed locally
    }
    
    /* Not in NS-2. Implemented from RadioNoiseIndep */
    public void endReceive(final Double powerObj_mW)
    {
        System.out.println("Receival Canceled");
        //energyConsumptionModel.simulatePacketEndsReceiving();
    }
           
    
    /* Downward call from MAC layers. MAC layer has a packets that needs to be transmitted */
    public void send(Message msg)
    {
        if (msg instanceof MacMessage_802_15_4)
            recv(msg, -1);
        else
            System.out.println("<WARNING>[Phy802_15_4Impl.send(Message)] - Received NON-MacMessage_802_15_4 compatible message. Phy802_15_4 is compatible only with Mac802_15_4 implementations");
    }
    
    // Not in NS-2 (actually, it was under the WirelessPhy)
    private int sendUp(MacMessage_802_15_4 p)
    {
        macEntity.receive(p); //????
        return p == null ? 0 : 1;
    }
    
    private void recv(Message msg, double powerObj_mW/* , Handler h */) // powerObj_mW is != -1 from upward calls and -1 for downward calls
    {
        
        MacMessage_802_15_4 p = null;
        if (msg instanceof MacMessage_802_15_4)
            p = ((MacMessage_802_15_4)msg).copy();
 
        if (p == null)
        {
            System.out.println("<WARNING>[Phy802_15_4].recv(msg, powerObj_mW) - a non MacMessage_802_15_4 formatted message received. Cannot handle these. Dropping message");
            return;
        }
        hdr_lrwpan wph = p.HDR_LRWPAN();
        hdr_cmn ch = p.HDR_CMN();
        FrameCtrl frmCtrl = new FrameCtrl();

        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
            System.out.println("[" + JistAPI.getTime() + "][#"+ localAddr + "][PHY.recv()] pkt type = " + Trace.wpan_pName(p) );        
        
        // cannot send/receive if I don't have enough power
        if (energyManagementUnit.getBattery().getPercentageEnergyLevel() < 1)
            return;
        
	switch(ch.direction())
	{
	case DOWN:
            if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                System.out.println("[" + JistAPI.getTime() + "][#" + localAddr + ")[PHY.recv()] -  outgoing pkt: type = " + Trace.wpan_pName(p) +", src = " + Trace.p802_15_4macSA(p) + ", dst = " + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());

            PD_DATA_request(ch.size(),p);
            break;
	case UP:
	default:
		/* OLIVER: This was in NS-2. However, the problem is that sendUp() the way I did it pushes the packet to the MAC, before packet receival completes
                 *         and regardless if the packet destination is this node or not.
                 if (sendUp(p) == 0)	
		{
			//MacMessage_802_15_4::free(p);
                        p = null;
			return;
		}*/

		if (NFAILLINK.updateNFailLink(Def.fl_oper_est,/* index_ */ localAddr.hashCode()) == 0)
		{
			//MacMessage_802_15_4::free(p);
                        p = null;
			return;
		}

                if (LFAILLINK.updateLFailLink(Def.fl_oper_est,Trace.p802_15_4macSA(p),localAddr.hashCode()) == 0)	//broadcast packets can still reach here
                {
                        //MacMessage_802_15_4::free(p);
                        p = null;
                        return;
                }

                frmCtrl.FrmCtrl = wph.MHR_FrmCtrl;
                frmCtrl.parse();
		//tap out
		//if (mac.tap() && frmCtrl.frmType == Const.defFrmCtrl_Type_Data)
		//	mac.tap().tap(p);

		//if (node().energy_model() && node().energy_model().adaptivefidelity())
		//	node().energy_model().add_neighbor(Trace.p802_15_4macSA(p));

		//Under whatever condition, we should mark the media as busy.
		// --no matter the packet(s) is for this node or not, no matter
		//   what state the transceiver is in, RX_ON,TX_ON or TRX_OFF, and
		//   no matter which channel is being used.
		//Note that current WirelessPhy.sendUp() prevents packets with (Pr < CSThresh_)
		//from reaching here --. need to modify.WirelessPhy.sendUp() if we want to see
		//all the packets here (but seems no reason to do that).
		//	in dB as can be seen from following:
		//	not very clear (now CPThresh_ is just a ratio, not in dB?)
		
                p.txinfo_.RxPr = powerObj_mW; // OLIVER: Not in NS-2, where it was done differently
                rxTotPower[wph.phyCurrentChannel] += p.txinfo_.RxPr;
                
                if (Def.DEBUG802_15_4_rxTotNum && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                    System.out.println("[#" + localAddr + "-PHY.recv()] Increments rxTotNum (" + rxTotNum[wph.phyCurrentChannel] + " -> " + (rxTotNum[wph.phyCurrentChannel] + 1) + ")");
		rxTotNum[wph.phyCurrentChannel]++;
		if (EDH.bussy())
                    if(rxEDPeakPower < rxTotPower[ppib.phyCurrentChannel])
			rxEDPeakPower = rxTotPower[ppib.phyCurrentChannel];
		
		if ((Trace.p802_15_4macDA(p) == localAddr.hashCode())			//packet for this node
		  ||(Trace.p802_15_4macDA(p) == Def.MAC_BROADCAST))		//broadcast packet
			rxThisTotNum[wph.phyCurrentChannel]++;

                if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                {
                    
                    System.out.println("[" + JistAPI.getTime() + "]<PHY>(node" + localAddr + ") trx_state = " + trx_state);
                    System.out.println("[" + JistAPI.getTime() + "]<PHY>(node" + localAddr + ") macDA = " + Trace.p802_15_4macDA(p));
                    System.out.println("[" + JistAPI.getTime() + "]<PHY>(node" + localAddr + ") localAddr = " + localAddr.hashCode());
                }
                
                
		if (trx_state == PHYenum.p_RX_ON)
		if (wph.phyCurrentChannel == ppib.phyCurrentChannel)            //current channel
		if ((Trace.p802_15_4macDA(p) == localAddr.hashCode())		//packet for this node
		  ||(Trace.p802_15_4macDA(p) == Def.MAC_BROADCAST))		//broadcast packet
		if (wph.SHR_SFD == Def.defSHR_SFD)				//valid SFD
		{
                        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                            System.out.println("[" + JistAPI.getTime() + "]<PHY>(node" + localAddr + ") incoming pkt: type = " + Trace.wpan_pName(p) +", src = " + Trace.p802_15_4macSA(p) + ", dst = " + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());
	
                        //wph.colFlag = false;
			if (rxPkt == null)
			{
				rxPkt = p;
				rxPkt.HDR_LRWPAN().rxTotPower = rxTotPower[wph.phyCurrentChannel];
			}
			else
			{
                                if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                                    System.out.println("[D][COL][" + /* __FILE__ + */ " :: " + /* __FUNCTION__ + */ "][" + JistAPI.getTime() + "]<PHY>(node" + localAddr + ") COLLISION: \n\t First (power: " + rxPkt.txinfo_.RxPr +"): type = " + Trace.wpan_pName(rxPkt) +", src = " + Trace.p802_15_4macSA(rxPkt) + ", dst = " + Trace.p802_15_4macDA(rxPkt) + ", uid = " + rxPkt.HDR_CMN().uid_ + ", mac_uid = " + rxPkt.HDR_LRWPAN().uid + ", size = " + rxPkt.HDR_CMN().size() 
                                                     +
                                                    "\n\tSecond (power: " + p.txinfo_.RxPr + "): type = " + Trace.wpan_pName(p) +", src = " + Trace.p802_15_4macSA(p) + ", dst = " + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());
                                           

				//wph.colFlag = true;
				//rxPkt.HDR_LRWPAN().colFlag = true;
				//mac.nam.flashNodeColor(JistAPI.getTime());
				if (p.txinfo_.RxPr > rxPkt.txinfo_.RxPr)
				{
					//What should we do if there is a transceiver state set pending?
					//  1. continue defering (could be unbounded delay)
					//..2. set transceiver state now (the incoming packet ignored)
					//We select choice 1, as the traffic rate is supposed to be low.
					rxPkt = p;
					rxPkt.HDR_LRWPAN().rxTotPower = rxTotPower[wph.phyCurrentChannel];
				}
			}
		}
                else
                {
                    if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                        System.out.println("[" + JistAPI.getTime() + "]<PHY>(node" + localAddr + ") invalid");
                }
		if (rxPkt != null)
		if (rxPkt.HDR_LRWPAN().rxTotPower < rxTotPower[rxPkt.HDR_LRWPAN().phyCurrentChannel])
			rxPkt.HDR_LRWPAN().rxTotPower = rxTotPower[rxPkt.HDR_LRWPAN().phyCurrentChannel];
		assert(ch.size() > 0);
		if (ch.direction() != hdr_cmn.dir_t.UP)
		{
			System.out.println("MacMessage_802_15_4-flow direction not specified: sending up the stack on default.\n");
			ch.direction_ = hdr_cmn.dir_t.UP;	//we don't want MAC to handle the same problem
		}
		//Scheduler.instance().schedule(/* & */recvOverH, (Event )p, trxTime(p,true));
                double trxTimeRX = (trxTime(p, true));
                //System.out.println("trxTimeRX = " + trxTimeRX);
                
                energyConsumptionModel.simulateRadioWakes();
                energyConsumptionModel.simulatePacketStartsReceiving();  
                energyManagementUnit.getEnergyConsumptionModel().setCPUDutyCycle(100);
                if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                    System.out.println("[" + JistAPI.getTime() + "][#" + localAddr + "-PHY.recv()] recvOverH.startTimer(" + trxTimeRX +")");
                   
                //if (recvOverH.bussy()) // OLIVER: create a new instance of it not to disrupt the previous one which is about to timeout()
                  //  recvOverH = new Phy802_15_4Timer(self, phyRecvOverHType, localAddr.hashCode(), energyManagementUnit, toSleepTimer2).getProxy();
                recvOverH.startTimer(trxTimeRX, p);
                
		break;
	}
    }
    
    
    public void requestNewSleepTimer(TimerInterface802_15_4 timerEntity)
    {
        //timerEntity.setNewSleepTimer(new ToSleepTimer(localAddr.hashCode(), ((double)toSleepTimerPeriod)/Constants.SECOND, energyConsumptionModel).getProxy());
    }
    
    
    public MacMessage_802_15_4 rxPacket() 
    {
        return rxPkt;
    }
	
    public static PHY_PIB PPIB;

    public void CCAHandler()
    {
         if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
            System.out.println("[PHY.CCAHandler()] Note: determines if the channel is idle and reports its findings by calling Mac802_15_4.PLME_CCA_confirm()");
        
        PHYenum t_status;

	//refer to sec 6.7.9 for CCA details
	//  1. CCA will be affected by outgoing packets,
	//     incoming packets (both destined for this device 
	//     and not destined for this device) and other
	//     interferences.
	//  2. In implementation, we don't care about the details 
	//     and just need to perform an actual measurement.
	if ((tx_state == PHYenum.p_BUSY)||(rxTotNum[ppib.phyCurrentChannel] > 0))
	{
		t_status = PHYenum.p_BUSY;
	}
	else if (ppib.phyCCAMode == 1)	//ED detection
	{
		//sec 6.5.3.3 and 6.6.3.4
		// -- receiver sensitivity: -85 dBm or better for 2.4G
		// -- receiver sensitivity: -92 dBm or better for 868M/915M
		//sec 6.7.9
		// -- ED threshold at most 10 dB above receiver sensitivity.
		//For simulations, we simply compare with CSThresh_
		t_status = (rxTotPower[ppib.phyCurrentChannel] >= /* CSThresh_ */ radioInfo.getShared().getThreshold_W()  /* CSThresh_ is (W) */) ? PHYenum.p_BUSY : PHYenum.p_IDLE; // CSThresh - carrier sense threshold
	}
	else if (ppib.phyCCAMode == 2)	//carrier sense only
	{
		t_status = (rxTotNum[ppib.phyCurrentChannel] > 0) ? PHYenum.p_BUSY : PHYenum.p_IDLE;
	}
	else //if (ppib.phyCCAMode == 3)	//both
	{
		t_status = ((rxTotPower[ppib.phyCurrentChannel] >= /* CSThresh_ */ radioInfo.getShared().getThreshold_W() /* CSThresh_ is (W) */)&&(rxTotNum[ppib.phyCurrentChannel] > 0)) ? PHYenum.p_BUSY : PHYenum.p_IDLE;
	}
	macEntity.PLME_CCA_confirm(t_status);
    }
    
    public void EDHandler()
    {
        int energy;
	byte t_EnergyLevel;

	//refer to sec 6.7.7 for ED implementation details
	//ED is somewhat simulation/implementation specific; here's our way:

	/* Linux floating number compatibility
	energy = (int)((rxEDPeakPower/RXThresh_)*128);
	*/
	{
	double tmpf;
	tmpf = rxEDPeakPower/ /* RXThresh_ */ radioInfo.getShared().getThreshold_W() /* RXThresh_ is (W) */; // RXThresh_: receive power threshold (W) OLIVER: same value as CSThresh_
	energy = (int)(tmpf * 128);
	}
	t_EnergyLevel = (energy > 255) ? (byte)255 : (byte)energy;
	macEntity.PLME_ED_confirm(PHYenum.p_SUCCESS,t_EnergyLevel);
    }
    
    public void TRXHandler()
    {
        //System.out.println("[TRXHandler][#" + localAddr +"] - trx_state = " + trx_state);
        trx_state = trx_state_turnaround;
	//send a confirm
	macEntity.PLME_SET_TRX_STATE_confirm(trx_state);
    }
    
    public void recvOverHandler(MacMessage_802_15_4 p)
    {
         if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
            System.out.println("[" + JistAPI.getTime() + "] - #"+localAddr+"-PHY.recvOverHandler()] - recvOverH.timeout()");
        
        
        byte lq;
	hdr_lrwpan wph = p.HDR_LRWPAN();
	hdr_cmn ch = p.HDR_CMN();
        //System.out.println("[DEBUG][#" + localAddr + " recvOverHandler()");
        energyConsumptionModel.simulatePacketEndsReceiving();
        if (!toSleepTimer2.bussy())
        {
            energyConsumptionModel.simulateRadioGoesToSleep();
            energyManagementUnit.getEnergyConsumptionModel().setCPUDutyCycle(5);
        }
                
        if (Def.DEBUG802_15_4_rxTotNum && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
            System.out.println("[" + JistAPI.getTime() + "] - #"+localAddr+"-PHY.recvOverHandler()] - decrements rxTotNum (for CS) " + rxTotNum[wph.phyCurrentChannel] + " -> " + (rxTotNum[wph.phyCurrentChannel] - 1) + ")");
        
	rxTotPower[wph.phyCurrentChannel] -= p.txinfo_.RxPr;
	rxTotNum[wph.phyCurrentChannel]--;
	
	if (rxTotNum[wph.phyCurrentChannel] == 0)
		rxTotPower[wph.phyCurrentChannel] = 0.0;

	if ((Trace.p802_15_4macDA(p) != localAddr.hashCode())
	  &&(Trace.p802_15_4macDA(p) != Def.MAC_BROADCAST))	//packet not for this node (interference)
        {
		//MacMessage_802_15_4::free(p);
                p = null;
                //markSleeps();
        }
        
	// Rey: added proper rxPkt handling  
	//else if (p != rxPkt)				//packet corrupted (not the strongest one) or un-detectable
	else if (rxPkt!=null && p != rxPkt)				//packet corrupted (not the strongest one) or un-detectable
	{
                if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
                    System.out.println("[D][" + ((wph.phyCurrentChannel != ppib.phyCurrentChannel) ? "CHN" : "NOT") + "][+ " + /* __FILE__ + */ "::" + /* __FUNCTION__ + */ "::" /* __LINE__*/ + "][" + JistAPI.getTime() + "]<PHY>(node " + localAddr + ") dropping pkt: type = " + Trace.wpan_pName(p) + ", src = " + Trace.p802_15_4macSA(p) + ", dst = " + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());

                rxThisTotNum[wph.phyCurrentChannel]--;
		drop(p,(wph.phyCurrentChannel != ppib.phyCurrentChannel) ? "CHN":"NOT");
                // Rey: added missing reset of rxPkt
               rxPkt = null;           
	}
	else
	{
		rxThisTotNum[wph.phyCurrentChannel]--;
		//measure (here calculate) the link quality
		lq = measureLinkQ(p);
		//ch.size()  -= Def.defPHY_HEADER_LEN;
                ch.setSize(ch.size() - Def.defPHY_HEADER_LEN);
		rxPkt = null;
		if ((ch.size() <= 0) 
		    ||(ch.size() > Const.aMaxPHYPacketSize)
		    ||ch.error() == 1)		//incomplete reception (due to FORCE_TRX_OFF),data packet received during ED or other errors
		{
                    
                        if (Def.DEBUG802_15_4_err && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
        			System.out.println("[D][ERR][" + /* __FILE__ + */  "::" + /* __FUNCTION__ + */ "::" + JistAPI.getTime() + "][" + /*__LINE__*/  "]<PHY>(node " + localAddr + ") dropping pkt: type = " + Trace.wpan_pName(p) + ", src = " + Trace.p802_15_4macSA(p) + ", dst = " + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());

			drop(p,"ERR");
		}
		else
		{
                        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
        			System.out.println("[" + /* __FILE__ + */ "::" + /* __FUNCTION__ + */ "][" + JistAPI.getTime() + "]<PHY>(node" + localAddr + ") incoming pkt *:  type = " + Trace.wpan_pName(p) + ", src = " + Trace.p802_15_4macSA(p) + ", dst = " + Trace.p802_15_4macDA(p) + ", uid = " + ch.uid() + ", mac_uid = " + wph.uid + ", size = " + ch.size());

			PD_DATA_indication((byte)ch.size(),p,lq);	//MAC sublayer need to further check if the packet
								//is really received successfully or not.
		}
		if (trx_state_defer_set != PHYenum.p_IDLE)
		{
			trx_state_turnaround = trx_state_defer_set;
			trx_state_defer_set = PHYenum.p_IDLE;
			if (trx_state_turnaround == PHYenum.p_TRX_OFF)
			{
				trx_state = trx_state_turnaround;
				macEntity.PLME_SET_TRX_STATE_confirm(trx_state);
			}
			else
			{
				//we need to delay <aTurnaroundTime> symbols for Rx2Tx
				trx_state = PHYenum.p_TRX_OFF;	//should be disabled immediately (further reception will not succeed)
                                //energyConsumptionModel.simulateRadioGoesToSleep();
                                TRXH.startTimer(Const.aTurnaroundTime/getRate_BitsPerSecond('s'));
			}
		}
	}
        //System.out.println("end of RecvOverHandler");
    }
    
    public void drop(MacMessage_802_15_4 p, String reason)
    {
        
    }
    
    public void sendOverHandler()
    {
        if (Def.DEBUG802_15_4 && ( Def.DEBUG802_15_4_nodeid==(MacAddress.NULL) || Def.DEBUG802_15_4_nodeid.equals(localAddr) ))
		System.out.println("[" + /* __FILE__ + */ "::" + /* __FUNCTION__ + */ "][" + JistAPI.getTime() + "]<PHY>(node" + localAddr + ") sending over:  type = " + Trace.wpan_pName(txPktCopy) + ", src = " + Trace.p802_15_4macSA(txPktCopy) + ", dst = " + Trace.p802_15_4macDA(txPktCopy) + ", uid = " + txPktCopy.HDR_CMN().uid() + ", mac_uid = " + txPktCopy.HDR_LRWPAN().uid + ", size = " + txPktCopy.HDR_CMN().size());

	assert(tx_state == PHYenum.p_BUSY);
	assert(txPktCopy != null);
	//MacMessage_802_15_4::free(txPktCopy);
	tx_state = PHYenum.p_IDLE;
        
        
        //System.out.println("[DEBUG][#" + localAddr + " sendOverHandler()");
        if (!sendOverH.canceled())  // OLIVER: to differentiate between the timer calls and MAC's ones. MAC cancels this timer before calling sendOverHandler()
        {
            energyConsumptionModel.simulatePacketEndsTransmitting();
            energyConsumptionModel.simulateRadioGoesToSleep();
            energyManagementUnit.getEnergyConsumptionModel().setCPUDutyCycle(5);
            markWakes();
        }
            
	macEntity.PD_DATA_confirm(PHYenum.p_SUCCESS);
        
	if (trx_state_defer_set != PHYenum.p_IDLE)
	{
		trx_state_turnaround = trx_state_defer_set;
		trx_state_defer_set = PHYenum.p_IDLE;
               
		if (trx_state_turnaround == PHYenum.p_TRX_OFF)
		{
			trx_state = trx_state_turnaround;
                        //energyConsumptionModel.simulateRadioGoesToSleep();
			macEntity.PLME_SET_TRX_STATE_confirm(trx_state);
		}
		else
		{
			//we need to delay <aTurnaroundTime> symbols for Rx2Tx
			trx_state = PHYenum.p_TRX_OFF;	//should be disabled immediately (further transmission will not succeed) 
                        //energyConsumptionModel.simulateRadioGoesToSleep();
			TRXH.startTimer(Const.aTurnaroundTime/getRate_BitsPerSecond('s'));  
		}
	}
    }

   
};
