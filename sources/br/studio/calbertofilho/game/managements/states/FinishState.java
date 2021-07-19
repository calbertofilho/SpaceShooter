package br.studio.calbertofilho.game.managements.states;

import java.awt.Graphics2D;

import br.studio.calbertofilho.game.controllers.handlers.Keyboard;
import br.studio.calbertofilho.game.controllers.handlers.Mouse;
import br.studio.calbertofilho.game.managements.StatesManager;

public class FinishState extends States {

	public FinishState(StatesManager manager) {
		super(manager);
	}

	@Override
	public void input(Mouse mouse, Keyboard keyboard) {}

	@Override
	public void update() {}

	@Override
	public void render(Graphics2D graphics) {}

}
