package com.daniel.ninja.framework;

import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.Timer;

import com.daniel.ninja.objects.Bullet;
import com.daniel.ninja.objects.Player;
import com.daniel.ninja.window.Handler;

public class KeyInput extends KeyAdapter {
	
	Player tempObject;
	Handler handler;

	boolean showKeyPress = false;
	int jumpTime = (int)System.currentTimeMillis();

	/** Stores currently pressed keys */
	HashSet<Integer> pressedKeys = new HashSet<Integer>();

	public KeyInput(Handler handler){
		this.handler = handler;
		//Checks every 100ms if there's keys pressed
		new Timer(100, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String keysString = "";
				if(!pressedKeys.isEmpty()){
					Iterator<Integer> i = pressedKeys.iterator();
					while(i.hasNext()){
						keysString += i.next() + ",";
						//System.out.println(keysString);
					}
					/*******************************************************/
					if(showKeyPress){
						if(keysString.contains(Integer.toString(KeyEvent.VK_W))){
							System.out.print("W");
						}
						if(keysString.contains(Integer.toString(KeyEvent.VK_A))){
							System.out.print("A");
						}
						if(keysString.contains(Integer.toString(KeyEvent.VK_D))){
							System.out.print("D");
						}
						if(keysString.contains(Integer.toString(KeyEvent.VK_SPACE))){
							System.out.print("SPACE");
						}
					}
					/*******************************************************/
				} 
				UpdatePlayer(keysString);
				//output.setText(keysString);
			}
		}).start();
	}

	@Override
	public void keyPressed(KeyEvent event){
		//Add key to hashSet when pressed
		int keyCode = event.getKeyCode();
		pressedKeys.add(keyCode);
	}
	@Override
	public void keyReleased(KeyEvent event){
		//Remove key from hashset when released
		int keyCode = event.getKeyCode();
		pressedKeys.remove(keyCode);
	}
	@Override
	public void keyTyped(KeyEvent event){}

	public void UpdatePlayer(String pressedKeys){
		if(tempObject != handler.getTempObjectPlayer()){
			tempObject = (Player) handler.getTempObjectPlayer();
		}		

		if(!handler.getPaused()){
			if(pressedKeys.contains(Integer.toString(KeyEvent.VK_D)) && !tempObject.getWallRight() && !tempObject.getJustWallHungRight()){
				tempObject.setMovingRight(true);
				
				//Walk method receives parameter, Left?, and false represents walking to the right.
				tempObject.walk(false);
				
			} else {
				tempObject.setMovingRight(false);
			}

			if(pressedKeys.contains(Integer.toString(KeyEvent.VK_A)) && !tempObject.getWallLeft() && !tempObject.getJustWallHungLeft()){
				tempObject.setMovingLeft(true);
				
				//Walk method receives parameter, Left?, and true represents walking to the left.
				tempObject.walk(true);
				
			} else{
				tempObject.setMovingLeft(false);
			}

			if(pressedKeys.contains(Integer.toString(KeyEvent.VK_W)) /*&& !tempObject.isJumping()*/ && !tempObject.isFalling()){
				tempObject.setJumping(true);
				tempObject.jump();

				//tempObject.setX(tempObject.getX() + tempObject.getFacing());
				if(tempObject.getWallHangingLeft() || tempObject.getWallHangingRight()){
					tempObject.setVelX(5 * tempObject.getFacing());
					if(tempObject.getWallHangingLeft()) tempObject.setJustHungLeft(true);
					else if(tempObject.getWallHangingRight()) tempObject.setJustHungRight(true);
				}

			}

			if(pressedKeys.contains(Integer.toString(KeyEvent.VK_E))){
				if(tempObject.getTouchingFlag()){
					System.out.println("NEXT LEVEL");
					handler.switchLevel();
				}
			}
			
			if(pressedKeys.contains(Integer.toString(KeyEvent.VK_HOME))){
				System.out.println("Respawn");
				handler.restartLevel();
			}

			if(pressedKeys.contains(Integer.toString(KeyEvent.VK_SPACE))){
				handler.addObject(new Bullet(tempObject.getX() - (30 * Math.sin(tempObject.getAngleInRads())),tempObject.getY() + (45 * Math.cos(tempObject.getAngleInRads())), ObjectId.Bullet, tempObject.getFacing() * 10, tempObject.getAngleInRads()));
			}

			if(pressedKeys.contains(Integer.toString(KeyEvent.VK_SHIFT))){
				tempObject.setSprinting(true);
			} else {
				tempObject.setSprinting(false); 
			}
			
			if(pressedKeys.contains(Integer.toString(KeyEvent.VK_0))){
				System.out.println("Mouse X: " + MouseInfo.getPointerInfo().getLocation().x + " & Mouse Y: " + MouseInfo.getPointerInfo().getLocation().y);
			}

			if(pressedKeys.contains(Integer.toString(KeyEvent.VK_CONTROL))){
				Player.showDeveloperTools = !Player.showDeveloperTools;
			}
		}
		if(pressedKeys.contains(Integer.toString(KeyEvent.VK_ESCAPE)) && (( (int)System.currentTimeMillis() - jumpTime )) > 1000){
			
			jumpTime = (int) System.currentTimeMillis();
			
			handler.setPaused(!handler.getPaused());
			System.out.println("Paused: " + handler.getPaused());
		}
	}
}
