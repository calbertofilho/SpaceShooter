package br.studio.calbertofilho.game.managements.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;

import br.studio.calbertofilho.game.controllers.containers.DrawablePanel;
import br.studio.calbertofilho.game.controllers.handlers.Keyboard;
import br.studio.calbertofilho.game.controllers.handlers.Mouse;
import br.studio.calbertofilho.game.managements.StatesManager;

public class PauseState extends States {

	private Font textFont;
	private String text;
	private int textLength;

	public PauseState(StatesManager manager) {
		super(manager);
		loadResources();
	}

	private void loadResources() {
		try {
			textFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources\\assets\\fonts\\Audiowide-Regular.ttf"));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void input(Mouse mouse, Keyboard keyboard) {
		keyboard.pauseKey.tick();
		if (keyboard.pauseKey.isClicked())
			manager.pop(StatesManager.PAUSE);
	}

	@Override
	public void update() {}

	@Override
	public void render(Graphics2D graphics) {
		graphics.setColor(new Color(55, 108, 151));
		graphics.fillRect(0, 0, DrawablePanel.getGameWidth(), DrawablePanel.getGameHeight());
		graphics.setFont(textFont.deriveFont(Font.BOLD, 24));
		graphics.setColor(Color.WHITE);
		text = "---     J O G O   S U S P E N S O     ---";
		textLength = (int) graphics.getFontMetrics().getStringBounds(text, graphics).getWidth();
		graphics.drawString(text, DrawablePanel.getGameWidth() / 2 - textLength / 2, DrawablePanel.getGameHeight() / 2);
	}

}
