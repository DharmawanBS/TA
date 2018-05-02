/*
 * DeploymentModel.java
 *
 * Created on April 8, 2008, 11:20 AM
 *
 * @author Oliviu C. Ghica, Northwestern University
 * @version 1.0
 */

package sidnet.models.deployment.interfaces;

import jist.swans.field.Placement;


/**
 *  Marker for classes that provide deployment model functionality
 */
public interface DeploymentModel {
    /**
     *  Must be implemented, by design, as a blocking call.
     *  It should return only when user completed configuring
     *  the deployment parameters and clicked "DEPLOY"
     *  
     *  @return a Placement object
     */
    public Placement getPlacement();
}
