package com.daniel.ninja.window;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class PauseMenu extends JPanel{

	/**
	 * Work In Progress... Should be the pause menu GUI
	 */
	private static final long serialVersionUID = 1L;
	
	final static boolean shouldFill = false;
    final static boolean shouldWeightX = true;
    final static boolean RIGHT_TO_LEFT = false;
		
    public PauseMenu() {
    	if (RIGHT_TO_LEFT) {
    		setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    	}
    	
    	try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

    	this.setBackground(new Color(0, 0, 0, 10));
    	setOpaque(false);
    	JButton button;
    	setLayout(new GridBagLayout());
    	GridBagConstraints c = new GridBagConstraints();
    	if (shouldFill) {
    		//natural height, maximum width
    		c.fill = GridBagConstraints.HORIZONTAL;
    	}

    	System.out.println("PAUSE BUTTONS");
    	
    	button = new JButton("Button 1");
    	if (shouldWeightX) {
    		c.weightx = 0.5;
    	}
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.ipady = 20;
    	c.insets = new Insets(10,10,10,10);  //padding
    	c.gridx = 0;
    	c.gridy = 0;
    	add(button, c);
    	

    	button = new JButton("Button 2");
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.ipady = 20;
    	c.weightx = 0.5;
    	c.insets = new Insets(10,10,10,10);  //padding
    	c.gridx = 1;
    	c.gridy = 0;
    	add(button, c);

    	button = new JButton("Button 3");
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.ipady = 20;
    	c.weightx = 0.5;
    	c.insets = new Insets(10,10,10,10);  //padding
    	c.gridx = 2;
    	c.gridy = 0;
    	add(button, c);

    	button = new JButton("Long-Named Button 4");
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.ipady = 40;      //make this component tall
    	c.weightx = 0.0;
    	c.gridwidth = 3;
    	c.gridx = 0;
    	c.gridy = 1;
    	add(button, c);

    	button = new JButton("5");
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.ipady = 0;       //reset to default
    	c.weighty = 1.0;   //request any extra vertical space
    	c.anchor = GridBagConstraints.PAGE_END; //bottom of space
    	c.insets = new Insets(10,10,10,10);  //top padding
    	c.gridx = 1;       //aligned with button 2
    	c.gridwidth = 2;   //2 columns wide
    	c.gridy = 2;       //third row
    	add(button, c);
    }
}
