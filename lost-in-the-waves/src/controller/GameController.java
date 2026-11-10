package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Timer;

import model.GameModel;
import view.TelaPrincipal;

/**
 * CONTROLLER: recebe os eventos do Swing (teclado, menu, Timer e janela),
 * repassa os comandos para o model e manda a view se atualizar.
 */
public class GameController implements ActionListener, KeyListener {

	private static final int FRAME_DELAY = 16; // ~60 quadros por segundo

	private final GameModel model;
	private final TelaPrincipal view;
	private final Timer timer;
	private boolean left;
	private boolean right;
	private boolean down;
	private boolean jumpHeld;
	private boolean enterHeld;
	private boolean pauseHeld;

	public GameController(GameModel model, TelaPrincipal view) {
		this.model = model;
		this.view = view;
		this.timer = new Timer(FRAME_DELAY, this);

		view.getGamePanel().setModel(model);
		view.getGamePanel().addKeyListener(this);
		view.getMntmNovoJogo().addActionListener(this);
		view.getMntmPausar().addActionListener(this);
		view.getMntmMenuPrincipal().addActionListener(this);
		view.getMntmSair().addActionListener(this);
		view.getMntmControles().addActionListener(this);
		view.getMntmSobre().addActionListener(this);
		view.addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
				view.getGamePanel().requestFocusInWindow();
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				releaseKeys();
				model.pause();
				atualizarTela();
			}
		});
	}

	public void iniciar() {
		atualizarTela();
		view.setVisible(true);
		view.getGamePanel().requestFocusInWindow();
		timer.start();
	}

	// ------------------------------------------------------------------
	// ActionListener: Timer (game loop) e itens de menu
	// ------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == timer) {
			model.setInput(left, right, down);
			model.update();
		} else if (source == view.getMntmNovoJogo()) {
			releaseKeys();
			model.newGame();
		} else if (source == view.getMntmPausar()) {
			model.togglePause();
		} else if (source == view.getMntmMenuPrincipal()) {
			model.backToMenu();
		} else if (source == view.getMntmSair()) {
			view.dispose();
			System.exit(0);
		} else if (source == view.getMntmControles()) {
			model.pause();
			view.mostrarMensagem("Controles",
					"← / → : mover a surfista\n" + "ESPAÇO ou ↑ : pular\n"
							+ "↓ : agachar (passar sob as gaivotas)\n" + "P : pausar / continuar\n"
							+ "ENTER : começar, avançar a história e tentar de novo");
		} else if (source == view.getMntmSobre()) {
			model.pause();
			view.mostrarMensagem("Sobre",
					"Lost in the Waves\n" + "Trabalho G2 - Programação Orientada a Objetos\n"
							+ "Java Swing + MVC\n\n" + "Assets: High Tides Basic Pack, por Kanpelle.");
		}
		atualizarTela();
		if (source != timer) {
			view.getGamePanel().requestFocusInWindow();
		}
	}

	private void atualizarTela() {
		view.atualizarHud(model.getLevel(), model.getStageCount(), model.getStage().getName(),
				model.getDistanceRemaining(), model.getPoints(), model.getRecord(), model.getEnergy(),
				model.hasShield());
		view.getGamePanel().repaint();
	}

	private void releaseKeys() {
		left = right = down = jumpHeld = enterHeld = pauseHeld = false;
	}

	// ------------------------------------------------------------------
	// KeyListener: teclado do jogador
	// ------------------------------------------------------------------

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_A:
			left = true;
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_D:
			right = true;
			break;
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_S:
			down = true;
			break;
		case KeyEvent.VK_SPACE:
		case KeyEvent.VK_UP:
		case KeyEvent.VK_W:
			if (!jumpHeld) {
				model.setInput(left, right, down);
				model.jump();
			}
			jumpHeld = true;
			break;
		case KeyEvent.VK_ENTER:
			if (!enterHeld) {
				model.confirm();
			}
			enterHeld = true;
			break;
		case KeyEvent.VK_P:
			if (!pauseHeld) {
				model.togglePause();
			}
			pauseHeld = true;
			break;
		default:
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_A:
			left = false;
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_D:
			right = false;
			break;
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_S:
			down = false;
			break;
		case KeyEvent.VK_SPACE:
		case KeyEvent.VK_UP:
		case KeyEvent.VK_W:
			jumpHeld = false;
			break;
		case KeyEvent.VK_ENTER:
			enterHeld = false;
			break;
		case KeyEvent.VK_P:
			pauseHeld = false;
			break;
		default:
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// nao utilizado
	}
}
