package sidnet.models.deployment.models.discrepancy;

/*
 * @author Oliviu C. Ghica
 * @version 0.2
 * @date 10/07/2009
 */

import java.util.LinkedList;
import java.util.List;

import sidnet.core.misc.NCS_Location2D;
import jist.swans.field.Placement;
import jist.swans.misc.Location;

public class DiscrepancyControlledPlacement implements Placement{

	private double targetDiscrepancy = 0;
	private double currentDiscrepancy = 1;
	private double currentError = 1;
	private double targetError = 0.001; 
	private List<Location> locations = new LinkedList<Location>();
	private java.util.Random rand;
	private int width, length;
	private ShapeGenerator shapeGen;
	private int N;
	private int index = 0;
	double radix = 0;
	double radixIncrement = 0.01;
	
	public DiscrepancyControlledPlacement(
			double targetDiscrepancy,
			int width,
			int length,
			int expectedNumberOfNodes, 
			long seed) {
		this.targetDiscrepancy = targetDiscrepancy;
		rand = new java.util.Random(seed);
		this.width = width;
		this.length = length;
		//shapeGen = new RectangleGenerator(width, length, levels);
		shapeGen = new CircleGenerator(width, length, seed);
		N = expectedNumberOfNodes;
		
		generateGrid();
		
		currentDiscrepancy = calculateDiscrepancy(null);
		currentError = Math.abs(targetDiscrepancy - currentDiscrepancy);
		System.out.println("currentDiscrepancy = " + currentDiscrepancy);
		
		perturbToDiscrepancy();
		
		currentDiscrepancy = calculateDiscrepancy(null);
		currentError = Math.abs(targetDiscrepancy - currentDiscrepancy);
		
		System.out.println("currentDiscrepancy = " + currentDiscrepancy);
		
		// Calculate Heinrich discrepancy
		List<NCS_Location2D> ncsLocations = new LinkedList<NCS_Location2D>();
		int i = 0;
		for(Location loc: locations) {			
			if (i < N) {
				i++;
				ncsLocations.add(new NCS_Location2D( ((double)loc.getX()) / length, ((double)loc.getY()) / length));
			}
		}
		System.out.println("Heinrich Discrepancy = " + HeinrichDiscrepancy.calculateDiscrepancy(ncsLocations));
		
		// Calculate Tovstik discrepancy
		System.out.println("Calculating Tovstik discrepancy ... ");
		
		System.out.println("Tovstik Discrepancy = " + calculateTovstikDiscrepancy(ncsLocations));
	}
	
	public Location getNextLocation() {
		return locations.get(index++);
	}

	public int getSize() {
		return locations.size();
	}

	private boolean converges(Location newLocation) {
		double newDiscrepancy = calculateDiscrepancy(newLocation);
		//System.out.println("newDisc = " + newDiscrepancy + " error = " + Math.abs(newDiscrepancy - targetDiscrepancy) + " currentError = " + discrepancyError);
		if (Math.abs(newDiscrepancy - targetDiscrepancy) <= targetError )
			return true;
			
		return false;			
	}
	
	private void store(Location newLocation) {
		currentDiscrepancy = calculateDiscrepancy(newLocation);
		locations.add(newLocation);		
		currentError = Math.abs(targetDiscrepancy - currentDiscrepancy);
	}
	
	private double calculateDiscrepancy(Location newLocation) {
		Shape shape;
		double minVal = 1;
		double sum = 0;
		int count = 0;
		
		if (newLocation != null)
			locations.add(newLocation);
		
		shapeGen.reset();		
		while(shapeGen.hasNext()) {
			shape = shapeGen.getNext();
			int n = shape.countPointsInside(locations);
			double newVal = Math.abs(((double)n)/locations.size() -
					shape.area()/(width * length));			
			sum += newVal;
			count++;
		}
		
		if (newLocation != null)
			locations.remove(newLocation);
		
		//return minVal;
		return sum / count; 
	}	
	
	
	
	private static double calculateTovstikDiscrepancy(List<NCS_Location2D> P) {
		
		// insert (0,0) and (1,1)
		P.add(0, new NCS_Location2D(0,0));
		P.add(P.size(), new NCS_Location2D(1,1));
		
		int N = P.size();
		
		NCS_Location2D[] x_star = new NCS_Location2D[N];
		NCS_Location2D[] y_star = new NCS_Location2D[N];
		
		// Step 1. Order monothonically increasing the P-series by x-coordinate
		boolean done = false;
		while (!done) {
			done = true;
			for (int i = 0; i < N - 1; i++) {
				if (P.get(i).getX() > P.get(i+1).getX()) {
					NCS_Location2D tmp = P.remove(i);
					P.add(i, P.remove(i));
					P.add(i+1, tmp);
					done = false;
				}				
			}
		}
		
		// verification
		for (int i = 0; i < N - 1; i++) {
			assert(P.get(i).getX() <= P.get(i+1).getX());
			x_star[i] = P.get(i);
		}
		x_star[N - 1] = P.get(N - 1);
		
		// Step2. Order monothonically increasing the P-series by y-coordinate
		done = false;
		while (!done) {
			done = true;		
			for (int i = 0; i < N - 1; i++) {
				if (P.get(i).getY() > P.get(i+1).getY()) {
					NCS_Location2D tmp = P.remove(i);
					P.add(i, P.remove(i));
					P.add(i+1, tmp);
					done = false;
				}				
			}
		}
		
		// verification
		for (int i = 0; i < N - 1; i++) {
			assert(P.get(i).getY() <= P.get(i+1).getY());
			y_star[i] = P.get(i);
		}
		y_star[N - 1] = P.get(N - 1);
		
		// main algorithm	
		double d2 = 0;
		double s1 = 0;
		double s2 = 0;
		
		System.out.println("N = " + N);		
		
		for (int j = 1; j < N-1; j++) {
			for (int i = 1; i < N-1; i++) {
				int v = i;
				s1 = S(i, j, v, N-2, x_star, y_star);
				d2 = Math.max(d2, s1);
				s2 = S(i-1, j, v-1, N-2, x_star, y_star);
				d2 = Math.max(d2, s2);						
			}
			//System.out.println("d2 = " + d2);
		}
					
		return d2;
	}
	
	private static double S(int i, int j, int v, int N, 
							NCS_Location2D[] x_star,
						    NCS_Location2D[] y_star) {
		return Math.max(
				((double)v)/N - x_star[i].getX()*y_star[j].getY(), 
				x_star[i+1].getX()*y_star[j+1].getY() - ((double)v)/N);
	}
	
	private void generateGrid() {
		int numCond = (int)Math.pow(Math.ceil(Math.sqrt(N)),2);
		int numCols = (int)Math.sqrt(numCond);
		int numRows = numCond / numCols;
		//System.out.println("numCols = " + numCols + " numRows = " + numRows);
		
		for (int i = 0; i < numCols; i++)
			for (int j = 0; j < numRows; j++) {
				locations.add(
						new Location.Location2D((float)(((double)i + 0.5) * (((double)width)/numCols)),
												(float)(((double)j + 0.5) * (((double)length)/numRows))));
			}				
	}
	
	private void perturbToDiscrepancy() {
		int contor = 0;				
		double candidateDiscrepancy;
		double candidateError;
		
		// and try to relocate it such that the discrepancy converges towards the target
		do {
			if (contor % 1000 == 0)
				radix += radixIncrement;
			perturbOneNode();
			candidateDiscrepancy = calculateDiscrepancy(null);
			candidateError = Math.abs(candidateDiscrepancy - targetDiscrepancy);
			//System.out.println("perturb one node - candidateError = " + candidateError + " candidateDiscrepancy = " + candidateDiscrepancy);
			currentDiscrepancy = candidateDiscrepancy;
			currentError = candidateError;
			contor++;
		} while (currentError > targetError && contor < 10000);
		
		// finish when no more progress towards the target can be made		
	}
	
	private void perturbOneNode() {
		int contor = 0;
		double candidateDiscrepancy;
		double candidateError = 1;	
		Location loc = null;
		
		int angle = 0;
		// randomly pick a node
		int nodeNum = rand.nextInt(N);
		Location oldloc = locations.remove(nodeNum);
		double origX = oldloc.getX();
		double origY = oldloc.getY();
		
		while (candidateError > currentError && contor < N) {			
			angle = rand.nextInt(360);
			//loc = new Location.Location2D(
			//		rand.nextFloat() * width,
			//		rand.nextFloat() * length);
			double x = origX + (double)radix * width * Math.cos (Math.toRadians(angle));
			double y = origY + (double)radix * length * Math.sin (Math.toRadians(angle));
			if (x < 0)
				x = -x;
			if (x > width) 
				x = x % width;
			
			if (y < 0)
				y = -y;
			if (y > length) 
				y = y % length;
			
			loc = new Location.Location2D((float)x, (float)y);
			candidateDiscrepancy = calculateDiscrepancy(loc);
			candidateError = Math.abs(candidateDiscrepancy - targetDiscrepancy);		
			contor++;			
		} 
		
		if (candidateError > currentError)
			locations.add(oldloc);
		else	
			locations.add(loc);
	}
}
