package sidnet.core.timers;

import java.util.LinkedList;
import java.util.List;
import sidnet.core.misc.Queue;

public class JistTimerPool {
	private long timerPoolID;
	private TimerCallbackInterface callback;
	private List<JistTimer> readyPool;
	private Queue activeQueue;

	
	public JistTimerPool(long timerPoolID, TimerCallbackInterface callback) {
		this.timerPoolID = timerPoolID;
		readyPool = new LinkedList<JistTimer>();
		activeQueue = new Queue();
		this.callback = callback;
	}
	
	public JistTimer getFreshTimer() {
		checkActivePool();
		
		if (readyPool.size() == 0)
			readyPool.add(new JistTimer(timerPoolID, callback));
		
		JistTimer freshTimer = readyPool.remove(0);
		
		assert !freshTimer.isBusy();
		
		activeQueue.enQueue(freshTimer);
		
		return freshTimer;		
	}
	
	public int size() {
		return readyPool.size() + activeQueue.size();
	}
	
	private void checkActivePool() {
		if (activeQueue.size() == 0)
			return;
		
		JistTimer activeTimer = (JistTimer)activeQueue.deQueue();
		if (!activeTimer.isBusy())
			readyPool.add(0, activeTimer);
		else
			activeQueue.enQueue(activeTimer);
	}
}
