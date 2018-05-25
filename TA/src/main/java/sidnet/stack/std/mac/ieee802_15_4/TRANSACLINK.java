/*
 * TRANSACLINK.java
 *
 * Created on July 17, 2008, 11:28 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

import jist.runtime.JistAPI;
import jist.swans.Constants;
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
class TRANSACLINK
{
    public static int maxNumTransactions = 70; // ??? In NS-2 it was an outsider #define
    public byte pendAddrMode;
    public int pendAddr16;
    public /*IE3ADDR*/ int pendAddr64;
    public MacMessage_802_15_4 pkt;
    public byte msduHandle;
    public double expTime;
    public TRANSACLINK last;
    public TRANSACLINK next;
    public TRANSACLINK(byte pendAM, /*IE3ADDR*/ int pendAddr, MacMessage_802_15_4 p, byte msduH, double kpTime)
    {
        pendAddrMode = pendAM;
        pendAddr64 = pendAddr;
        pkt = p;
        msduHandle = msduH;
        expTime = JistAPI.getTime()/Constants.SECOND + kpTime;
        last = null;
        next = null;
    };
    
    // ??? In NS-2, the following were not members of TRANSACLINK
   //--------------------------------------------------------------------------------------

    public static void purgeTransacLink(TRANSACLINK/* ** */ transacLink1, TRANSACLINK/* ** */ transacLink2)
    {
            //purge expired transactions
            TRANSACLINK tmp,tmp2;

            tmp = transacLink1;
            while(tmp != null)
            {
                    if (JistAPI.getTime()/Constants.SECOND > tmp.expTime)
                    {
                            tmp2 = tmp;
                            if (tmp.next != null)
                                    tmp = tmp.next.next;
                            else
                                    tmp = null;
                            //--- delete the transaction ---
                            //don't try to call updateTransacLink() -- to avoid re-entries of functions
                            if(tmp2.last != null)
                            {
                                    tmp2.last.next = tmp2.next;
                                    if(tmp2.next != null)
                                            tmp2.next.last = tmp2.last;
                                    else
                                            transacLink2 = tmp2.last;
                            }
                            else if (tmp2.next != null)
                            {
                                    transacLink1 = tmp2.next;
                                    tmp2.next.last = null;
                            }
                            else
                            {
                                    transacLink1 = null;
                                    transacLink2 = null;
                            }
                            //free the packet first
                            //MacMessage_802_15_4.free(tmp2.pkt);
                            //delete tmp2;
                            //--------------------------------
                    }
                    else
                            tmp = tmp.next;
            }
    }

    public static int addTransacLink(TRANSACLINK/* ** */ transacLink1, TRANSACLINK/* ** */ transacLink2, byte pendAM, /*IE3ADDR*/ int  pendAddr, MacMessage_802_15_4 p, byte msduH, double kpTime)
    {
            TRANSACLINK tmp;
            if(transacLink2 == null)		//not exist yet
            {
                    transacLink2 = new TRANSACLINK(pendAM,pendAddr,p,msduH,kpTime);
                    if (transacLink2 == null) return 1;
                    transacLink1 = transacLink2;
            }
            else
            {
                    tmp=new TRANSACLINK(pendAM,pendAddr,p,msduH,kpTime);
                    if (tmp == null) return 1;
                    tmp.last = transacLink2;
                    (transacLink2).next = tmp;
                    transacLink2 = tmp;
            }
            return 0;
    }

    public static MacMessage_802_15_4 getPktFrTransacLink(TRANSACLINK/* ** */ transacLink1, byte pendAM, /*IE3ADDR*/ int  pendAddr)
    {
            TRANSACLINK tmp;

            tmp = transacLink1;

            while(tmp != null)
            {
                    if((tmp.pendAddrMode == pendAM)
                    && (((pendAM == Const.defFrmCtrl_AddrMode16)&&((short)pendAddr == tmp.pendAddr16))
                     ||((pendAM == Const.defFrmCtrl_AddrMode64)&&(pendAddr == tmp.pendAddr64))))
                    {
                            return tmp.pkt;
                    }
                    tmp = tmp.next;
            }
            return null;
    }

    public static int updateTransacLink(int oper, TRANSACLINK/* ** */ transacLink1, TRANSACLINK/* ** */ transacLink2, byte pendAM, /*IE3ADDR*/ int  pendAddr)
    {
            TRANSACLINK tmp;
            int i, rt;

            //purge first if (oper == Def.tr_oper_est)
            if (oper == Def.tr_oper_est)
                    purgeTransacLink(transacLink1,transacLink2);

            rt = 1;

            tmp = transacLink1;

            if (oper == Def.tr_oper_EST)
            {
                    while(tmp != null)
                    {
                            if((tmp.pendAddrMode == pendAM)
                            && (((pendAM == Const.defFrmCtrl_AddrMode16)&&((short)pendAddr == tmp.pendAddr16))
                            ||((pendAM == Const.defFrmCtrl_AddrMode64)&&(pendAddr == tmp.pendAddr64))))
                            {
                                    rt = 0;
                                    tmp = tmp.next;
                                    break;
                            }
                            tmp = tmp.next;
                    }
                    if (rt == 1) 
                        return 1;
            }

            rt = 1;

            while(tmp != null)
            {
                    if((tmp.pendAddrMode == pendAM)
                    && (((pendAM == Const.defFrmCtrl_AddrMode16)&&((short)pendAddr == tmp.pendAddr16))
                     ||((pendAM == Const.defFrmCtrl_AddrMode64)&&(pendAddr == tmp.pendAddr64))))
                    {
                            if (oper == Def.tr_oper_del)	//delete an element
                            {
                                    if(tmp.last != null)
                                    {
                                            tmp.last.next = tmp.next;
                                            if(tmp.next != null)
                                                    tmp.next.last = tmp.last;
                                            else
                                                    transacLink2 = tmp.last;
                                    }
                                    else if (tmp.next != null)
                                    {
                                            transacLink1 = tmp.next;
                                            tmp.next.last = null;
                                    }
                                    else
                                    {
                                            transacLink1 = null;
                                            transacLink2 = null;
                                    }
                                    //free the packet first
                                    //MacMessage_802_15_4.free(tmp.pkt);
                                    //delete tmp;
                            }
                            rt = 0;
                            break;
                    }
                    tmp = tmp.next;
            }
            return rt;
    }

    public static int updateTransacLinkByPktOrHandle(int oper, TRANSACLINK/* ** */ transacLink1, TRANSACLINK/* ** */ transacLink2, MacMessage_802_15_4 pkt, byte msduH /* = 0 */)
    {
            TRANSACLINK tmp;
            int i, rt;

            //purge first if (oper == Def.tr_oper_est)
            if (oper == Def.tr_oper_est)
                    purgeTransacLink(transacLink1,transacLink2);

            rt = 1;

            tmp = transacLink1;

            while(tmp != null)
            {
                    if(((pkt != null)&&(tmp.pkt == pkt))
                     ||((pkt == null)&&(tmp.msduHandle == msduH)))
                    {
                            if (oper == Def.tr_oper_del)	//delete an element
                            {
                                    if(tmp.last != null)
                                    {
                                            tmp.last.next = tmp.next;
                                            if(tmp.next != null)
                                                    tmp.next.last = tmp.last;
                                            else
                                                    transacLink2 = tmp.last;
                                    }
                                    else if (tmp.next != null)
                                    {
                                            transacLink1 = tmp.next;
                                            tmp.next.last = null;
                                    }
                                    else
                                    {
                                            transacLink1 = null;
                                            transacLink2 = null;
                                    }
                                    //free the packet first
                                    //MacMessage_802_15_4.free(tmp.pkt);
                                    //delete tmp;
                            }
                            rt = 0;
                            break;
                    }
                    tmp = tmp.next;
            }
            return rt;
    }

    public static int numberTransacLink(TRANSACLINK/* ** */ transacLink1, TRANSACLINK/* ** */ transacLink2)
    {
            //return the number of transactions in the link
            TRANSACLINK tmp;
            int num;

            //purge first
            purgeTransacLink(transacLink1,transacLink2);

            num = 0;
            tmp = transacLink1;
            while(tmp != null)
            {
                    num++;
                    tmp = tmp.next;
            }
            return num;
    }

    public static int chkAddTransacLink(TRANSACLINK/* ** */ transacLink1, TRANSACLINK/* ** */ transacLink2, byte pendAM, /*IE3ADDR*/ int  pendAddr, MacMessage_802_15_4 p, byte msduH, double kpTime)
    {
            int i;

            //purge first
            purgeTransacLink(transacLink1,transacLink2);

            i = numberTransacLink(transacLink1,transacLink2);
            if (i >= Const.maxNumTransactions) return 1;

            i = addTransacLink(transacLink1,transacLink2,pendAM,pendAddr,p,msduH,kpTime);
            if (i == 0) return 0;
            else return 2;
    }

    public static void emptyTransacLink(TRANSACLINK/* ** */ transacLink1, TRANSACLINK/* ** */ transacLink2)
    {
            TRANSACLINK tmp, tmp2;

            if(transacLink1 != null)
            {
                    tmp = transacLink1;
                    while(tmp != null)
                    {
                            tmp2 = tmp;
                            tmp = tmp.next;
                            //free the packet first
                            //MacMessage_802_15_4.free(tmp2.pkt);
                            //delete tmp2;
                    }
                    transacLink1 = null;
            }
            transacLink2 = transacLink1;
    }

    void dumpTransacLink(TRANSACLINK transacLink1, /*IE3ADDR*/ short  coorAddr)
    {
            TRANSACLINK tmp;
            int i;
            //char[] tmpstr = new char[121];
            String tmpstr = new String();
            //char[] tmpstr2 = new char[61];
            String tmpstr2 = new String();
            //FrameCtrl frmCtrl;
            FrameCtrl frmCtrl = new FrameCtrl();

            System.out.println("[" + JistAPI.getTime() + "] --- dump transaction list (by coordinator " + coorAddr + ") ---");
            tmp = transacLink1;
            i = 1;
            while(tmp != null)
            {
                    tmpstr = "\t " + i + ":\t";
                    if (tmp.pendAddrMode == Const.defFrmCtrl_AddrMode16)
                            //sprintf(tmpstr2,"pendAddrMode = 16-bit\tpendAddr = %d\t",tmp.pendAddr16);
                            tmpstr2 = "pendAddrMode = 16-bit\tpendAddr = " + tmp.pendAddr16 + "\t";
                    else
                            //sprintf(tmpstr2,"pendAddrMode = 64-bit\tpendAddr = %d\t",tmp.pendAddr64);
                            tmpstr2 = "pendAddrMode = 64-bit\tpendAddr = " + tmp.pendAddr64 + "\t";
                    //strcat(tmpstr,tmpstr2);
                    tmpstr += tmpstr2;
                    frmCtrl.FrmCtrl = tmp.pkt.HDR_LRWPAN().MHR_FrmCtrl;
                    frmCtrl.parse();
                    if (frmCtrl.frmType == Const.defFrmCtrl_Type_Data)
                            //strcat(tmpstr,"pktType = data\t");
                            tmpstr += "pktType = data\t";
                    else
                            //strcat(tmpstr,"pktType = command\t");
                            tmpstr += "pktType = command\t";
                    //sprintf(tmpstr2,"expTime = %f\n",tmp.expTime);
                    tmpstr2 = "expTime = " + tmp.expTime + "\n";
                    //strcat(tmpstr,tmpstr2);
                    tmpstr += tmpstr2;
                    System.out.println(tmpstr);
                    tmp = tmp.next;
                    i++;
            }
    }
};
