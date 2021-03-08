package com.osni.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.osni.main.Game;
import com.osni.world.Camera;
import com.osni.world.World;

public class Shoot extends Entity{
	
	private double dx, dy;
	private double speed = 3;
	private int maxPos = 20, currPos = 0;
	private boolean wallShoot = false;
	private int shootFrame = 0;
	public boolean jump = false, isJumping = false, jUp=true, jDown=false;
	public int z = 0;
	public int jumpHeight = 10, currJump = 0;
	public int jumpSpeed=1;
	
	public Shoot(int x, int y, int w, int h, BufferedImage sprite, double dx, double dy) {
		super(x, y, w, h, sprite);
		this.dx = dx;
		this.dy = dy;
	}

	public void tick () {
		
		x+=dx*speed;
		y+=dy*speed;
		currPos++;
		
		if (wallCol()) {
			shootFrame++;
			if (shootFrame == 10) {
				shootFrame=0;
				wallShoot = false;
			}
		}
		
		isJumping = true;
		
		//going up and down
		if (isJumping == true) {
			if (jUp) {
				currJump+=jumpSpeed;
			} else if (jDown) {
				currJump-=jumpSpeed;
				if (currJump <= 0) {
					isJumping = false;
					jUp = true;
					jDown = false;
				}
					
			}
			z = currJump;
			if (currJump >= jumpHeight) {
				jUp = false;
				jDown = true;
			}
		}
		
		
		if (currPos == maxPos) {
			Game.shoots.remove(this);
			return;
		}
		
		//wallCol();
		//System.out.println(wallShoot+"4");

	}
	
	public boolean wallCol() {
		if (!World.isFree(this.getX(), this.getY(),z)){
			Game.shoots.remove(this);
			wallShoot = true;
			return true;
		}
		return false;
	}
	
	public void render(Graphics g) {
		if (!wallShoot) {
			g.drawImage(GUN_EN, this.getX() - Camera.x, this.getY() - Camera.y - z, width, height, null);
		} else {
			//System.out.println("he");
			g.drawImage(ENEMY_DAMAGE, this.getX() - Camera.x, this.getY() - Camera.y - z, width, height, null);
			
		}
	}
}
