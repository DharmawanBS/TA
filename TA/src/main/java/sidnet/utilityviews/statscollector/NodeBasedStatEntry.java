package sidnet.utilityviews.statscollector;

import sidnet.core.misc.Node;

public interface NodeBasedStatEntry {

	/**
     * User-defined processing (update of inner statistical values) based on the informations comprised in the Node
     * The implementation must call this method first
     */
   public void update(Node[] nodes);
}
