package model;

/** Fase do jogo: nome, velocidade do mar, intervalo entre obstaculos e perigos. */
public class Stage {

	private final String name;
	private final double speed;
	private final int interval;
	private final ObstacleType[] hazards;

	public Stage(String name, double speed, int interval, ObstacleType... hazards) {
		this.name = name;
		this.speed = speed;
		this.interval = interval;
		this.hazards = hazards;
	}

	public String getName() {
		return name;
	}

	public double getSpeed() {
		return speed;
	}

	public int getInterval() {
		return interval;
	}

	public ObstacleType[] getHazards() {
		return hazards;
	}
}
