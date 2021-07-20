package br.studio.calbertofilho.game.managers;

import java.awt.Graphics2D;

import br.studio.calbertofilho.game.controllers.handlers.Keyboard;
import br.studio.calbertofilho.game.controllers.handlers.Mouse;
import br.studio.calbertofilho.game.managers.states.FinishState;
import br.studio.calbertofilho.game.managers.states.GameOverState;
import br.studio.calbertofilho.game.managers.states.MenuState;
import br.studio.calbertofilho.game.managers.states.PauseState;
import br.studio.calbertofilho.game.managers.states.PlayState;
import br.studio.calbertofilho.game.managers.states.States;

public class StatesManager {

	private States states[];
	public static final int MENU = 0;
	public static final int PLAY = 1;
	public static final int PAUSE = 2;
	public static final int GAME_OVER = 3;
	public static final int FINISH = 4;

	public StatesManager() {
		states = new States[5];
		states[PLAY] = new PlayState(this);
	}

	public boolean getState(int state) {
		return states[state] != null;
	}

	public void add(int state) {
		if (states[state] != null)
			return;
		if (state == MENU)
			states[MENU] = new MenuState(this);
		if (state == PLAY)
			states[PLAY] = new PlayState(this);
		if (state == PAUSE)
			states[PAUSE] = new PauseState(this);
		if (state == GAME_OVER)
			states[GAME_OVER] = new GameOverState(this);
		if (state == FINISH)
			states[FINISH] = new FinishState(this);
	}

	public void pop(int state) {
		states[state] = null;
	}

	public void addAndPop(int state) {
		addAndPop(state, 0);
	}

	private void addAndPop(int state, int remove) {
		pop(remove);
		add(state);
	}

	public void input(Mouse mouse, Keyboard keyboard) {
		for (States state : states)
			if (state != null)
				state.input(mouse, keyboard);
	}

	public void update() {
		for (States state : states)
			if (state != null)
				state.update();
	}

	public void render(Graphics2D graphics) {
		for (States state : states)
			if (state != null)
				state.render(graphics);
	}

}
