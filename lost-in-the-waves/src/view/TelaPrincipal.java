package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * VIEW: janela principal criada com o WindowBuilder.
 * Possui menu, HUD (fase, distancia, pontos, recorde e energia) e o painel do jogo.
 * Nao trata eventos: o GameController registra os listeners pelos getters.
 */
public class TelaPrincipal extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final Color INK = new Color(16, 38, 60);
	private static final Color FOAM = new Color(229, 250, 248);
	private static final Color GOLD = new Color(255, 203, 104);
	private static final Color GREEN = new Color(97, 214, 177);
	private static final Color RED = new Color(245, 118, 98);

	private JPanel contentPane;
	private GamePanel gamePanel;
	private JLabel lblFase;
	private JLabel lblDistancia;
	private JLabel lblPontos;
	private JLabel lblRecorde;
	private JLabel lblEscudo;
	private JProgressBar pbEnergia;
	private JMenuItem mntmNovoJogo;
	private JMenuItem mntmPausar;
	private JMenuItem mntmMenuPrincipal;
	private JMenuItem mntmSair;
	private JMenuItem mntmControles;
	private JMenuItem mntmSobre;

	/**
	 * Create the frame.
	 */
	public TelaPrincipal() {
		setTitle("Lost in the Waves");
		setIconImage(Toolkit.getDefaultToolkit().getImage(TelaPrincipal.class.getResource("/assets/ball.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnJogo = new JMenu("Jogo");
		menuBar.add(mnJogo);

		mntmNovoJogo = new JMenuItem("Novo jogo");
		mnJogo.add(mntmNovoJogo);

		mntmPausar = new JMenuItem("Pausar / Continuar");
		mnJogo.add(mntmPausar);

		mntmMenuPrincipal = new JMenuItem("Menu principal");
		mnJogo.add(mntmMenuPrincipal);

		JSeparator separator = new JSeparator();
		mnJogo.add(separator);

		mntmSair = new JMenuItem("Sair");
		mnJogo.add(mntmSair);

		JMenu mnAjuda = new JMenu("Ajuda");
		menuBar.add(mnAjuda);

		mntmControles = new JMenuItem("Controles");
		mnAjuda.add(mntmControles);

		mntmSobre = new JMenuItem("Sobre");
		mnAjuda.add(mntmSobre);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panelHud = new JPanel();
		panelHud.setBackground(INK);
		panelHud.setPreferredSize(new Dimension(800, 52));
		panelHud.setLayout(null);
		contentPane.add(panelHud, BorderLayout.NORTH);

		lblFase = new JLabel("1/4  Mar Aberto");
		lblFase.setForeground(GOLD);
		lblFase.setFont(new Font("Monospaced", Font.BOLD, 15));
		lblFase.setBounds(16, 6, 260, 20);
		panelHud.add(lblFase);

		lblDistancia = new JLabel("600 m até o fim do trecho");
		lblDistancia.setForeground(FOAM);
		lblDistancia.setFont(new Font("Monospaced", Font.BOLD, 13));
		lblDistancia.setBounds(16, 27, 260, 20);
		panelHud.add(lblDistancia);

		lblPontos = new JLabel("0 pts");
		lblPontos.setForeground(FOAM);
		lblPontos.setFont(new Font("Monospaced", Font.BOLD, 15));
		lblPontos.setBounds(290, 6, 180, 20);
		panelHud.add(lblPontos);

		lblRecorde = new JLabel("Recorde: 0");
		lblRecorde.setForeground(FOAM);
		lblRecorde.setFont(new Font("Monospaced", Font.BOLD, 13));
		lblRecorde.setBounds(290, 27, 180, 20);
		panelHud.add(lblRecorde);

		JLabel lblEnergia = new JLabel("ENERGIA");
		lblEnergia.setForeground(GOLD);
		lblEnergia.setFont(new Font("Monospaced", Font.BOLD, 13));
		lblEnergia.setBounds(490, 6, 80, 20);
		panelHud.add(lblEnergia);

		lblEscudo = new JLabel("ESCUDO");
		lblEscudo.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEscudo.setForeground(new Color(160, 241, 170));
		lblEscudo.setFont(new Font("Monospaced", Font.BOLD, 13));
		lblEscudo.setBounds(684, 6, 100, 20);
		lblEscudo.setVisible(false);
		panelHud.add(lblEscudo);

		pbEnergia = new JProgressBar();
		pbEnergia.setValue(100);
		pbEnergia.setStringPainted(true);
		pbEnergia.setForeground(GREEN);
		pbEnergia.setBackground(new Color(55, 81, 103));
		pbEnergia.setFont(new Font("Monospaced", Font.BOLD, 11));
		pbEnergia.setBounds(490, 28, 294, 16);
		panelHud.add(pbEnergia);

		gamePanel = new GamePanel();
		contentPane.add(gamePanel, BorderLayout.CENTER);

		JLabel lblControles = new JLabel(
				"← → MOVER   •   ESPAÇO PULAR   •   ↓ AGACHAR   •   P PAUSAR   •   ENTER CONFIRMAR");
		lblControles.setOpaque(true);
		lblControles.setBackground(INK);
		lblControles.setForeground(FOAM);
		lblControles.setHorizontalAlignment(SwingConstants.CENTER);
		lblControles.setFont(new Font("Monospaced", Font.BOLD, 13));
		lblControles.setPreferredSize(new Dimension(800, 30));
		contentPane.add(lblControles, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(null);
	}

	/** Atualiza os componentes do HUD com os valores informados pelo controller. */
	public void atualizarHud(int fase, int totalFases, String nomeFase, int distancia, int pontos, int recorde,
			int energia, boolean escudo) {
		lblFase.setText(fase + "/" + totalFases + "  " + nomeFase);
		lblDistancia.setText(distancia + " m até o fim do trecho");
		lblPontos.setText(pontos + " pts");
		lblRecorde.setText("Recorde: " + recorde);
		pbEnergia.setValue(energia);
		pbEnergia.setForeground(energia > 25 ? GREEN : RED);
		lblEscudo.setVisible(escudo);
	}

	public void mostrarMensagem(String titulo, String mensagem) {
		JOptionPane.showMessageDialog(this, mensagem, titulo, JOptionPane.INFORMATION_MESSAGE);
	}

	public GamePanel getGamePanel() {
		return gamePanel;
	}

	public JMenuItem getMntmNovoJogo() {
		return mntmNovoJogo;
	}

	public JMenuItem getMntmPausar() {
		return mntmPausar;
	}

	public JMenuItem getMntmMenuPrincipal() {
		return mntmMenuPrincipal;
	}

	public JMenuItem getMntmSair() {
		return mntmSair;
	}

	public JMenuItem getMntmControles() {
		return mntmControles;
	}

	public JMenuItem getMntmSobre() {
		return mntmSobre;
	}
}
