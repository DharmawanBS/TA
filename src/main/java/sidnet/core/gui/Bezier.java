/*
 * Bezier.java
 *
 * Created on August 17, 2005, 11:59 AM
 */

package sidnet.core.gui;

import javax.swing.*; // For JPanel, etc.
import java.awt.*;           // For Graphics, etc.
import java.awt.geom.*;      // For Ellipse2D, etc.
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import sidnet.core.misc.Location2D;

/**
 *
 * @author  Oliviu Ghica
 */
public class Bezier extends JPanel implements MouseListener, MouseMotionListener, ActionListener{
    double[] pX;    // Parameter set for X(t)
    double[] pY;    // Parameter set for Y(t)
    public double[] xSet;  // The set of start, end and control points of Bezier on X-axis
    public double[] ySet;  // The set of start, end and control points of Bezier on Y-axis
    int degree;
    int numPoints;
    
    // Graphics
    private JPanel guiPanel;
    private int once = 0;
    private boolean displayON         = false;
    private boolean showBezierHandles = false;
    private boolean lineSourceSelected= false;
    private boolean lineDestinationSelected = false;
    private int initX, initY;
    private int SCALE = 4; // used to represent smaller bezier handling lines (set to 4) ????

    // Menus
    JMenuItem menuItem;
    String menuShowText;
    String menuHideText;

    public Bezier(int degree, int numPoints)
    {
        this.degree = degree;
        this.numPoints = numPoints;

        pX = new double[degree];
        pY = new double[degree];
    }
    
    /** Creates a new instance of Bezier */
    public Bezier(double[] xSet, double[] ySet, int degree, int numPoints, String menuShowText, String menuHideText) {
        this.xSet = new double[degree];
        this.ySet = new double[degree];
        for (int i = 0; i < degree; i++)
        {
            this.xSet[i]   = xSet[i];
            this.ySet[i]   = ySet[i];
        }
        this.degree = degree;
        this.numPoints = numPoints;
        
        pX = new double[degree];
        pY = new double[degree];
        
        menuItem = new JMenuItem(menuShowText);
        this.menuShowText = menuShowText;
        this.menuHideText = menuHideText;
        computeParams();
   }
    
    public Bezier(Bezier curve)
    {
        degree = curve.degree;
        xSet = new double[degree];
        ySet = new double[degree];
        for (int i=0; i< degree; i++)
        {
            xSet[i] = curve.xSet[i];
            ySet[i] = curve.ySet[i];
        }
        
        numPoints = curve.numPoints;
        
        //setGUIPanel(curve.guiPanel);
        displayON = false;
        showBezierHandles = false;
        
        pX = new double[degree];
        pY = new double[degree];
        
        computeParams();
    }
    
    public double getLength()
    {
        double length = 0;
        double [][]xyCurve = getCurve(numPoints);
        for (int i = 1; i < numPoints; i++)
        {
            length = length + Math.sqrt((xyCurve[i][0]-xyCurve[i-1][0])*(xyCurve[i][0]-xyCurve[i-1][0]) + (xyCurve[i][1]-xyCurve[i-1][1])*(xyCurve[i][1]-xyCurve[i-1][1]));
        }
        return length;
    }
    
    public void setGUIPanel(JPanel guiPanel)
    {
        if (this.guiPanel != guiPanel)
        {
            this.guiPanel = guiPanel;
            this.setBounds(0,0, guiPanel.getWidth(), guiPanel.getHeight());
            this.setOpaque(false);
            this.guiPanel.add(this);
        }
    }
    
    public void turnDisplayOn(boolean set)
    {
        System.out.println("display = " + set);
        displayON = set;
        repaint();
    }
    
    public void MenuPluginInit(JPopupMenu hostPopupMenu)
    {       
        menuItem.addActionListener( this );
        hostPopupMenu.addSeparator();
        hostPopupMenu.add(menuItem);
    }

    
    public void computeParams()
    // Computes the parameters of X(t) and Y(t) Bezier Polynomials
    // X(t) = Ax*t^3 + Bx*t^2 + Cx*t + X0 
    // Y(t) = Ay*t^3 + By*t^2 + Cy*t + Y0 
    {
        double[] xScaleSet={0,0,0,0}, yScaleSet={0,0,0,0};
        
        xScaleSet[0] = xSet[0];
        yScaleSet[0] = ySet[0];
        xScaleSet[1] = xSet[0]+(xSet[1] - xSet[0])*SCALE;
        yScaleSet[1] = ySet[0]+(ySet[1] - ySet[0])*SCALE;
        xScaleSet[2] = xSet[3]+(xSet[2] - xSet[3])*SCALE;
        yScaleSet[2] = ySet[3]+(ySet[2] - ySet[3])*SCALE;
        xScaleSet[3] = xSet[3];
        yScaleSet[3] = ySet[3];
        
        pX[0]  = xScaleSet[0];
        pY[0]  = yScaleSet[0];
        
        pX[1]  = 3 * (xScaleSet[1] - xScaleSet[0]);                   // Cx = 3(X1 - X0)
        pY[1]  = 3 * (yScaleSet[1] - yScaleSet[0]);                   // Cy = 3(Y1 - Y0)
        
        pX[2]  = 3 * (xScaleSet[2] - xScaleSet[1])- pX[1];            // Bx = 3(X2 - X1) - Cx
        pY[2]  = 3 * (yScaleSet[2] - yScaleSet[1])- pY[1];            // By = 3(Y2 - Y1) - Cy
        
        pX[3]  =     (xScaleSet[3] - xScaleSet[0]) - pX[1] - pX[2];   // Ax = (X3 - X0) - Cx - Bx
        pY[3]  =     (yScaleSet[3] - yScaleSet[0]) - pY[1] - pY[2];   // Ay = (Y3 - Y0) - Cy - By      
    }
   
    public double X(double t)
    {
        double X = 0;
        for (int i = 0; i < degree; i++)
            X = X + pX[i] * Math.pow(t, (double)i);
   
        return X;
    }
    
    public double Y(double t)
    {
        double Y = 0;
        for (int i = 0; i < degree; i++)
            Y = Y + pY[i] * Math.pow(t, (double)i);
   
        return Y;
    }
    
    public Location2D getCP(int index)
    {
        return new Location2D((int)xSet[index] * SCALE, (int)ySet[index] * SCALE);
    }
    
    public double[][] getCurve(int n)
    // Returns the set of points of the curve
    //
    // [X0,Y0], [X1, Y1], ... ,[XnumPoints - 1, YnumPoints - 1]
    // where Xi = xyCurve[i][0]
    //       Yi = xyCurve[i][1]
    {
        double[][] xyCurve = new double[n][n];
        double t = 0;
        int i = 0;
        
        while (t <= 1)
        {
            xyCurve[i][0] = X(t);
            xyCurve[i][1] = Y(t);
            
            //System.out.println("xyCurve["+i+"] = ("+xyCurve[i][0]+", "+xyCurve[i][1]);
            
            t = t + (double)1 / n;
            i++;
            if (i >= n)
                i = n-1;
        }
        return xyCurve;
    }
    
    public double[] getXSet(){ return xSet; }
    
    public double[] getYSet(){ return ySet; }
    
    public void Refresh()
    {
        repaint();
    }
    
     public void paintComponent(Graphics g) {
        if (displayON)
        {
            //clear(g);
            Graphics2D g2d = (Graphics2D)g;
            this.setLocation(0,0);
            int n = 50;
            
            if (once > -2)
            {
                double[][] xyCurve = getCurve(n);

                for (int i=0; i < n; i++)
                    {
                        Ellipse2D.Double elipse = new Ellipse2D.Double( xyCurve[i][0]-1, xyCurve[i][1]-1, 2, 2);
                        g2d.setColor(Color.red);
                        g2d.draw(elipse);
                        g2d.fill(elipse);
                    }
                once ++;
            }
            
            if (showBezierHandles)
            {
                Line2D.Double line1 = new Line2D.Double(xSet[0], ySet[0], xSet[1], ySet[1]);
                Line2D.Double line2 = new Line2D.Double(xSet[degree-1], ySet[degree-1], xSet[degree-2], ySet[degree-2]);
                Ellipse2D.Double elipse1 = new Ellipse2D.Double(xSet[1]-5, ySet[1]-5, 10,10);
                Ellipse2D.Double elipse2 = new Ellipse2D.Double(xSet[degree-2]-5, ySet[degree-2]-5, 10,10);
                
                if (lineSourceSelected)
                    g2d.setColor(Color.red);
                else
                    g2d.setColor(Color.white);
                g2d.draw(line1);
                g2d.fill(elipse1);
                
                if (lineDestinationSelected)
                    g2d.setColor(Color.red);
                else
                    g2d.setColor(Color.white);                   
                g2d.draw(line2);              
                g2d.fill(elipse2);
            }
        }
     }
   
     
    protected void clear(Graphics g) {
        super.paintComponent(g);
    }     
    
    
    
    public void mouseDragged(MouseEvent e) {
       int currentX, currentY;
            
       currentX = e.getX();
       currentY = e.getY();
   
       if (lineSourceSelected)
       {
           xSet[1] = currentX;
           ySet[1] = currentY;
       }
       
       if (lineDestinationSelected)
       {
           xSet[degree-2] = currentX;
           ySet[degree-2] = currentY;
       }
        
        computeParams();
        repaint();
    }
    
    public void mouseMoved(MouseEvent e) {
 
    }
    
    public void mouseClicked(MouseEvent e) {
       
    }
    
    public void mouseEntered(MouseEvent e) {
    }
    
    public void mouseExited(MouseEvent e) {
    }
    
    public void mousePressed(MouseEvent e) {
        
        if (e.getButton() == e.BUTTON1)
        {
            if (e.getX() < xSet[1] + 15 && e.getX() > xSet[1] - 15 &&
                e.getY() < ySet[1] + 15 && e.getY() > ySet[1] - 15)
                lineSourceSelected = true;
            else if (e.getX() < xSet[degree-2] + 15 && e.getX() > xSet[degree-2] - 15 &&
                     e.getY() < ySet[degree-2] + 15 && e.getY() > ySet[degree-2] - 15)
                     lineDestinationSelected = true;                 
            else
            {
                removeMouseMotionListener( this );
                removeMouseListener( this );
                showBezierHandles = false;
            }
            
            repaint();
        }
        else
        {
            removeMouseMotionListener( this );
            removeMouseListener( this );
            showBezierHandles = false;
        }
            
           
    }
    
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == e.BUTTON1)
        {
            lineSourceSelected = false;
            lineDestinationSelected = false;
            repaint();
        }
    }
 
    public void actionPerformed(ActionEvent e) {
        System.out.println("Bezier Action Listener");
        
        if (e.getActionCommand() == menuShowText)
           showBezierHandles = !showBezierHandles;
        if(showBezierHandles == true)
            System.out.println("true");
        else
            System.out.println("false");
        
        if (showBezierHandles)
        {
            addMouseMotionListener( this );
            addMouseListener( this );
        }
        else
        {
            removeMouseMotionListener( this );
            removeMouseListener( this );
        }
        repaint();
    }    
    
    
}
