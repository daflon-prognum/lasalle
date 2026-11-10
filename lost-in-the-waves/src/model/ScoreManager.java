package model;

/** Controla pontuacao, combo de desvios e recorde. */
public class ScoreManager {

	private static final int DODGE_POINTS = 15;
	private static final int COMBO_BONUS = 5;
	private static final int MAX_COMBO = 5;

	private int points;
	private int record;
	private int combo;
	private int stageStartPoints;

	public void addPoints(int value) {
		points += value;
	}

	/** Obstaculo desviado: pontos base + bonus pelo combo de desvios seguidos. */
	public void registerDodge() {
		combo = Math.min(combo + 1, MAX_COMBO);
		points += DODGE_POINTS + COMBO_BONUS * combo;
	}

	public void resetCombo() {
		combo = 0;
	}

	public void saveRecord() {
		record = Math.max(record, points);
	}

	/** Guarda a pontuacao no inicio da fase para recomecar dela apos um game over. */
	public void markStageStart() {
		stageStartPoints = points;
	}

	public void restartStage() {
		points = stageStartPoints;
		combo = 0;
	}

	public void resetAll() {
		points = 0;
		stageStartPoints = 0;
		combo = 0;
	}

	public int getPoints() {
		return points;
	}

	public int getRecord() {
		return record;
	}

	public int getCombo() {
		return combo;
	}
}
