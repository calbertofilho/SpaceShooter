package br.studio.calbertofilho.game.controllers.containers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import br.studio.calbertofilho.game.controllers.handlers.Keyboard;
import br.studio.calbertofilho.game.controllers.handlers.Mouse;
import br.studio.calbertofilho.game.objects.Bullet;
import br.studio.calbertofilho.game.objects.Enemy;
import br.studio.calbertofilho.game.objects.Player;

@SuppressWarnings("serial")
public class DrawPanel extends JPanel implements Runnable {

	private static Dimension gameDimensions;
	private Thread thread;
	private boolean running;
	private Keyboard keyboard;
	private Mouse mouse;
	private BufferedImage image;
	private Graphics graphs;
	private Graphics2D graphics;
	private final int TARGET_FPS = 60;
	private static double averageFPS;
	private long startTime, URDTimeMillis, targetTime, waitTime, totalTime;
	private int frameCount, maxFrameCount;
	private static Player player;
	private double dX, dY, distance;
	private Bullet bullet;
	private static ArrayList<Bullet> bullets;
	private double bulletX, bulletY, bulletRadius;
	private Enemy enemy;
	private static ArrayList<Enemy> enemies;
	private double enemyX, enemyY, enemyRadius;

	public DrawPanel(int width, int height) {
		super();
		gameDimensions = new Dimension(width, height);
		setPreferredSize(gameDimensions);
		setFocusable(true);
		requestFocus();
		setDoubleBuffered(true);
	}

	@Override
	public void addNotify() {
		super.addNotify();
		if (thread == null) {
			thread = new Thread(this, "MainThread");
			thread.start();
		}
	}

	@Override
	public void run() {
		init();
		while (running) {
			startTime = System.nanoTime();
	/////////////////////////
			updateGame();
			inputControls();
			renderGame();
			drawGame();
	/////////////////////////
			URDTimeMillis = (System.nanoTime() - startTime) / 1000000;
			waitTime = (targetTime - URDTimeMillis) > 0 ? (targetTime - URDTimeMillis) : 0;
			try {
				Thread.sleep(waitTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			totalTime += System.nanoTime() - startTime;
			frameCount++;
			if (frameCount == maxFrameCount) {
				averageFPS = 1000.0 / ((totalTime / frameCount) / 1000000);
				frameCount = 0;
				totalTime = 0;
			}
		}
	}

	public void init() {
		running = true;
		keyboard = new Keyboard(this);
		mouse = new Mouse(this);
		image = new BufferedImage(getGameWidth(), getGameHeight(), BufferedImage.TYPE_INT_ARGB);
		graphics = (Graphics2D) image.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		totalTime = 0;
		frameCount = 0;
		maxFrameCount = TARGET_FPS;
		targetTime = 1000 / TARGET_FPS;
		player = new Player();
		bullets = new ArrayList<Bullet>();
		enemies = new ArrayList<Enemy>();
		for (int i = 0; i < 5; i++) {
			enemies.add(new Enemy(1, 1));
		}
	}

////////////////////////////////////////////////////////////////////////////////
	private void inputControls() {
		player.input(keyboard, mouse);
	}

	private void updateGame() {
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
				enemies.remove(i);
				i--;
			}
		}
	}

	private void renderGame() {
		// draw the background color //
		graphics.setColor(new Color(135, 206, 250)); //light sky
		graphics.fillRect(0, 0, getGameWidth(), getGameHeight());
		// draw player               //
		player.render(graphics);
		// draw bullets              //
		for (int i = 0; i < bullets.size(); i++) {
			bullet = bullets.get(i);
			bullet.render(graphics);
		}
		// draw enemies              //
		for (Enemy enemy : enemies) {
			enemy.render(graphics);
		}
		// show FPS counter          //
		graphics.setColor(Color.BLACK);
		String text = String.format("FPS: %.2f", getGameFPS());
		graphics.drawString(text, (getGameWidth() - graphics.getFontMetrics().stringWidth(text)) - 5, graphics.getFontMetrics().getHeight());
		text = "Disparos: " + bullets.size();
		graphics.drawString(text, (getGameWidth() - graphics.getFontMetrics().stringWidth(text)) - 5, 2 * graphics.getFontMetrics().getHeight());
	}

	private void drawGame() {
		graphs = this.getGraphics();
		graphs.drawImage(image, 0, 0, null);
		graphs.dispose();
	}
////////////////////////////////////////////////////////////////////////////////

	public static double getGameFPS() {
		return averageFPS;
	}

	public static int getGameWidth() {
		return (int) gameDimensions.getWidth();
	}

	public static int getGameHeight() {
		return (int) gameDimensions.getHeight();
	}

	public static Player getPlayer() {
		return player;
	}

	public static void addBullet(Bullet bullet) {
		bullets.add(bullet);
	}

}
