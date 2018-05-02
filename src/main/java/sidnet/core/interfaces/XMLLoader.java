/*
 * XMLLoader.java
 *
 * Created on November 21, 2007, 9:48 AM
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
public interface XMLLoader {
    public void enableLoaderFromXML(String xmlSchemaDirectory, String xsdSchemaFilename, String frameTitle, Class pojoDescriptor, XMLLoaderListener xmlLoaderListener);
}
