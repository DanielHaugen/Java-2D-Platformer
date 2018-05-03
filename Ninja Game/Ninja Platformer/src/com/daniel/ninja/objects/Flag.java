package com.daniel.ninja.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import com.daniel.ninja.framework.GameObject;
import com.daniel.ninja.framework.ObjectId;
import com.daniel.ninja.window.BufferedImageLoader;
import com.daniel.ninja.window.Game;

public class Flag extends GameObject{
	private BufferedImage window;
	BufferedImageLoader loader = new BufferedImageLoader();
	Game gameInstance = new Game();

	public Flag(float x, float y, ObjectId id) {
		super(x, y, id);
	}

	public void tick(LinkedList<GameObject> object) {
		
	}

	public void render(Graphics g) {
		if(window == null) window = loader.loadImage("/Window Sprite.png");
		g.setColor(Color.yellow);
		g.fillRect((int)x, (int)y, 32, 32);
		//g.drawImage(window,(int) x,(int) y-32, null);
	}

	public Rectangle getBounds() {
		return new Rectangle((int)x,(int)y, 32, 32);
	}

}