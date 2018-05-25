package sidnet.core.gui.animations;

import javax.swing.JPanel;
import sidnet.core.misc.Location2D;
import jist.runtime.JistAPI.DoNotRewrite;

public abstract class AnimationModule implements DoNotRewrite, Runnable {
	protected static JPanel hostPanel;
	protected Location2D loc;
	
	protected static int COUNT_INSTANCES = 20;
	
	public void animateOn(Location2D loc, JPanel hostPanel) {
		AnimationModule.hostPanel = hostPanel;
		this.loc = loc;
	}	
	
	public boolean maxedOut() {
		return COUNT_INSTANCES <= 0;
	}
}
