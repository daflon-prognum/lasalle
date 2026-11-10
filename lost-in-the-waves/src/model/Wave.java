package model;

/** Onda que quebra na frente da surfista. */
public class Wave extends Obstacle {

	public Wave(double x) {
		super(x, 72, 38);
	}

	@Override
	public ObstacleType getType() {
		return ObstacleType.WAVE;
	}
}
