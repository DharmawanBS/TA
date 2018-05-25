/*
 * RangeECGRID.java
 *
 * Created on June 13, 2006, 10:05 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.misc;

/**
 *
 * @author Oliviu Ghica
 */
public class RangeECGRID {
    /*  Defines a rectangle in grid space by two corner coordinates */
    int rowGridID1, colGridID1;
    int rowGridID2, colGridID2;
    
    /** Creates a new instance of RangeECGRID */
    public RangeECGRID(int rowGridID1, int colGridID1, int rowGridID2, int colGridID2) {
        this.rowGridID1 = rowGridID1;
        this.colGridID1 = colGridID1;
        this.rowGridID2 = rowGridID2;
        this.colGridID2 = colGridID2;
    }
    
    public boolean isWithinRange(int rowGridID, int colGridID)
    {
        if (rowGridID1 > rowGridID2)
        {
            if (rowGridID > rowGridID1 || rowGridID < rowGridID2)
                return false;
        }
        else
            if (rowGridID > rowGridID2 || rowGridID < rowGridID1)
                return false;
        
        if (colGridID1 > colGridID2)
        {
            if (colGridID > colGridID1 || colGridID < colGridID2)
                return false;
        }
        else
            if (colGridID > colGridID2 || colGridID < colGridID1)
                return false;
     
        return true;
    }
    
}
