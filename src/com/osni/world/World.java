package com.osni.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.osni.entities.*;
import com.osni.graphics.Spritesheet;
import com.osni.graphics.UI;
import com.osni.main.Game;

public class World {
	
	public static Tile[] tiles;
	public static int WIDTH, HEIGHT;
	public static int TILE_SIZE = 16;
		
	public World (String path){
		try {
			//geting image from path (/map.png)
			BufferedImage mapSprite = ImageIO.read(getClass().getResource(path));
			//creating an array with all pixels in the screen (width * height)
			WIDTH = mapSprite.getWidth();
			HEIGHT = mapSprite.getHeight();
			int[] pixels = new int[WIDTH * HEIGHT];
			tiles = new Tile[WIDTH * HEIGHT];
			mapSprite.getRGB(0, 0, WIDTH, HEIGHT, pixels, 0, WIDTH);
			/*
			for (int i = 0; i < pixels.length; i ++) {
				//percorre todos pixels da tela
				//pixels[i] == FF + cor em hexadecimal (pegar no paint.net)
				if (pixels[i]== 0xFFFF0010) {
					System.out.println("Red");
				}
			}*/
			
			for (int xx = 0; xx < WIDTH; xx++) {
				for (int yy = 0; yy < WIDTH; yy++) {
					int curr = xx + (yy*WIDTH);
					tiles[curr] = new FloorTile(xx*16,yy*16,Tile.TILE_FLOOR);
					if (pixels[curr] == 0xFF000000) {
						//floor
						tiles[curr] = new FloorTile(xx*16,yy*16,Tile.TILE_FLOOR);
					} else if (pixels[curr] == 0xFFFFFFFF) {
						tiles[curr] = new WallTile(xx*16,yy*16,Tile.TILE_WALL);
						//parede
					} else if (pixels[curr] == 0xFF0019FF) {
						//player
						Game.player.setX(xx*16);
						Game.player.setY(yy*16);
					} else if (pixels[curr] == 0xFFFF0010) {
						//enemy position, ENEMY_EN its the enemy sprite
						// creates 1 instance of enemy then add to 2 lists
						Enemy en = new Enemy(xx*16, yy*16, 16, 16, Entity.ENEMY_EN);
						Game.entities.add(en);
						Game.enemies.add(en);
					} else if (pixels[curr] == 0xFF705D00) {
						//gun 
						Game.entities.add(new Gun(xx*16, yy*16, 16, 16, Entity.GUN_EN));
					} else if (pixels[curr] == 0xFFFFD800) {
						//ammo
						Game.entities.add(new Ammo(xx*16, yy*16, 16, 16, Entity.AMMO_EN));
					} else if (pixels[curr] == 0xFFFFE5DB) {
						//lifepack
						Game.entities.add(new LifePack(xx*16, yy*16, 16, 16, Entity.LIFEPACK_EN));
					}
				}
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean isFree(int nextX, int nextY, int zPlayer) {
		//converting next position to tiles and checking in all directions
		int x1 = nextX / TILE_SIZE;
		int y1 = nextY / TILE_SIZE;
		
		int x2 = (nextX+TILE_SIZE-1) /TILE_SIZE;
		int y2 = nextY / TILE_SIZE;
		
		int x3 = nextX / TILE_SIZE;
		int y3 = (nextY+TILE_SIZE-1) /TILE_SIZE;
		
		int x4 = (nextX+TILE_SIZE-1) /TILE_SIZE;
		int y4 = (nextY+TILE_SIZE-1) /TILE_SIZE;
		
		// if one of them is false, means that we hit an wall in some of the directios
		// once we want to check what is free, we need to negate the return
		if (!(tiles[x1 +(y1*World.WIDTH)] instanceof WallTile || 
			     tiles[x2 +(y2*World.WIDTH)] instanceof WallTile || 
			     tiles[x3 +(y3*World.WIDTH)] instanceof WallTile || 
			     tiles[x4 +(y4*World.WIDTH)] instanceof WallTile)) {
			return true;
		}
		return false;
	}
	
	public static void restartGame (String lvl) {
		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Enemy>();
		Game.shoots = new ArrayList<Shoot>();
		Game.ui = new UI();
		Game.spritesheet = new Spritesheet("/sprites.png");
		Game.player = new Player (0,0,16,16,Game.spritesheet.getSprite(32, 0, 16, 16));
		Game.entities.add(Game.player);
		Game.world = new World("/"+lvl);
		return;
	}
	
	public void render(Graphics g) {
		//Initial camera position / 16 size of the entities
		int xstart = Camera.x >> 4;
		int ystart = Camera.y >> 4;
		
		int xfinal = xstart + (Game.WIDTH >> 4);
		int yfinal = ystart + (Game.HEIGHT >> 4);
		
		for (int xx = xstart; xx <= xfinal; xx++) {
			for (int yy = ystart; yy <= yfinal; yy++) {
				if (xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT)
					continue;
				int curr = xx + (yy*WIDTH);
				Tile tile = tiles[curr];
				tile.render(g);
			}
		}
	}
}
