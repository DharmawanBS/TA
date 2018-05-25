/*
 * CallbackInterface.java
 *
 * Created on October 25, 2007, 8:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sidnet.core.interfaces;

/**
 *
 * @author Oliviu Ghica, Northwestern University
 *
 * Helper interface to notify the implementing class of an event coming from upper layers that must be acted upon
 */

public interface CallbackInterface {
    public void signalUserRequest();
}
