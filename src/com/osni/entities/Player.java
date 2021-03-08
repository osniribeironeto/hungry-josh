package com.osni.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.osni.main.Game;
import com.osni.main.Sounds;
import com.osni.world.Camera;
import com.osni.world.World;

public class Player extends Entity{
	
	public boolean right,up,left,down;
	
	public double speed = 1.5;
	public int right_dir = 0;
	public int left_dir = 1;
	public int dir = right_dir;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	private BufferedImage playerDamaged;
	private int frames = 0, maxFrames = 10, index = 0, maxIndex = 3;
	private boolean moved = false;
	public int hp = 100;
	public static int maxHp = 100;
	public int ammo = 0;
	private boolean gunOn = false;
	
	public boolean isDamaged = false;
	private int damageFrames = 0;
	
	public boolean shooting = false;
	public boolean mouseShoot = false;
	public int mx, my; //mouse positions
	
	//jump vars
	public boolean jump = false, isJumping = false, jUp=false, jDown=false;
	public int z = 0;
	public int jumpHeight = 30, currJump = 0;
	public int jumpSpeed=1;
	
	
	private final int PXL = 16;
	
	public Player(int x, int y, int w, int h, BufferedImage sprite) {
		super(x, y, w, h, sprite);
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		playerDamaged = Game.spritesheet.getSprite(0, 16, 16, 16);
		for (int i = 0; i < 4; i++) {
			// Josh rightPlayer[i] = Game.spritesheet.getSprite(32 + (i*PXL), PXL*2, PXL, PXL);
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i*PXL), 0, PXL, PXL);
		}
		for (int i = 0; i < 4; i++) {
			// Josh leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*PXL), PXL*3, PXL, PXL);
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*PXL), PXL, PXL, PXL);
		}
	}
	
	public void tick() {
		//jump
		if (jump) {
			if (isJumping == false) {
				jump = false;
				isJumping = true;
				jUp = true;
			}
		}
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
		
		//moving player
		moved = false;
		if (right && World.isFree((int)(x+speed), this.getY(), z)) {
			moved = true;
			dir = right_dir;
			incX(speed);
			Camera.x+=speed;			
		} else if (left && World.isFree((int)(x-speed), this.getY(),z)) {
			moved = true;
			dir = left_dir;
			incX(-speed);
			Camera.x-=speed;
		}
		
		if (up && World.isFree(this.getX(),(int)(y-speed),z)) {
			moved = true;
			this.incY(-speed);
			Camera.y-=speed;
		} else if (down && World.isFree(this.getX(),(int)(y+speed),z)) {
			moved = true;
			this.incY(speed);
			Camera.y+=speed;
		}
		
		if (moved) {
			frames ++;
			if (frames == maxFrames) {
				frames = 0;
				index++;
				if (index > maxIndex) 
					index = 0;
			}
		}
		
		//
		checkLifePack();
		checkAmmoPack();
		checkGun();
		
		//check colling with enemy
		if (isDamaged) {
			damageFrames++;
			if (damageFrames == 6) {
				damageFrames=0;
				isDamaged=false;
			}
		}
		
		//shooting logic
		if (shooting && gunOn && ammo > 0) {
			//create bullet and shoot
			int dx = 0;
			if (dir == right_dir) {
				dx = 1;
			} else if (dir == left_dir) {
				dx = -1;
			}
			shooting = false;
			ammo--;
			Shoot shoot = new Shoot(this.getX(), this.getY(), 16, 16, null, dx, 0);
			Game.shoots.add(shoot);
		}
		
		if (mouseShoot) {
			mouseShoot=false;
			if (gunOn && ammo > 0) {
				ammo--;
			
				double angle = 0;
				int px = 0;
				int py = 0;
				
				if (dir == right_dir) {
					px = 8;
					angle = Math.atan2(my - (this.getY() + py - Camera.y), mx - (this.getX() + px - Camera.x));
					
				} else {
					px = -8;
					angle = Math.atan2(my - (this.getY() + py - Camera.y), (mx - (this.getX() + px - Camera.x)));
				}
				
				double dx = Math.cos(angle);
				double dy = Math.sin(angle);
				
				Shoot shoot = new Shoot(this.getX()+px, this.getY()+py, 16, 16, null, dx, dy);
				Game.shoots.add(shoot);
					
			}
		}
		
		if (hp <= 0) {
			//Game Over
			hp=0;
			Game.gameState = "GAMEOVER";
		}
		
		//Camera positioning
		//x player position - original width
		// divided by 2 to be in the mid
		//Camera.x = this.getX() - Game.WIDTH/2;
		//Camera.y = this.getY() - Game.HEIGHT/2;
		
		//limiting black part *16 to convert to pixel
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2), 0, World.WIDTH*16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2), 0, World.HEIGHT*16 - Game.HEIGHT);
	}
	
	public void render (Graphics g) {
		if (!isDamaged) {
			if (dir == right_dir) {
				g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y -z, null);
				if (gunOn) {
					g.drawImage(GUN_RG, this.getX() - Camera.x + 7, this.getY() - Camera.y-z, null);
				} 
			} else if (dir == left_dir) {
				g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y -z, null);
				if (gunOn) {
					g.drawImage(GUN_LF, this.getX() - Camera.x - 7, this.getY() - Camera.y-z, null);
				} 
			}
		} else {
			if (dir == right_dir) {
				g.drawImage(playerDamaged, this.getX() - Camera.x, this.getY() - Camera.y-z, null);
				g.drawImage(GUN_RG_D, this.getX() - Camera.x + 7, this.getY() - Camera.y-z, null);
			} else if (dir == left_dir) {
				g.drawImage(playerDamaged, this.getX() - Camera.x, this.getY() - Camera.y-z, null);
				g.drawImage(GUN_LF_D, this.getX() - Camera.x - 7, this.getY() - Camera.y-z, null);
			}
		}
		if (isJumping) {
			g.setColor(new Color(0,0,0,200));
			g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y + 10, 14, 
					8);
		}
	}
	
	public void checkLifePack () {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity curr = Game.entities.get(i);
			if (curr instanceof LifePack) {
				if (Entity.isColliding(this, curr)) {
					hp +=10;
					Sounds.lifeSound.play();
					if (hp > 100)
						hp = 100;
					Game.entities.remove(curr);
					
				}
			}
		}
	}
	
		
		
	public void checkAmmoPack () {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity curr = Game.entities.get(i);
			if (curr instanceof Ammo) {
				if (Entity.isColliding(this, curr)) {
					ammo += 10;
					Sounds.ammoSound.play();
					if (ammo > 50)
						ammo = 50;
					Game.entities.remove(curr);
				}
			}
		}
	}
	public void checkGun () {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity curr = Game.entities.get(i);
			if (curr instanceof Gun) {
				if (Entity.isColliding(this, curr)) {
					gunOn=true;
					Sounds.gunSound.play();
					Game.entities.remove(curr);
					ammo=ammo+20;
				}
			}
		}
	}
	
	public int getScore() {
		return Enemy.score;
	}
}
