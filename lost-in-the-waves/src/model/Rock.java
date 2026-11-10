package model;

/** Rocha parada na agua. */
public class Rock extends Obstacle {

	public Rock(double x) {
		super(x, 46, 31);
	}

	@Override
	public ObstacleType getType() {
		return ObstacleType.ROCK;
	}
}
