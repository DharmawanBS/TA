/*
 * p802_15_4hlist.java
 *
 * Created on July 8, 2008, 10:54 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

import jist.runtime.JistAPI;

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
public class HLISTLINK
{
    public int hostID;	//host unique id
    public byte SN;		//SN of packet last received
    public HLISTLINK last;
    public HLISTLINK next;
    HLISTLINK(int hostid, byte sn)
    {
            hostID = hostid;
            SN = sn;
            last = null;
            next = null;
    };
    
    // -- static functions
    public static int addHListLink(HLISTLINK hlistLink1, HLISTLINK hlistLink2, int hostid, byte sn)
    {
	HLISTLINK tmp;
	if(hlistLink2 == null)		//not exist yet
	{
		hlistLink2 = new HLISTLINK(hostid, sn);
		if (hlistLink2 == null)
                    return 1;
		hlistLink1 = hlistLink2;
	}
	else
	{
		tmp=new HLISTLINK(hostid, sn);
		if (tmp == null)
                    return 1;
		tmp.last = hlistLink2;
		(hlistLink2).next = tmp;
		hlistLink2 = tmp;
	}
	return 0;
    }

    public static int updateHListLink(int oper, HLISTLINK hlistLink1, HLISTLINK hlistLink2, int hostid, byte sn /* = 0 */)
    {
	HLISTLINK tmp;
	int i, ok;

	ok = 1;

	tmp = hlistLink1;
	while(tmp != null)
	{
		if(tmp.hostID == hostid)
		{
			if (oper == Def.hl_oper_del)	//delete an element
			{
				if(tmp.last != null)
				{
					tmp.last.next = tmp.next;
					if(tmp.next != null)
						tmp.next.last = tmp.last;
					else
						hlistLink2 = tmp.last;
				}
				else if (tmp.next != null)
				{
					hlistLink1 = tmp.next;
					tmp.next.last = null;
				}
				else
				{
					hlistLink1 = null;
					hlistLink2 = null;
				}
				//delete tmp;
			}
			if (oper == Def.hl_oper_rpl)	//replace
			{
				if (tmp.SN != sn)
					tmp.SN = sn;
				else
				{
					ok = 2;
					break;
				}
			}
			ok = 0;
			break;
		}
		tmp = tmp.next;
	}
	return ok;
    }

    public static int chkAddUpdHListLink(HLISTLINK hlistLink1, HLISTLINK hlistLink2, int hostid, byte sn)
    {
	int i;

	i = updateHListLink(Def.hl_oper_rpl, hlistLink1, hlistLink2, hostid, sn);
	if (i == 0) return 1;
	else if (i == 2) return 2;

	i = addHListLink(hlistLink1, hlistLink2, hostid, sn);
	if (i == 0) return 0;
	else return 3;
    }

    public static void emptyHListLink(HLISTLINK hlistLink1, HLISTLINK hlistLink2)
    {
	HLISTLINK tmp, tmp2;

	if(hlistLink1 != null)
	{
		tmp = hlistLink1;
		while(tmp != null)
		{
			tmp2 = tmp;
			tmp = tmp.next;
			//delete tmp2;
		}
		hlistLink1 = null;
	}
	hlistLink2 = hlistLink1;
    }

    public static void dumpHListLink(HLISTLINK hlistLink1, short hostid)
    {
	HLISTLINK tmp;
	int i;

	System.out.println("[" + JistAPI.getTime() + "] --- dump host list (by host " + hostid + ") ---");
	tmp = hlistLink1;
	i = 1;
	while(tmp != null)
	{
		System.out.println("\t" + i + ":\tfrom host " + tmp.hostID + ":\tSN = " + tmp.SN);
		tmp = tmp.next;
		i++;
	}
    }
};
