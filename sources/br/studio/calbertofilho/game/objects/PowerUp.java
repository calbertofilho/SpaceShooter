package br.studio.calbertofilho.game.objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import br.studio.calbertofilho.game.controllers.containers.DrawablePanel;

public class PowerUp {

	private double posX, posY;
	private int length, type, speed;
	private Color typeColor;
	// Types
	//
	// 1 -- extralife
	// 2 -- +1 bullet power
	// 3 -- +2 bullet power

	public PowerUp(int type, double posX, double posY) {
		this.type = type;
		this.posX = posX;
		this.posY = posY;
		length = (type == 3) ? 5 : 3;
		typeColor = (type == 1) ? Color.GREEN : Color.ORANGE;
		speed = 2;
	}

	public void update() {
		posY += speed;
	}

	public void render(Graphics2D graphics) {
		// draw a contour line   //
		graphics.setStroke(new BasicStroke(3));
		graphics.setColor(typeColor.darker());
		graphics.fillRect((int) (posX - length), (int) (posY - length), length * 2, length * 2);
		graphics.setStroke(new BasicStroke(1));
		// fill the object       //
		graphics.setColor(typeColor);
		graphics.fillRect((int) (posX - length), (int) (posY - length), length * 2, length * 2);
	}

	public boolean isVisible() {
		if (posY > DrawablePanel.getGameHeight() + length)
			return true;
		return false;
	}

	public double getPosX() {
		return posX;
	}

	public double getPosY() {
		return posY;
	}

	public int getLength() {
		return length;
	}

	public int getType() {
		return type;
	}

}
