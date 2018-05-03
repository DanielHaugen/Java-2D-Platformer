package com.daniel.ninja.window;

import java.awt.MouseInfo;

import com.daniel.ninja.framework.GameObject;

public class Camera {

	private double x;

	private double y;

	private boolean cameraLead = true;
	
	public Camera(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public void tick(GameObject player){
		if(cameraLead){
			x = (-player.getX() + Game.WIDTH/2) - MouseInfo.getPointerInfo().getLocation().x/6;
			y = (-player.getY() + Game.HEIGHT/2) - MouseInfo.getPointerInfo().getLocation().y/6;
		}
	}
	
	public void setX(float x){
		this.x = x;
	}
	public void setY(float y){
		this.y = y;
	}
	
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	
	public boolean getCameraLead(){
		return cameraLead;
	}
	public boolean setCameraLead(boolean b){
		return cameraLead = b;
	}

}
