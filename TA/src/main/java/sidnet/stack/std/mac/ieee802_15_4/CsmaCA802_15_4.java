/*
 * CsmaCA802_15_4.java
 *
 * Created on June 30, 2008, 5:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import sidnet.core.timers.JistTimer;
import sidnet.core.timers.JistTimerInterface;
import sidnet.core.timers.TimerCallbackInterface;
import jist.runtime.JistAPI;
import jist.swans.Constants;
import jist.swans.mac.MacAddress;

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

public class CsmaCA802_15_4 {
	
	public static final int  CSMACA_BACKOFF_TIMER = 1,
							 CSMACA_BEACON_OTHER_TIMER = 2,
							 CSMACA_DEFER_CCAT_TIMER = 3;
	
    //timers
    private JistTimerInterface backoffT,  
                               bcnOtherT, 
                               deferCCAT; 

    public Phy802_15_4 phyEntity;
    public Mac802_15_4Impl mac;
       
    private byte NB, // NB is the number
    				 // of times the CSMA-CA algorithm was required to backoff while attempting the current transmission; this
                     // value shall be initialized to zero before each new transmission attempt
    			 
    			 CW, // CW is the contention window length,
    			     // defining the number of backoff periods that need to be clear of channel activity before the transmission can
    			     // commence; this value shall be initialized to two before each transmission attempt and reset to two each time
    			     // the channel is assessed to be busy
    			 BE; //The CW variable is only used for slotted CSMA-CA

    private boolean ackReq, beaconEnabled, beaconOther, waitNextBeacon;
    private long bcnTxTime_jtu, bcnRxTime_jtu, backoffPeriod_jtu; 
    
    private int backoffPeriodsLeft;			//backoff periods left for next superframe (negative value means no backoff)
    
    private MacMessage_802_15_4 txPkt;
    private MacAddress localAddr;
    
    static Logger logger;	
	static {
		 logger = Logger.getLogger("CsmaCA802_15_4");
		 logger.setLevel(Level.INFO);
		 logger.setAdditivity(false);
		 logger.addAppender(new ConsoleAppender(new PatternLayout("\t%-5p %c *** %m \n")));
	}
    
    public CsmaCA802_15_4(Phy802_15_4 p, Mac802_15_4Impl m, MacAddress localAddr) {
        phyEntity = p;
        this.localAddr = localAddr;
        mac = m;
		txPkt = null;
		waitNextBeacon = false;

		backoffT  = new JistTimer(CSMACA_BACKOFF_TIMER,      (TimerCallbackInterface)m.getProxy()).getProxy();
		bcnOtherT = new JistTimer(CSMACA_BEACON_OTHER_TIMER, (TimerCallbackInterface)m.getProxy()).getProxy();
	    deferCCAT = new JistTimer(CSMACA_DEFER_CCAT_TIMER,   (TimerCallbackInterface)m.getProxy()).getProxy();
    }

    protected void reset() {
        if (beaconEnabled) {
			NB = 0;
			CW = 2;
			BE = mac.mpib.macMinBE;
			if ((mac.mpib.macBattLifeExt)&&(BE > 2))
				BE = 2;
		} else {
			NB = 0;
			BE = mac.mpib.macMinBE;
		}
    }
    
    protected long adjustTime(long wtime_jtu) {
        //find the beginning point of CAP and adjust the scheduled time
		//if it comes before CAP
		long neg_jtu;
		long tmpf_jtu; // jtu - Jist (atomic) Time Units

		assert(txPkt != null);
		
		if (!mac.toParent(txPkt)) {
			if (mac.mpib.macBeaconOrder != 15) {
				/* Linux floating number compatibility
				neg = (JistAPI.getTime() + wtime - bcnTxTime) - mac.beaconPeriods * backoffPeriod_jtu;
				*/
				tmpf_jtu = mac.beaconPeriods * backoffPeriod_jtu;
				tmpf_jtu = JistAPI.getTime() - tmpf_jtu;
				tmpf_jtu += wtime_jtu;
				neg_jtu = tmpf_jtu - bcnTxTime_jtu;
	
				if (neg_jtu < 0)
					wtime_jtu -= neg_jtu;
				return wtime_jtu;
			} else
				return wtime_jtu;
		} else {
			if (mac.macBeaconOrder2 != 15) {
				/* Linux floating number compatibility
				neg = (JistAPI.getTime() + wtime - bcnRxTime) - mac.beaconPeriods2 * backoffPeriod_jtu;
				*/
				tmpf_jtu = mac.beaconPeriods2 * backoffPeriod_jtu;
				tmpf_jtu = JistAPI.getTime() - tmpf_jtu;		
				tmpf_jtu += wtime_jtu;
				neg_jtu = tmpf_jtu - bcnRxTime_jtu;
	
				if (neg_jtu < 0)
					wtime_jtu -= neg_jtu;
				return wtime_jtu;
			}
			else
				return wtime_jtu;
		}
    }
    
    protected boolean canProceed(long wtime, boolean afterCCA /*= false */) {
		return true;
    }
    
    protected void newBeacon(CHAR trx/*char trx*/) {
        //this function will be called by MAC each time a new beacon received or sent within the current PAN
		long wtime_jtu;
	
		if (mac.txAck == null)
			mac.plme_set_trx_state_request(PHYenum.p_RX_ON);	
	
		if (bcnOtherT.isBusy())
			bcnOtherT.cancelAndReset();	
	
		if (waitNextBeacon)
			if ((txPkt != null) && (!backoffT.isBusy())) {
				assert(backoffPeriodsLeft >= 0);
				if (backoffPeriodsLeft == 0) {
					wtime_jtu = adjustTime(0);
					if (canProceed(wtime_jtu, false));
						backoffHandler();	//no need to resume backoff
				} else {
					wtime_jtu = adjustTime(0);
					wtime_jtu += backoffPeriodsLeft * backoffPeriod_jtu;
					if (canProceed(wtime_jtu, false));
		                backoffT.startTimer(wtime_jtu);
				}
			}
		waitNextBeacon = false;
    }
    
    protected void start(boolean firsttime, MacMessage_802_15_4 pkt /*= 0 */, boolean ackreq /*= 0*/) {
        boolean backoff;
        double rate_bpjtu; // bit per atomic Unit of time in Jist
        long wtime_jtu,BI2_jtu;

        logger.debug("CSMACA.start()]");
        
		if (mac.txAck != null) {
			mac.backoffStatus = 0;
			txPkt = null;
			return;
		}

		assert(!backoffT.isBusy());
		
		if (firsttime) {
			beaconEnabled = ((mac.mpib.macBeaconOrder != 15)||(mac.macBeaconOrder2 != 15));
			beaconOther = (mac.macBeaconOrder3 != 15);
			reset();	
			assert(txPkt == null);
			txPkt = pkt;
			ackReq = ackreq;
			rate_bpjtu = phyEntity.getRate_BitsPerJistAtomicUnit('s');
			backoffPeriod_jtu = (long)(Const.aUnitBackoffPeriod / rate_bpjtu); // aUnitBackoffPeriod is in bits
			if (beaconEnabled)	{
				bcnTxTime_jtu = (long)(mac.macBcnTxTime / rate_bpjtu); // TODO check time unit - macBcnTxTime is in bits (or symbols)
				bcnRxTime_jtu = (long)(mac.macBcnRxTime / rate_bpjtu); // TODO check time unit - macBcnRxTime is in bits (or symbols)
				//it's possible we missed some beacons
				BI2_jtu = (long)(mac.sfSpec2.BI  / phyEntity.getRate_BitsPerJistAtomicUnit('s')); // mac.sfSpec2.BI is in symbols
				if (mac.macBeaconOrder2 != 15)
				while (bcnRxTime_jtu + BI2_jtu < JistAPI.getTime())
					bcnRxTime_jtu += BI2_jtu;
			}
		}
		
        wtime_jtu = 0;
        while (wtime_jtu <= 0)
            wtime_jtu = (Constants.random.nextInt() % (1 << BE)) * backoffPeriod_jtu; // the next backoff interval
        
		wtime_jtu = adjustTime(wtime_jtu);
		backoff = true;
		if (beaconEnabled || beaconOther) {
			if (beaconEnabled)
			if (firsttime)
				wtime_jtu = (long)(mac.locateBoundary(mac.toParent(txPkt), wtime_jtu / Constants.SECOND) * Constants.SECOND); // TODO: currently MAC expectes in [seconds]
			if (!canProceed(wtime_jtu, false))		
				backoff = false;
		}
		if (backoff)
	        backoffT.startTimer(wtime_jtu);
    }
    
    protected void cancel() {
        if (bcnOtherT.isBusy())
        	bcnOtherT.cancelAndReset();
        else if (backoffT.isBusy())
        	backoffT.cancelAndReset();
        else if (deferCCAT.isBusy())
        	deferCCAT.cancelAndReset();
        else        
        	mac.taskP.setTaskStatus(taskPending.TP_CCA_csmaca, false);
        
        txPkt = null;
    }
    
    public void backoffHandler() {
        logger.debug("[CSMACA.backoffHandler()]");
        
        mac.taskP.setTaskStatus(taskPending.TP_RX_ON_csmaca, true);
        mac.plme_set_trx_state_request(PHYenum.p_RX_ON);
    }
    
    public void RX_ON_confirm(PHYenum status) {
        long now_jtu, wtime_jtu;

		if (status != PHYenum.p_RX_ON) {
			if (status == PHYenum.p_BUSY_TX)
	            mac.taskP.setTaskStatus(taskPending.TP_RX_ON_csmaca, true);
			else
				backoffHandler();
			return;
		}
	
		//locate backoff boundary if needed
		now_jtu = JistAPI.getTime();
		if (beaconEnabled)
			wtime_jtu = (long)(mac.locateBoundary(mac.toParent(txPkt), 0.0) * Constants.SECOND); // TODO: MAC uses [second]
		else
			wtime_jtu = 0;
	
		if (wtime_jtu == 0) {
	        mac.taskP.setTaskStatus(taskPending.TP_CCA_csmaca, true);
			phyEntity.PLME_CCA_request();
		} else
			deferCCAT.startTimer(wtime_jtu);
    }
    
    public void bcnOtherHandler() {
        newBeacon(CHAR.R/*'R'*/);
    }
    
    public void deferCCAHandler() {
        mac.taskP.setTaskStatus(taskPending.TP_CCA_csmaca, true);
        phyEntity.PLME_CCA_request();
    }
    
    public void CCA_confirm(PHYenum status) {
        //This function should be called when mac receiving CCA_confirm.
		boolean idle;
	
		idle = (status == PHYenum.p_IDLE) ? true : false;	
		if (idle) {
			if ((!beaconEnabled)&&(!beaconOther)) {
				txPkt = null;
				mac.csmacaCallBack(PHYenum.p_IDLE);
			} else {
				if (beaconEnabled)
					CW--;
				else
					CW = 0;
				if (CW == 0) {
					//timing condition may not still hold -- check again
					if (canProceed(0, true)) {
						txPkt = null;
						mac.csmacaCallBack(PHYenum.p_IDLE);
					} else {	//postpone until next beacon sent or received
						if (beaconEnabled) CW = 2;
							backoffPeriodsLeft = 0;
					}
				}
				else //perform CCA again
					backoffHandler();
			}
		} else { // channel is busy
			if (beaconEnabled)
				CW = 2;
			NB++;
			if (NB > mac.mpib.macMaxCSMABackoffs) {
				txPkt = null;
				mac.csmacaCallBack(PHYenum.p_BUSY);
			} else { //backoff again
				BE++;
				if (BE > Const.aMaxBE)
					BE = Const.aMaxBE;
				start(false, null, false);
			}
		}
    }
};
/* END *** CSMACA *** */
