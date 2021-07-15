package br.studio.calbertofilho.game.objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import br.studio.calbertofilho.game.controllers.containers.DrawablePanel;
import br.studio.calbertofilho.game.controllers.handlers.Keyboard;
import br.studio.calbertofilho.game.controllers.handlers.Mouse;
import br.studio.calbertofilho.game.managements.states.PlayState;

public class Player {

	private int[] x, y, requiredPower;
	private int posX, posY, radius;
	private int dX, dY;
	private int speed, lives, score, power, powerLevel;
	private boolean left, right, up, down, attack, recovering, invincibility;
	private long attackingDelay, recoveryDelay, invincibilityDelay, attackingTimer, recoveryTimer, invincibilityTimer;
	private long elapsedTime, elapsedInvincibilityTime, elapsedRecoveringTime;
	private Color normalColor, hitColor, invincibilityColor;

	public Player() {
		x = new int[3];
		y = new int[3];
		posX = DrawablePanel.getGameWidth() / 2;
		posY = DrawablePanel.getGameHeight() - 100;
		radius = 10;
		dX = 0;
		dY = 0;
		speed = 5;
		lives = 3;
		score = 0;
		normalColor = Color.WHITE;
		hitColor = new Color(255, 139, 142);
		invincibilityColor = Color.BLUE;
		left = right = up = down = attack = recovering = invincibility = false;
		attackingTimer = System.nanoTime();
		attackingDelay = 400;
		recoveryDelay = 3000;
		invincibilityDelay = 15000;
		recoveryTimer  = 0;
		requiredPower = new int[] {1, 2, 3, 4, 5};
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public int getRadius() {
		return radius;
	}

	public int getLives() {
		return lives;
	}

	public boolean isDead() {
		return lives <= 0;
	}

	public boolean isRecovering() {
		return recovering;
	}

	public int getScore() {
		return score;
	}

	public void addScore(int score) {
		this.score += score;
	}

	public Color getColor() {
		return normalColor;
	}

	public void gainsLife() {
		lives++;
	}

	public void loseLife() {
		lives--;
		recovering = true;
		recoveryTimer = System.nanoTime();
	}

	public int getInvincibilityDelay() {
		return (int) invincibilityDelay;
	}

	public void increasePower(int power) {
		if (this.power < 5)
			this.power += power;
		if (powerLevel == 4) {
			if (power > requiredPower[powerLevel]) {
				power = requiredPower[powerLevel];
			}
			return;
		}
		if (this.power >= requiredPower[powerLevel]) {
			this.power -= requiredPower[powerLevel];
			powerLevel++;
		}
	}

	public int getPowerLevel() {
		return powerLevel;
	}

	public int getSpeed() {
		return speed;
	}

	public int getPower() {
		return power;
	}

	public int getRequiredPower() {
		return requiredPower[powerLevel];
	}

	public void setAttackingVelocity(long attackingDelay) {
		if (this.attackingDelay > 100)
			this.attackingDelay -= attackingDelay;
	}

	public void setInvincibility(boolean invincibility) {
		this.invincibility = invincibility;
	}

	public boolean isInvincible() {
		return invincibility;
	}

	public void update() {
	// movement and direction //
		if (left)
			dX = -speed;
		if (right)
			dX = speed;
		if (up)
			dY = -speed;
		if (down)
			dY = speed;
		posX += dX;
		posY += dY;
	// positioning on screen //
		if (posX < radius)
			posX = radius;
		if (posY < radius)
			posY = radius;
		if (posX > DrawablePanel.getGameWidth() - radius)
			posX = DrawablePanel.getGameWidth() - radius;
		if (posY > DrawablePanel.getGameHeight() - radius)
			posY = DrawablePanel.getGameHeight() - radius;
	// reseting variables   //
		dX = 0;
		dY = 0;
	// attacking            //
		if (attack) {
			elapsedTime = (System.nanoTime() - attackingTimer) / 1000000;
			if (elapsedTime > attackingDelay) {
				attackingTimer = System.nanoTime();
				if (powerLevel == 0) {
					PlayState.addBullet(new Bullet(270, posX, posY - radius * 2));
				}
				else if (powerLevel == 1) {
					PlayState.addBullet(new Bullet(270, posX + 5, posY - radius * 2));
					PlayState.addBullet(new Bullet(270, posX - 5, posY - radius * 2));
				} else if (powerLevel == 2) {
					PlayState.addBullet(new Bullet(275, posX + 5, posY - radius * 2));
					PlayState.addBullet(new Bullet(270, posX, posY - radius * 2));
					PlayState.addBullet(new Bullet(265, posX - 5, posY - radius * 2));
				} else if (powerLevel == 3) {
					PlayState.addBullet(new Bullet(276, posX + 5, posY - radius * 2));
					PlayState.addBullet(new Bullet(271, posX + 5, posY - radius * 2));
					PlayState.addBullet(new Bullet(269, posX - 5, posY - radius * 2));
					PlayState.addBullet(new Bullet(264, posX - 5, posY - radius * 2));
				} else {
					PlayState.addBullet(new Bullet(278, posX + 5, posY - radius * 2));
					PlayState.addBullet(new Bullet(274, posX + 5, posY - radius * 2));
					PlayState.addBullet(new Bullet(270, posX, posY - radius * 2));
					PlayState.addBullet(new Bullet(266, posX - 5, posY - radius * 2));
					PlayState.addBullet(new Bullet(262, posX - 5, posY - radius * 2));
				}
			}
		}
	// invincible after hit //
		if (recovering) {
			elapsedRecoveringTime = (System.nanoTime() - recoveryTimer) / 1000000;
			if (elapsedRecoveringTime > recoveryDelay) {
				recovering = false;
				recoveryTimer = 0;
			}
		}
	// invincibility        //
		if (invincibility) {
			elapsedInvincibilityTime = (System.nanoTime() - invincibilityTimer) / 1000000;
			if (elapsedInvincibilityTime > invincibilityDelay) {
				invincibilityTimer = 0;
			}
		}
	}

	public void render(Graphics2D graphics) {
		// set position        //
		x[0] = posX - radius;
		x[1] = posX;
		x[2] = posX + radius;
		y[0] = posY + radius;
		y[1] = posY - radius * 2;
		y[2] = posY + radius;
		if (recovering) {
			// draw a contour line //
			graphics.setStroke(new BasicStroke(3));
			graphics.setColor(hitColor.darker());
			graphics.drawPolygon(x, y, x.length);
			graphics.setStroke(new BasicStroke(1));
			// fill the object     //
			graphics.setColor(hitColor);
			graphics.fillPolygon(x, y, x.length);
		} else if (invincibility) {
			// draw a contour line //
			graphics.setStroke(new BasicStroke(3));
			graphics.setColor(normalColor.darker());
			graphics.drawPolygon(x, y, x.length);
			graphics.setColor(invincibilityColor.darker());
			graphics.drawOval((int) posX - radius * 3, (int) posY - radius * 3, radius * 6, radius * 6);
			graphics.setStroke(new BasicStroke(1));
			// fill the object     //
			graphics.setColor(normalColor);
			graphics.fillPolygon(x, y, x.length);
			graphics.setColor(new Color(invincibilityColor.getRed(), invincibilityColor.getGreen(), invincibilityColor.getBlue(), 128));
			graphics.fillOval((int) posX - radius * 3, (int) posY - radius * 3, radius * 6, radius * 6);
		} else {
			// draw a contour line //
			graphics.setStroke(new BasicStroke(3));
			graphics.setColor(normalColor.darker());
			graphics.drawPolygon(x, y, x.length);
			graphics.setStroke(new BasicStroke(1));
			// fill the object     //
			graphics.setColor(normalColor);
			graphics.fillPolygon(x, y, x.length);
		}
	}

	public void input(Keyboard keyboard, Mouse mouse) {
		left = keyboard.leftKey.isDown();
		right = keyboard.rightKey.isDown();
		up = keyboard.upKey.isDown();
		down = keyboard.downKey.isDown();
		attack = keyboard.attackKey.isDown();
	}

}
