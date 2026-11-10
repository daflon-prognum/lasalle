package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * MODEL: guarda todo o estado da partida e aplica as regras do jogo.
 * Nao conhece nenhuma classe de interface grafica.
 */
public class GameModel {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 446;
	public static final int GOAL = 600;
	public static final int MAX_ENERGY = 100;
	public static final int INTRO_PAGES = 3;
	private static final int HIT_DAMAGE = 25;
	private static final int RECOVERY_TICKS = 60;
	private static final int SHIELD_TICKS = 360;
	private static final int SPAWN_LIMIT = 520;
	private static final int BEACH_DISTANCE = 540;

	private static final Stage[] STAGES = {
			new Stage("Mar Aberto", 5.0, 104, ObstacleType.ROCK, ObstacleType.LOG, ObstacleType.GULL, ObstacleType.BUOY),
			new Stage("Águas Perigosas", 6.4, 91, ObstacleType.SHARK, ObstacleType.GULL, ObstacleType.WHALE, ObstacleType.LOG),
			new Stage("Tempestade", 7.7, 82, ObstacleType.WHIRLPOOL, ObstacleType.WAVE, ObstacleType.GULL, ObstacleType.SHARK),
			new Stage("A Última Onda", 9.0, 75, ObstacleType.WAVE, ObstacleType.WHALE, ObstacleType.GULL, ObstacleType.WHIRLPOOL, ObstacleType.SHARK) };

	private final Surfer surfer = new Surfer();
	private final List<Obstacle> obstacles = new ArrayList<>();
	private final List<Item> items = new ArrayList<>();
	private final ScoreManager score = new ScoreManager();
	private final CollisionManager collisions = new CollisionManager();
	private final Random random = new Random();

	private GameState state = GameState.TITLE;
	private int level = 1;
	private int ticks;
	private int nextObstacle;
	private int spawned;
	private int intro;
	private int introTicks;
	private int endTicks;
	private int energy = MAX_ENERGY;
	private int shield;
	private int recovery;
	private double scroll;
	private boolean left;
	private boolean right;
	private boolean down;

	public GameModel() {
		resetScenery();
	}

	// ------------------------------------------------------------------
	// Comandos recebidos do controller
	// ------------------------------------------------------------------

	public void setInput(boolean left, boolean right, boolean down) {
		this.left = left;
		this.right = right;
		this.down = down;
	}

	public void jump() {
		if (state == GameState.PLAYING && !down) {
			surfer.jump();
		}
	}

	/** Acao da tecla ENTER: avanca menus, cutscenes e reinicia apos derrota. */
	public void confirm() {
		switch (state) {
		case TITLE:
			intro = 0;
			introTicks = 0;
			resetScenery();
			state = GameState.INTRO;
			break;
		case INTRO:
			intro++;
			introTicks = 0;
			if (intro == INTRO_PAGES) {
				start();
			}
			break;
		case ROUTE:
			level++;
			score.markStageStart();
			start();
			break;
		case LOST:
			start();
			break;
		case WON:
			backToMenu();
			break;
		default:
			break;
		}
	}

	public void togglePause() {
		if (state == GameState.PLAYING) {
			state = GameState.PAUSED;
		} else if (state == GameState.PAUSED) {
			state = GameState.PLAYING;
		}
	}

	public void pause() {
		if (state == GameState.PLAYING) {
			state = GameState.PAUSED;
		}
	}

	public void newGame() {
		level = 1;
		score.resetAll();
		start();
	}

	public void backToMenu() {
		level = 1;
		intro = 0;
		score.resetAll();
		resetScenery();
		state = GameState.TITLE;
	}

	// ------------------------------------------------------------------
	// Laco principal (chamado a cada tick do Timer)
	// ------------------------------------------------------------------

	public void update() {
		if (state == GameState.INTRO) {
			introTicks++;
			scroll = (scroll + 2.5) % (2 * WIDTH);
			return;
		}
		if (state == GameState.WON) {
			endTicks++;
			return;
		}
		if (state != GameState.PLAYING) {
			return;
		}
		ticks++;
		if (ticks % 4 == 0) {
			score.addPoints(1);
		}
		if (shield > 0) {
			shield--;
		}
		if (recovery > 0) {
			recovery--;
		}
		double speed = getStage().getSpeed() + getDistance() / 240.0;
		scroll = (scroll + speed * 0.5) % (2 * WIDTH);
		surfer.setCrouching(down && surfer.isLanded());
		surfer.move((right ? 1 : 0) - (left ? 1 : 0));
		spawn();
		updateObstacles(speed);
		updateItems(speed);
		checkEnd();
	}

	private void start() {
		surfer.reset();
		obstacles.clear();
		items.clear();
		score.restartStage();
		energy = MAX_ENERGY;
		shield = 0;
		recovery = 0;
		left = right = down = false;
		ticks = 0;
		scroll = 0;
		nextObstacle = 36;
		spawned = 0;
		state = GameState.PLAYING;
	}

	private void resetScenery() {
		ticks = 0;
		scroll = 0;
		obstacles.clear();
		items.clear();
		surfer.reset();
	}

	private void spawn() {
		nextObstacle--;
		if (getDistance() >= SPAWN_LIMIT || nextObstacle > 0) {
			return;
		}
		ObstacleType[] hazards = getStage().getHazards();
		ObstacleType type = spawned < hazards.length ? hazards[spawned] : hazards[random.nextInt(hazards.length)];
		obstacles.add(Obstacle.create(type, WIDTH + 10));
		if (spawned % 2 == 0) {
			ItemType[] kinds = ItemType.values();
			items.add(new Item(WIDTH + 185, kinds[(spawned / 2) % kinds.length]));
		}
		spawned++;
		nextObstacle = getStage().getInterval() + random.nextInt(18);
	}

	private void updateObstacles(double speed) {
		Iterator<Obstacle> it = obstacles.iterator();
		while (it.hasNext()) {
			Obstacle obstacle = it.next();
			obstacle.move(speed);
			obstacle.interact(surfer);
			if (collisions.hits(surfer, obstacle)) {
				it.remove();
				takeHit();
			} else if (obstacle.isOffScreen()) {
				it.remove();
				score.registerDodge();
			}
		}
	}

	private void takeHit() {
		score.resetCombo();
		if (shield > 0) {
			shield = 0;
		} else if (recovery == 0) {
			energy = Math.max(0, energy - HIT_DAMAGE);
			recovery = RECOVERY_TICKS;
		}
	}

	private void updateItems(double speed) {
		Iterator<Item> it = items.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			item.move(speed);
			if (collisions.collects(surfer, item)) {
				it.remove();
				collect(item.getType());
			} else if (item.isOffScreen()) {
				it.remove();
			}
		}
	}

	private void collect(ItemType type) {
		energy = Math.min(MAX_ENERGY, energy + type.getEnergy());
		score.addPoints(type.getPoints());
		if (type == ItemType.SHIELD) {
			shield = SHIELD_TICKS;
		}
	}

	private void checkEnd() {
		if (energy == 0) {
			state = GameState.LOST;
			score.saveRecord();
		} else if (getDistance() >= GOAL) {
			state = level == STAGES.length ? GameState.WON : GameState.ROUTE;
			endTicks = 0;
			score.saveRecord();
			surfer.land();
		}
	}

	// ------------------------------------------------------------------
	// Consultas usadas pela view e pelo controller
	// ------------------------------------------------------------------

	public GameState getState() {
		return state;
	}

	public Stage getStage() {
		return STAGES[level - 1];
	}

	public int getLevel() {
		return level;
	}

	public int getStageCount() {
		return STAGES.length;
	}

	public boolean isLastStage() {
		return level == STAGES.length;
	}

	public int getDistance() {
		return ticks / 4;
	}

	public int getDistanceRemaining() {
		return Math.max(0, GOAL - getDistance());
	}

	public boolean isStormy() {
		return level >= 3 && getDistance() < BEACH_DISTANCE + 5;
	}

	public boolean isBeachVisible() {
		return isLastStage() && getDistance() >= BEACH_DISTANCE;
	}

	/** Deslocamento da praia em pixels a partir da borda direita. */
	public int getShoreX() {
		return WIDTH - (getDistance() - BEACH_DISTANCE) * 13;
	}

	public boolean isSurferVisible() {
		return recovery == 0 || ticks % 12 < 8 || state != GameState.PLAYING;
	}

	public Surfer getSurfer() {
		return surfer;
	}

	public List<Obstacle> getObstacles() {
		return Collections.unmodifiableList(obstacles);
	}

	public List<Item> getItems() {
		return Collections.unmodifiableList(items);
	}

	public int getEnergy() {
		return energy;
	}

	public boolean hasShield() {
		return shield > 0;
	}

	public int getPoints() {
		return score.getPoints();
	}

	public int getRecord() {
		return Math.max(score.getRecord(), score.getPoints());
	}

	public int getCombo() {
		return score.getCombo();
	}

	public int getIntro() {
		return intro;
	}

	/** Contador de quadros da cutscene atual (usado pela view para animar o prologo). */
	public int getIntroTicks() {
		return introTicks;
	}

	/** Contador de quadros da cena final (chegada a praia). */
	public int getEndTicks() {
		return endTicks;
	}

	public int getTicks() {
		return ticks;
	}

	public double getScroll() {
		return scroll;
	}
}
