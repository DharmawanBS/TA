/*
 * WatchList.java
 *
 * Created on June 14, 2006, 4:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.misc;

import java.util.LinkedList;
import jist.runtime.JistAPI;

/**
 *
 * @author Oliviu Ghica
 */
public class WatchList {
    public class Item
    {
        public int id;
        public long s_seq;
        public long registrationTimeStamp;
        public long expirationTimeStamp;
        
        public Item(int id, long s_seq, long registrationTimeStamp, long expirationTimeStamp)
        {
            this.id                    = id;
            this.s_seq                   = s_seq;
            this.registrationTimeStamp = registrationTimeStamp;
            this.expirationTimeStamp   = expirationTimeStamp;
        }
    }
    
    private LinkedList<Item> packetList;
    private long expirationInterval;
    
    /** Creates a new instance of WatchList */
    public WatchList(long expirationInterval) {
        packetList = new LinkedList();
        this.expirationInterval = expirationInterval;
    }
    
    public void monitor(int id, long s_seq)
    {
        long currentTimeStamp = JistAPI.getTime();
        packetList.add(new Item(id, s_seq, currentTimeStamp, currentTimeStamp + expirationInterval));
    }
    
    public boolean isDuplicate(int id, long s_seq)
    {
        long currentTimeStamp = JistAPI.getTime();
        purgeExpired(currentTimeStamp);
        
        if (!exists(id, s_seq))
        {
            // OLIVER: 05/13/2009 commented out below since will create problems checking twice the packet if it is duplicate
            //packetList.add(new Item(id, ttl, currentTimeStamp, currentTimeStamp + expirationInterval));
            return false;
        }
        return true;
    }
  
    
    private boolean exists(int id, long s_seq)
    {
        for (Item item:packetList)
            if (item.id == id && item.s_seq == s_seq)
                return true;
        return false;
    }
    
    private void purgeExpired(long currentTimeStamp)
    {
        LinkedList<Item> expirationList = new LinkedList<Item>();
        
        for (Item item: packetList)
            if (currentTimeStamp > item.expirationTimeStamp)
                expirationList.add(item);
        
        for (Item item: expirationList)
            packetList.remove(item);
    }
}
