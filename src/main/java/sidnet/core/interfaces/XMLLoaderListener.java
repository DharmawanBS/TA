/*
 * XMLLoaderListener.java
 *
 * Created on November 21, 2007, 10:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.interfaces;

import java.util.List;

/**
 *
 * @author Oliver
 */
public interface XMLLoaderListener {
    public void handleParsedObjects(List<Object> parsedListOfPOJOs);
}
