package br.studio.calbertofilho.game.objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import br.studio.calbertofilho.game.controllers.containers.DrawPanel;

public class Enemy {

	private double posX, posY, dX, dY, angle, radians, speed;
	private int radius, health, type, rank, amount;
	private Color normalColor, hitColor;
	private boolean ready, dead, hit;
	private long hitTimer, elapsedTime;
	private Enemy enemy;

	public Enemy(int type, int rank) {
		this.type = type;
		this.rank = rank;
		// default enemy
		if (type == 1) {
			if (rank == 1) {
				normalColor = new Color(133, 225, 75);
				hitColor = new Color(255, 120, 82);
				speed = 2;
				radius = 4;
				health = 1;
			}
			if (rank == 2) {
				normalColor = new Color(102, 174, 58);
				hitColor = new Color(204, 95, 65);
				speed = 2;
				radius = 6;
				health = 2;
			}
			if (rank == 3) {
				normalColor = new Color(72, 123, 40);
				hitColor = new Color(153, 71, 48);
				speed = 1.5;
				radius = 8;
				health = 3;
			}
			if (rank == 4) {
				normalColor = new Color(42, 72, 23);
				hitColor = new Color(101, 47 ,32);
				speed = 1.5;
				radius = 10;
				health = 4;
			}
		}
		// stronger, faster default
		if (type == 2) {
			if (rank == 1) {
				speed = 3;
				radius = 5;
				health = 2;
			}
		}
		// slower, but hard to kill
		if (type == 3) {
			if (rank == 1) {
				speed = 1.5;
				radius = 5;
				health = 5;
			}
		}
		posX = Math.random() * DrawPanel.getGameWidth() / 2 + DrawPanel.getGameHeight() / 4;
		posY = -radius;
		angle = Math.random() * 140 + 20;
		radians = Math.toRadians(angle);
		dX = Math.cos(radians) * speed;
		dY = Math.sin(radians) * speed;
		ready = dead = hit = false;
		hitTimer = 0;
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
		if (hit) {
			elapsedTime = (System.nanoTime() - hitTimer) / 1000000;
			if (elapsedTime > 50) {
				hit = false;
				hitTimer = 0;
			}
		}
	}

	public void render(Graphics2D graphics) {
		if (hit) {
			// draw a contour line //
			graphics.setStroke(new BasicStroke(3));
			graphics.setColor(hitColor.darker());
			graphics.drawOval((int) (posX - radius), (int) (posY - radius), radius * 2, radius * 2);
			graphics.setStroke(new BasicStroke(1));
			// fill the object     //
			graphics.setColor(hitColor);
			graphics.fillOval((int) (posX - radius), (int) (posY - radius), radius * 2, radius * 2);
		} else {
			// draw a contour line //
			graphics.setStroke(new BasicStroke(3));
			graphics.setColor(normalColor.darker());
			graphics.drawOval((int) (posX - radius), (int) (posY - radius), radius * 2, radius * 2);
			graphics.setStroke(new BasicStroke(1));
			// fill the object     //
			graphics.setColor(normalColor);
			graphics.fillOval((int) (posX - radius), (int) (posY - radius), radius * 2, radius * 2);
		}
	}

	public void hit() {
		health--;
		if (health <= 0)
			dead = true;
		hit = true;
		hitTimer = System.nanoTime();
	}

	public void explode() {
		if (rank > 1) {
			amount = 0;
			if (type == 1)
				amount = 3;
			for (int i = 0; i < amount; i++) {
				enemy = new Enemy(getType(), getRank() - 1);
				enemy.posX = this.getX();
				enemy.posY = this.getY();
				angle = 0;
				if (!ready)
					angle = Math.random() * 140 + 20;
				else
					angle = Math.random() * 360;
				enemy.radians = Math.toRadians(angle);
				DrawPanel.addEnemy(enemy);
			}
		}
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

	public int getType() {
		return type;
	}

	public int getRank() {
		return rank;
	}

}
