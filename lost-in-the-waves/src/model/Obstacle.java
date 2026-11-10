package model;

import java.awt.geom.Rectangle2D;

/**
 * Obstaculo generico. Cada tipo concreto (Rock, Shark, Whirlpool...) herda desta
 * classe e pode sobrescrever velocidade extra, area de colisao e interacao.
 */
public abstract class Obstacle extends GameObject {

	public Obstacle(double x, int width, int height) {
		super(x, Surfer.FLOOR, width, height);
	}

	public abstract ObstacleType getType();

	/** Velocidade adicional em relacao ao mar (ex.: o tubarao nada mais rapido). */
	public double getSpeedBonus() {
		return 0;
	}

	/** Efeito do obstaculo sobre a surfista mesmo sem colisao (ex.: redemoinho). */
	public void interact(Surfer surfer) {
	}

	@Override
	public void move(double speed) {
		super.move(speed + getSpeedBonus());
	}

	@Override
	public Rectangle2D getBounds() {
		return new Rectangle2D.Double(x + 5, Surfer.FLOOR - height + 6, width - 10, height - 7);
	}

	/** Fabrica de obstaculos a partir do tipo. */
	public static Obstacle create(ObstacleType type, double x) {
		switch (type) {
		case ROCK:
			return new Rock(x);
		case BUOY:
			return new Buoy(x);
		case WAVE:
			return new Wave(x);
		case GULL:
			return new Gull(x);
		case LOG:
			return new Log(x);
		case SHARK:
			return new Shark(x);
		case WHIRLPOOL:
			return new Whirlpool(x);
		default:
			return new Whale(x);
		}
	}
}
