package com.osni.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.osni.main.Game;

public class Tile {
	//Tile = statics objects in the game like floor, walls..
	
	// FILE_FLOR will call from Game class the position of the floor and wall in the spritesheet
	public static BufferedImage TILE_FLOOR = Game.spritesheet.getSprite(0, 0, 16, 16);
	public static BufferedImage TILE_WALL = Game.spritesheet.getSprite(16, 0, 16, 16);
	
	private BufferedImage sprite;
	private int x,y;
	
	public Tile(int x, int y, BufferedImage sprite) {
		this.x=x;
		this.y=y;
		this.sprite=sprite;
	}
	
	public void render(Graphics g) {
		g.drawImage(sprite, x - Camera.x, y - Camera.y, null);
		
	}

}
