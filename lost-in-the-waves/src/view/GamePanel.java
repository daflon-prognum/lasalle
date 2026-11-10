package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
			drawSurferOnSand(g);
			drawCard(g, "EPÍLOGO  /  " + model.getPoints() + " PONTOS", "Enfim, a areia!",
					"A surfista desembarca. Sua jornada chegou ao fim.", "ENTER  /  VOLTAR AO MENU");
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
		for (Obstacle obstacle : model.getObstacles()) {
			SpriteRenderer.drawObstacle(g, obstacle);
		}
		for (Item item : model.getItems()) {
			SpriteRenderer.drawItem(g, item);
		}
		if (model.getState() == GameState.INTRO && model.getIntro() == 1) {
			SpriteRenderer.drawWave(g, 107, Surfer.FLOOR);
			g.drawImage(Assets.BOARD, 185, 400, 64, 15, null);
		} else if (model.isSurferVisible()) {
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
			drawCard(g, "PRÓLOGO  /  " + (page + 1) + "/" + GameModel.INTRO_PAGES, titles[page], lines[page],
					"ENTER  /  CONTINUAR");
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
		BufferedImage sprite = Assets.SURFER;
		int x = (int) surfer.getX();
		int y = (int) surfer.getY();
		if (surfer.isCrouching()) {
			int board = sprite.getHeight() * 85 / 100;
			g.drawImage(sprite, x + 8, y - 32, x + Surfer.WIDTH - 8, y - 10, 0, 0, sprite.getWidth(), board, null);
			g.drawImage(sprite, x, y - 10, x + Surfer.WIDTH, y, 0, board, sprite.getWidth(), sprite.getHeight(), null);
		} else {
			g.drawImage(sprite, x, y - Surfer.HEIGHT, Surfer.WIDTH, Surfer.HEIGHT, null);
		}
	}

	private void drawSurferOnSand(Graphics2D g) {
		BufferedImage sprite = Assets.SURFER;
		int x = 250;
		int feet = sprite.getHeight() * 85 / 100;
		g.drawImage(sprite, x, 352, x + 68, 413, 0, 0, sprite.getWidth(), feet, null);
		g.drawImage(Assets.BOARD, x + 58, 400, 64, 15, null);
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

	private void drawBeach(Graphics2D g) {
		int shore = model.getShoreX();
		int[] edge = { shore + 160, shore + 122, shore + 139, shore + 80, shore + 42, shore, shore - 25 };
		int[] depths = { 284, 308, 329, 355, 381, 414, H };
		int[] xs = new int[edge.length + 2];
		int[] ys = new int[edge.length + 2];
		for (int i = 0; i < edge.length; i++) {
			xs[i] = edge[i];
			ys[i] = depths[i];
		}
		xs[edge.length] = W;
		ys[edge.length] = H;
		xs[edge.length + 1] = W;
		ys[edge.length + 1] = 284;
		g.setColor(new Color(0xFFEFC7));
		g.fillPolygon(xs, ys, xs.length);
		for (int i = 0; i < edge.length; i++) {
			xs[i] += 18;
		}
		g.setPaint(new TexturePaint(Assets.SAND, new Rectangle(0, 0, 96, 96)));
		g.fillPolygon(xs, ys, xs.length);
		g.setColor(new Color(0xFAFEF1));
		g.setStroke(new BasicStroke(3));
		g.drawPolyline(edge, depths, edge.length);
		g.setStroke(new BasicStroke(1));
		g.drawImage(Assets.PALM, shore + 244, 294, 54, 116, null);
		g.drawImage(Assets.CRAB, shore + 360, 408, 35, 24, null);
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
