package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import model.GameModel;
import model.GameState;
import model.Item;
import model.Obstacle;
import model.Surfer;

/**
 * VIEW: painel onde o jogo e desenhado. Apenas le o estado do GameModel,
 * nunca altera as regras da partida.
 */
public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int W = GameModel.WIDTH;
	private static final int H = GameModel.HEIGHT;
	private static final Color INK = new Color(0x10263C);
	private static final Color FOAM = new Color(0xE5FAF8);
	private static final Color GOLD = new Color(0xFFCB68);

	private static final BufferedImage PERSON = Assets.SURFER.getSubimage(0, 0, Assets.SURFER.getWidth(),
			Assets.SURFER.getHeight() * 85 / 100);
	private static final BufferedImage SURF_BOARD = Assets.SURFER.getSubimage(0, Assets.SURFER.getHeight() * 85 / 100,
			Assets.SURFER.getWidth(), Assets.SURFER.getHeight() - Assets.SURFER.getHeight() * 85 / 100);
	private static final int PERSON_H = Surfer.HEIGHT * 85 / 100;
	private static final int BOARD_H = Surfer.HEIGHT - PERSON_H;

	private transient GameModel model;

	public GamePanel() {
		setPreferredSize(new Dimension(W, H));
		setBackground(INK);
		setFocusable(true);
		setDoubleBuffered(true);
	}

	public void setModel(GameModel model) {
		this.model = model;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics.create();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		if (model == null) { // modo de design do WindowBuilder
			drawBeachBackground(g);
			g.dispose();
			return;
		}
		switch (model.getState()) {
		case TITLE:
			drawBeachBackground(g);
			drawCard(g, "AVENTURA RETRÔ  /  JAVA SWING", "Lost in the Waves",
					"Sobreviva ao oceano e encontre a areia.", "ENTER  /  COMEÇAR");
			break;
		case WON:
			drawBeachBackground(g);
			drawEnding(g, model.getEndTicks());
			drawCaption(g, "EPÍLOGO  /  " + model.getPoints() + " PONTOS", "Enfim, a areia!",
					"A surfista desembarca. Sua jornada chegou ao fim.");
			break;
		default:
			drawSea(g);
			drawStateCard(g);
		}
		g.dispose();
	}

	private void drawSea(Graphics2D g) {
		int offset = (int) model.getScroll() % W;
		boolean flipped = model.getScroll() >= W;
		drawOcean(g, -offset, flipped);
		drawOcean(g, W - offset, !flipped);
		drawSwell(g);
		if (model.isStormy()) {
			drawStorm(g);
		}
		if (model.isBeachVisible()) {
			drawBeach(g);
		}
		if (model.getState() == GameState.INTRO) {
			drawCutscene(g);
			return;
		}
		for (Obstacle obstacle : model.getObstacles()) {
			SpriteRenderer.drawObstacle(g, obstacle, model.getTicks());
		}
		for (Item item : model.getItems()) {
			SpriteRenderer.drawItem(g, item);
		}
		if (model.isSurferVisible()) {
			drawSurfer(g);
		}
		if (model.hasShield() && model.getState() == GameState.PLAYING) {
			drawShield(g);
		}
	}

	private void drawStateCard(Graphics2D g) {
		switch (model.getState()) {
		case INTRO:
			String[] titles = { "Uma sessão de surfe", "A queda", "Encontre a areia" };
			String[] lines = { "A surfista seguia as ondas como em qualquer outro dia.",
					"Uma onda enorme a derruba da prancha.",
					"Ela recupera a prancha. A praia sumiu no horizonte." };
			int page = model.getIntro();
			drawCaption(g, "PRÓLOGO  " + (page + 1) + "/" + GameModel.INTRO_PAGES, titles[page], lines[page]);
			break;
		case ROUTE:
			drawCard(g, model.getStage().getName(), "Ainda há caminho",
					model.getLevel() == 1 ? "Entre as ondas, ela avista uma rota rumo à costa."
							: "Ela segue a corrente na direção da terra.",
					"ENTER  /  PRÓXIMA FASE");
			break;
		case PAUSED:
			drawCard(g, "INTERVALO", "Em pausa", "A partida está parada.", "P  /  CONTINUAR");
			break;
		case LOST:
			drawCard(g, "SEM ENERGIA", "Fim de jogo",
					"Pontos: " + model.getPoints() + "    Recorde: " + model.getRecord(), "ENTER  /  TENTAR DE NOVO");
			break;
		default:
			break;
		}
	}

	private void drawSurfer(Graphics2D g) {
		Surfer surfer = model.getSurfer();
		int x = (int) surfer.getX();
		int y = (int) surfer.getY();
		if (surfer.isCrouching()) {
			drawCrouch(g, x, y);
		} else {
			g.drawImage(Assets.SURFER, x, y - Surfer.HEIGHT, Surfer.WIDTH, Surfer.HEIGHT, null);
		}
	}

	/**
	 * Agachada: prancha igual, pernas dobradas (mais baixas e abertas) e tronco um pouco menor,
	 * em vez de achatar a imagem inteira.
	 */
	private void drawCrouch(Graphics2D g, int x, int y) {
		BufferedImage sprite = Assets.SURFER;
		int sw = sprite.getWidth();
		int sh = sprite.getHeight();
		int hip = sh * 55 / 100;
		int knees = sh * 85 / 100;
		g.drawImage(sprite, x, y - BOARD_H, x + Surfer.WIDTH, y, 0, knees, sw, sh, null);
		g.drawImage(sprite, x - 5, y - BOARD_H - 9, x + Surfer.WIDTH + 5, y - BOARD_H + 1, 0, hip, sw, knees, null);
		int torsoW = Surfer.WIDTH * 80 / 100;
		int torsoH = 33;
		int tx = x + (Surfer.WIDTH - torsoW) / 2;
		g.drawImage(sprite, tx, y - BOARD_H - 8 - torsoH, tx + torsoW, y - BOARD_H - 8, 0, 0, sw, hip, null);
	}

	// ------------------------------------------------------------------
	// Prologo animado (cutscenes)
	// ------------------------------------------------------------------

	private void drawCutscene(Graphics2D g) {
		int t = model.getIntroTicks();
		switch (model.getIntro()) {
		case 0:
			drawRiding(g, 250 + (int) (Math.sin(t * 0.03) * 12), 0, t);
			break;
		case 1:
			drawFall(g, t);
			break;
		default:
			drawRecover(g, t);
		}
	}

	/** Surfista de pe na prancha, balancando com o mar. */
	private void drawRiding(Graphics2D g, int x, double angle, int t) {
		int feet = Surfer.FLOOR - (int) Math.round(Math.sin(t * 0.12) * 2);
		drawSpray(g, x + Surfer.WIDTH - 6, feet, t);
		Graphics2D r = (Graphics2D) g.create();
		r.rotate(Math.toRadians(angle), x + Surfer.WIDTH / 2.0, feet);
		r.drawImage(Assets.SURFER, x, feet - Surfer.HEIGHT, Surfer.WIDTH, Surfer.HEIGHT, null);
		r.dispose();
	}

	/** A queda: a onda gigante chega, a surfista gira no ar, cai no mar e a prancha voa longe. */
	private void drawFall(Graphics2D g, int t) {
		final int hit = 88;
		final int flight = 56;
		final int land = hit + flight;
		Graphics2D s = (Graphics2D) g.create();
		if (t > hit - 6 && t < hit + 22) {
			s.translate(Math.sin(t * 2.3) * 4, Math.cos(t * 3.1) * 3);
		}
		int waveX = (int) (900 - t * 6.5);
		if (t < hit) {
			double wobble = Math.sin(t * 0.45) * Math.min(1, t / 70.0) * 7;
			drawRiding(s, 250, wobble, t);
		} else if (t < land) {
			double p = (t - hit) / (double) flight;
			double arc = Math.sin(Math.PI * p);
			drawBoard(s, 290 + 200 * p, Surfer.FLOOR - 6 - 130 * arc, 600 * p);
			double cx = 250 - 150 * p + Surfer.WIDTH / 2.0;
			double cy = Surfer.FLOOR - PERSON_H / 2.0 + 18 * p - 120 * arc;
			Graphics2D r = (Graphics2D) s.create();
			r.rotate(Math.toRadians(-540 * p - 30 * arc), cx, cy); // gira 1,5 volta e entra de cabeca na agua
			r.drawImage(PERSON, (int) (cx - Surfer.WIDTH / 2.0), (int) (cy - PERSON_H / 2.0), Surfer.WIDTH, PERSON_H,
					null);
			r.dispose();
		} else {
			double bob = Math.sin(t * 0.1) * 3;
			drawBoard(s, 490, Surfer.FLOOR - 4 + bob, Math.sin(t * 0.07) * 8);
			double rise = Math.max(0, Math.min(1, (t - land - 16) / 40.0));
			drawSwimmer(s, 100, rise, t);
			drawSplash(s, 134, t - land);
		}
		SpriteRenderer.drawWave(s, waveX, Surfer.FLOOR + 4, 3.0, t);
		s.dispose();
	}

	/** Depois da queda: ela nada ate a prancha e sobe de novo. */
	private void drawRecover(Graphics2D g, int t) {
		if (t < 60) {
			double q = t / 60.0;
			drawBoard(g, 262, Surfer.FLOOR - 4 + Math.sin(t * 0.1) * 3, 0);
			drawSwimmer(g, (int) (150 + 80 * q), 1, t);
		} else if (t < 95) {
			double q = (t - 60) / 35.0;
			Graphics2D c = (Graphics2D) g.create();
			c.clipRect(0, 0, getWidth(), Surfer.FLOOR - BOARD_H);
			int feet = (int) (Surfer.FLOOR - BOARD_H + 34 * (1 - q));
			c.drawImage(PERSON, 250, feet - PERSON_H, Surfer.WIDTH, PERSON_H, null);
			c.dispose();
			drawBoard(g, 250, Surfer.FLOOR - BOARD_H, 0);
			drawRipple(g, 284, t);
		} else {
			drawRiding(g, 250, 0, t);
		}
	}

	/** Prancha (recortada do proprio sprite) desenhada a partir do canto superior esquerdo, com rotacao. */
	private void drawBoard(Graphics2D g, double x, double y, double angle) {
		Graphics2D r = (Graphics2D) g.create();
		r.rotate(Math.toRadians(angle), x + Surfer.WIDTH / 2.0, y + BOARD_H / 2.0);
		r.drawImage(SURF_BOARD, (int) x, (int) y, Surfer.WIDTH, BOARD_H, null);
		r.dispose();
	}

	/** Cabeca e ombros para fora da agua; rise vai de 0 (submersa) a 1. */
	private void drawSwimmer(Graphics2D g, int x, double rise, int t) {
		int water = Surfer.FLOOR - 6;
		int bob = (int) Math.round(Math.sin(t * 0.15) * 2);
		int shown = (int) (30 * rise);
		Graphics2D c = (Graphics2D) g.create();
		c.clipRect(0, 0, getWidth(), water);
		c.drawImage(PERSON, x, water - shown + bob, Surfer.WIDTH, PERSON_H, null);
		c.dispose();
		if (rise > 0) {
			drawRipple(g, x + Surfer.WIDTH / 2, t);
		}
	}

	private void drawRipple(Graphics2D g, int cx, int t) {
		int water = Surfer.FLOOR - 6;
		int grow = (int) (Math.sin(t * 0.15) * 4);
		g.setColor(new Color(0xCCF6FBF9, true));
		g.setStroke(new BasicStroke(2));
		g.drawOval(cx - 40 - grow, water - 4, 80 + 2 * grow, 9);
		g.setStroke(new BasicStroke(1));
	}

	/** Respingos de agua no momento em que ela cai no mar. */
	private void drawSplash(Graphics2D g, int x, int s) {
		if (s < 0 || s > 46) {
			return;
		}
		g.setColor(new Color(0xF4FFFD));
		for (int k = 0; k < 16; k++) {
			double vx = (k - 7.5) * 0.75;
			double vy = -7 - (k % 4) * 1.4;
			int px = (int) (x + vx * s);
			int py = (int) (Surfer.FLOOR - 8 + vy * s + 0.38 * s * s);
			if (py <= Surfer.FLOOR) {
				int size = s < 20 ? 5 : 3;
				g.fillRect(px, py, size, size);
			}
		}
		int ring = Math.min(s * 4, 90);
		g.setColor(new Color(0xB4F6FBF9, true));
		g.fillOval(x - ring, Surfer.FLOOR - 10, ring * 2, 12);
	}

	/** Esteira de espuma atras da prancha. */
	private void drawSpray(Graphics2D g, int x, int feet, int t) {
		g.setColor(new Color(0xE6F4FFFD, true));
		for (int i = 0; i < 6; i++) {
			int dx = (i * 9 + t * 2) % 54;
			int dy = (int) (Math.sin((t + i * 7) * 0.4) * 3);
			g.fillRect(x + dx, feet - 6 + dy, 4 - i / 2, 3);
		}
	}

	/** Legenda compacta no topo para as cutscenes, deixando a animacao visivel. */
	private void drawCaption(Graphics2D g, String label, String title, String body) {
		int left = 110;
		int width = W - 2 * left;
		g.setColor(new Color(0x85203E57, true));
		g.fillRect(left + 6, 26, width, 128);
		g.setColor(INK);
		g.fillRect(left, 20, width, 128);
		g.setColor(GOLD);
		g.fillRect(left, 20, width, 4);
		g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
		g.drawString(label, left + 20, 46);
		String hint = "ENTER ›";
		g.drawString(hint, left + width - 20 - g.getFontMetrics().stringWidth(hint), 46);
		centered(g, title, 92, 34, FOAM);
		centered(g, body, 126, 15, FOAM);
	}

	// ------------------------------------------------------------------
	// Epilogo animado: chegada a praia
	// ------------------------------------------------------------------

	private static final int SHORE_WATER = 352;
	private static final int SAND_FEET = 404;

	/**
	 * Ela surfa ate a beira, pula da prancha para a areia, caminha ate a toalha e comemora;
	 * a prancha e trazida pelas ondas ate a areia e um caranguejo vem ver.
	 */
	private void drawEnding(Graphics2D g, int t) {
		final int arrive = 100;
		final int hop = 24;
		final int walk = 96;
		drawCrab(g, t);
		double wash = Math.max(0, Math.min(1, (t - arrive - hop) / 60.0));
		if (t < arrive) {
			// vem do horizonte em direcao a praia, crescendo conforme se aproxima
			double p = t / (double) arrive;
			double scale = 0.3 + 0.7 * p * p;
			int w = (int) (Surfer.WIDTH * scale);
			int h = (int) (Surfer.HEIGHT * scale);
			int cx = (int) (450 - 126 * p);
			int feet = (int) (262 + (SHORE_WATER - 262) * p + Math.sin(t * 0.15) * 2 * scale);
			g.setColor(new Color(0xD9F6FBF9, true));
			g.fillOval(cx - w / 2 - (int) (10 * scale), feet - (int) (5 * scale), w + (int) (20 * scale), (int) (9 * scale) + 2);
			for (int k = 0; k < 5; k++) {
				int dy = (int) ((k * 7 + t * 1.5) % 30 * scale);
				g.fillRect(cx - (int) ((k - 2) * 9 * scale), feet - 2 + dy / 3, Math.max(2, (int) (4 * scale)), 2);
			}
			g.drawImage(Assets.SURFER, cx - w / 2, feet - h, w, h, null);
			return;
		}
		double boardX = 290 + 14 * wash;
		double boardFeet = SHORE_WATER + (SAND_FEET - 6 - SHORE_WATER) * wash + Math.sin(t * 0.1) * 2 * (1 - wash);
		drawBoard(g, boardX, boardFeet - BOARD_H, -8 * wash);
		if (wash < 1) {
			drawRipple(g, (int) boardX + Surfer.WIDTH / 2, t);
		}
		double x;
		double feet;
		double step = -1;
		if (t < arrive + hop) {
			double p = (t - arrive) / (double) hop;
			x = 290 + 60 * p;
			feet = SHORE_WATER + (SAND_FEET - SHORE_WATER) * p - 44 * Math.sin(Math.PI * p);
			if (p > 0.85) {
				drawSandPuff(g, (int) x + Surfer.WIDTH / 2, t - arrive - hop + 4);
			}
		} else if (t < arrive + hop + walk) {
			double p = (t - arrive - hop) / (double) walk;
			x = 350 + 120 * p;
			feet = SAND_FEET;
			step = (t - arrive - hop) * 0.24;
			drawSandPuff(g, 350 + Surfer.WIDTH / 2, t - arrive - hop + 4);
		} else {
			int c = t - arrive - hop - walk;
			int k = c % 46;
			boolean jumping = c < 46 * 4 && k < 22;
			x = 470;
			feet = SAND_FEET - (jumping ? Math.sin(Math.PI * k / 22.0) * 26 : 0);
			if (c < 46 * 4 + 30) {
				drawConfetti(g, 470 + Surfer.WIDTH / 2, c);
			}
		}
		if (t > arrive + hop * 2 / 3) {
			drawShadow(g, (int) x, SAND_FEET + 2);
		}
		drawWalker(g, x, feet, step);
	}

	/**
	 * Surfista a pe (sem prancha). O sprite so tem a pose em pe, entao a caminhada e montada
	 * recortando as pernas: cada perna avanca e levanta alternadamente e o corpo sobe e desce.
	 * step negativo = parada.
	 */
	private void drawWalker(Graphics2D g, double x, double feet, double step) {
		BufferedImage sprite = Assets.SURFER;
		final int hip = 290;
		final int sole = 385;
		final int split = 209;
		double sx = Surfer.WIDTH / (double) sprite.getWidth();
		double sy = Surfer.HEIGHT / (double) sprite.getHeight();
		double s = step < 0 ? 0 : Math.sin(step);
		double bob = step < 0 ? 0 : -Math.abs(Math.cos(step)) * 2.5;
		int top = (int) Math.round(feet - sole * sy);
		int hipY = (int) Math.round(feet - (sole - hip) * sy);
		int splitX = (int) Math.round(x + split * sx);
		int right = (int) Math.round(x + Surfer.WIDTH);
		// perna de tras (esquerda na tela) e perna da frente (direita)
		int backDx = (int) Math.round(5 * s);
		int backLift = (int) Math.round(-Math.max(0, -s) * 5);
		int frontDx = (int) Math.round(-5 * s);
		int frontLift = (int) Math.round(-Math.max(0, s) * 5);
		int b = (int) Math.round(bob);
		g.drawImage(sprite, (int) x + backDx, hipY + backLift + b, splitX + backDx, (int) feet + backLift, 0, hip, split, sole, null);
		g.drawImage(sprite, splitX + frontDx, hipY + frontLift + b, right + frontDx, (int) feet + frontLift, split, hip,
				sprite.getWidth(), sole, null);
		// tronco, bracos e cabeca por cima, acompanhando o balanco
		int overlap = 2; // cobre a emenda entre quadril e pernas
		g.drawImage(sprite, (int) x, top + b, right, hipY + b + overlap, 0, 0, sprite.getWidth(),
				hip + (int) Math.round(overlap / sy), null);
	}

	private void drawShadow(Graphics2D g, int x, int y) {
		g.setColor(new Color(0x40203E57, true));
		g.fillOval(x + 12, y - 4, Surfer.WIDTH - 24, 8);
	}

	/** Poeirinha de areia quando ela pousa. */
	private void drawSandPuff(Graphics2D g, int cx, int s) {
		if (s < 0 || s > 26) {
			return;
		}
		g.setColor(new Color(0xC8E9CF96, true));
		for (int k = 0; k < 8; k++) {
			double vx = (k - 3.5) * 1.1;
			int px = (int) (cx + vx * s);
			int py = (int) (SAND_FEET - 3 - 2.6 * s + 0.12 * s * s);
			if (py <= SAND_FEET) {
				g.fillRect(px, py, 4, 4);
			}
		}
	}

	/** Confete colorido de comemoracao. */
	private void drawConfetti(Graphics2D g, int cx, int c) {
		Color[] colors = { GOLD, new Color(0xE84855), new Color(0x31C6CD), new Color(0xFFFFFF) };
		for (int k = 0; k < 18; k++) {
			int life = (c + k * 11) % 70;
			double angle = k * 2.399;
			int px = (int) (cx + Math.cos(angle) * (20 + life * 1.6));
			int py = (int) (SAND_FEET - 90 - Math.abs(Math.sin(angle)) * 30 + life * 1.1);
			g.setColor(colors[k % colors.length]);
			g.fillRect(px, py, 5, (life / 6) % 2 == 0 ? 5 : 2);
		}
	}

	/** Caranguejo atravessando a areia, de lado. */
	private void drawCrab(Graphics2D g, int t) {
		int x = (int) Math.max(640, 830 - Math.max(0, t - 120) * 1.4);
		int wobble = (t / 6) % 2 == 0 ? 0 : 2;
		g.drawImage(Assets.CRAB, x, 412 - wobble, 35, 24, null);
	}

	private void drawShield(Graphics2D g) {
		Surfer surfer = model.getSurfer();
		g.setColor(new Color(0x70A0F1AA, true));
		g.setStroke(new BasicStroke(3));
		g.drawOval((int) surfer.getX() - 6, (int) surfer.getY() - Surfer.HEIGHT - 6, Surfer.WIDTH + 12,
				Surfer.HEIGHT + 10);
		g.setStroke(new BasicStroke(1));
	}

	private void drawStorm(Graphics2D g) {
		int ticks = model.getTicks();
		g.setColor(new Color(0x72232651, true));
		g.fillRect(0, 0, W, H);
		g.setColor(new Color(0x8BBDE2ED, true));
		for (int i = 0; i < 56; i++) {
			int x = Math.floorMod(i * 137 - ticks * 7, W);
			int y = Math.floorMod(i * 53 + ticks * 13, H - 6);
			g.drawLine(x, y, x - 7, y + 13);
		}
	}

	private void drawSwell(Graphics2D g) {
		int offset = (int) model.getScroll() % 340;
		for (int x = 32 - offset; x < W; x += 340) {
			g.setColor(new Color(0x509AF5EF, true));
			g.fillPolygon(new int[] { x, x + 44, x + 64, x + 90, x + 125, x + 154 },
					new int[] { 334, 325, 310, 313, 329, 334 }, 6);
			g.setColor(new Color(0x9FE6F3EF, true));
			g.fillPolygon(new int[] { x + 44, x + 64, x + 90, x + 103, x + 83, x + 61 },
					new int[] { 325, 310, 313, 319, 316, 315 }, 6);
		}
	}

	// ------------------------------------------------------------------
	// Praia que aparece no fim da ultima fase
	// ------------------------------------------------------------------

	private static final int HORIZON = 284;

	/** Posicao x da beira da agua na altura y (diagonal com leve ondulacao). */
	private static double shoreEdge(int shore, int y) {
		return shore + 160 - (y - HORIZON) * 185.0 / (H - HORIZON) + 7 * Math.sin(y * 0.08);
	}

	private void drawBeach(Graphics2D g) {
		int shore = model.getShoreX();
		int t = model.getTicks();
		int n = (H - HORIZON) / 6 + 1;
		int[] ys = new int[n];
		for (int i = 0; i < n; i++) {
			ys[i] = Math.min(H, HORIZON + i * 6);
		}
		// agua rasa (turquesa clara) antes da areia
		fillBand(g, shore, ys, -34, 0, new Color(0x9A8FE8E2, true));
		// areia molhada e areia seca com textura
		fillBand(g, shore, ys, 0, 400, new Color(0xD3AE72));
		Graphics2D dry = (Graphics2D) g.create();
		dry.setPaint(new TexturePaint(Assets.SAND, new Rectangle(0, 0, 96, 96)));
		fillBand(dry, shore, ys, 16, 400, null);
		dry.setPaint(new GradientPaint(0, HORIZON, new Color(0x50FFF6DC, true), 0, H, new Color(0x00FFF6DC, true)));
		fillBand(dry, shore, ys, 16, 400, null);
		dry.dispose();
		// espuma que vai e volta na beira
		g.setColor(new Color(0xF6FFFD));
		g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		int[] fx = new int[n];
		for (int i = 0; i < n; i++) {
			fx[i] = (int) (shoreEdge(shore, ys[i]) + 3 + 5 * Math.sin(t * 0.06 + ys[i] * 0.03));
		}
		g.drawPolyline(fx, ys, n);
		g.setStroke(new BasicStroke(1));
		for (int i = 0; i < n; i += 2) {
			int bubble = (int) ((t * 0.7 + i * 5) % 14);
			g.fillRect(fx[i] - 6 - bubble, ys[i] + 2, 3, 2);
		}
		// vegetacao no fundo da praia
		int back = (int) shoreEdge(shore, HORIZON) + 40;
		for (int x = back; x < W + 20; x += 26) {
			int h = 8 + (x / 26 % 3) * 4;
			g.setColor(new Color(0x3C8A44));
			g.fillOval(x, HORIZON - h + 6, 34, h + 6);
			g.setColor(new Color(0x6CC04A));
			g.fillOval(x + 6, HORIZON - h + 6, 18, h / 2 + 3);
		}
		g.drawImage(Assets.PALM, back + 120, HORIZON - 44, 22, 52, null);
		g.drawImage(Assets.PALM, back + 330, HORIZON - 38, 18, 44, null);
		// conchas e estrela-do-mar espalhadas
		int[][] shells = { { 210, 348 }, { 300, 420 }, { 470, 372 }, { 390, 436 }, { 560, 410 } };
		for (int[] sh : shells) {
			g.setColor(new Color(0xF7D2D8));
			g.fillRect(shore + sh[0], sh[1], 5, 4);
			g.setColor(new Color(0xFFFFFF));
			g.fillRect(shore + sh[0] + 1, sh[1], 2, 1);
		}
		drawStarfish(g, shore + 250, 430);
		// coqueiro com sombra, guarda-sol e toalha (iguais aos da cena final)
		g.setColor(new Color(0x45A0752F, true));
		g.fillOval(shore + 236, 402, 74, 12);
		g.drawImage(Assets.PALM, shore + 244, 294, 54, 116, null);
		drawUmbrella(g, shore + 420, 418);
		g.drawImage(Assets.CRAB, (int) (shore + 360 + Math.sin(t * 0.05) * 22), 412, 35, 24, null);
	}

	/** Preenche a faixa entre a beira deslocada de "from" e de "to" pixels (to = 400 vai ate a borda). */
	private void fillBand(Graphics2D g, int shore, int[] ys, int from, int to, Color color) {
		int n = ys.length;
		int[] xs = new int[n * 2];
		int[] yy = new int[n * 2];
		for (int i = 0; i < n; i++) {
			double edge = shoreEdge(shore, ys[i]);
			xs[i] = (int) (edge + from);
			yy[i] = ys[i];
			xs[n * 2 - 1 - i] = to >= 400 ? W + 4 : (int) (edge + to);
			yy[n * 2 - 1 - i] = ys[i];
		}
		if (color != null) {
			g.setColor(color);
		}
		g.fillPolygon(xs, yy, xs.length);
	}

	private void drawStarfish(Graphics2D g, int x, int y) {
		g.setColor(new Color(0xF08A3C));
		g.fillRect(x + 4, y, 4, 12);
		g.fillRect(x, y + 4, 12, 4);
		g.fillRect(x + 1, y + 9, 3, 3);
		g.fillRect(x + 8, y + 9, 3, 3);
		g.setColor(new Color(0xFFC07A));
		g.fillRect(x + 5, y + 5, 2, 2);
	}

	/** Guarda-sol listrado de vermelho e branco com toalha azul, como na imagem da praia. */
	private void drawUmbrella(Graphics2D g, int x, int base) {
		g.setColor(new Color(0x3D7FB5));
		g.fillRect(x - 34, base - 4, 66, 12);
		g.setColor(new Color(0xA9D6F2));
		for (int i = 0; i < 66; i += 8) {
			g.fillRect(x - 34 + i, base - 4, 3, 12);
		}
		g.setColor(new Color(0x45A0752F, true));
		g.fillOval(x - 46, base - 8, 92, 12);
		g.setColor(new Color(0x6B6F75));
		g.fillRect(x - 2, base - 78, 4, 80);
		for (int i = 0; i < 6; i++) {
			g.setColor(i % 2 == 0 ? new Color(0xE23B3B) : new Color(0xF7F7F2));
			g.fillArc(x - 46, base - 104, 92, 56, 180 - i * 30, -30);
		}
		g.setColor(new Color(0x2B2F3A));
		g.drawArc(x - 46, base - 104, 92, 56, 0, 180);
		g.drawLine(x - 46, base - 76, x + 46, base - 76);
	}

	private void drawOcean(Graphics2D g, int x, boolean flipped) {
		// a imagem e desenhada com 480 px de altura para manter o horizonte original
		if (flipped) {
			g.drawImage(Assets.OCEAN, x + W, 0, -W, 480, null);
		} else {
			g.drawImage(Assets.OCEAN, x, 0, W, 480, null);
		}
	}

	private void drawBeachBackground(Graphics2D g) {
		g.drawImage(Assets.BEACH, 0, H - 480, W, 480, null);
	}

	private void drawCard(Graphics2D g, String label, String title, String body, String action) {
		g.setColor(new Color(0x85203E57, true));
		g.fillRect(124, 81, 568, 217);
		g.setColor(INK);
		g.fillRect(116, 73, 568, 217);
		g.setColor(GOLD);
		g.fillRect(116, 73, 568, 5);
		centered(g, label, 108, 15, GOLD);
		centered(g, title, 169, 43, FOAM);
		centered(g, body, 204, 14, FOAM);
		g.setColor(GOLD);
		g.fillRect(246, 227, 308, 41);
		centered(g, action, 253, 16, INK);
	}

	private void centered(Graphics2D g, String value, int y, int size, Color color) {
		g.setFont(new Font(Font.MONOSPACED, Font.BOLD, size));
		g.setColor(color);
		g.drawString(value, (W - g.getFontMetrics().stringWidth(value)) / 2, y);
	}
}
