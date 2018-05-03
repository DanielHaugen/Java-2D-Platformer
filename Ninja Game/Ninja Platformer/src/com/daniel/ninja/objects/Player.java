package com.daniel.ninja.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.LinkedList;

import com.daniel.ninja.framework.GameObject;
import com.daniel.ninja.framework.ObjectId;
import com.daniel.ninja.framework.Texture;
import com.daniel.ninja.window.Animation;
import com.daniel.ninja.window.Game;
import com.daniel.ninja.window.Handler;

public class Player extends GameObject{
	
	/**Basics**/
	private int width = 48, height = 96;
	private float gravity = 0.5f;
	private final int MAX_SPEED = 15;
	
	private double rotationCount = 0;
	private double shiftRotation = 0;
	
	/**Movement**/
	private boolean movingLeft = false;
	private boolean movingRight = false;
	private boolean sprinting = false;
	
	/**Collision Testing**/
	private boolean wallRight = false;
	private boolean wallLeft = false;
	private boolean rightFoot = false;
	private boolean leftFoot = false;
	private boolean underRightFoot = false;
	private boolean underLeftFoot = false;
	private boolean topColliding = false;
	private boolean bottomColliding = false;
	
	private boolean bodyColliding = false;
	
	private boolean touchingFlag = false;
	
	/**Wall Hanging**/
	private boolean wallHangingLeft = false;
	private boolean wallHangingRight = false;
	
	private boolean justWallHungLeft = false;
	private boolean justWallHungRight = false;
	
	/**Instances**/
	private Handler handler;
	private Game gameTerrain = new Game();
	Graphics g;
	Graphics2D g2d;
	
	private Area terrainBounds = new Area(gameTerrain.getTerrainArea());
	
	Texture tex = Game.getInstance();
	
	/****/
	
	private Animation playerWalk, playerWalkLeft;
	
	public static boolean showDeveloperTools = false;
	
	public Player(float x, float y, Handler handler, ObjectId id) {
		super(x, y, id);
		this.handler = handler;
		
		playerWalk = new Animation(5, tex.player[1],tex.player[2],tex.player[3],tex.player[4],tex.player[5]);
		playerWalkLeft = new Animation(5, tex.player[7],tex.player[8],tex.player[9],tex.player[10],tex.player[11]);
	}

	public void tick(LinkedList<GameObject> object) {
		
		x += velX * Math.cos(Math.abs(getAngleInRads())) + (velY * Math.sin(-getAngleInRads()));
		y += velY - (velX * Math.sin(-getAngleInRads()));
		
		if(!(leftFoot || rightFoot) && Math.abs(rotationCount) < 5) rotationCount = 0;
		
		if(velX !=0 && !(movingLeft || movingRight) && !(falling || jumping)) velX /= 1.15;
		
		if((wallHangingLeft || wallHangingRight || Math.abs(rotationCount)>50) && !(falling || jumping) && velY <= 0){
			velY = 0.5;
		}
		
		if(Math.abs(velX) < 0.5) velX = 0;
		
		if(velX > 0)facing = 1; //1 = Right, -1 = Left
		else if(velX < 0)facing = -1;
		
		if((falling || jumping) && !(wallHangingLeft || wallHangingRight)){
			velY += gravity;
			
			if(velY > MAX_SPEED){
				velY = MAX_SPEED;
			}
		}
		
		Collision(object);
		
		playerWalk.runAnimation();
		playerWalkLeft.runAnimation();
	}
	
    public boolean doAreasCollide(Area area1, Area area2) {
        boolean collide = false;

        Area collide1 = new Area(area1);
        collide1.subtract(area2);
        if (!collide1.equals(area1)) {
            collide = true;
            return collide;
        }

        Area collide2 = new Area(area2);
        collide2.subtract(area1);
        if (!collide2.equals(area2)) {
            collide = true;
            return collide;
        }
        
        return collide;
    }
    
	private void Collision(LinkedList<GameObject> object){
		if(doAreasCollide(new Area (getRotatedBounds(getBoundsBottom())), terrainBounds)){
			velY = 0;
			bottomColliding = true;
			falling = false;
			jumping = false;
			justWallHungLeft = false;
			justWallHungRight = false;
			
			if(doAreasCollide(new Area (getRotatedBounds(getBoundsLeftFoot())), terrainBounds)){
				leftFoot = true;
				//if(!rightFoot) rotationCount += Math.toDegrees( this.getRotationAngle(getBoundsRightFoot()) );
				shiftToSurface(getBoundsRightFoot());
			}else leftFoot = false;
			if(doAreasCollide(new Area (getRotatedBounds(getBoundsRightFoot())), terrainBounds)){
				rightFoot = true;
				//if(!leftFoot) rotationCount += Math.toDegrees( this.getRotationAngle(getBoundsLeftFoot()) );
				shiftToSurface(getBoundsLeftFoot());
			}else rightFoot = false;
			
			if(leftFoot || rightFoot){
				rotationCount += Math.toDegrees( this.getRotationAngle(getBoundsLeftFoot()) );
			}
			if(leftFoot && rightFoot){
				shiftToSurface(getBoundsLeftFoot());
			}
			/*if(((underRightFoot && !underLeftFoot) || (underLeftFoot && !underRightFoot)) )
				rotationCount += Math.toDegrees( this.getRotationAngle(getBoundsLeftFoot()) ) * facing;*/
			
			if(doAreasCollide(new Area (getRotatedBounds(getBoundsUnderRightFoot())), terrainBounds)) underRightFoot = true;
			else underRightFoot = false;
			if(doAreasCollide(new Area (getRotatedBounds(getBoundsUnderLeftFoot())), terrainBounds)) underLeftFoot = true;
			else underLeftFoot = false;
			
		} else{
			falling = true;
			bottomColliding = false;
			leftFoot = false;
			rightFoot = false;
			underRightFoot = false;
			underLeftFoot = false;
		}
		
		if(doAreasCollide(new Area (getRotatedBounds(getBoundsTop())), terrainBounds)){
			topColliding = true;
			y += 7;
			velY = 1;
			if(!doAreasCollide(new Area (getRotatedBounds(getBoundsBottom())), terrainBounds))falling = true;
		} else topColliding = false;
		
		if(doAreasCollide(new Area (getRotatedBounds(getBoundsRight())), terrainBounds)){
			if(jumping || falling && !doAreasCollide(new Area (getRotatedBounds(getBoundsRightFoot())), terrainBounds)){
				wallHangingRight = true;
				justWallHungLeft = false;
				facing = -1;
				velY += .01;
				falling = false;
				jumping = false;
			}else{
				wallHangingRight = false;
			}
			wallRight = true;
			velX = 0;
		} else{
			wallRight = false;
			wallHangingRight = false;
		}
		
		if(doAreasCollide(new Area (getRotatedBounds(getBoundsLeft())), terrainBounds)){
			if(jumping || falling && !doAreasCollide(new Area (getRotatedBounds(getBoundsLeftFoot())), terrainBounds)){
				wallHangingLeft = true;
				justWallHungRight = false;
				facing = 1;
				velY += .01;
				falling = false;
				jumping = false;
			}else{
				wallHangingLeft = false;
			}
			wallLeft = true;
			velX = 0;
		} else{
			wallLeft = false;
			wallHangingLeft = false;
		}
		
		if(!(leftFoot && rightFoot) && (falling || jumping)){
			if(rotationCount != 0) rotationCount = rotationCount/1.125;
		}
		
		for(int i = 0; i < handler.object.size(); i++){
			GameObject tempObject = handler.object.get(i);
			
			if(tempObject.getId() == ObjectId.Flag){
				//Switch Level
				if(this.getBounds().intersects(tempObject.getBounds())){
					touchingFlag = true;
					//handler.switchLevel();
				} else {
					touchingFlag = false;
				}
			}
		}
		if(bottomColliding || wallRight || wallLeft || topColliding || rightFoot || leftFoot) this.bodyColliding = true;
		else this.bodyColliding = false;
	}

	public void render(Graphics g) {
		this.g = g;
		this.g2d = (Graphics2D) g;
		
		AffineTransform old = g2d.getTransform();
		g2d.rotate(getAngleInRads(),(double) x,(double) y);
		
		if(jumping){
			if(facing == 1)
				g.drawImage(tex.player_jump[1], (int)x, (int)y,48, 96, null);
			else
				g.drawImage(tex.player_jump[0], (int)x, (int)y,48, 96, null);
		}else{
			if(velX != 0){
				if(facing == 1)
					playerWalk.drawAnimation(g, (int)x, (int)y, 48, 96);
				else
					playerWalkLeft.drawAnimation(g, (int)x, (int)y, 48, 96);
			}else
				if(facing == 1)
					g.drawImage(tex.player[0], (int)x, (int)y,48, 96, null);
				else
					g.drawImage(tex.player[6], (int)x, (int)y,48, 96, null);
		}
		
		
		if(showDeveloperTools){
				
			if(bottomColliding)g.setColor(Color.green);
			else g.setColor(Color.red);
			g2d.draw(getBoundsBottom());

			if(wallRight)g.setColor(Color.green);
			else g.setColor(Color.red);
			g2d.draw(getBoundsRight());

			if(wallLeft)g.setColor(Color.green);
			else g.setColor(Color.red);
			g2d.draw(getBoundsLeft());
			
			if(topColliding)g.setColor(Color.green);
			else g.setColor(Color.red);
			g2d.draw(getBoundsTop());

			if(rightFoot)g.setColor(Color.white);
			else g.setColor(Color.red);
			g2d.draw(getBoundsRightFoot());

			if(leftFoot)g.setColor(Color.white);
			else g.setColor(Color.red);
			g2d.draw(getBoundsLeftFoot());
			
			if(underRightFoot)g.setColor(Color.green);
			else g.setColor(Color.red);
			g2d.draw(getBoundsUnderRightFoot());

			if(underLeftFoot)g.setColor(Color.green);
			else g.setColor(Color.red);
			g2d.draw(getBoundsUnderLeftFoot());
			
		}
		
		g2d.setTransform(old);
	}
	
	public void jump(){
		if(Math.abs(rotationCount) > 115){
			y+=10;
			x-=5;
			velY = 10;
		}else{
			velY = -10;
		}
	}
	
	public void walk(boolean left){
		if(left){
			if(sprinting){
				if(velX <= -5){
					if(velX <= -8){
						velX = -8;
					}else{
						velX--;
					}
				} else{
					velX = -5;
				}
			} else{
				if(velX < -5){
					velX++;
				} else{
					velX=-5;
				}
			}
		}else{
			if(sprinting){
				if(velX >= 5){
					if(velX >= 8){
						velX = 8;
					}else{
						velX++;
					}
				} else{
					velX = 5;
				}
			} else{
				if(velX > 5){
					velX--;
				} else{
					velX = 5;
				}
			}
		}
	}
	
	
	
	/**Setters**/
	//Movement
	public void setMovingLeft(boolean movingLeft){
		this.movingLeft = movingLeft;
	}
	public void setMovingRight(boolean movingRight){
		this.movingRight = movingRight;
	}
	public void setSprinting(boolean sprinting){
		this.sprinting = sprinting;
	}
	//Collision
	public void setWallLeft(boolean wallLeft){
		this.wallLeft = wallLeft;
	}
	public void setWallRight(boolean wallRight){
		this.wallRight = wallRight;
	}
	public void setLeftFootColliding(boolean leftFoot){
		this.leftFoot = leftFoot;
	}
	public void setRightFootColliding(boolean rightFoot){
		this.rightFoot = rightFoot;
	}
	public void setTopColliding(boolean topColliding){
		this.topColliding = topColliding;
	}
	public void setBottomColliding(boolean bottomColliding){
		this.bottomColliding = bottomColliding;
	}
	public void setTouchingFlag(boolean touchingFlag){
		this.touchingFlag = touchingFlag;
	}
	//Wall Hanging
	public void setWallHangingLeft(boolean wallHangingLeft){
		this.wallHangingLeft = wallHangingLeft;
	}
	public void setWallHangingRight(boolean wallHangingRight){
		this.wallHangingRight = wallHangingRight;
	}
	public void setJustHungLeft(boolean justWallHungLeft){
		this.justWallHungLeft = justWallHungLeft;
	}
	public void setJustHungRight(boolean justWallHungRight){
		this.justWallHungRight = justWallHungRight;
	}

	/**Getters**/	
	//Movement
	public boolean getMovingLeft(){
		return this.movingLeft;
	}
	public boolean getMovingRight(){
		return this.movingRight;
	}
	public boolean getSprinting(){
		return sprinting;
	}
	//Collision
	public double getRotationCount(){
		return rotationCount;
	}
	public boolean getBodyColliding(){
		return this.bodyColliding;
	}
	public boolean getWallLeft(){
		return this.wallLeft;
	}
	public boolean getWallRight(){
		return this.wallRight;
	}
	public boolean getLeftFootColliding(){
		return this.leftFoot;
	}
	public boolean getRightFootColliding(){
		return this.rightFoot;
	}
	public boolean getTopColliding(){
		return this.topColliding;
	}
	public boolean getBottomColliding(){
		return this.bottomColliding;
	}
	public boolean getTouchingFlag(){
		return touchingFlag;
	}
	//Wall Hanging
	public boolean getWallHangingLeft(){
		return wallHangingLeft;
	}
	public boolean getWallHangingRight(){
		return wallHangingRight;
	}
	public boolean getJustWallHungLeft(){
		return justWallHungLeft;
	}
	public boolean getJustWallHungRight(){
		return justWallHungRight;
	}


	public void getPlayerID(){
		for(int i = 0; i < handler.object.size(); i++){
			if(handler.object.get(i).getId() == ObjectId.Player){
				return;
			}
		}
	}

	//TerrainBounds
	public void updateTerrainBounds(){
		this.terrainBounds = gameTerrain.getTerrainArea();
	}
	public Area getTerrainBounds(){
		return this.terrainBounds;
	}
	public void setTerrainBounds(Area area){
		this.terrainBounds = area;
	}

	private double getDistFootToGround(Rectangle footNotPlanted){

		System.out.println("getDistFootToGround");
		if(falling || jumping)return 100;

		double dist, tmpHeight;
		Rectangle tmpRect;
		
		if(footNotPlanted == getBoundsRightFoot()) tmpRect = this.getBoundsUnderRightFoot();
		else tmpRect = this.getBoundsUnderLeftFoot();
		
		tmpHeight = 3;
		tmpRect.setSize(5, (int) tmpHeight);

		while((leftFoot || rightFoot) && tmpHeight <= 18 && !doAreasCollide(new Area (getRotatedBounds(tmpRect)), terrainBounds)){
			tmpHeight+= 2;
			tmpRect.setSize(5, (int) tmpHeight);

			//System.out.println(tmpRect.getBounds() + " & " +tmpHeight);
			//System.out.println("distY:" + ((tmpHeight+tmpX)-startY));
		}

		dist = tmpHeight;
		//if(shiftRotation > 90 && shiftRotation < 270) dist = Math.abs(dist) * -1;
		return dist;
	}

	private double getDistFootToTopGround(Rectangle footNotPlanted){
		double startY,startX;
		double tmpX, tmpY;
		double dist;
		Rectangle tmpRect;
		
		if(footNotPlanted == getBoundsRightFoot()) tmpRect = this.getBoundsUnderRightFoot();
		else tmpRect = this.getBoundsUnderLeftFoot();
		
		startX = tmpRect.getX();
		startY = tmpRect.getY();
		tmpX = startX;
		tmpY = startY;
		tmpRect.setLocation((int)tmpX, (int)tmpY);
		tmpRect.setSize(4, 1);

		while((Math.abs(tmpY-startY)+Math.abs(tmpX-startX)) < 15 && doAreasCollide(new Area (getRotatedBounds(tmpRect)), terrainBounds)/*terrainBounds.contains(tmpX, tmpY)*/){
			tmpY -= 3 * Math.sin(Math.toRadians(shiftRotation));
			tmpX -= 3 * Math.cos(Math.toRadians(shiftRotation));
			tmpRect.setLocation((int)tmpX, (int)tmpY);
		}
		dist = Math.abs(startY-tmpY) + Math.abs(tmpX-startX);
		System.out.println("ToTop: " + dist);
		/*System.out.println("ToTop: " + dist + " && StartPoint: (" + String.format("%.2f", startX) + "," + String.format("%.2f", startY) + ") 
			&& EndPoint: (" + String.format("%.2f", tmpX) + "," + String.format("%.2f", tmpY) + ")");*/
		return dist;
	}

	private double getRotationAngle(Rectangle footNotPlanted){

		System.out.println("getRotationAngle");

		double theta; //Theta is the angle to be solved for.
		double distHoriz; //Adjacent to angle.
		double distVert = getDistFootToGround(footNotPlanted); //Opposite to angle.
		double tmpX, tmpY; //Foot Coordinates
		double rotatedX, rotatedY; //Rotated Foot Coordinates


		if(leftFoot){
			tmpX = getBoundsLeftFoot().getX() + (getBoundsLeftFoot().getWidth()/1.225);
			tmpY = getBoundsLeftFoot().getY() + (getBoundsLeftFoot().getHeight()/1.35);
		}
		else{
			tmpX = getBoundsRightFoot().getX() + (getBoundsRightFoot().getWidth()/2)-((getBoundsRightFoot().getWidth()/2)/2);
			tmpY = getBoundsRightFoot().getY() + (getBoundsRightFoot().getHeight()/1.35);
		}

		rotatedX = (x-tmpX) * Math.cos(Math.toRadians(shiftRotation)) - (y-tmpY) * Math.sin(Math.toRadians(shiftRotation));
		rotatedY = (y-tmpY) * Math.cos(Math.toRadians(shiftRotation)) + (x-tmpX) * Math.sin(Math.toRadians(shiftRotation));

		rotatedX += x;
		rotatedY += y;

		distHoriz = Math.sqrt( Math.pow((tmpX - rotatedX), 2) + Math.pow((tmpY - rotatedY), 2) );
		
		distHoriz = Math.abs(distHoriz) * -facing;

		theta = Math.atan(distVert/distHoriz) * (Math.abs(velX+1)/1.25);

		calcShiftAngle();
		shiftToSurface(footNotPlanted);
		System.out.println("**************************\n" + "Left Foot Touching: " + (footNotPlanted.getX() == getBoundsUnderLeftFoot().getX()) + " DistVert: " + distVert + " & DistHoriz: " + distHoriz + "\n" + "Theta: " + theta + "\n**************************");
		return theta;
	}

	private void shiftToSurface(Rectangle footNotPlanted){
		
		if(shiftRotation <=90 || shiftRotation >= 270){
			System.out.println("shiftToSurface : True && Dist: " + getDistFootToTopGround(footNotPlanted));
			
			x -= Math.sin(Math.toRadians(shiftRotation)) * getDistFootToTopGround(footNotPlanted);
			y -= Math.cos(Math.toRadians(shiftRotation)) * getDistFootToTopGround(footNotPlanted);
			

		}else{
			System.out.println("shiftToSurface : False");
			
			x += (Math.sin(Math.toRadians(shiftRotation)) * getDistFootToTopGround(footNotPlanted))/5;
			y += (Math.cos(Math.toRadians(shiftRotation)) * getDistFootToTopGround(footNotPlanted))/5;
		}
	}

	public Shape getRotatedBounds(Rectangle bounds){
		AffineTransform at = AffineTransform.getRotateInstance(getAngleInRads(), x,  y);
		return at.createTransformedShape(bounds);
	}
	
	private double calcShiftAngle(){
		
		if(rotationCount>0){
			shiftRotation = 360 - Math.abs(rotationCount);
		} else{
			shiftRotation = Math.abs(rotationCount);
		}
		
		return shiftRotation;
	}
	
	public double getShiftAngle(){
		return shiftRotation;
	}
	
	public double getAngleInRads(){
		return Math.toRadians(rotationCount);
	}

	/**Player Boundaries**/
	public Rectangle getBounds() {
		return new Rectangle((int)x, (int)y, (int)width, (int)height);
	}
	public Rectangle getBoundsBottom() {
		return new Rectangle((int) ((int)x+(width/2)-((width/2)/2)), (int) ((int)y+(height/2)), (int)width/2, (int)height/2);
	}
	public Rectangle getBoundsTop() {
		return new Rectangle((int) ((int)x+(width/2)-((width/2)/2)), (int)y, (int)width/2, (int)height/2);
	}
	public Rectangle getBoundsRight() {
		return new Rectangle((int) ((int)x+width-5), (int)y+5, (int)5, (int)height/2+10);
	}
	public Rectangle getBoundsLeft() {
		return new Rectangle((int)x, (int)y+5, (int)5, (int)height/2+10);
	}
	public Rectangle getBoundsRightFoot() {
		return new Rectangle((int) ((int)x+(width/1.225)-((width/2)/2)), (int) ((int)y+(height/1.35)), (int)width/5, (int)(height/4.3));
	}
	public Rectangle getBoundsLeftFoot() {
		return new Rectangle((int) ((int)x+(width/2)-((width/2)/2)), (int) ((int)y+(height/1.35)), (int)width/5, (int)(height/4.3));
	}
	public Rectangle getBoundsUnderRightFoot() {
		return new Rectangle((int) ((int)x+(width/1.225)-((width/2)/2)), (int) ((int)y+(height/1.085)), (int)width/5, (int)(height/11));
	}
	public Rectangle getBoundsUnderLeftFoot() {
		return new Rectangle((int) ((int)x+(width/2)-((width/2)/2)), (int) ((int)y+(height/1.085)), (int)width/5, (int)(height/11));
	}

}
