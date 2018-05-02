/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sidnet.models.deployment.utils;

import java.util.Iterator;
import java.util.LinkedList;
import jist.swans.field.Placement;
import jist.swans.misc.Location.Location2D;

/**
 *
 * @author Oliver
 */
public class PlacementList implements Placement
    {
        private LinkedList<Location2D> locationList;
        private Iterator listIterator = null;
        public PlacementList()
        {
            locationList = new LinkedList<Location2D>();          
        }
        
        public synchronized void add(Location2D loc)
        {
            locationList.add(loc);
        }
        
        public int getSize()
        {
            return locationList.size();
        }
        
         /**
           * Return location of next node.
           *
           * @return location of next node
           */
         public Location2D getNextLocation()
         {
             Location2D nextLocation = null;
             if (listIterator == null)
                listIterator = locationList.iterator();
             if (listIterator.hasNext())
                nextLocation = (Location2D)listIterator.next();
             return nextLocation;
         }
         
         public LinkedList<Location2D> getAsLinkedList()
         {
             return locationList;
         }
    }
