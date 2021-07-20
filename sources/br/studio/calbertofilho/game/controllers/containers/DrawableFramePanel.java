package br.studio.calbertofilho.game.controllers.containers;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import br.studio.calbertofilho.game.controllers.handlers.Keyboard;
import br.studio.calbertofilho.game.controllers.handlers.Mouse;
import br.studio.calbertofilho.game.managements.StatesManager;

@SuppressWarnings("serial")
public class DrawableFramePanel extends JPanel implements Runnable {

	private static Dimension gameDimensions;
	private Thread thread;
	private static boolean running;
	private Keyboard keyboard;
	private Mouse mouse;
	private BufferedImage image;
	private Graphics graphs;
	private Graphics2D graphics;
	private final int TARGET_FPS = 60;
	private static double averageFPS;
	private long startTime, URDTimeMillis, targetTime, waitTime, totalTime;
	private int frameCount, maxFrameCount;
	private StatesManager gameManager;

	public DrawableFramePanel(int width, int height) {
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
		initGraphics();
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

	public void initGraphics() {
		image = new BufferedImage(getGameWidth(), getGameHeight(), BufferedImage.TYPE_INT_ARGB);
		graphics = (Graphics2D) image.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	public void init() {
		running = true;
		keyboard = new Keyboard(this);
		mouse = new Mouse(this);
		gameManager = new StatesManager();
		totalTime = frameCount = 0;
		maxFrameCount = TARGET_FPS;
		targetTime = 1000 / TARGET_FPS;
	}

////////////////////////////////////////////////////////////////////////////////
	private void inputControls() {
		gameManager.input(mouse, keyboard);
	}

	private void updateGame() {
		gameManager.update();
	}

	private void renderGame() {
		gameManager.render(graphics);
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

	public static void setRunning(boolean running) {
		DrawableFramePanel.running = running;
	}

}
