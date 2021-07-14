package br.studio.calbertofilho.game.objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import br.studio.calbertofilho.game.controllers.containers.DrawablePanel;

public class Bullet {

	private double posX, posY;
	private int radius;
	private double dX, dY;
	private double radians, speed;
	private Color normalColor;

	public Bullet(double angle, int x, int y) {
		this.posX = x;
		this.posY = y;
		radius = 2;
		radians = Math.toRadians(angle);
		speed = 10;
		dX = Math.cos(radians) * speed;
		dY = Math.sin(radians) * speed;
		normalColor = Color.ORANGE;
	}

	public void update() {
		posX += dX;
		posY += dY;
	}

	public void render(Graphics2D graphics) {
		graphics.setStroke(new BasicStroke(3));
		graphics.setColor(normalColor.darker());
		graphics.drawOval((int) posX - radius, (int) posY - radius, radius * 2, radius * 2);
		graphics.setStroke(new BasicStroke(1));
		graphics.setColor(normalColor);
		graphics.fillOval((int) posX - radius, (int) posY - radius, radius * 2, radius * 2);
	}

	public boolean isVisible() {
		if ((posX < -radius) || (posY < -radius) || (posX > DrawablePanel.getGameWidth() + radius) || (posY > DrawablePanel.getGameHeight() + radius))
			return true;
		return false;
	}

	public double getX() {
		return posX;
	}

	public double getY() {
		return posY;
	}

	public double getRadius() {
		return radius;
	}

}
