package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
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
		default: // SHARK e WHALE
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

	/**
	 * Onda quebrando para a esquerda (na direcao da surfista): costas escuras, face clara,
	 * crista de espuma que se curva formando um tubo e respingos animados.
	 * As coordenadas locais vao de x 0..72 e y -40..0; scale aumenta a onda (usado no prologo).
	 */
	public static void drawWave(Graphics2D g, int x, int y, double scale, int tick) {
		Graphics2D w = (Graphics2D) g.create();
		w.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		w.translate(x, y);
		w.scale(scale, scale);
		// corpo da onda: costas suaves a direita, crista projetada para a esquerda
		Path2D body = new Path2D.Double();
		body.moveTo(84, 2);
		body.curveTo(72, -4, 62, -24, 48, -38);
		body.curveTo(38, -46, 22, -46, 12, -40);
		body.curveTo(4, -35, -2, -28, -3, -20);
		body.curveTo(1, -24, 6, -27, 10, -26);
		body.curveTo(18, -22, 22, -12, 24, 2);
		body.closePath();
		w.setPaint(new GradientPaint(0, -46, new Color(0x2CB7CB), 0, 2, new Color(0x0A5C7A)));
		w.fill(body);
		// tubo: a parte oca embaixo da crista, mais escura
		Path2D tube = new Path2D.Double();
		tube.moveTo(-3, -20);
		tube.curveTo(1, -24, 6, -27, 10, -26);
		tube.curveTo(18, -22, 22, -12, 24, 2);
		tube.lineTo(6, 2);
		tube.curveTo(8, -6, 6, -14, -3, -20);
		tube.closePath();
		w.setPaint(new GradientPaint(20, -24, new Color(0x0E6F8D), 4, 2, new Color(0x063F57)));
		w.fill(tube);
		// face iluminada (parede interna do tubo)
		Path2D face = new Path2D.Double();
		face.moveTo(12, -25);
		face.curveTo(19, -20, 23, -10, 24, 2);
		face.lineTo(28, 2);
		face.curveTo(27, -12, 24, -24, 18, -31);
		face.closePath();
		w.setColor(new Color(0x5ED9E2));
		w.fill(face);
		// crista de espuma acompanhando o topo ate a ponta que cai
		Path2D lip = new Path2D.Double();
		lip.moveTo(58, -32);
		lip.curveTo(44, -46, 24, -48, 12, -40);
		lip.curveTo(4, -35, -2, -28, -3, -20);
		w.setColor(new Color(0xF4FFFD));
		w.setStroke(new BasicStroke(4.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		w.draw(lip);
		w.setColor(new Color(0xBFF3F5));
		w.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		Path2D lip2 = new Path2D.Double();
		lip2.moveTo(50, -36);
		lip2.curveTo(40, -42, 26, -42, 16, -37);
		w.draw(lip2);
		// gotas caindo da ponta da crista
		w.setColor(new Color(0xF4FFFD));
		for (int i = 0; i < 3; i++) {
			double fall = ((tick * 0.8 + i * 7) % 20) / 20.0;
			w.fillRect((int) (-4 + i * 2), (int) (-18 + fall * 18), 2, 3);
		}
		// respingos animados sobre a crista
		w.setColor(new Color(0xF4FFFD));
		for (int i = 0; i < 6; i++) {
			double phase = ((tick * 0.9 + i * 11) % 22) / 22.0;
			double px = 8 + i * 8 - phase * 10;
			double py = -38 - phase * 9 + (i % 2) * 3;
			int size = phase < 0.6 ? 3 : 2;
			w.fillRect((int) px, (int) py, size, size);
		}
		// espuma na base (agua branca onde a crista quebra)
		w.setStroke(new BasicStroke(1));
		w.fillRoundRect(-8, -1, 96, 4, 4, 4);
		w.fillOval(-10, -6, 22, 9);
		w.fillOval(4, -4, 16, 6);
		for (int i = 0; i < 7; i++) {
			int bx = (int) ((i * 13 + tick * 0.7) % 92) - 6;
			int bh = 2 + (i % 3);
			w.fillRect(bx, -bh, 3 + (i % 2), bh);
		}
		w.dispose();
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
