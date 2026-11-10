package view;

import java.awt.Color;
import java.awt.Graphics2D;

import model.Gull;
import model.Item;
import model.Obstacle;
import model.ObstacleType;
import model.Surfer;

/** Desenha obstaculos e itens em pixel art usando Graphics2D. */
public final class SpriteRenderer {

	private SpriteRenderer() {
	}

	public static void drawObstacle(Graphics2D g, Obstacle obstacle) {
		int left = (int) obstacle.getX();
		int width = obstacle.getWidth();
		int height = obstacle.getHeight();
		int floor = Surfer.FLOOR;
		switch (obstacle.getType()) {
		case GULL:
			drawGull(g, left, floor - Gull.FLIGHT_HEIGHT);
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
			drawWave(g, left, floor);
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

	private static void drawGull(Graphics2D g, int left, int y) {
		int[] wingsX = { 0, 10, 20, 29, 39, 47, 54, 46, 37, 29, 21, 12 };
		int[] wingsY = { 0, 1, 7, 11, 4, 2, 5, 8, 11, 18, 13, 7 };
		for (int i = 0; i < wingsX.length; i++) {
			wingsX[i] += left;
			wingsY[i] += y;
		}
		g.setColor(new Color(0xFFF7DE));
		g.fillPolygon(wingsX, wingsY, wingsX.length);
		g.setColor(new Color(0x17344E));
		g.drawPolygon(wingsX, wingsY, wingsX.length);
		g.setColor(new Color(0xFFCB68));
		g.fillRect(left + 18, y + 13, 6, 3);
	}

	public static void drawWave(Graphics2D g, int x, int y) {
		Graphics2D wave = (Graphics2D) g.create();
		wave.translate(x, y);
		wave.setColor(new Color(0x087896));
		wave.fillPolygon(new int[] { 0, 12, 22, 31, 45, 60, 65, 50, 47, 54, 72 },
				new int[] { 0, -9, -26, -34, -38, -33, -24, -28, -17, -7, 0 }, 11);
		wave.setColor(new Color(0x31C6CD));
		wave.fillPolygon(new int[] { 7, 22, 35, 45, 39, 44, 58 }, new int[] { 0, -11, -29, -29, -17, -5, 0 }, 7);
		wave.setColor(new Color(0xEDFDF8));
		wave.fillPolygon(new int[] { 22, 31, 45, 60, 65, 59, 48, 39, 30 },
				new int[] { -26, -34, -38, -33, -24, -25, -31, -31, -27 }, 9);
		wave.fillRect(4, 0, 64, 3);
		wave.dispose();
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
