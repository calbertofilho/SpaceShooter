package br.studio.calbertofilho.game.managements.states;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import br.studio.calbertofilho.game.controllers.containers.DrawablePanel;
import br.studio.calbertofilho.game.controllers.handlers.Keyboard;
import br.studio.calbertofilho.game.controllers.handlers.Mouse;
import br.studio.calbertofilho.game.managements.StatesManager;
import br.studio.calbertofilho.game.objects.Bullet;
import br.studio.calbertofilho.game.objects.Enemy;
import br.studio.calbertofilho.game.objects.Player;
import br.studio.calbertofilho.game.objects.PowerUp;

public class PlayState extends States {

	private boolean waveStart;
	private static Player player;
	private int playerX, playerY, playerRadius;
	private Bullet bullet;
	private static ArrayList<Bullet> bullets;
	private double bulletX, bulletY, bulletRadius;
	private Enemy enemy;
	private static ArrayList<Enemy> enemies;
	private double enemyX, enemyY, enemyRadius;
	private PowerUp powerUp;
	private static ArrayList<PowerUp> powerUps;
	private int powerUpType;
	private double powerUpX, powerUpY, powerUpLength;
	private double dX, dY, distance, random;
	private long waveStartTimer, waveStartTimerDiff;
	private int waveNumber, waveDelay, textLength, alphaFontColor;
	private Font font;
	private String text;

	public PlayState(StatesManager manager) {
		super(manager);
		player = new Player();
		bullets = new ArrayList<Bullet>();
		enemies = new ArrayList<Enemy>();
		powerUps = new ArrayList<PowerUp>();
		waveStart = true;
		waveStartTimer = waveStartTimerDiff = waveNumber = 0;
		waveDelay = 2000;
		loadResources();
	}

	private void loadResources() {
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new File("resources\\assets\\fonts\\Audiowide-Regular.ttf"));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void input(Mouse mouse, Keyboard keyboard) {
		player.input(keyboard, mouse);
	}

	@Override
	public void update() {
		// new wave                  //
		if ((waveStartTimer == 0) && (enemies.size() == 0)) {
			waveNumber++;
			waveStart = false;
			waveStartTimer = System.nanoTime();
		} else {
			waveStartTimerDiff = (System.nanoTime() - waveStartTimer) / 1000000;
			if (waveStartTimerDiff > waveDelay) {
				waveStart = true;
				waveStartTimer = waveStartTimerDiff = 0;
			}
		}
		// create enemies            //
		if (waveStart && (enemies.size() == 0))
			createNewEnemies();
		// player                    //
		player.update();
		// bullets                   //
		for (int i = 0; i < bullets.size(); i++) {
			bullet = bullets.get(i);
			bullet.update();
			if (bullet.isVisible())
				bullets.remove(bullet);
		}
		// enemies                   //
		for (Enemy enemy : enemies) {
			enemy.update();
		}
		// powerUps                  //
		for (int i = 0; i < powerUps.size(); i++) {
			powerUp = powerUps.get(i);
			powerUp.update();
			if (powerUp.isVisible())
				powerUps.remove(powerUp);
		}
		// collisions bullet-enemies //
		for (int i = 0; i < bullets.size(); i++) {
			bullet = bullets.get(i);
			bulletX = bullet.getX();
			bulletY = bullet.getY();
			bulletRadius = bullet.getRadius();
			for (int j = 0; j < enemies.size(); j++) {
				enemy = enemies.get(j);
				enemyX = enemy.getX();
				enemyY = enemy.getY();
				enemyRadius = enemy.getRadius();
				dX = bulletX - enemyX;
				dY = bulletY - enemyY;
				distance = Math.sqrt(dX * dX + dY * dY);
				if (distance < bulletRadius + enemyRadius) {
					enemy.hit();
					bullets.remove(i);
					i--;
					break;
				}
			}
		}
		// check dead enemies        //
		for (int i = 0; i < enemies.size(); i++) {
			enemy = enemies.get(i);
			if (enemy.isDead()) {
				// chance for powerUp
				random = Math.random();
				if (random < 0.001)
					powerUps.add(new PowerUp(1, enemy.getX(), enemy.getY()));
				else if (random < 0.020)
					powerUps.add(new PowerUp(3, enemy.getX(), enemy.getY()));
				else if (random < 0.120)
					powerUps.add(new PowerUp(2, enemy.getX(), enemy.getY()));
				// player score
				player.addScore(enemy.getType() + enemy.getRank());
				// enemy explode
				enemy.explode();
				// remove dead enemy
				enemies.remove(enemy);
				i--;
			}
		}
		// collisions player-enemy   //
		if (!player.isRecovering()) {
			playerX = player.getPosX();
			playerY = player.getPosY();
			playerRadius = player.getRadius();
			for (int i = 0; i < enemies.size(); i++) {
				enemy = enemies.get(i);
				enemyX = enemy.getX();
				enemyY = enemy.getY();
				enemyRadius = enemy.getRadius();
				dX = playerX - enemyX;
				dY = playerY - enemyY;
				distance = Math.sqrt(dX * dX + dY * dY);
				if (distance < playerRadius + enemyRadius) {
					player.loseLife();
				}
			}
		}
		// collision player-powerUp  //
		playerX = player.getPosX();
		playerY = player.getPosY();
		playerRadius = player.getRadius();
		for (int i = 0; i < powerUps.size(); i++) {
			powerUp = powerUps.get(i);
			powerUpX = powerUp.getPosX();
			powerUpY = powerUp.getPosY();
			powerUpLength = powerUp.getLength();
			dX = playerX - powerUpX;
			dY = playerY - powerUpY;
			distance = Math.sqrt(dX * dX + dY * dY);
			// collected powerUp
			if (distance < playerRadius + powerUpLength) {
				powerUpType = powerUp.getType();
				if (powerUpType == 1)
					player.gainsLife();
				if (powerUpType == 2)
					player.increasePower(1);
				if (powerUpType == 3)
					player.increasePower(2);
				powerUps.remove(powerUp);
				i--;
			}
		}
	}

	@Override
	public void render(Graphics2D graphics) {
		// draw player               //
		player.render(graphics);
		// draw bullets              //
		for (int i = 0; i < bullets.size(); i++) {
			bullet = bullets.get(i);
			bullet.render(graphics);
		}
		// draw enemies              //
		for (int i = 0; i < enemies.size(); i++) {
			enemy = enemies.get(i);
			enemy.render(graphics);
		}
		// draw bullets              //
		for (int i = 0; i < powerUps.size(); i++) {
			powerUp = powerUps.get(i);
			powerUp.render(graphics);
		}
		// set font and color to draw wave number
		graphics.setFont(font.deriveFont(Font.PLAIN, 24));
		alphaFontColor = (int) (255 * Math.sin(Math.PI * waveStartTimerDiff / waveDelay));
		if (alphaFontColor > 255)
			alphaFontColor = 255;
		graphics.setColor(new Color(255, 255, 255, alphaFontColor));
		// draw wave number          //
		text = "---     W A V E   " + waveNumber + "     ---";
		textLength = (int) graphics.getFontMetrics().getStringBounds(text, graphics).getWidth();
		graphics.drawString(text, DrawablePanel.getGameWidth() / 2 - textLength / 2, DrawablePanel.getGameHeight() / 2);
		// set font and color to draw others text in the game
		graphics.setFont(font.deriveFont(Font.PLAIN, 14));
		graphics.setColor(Color.GREEN);
		// show player lives         //
		text = "Vidas";
		graphics.drawString(text, 10, graphics.getFontMetrics().getHeight());
		graphics.drawRect(10, 22, 20 * player.getLives(), 28);
		int[] x = {-player.getRadius() / 2, 0, player.getRadius() / 2};
		int[] y = {40 + player.getRadius() / 2, 40 - (player.getRadius() / 2) * 2, 40 + player.getRadius() / 2};
		for (int i = 0; i < player.getLives(); i++) {
			x[0] += 20;
			x[1] += 20;
			x[2] += 20;
			// draw a contour line   //
			graphics.setStroke(new BasicStroke(3));
			graphics.setColor(player.getColor().darker());
			graphics.drawPolygon(x, y, x.length);
			graphics.setStroke(new BasicStroke(1));
			// fill the object       //
			graphics.setColor(player.getColor());
			graphics.fillPolygon(x, y, x.length);
		}
		// show player power         //
		graphics.setColor(Color.ORANGE.darker());
		graphics.setStroke(new BasicStroke(2));
		for (int i = 0; i < player.getRequiredPower(); i++) {
			graphics.drawRect(10 + 8 * i, 60, 8, 8);
		}
		graphics.setStroke(new BasicStroke(1));
		graphics.setColor(Color.ORANGE);
		graphics.fillRect(10, 60, player.getPower() * 8, 8);
		// show player score         //
		graphics.setColor(Color.WHITE);
		text = "Pontua��o: " + player.getScore();
		graphics.drawString(text, (DrawablePanel.getGameWidth() - graphics.getFontMetrics().stringWidth(text)) - 5, graphics.getFontMetrics().getHeight());
		// show bullets counter      //
		text = "Disparos: " + bullets.size();
		graphics.drawString(text, (DrawablePanel.getGameWidth() - graphics.getFontMetrics().stringWidth(text)) - 5, 2 * graphics.getFontMetrics().getHeight());
		// show enemies counter      //
		text = "Inimigos: " + enemies.size();
		graphics.drawString(text, (DrawablePanel.getGameWidth() - graphics.getFontMetrics().stringWidth(text)) - 5, 3 * graphics.getFontMetrics().getHeight());
		// show FPS counter          //
		graphics.setColor(Color.YELLOW);
		text = String.format("FPS: %.2f", DrawablePanel.getGameFPS());
		graphics.drawString(text, 5, DrawablePanel.getGameHeight() - 5);
	}

	private void createNewEnemies() {
		enemies.clear();
		if (waveNumber == 1) {
			for (int i = 0; i < 4; i++) {
				enemies.add(new Enemy(1, 1));
			}
		}
		if (waveNumber == 2) {
			for (int i = 0; i < 4; i++) {
				enemies.add(new Enemy(1, 1));
			}
			enemies.add(new Enemy(1, 2));
			enemies.add(new Enemy(1, 2));
		}
		if (waveNumber == 3) {
			enemies.add(new Enemy(1, 3));
			enemies.add(new Enemy(1, 3));
			enemies.add(new Enemy(1, 4));
		}
	}

	public static Player getPlayer() {
		return player;
	}

	public static void addBullet(Bullet bullet) {
		bullets.add(bullet);
	}

	public static void addEnemy(Enemy enemy) {
		enemies.add(enemy);
	}

}
