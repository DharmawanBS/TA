package sidnet.core.timers;

import jist.runtime.JistAPI;
import jist.runtime.JistAPI.Continuable;
import jist.runtime.JistAPI.Continuation;

public class JistTimer 
implements JistTimerInterface {
	private long timerID;
	private JistTimerInterface self;
	private boolean busy;
	private TimerCallbackInterface callback;
	private long timerSequence = 0;
	private boolean expired;
	
	public JistTimer(long timerID, TimerCallbackInterface callback) {		
		this.timerID = timerID;
		this.self = (JistTimerInterface)JistAPI.proxy(this, JistTimerInterface.class);
		this.callback = callback;
		busy = false;
		expired = false;
	}
	
	public void startTimer(long wtime) {
		/* Since we cannot throw exceptions due to Rewriter limitations,
		 * we don't signal if timer is already busy. The user
		 * needs to check for this beforehand
		 */
		
		assert !busy; // suggestion: check if it is busy before calling the startTimer();
		
		if (busy) { // if assertions are disabled ... Cannot use exceptions due to Rewriter ... :(
			//System.err.println("[JistTimer] <ERROR> - Attempting to start a timer (timerID: " + timerID + ") that has already started.");
			//return;
			throw new RuntimeException("[JistTimer] <ERROR> - Attempting to start a timer (timerID: " + timerID + ") that has already started.");
		}
		
		//reset();
		busy = true;
		expired = false;
		
		/* If the timer is to be reused, this makes sure it does not conflict 
		 * with its previous instance that supposingly was canceled 
		 */
		long matchedTimerSequence = ++timerSequence;
		//System.out.println("Timer #" + timerID + " started");
		JistAPI.sleepBlock(wtime);
		
		if (busy && matchedTimerSequence == timerSequence)
			timeout();
	}
	
	public boolean isBusy(){
		return busy;
	}
	
	public void cancelAndReset(){
		reset();
	}

	private void timeout() {
		//System.out.println("Timer #" + timerID + " timeout");
		assert busy;
		assert !expired;
		
		busy = false;
		expired = true;
		callback.timeout(timerID);
	}
	
	private void reset() {
		//System.out.println("Timer #" + timerID + " reset");
		busy = false;
		expired = false;
	}

	public boolean expired(){
		return expired;
	}
	
	public JistTimerInterface getProxy() {
		return self;
	}
}
