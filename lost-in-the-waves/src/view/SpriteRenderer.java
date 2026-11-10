package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

import model.Gull;
import model.Item;
import model.Obstacle;
import model.ObstacleType;
import model.Surfer;

/** Desenha obstaculos e itens em pixel art usando Graphics2D. */
public final class SpriteRenderer {

	private SpriteRenderer() {
	}

	public static void drawObstacle(Graphics2D g, Obstacle obstacle, int tick) {
		int left = (int) obstacle.getX();
		int width = obstacle.getWidth();
		int height = obstacle.getHeight();
		int floor = Surfer.FLOOR;
		switch (obstacle.getType()) {
		case GULL:
			drawGull(g, left, floor - Gull.FLIGHT_HEIGHT, tick, true);
			return;
		default:
			g.setColor(new Color(0xD6F8F5));
			g.fillRect(left - 6, floor + 1, width + 14, 3);
		}
		switch (obstacle.getType()) {
		case ROCK:
			g.setColor(new Color(0x20334C));
			g.fillPolygon(new int[] { left, left + 9, left + 25, left + 38, left + width },
					new int[] { floor, floor - 23, floor - height, floor - 23, floor }, 5);
			g.setColor(new Color(0x6C8498));
			g.fillPolygon(new int[] { left + 9, left + 25, left + 20 },
					new int[] { floor - 23, floor - height, floor - 9 }, 3);
			break;
		case BUOY:
			g.setColor(new Color(0x822B40));
			g.fillRect(left + 13, floor - height, 6, 23);
			g.setColor(new Color(0xF8AF4D));
			g.fillRect(left + 8, floor - height + 4, 16, 6);
			g.setColor(new Color(0xE95B48));
			g.fillRect(left + 3, floor - 22, width - 6, 20);
			g.setColor(new Color(0xFFF2C0));
			g.fillRect(left + 3, floor - 16, width - 6, 6);
			break;
		case WAVE:
			drawWave(g, left, floor, 1.0, tick);
			break;
		case LOG:
			g.setColor(new Color(0x643C31));
			g.fillRoundRect(left, floor - 18, width, 18, 8, 8);
			g.setColor(new Color(0xBE8A59));
			g.fillOval(left + 2, floor - 16, 14, 14);
			g.drawLine(left + 20, floor - 13, left + 57, floor - 13);
			break;
		case WHIRLPOOL:
			g.setColor(new Color(0x19395E));
			g.fillOval(left, floor - 17, width, 22);
			g.setColor(new Color(0x7CCFDD));
			g.drawArc(left + 4, floor - 15, width - 8, 16, 35, 280);
			g.drawArc(left + 16, floor - 11, width - 32, 8, 215, 280);
			break;
		case SHARK:
			drawShark(g, left, floor, tick);
			break;
		default: // WHALE
			g.setColor(new Color(0x254364));
			g.fillOval(left, floor - 15, width, 18);
			g.fillPolygon(new int[] { left + 15, left + 37, left + 50 },
					new int[] { floor - 8, floor - height, floor - 8 }, 3);
			if (obstacle.getType() == ObstacleType.WHALE) {
				g.fillOval(left + 9, floor - 34, width - 14, 36);
				g.setColor(new Color(0xD6F8F5));
				g.fillRect(left + 25, floor - 23, 3, 3);
				g.drawLine(left + 48, floor - 31, left + 39, floor - 48);
				g.drawLine(left + 48, floor - 31, left + 56, floor - 47);
			}
		}
	}

	/**
	 * Gaivota voando para a esquerda, com bater de asas e sombra na agua
	 * (a sombra ajuda o jogador a perceber que ela voa baixo e que e preciso agachar).
	 */
	public static void drawGull(Graphics2D g, int left, int top, int tick, boolean shadow) {
		Graphics2D b = (Graphics2D) g.create();
		b.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		double flap = 0.3 + 0.7 * Math.sin(tick * 0.38); // asa desce pouco: nao encosta em quem agacha
		int bob = (int) Math.round(Math.sin(tick * 0.19) * 2);
		int y = top + bob;
		if (shadow) {
			b.setColor(new Color(0x10263C).darker());
			b.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.30f));
			b.fillOval(left + 10, Surfer.FLOOR - 4, 40, 7);
			b.setComposite(java.awt.AlphaComposite.SrcOver);
		}
		Color outline = new Color(0x17344E);
		// asa de tras (cinza)
		Path2D far = new Path2D.Double();
		far.moveTo(left + 26, y + 11);
		far.lineTo(left + 40, y + 11);
		far.lineTo(left + 46, y + 11 - 13 * flap);
		far.lineTo(left + 33, y + 10 - 11 * flap);
		far.closePath();
		b.setColor(new Color(0xA9B7C2));
		b.fill(far);
		// cauda
		b.setColor(new Color(0xE9EEF1));
		b.fillPolygon(new int[] { left + 44, left + 57, left + 55, left + 44 },
				new int[] { y + 10, y + 7, y + 15, y + 15 }, 4);
		// corpo e cabeca
		b.setColor(new Color(0xFBFDFE));
		b.fillOval(left + 12, y + 7, 36, 12);
		b.fillOval(left + 5, y + 3, 14, 13);
		b.setColor(new Color(0xC8D3DB));
		b.fillOval(left + 20, y + 7, 26, 5);
		b.setColor(outline);
		b.setStroke(new BasicStroke(1.4f));
		b.drawOval(left + 12, y + 7, 36, 12);
		b.drawArc(left + 5, y + 3, 14, 13, 40, 240);
		// bico e olho
		b.setColor(new Color(0xF7A23B));
		b.fillPolygon(new int[] { left - 3, left + 7, left + 7 }, new int[] { y + 10, y + 8, y + 12 }, 3);
		b.setColor(new Color(0x10263C));
		b.fillRect(left + 9, y + 7, 3, 3);
		// asa da frente (branca com ponta escura)
		Path2D near = new Path2D.Double();
		near.moveTo(left + 22, y + 12);
		near.lineTo(left + 38, y + 12);
		near.lineTo(left + 44, y + 12 - 19 * flap);
		near.lineTo(left + 28, y + 11 - 16 * flap);
		near.closePath();
		b.setColor(new Color(0xFFFFFF));
		b.fill(near);
		b.setColor(outline);
		b.draw(near);
		b.setColor(new Color(0x2B3A47));
		b.fillOval((int) (left + 39), (int) (y + 9 - 18 * flap), 7, 6);
		b.dispose();
	}

	// ------------------------------------------------------------------
	// Sprites em pixel art (cada letra e um "pixel" de 3x3)
	// ------------------------------------------------------------------

	private static final int PX = 3;

	/** Tubarao nadando para a esquerda, com barbatana, olho, dentes e barriga clara. */
	private static final String[] SHARK = {
			"............D.............",
			"...........DD.............",
			"..........DGD..........D..",
			".........DGGD.........DD..",
			"........DGGGD........DGD..",
			"....DDDDGGGGGDDDDDDDDGGD..",
			"..DDGGGGGGGGGGGGGGGGGGGD..",
			".DGGEGGDGDGGGGGGGGGGGGGGD.",
			"DGGGGGGDGDGGGGGGGGGGGGDGD.",
			"RTRTRBBBBBBBBBBBBBBBBGD.DD",
			".RBBBBBBBBBBBBBBBBBBBD....", };

	/** Onda quebrando para a esquerda: crista de espuma, tubo escuro, face clara e espuma na base. */
	private static final String[] WAVE = {
			"........WWWWWW..............",
			"......WWLLLLLLWW............",
			"....WWLLMMMMMMLLW...........",
			"...WLLMMMMMMMMMMLW..........",
			"..WLMMDDDDDDMMMMMLW.........",
			"..WMDKKKKKKKDMMMMMLW........",
			"..WDKKKKKKKKKDMMMMMLW.......",
			"..W..KKKKKKKKDMMMMMMLW......",
			"......KKKKKKKDLMMMMMMLW.....",
			".......KKKKKKDLMMMMMMMLLW...",
			"........KKKKKDLLMMMMMMMMLLW.",
			"........KKKKDDLLMMMMMMMMMMLL",
			".........KKKDLLLMMMMMMMMMMMM",
			"......WWKKKDLLLMMMMMMMMMMMMM",
			"...WWWWWWWWWWLLWWMMMMMMWWMMM",
			"WWWWWWWWWWWWWWWWWWWWWWWWWWWW", };

	private static Color pixelColor(char c) {
		switch (c) {
		case 'D':
			return new Color(0x22374B);
		case 'G':
			return new Color(0x587389);
		case 'B':
			return new Color(0xE4ECF0);
		case 'E':
			return new Color(0x0B1520);
		case 'R':
			return new Color(0x8E2F3F);
		case 'T':
		case 'W':
			return new Color(0xF4FFFD);
		case 'L':
			return new Color(0x8BE8EE);
		case 'M':
			return new Color(0x2AA8C4);
		case 'K':
			return new Color(0x0B4664);
		default:
			return null;
		}
	}

	private static void drawPixels(Graphics2D g, String[] rows, int x, int y, int px) {
		for (int r = 0; r < rows.length; r++) {
			for (int c = 0; c < rows[r].length(); c++) {
				Color color = pixelColor(rows[r].charAt(c));
				if (color != null) {
					g.setColor(color);
					g.fillRect(x + c * px, y + r * px, px, px);
				}
			}
		}
	}

	/** Tubarao meio submerso avancando rapido, com marola na frente do focinho. */
	public static void drawShark(Graphics2D g, int left, int floor, int tick) {
		int bob = (tick / 10) % 2;
		int top = floor - 31 + bob;
		drawPixels(g, SHARK, left - 6, top, PX);
		// espuma na linha d'agua
		g.setColor(new Color(0xF4FFFD));
		g.fillRect(left - 4, floor - 1, 78, 2);
		g.fillRect(left + 4, floor + 1, 60, 2);
		// marola e respingos no focinho (ele vem mais rapido que o mar)
		for (int i = 0; i < 4; i++) {
			int phase = (tick + i * 5) % 20;
			g.fillRect(left - 10 - phase, floor - 8 - (i % 2) * 3 + phase / 4, 3, 3);
		}
		g.fillRect(left - 12, floor - 6, 8, 3);
		// esteira atras da cauda
		for (int i = 0; i < 3; i++) {
			int dx = (i * 9 + tick * 2) % 27;
			g.fillRect(left + 76 + dx, floor - 4, 5 - i, 2);
		}
	}

	/**
	 * Onda quebrando para a esquerda (na direcao da surfista), em pixel art.
	 * A espuma da crista brilha, gotas caem da ponta e a espuma da base corre.
	 * scale aumenta o tamanho dos pixels (usado no prologo).
	 */
	public static void drawWave(Graphics2D g, int x, int y, double scale, int tick) {
		int px = Math.max(PX, (int) Math.round(PX * scale));
		int rows = WAVE.length;
		int top = y - (rows - 1) * px;
		int left = x - 2 * px;
		drawPixels(g, WAVE, left, top, px);
		// brilho que percorre a crista
		g.setColor(new Color(0xFFFFFF));
		int shine = (tick / 4) % 10;
		g.fillRect(left + (6 + shine) * px, top + px, px, px);
		// gotas caindo da ponta da crista
		g.setColor(new Color(0xF4FFFD));
		for (int i = 0; i < 3; i++) {
			int fall = (tick + i * 9) % 27;
			g.fillRect(left + (2 + i % 2) * px, top + (8 + fall / 3) * px, px, px);
		}
		// respingos saltando da crista
		for (int i = 0; i < 4; i++) {
			int phase = (tick + i * 7) % 28;
			int sx = left + (7 + i * 2) * px - phase * px / 6;
			int sy = top - phase * px / 5 + (phase * phase) * px / 160;
			if (sy < top + px) {
				g.fillRect(sx, sy, px, px);
			}
		}
		// espuma correndo na base
		for (int i = 0; i < 5; i++) {
			int bx = (i * 6 + tick / 3) % 30;
			g.fillRect(left + (bx - 1) * px, y - px, px * 2, px);
		}
	}

	public static void drawItem(Graphics2D g, Item item) {
		int left = (int) item.getX();
		int y = (int) item.getY();
		int size = Item.SIZE;
		switch (item.getType()) {
		case BALL:
			g.drawImage(Assets.BALL, left - 3, y - 3, size + 6, size + 6, null);
			return;
		default:
			g.setColor(new Color(0x183754));
			g.fillRoundRect(left, y, size, size, 8, 8);
		}
		switch (item.getType()) {
		case WATER:
			g.setColor(new Color(0x69E7FF));
			g.fillRect(left + 8, y + 7, 8, 12);
			g.fillRect(left + 10, y + 4, 4, 3);
			break;
		case SHIELD:
			g.setColor(new Color(0xA0F1AA));
			g.fillPolygon(new int[] { left + 5, left + 19, left + 17, left + 12, left + 7 },
					new int[] { y + 6, y + 6, y + 15, y + 20, y + 15 }, 5);
			break;
		case COIN:
			g.setColor(new Color(0xFFD166));
			g.fillOval(left + 5, y + 5, 14, 14);
			g.setColor(new Color(0x9E622C));
			g.drawOval(left + 8, y + 8, 8, 8);
			break;
		default: // COCONUT
			g.setColor(new Color(0xAE774B));
			g.fillOval(left + 5, y + 5, 14, 14);
			g.setColor(new Color(0x593726));
			g.drawOval(left + 8, y + 8, 8, 8);
		}
	}
}
