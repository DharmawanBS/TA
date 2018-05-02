package sidnet.models.deployment.models.xml;

import java.util.Iterator;

import jist.swans.field.Placement;
import jist.swans.misc.Location;
import jist.swans.misc.Location.Location2D;

public class XMLPlacement implements Placement {
    @SuppressWarnings("unchecked")
	private Iterator locationDataIterator;
    private int size;
    private int fieldWidth;
    private int fieldLength;
    
    public XMLPlacement(LocationData locationData, int fieldLength, int fieldWidth) {
        this.locationDataIterator = (locationData.nodesList).iterator();
        this.size = locationData.nodesList.size();
        
        this.fieldLength = fieldLength;
        this.fieldWidth = fieldWidth;
    }
    
    public int getSize() {
        return size;
    }
    
     /**
       * Return location of next node.
       *
       * @return location of next node
       */
     public Location getNextLocation()
     {
         LocationData.Loc nextNode = null;
         if (locationDataIterator.hasNext())
            nextNode = (LocationData.Loc)locationDataIterator.next();
         if (nextNode != null)
            return new Location2D(nextNode.x * fieldWidth, nextNode.y * fieldLength);
         else
            return null;
     }
}