package com.daniel.ninja.window;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Area;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import com.daniel.ninja.framework.KeyInput;
import com.daniel.ninja.framework.ObjectId;
import com.daniel.ninja.framework.Texture;
import com.daniel.ninja.objects.CustomShape;
import com.daniel.ninja.objects.Player;

public class Game extends Canvas implements Runnable{

	private static final long serialVersionUID = -1865647683144431560L;

	private boolean running = false;
	private Thread thread;
	private int framesPerSecond;

	public static int WIDTH;
	public static int HEIGHT;
	private double delta = 0;

	private BufferedImage TestLevel, healthBar;

	private CustomShape shape = new CustomShape("/TestMenu.png");
	
	private Area shapeArea = shape.getArea();


	
	//Object
	Handler handler;
	Camera cam;
	static Texture tex;
	Player tempObject;

	private int LEVEL = 0;
	private double alphaFade = 0;

	private boolean justPaused = false;
	private boolean justSwitchedLevel = false;

	private int camX, camY;

	private void init(){
		WIDTH = getWidth();
		HEIGHT = getHeight();

		tex = new Texture();

		cam = new Camera(0, 0);
		handler = new Handler(cam);

		//Load Levels
		BufferedImageLoader loader = new BufferedImageLoader();
		TestLevel = loader.loadImage(handler.getLevelName()); //Loading the Level

		//Load Extras
		healthBar = loader.loadImage("/HealthBar.png"); //Loading Health bar

		handler.LoadImageLevel(TestLevel);

		handler.tick();

		if(tempObject != handler.getTempObjectPlayer()){
			tempObject = (Player) handler.getTempObjectPlayer();
		}

		this.addKeyListener(new KeyInput(handler));

		UpdateWindowSize(Window.frame);
	}

	public synchronized void start(){
		if(running)
			return;

		running = true;
		thread = new Thread(this);
		thread.start();


	}

	public void run(){
		init();
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		int frames = 0;
		long timer = System.currentTimeMillis();
		int updates = 0;

		while(running){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1){
				tick();
				updates++;
				delta--;
			}
			render();
			frames++;
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				System.out.println("FPS: " + frames + " -- TICKS: " + updates + " -- Delta: " + (double)Math.round(delta*100)/100);
				framesPerSecond = frames;
				frames = 0;
				updates = 0;
			}

		}
	}

	private void tick(){
		handler.tick();
		if(tempObject != handler.getTempObjectPlayer()){
			tempObject = (Player) handler.getTempObjectPlayer();
		}
		if(tempObject.getTerrainBounds() != this.shapeArea) tempObject.setTerrainBounds(shapeArea);



		cam.tick(tempObject);
	}

	private void render(){
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null){
			this.createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();
		Graphics2D g2d = (Graphics2D) g;

		RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF );
		qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
		g2d.setRenderingHints( qualityHints );
		
		/////////////  Draw Here  ////////////////

		g.setColor(new Color(25,191,224));
		g.fillRect(0, 0, getWidth(), getHeight());


		/**
		//Screen Size
				g.setColor(new Color(255,255,255));
				((Graphics2D) g).draw(this.getScreenBounds());
		 */

		g2d.translate(cam.getX(), cam.getY()); //begin of cam

		g.drawImage(handler.getLevelImage(), 0, 0, this); //Draw Map

		/**********Test Map Area**********\
			 g.setColor(new Color(255,255,255));
			((Graphics2D) g).fill(this.shapeArea);
			\*********************************/

		handler.render(g);

		g2d.translate(-cam.getX(), -cam.getY()); //end of cam


		g.drawImage(healthBar, 10, 10, this);

		if(Player.showDeveloperTools){
			if(tempObject.getId() == ObjectId.Player){
				g.setColor(Color.red);
				drawSplitString(g2d, "Entities: " + handler.object.size() + " & " + " FPS: " + this.framesPerSecond + " & " + " Rotation: " + String.format("%.2f", tempObject.getRotationCount()) +  " & " + " Trig-Rotation: " + String.format("%.2f", tempObject.getShiftAngle()) + 
						"\nX: " + String.format("%.2f", tempObject.getX()) + " & " + " Y: " + String.format("%.2f", tempObject.getY()) + 
						"\nVelocity X: " + String.format("%.2f", tempObject.getVelX()) + " & " + " Velocity Y: " + String.format("%.2f", tempObject.getVelY()) +
						"\nWall Hanging: Left = " + tempObject.getWallHangingLeft() + " | " + "Right = " + tempObject.getWallHangingRight() +
						"\nJumping:  " + tempObject.isJumping() + "  &  " + "Falling:  " + tempObject.isFalling() +
						"\nSprinting:  " + tempObject.getSprinting() +
						"\nJust Wall Hung:  Left =" + tempObject.getJustWallHungLeft() + " | " + "Right = " + tempObject.getJustWallHungRight(), 10, 100);
			}
		}

		if(handler.getPaused() || handler.getSwitchingLevel()){
			if(cam.getCameraLead())cam.setCameraLead(false);

			if(handler.getPaused()){
				this.justPaused = true;

				if(alphaFade < 245){
					if(framesPerSecond <= 245) alphaFade+= 245 / (framesPerSecond + 1);
					else alphaFade+= 0.8;
					if(alphaFade > 245) alphaFade= 245;
				}

				g.setColor(new Color(0,0,0, (int) alphaFade));
				g.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 50, 50);

				g2d.setStroke(new BasicStroke(5));
				g.setColor(new Color(255,255,255, (int) alphaFade));
				g.drawRoundRect(5, 5, getWidth()-10, getHeight()-10, 50, 50);
			} else{
				this.justSwitchedLevel = true;

				if(alphaFade < 255){
					if(framesPerSecond <= 255) alphaFade+= 255 / (framesPerSecond+1);
					else alphaFade++;
					if(alphaFade > 255) alphaFade= 255;
				} else{
					//Switch Map Area
					shape.setImageArea(handler.getLevelName());
					shapeArea = shape.getArea();
					//tempObject.updateTerrainBounds();
					///////////////////////////////////////
					synchronized(handler) {
						System.out.println("Loaded");
						handler.notify();
					}
				}

				g.setColor(new Color(0,0,0, (int) alphaFade));
				g.fillRect(0, 0, getWidth(), getHeight());
			}
		}else{
			if(!cam.getCameraLead())cam.setCameraLead(true);

			if(alphaFade > 0){
				if(framesPerSecond <= 255) alphaFade-= 255 / ((framesPerSecond/2) + 15);
				else alphaFade-= 0.5;
				if(alphaFade < 0) alphaFade= 0;

				g.setColor(new Color(10,10,10, (int) alphaFade));
				if(this.justPaused){
					g.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 50, 50);
					if(alphaFade <= 0) this.justPaused = false;
				}
				else if(this.justSwitchedLevel){
					g.fillRect(0, 0, getWidth(), getHeight());
					if(alphaFade <= 0) this.justSwitchedLevel = false;
				}
			}

		}

		//////////////////////////////////////////
		g.dispose();
		bs.show();


	}

	private void drawSplitString(Graphics g, String text, int x, int y) {
		for (String line : text.split("\n"))
			g.drawString(line, x, y += g.getFontMetrics().getHeight());
	}

	public static Texture getInstance(){
		return tex;
	}

	JFrame frame;
	public void UpdateWindowSize(JFrame frame){

		this.frame = frame;

		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent arg0) {
				String message = " Width: " +
						Integer.toString(frame.getWidth()) + " & Height: "
						+ Integer.toString(frame.getHeight());
				System.out.println(message);

				WIDTH = frame.getWidth();
				HEIGHT = frame.getHeight();
			}
		}
				);

	}

	public int getScreenWidth(){
		WIDTH = getWidth();
		return Game.WIDTH;
	}
	public int getScreenHeight(){
		HEIGHT = getHeight();
		return Game.HEIGHT;
	}

	public int getLevel(){
		return this.LEVEL;
	}
	public void setLevel(int level){
		this.LEVEL = level;
	}

	public int getCamX(){
		return this.camX;
	}
	public void setCamX(int camX){
		this.camX = camX;
	}
	public int getCamY(){
		return this.camY;
	}
	public void setCamY(int camY){
		this.camY = camY;
	}

	public CustomShape getShapeLevel(){
		return shape;
	}
	public Area getTerrainArea(){
		return shapeArea;
	}

	public boolean getJustSwitchedLevel(){
		return this.justSwitchedLevel;
	}

	public Rectangle getScreenBounds() {
		return new Rectangle(this.camX-25, this.camY, WIDTH+50, HEIGHT);
	}

	public static void main(String args[]){
		new Window(800, 600, "Ninja Platform Game Prototype", new Game());
	}
}
