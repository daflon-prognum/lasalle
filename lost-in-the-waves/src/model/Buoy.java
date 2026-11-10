package model;

/** Boia de sinalizacao. */
public class Buoy extends Obstacle {

	public Buoy(double x) {
		super(x, 32, 42);
	}

	@Override
	public ObstacleType getType() {
		return ObstacleType.BUOY;
	}
}
