package sidnet.models.deployment.models.discrepancy;

public interface ShapeGenerator {

	void reset();

	boolean hasNext();

	Shape getNext();
}
