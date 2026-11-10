package model;

/** Redemoinho: puxa a surfista para o centro quando ela esta na agua. */
public class Whirlpool extends Obstacle {

	private static final int RANGE = 110;
	private static final double PULL = 2;

	public Whirlpool(double x) {
		super(x, 64, 22);
	}

	@Override
	public ObstacleType getType() {
		return ObstacleType.WHIRLPOOL;
	}

	@Override
	public void interact(Surfer surfer) {
		double gap = x - surfer.getX();
		if (surfer.isLanded() && Math.abs(gap) < RANGE) {
			surfer.push(Math.signum(gap) * PULL);
		}
	}
}
