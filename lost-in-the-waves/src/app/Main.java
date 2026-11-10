package app;

import java.awt.EventQueue;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import controller.GameController;
import model.GameModel;
import view.TelaPrincipal;

/** Ponto de entrada: monta o MVC (model, view e controller) e inicia o jogo. */
public class Main {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
					GameModel model = new GameModel();
					TelaPrincipal view = new TelaPrincipal();
					GameController controller = new GameController(model, view);
					controller.iniciar();
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Não foi possível iniciar o jogo:\n" + e,
							"Lost in the Waves", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
}
