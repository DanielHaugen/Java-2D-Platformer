package com.daniel.ninja.framework;

import java.awt.image.BufferedImage;

import com.daniel.ninja.window.BufferedImageLoader;

public class Texture {
	
	SpriteSheet ps; //Player Sheet
	
	private BufferedImage player_sheet = null;
	
	public BufferedImage[] player  = new BufferedImage[12];
	public BufferedImage[] player_jump  = new BufferedImage[2];
	
	public Texture(){
		BufferedImageLoader loader = new BufferedImageLoader();
		try{
			player_sheet = loader.loadImage("/Ninja Sprite Sheet.png");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		ps = new SpriteSheet(player_sheet);
		
		getTextures();
	}
	
	private void getTextures(){		
		
		/**Right**/ //    (Columns,Rows)
		player[0] = ps.grabImage(1, 1, 65, 109); //Idle Frame for Player
		player[1] = ps.grabImage(2, 1, 69, 109); //Walking Animation for Player
		player[2] = ps.grabImage(3, 1, 75, 109); //Walking Animation for Player
		player[3] = ps.grabImage(4, 1, 79, 109); //Walking Animation for Player
		player[4] = ps.grabImage(5, 1, 82, 109); //Walking Animation for Player
		player[5] = ps.grabImage(6, 1, 84, 109); //Walking Animation for Player
		
		/**Left**/
		player[6] = ps.grabImage(8, 2, 64, 109); //Idle Frame for Player
		player[7] = ps.grabImage(5, 2, 71, 109); //Walking Animation for Player
		player[8] = ps.grabImage(4, 2, 67, 109); //Walking Animation for Player
		player[9] = ps.grabImage(3, 2, 93, 109); //Walking Animation for Player
		player[10] = ps.grabImage(2, 2, 95, 109); //Walking Animation for Player
		player[11] = ps.grabImage(1, 2, 92, 109); //Walking Animation for Player
		
		player_jump[0] = ps.grabImage(5, 2, 72, 109); //Left Jump Frame for Player
		player_jump[1] = ps.grabImage(2, 1, 69, 109); //Right Jump Frame for Player
	}
	
}
