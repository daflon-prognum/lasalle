package model;

/** Tubarao: avanca em direcao a surfista mais rapido que o mar. */
public class Shark extends Obstacle {

	public Shark(double x) {
		super(x, 64, 22);
	}

	@Override
	public ObstacleType getType() {
		return ObstacleType.SHARK;
	}

	@Override
	public double getSpeedBonus() {
		return 1.45;
	}
}
