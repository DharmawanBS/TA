/*
 * LFAILLINK.java
 *
 * Created on July 17, 2008, 11:30 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.stack.std.mac.ieee802_15_4;

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
//link failure
class LFAILLINK
{
    public int src,dst;
    public LFAILLINK last;
    public LFAILLINK next;
    
    public LFAILLINK(int s,int d)
    {
        src = s;
        dst = d;
        last = null;
        next = null;
    };

    // ??? Not members in NS-2
    public static int addLFailLink(int s,int d)
    {
        LFAILLINK tmp;
        if(Fail.lfailLink2 == null)		//not exist yet
        {
                Fail.lfailLink2 = new LFAILLINK(s,d);
                if (Fail.lfailLink2 == null) return 1;
                Fail.lfailLink1 = Fail.lfailLink2;
        }
        else
        {
                tmp=new LFAILLINK(s,d);
                if (tmp == null) return 1;
                tmp.last = Fail.lfailLink2;
                (Fail.lfailLink2).next = tmp;
                Fail.lfailLink2 = tmp;
        }
        return 0;
    }
    
    public static int updateLFailLink(int oper,int s,int d)
    {
        LFAILLINK tmp;
        int rt;

        rt = 1;

        tmp = Fail.lfailLink1;
        while(tmp != null)
        {
                if ((tmp.src == s)&&(tmp.dst == d))
                {
                        if (oper == Def.fl_oper_del)	//delete an element
                        {
                                if(tmp.last != null)
                                {
                                        tmp.last.next = tmp.next;
                                        if(tmp.next != null)
                                                tmp.next.last = tmp.last;
                                        else
                                                Fail.lfailLink2 = tmp.last;
                                }
                                else if (tmp.next != null)
                                {
                                        Fail.lfailLink1 = tmp.next;
                                        tmp.next.last = null;
                                }
                                else
                                {
                                        Fail.lfailLink1 = null;
                                        Fail.lfailLink2 = null;
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
    public static int chkAddLFailLink(int s,int d)
    {
         int i;

        i = updateLFailLink(Def.fl_oper_est,s,d);
        if (i == 0) return 1;
        i = addLFailLink(s,d);
        if (i == 0) return 0;
        else return 2;
    }
}

