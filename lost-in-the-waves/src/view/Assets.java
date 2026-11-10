package view;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/** Carrega as imagens da pasta src/assets (empacotadas junto com as classes). */
public final class Assets {

	public static final BufferedImage OCEAN = load("sea_day");
	public static final BufferedImage BEACH = load("beach");
	public static final BufferedImage SURFER = load("surfer");
	public static final BufferedImage BOARD = load("board");
	public static final BufferedImage PALM = load("palm");
	public static final BufferedImage SAND = load("sand");
	public static final BufferedImage CRAB = load("crab");
	public static final BufferedImage BALL = load("ball");

	private Assets() {
	}

	private static BufferedImage load(String name) {
		URL url = Assets.class.getResource("/assets/" + name + ".png");
		if (url == null) {
			throw new IllegalStateException("Imagem nao encontrada: assets/" + name + ".png");
		}
		try {
			return ImageIO.read(url);
		} catch (IOException e) {
			throw new IllegalStateException("Erro ao ler assets/" + name + ".png", e);
		}
	}
}
