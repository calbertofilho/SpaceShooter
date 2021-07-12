package br.studio.calbertofilho.game.objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import br.studio.calbertofilho.game.controllers.containers.DrawPanel;
import br.studio.calbertofilho.game.controllers.handlers.Keyboard;
import br.studio.calbertofilho.game.controllers.handlers.Mouse;

public class Player {

	private int[] x, y;
	private int posX, posY, radius;
	private int dX, dY;
	private int speed, lives;
	private boolean left, right, up, down, attack;
	private long attackingTime, elapsedTime, attackingDelay;
	private Color normalColor, hitColor;

	public Player() {
		x = new int[3];
		y = new int[3];
		posX = DrawPanel.getGameWidth() / 2;
		posY = DrawPanel.getGameHeight() / 2;
		radius = 10;
		dX = 0;
		dY = 0;
		speed = 5;
		lives = 3;
		left = right = up = down = attack = false;
		attackingTime = System.nanoTime();
		attackingDelay = 200;
		normalColor = Color.WHITE;
		hitColor = Color.RED;
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
		if (posX > DrawPanel.getGameWidth() - radius)
			posX = DrawPanel.getGameWidth() - radius;
		if (posY > DrawPanel.getGameHeight() - radius)
			posY = DrawPanel.getGameHeight() - radius;
	// reseting variables   //
		dX = 0;
		dY = 0;
	// attacking            //
		if (attack) {
			elapsedTime = (System.nanoTime() - attackingTime) / 1000000;
			if (elapsedTime > attackingDelay) {
				DrawPanel.addBullet(new Bullet(270, posX, posY - radius * 2));
				attackingTime = System.nanoTime();
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
		// draw a contour line //
		graphics.setStroke(new BasicStroke(3));
		graphics.setColor(normalColor.darker());
		graphics.drawPolygon(x, y, x.length);
		graphics.setStroke(new BasicStroke(1));
		// fill the object     //
		graphics.setColor(normalColor);
		graphics.fillPolygon(x, y, x.length);
	}

	public void input(Keyboard keyboard, Mouse mouse) {
		left = keyboard.leftKey.isDown();
		right = keyboard.rightKey.isDown();
		up = keyboard.upKey.isDown();
		down = keyboard.downKey.isDown();
		attack = keyboard.attackKey.isDown();
	}

}
