package br.studio.calbertofilho.game.texts;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;

public class Notification {

	private double posX, posY;
	private long time, start, elapsed, elapsedTime;
	private int textLength, alphaFontColor;
	private String message;
	private Font font;

	public Notification(double posX, double posY, long time, String message) {
		this.posX = posX;
		this.posY = posY;
		this.time = time;
		this.message = message;
		start = System.nanoTime();
		loadResources();
	}

	private void loadResources() {
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new File("resources\\assets\\fonts\\Aero.ttf"));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	public void update() {
		elapsedTime = (System.nanoTime() - start) / 1000000;
	}

	public void render(Graphics2D graphics) {
		graphics.setFont(font.deriveFont(Font.PLAIN, 12));
		elapsed = (System.nanoTime() - start) / 1000000;
		alphaFontColor = (int) (255 * Math.sin(Math.PI * elapsed / time));
		alphaFontColor = (alphaFontColor > 255) ? 255 : alphaFontColor;
		alphaFontColor = (alphaFontColor < 0) ? 0 : alphaFontColor;
		graphics.setColor(new Color(255, 255, 0, alphaFontColor));
		textLength = (int) graphics.getFontMetrics().getStringBounds(message, graphics).getWidth();
		graphics.drawString(message, (int) posX - (textLength / 2), (int) posY);
	}

	public boolean isVisible() {
		if (elapsedTime > time)
			return false;
		return true;
	}

}
