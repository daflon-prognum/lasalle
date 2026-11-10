package model;

/** Estados possiveis da partida. */
public enum GameState {
	TITLE,   // menu principal
	INTRO,   // cutscene inicial (prologo)
	PLAYING, // partida em andamento
	PAUSED,  // pausa
	LOST,    // energia acabou (game over)
	ROUTE,   // fase concluida, cutscene intermediaria
	WON      // chegou a praia (cutscene final)
}
