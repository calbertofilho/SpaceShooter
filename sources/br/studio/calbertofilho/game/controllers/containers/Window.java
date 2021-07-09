package br.studio.calbertofilho.game.controllers.containers;

import java.awt.Image;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Window extends JFrame {

	private Panel game;

	public Window(String title, Image icon) {
		setTitle(title);
		setIconImage(icon);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(getContentPane().getPreferredSize());
		setMaximumSize(getContentPane().getPreferredSize());
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void addNotify() {
		super.addNotify();
		game = new Panel();
		setContentPane(game);
	}

}
