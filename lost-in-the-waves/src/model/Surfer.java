package model;

import java.awt.geom.Rectangle2D;

/**
 * Personagem controlada pelo jogador. A coordenada y representa a linha dos pes.
 */
public class Surfer extends GameObject {

	public static final int WIDTH = 68;
	public static final int HEIGHT = 72;
	public static final int FLOOR = 414;
	public static final int CROUCH_HEIGHT = 42;
	private static final int MIN_X = 35;
	private static final int MAX_X = 310;
	private static final double STEP = 4.4;
	private static final double JUMP_FORCE = -13.8;
	private static final double GRAVITY = 0.67;

	private double velocity;
	private boolean landed;
	private boolean crouching;

	public Surfer() {
		super(132, FLOOR, WIDTH, HEIGHT);
		reset();
	}

	public void reset() {
		x = 132;
		land();
	}

	public void land() {
		y = FLOOR;
		velocity = 0;
		landed = true;
		crouching = false;
	}

	public void jump() {
		if (landed && !crouching) {
			velocity = JUMP_FORCE;
			landed = false;
		}
	}

	/** Move na horizontal (direcao -1, 0 ou 1) e aplica a gravidade do pulo. */
	public void move(int direction) {
		push(direction * STEP);
		if (!landed) {
			y += velocity;
			velocity += GRAVITY;
			if (y >= FLOOR) {
				land();
			}
		}
	}

	/** Empurra a surfista na horizontal respeitando os limites da tela. */
	public void push(double dx) {
		x = Math.max(MIN_X, Math.min(MAX_X, x + dx));
	}

	@Override
	public Rectangle2D getBounds() {
		int h = crouching ? CROUCH_HEIGHT : 62;
		return new Rectangle2D.Double(x + 8, y - h, WIDTH - 16, h - 3);
	}

	public boolean isLanded() {
		return landed;
	}

	public boolean isCrouching() {
		return crouching;
	}

	public void setCrouching(boolean crouching) {
		this.crouching = crouching;
	}
}
