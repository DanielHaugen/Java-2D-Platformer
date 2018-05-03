package com.daniel.ninja.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.LinkedList;

import com.daniel.ninja.framework.GameObject;
import com.daniel.ninja.framework.ObjectId;
import com.daniel.ninja.window.Handler;

public class Bullet extends GameObject{
	
	double angleInRads;
	int vel;
	
	public Bullet(double x, double y, ObjectId id, int vel, double angleInRads) {
		super(x, y, id);
		this.vel = vel;
		this.angleInRads = angleInRads;
	}

	public void tick(LinkedList<GameObject> object) {
		/*if(tempObject != handler.getTempObjectPlayer()){
			tempObject = (Player) handler.getTempObjectPlayer();
		}*/
		x += vel * Math.cos(angleInRads);
		y += vel * Math.sin(angleInRads);
	}

	public void render(Graphics g) {
		g.setColor(Color.red);
		g.fillRect((int)x, (int)y, 16, 16);
	}

	public Rectangle getBounds() {
		return new Rectangle((int)x,(int)y, 16, 16);
	}

}
