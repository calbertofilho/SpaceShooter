package br.studio.calbertofilho.game.controllers.handlers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import br.studio.calbertofilho.game.controllers.containers.DrawablePanel;

public class Keyboard implements KeyListener {

	private static ArrayList<Key> keys = new ArrayList<Key>();

	public class Key {
		private int presses, absorbs;
		private boolean down, clicked;

		public Key() {
			keys.add(this);
		}

		public void toggle(boolean pressed) {
			if (pressed != down)
				down = pressed;
			if (pressed)
				presses++;
		}

		public void tick() {
			if (absorbs < presses) {
				absorbs++;
				clicked = true;
			} else
				clicked = false;
		}

		public boolean isDown() {
			return down;
		}

		public boolean isClicked() {
			return clicked;
		}
	}

	public Key upKey = new Key();
	public Key downKey = new Key();
	public Key leftKey = new Key();
	public Key rightKey = new Key();
	public Key attackKey = new Key();
	public Key pauseKey = new Key();
	public Key enterKey = new Key();
	public Key escapeKey = new Key();

	public Keyboard(DrawablePanel game) {
		game.addKeyListener(this);
	}

	public void releaseAll() {
		for (Key key : keys)
			key.down = false;
	}

	public void tick() {
		for (Key key : keys)
			key.tick();
	}

	public void toggle(KeyEvent key, boolean pressed) {
		if ((key.getKeyCode() == KeyEvent.VK_W) || (key.getKeyCode() == KeyEvent.VK_UP))
			upKey.toggle(pressed);
		if ((key.getKeyCode() == KeyEvent.VK_S) || (key.getKeyCode() == KeyEvent.VK_DOWN))
			downKey.toggle(pressed);
		if ((key.getKeyCode() == KeyEvent.VK_A) || (key.getKeyCode() == KeyEvent.VK_LEFT))
			leftKey.toggle(pressed);
		if ((key.getKeyCode() == KeyEvent.VK_D) || (key.getKeyCode() == KeyEvent.VK_RIGHT))
			rightKey.toggle(pressed);
		if (key.getKeyCode() == KeyEvent.VK_SPACE)
			attackKey.toggle(pressed);
		if (key.getKeyCode() == KeyEvent.VK_PAUSE)
			pauseKey.toggle(pressed);
		if (key.getKeyCode() == KeyEvent.VK_ENTER)
			enterKey.toggle(pressed);
		if (key.getKeyCode() == KeyEvent.VK_ESCAPE)
			escapeKey.toggle(pressed);
	}

	@Override
	public void keyTyped(KeyEvent key) {}

	@Override
	public void keyPressed(KeyEvent key) {
		toggle(key, true);
	}

	@Override
	public void keyReleased(KeyEvent key) {
		toggle(key, false);
	}

}
