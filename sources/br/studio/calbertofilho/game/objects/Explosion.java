package br.studio.calbertofilho.game.objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Explosion {

	private double posX, posY;
	private int radius, maxRadius;

	public Explosion(double posX, double posY, int radius, int maxRadius) {
		this.posX = posX;
		this.posY = posY;
		this.radius = radius;
		this.maxRadius = maxRadius;
	}

	public void update() {
		radius += 2;
	}

	public void render(Graphics2D graphics) {
		graphics.setColor(Color.RED);
		graphics.setStroke(new BasicStroke(3));
		graphics.drawOval((int) (posX - radius), (int) (posY - radius), radius * 2, radius * 2);
		graphics.setStroke(new BasicStroke(1));
	}

	public boolean isVisible() {
		if (radius >= maxRadius)
			return false;
		return true;
	}

}
