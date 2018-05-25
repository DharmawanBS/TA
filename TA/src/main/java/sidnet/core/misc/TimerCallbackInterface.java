package sidnet.core.misc;

import jist.runtime.JistAPI;

@Deprecated
public interface TimerCallbackInterface
extends JistAPI.Proxiable {
	public void timeout();
}
