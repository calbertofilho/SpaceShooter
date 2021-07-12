package br.studio.calbertofilho.game.objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import br.studio.calbertofilho.game.controllers.containers.DrawPanel;

public class Enemy {

	private double posX, posY;
	private double dX, dY, angle, radians, speed;
	private int radius, health, type, rank;
	private Color normalColor;
	private boolean ready, dead;

	public Enemy(int type, int rank) {
		this.type = type;
		this.rank = rank;
		if (type == 1) {
			normalColor = Color.BLUE;
			if (rank == 1) {
				speed = 2;
				radius = 5;
				health = 1;
			}
		}
		posX = Math.random() * DrawPanel.getGameWidth() / 2 + DrawPanel.getGameHeight() / 4;
		posY = -radius;
		angle = Math.random() * 140 + 20;
		radians = Math.toRadians(angle);
		dX = Math.cos(radians) * speed;
		dY = Math.sin(radians) * speed;
		ready = dead = false;
	}

	public void update() {
		posX += dX;
		posY += dY;
		if (!ready) {
			if ((posX > radius) && (posY > radius) && (posX < DrawPanel.getGameWidth() - radius) && (posY < DrawPanel.getGameHeight() - radius))
				ready = true;
		}
		if ((posX < radius) && (dX < 0))
			dX = -dX;
		if ((posY < radius) && (dY < 0))
			dY = -dY;
		if ((posX > DrawPanel.getGameWidth() - radius) && (dX > 0))
			dX = -dX;
		if ((posY > DrawPanel.getGameHeight() - radius) && (dY > 0))
			dY = -dY;
	}

	public void render(Graphics2D graphics) {
		graphics.setStroke(new BasicStroke(3));
		graphics.setColor(normalColor.darker());
		graphics.drawOval((int) (posX - radius), (int) (posY - radius), radius * 2, radius * 2);
		graphics.setStroke(new BasicStroke(1));
		// fill the object     //
		graphics.setColor(normalColor.brighter());
		graphics.fillOval((int) (posX - radius), (int) (posY - radius), radius * 2, radius * 2);
	}

	public void hit() {
		health--;
		if (health <= 0)
			dead = true;
	}

	public boolean isDead() {
		return dead;
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
