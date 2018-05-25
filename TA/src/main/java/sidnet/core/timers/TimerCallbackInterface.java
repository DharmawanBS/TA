package sidnet.core.timers;

import jist.runtime.JistAPI;

public interface TimerCallbackInterface
extends JistAPI.Proxiable {
	public void timeout(long timerID);
}
