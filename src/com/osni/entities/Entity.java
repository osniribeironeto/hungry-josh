package com.osni.entities;


import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import com.osni.main.Game;
import com.osni.world.Camera;
import com.osni.world.Node;
import com.osni.world.Vector2i;

public class Entity {
	
	public static BufferedImage LIFEPACK_EN = Game.spritesheet.getSprite(6*16, 0, 16, 16);
	public static BufferedImage GUN_EN = Game.spritesheet.getSprite(8*16, 0, 16, 16);
	public static BufferedImage GUN_LF = Game.spritesheet.getSprite(9*16, 0, 16, 16);
	public static BufferedImage GUN_RG = Game.spritesheet.getSprite(8*16, 16, 16, 16);
	public static BufferedImage GUN_LF_D = Game.spritesheet.getSprite(16, 16*2, 16, 16);
	public static BufferedImage GUN_RG_D = Game.spritesheet.getSprite(0, 16*2, 16, 16);
	public static BufferedImage AMMO_EN = Game.spritesheet.getSprite(9*16, 16, 16, 16);
	public static BufferedImage ENEMY_EN = Game.spritesheet.getSprite(6*16, 16, 16, 16);	
	public static BufferedImage ENEMY_DAMAGE = Game.spritesheet.getSprite(16, 16, 16, 16);	
	
	protected double x,y;
	protected int z;
	protected int width,height;
	public int maskX = 12, maskY = 8, maskW = 12, maskH = 10;
	
	private BufferedImage sprite;
	
	protected List<Node> path;
	
	public Entity (int x, int y, int w, int h, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		this.sprite = sprite;
		
		this.maskX=0;
		this.maskY=0;
		this.maskW=w;
		this.maskH=h;
	}
	
	
	public void followPath (List<Node> path) {
		if (path!= null) { // path found
			if (path.size() > 0 ) { //there is path do be followed yet
				Vector2i target = path.get(path.size() - 1 ).tile; //where i want to go, will get last item (tile) of the list (-1)
				//xprev = x;
				//yprev = y;
				if (x < target.x * 16) {
					x++; // or speed
				}else if (x > target.x * 16) {
					x--;
				}
				
				if (y < target.y *16) {
					y++;
				}else if (y > target.y *16) {
					y--;
				}
				
				if (x == target.x * 16 && y == target.y *16) // if i get in the target position, means we can go to another path
					path.remove(path.size()-1);
			}
		}
	}
	
	
	//entity collision
	public static boolean isColliding(Entity e1, Entity e2){
		Rectangle e1Mask = new Rectangle(e1.getX()+ e1.maskX, e1.getY()+e1.getY(),e1.getWidth()-2,e1.getHeight()-2);
		Rectangle e2Mask = new Rectangle(e2.getX()+ e2.maskX, e1.getY()+e2.getY(),e2.getWidth()-2,e2.getHeight()-2);
		return e1Mask.intersects(e2Mask) && e1.z == e2.z;
	}
	
	//distance calculing
	public double calculateDistance(int x1, int y1, int x2, int y2) {
		return Math.sqrt( (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) );
	}
	
	//GETS
	public int getX() {
		return (int)(this.x);
	}
	public int getY() {
		return (int)(this.y);
	}
	public int getWidth() {
		return (int) this.width;
	}
	public int getHeight() {
		return (int) this.height;
	}
	
	//SETS
	
	public void setMask(int mx, int my, int mh, int mw) {
		this.maskX=mx;
		this.maskY=my;
		this.maskH=mh;
		this.maskW=mw;
	}
	
	public void setX(double newX) {
		this.x = newX;
	}
	public void setY(double newY) {
		this.y = newY;
	}	
	public void setWidth(int newW) {
		this.width = newW;
	}	
	public void setHeight(int newH) {
		this.height = newH;
	}
	
	//INCREMENTS
	public void incX(double x) {
		this.x += x;
	}
	public void incY(double y) {
		this.y += y;
	}
	public void incWidth(double w) {
		this.width += w;
	}
	public void incHeight(double h) {
		this.height += h;
	}

	public void render (Graphics g) {
		
		g.drawImage(sprite, (int)this.getX() - Camera.x, (int)this.getY() - Camera.y, null);
		//g.setColor(Color.red);
		//g.fillRect((int)this.getX() - Camera.x + maskX, (int)this.getY() - Camera.y + maskY, maskW, maskH);
		
	}

	public void tick () {
		
	}
}
