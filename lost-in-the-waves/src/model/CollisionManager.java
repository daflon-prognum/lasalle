package model;

/** Centraliza as regras de colisao entre a surfista e os demais objetos. */
public class CollisionManager {

	public boolean hits(Surfer surfer, Obstacle obstacle) {
		return surfer.collidesWith(obstacle);
	}

	public boolean collects(Surfer surfer, Item item) {
		return surfer.collidesWith(item);
	}
}
