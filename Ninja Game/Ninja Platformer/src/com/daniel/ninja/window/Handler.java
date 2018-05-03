package com.daniel.ninja.window;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import com.daniel.ninja.framework.GameObject;
import com.daniel.ninja.framework.ObjectId;
import com.daniel.ninja.objects.Flag;
import com.daniel.ninja.objects.Player;

public class Handler {
	public LinkedList<GameObject> object = new LinkedList<GameObject>();

	private int currentLevel = 0;
	private BufferedImage currentLevelImage;
	private String allLevels[] = {"/TestMenu.png", "/TestLevel.png", "/Test.png", "/Full360TestMap.png"};
	private String currentLvlName = allLevels[currentLevel];
	private boolean switchingLevels = false;
	private boolean justClearedLevel = false;

	private boolean gamePaused = false;

	private GameObject tempObject;
	Game gameInstance = new Game();
	private Camera cam;
	GameObject playerId;
	BufferedImageLoader loader = new BufferedImageLoader();

	public Handler(Camera cam){
		this.cam = cam;
	}

	public void tick(){
		if(!gamePaused){
			for(int i = 0; i < object.size(); i++){

				tempObject = object.get(i);

				tempObject.tick(object);

				gameInstance.setCamX((int) -cam.getX());
				gameInstance.setCamY((int) -cam.getY());

				if(object.get(i).getId() == ObjectId.Bullet){
					if(!gameInstance.getScreenBounds().contains(object.get(i).getBounds())){
						this.removeObject(object.get(i));
					}
				}
			}
		}

	}

	public void render(Graphics g){
		for(int i = 0; i < object.size(); i++){

			if(gameInstance.getScreenBounds().contains(object.get(i).getBounds())){
				tempObject = object.get(i);
				tempObject.render(g);
			}

		}
	}

	public void LoadImageLevel(BufferedImage image){
		int w = image.getWidth();
		int h = image.getHeight();

		for(int xx = 0; xx < w; xx++){
			for(int yy = 0; yy< h; yy++){
				Color pixel = new Color(image.getRGB(xx, yy));
				int red = pixel.getRed();
				int green = pixel.getGreen();
				int blue = pixel.getBlue();

				if (red == 255 && green == 255 && blue == 0){
					System.out.println("Flag Spawn Coord: (" + xx + " , " + yy + ")");
					addObject(new Flag(xx, yy, ObjectId.Flag));
				}

				if (red == 0 && green == 0 && blue == 255){ //(228,235)
					System.out.println("Player Spawn Coord: (" + xx + " , " + yy + ")");
					addObject(new Player((float) (xx*1.75), (float) (yy*1.275), this, ObjectId.Player)); //Usual Spawn Point at (150,150) on Map Creation.
				}
			}
		}
	}

	public GameObject getTempObjectPlayer(){
		if (playerId == null || this.justClearedLevel){
			tick();
			for(int i = 0; i < object.size(); i++){
				if(object.get(i).getId() == ObjectId.Player){
					playerId = object.get(i);
					System.out.println(playerId);
					if(this.justClearedLevel) this.justClearedLevel = false;
				} else{
					System.out.println("No Player Found... Recalculating...");
				}
			}
		}
		return this.playerId;
	}

	public GameObject setTempObjectplayerId(int TrueplayerId){
		return this.playerId = object.get(TrueplayerId);
	}

	public void switchLevel(){
		this.switchingLevels = true;

		synchronized(this) {
			try {
				// Calling wait() will block this thread until another thread
				// calls notify() on the object.
				this.wait();
			} catch (InterruptedException e) {
				// Happens if someone interrupts your thread.
			}
		}


		System.out.println("\n" + "Current Level: " + gameInstance.getLevel());
		currentLevel = gameInstance.getLevel();
		clearLevel();
		this.justClearedLevel = true;

		cam.setX(0);
		cam.setY(0);

		System.out.println("Next Level: " + ++currentLevel);
		if(currentLevel<allLevels.length){
			gameInstance.setLevel(currentLevel++);
		}
		currentLevel = gameInstance.getLevel();
		currentLvlName = allLevels[currentLevel];
		System.out.println("Now Current Level, Due To Switch: " + gameInstance.getLevel() + "\n");

		currentLevelImage = loader.loadImage(currentLvlName);

		this.LoadImageLevel(currentLevelImage);

		getTempObjectPlayer();

		this.switchingLevels = false;

	}
	
	public void restartLevel(){
		this.switchingLevels = true;

		synchronized(this) {
			try {
				// Calling wait() will block this thread until another thread
				// calls notify() on the object.
				this.wait();
			} catch (InterruptedException e) {
				// Happens if someone interrupts your thread.
			}
		}


		System.out.println("\n" + "Current Level: " + gameInstance.getLevel());
		currentLevel = gameInstance.getLevel();
		clearLevel();
		this.justClearedLevel = true;

		cam.setX(0);
		cam.setY(0);

		System.out.println("Restarting Level: " + currentLevel);
		currentLevel = gameInstance.getLevel();
		currentLvlName = allLevels[currentLevel];
		System.out.println("Now Current Level, Due To Switch: " + gameInstance.getLevel() + "\n");

		currentLevelImage = loader.loadImage(currentLvlName);

		this.LoadImageLevel(currentLevelImage);

		getTempObjectPlayer();

		this.switchingLevels = false;

	}

	private void clearLevel(){
		object.clear();
	}

	public void addObject(GameObject object){
		this.object.add(object);
	}
	public void removeObject(GameObject object){
		this.object.remove(object);
	}

	public boolean getPaused(){
		return gamePaused;
	}
	public void setPaused(boolean b){
		this.gamePaused = b;
	}

	public String getLevelName(){
		if(currentLevel == 1){
			System.out.println("Level");
		}
		return currentLvlName;
	}

	public BufferedImage getLevelImage(){
		if(currentLevelImage!= null) return currentLevelImage;
		else{
			currentLevelImage = loader.loadImage(currentLvlName);
			return currentLevelImage;
		}
	}

	public boolean getSwitchingLevel(){
		return this.switchingLevels;
	}

}
