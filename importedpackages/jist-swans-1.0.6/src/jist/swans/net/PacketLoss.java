//////////////////////////////////////////////////
// JIST (Java In Simulation Time) Project
// Timestamp: <PacketLoss.java Tue 2004/04/06 11:32:52 barr pompom.cs.cornell.edu>
//

// Copyright (C) 2004 by Cornell University
// All rights reserved.
// Refer to LICENSE for terms and conditions of use.

package jist.swans.net;

import jist.swans.Constants;

/**
 * Packet loss models.
 *
 * @author Rimon Barr &lt;barr+jist@cs.cornell.edu&gt;
 * @version $Id: PacketLoss.java,v 1.6 2004/04/06 16:07:49 barr Exp $
 * @since SWANS1.0
 */

public interface PacketLoss
{
  /**
   * Decide whether to drop a packet.
   *
   * @param msg message being processed
   * @return whether packet should be dropped
   */
  boolean shouldDrop(NetMessage msg);

  //////////////////////////////////////////////////
  // Zero packet loss
  //

  /**
   * No packet loss.
   */
  public static class Zero implements PacketLoss
  {
    /** {@inheritDoc} */
    public boolean shouldDrop(NetMessage msg)
    {
      return false;
    }
  }

  //////////////////////////////////////////////////
  // Uniform probability packet loss
  //

  /**
   * Uniformly random packet loss.
   */
  public static class Uniform implements PacketLoss
  {
    /** 
     * packet loss probability.
     */
    private double prob;

    /**
     * Initialize uniform packet loss model.
     *
     * @param prob packet loss probability
     */
    public Uniform(double prob)
    {
      this.prob = prob;
    }

    /** {@inheritDoc} */
    public boolean shouldDrop(NetMessage msg)
    {
      return Constants.random.nextDouble()<prob;
    }
  }

} // class: PacketLoss
