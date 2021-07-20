package br.studio.calbertofilho.game.managers.states;

import java.awt.Graphics2D;

import br.studio.calbertofilho.game.controllers.handlers.Keyboard;
import br.studio.calbertofilho.game.controllers.handlers.Mouse;
import br.studio.calbertofilho.game.managers.StatesManager;

public abstract class States {

	protected StatesManager manager;

	public States(StatesManager manager) {
		this.manager = manager;
		init();
	}

	public abstract void init();
	public abstract void input(Mouse mouse, Keyboard keyboard);
	public abstract void update();
	public abstract void render(Graphics2D graphics);

}
