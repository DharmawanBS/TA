/*
 * DEVICELINK.java
 *
 * Created on July 17, 2008, 11:28 AM
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
class DEVICELINK
{
    public /*IE3ADDR*/ int   addr64;		//extended address of the associated device
    public int addr16;		//assigned short address
    public byte capability;	//device capability
    public DEVICELINK last;
    public DEVICELINK next;
    public DEVICELINK(/*IE3ADDR*/ int  addr, byte cap)
    {
            addr64 = addr;
            addr16 = addr;		
            capability = cap;
            last = null;
            next = null;
    };
    
    // ??? In NS-2, the following were not members of DEVICELINK
    public static int addDeviceLink(DEVICELINK/* ** */ deviceLink1, DEVICELINK/* ** */ deviceLink2, /*IE3ADDR*/ int  addr, byte cap) // ???
    {
        DEVICELINK tmp;
	if(deviceLink2 == null)		//not exist yet
	{
		deviceLink2 = new DEVICELINK(addr,cap);
		if (deviceLink2 == null) return 1;
		deviceLink1 = deviceLink2;
	}
	else
	{
		tmp=new DEVICELINK(addr,cap);
		if (tmp == null) return 1;
		tmp.last = deviceLink2;
		(deviceLink2).next = tmp;
		deviceLink2 = tmp;
	}
	return 0;
    }
    public static int updateDeviceLink(int oper, DEVICELINK /* ** */ deviceLink1, DEVICELINK /* ** */ deviceLink2, /*IE3ADDR*/ int  addr) // ???
    {
        DEVICELINK tmp;
	int rt;

	rt = 1;

	tmp = deviceLink1;
	while(tmp != null)
	{
		if(tmp.addr64 == addr)
		{
			if (oper == Def.tr_oper_del)	//delete an element
			{
				if(tmp.last != null)
				{
					tmp.last.next = tmp.next;
					if(tmp.next != null)
						tmp.next.last = tmp.last;
					else
						deviceLink2 = tmp.last;
				}
				else if (tmp.next != null)
				{
					deviceLink1 = tmp.next;
					tmp.next.last = null;
				}
				else
				{
					deviceLink1 = null;
					deviceLink2 = null;
				}
				//delete tmp;
			}
			rt = 0;
			break;
		}
		tmp = tmp.next;
	}
	return rt;
    }
    
    public static int numberDeviceLink(DEVICELINK /* ** */ deviceLink1) // ??/
    {
        DEVICELINK tmp;
	int num;

	num = 0;
	tmp = deviceLink1;
	while(tmp != null)
	{
		num++;
		tmp = tmp.next;
	}
	return num;
    }
    public static int chkAddDeviceLink(DEVICELINK/* ** */ deviceLink1, DEVICELINK/* ** */ deviceLink2, /*IE3ADDR*/ int  addr, byte cap) // ???
    {
         int i;

        i = updateDeviceLink(Def.tr_oper_est, deviceLink1, deviceLink2, addr);
        if (i == 0) return 1;
        i = addDeviceLink(deviceLink1, deviceLink2, addr, cap);
        if (i == 0) return 0;
        else return 2;
    }
    public static void emptyDeviceLink(DEVICELINK/* ** */ deviceLink1, DEVICELINK/* ** */ deviceLink2) // ???
    {
        DEVICELINK tmp, tmp2;

	if(deviceLink1 != null)
	{
		tmp = deviceLink1;
		while(tmp != null)
		{
			tmp2 = tmp;
			tmp = tmp.next;
			//delete tmp2;
		}
                deviceLink1 = null;
	}
	deviceLink2 = deviceLink1;
    }
    public void dumpDeviceLink(DEVICELINK deviceLink1, /*IE3ADDR*/ short  coorAddr) // ???
    {
        DEVICELINK tmp;
	int i;
	char[] tmpstr = new char[81];

	System.out.println("[" + JistAPI.getTime() + "] --- dump associated device list (by coordinator coorAddr) ---");
	tmp = deviceLink1;
	i = 1;
	while(tmp != null)
	{
		System.out.println("\t " + i + ":\taddress = 0x" + tmp.addr64);
		tmp = tmp.next;
		i++;
	}
    }
    
    
};