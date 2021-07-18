package br.studio.calbertofilho.game.objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import br.studio.calbertofilho.game.controllers.containers.DrawablePanel;
import br.studio.calbertofilho.game.managements.states.PlayState;

public class Enemy {

	private double posX, posY, dX, dY, angle, radians, speed, reboundSpeed, deacc;
	private int radius, health, type, rank, amount;
	private Color normalColor, hitColor;
	private boolean ready, dead, hit, slow;
	private long hitTimer, elapsedTime, reboundTimer, reboundElapsedTime;;
	private Enemy enemy;

	public Enemy(int type, int rank) {
		this.type = type;
		this.rank = rank;
		radius = 3 + (type * rank);
		health = type * rank;
		reboundSpeed = 0;
		deacc = 0.3f;
		// default enemy
		if (type == 1) {
			if (rank == 1) {
				normalColor = new Color(34, 207, 190);
				hitColor = new Color(20, 149, 76);
				speed = 2;
			}
			if (rank == 2) {
				normalColor = new Color(28, 169, 155);
				hitColor = new Color(15, 111, 57);
				speed = 2;
			}
			if (rank == 3) {
				normalColor = new Color(19, 117, 108);
				hitColor = new Color(8, 60, 30);
				speed = 1.5;
			}
			if (rank == 4) {
				normalColor = new Color(15, 92, 84);
				hitColor = new Color(7, 34, 17);
				speed = 1.5;
			}
		}
		// stronger, faster default
		if (type == 2) {
			if (rank == 1) {
				normalColor = new Color(255, 165, 52);
				hitColor = new Color(226, 91, 42);
				speed = 3;
			}
			if (rank == 2) {
				normalColor = new Color(245, 146, 50);
				hitColor = new Color(188, 76, 35);
				speed = 3;
			}
			if (rank == 3) {
				normalColor = new Color(194, 115, 39);
				hitColor = new Color(137, 55, 25);
				speed = 2.5;
			}
			if (rank == 4) {
				normalColor = new Color(168, 100, 34);
				hitColor = new Color(111, 45, 20);
				speed = 2.5;
			}
		}
		// slower, but hard to kill
		if (type == 3) {
			if (rank == 1) {
				normalColor = new Color(183, 45, 179); 
				hitColor = new Color(89, 38, 130);
				speed = 1.5;
			}
			if (rank == 2) {
				normalColor = new Color(145, 36, 142); 
				hitColor = new Color(63, 27, 92);
				speed = 1.5;
			}
			if (rank == 3) {
				normalColor = new Color(93, 23, 92);
				hitColor = new Color(28, 12, 40);
				speed = 1;
			}
			if (rank == 4) {
				normalColor = new Color(68, 17, 67); 
				hitColor = new Color(10, 4, 15);
				speed = 1;
			}
		}
		posX = Math.random() * DrawablePanel.getGameWidth() / 2 + DrawablePanel.getGameHeight() / 4;
		posY = -radius;
		angle = Math.random() * 140 + 20;
		radians = Math.toRadians(angle);
		dX = Math.cos(radians) * speed;
		dY = Math.sin(radians) * speed;
		ready = dead = hit = slow = false;
		hitTimer = 0;
	}

	public void update() {
		if (slow) {
			posX += dX * 0.3;
			posY += dY * 0.3;
		} else {
			// identificar o sinal de dX e dYçççcclll
			posX += dX + reboundSpeed;
			posY += dY + reboundSpeed;
			reboundElapsedTime = (System.nanoTime() - reboundTimer) / 1000000;
			if (reboundElapsedTime > 50) {
				reboundSpeed -= deacc;
				if (reboundSpeed < 0)
					reboundSpeed = 0;
			}
		}
		if (!ready) {
			if ((posX > radius) && (posY > radius) && (posX < DrawablePanel.getGameWidth() - radius) && (posY < DrawablePanel.getGameHeight() - radius))
				ready = true;
		}
		if ((posX < radius) && (dX < 0))
			dX = -dX;
		if ((posY < radius) && (dY < 0))
			dY = -dY;
		if ((posX > DrawablePanel.getGameWidth() - radius) && (dX > 0))
			dX = -dX;
		if ((posY > DrawablePanel.getGameHeight() - radius) && (dY > 0))
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
			amount = getType() * getRank();
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
				enemy.setSlow(this.slow);
				PlayState.addEnemy(enemy);
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

	public int getRadius() {
		return radius;
	}

	public int getType() {
		return type;
	}

	public int getRank() {
		return rank;
	}

	public void setSlow(boolean slow) {
		this.slow = slow;
	}

	public void rebounds() {
		reboundTimer = System.nanoTime();
		reboundSpeed = 5;
		dX = -dX;
		dY = -dY;
	}

}
