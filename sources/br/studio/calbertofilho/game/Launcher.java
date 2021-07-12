package br.studio.calbertofilho.game;

import javax.swing.ImageIcon;

import br.studio.calbertofilho.game.controllers.containers.Window;

public class Launcher {

	public static void main(String[] args) {
		new Window("SpaceShooter v1.0", new ImageIcon("resources\\assets\\images\\icon\\gameIcon.png").getImage(), 600, 800);
	}

}
