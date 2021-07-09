package br.studio.calbertofilho.game.controllers.containers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import br.studio.calbertofilho.game.controllers.handlers.Keyboard;
import br.studio.calbertofilho.game.controllers.handlers.Mouse;

@SuppressWarnings("serial")
public class Panel extends JPanel implements Runnable {

	public static final int WIDTH = 400, HEIGHT = 600;
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

	public Panel() {
		super();
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		requestFocus();
		setDoubleBuffered(true);
		running = false;
	}

	@Override
	public void addNotify() {
		super.addNotify();
		if (thread == null) {
			thread = new Thread(this, "GameThread");
			thread.start();
		}
	}

	@Override
	public void run() {
		init();
		while (running) {
			startTime = System.nanoTime();
	/////////////////////////
			input();
			update();
			render();
			draw();
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
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		graphics = (Graphics2D) image.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		totalTime = 0;
		frameCount = 0;
		maxFrameCount = TARGET_FPS;
		targetTime = 1000 / TARGET_FPS;
	}

////////////////////////////////////////////////////////////////////////////////
	private void input() {}

	private void update() {}

	private void render() {
		graphics.setColor(new Color(135, 206, 250)); //light sky
		graphics.fillRect(0, 0, WIDTH, HEIGHT);
		// show fps counter
		graphics.setColor(Color.WHITE);
		String text = String.format("FPS: %.2f", getFPS());
		graphics.drawString(text, (WIDTH - graphics.getFontMetrics().stringWidth(text)) - 5, graphics.getFontMetrics().getHeight());
	}

	private void draw() {
		graphs = this.getGraphics();
		graphs.drawImage(image, 0, 0, null);
		graphs.dispose();
	}
////////////////////////////////////////////////////////////////////////////////

	public static double getFPS() {
		return averageFPS;
	}

}
