/*
 * DeadEndList.java
 *
 * Created on July 29, 2005, 11:45 AM
 */

/**
 *
 * @author  Oliviu Ghica
 */
package sidnet.core.misc;

import java.util.LinkedList;

public class DeadEndList {
    LinkedList deadEndLinkedList;
    long expirationPeriod;                // By convention, '-1' means no such feature will be used: Items DO NOT expire
    
    /** Creates a new instance of DeadEndList */
    public DeadEndList(long ExpPer) {
        deadEndLinkedList = new LinkedList();
        expirationPeriod  = ExpPer;
        if (expirationPeriod <= 0)
            expirationPeriod = -1;          
    }
    
    public void Add_And_Monitor(DeadEndListEntry entry){ deadEndLinkedList.addLast(entry); }
    
    public void Block(DeadEndListEntry entry, long currentTimeStamp)
    {
        for (int i = 0; i < deadEndLinkedList.size(); i++)
        {
            DeadEndListEntry listentry = (DeadEndListEntry)deadEndLinkedList.get(i);
            if (entry.nodeIP == listentry.nodeIP &&
                entry.packetID == listentry.packetID &&
                entry.destIP == listentry.destIP)
                ((DeadEndListEntry)deadEndLinkedList.get(i)).Block(currentTimeStamp);
        }   
    }
    
    public void Remove(DeadEndListEntry entry){ deadEndLinkedList.remove(entry); }
    
    // Check for expired Items in the list. Remove the expired ones
    private void RemoveExpired(long currentTimeStamp)
    {
        if (expirationPeriod != -1)     // If we permit items to expire
            for (int i=0 ; i < deadEndLinkedList.size(); i++)
                if (currentTimeStamp - ((DeadEndListEntry)deadEndLinkedList.get(i)).timeStamp > expirationPeriod)
                    deadEndLinkedList.remove(i);
    }
    
    public int GetItemStatus(DeadEndListEntry entry, long currentTimeStamp)
    {
        RemoveExpired(currentTimeStamp);
        
        for (int i = 0; i < deadEndLinkedList.size(); i++)
        {
            DeadEndListEntry listentry = (DeadEndListEntry)deadEndLinkedList.get(i);
        
            if (entry.nodeIP == listentry.nodeIP &&
                entry.packetID == listentry.packetID &&
                entry.destIP == listentry.destIP)
                return listentry.status;
        }
        
        return -1;          // Item not found in the list
    }
    
    public int size()
    {
        return deadEndLinkedList.size();
    }
}
