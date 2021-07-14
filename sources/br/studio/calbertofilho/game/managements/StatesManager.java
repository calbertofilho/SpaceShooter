package br.studio.calbertofilho.game.managements;

import java.awt.Graphics2D;
import java.util.ArrayList;

import br.studio.calbertofilho.game.controllers.handlers.Keyboard;
import br.studio.calbertofilho.game.controllers.handlers.Mouse;
import br.studio.calbertofilho.game.managements.states.PlayState;
import br.studio.calbertofilho.game.managements.states.States;

public class StatesManager {

	private ArrayList<States> states;
	private static final int PLAY = 0;
//	private static final int MENU = 1;
//	private static final int PAUSE = 2;
//	private static final int GAME_OVER = 3;

	public StatesManager() {
		states = new ArrayList<States>();
		add(PLAY);
	}

	public void pop(int state) {
		states.remove(state);
	}

	public void add(int state) {
		if (state == PLAY)
			states.add(new PlayState(this));
//		if (state == MENU)
//			states.add(new MenuState(this));
//		if (state == PAUSE)
//			states.add(new PauseState(this));
//		if (state == GAME_OVER)
//			states.add(new GameOverState(this));
	}

	public void addAndPop(int state) {
		states.remove(0);
		add(state);
	}

	public void input(Mouse mouse, Keyboard keyboard) {
		for (States state : states)
			state.input(mouse, keyboard);
	}

	public void update() {
		for (States state : states)
			state.update();
	}

	public void render(Graphics2D graphics) {
		for (States state : states)
			state.render(graphics);
	}

}
