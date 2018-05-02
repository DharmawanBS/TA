/*
 * Path.java
 *
 * Created on July 16, 2007, 10:25 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.misc;

/**
 *
 * @author zbisch
 */
public class Path 
{
    private int x[];
    private int y[];
    private long time[];
    private int numberOfPoints;
    
    /** Creates a new instance of Path */
    public Path(int x, int y, long time) 
    {
        this.x = new int[1];
        this.y = new int[1];
        this.time = new long[1];
        this.numberOfPoints = 1;
        this.x[0] = x;
        this.y[0] = y;
        this.time[0] = time;
    }
    
    public void addPoint(int x, int y, long time)
    {
        int tempX[] = new int[numberOfPoints+1];
        int tempY[] = new int[numberOfPoints+1];
        long tempT[] = new long[numberOfPoints+1];
        
        for(int i=0;i<numberOfPoints;i++)
        {
            tempX[i] = this.x[i];
            tempY[i] = this.y[i];
            tempT[i] = this.time[i];
        }
        tempX[numberOfPoints] = x;
        tempY[numberOfPoints] = y;
        tempT[numberOfPoints] = time;
        
        this.x = tempX;
        this.y = tempY;
        this.time = tempT;
        numberOfPoints++;
    }
    public int getNumberOfPoints()
    {
        return numberOfPoints;
    }
    public int xByIndex(int i)
    {
        if(i < numberOfPoints)
        {
            return x[i];
        }
        return -1;
    }
    public int yByIndex(int i)
    {
        if(i < numberOfPoints)
        {
            return y[i];
        }
        return -1;
    }
    public long timeByIndex(int i)
    {
        if(i < numberOfPoints)
        {
            return time[i];
        }
        return -1;
    }
    public int getCurrentPoint(long currentTime)
    {
        int tempPoint = -1;
        for(int i = 0; i < numberOfPoints; i++)
        {
            if(currentTime > time[i])
            {
                tempPoint = i;
            }
        }
        return tempPoint;
    }
}
