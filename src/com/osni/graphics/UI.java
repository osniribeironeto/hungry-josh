package com.osni.graphics;

import java.awt.Color;
import java.awt.Graphics;

import com.osni.main.Game;

public class UI {
	
	
	public void render (Graphics g) {
		g.setColor(Color.red);
		g.fillRect(10, 10, 50, 8);
		g.setColor(Color.GREEN);
		g.fillRect(10, 10, Game.player.hp/2 , 8);
		g.setColor(Color.white);		
	}
}
