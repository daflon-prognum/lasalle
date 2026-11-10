package model;

import java.awt.geom.Rectangle2D;

/**
 * Classe base de todos os elementos que aparecem na tela do jogo.
 * Guarda posicao e tamanho e oferece a area usada na deteccao de colisoes.
 */
public abstract class GameObject {

	protected double x;
	protected double y;
	protected int width;
	protected int height;

	public GameObject(double x, double y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/** Area de colisao do objeto. As subclasses podem ajustar. */
	public Rectangle2D getBounds() {
		return new Rectangle2D.Double(x, y, width, height);
	}

	public boolean collidesWith(GameObject other) {
		return getBounds().intersects(other.getBounds());
	}

	/** Desloca o objeto para a esquerda (o mar "anda" em direcao a surfista). */
	public void move(double speed) {
		x -= speed;
	}

	public boolean isOffScreen() {
		return x + width < 0;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
