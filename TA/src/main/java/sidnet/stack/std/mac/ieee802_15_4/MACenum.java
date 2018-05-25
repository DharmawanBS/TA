/*
 * MACenum.java
 *
 * Created on July 17, 2008, 11:41 AM
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
//MAC enumerations description (Table 64)
public enum MACenum// typedef enum
{
	m_SUCCESS ((byte)0),
	//--- following from Table 68) ---
	m_PAN_at_capacity((byte)1),
	m_PAN_access_denied((byte)2),
	//--------------------------------
	m_BEACON_LOSS ((byte)224), // 0xe0 ???  OLIVER: only this one entry had a (value), along with m_SUCCESS(0) originally
	m_CHANNEL_ACCESS_FAILURE ((byte)225),
	m_DENIED ((byte)226),
	m_DISABLE_TRX_FAILURE ((byte)227),
	m_FAILED_SECURITY_CHECK ((byte)228),
	m_FRAME_TOO_LONG ((byte)229),
	m_INVALID_GTS ((byte)230),
	m_INVALID_HANDLE ((byte)231),
	m_INVALID_PARAMETER ((byte)232),
	m_NO_ACK ((byte)233),
	m_NO_BEACON ((byte)234),
	m_NO_DATA ((byte)235),
	m_NO_SHORT_ADDRESS ((byte)236),
	m_OUT_OF_CAP ((byte)237),
	m_PAN_ID_CONFLICT ((byte)(byte)238),
	m_REALIGNMENT ((byte)239),
	m_TRANSACTION_EXPIRED ((byte)240),
	m_TRANSACTION_OVERFLOW ((byte)241),
	m_TX_ACTIVE ((byte)242),
	m_UNAVAILABLE_KEY ((byte)243),
	m_UNSUPPORTED_ATTRIBUTE ((byte)244),
	m_UNDEFINED ((byte)245);			//we added this for handling any case not specified in the draft
        
        private byte index;
        MACenum(byte index)
        {
            this.index = index;
        }
        
        public byte getByteVal()
        {
            return index;
        }
        
        public static MACenum retrieveFor(byte index)
        {
            switch(index)
            {
                case(0): return m_SUCCESS ;
                //--- following from Table 68) ---
                case(1): return m_PAN_at_capacity; 
                case(2): return m_PAN_access_denied; 
                //--------------------------------
                case((byte)224): return m_BEACON_LOSS ; // 0xe0 ???  OLIVER: only this one entry had a (value), along with m_SUCCESS(0) originally
                case((byte)225): return m_CHANNEL_ACCESS_FAILURE ; 
                case((byte)226): return m_DENIED ; 
                case((byte)227): return m_DISABLE_TRX_FAILURE ;
                case((byte)228): return m_FAILED_SECURITY_CHECK ; 
                case((byte)229): return m_FRAME_TOO_LONG ;
                case((byte)230): return m_INVALID_GTS ; 
                case((byte)231): return m_INVALID_HANDLE ;
                case((byte)232): return m_INVALID_PARAMETER ; 
                case((byte)233): return m_NO_ACK ;
                case((byte)234): return m_NO_BEACON ;
                case((byte)235): return m_NO_DATA;
                case((byte)236): return m_NO_SHORT_ADDRESS ; 
                case((byte)237): return m_OUT_OF_CAP ; 
                case((byte)238): return m_PAN_ID_CONFLICT ; 
                case((byte)239): return m_REALIGNMENT ; 
                case((byte)240): return m_TRANSACTION_EXPIRED ; 
                case((byte)241): return m_TRANSACTION_OVERFLOW ;
                case((byte)242): return m_TX_ACTIVE ;
                case((byte)243): return m_UNAVAILABLE_KEY ;
                case((byte)244): return m_UNSUPPORTED_ATTRIBUTE ;
                default: return m_UNDEFINED ;	
            }
        }
}
