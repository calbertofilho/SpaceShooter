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
import br.studio.calbertofilho.game.objects.Explosion;
import br.studio.calbertofilho.game.objects.Player;
import br.studio.calbertofilho.game.objects.PowerUp;
import br.studio.calbertofilho.game.texts.Notification;

public class PlayState extends States {

	private static Player player;
	private int playerX, playerY, playerRadius, positionSlowDownPowerUp, positionInvicibilityPowerUp;
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
	private Explosion explosion;
	private static ArrayList<Explosion> explosions;
	private Notification notification;
	private ArrayList<Notification> notifications;
	private boolean waveStart;
	private long waveStartTimer, waveStartTimerDiff, slowDownTimer, slowDownTimerDiff, invincibilityTimer, invincibilityTimerDiff;
	private int waveNumber, waveDelay, textLength, alphaFontColor, slowDownDelay, invincibilityDelay;
	private Font textFont, scoreFont;
	private String text, scores;

	public PlayState(StatesManager manager) {
		super(manager);
		player = new Player();
		bullets = new ArrayList<Bullet>();
		enemies = new ArrayList<Enemy>();
		powerUps = new ArrayList<PowerUp>();
		explosions = new ArrayList<Explosion>();
		notifications = new ArrayList<Notification>();
		waveStart = true;
		waveStartTimer = waveStartTimerDiff = waveNumber = 0;
		waveDelay = 2000;
		loadResources();
	}

	private void loadResources() {
		try {
			textFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources\\assets\\fonts\\Audiowide-Regular.ttf"));
			scoreFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources\\assets\\fonts\\04B_19__.TTF"));
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
			clearScenery();
		} else {
			waveStartTimerDiff = (System.nanoTime() - waveStartTimer) / 1000000;
			if (waveStartTimerDiff > waveDelay) {
				waveStart = true;
				waveStartTimer = waveStartTimerDiff = 0;
				if (waveNumber >= 11)
					congratulations();
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
			if (!bullet.isVisible())
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
			if (!powerUp.isVisible())
				powerUps.remove(powerUp);
		}
		// explosions                //
		for (int i = 0; i < explosions.size(); i++) {
			explosion = explosions.get(i);
			explosion.update();
			if (!explosion.isVisible())
				explosions.remove(explosion);
		}
		// notifications             //
		for (int i = 0; i < notifications.size(); i++) {
			notification = notifications.get(i);
			notification.update();
			if (!notification.isVisible())
				notifications.remove(notification);
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
					powerUps.add(new PowerUp(PowerUp.EXTRALIFE, enemy.getX(), enemy.getY()));
				else if (random < 0.010)
					powerUps.add(new PowerUp(PowerUp.DOUBLEPOWER, enemy.getX(), enemy.getY()));
				else if (random < 0.020)
					powerUps.add(new PowerUp(PowerUp.INVINCIBILITY, enemy.getX(), enemy.getY()));
				else if (random < 0.100)
					powerUps.add(new PowerUp(PowerUp.POWER, enemy.getX(), enemy.getY()));
				else if (random < 0.101)
					powerUps.add(new PowerUp(PowerUp.SLOWDOWN, enemy.getX(), enemy.getY()));
				// player score
				player.addScore(enemy.getType() + enemy.getRank());
				// enemy explode
				enemy.explode();
				explosions.add(new Explosion(enemy.getX(), enemy.getY(), enemy.getRadius(), enemy.getRadius() + 30));
				// remove dead enemy
				enemies.remove(enemy);
				i--;
			}
		}
		// check dead player         //
		if (player.isDead())
			gameOver();
		// collisions player-enemy   //
		if ((!player.isRecovering()) && (!player.isInvincible())) {
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
				if (powerUpType == PowerUp.EXTRALIFE) {
					player.gainsLife();
					notifications.add(new Notification(powerUp.getPosX(), powerUp.getPosY(), 1000, "Vida Extra"));
					player.addScore(100);
				}
				if (powerUpType == PowerUp.POWER) {
					player.increasePower(1);
					player.setAttackingVelocity(10);
					notifications.add(new Notification(powerUp.getPosX(), powerUp.getPosY(), 1000, "Power Up"));
					player.addScore(10);
				}
				if (powerUpType == PowerUp.DOUBLEPOWER) {
					player.increasePower(2);
					player.setAttackingVelocity(20);
					notifications.add(new Notification(powerUp.getPosX(), powerUp.getPosY(), 1000, "Double Power Up"));
					player.addScore(20);
				}
				if (powerUpType == PowerUp.SLOWDOWN) {
					slowDownDelay = 10000;
					slowDownTimer = System.nanoTime();
					for (int j = 0; j < enemies.size(); j++) {
						enemy = enemies.get(j);
						enemy.setSlow(true);
					}
					notifications.add(new Notification(powerUp.getPosX(), powerUp.getPosY(), 1000, "Slow Down"));
					player.addScore(5);
				}
				if (powerUpType == PowerUp.INVINCIBILITY) {
					invincibilityDelay = player.getInvincibilityDelay();
					invincibilityTimer = System.nanoTime();
					player.setInvincibility(true);
					notifications.add(new Notification(powerUp.getPosX(), powerUp.getPosY(), 1000, "Invencibilidade"));
					player.addScore(50);
				}
				powerUps.remove(powerUp);
				i--;
			}
		}
		// slowDown                  //
		if (slowDownTimer != 0) {
			slowDownTimerDiff = (System.nanoTime() - slowDownTimer) / 1000000;
			if (slowDownTimerDiff > slowDownDelay) {
				slowDownTimer = 0;
				for (int j = 0; j < enemies.size(); j++) {
					enemy = enemies.get(j);
					enemy.setSlow(false);
				}
			}
		}
		// invincibility             //
		if (invincibilityTimer != 0) {
			invincibilityTimerDiff = (System.nanoTime() - invincibilityTimer) / 1000000;
			if (invincibilityTimerDiff > invincibilityDelay) {
				invincibilityTimer = 0;
				player.setInvincibility(false);
			}
		}
		// collisions playerInvincibility-enemies   //
		if (player.isInvincible()) {
			playerX = player.getPosX();
			playerY = player.getPosY();
			playerRadius = player.getRadius() * 3;
			for (int i = 0; i < enemies.size(); i++) {
				enemy = enemies.get(i);
				enemyX = enemy.getX();
				enemyY = enemy.getY();
				enemyRadius = enemy.getRadius();
				dX = playerX - enemyX;
				dY = playerY - enemyY;
				distance = Math.sqrt(dX * dX + dY * dY);
				if (distance < playerRadius + enemyRadius) {
					enemy.rebounds();
				}
			}
		}

	}

	@Override
	public void render(Graphics2D graphics) {
	// draw the background color //
		if (slowDownTimer != 0)
			graphics.setColor(new Color(79, 155, 217));
		else
			graphics.setColor(new Color(55, 108, 151));
		graphics.fillRect(0, 0, DrawablePanel.getGameWidth(), DrawablePanel.getGameHeight());
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
	// draw powerUps             //
		for (int i = 0; i < powerUps.size(); i++) {
			powerUp = powerUps.get(i);
			powerUp.render(graphics);
		}
	// draw enemies explosions   //
		for (int i = 0; i < explosions.size(); i++) {
			explosion = explosions.get(i);
			explosion.render(graphics);
		}
	// draw notifications        //
		for (int i = 0; i < notifications.size(); i++) {
			notification = notifications.get(i);
			notification.render(graphics);
		}
	// draw wave number          //
		// set font and color to draw wave number
		graphics.setFont(textFont.deriveFont(Font.BOLD, 24));
		alphaFontColor = (int) (255 * Math.sin(Math.PI * waveStartTimerDiff / waveDelay));
		alphaFontColor = (alphaFontColor > 255) ? 255 : alphaFontColor;
		alphaFontColor = (alphaFontColor < 0) ? 0 : alphaFontColor;
		graphics.setColor(new Color(255, 255, 255, alphaFontColor));
		text = (waveNumber == 11) ?	"---     P A R A B É N S   ! ! !     ---" :	"---     W A V E   " + waveNumber + "     ---";
		textLength = (int) graphics.getFontMetrics().getStringBounds(text, graphics).getWidth();
		graphics.drawString(text, DrawablePanel.getGameWidth() / 2 - textLength / 2, DrawablePanel.getGameHeight() / 2);
	// show player lives         //
		graphics.setColor(Color.GREEN.darker());
		graphics.fillRect(10, 24, 15 * player.getLives(), 18);
		graphics.setColor(new Color(0, 78, 56));
		graphics.setStroke(new BasicStroke(2));
		graphics.drawRect(10, 24, 15 * player.getLives(), 18);
		graphics.setStroke(new BasicStroke(1));
		graphics.setColor(Color.GREEN.darker());
		graphics.setFont(textFont.deriveFont(Font.PLAIN, 14));
		text = "Vidas";
		graphics.drawString(text, 10, 2 + graphics.getFontMetrics().getHeight());
		int[] x = {3 - player.getRadius() / 4, 3, 3 + player.getRadius() / 4};
		int[] y = {35 + player.getRadius() / 4, 35 - (player.getRadius() / 4) * 2, 35 + player.getRadius() / 4};
		for (int i = 0; i < player.getLives(); i++) {
			x[0] += 15;
			x[1] += 15;
			x[2] += 15;
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
		graphics.setColor(Color.ORANGE);
		text = "Power";
		graphics.drawString(text, 10, 65);
		graphics.fillRect(10, 70, player.getPower() * 10, 10);
		graphics.setColor(Color.ORANGE.darker());
		graphics.setStroke(new BasicStroke(2));
		for (int i = 0; i < player.getRequiredPower(); i++) {
			graphics.drawRect(10 + 10 * i, 70, 10, 10);
		}
		graphics.setStroke(new BasicStroke(1));
	// show enemy slowDown timer //
		if (slowDownTimer != 0) {
			positionSlowDownPowerUp = 95;
			graphics.setColor(Color.CYAN);
			text = "SlowDown";
			graphics.drawString(text, 10, positionSlowDownPowerUp);
			graphics.setColor(Color.CYAN);
			graphics.fillRect(10, positionSlowDownPowerUp + 5, (int) (100 - 100.0 * slowDownTimerDiff / slowDownDelay), 10);
			graphics.setColor(Color.CYAN.darker());
			graphics.setStroke(new BasicStroke(2));
			graphics.drawRect(10, positionSlowDownPowerUp + 5, 100, 10);
			graphics.setStroke(new BasicStroke(1));
		}
	// show invincibility timer  //
		if (invincibilityTimer != 0) {
			if (slowDownTimer != 0)
				positionInvicibilityPowerUp = 125;
			else
				positionInvicibilityPowerUp = 95;
			graphics.setColor(Color.MAGENTA);
			text = "Invencibilidade";
			graphics.drawString(text, 10, positionInvicibilityPowerUp);
			graphics.setColor(Color.MAGENTA);
			graphics.fillRect(10, positionInvicibilityPowerUp + 5, (int) (100 - 100.0 * invincibilityTimerDiff / invincibilityDelay), 10);
			graphics.setColor(Color.MAGENTA.darker());
			graphics.setStroke(new BasicStroke(2));
			graphics.drawRect(10, positionInvicibilityPowerUp + 5, 100, 10);
			graphics.setStroke(new BasicStroke(1));
		}
//	// show wave number          //
		graphics.setColor(Color.WHITE);
//		text = (waveNumber == 11) ?	"PARABÉNS !!!" : "--- WAVE " + waveNumber + " ---";
//		graphics.drawString(text, (DrawablePanel.getGameWidth() - graphics.getFontMetrics().stringWidth(text)) - 5, graphics.getFontMetrics().getHeight());
	// show player score         //
		scores = String.valueOf(player.getScore());
		graphics.setFont(scoreFont.deriveFont(Font.PLAIN, 24));
		graphics.drawString(scores, (DrawablePanel.getGameWidth() - graphics.getFontMetrics().stringWidth(scores)) - 5, graphics.getFontMetrics().getHeight());
	// show bullets counter      //
		graphics.setFont(textFont.deriveFont(Font.PLAIN, 14));
//		text = "Disparos: " + bullets.size();
//		graphics.drawString(text, (DrawablePanel.getGameWidth() - graphics.getFontMetrics().stringWidth(text)) - 5, 3 * graphics.getFontMetrics().getHeight());
//	// show enemies counter      //
//		text = "Inimigos: " + enemies.size();
//		graphics.drawString(text, (DrawablePanel.getGameWidth() - graphics.getFontMetrics().stringWidth(text)) - 5, 4 * graphics.getFontMetrics().getHeight());
	// show FPS counter          //
		graphics.setColor(Color.YELLOW);
		text = String.format("%.2f fps", DrawablePanel.getGameFPS());
		graphics.drawString(text, 5, DrawablePanel.getGameHeight() - 5);
	}

	private void createNewEnemies() {
		if (waveNumber == 1) {
			for (int i = 0; i < 6; i++) {
				enemies.add(new Enemy(1, 1));
			}
		}
		if (waveNumber == 2) {
			for (int i = 0; i < 4; i++) {
				enemies.add(new Enemy(1, 1));
				enemies.add(new Enemy(1, 2));
			}
		}
		if (waveNumber == 3) {
			for (int i = 0; i < 2; i++) {
				enemies.add(new Enemy(1, 1));
				enemies.add(new Enemy(1, 2));
			}
			enemies.add(new Enemy(1, 3));
			enemies.add(new Enemy(1, 3));
			enemies.add(new Enemy(1, 4));
		}
		if (waveNumber == 4) {
			for (int i = 0; i < 4; i++) {
				enemies.add(new Enemy(1, 4));
			}
			enemies.add(new Enemy(2, 1));
			enemies.add(new Enemy(2, 1));
		}
		if (waveNumber == 5) {
			enemies.add(new Enemy(1, 4));
			for (int i = 0; i < 4; i++) {
				enemies.add(new Enemy(2, 1));
				enemies.add(new Enemy(2, 2));
			}
		}
		if (waveNumber == 6) {
			enemies.add(new Enemy(1, 4));
			enemies.add(new Enemy(2, 1));
			enemies.add(new Enemy(2, 2));
			enemies.add(new Enemy(2, 3));
			enemies.add(new Enemy(2, 4));
		}
		if (waveNumber == 7) {
			enemies.add(new Enemy(1, 4));
			enemies.add(new Enemy(2, 4));
			enemies.add(new Enemy(3, 1));
			enemies.add(new Enemy(3, 1));
			enemies.add(new Enemy(3, 1));
		}
		if (waveNumber == 8) {
			enemies.add(new Enemy(1, 4));
			enemies.add(new Enemy(2, 4));
			for (int i = 0; i < 2; i++) {
				enemies.add(new Enemy(3, 2));
			}
		}
		if (waveNumber == 9) {
			for (int i = 0; i < 2; i++) {
				enemies.add(new Enemy(1, 4));
				enemies.add(new Enemy(2, 4));
			}
			enemies.add(new Enemy(3, 3));
		}
		if (waveNumber == 10) {
			enemies.add(new Enemy(1, 4));
			enemies.add(new Enemy(2, 4));
			for (int i = 0; i < 2; i++) {
				enemies.add(new Enemy(3, 4));
			}
		}
	}

	private void clearScenery() {
		slowDownDelay = invincibilityDelay = 0;
		enemies.clear();
		bullets.clear();
		powerUps.clear();
	}

	private void congratulations() {      //State FinishState
		DrawablePanel.setRunning(false);
	}

	private void gameOver() {             //State GameOverState
		DrawablePanel.setRunning(false);
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
