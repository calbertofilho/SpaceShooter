package br.studio.calbertofilho.game.objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import br.studio.calbertofilho.game.controllers.containers.DrawablePanel;

public class PowerUp {

	private double posX, posY;
	private int length, type, speed;
	private Color typeColor;
	public static final int EXTRALIFE = 1;
	public static final int POWER = 2;
	public static final int DOUBLEPOWER = 3;
	public static final int SLOWDOWN = 4;
	public static final int INVINCIBILITY = 5;

	public PowerUp(int type, double posX, double posY) {
		this.type = type;
		this.posX = posX;
		this.posY = posY;
		speed = 2;
		length = 3;
		if (type == EXTRALIFE)
			typeColor = Color.GREEN;
		if ((type == POWER) || (type == DOUBLEPOWER))
			typeColor = Color.ORANGE;
		if (type == DOUBLEPOWER)
			length = 4;
		if (type == SLOWDOWN)
			typeColor = Color.CYAN;
		if (type == INVINCIBILITY)
			typeColor = Color.MAGENTA;
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
			return false;
		return true;
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
