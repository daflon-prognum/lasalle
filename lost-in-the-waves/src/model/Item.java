package model;

/** Item coletavel que recupera energia, da pontos ou protege a surfista. */
public class Item extends GameObject {

	public static final int SIZE = 24;
	private final ItemType type;

	public Item(double x, ItemType type) {
		super(x, Surfer.FLOOR - (type.isHigh() ? 93 : 30), SIZE, SIZE);
		this.type = type;
	}

	public ItemType getType() {
		return type;
	}
}
