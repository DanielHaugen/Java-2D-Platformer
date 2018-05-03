package com.daniel.ninja.objects;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

public class CustomShape {

    private BufferedImage image=null;
    
    private static final Set<Color> blacklistedColor = new HashSet<Color>(Arrays.asList(
    	     new Color[] {new Color(0,0,255,255), new Color(0,0,0,0), new Color(255,255,255,0)}
    	));

    public Area getArea() {

    	System.out.println("Checking Area...  " + this.toString());
    	
        if(image==null) return null;
        
        Area area = new Area();
        Rectangle r;
        int startY,checkY;
        
        for (int x=0; x<image.getWidth(); x++) {
            startY=0; //Reset startY for the next run (to check new pixel)
            checkY=-1; // set to -1 so we can check all y-values
            
            for (int y=0; y<image.getHeight(); y++) {
            	Color pixel = new Color(image.getRGB(x, y),true);
            	
				if (!blacklistedColor.contains(pixel)) {
                    if(startY==0) {startY=y;checkY=y;}
                    if(y>(checkY+1)) {
                        r = new Rectangle(x,startY,1,checkY-startY);
                        area.add(new Area(r)); 
                        startY=y;checkY=y;
                    }
                    checkY=y;
                }               
            }
            
            if((checkY-startY)!=0) {
                r = new Rectangle(x,startY,1,checkY-startY);
                area.add(new Area(r)); 
            }
        }
        
        return area;
    }

    public CustomShape(String path) {
        try {
            BufferedImage image = ImageIO.read(getClass().getResource(path));
            this.image = image;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setImageArea(String path){
    	try {
            BufferedImage image = ImageIO.read(getClass().getResource(path));
            this.image = image;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}