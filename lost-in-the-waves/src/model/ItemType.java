package model;

/** Itens coletaveis e o efeito de cada um. */
public enum ItemType {
	COCONUT("Coco", 25, 0, false),
	WATER("Agua", 15, 0, false),
	COIN("Moeda", 0, 100, true),
	SHIELD("Escudo", 0, 0, false);

	private final String label;
	private final int energy;
	private final int points;
	private final boolean high;

	ItemType(String label, int energy, int points, boolean high) {
		this.label = label;
		this.energy = energy;
		this.points = points;
		this.high = high;
	}

	public String getLabel() {
		return label;
	}

	public int getEnergy() {
		return energy;
	}

	public int getPoints() {
		return points;
	}

	/** Itens altos exigem pulo para serem coletados. */
	public boolean isHigh() {
		return high;
	}
}
