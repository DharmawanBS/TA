package sidnet.core.misc;

import jist.runtime.JistAPI;

@Deprecated
public class GeneralPurposeTimer
implements TimerInterface {

	private long timerSequence = 0;
	private boolean ready;
	private TimerCallbackInterface callback;
	
	 /** self-referencing proxy entity. */
    private Object self;
	
	public GeneralPurposeTimer(TimerCallbackInterface callback) {
		ready = true;
		this.callback = callback;
		this.self = 
			JistAPI.proxyMany(this, new Class[] { TimerInterface.class });
	}
	
	public void cancel() {
		timerSequence++;
		ready = true;
	}
	
	public boolean isReady() {
		return ready;
	}

	public void startTimer(double wtime) {
		cancel();
		ready = false;
		long startedOnSequence = timerSequence;
		JistAPI.sleepBlock((long)wtime);
		if (startedOnSequence == timerSequence)
			timeout();
	}

	public void timeout() {
		timerSequence++;
		ready = true;
		((TimerCallbackInterface)callback).timeout();		
	}
	
	 public TimerInterface getProxy() {
	        return (TimerInterface)self;
	 }
}
