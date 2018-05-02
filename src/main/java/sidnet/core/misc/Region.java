/*
 * Region.java
 *
 * Created on April 11, 2007, 11:47 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.misc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import sidnet.core.misc.Location2D;
import javax.swing.JPanel;
/**
 *
 * @author Oliver
 */
public class Region {
     // Configs
     public static final int WIDTH = 5;
     public static final int HEIGHT = 5;
     
     //Internal vars
     private int regionID;
     private LinkedList<Location2D> vertexList = null;
     private ListIterator listIterator;
     private LocationContext locationContext;
     
     
    /** Copy constructor */
    public Region(Region region)
    {
        this.regionID = region.getID();
        vertexList = new LinkedList<Location2D>();
        for (Location2D vertex: region.vertexList)
            this.vertexList.add(vertex);
        this.locationContext = new LocationContext(region.getLocationContext());
    }
     
    /** Creates a new instance of Region */
    public Region(int regionID, LocationContext locationContext) {
        vertexList   = new LinkedList<Location2D>();
        this.regionID = regionID;
        this.locationContext = locationContext;
    }
    
    public Region(int regionID, int x, int y, int width, int height, LocationContext locationContext)
    {
        vertexList   = new LinkedList<Location2D>();
        this.regionID = regionID;
        this.locationContext = locationContext;
        
        add(new Location2D(x, y));
        add(new Location2D(x+width, y));
        add(new Location2D(x+width, y+height));
        add(new Location2D(x, y+height));
    }
    
    public void add(Location2D vertex)
    {
        vertexList.addLast(vertex);
    }
    
    public int getID()
    {
        return regionID;
    }
    
    public int getSize()
    {
        return vertexList.size();
    }
    
    public void resetIterator()
    {
        listIterator = vertexList.listIterator(0);
    }
    
    public Location2D getNext()
    {
        if (listIterator.hasNext())
            return (Location2D)listIterator.next();
        else
            return null;
    }
    
    public boolean hasNext()
    {
        return listIterator.hasNext();
    }
    
    public Location2D getOrigin()
    {
        return vertexList.getFirst();
    }
    
    public Location2D getVertex(int vertexIndex)
    {
        resetIterator();
        if (vertexIndex < 0 || vertexIndex > getSize())
            return null;
        return vertexList.get(vertexIndex);
    }
    
    public Region getCopy()
    {
        resetIterator();
        Region cloneRegion = new Region(regionID, locationContext);
        Location2D temp = getNext();
        
        while (temp != null)
        {
            cloneRegion.add(temp);
            temp = getNext();
        }
        
        return cloneRegion;
    }
    
    public Region getCopy(LocationContext newLocationContext)
    {
        resetIterator();
        Region cloneRegion = new Region(regionID, newLocationContext);
        Location2D temp = getNext();
        
        while (temp != null)
        {
            cloneRegion.add(temp.toNCS(locationContext).fromNCS(newLocationContext));
            temp = getNext();
        }
        
        return cloneRegion;
    }
    
    public LocationContext getLocationContext()
    {
        return locationContext;
    }
    
     public boolean isInside(NCS_Location2D pp)
        {   
            // This method is patterned after [Franklin, 2000]
            int count = 0;
            
            if (vertexList.size() <= 2)
                return false;
            
            // Convert p from NCS coords
            Location2D p = pp.fromNCS(locationContext);
          
            // Loop through all the edges of a polygon
            for (int i = 0; i < vertexList.size(); i++)
            {
                if (((vertexList.get(i)).getY() <= p.getY() && (vertexList.get((i+1)%vertexList.size())).getY() > p.getY())        // an upward crossing
                    || ((vertexList.get(i)).getY() > p.getY() && (vertexList.get((i+1)%vertexList.size())).getY() <= p.getY()))           // a downward crossing
                {
                        // compute the actual edge-ray intersect x-coordinate
                        float vt = (float)((float)(p.getY() - (vertexList.get(i)).getY())/((float)(vertexList.get((i+1)%vertexList.size())).getY() - (vertexList.get(i)).getY()));
                        if ((p.getX() < (vertexList.get(i)).getX() + vt*((vertexList.get((i+1)%vertexList.size())).getX() - (vertexList.get(i)).getX())))        // p.x < intersect
                            ++count;        // a valid crossing of y = p.y right of p.x
                }
            }
            if (count % 2 == 0)
                return false;
            else
                return true;
        }
     
     public static Region getConvexHullRegion(Region region, Location2D point)
     {
         Location2D[] locationArray = new Location2D[region.getSize() + 1];
         
         region.resetIterator();
         int i = 0;
         Location2D vertex = region.getNext();
         
         while (vertex != null)
         {
             locationArray[i] = vertex;
             vertex = region.getNext();
             i++;
         }
         
         locationArray[i] = point;
         
         return getConvexHullRegion(region.getID(), region.getLocationContext(), locationArray);
     }
     
     public static Region getConvexHullRegion(int regionID, LocationContext locationContext, Location2D[] P)
     {
         LinkedList<Location2D> E = new LinkedList<Location2D>();
         
         boolean valid;
         
         for (int p = 0; p < P.length; p++)
             for(int q = 0; q < P.length; q++)
                 if (p != q)
                 {
                    valid = true;
                    
                    for (int r = 0; r < P.length; r++)
                        if (r != p && r != q)
                        {
                            /* test if 'r' lies on the left of the directed line from p to q */
                            
                            double px, py, qx, qy, rx, ry;
                            
                            px = P[p].getX();
                            py = P[p].getY();
                            
                            qx = P[q].getX();
                            qy = P[q].getY();
                            
                            rx = P[r].getX();
                            ry = P[r].getY();
                            
                            double kpq = (py - qy) / (px - qx);
                            
                            if (ry >= kpq * rx + py)
                                valid = false; /* on left */
                             
                            if (valid)
                            {
                                if (!E.contains(P[p]))
                                    E.add(P[p]);
                                if (!E.contains(P[q]))
                                    E.add(P[q]);
                            }
                        }
                 }
         
         /* build a region object out of E */
         Region convexHullRegion = new Region(regionID, locationContext);
         for(Location2D loc: E)
             convexHullRegion.add(loc);
         
         return convexHullRegion;
     }
     
     public int getAsMessageSize()
     {
         return getSize() * (2 /* x-coord as int */ + 2 /* y-coord as int */);
     }
     
     public List<Location2D> getVertexList() {
    	 return vertexList;
     }
     
     public List<NCS_Location2D> getNCSVertexList() {
    	 List<NCS_Location2D> ncs_vertexList = new LinkedList<NCS_Location2D>();
    	 
    	 for (Location2D vertex: vertexList)
    		 ncs_vertexList.add(vertex.toNCS(locationContext));
    	 
    	 return ncs_vertexList;
     }
    
     public Region getExpandedRegion(int d, LocationContext lc) {
    	 LocationContext newLocationContext = 
    		 new LocationContext(
    				 locationContext.getWidth()
    				 	+ 2 * d * locationContext.getWidth() / lc.getWidth(),
    				 locationContext.getHeight() 
    				 	+ 2 * d * locationContext.getHeight() / lc.getHeight());
    	 
    	 Region region = new Region(regionID, locationContext);
    	 
    	 double sumOldX = 0, sumOldY = 0;
    	 double sumNewX = 0, sumNewY = 0;
    	 
    	 // checking centers
    	 for (Location2D vertex: vertexList) { 
    		 Location2D newLoc = vertex.convertTo(locationContext, newLocationContext);    		 
    		 sumOldX += vertex.getX();
    		 sumOldY += vertex.getY();
    		 sumNewX += newLoc.getX();
    		 sumNewY += newLoc.getY();
    	 }
    	
         // centering
    	 double adjX = (sumNewX-sumOldX) / vertexList.size();
    	 double adjY = (sumNewY-sumOldY) / vertexList.size();
    	 
    	 // adjusting
    	 for (Location2D vertex: vertexList){
    		 Location2D newLoc = vertex.convertTo(locationContext, newLocationContext); 
    		 region.add(new Location2D(newLoc.getX() - adjX, newLoc.getY() - adjY));
    	 }
    	 
    	 return region;
     }
}
