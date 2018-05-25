/*
 * RegionList.java
 *
 * Created on April 11, 2007, 3:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.misc;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author Oliver
 */
public class RegionList {
     public static final int NOT_FOUND = -1;
    
     protected LinkedList<Region> list = null;
     
    /** Creates a new instance of RegionList */
    public RegionList() {
        list   = new LinkedList<Region>();
    }
    
    public void add(Region region)
    {
        list.add(region);
    }
    
    public int getIndex(int regionID)
    {
        int index = 0;

        for(Region region: list)
            if (region.getID() == regionID)
                return index;
            else
                index++;
        
        return NOT_FOUND;
    }
    
    public void remove(int regionID)
    {
        int index = getIndex(regionID);
        if (index != NOT_FOUND)
            list.remove(index);
    }
    
    public void removeFrom(int index)
    {
        if (index < list.size())
            list.remove(index);
    }
    
    public Region getElementAt(int index)
    {
        if (index < list.size())
            return (Region)list.get(index);
        else
            return null;
    }
    
    public int getSize()
    {
        return list.size();
    }
    
    public LinkedList<Region> getAsLinkedList()
    {
        return list;
    }
    
    public Region getRegionByID(int regionID)
    {
        for(Region currentRegion: list)
            if (currentRegion.getID() == regionID)
                return currentRegion;
        
        return null;
    }
}
