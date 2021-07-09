package br.studio.calbertofilho.game.controllers.containers;

import java.awt.Image;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Window extends JFrame {

	private DrawPanel game;
	private int width, height;

	public Window(String title, Image icon, int width, int height) {
		this.width = width;
		this.height = height;
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
		game = new DrawPanel(width, height);
		setContentPane(game);
	}

}
