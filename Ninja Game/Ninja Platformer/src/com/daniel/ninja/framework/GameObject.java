package com.daniel.ninja.framework;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.LinkedList;

public abstract class GameObject {
	protected double x,y;
	protected ObjectId id;
	protected double velX = 0;
	protected double velY = 0;
	protected boolean falling = true;
	protected boolean jumping = false;
	/**
	 * Returns the direction the player is facing.
	 * 1 = Right,
	 * -1 = Left
	 */
	protected static int facing = 1; //1 = Right, -1 = Left
	
	public GameObject(double x, double y, ObjectId id){
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	public abstract void tick(LinkedList<GameObject> object);
	public abstract void render(Graphics g);
	public abstract Rectangle getBounds();
	
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	public void setX(double x){
		this.x = x;
	}
	public void setY(double y){
		this.y = y;
	}
	
	public double getVelX(){
		return velX;
	}
	public double getVelY(){
		return velY;
	}
	public void setVelX(double velX){
		this.velX = velX;
	}
	public void setVelY(double velY){
		this.velY = velY;
	}
	
	public boolean isFalling() {
		return falling;
	}

	public void setFalling(boolean falling) {
		this.falling = falling;
	}

	public boolean isJumping() {
		return jumping;
	}

	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}
	
	public int getFacing(){
		return facing;
	}

	public ObjectId getId(){
		return id;
	}
	
}
