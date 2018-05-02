package sidnet.batch;

import java.util.Iterator;

public class ExperimentEnumerator 
implements Iterator<String> {
	
	private long runID,
				 numberOfExperimentsPerRepeat,
				 numberOfRepeats,
				 firstRepeat;
	
	private long currentExperimentNumber,
				 currentRepeatNumber;
	
	private long totalNumberOfSuccesses,
	             totalNumberOfFailures,
	             numberOfSuccessesWithinRepeat,
	             numberOfFailuresWithinRepeat;
	
	/**
	 * 
	 * @param runID
	 * @param numberOfExperimentsPerRepeat
	 * @param numberOfRepeats
	 * @param firstRepeat
	 */
	public ExperimentEnumerator(long runID,
								long numberOfExperimentsPerRepeat,
								long numberOfRepeats,
								long firstRepeat) {
		this.runID = runID;
		this.numberOfExperimentsPerRepeat = numberOfExperimentsPerRepeat;
		this.numberOfRepeats = numberOfRepeats;
		this.firstRepeat = firstRepeat;
		this.currentRepeatNumber = firstRepeat;
		this.currentExperimentNumber = 0;
		this.numberOfSuccessesWithinRepeat = 0;
		this.numberOfFailuresWithinRepeat = 0;
		this.totalNumberOfSuccesses = 0;
		this.totalNumberOfFailures = 0;
	}

	public boolean hasNext() {
		return (currentRepeatNumber < firstRepeat + numberOfRepeats - 1&&
			    currentExperimentNumber <= numberOfExperimentsPerRepeat) ||
			   (currentRepeatNumber == firstRepeat + numberOfRepeats - 1&&
			    currentExperimentNumber < numberOfExperimentsPerRepeat);
	}

	public String next() {
		if (!hasNext())
			throw new RuntimeException("Iterator out of limits. Have you checked if there are any elements left?");
		
		increment();		
		
		return "run" + runID + "-rpt" + currentRepeatNumber + "-exp" + currentExperimentNumber;
	}
	
	public void countSuccess() {
		numberOfSuccessesWithinRepeat++;
		totalNumberOfSuccesses++;
	}
	
	public void countFailure() {
		numberOfFailuresWithinRepeat++;
		totalNumberOfFailures++;
	}

	public void remove() {
		throw new RuntimeException("not implemented");		
	}
	
	public long getNumberOfExperimentsPerRepeat() {
		return numberOfExperimentsPerRepeat;
	}
	
	public long getNumberOfRepeats() {
		return numberOfRepeats;
	}
	
	public long getRunID() {
		return runID;		
	}
	
	public long getCurrentExperimentNumber() {
		return currentExperimentNumber;
	}
	
	public long getCurrentRepeatNumber(){
		return currentRepeatNumber;
	}
	
	public long getTotalNumberOfSuccesses() {
		return totalNumberOfSuccesses;
	}
	
	public long getTotalNumberOfFailures() {
		return totalNumberOfFailures;
	}
	
	public long getNumberOfSuccessesWithinRepeat() {
		return numberOfSuccessesWithinRepeat;
	}
	
	public long getNumberOfFailuresWithinRepeat() {
		return numberOfFailuresWithinRepeat;
	}
	
	public long getTotalNumberOfExperiments() {
		return numberOfRepeats * numberOfExperimentsPerRepeat;
	}
	
	public long getTotalNumberOfExperimentsRunSoFar() {
		return totalNumberOfSuccesses + totalNumberOfFailures;
	}
	
	public long getTotalNumberOfExperimentsLeft() {
		return getTotalNumberOfExperiments() - getTotalNumberOfExperimentsRunSoFar();
	}
	
	public long getTotalNumberOfExperimentsLeftWithinRepeat() {
		return numberOfExperimentsPerRepeat - numberOfSuccessesWithinRepeat - numberOfFailuresWithinRepeat;
	}
	
	public long getTotalNumberOfSuccessesWithinRepeat() {
		return numberOfSuccessesWithinRepeat;
	}
	
	public long getTotalNumberOfFailuresWithinRepeat() {
		return numberOfFailuresWithinRepeat;
	}
	
	private void increment() {
		currentExperimentNumber++;
		
		if (currentExperimentNumber > numberOfExperimentsPerRepeat) {
			currentExperimentNumber = 1;
			currentRepeatNumber ++;
			numberOfSuccessesWithinRepeat = 0;
			numberOfFailuresWithinRepeat = 0;
		}		
	}
}