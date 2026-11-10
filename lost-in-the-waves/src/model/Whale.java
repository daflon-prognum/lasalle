package model;

/** Baleia: o maior obstaculo do oceano. */
public class Whale extends Obstacle {

	public Whale(double x) {
		super(x, 94, 47);
	}

	@Override
	public ObstacleType getType() {
		return ObstacleType.WHALE;
	}
}
