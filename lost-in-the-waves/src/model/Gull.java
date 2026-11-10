package model;

import java.awt.geom.Rectangle2D;

/** Gaivota: voa baixo, entao a surfista precisa agachar para passar por baixo. */
public class Gull extends Obstacle {

	public static final int FLIGHT_HEIGHT = 59;

	public Gull(double x) {
		super(x, 54, 20);
	}

	@Override
	public ObstacleType getType() {
		return ObstacleType.GULL;
	}

	@Override
	public Rectangle2D getBounds() {
		return new Rectangle2D.Double(x + 5, Surfer.FLOOR - FLIGHT_HEIGHT, width - 10, height - 7);
	}
}
