package model;

/** Tronco boiando. */
public class Log extends Obstacle {

	public Log(double x) {
		super(x, 64, 22);
	}

	@Override
	public ObstacleType getType() {
		return ObstacleType.LOG;
	}
}
