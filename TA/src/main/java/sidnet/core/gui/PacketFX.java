 /* PacketFX.java
 *
 * Created on December 12, 2007, 1:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import jist.runtime.JistAPI;
import sidnet.core.misc.Location2D;
import sidnet.core.misc.Sleeper;

/**
 *
 * @author Oliver
 */
 

public class PacketFX extends Thread implements JistAPI.DoNotRewrite
    {
        public static final int TRANSMIT = 0;
        public static final int RECEIVE  = 1;
        private static final double PROGRESS_INCREMENT = 0.1;
        private static final double PROGRESS_RATIO = 0.5; // TRANSMIT / RECEIVE
        
        private Location2D fromLocation;
        private Location2D toLocation;
        private int userValue = 0;
        private int type;
        private Graphics2D g2d;
        
        private double progress = 0; // between 0 and 1
        private long delay;
        
      
        
        public PacketFX(Location2D fromLocation, Location2D toLocation, int type, Graphics2D g2d)
        {
            super();
            this.fromLocation = fromLocation;
            this.toLocation   = toLocation;
            this.type         = type;
            this.g2d          = g2d;
            delay             = 100;
            adjustMidPoint();
        }
        
        public PacketFX(Location2D fromLocation, Location2D toLocation, int type, Graphics2D g2d, long delay)
        {
            super();
            this.fromLocation = fromLocation;
            this.toLocation   = toLocation;
            this.type         = type;
            this.g2d          = g2d;
            this.delay        = delay;
            adjustMidPoint();
        }


        public PacketFX(Location2D fromLocation, Location2D toLocation, int type, Graphics2D g2d, int userValue)
        {
            super();
            this.fromLocation = fromLocation;
            this.toLocation   = toLocation;
            this.type         = type;
            this.userValue    = userValue;
            this.g2d          = g2d;
            this.delay        = 100;
            adjustMidPoint();
        }
        
        
        public PacketFX(Location2D fromLocation, Location2D toLocation, int type, Graphics2D g2d, int userValue, long delay)
        {
            super();
            this.fromLocation = fromLocation;
            this.toLocation   = toLocation;
            this.type         = type;
            this.userValue    = userValue;
            this.g2d          = g2d;
            this.delay        = delay;
            adjustMidPoint();
        }
        
        private void adjustMidPoint()
        {  
            if (type == TRANSMIT)
            {
                if (toLocation.getX() > fromLocation.getX())
                    toLocation.setX((int)((double)toLocation.getX() - ((double)(toLocation.getX() - fromLocation.getX())) * PROGRESS_RATIO));
                else
                    toLocation.setX((int)((double)toLocation.getX() + ((double)(fromLocation.getX() - toLocation.getX())) * PROGRESS_RATIO));
                
                if (toLocation.getY() > fromLocation.getY())
                    toLocation.setY((int)((double)toLocation.getY() - ((double)(toLocation.getY() - fromLocation.getY())) * PROGRESS_RATIO));
                else
                    toLocation.setY((int)((double)toLocation.getY() + ((double)(fromLocation.getY() - toLocation.getY())) * PROGRESS_RATIO));
            }
            else // RECEIVE
            {
                if (fromLocation.getX() > toLocation.getX())
                    fromLocation.setX((int)((double)fromLocation.getX() - ((double)(fromLocation.getX() - toLocation.getX())) * (1 - PROGRESS_RATIO)));
                else
                    fromLocation.setX((int)((double)fromLocation.getX() + ((double)(toLocation.getX() - fromLocation.getX())) * (1 - PROGRESS_RATIO)));
                
                if (fromLocation.getY() > toLocation.getY())
                    fromLocation.setY((int)((double)fromLocation.getY() - ((double)(fromLocation.getY() - toLocation.getY())) * (1 - PROGRESS_RATIO)));
                else
                    fromLocation.setY((int)((double)fromLocation.getY() + ((double)(toLocation.getY() - fromLocation.getY())) * (1 - PROGRESS_RATIO)));
            }
            /*if (type == TRANSMIT)
                System.out.println("TRANSMIT");
            else
                System.out.println("RECEIVE");*/
        }
        
        private Location2D nextProgressLocation()
        {
            Location2D nextPacketLocation;            
            if (progress >= 1)
                return null;
            
            double prog;
            if (type == TRANSMIT)
            {
                prog =  progress;
                nextPacketLocation = new Location2D(fromLocation.getX(), fromLocation.getY());
                
                 if (fromLocation.getX() > toLocation.getX())
                nextPacketLocation.setX((int)((double)fromLocation.getX() - ((double)(fromLocation.getX() - toLocation.getX())) * prog));
            else
                nextPacketLocation.setX((int)((double)fromLocation.getX() + ((double)(toLocation.getX() - fromLocation.getX())) * prog));

            if (nextPacketLocation.getY() > toLocation.getY())
                nextPacketLocation.setY((int)((double)fromLocation.getY() - ((double)(fromLocation.getY() - toLocation.getY())) * prog));
            else
                nextPacketLocation.setY((int)((double)fromLocation.getY() + ((double)(toLocation.getY() - fromLocation.getY())) * prog));
            }
            else
            {
                prog = 1 - progress;
                nextPacketLocation = new Location2D(toLocation.getX(), toLocation.getY());
                
                 if (nextPacketLocation.getX() > toLocation.getX())
                nextPacketLocation.setX((int)((double)toLocation.getX() - ((double)(toLocation.getX() - fromLocation.getX())) * prog));
            else
                nextPacketLocation.setX((int)((double)toLocation.getX() + ((double)(fromLocation.getX() - toLocation.getX())) * prog));

            if (nextPacketLocation.getY() > toLocation.getY())
                nextPacketLocation.setY((int)((double)toLocation.getY() - ((double)(toLocation.getY() - fromLocation.getY())) * prog));
            else
                nextPacketLocation.setY((int)((double)toLocation.getY() + ((double)(fromLocation.getY() - toLocation.getY())) * prog));
            }
            
           
            
            
            
            progress = progress + PROGRESS_INCREMENT;
            
            //System.out.println("progress " + progress);
            
            return nextPacketLocation;
        }
        
        public void setDelay(int delay)
        {
            this.delay = delay;
        }
        
        public void run()
        {
            Location2D nextPacketDisplayLocation = nextProgressLocation();
            Location2D lastNextPacketDisplayLocation;
           
            String userValueString = ((Integer)userValue).toString();
            int length = 15 + ( userValueString.length() -1 )* 10;
                       
            
            while (nextPacketDisplayLocation != null)
            {
               if (type == TRANSMIT)
               {
                   g2d.setColor(Color.GRAY);
                   g2d.fillRect((int)nextPacketDisplayLocation.getX(), (int)nextPacketDisplayLocation.getY(), (int)(progress*length), (int)(progress * 16));  
                   g2d.setColor(Color.BLACK);
                   g2d.drawRect((int)nextPacketDisplayLocation.getX(), (int)nextPacketDisplayLocation.getY(), (int)(progress*length), (int)(progress * 16));  
                   
               }
                else
                {
                    g2d.setColor(Color.GRAY);
                    g2d.fillRect((int)nextPacketDisplayLocation.getX(), (int)nextPacketDisplayLocation.getY(), (int)((1-progress)*length), (int)((1-progress) * 16));  
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect((int)nextPacketDisplayLocation.getX(), (int)nextPacketDisplayLocation.getY(), (int)((1-progress)*length), (int)((1-progress) * 16));  
                }
           
               
                lastNextPacketDisplayLocation = nextPacketDisplayLocation;
                nextPacketDisplayLocation = nextProgressLocation();
                
                if (nextPacketDisplayLocation == null && lastNextPacketDisplayLocation != null && type == TRANSMIT)
                   {
                        g2d.setColor(Color.ORANGE);
                        g2d.fillRect((int)lastNextPacketDisplayLocation.getX(), (int)lastNextPacketDisplayLocation.getY(), (int)(progress*length), (int)(progress * 16));  
                        g2d.setColor(Color.BLACK);
                        g2d.drawRect((int)lastNextPacketDisplayLocation.getX(), (int)lastNextPacketDisplayLocation.getY(), (int)(progress*length), (int)(progress * 16));
                        g2d.setColor(Color.WHITE);
                        g2d.drawString(userValueString, (int)lastNextPacketDisplayLocation.getX()+5, (int)lastNextPacketDisplayLocation.getY()+13);
                   }
                try{
                    this.sleep(delay);
                }
                catch(Exception ie) {ie.printStackTrace();};
            }
        }
            
    }
