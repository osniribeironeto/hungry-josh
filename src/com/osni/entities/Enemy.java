package com.osni.entities;


import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.osni.world.*;
import com.osni.main.Game;
import com.osni.main.Sounds;

public class Enemy extends Entity{
	
	private double speed = 0.4;
	

	private int frames = 0, maxFrames = 10, index = 0, maxIndex = 3;
	private boolean moved = false;
	//private BufferedImage[] enemyLeft;
	//private BufferedImage[] enemyRight;
	private BufferedImage enemyLeft;
    private BufferedImage enemyRight;
	public int right_dir = 0;
	public int left_dir = 1;
	public int dir = right_dir;
	public int hp=20;
	public static int score=0;
	public boolean isDamaged = false;
	public int damageFrames=8;
	public int currDamage=0;
	
	
	public Enemy(int x, int y, int w, int h, BufferedImage sprite) {
		super(x, y, w, h, sprite);
		/*enemyLeft = new BufferedImage[4];
		enemyRight = new BufferedImage[4];
		for (int i = 0; i < 4; i++) {
			// Josh rightPlayer[i] = Game.spritesheet.getSprite(32 + (i*PXL), PXL*2, PXL, PXL);
			enemyRight[i] = Game.spritesheet.getSprite(32 + (i*16), 32, 16, 16);
		}
		for (int i = 0; i < 4; i++) {
			// Josh leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*PXL), PXL*3, PXL, PXL);
			enemyLeft[i] = Game.spritesheet.getSprite(32 + (i*16), 48, 16, 16);
		}*/
		enemyRight = Game.spritesheet.getSprite(6*16, 16, 16, 16);
		enemyLeft = Game.spritesheet.getSprite(7*16, 16, 16, 16);
	}
	
	public void tick () {
		//if (Game.rand.nextInt(100) < 30) 
		
		moved = false;
		
		if (this.calculateDistance(this.getX(), this.getY(), Game.player.getX(), Game.player.getY()) < 90 ){
			if (!playerColliding()) {
				if ((int)x < Game.player.getX() && World.isFree((int)(x+speed), this.getY(),z)
						&& !isColliding((int)(x+speed), this.getY()))	{
					moved = true;
					x+=speed;
					dir = right_dir;
				} else if ((int)x > Game.player.getX() && World.isFree((int)(x-speed), this.getY(),z)
						&& !isColliding((int)(x-speed), this.getY())) {
					moved = true;
					x-=speed;
					dir = left_dir;
				}
				if ((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(y+speed),z)
						&& !isColliding(this.getX(), (int)(y+speed))) {
					moved = true;
					y+=speed;
				} else if ((int)y > Game.player.getY() && World.isFree(this.getX(), (int)(y-speed),z)
						&& !isColliding(this.getX(), (int)(y-speed))) {
					moved = true;	
					y-=speed;
				}
			}else {
				//we are colliding
				if (Game.rand.nextInt(100) < 30) {
					Game.player.hp--;
					Sounds.hitSound.play();
					Game.player.isDamaged = true;
					System.out.println("HP decreased to: " + Game.player.hp);
				} else if (Game.rand.nextInt(100) < 5){
						Game.player.hp--;
						Game.player.hp--;
						Sounds.hitSound.play();
						Game.player.isDamaged = true;
						System.out.println("Critical hit! HP decreased to: " + Game.player.hp);
				}
			
			}
		}
		
		/*
		if(path == null || path.size() == 0) {
			Vector2i start = new Vector2i( ((int)(x/16)) , ((int)(y/16)));
			Vector2i end = new Vector2i( ((int)(Game.player.x/16)), ((int)(Game.player.y/16)));
			path = AStart.findPath(Game.world, start, end);
		}
		
		
		followPath(path);
		*/
		if (moved) {
			frames ++;
			if (frames == maxFrames) {
				frames = 0;
				index++;
				if (index > maxIndex) 
					index = 0;
			}
		}
		
		shootColision();
		
		if (hp <= 0) {
			enemyDestroyed();
		}
		
		if (isDamaged) {
			this.currDamage++;
			if (this.currDamage == this.damageFrames) {
				this.currDamage=0;
				this.isDamaged=false;
			}
		}
	
	}
	
	public void shootColision() {
		for (int i = 0; i < Game.shoots.size(); i++) {
			Entity e = Game.shoots.get(i);
			
			if (e instanceof Shoot) {
				if (Entity.isColliding(this, e)) {
					Game.shoots.remove(i);
					isDamaged = true;
					hp-=5;
					Sounds.bombSound.play();
					return;
				}
			}
			
		}
	}
	
	public void enemyDestroyed() {
		Game.entities.remove(this);
		Game.enemies.remove(this);
		score+=100;
	}
	
	public boolean isColliding (int xNext, int yNext) {
		// to test rectangle collision
		Rectangle currEnemy = new Rectangle(xNext+maskX, yNext + maskY, maskW, maskH);
		for (int i = 0; i < Game.enemies.size(); i++) {
			Enemy e = Game.enemies.get(i);
			// if the enenmy is the same in the loop, continue
			if (e == this)
				continue;
			Rectangle targetEnemy = new Rectangle(e.getX() + maskX, e.getY() + maskY, maskW, maskH);
			if (currEnemy.intersects(targetEnemy) ) {
				return true;
			}
		}
		return false;
	}
	
	public boolean playerColliding () {
		Rectangle currEnemy = new Rectangle(this.getX(), this.getY(), maskW, maskH);
		Rectangle player = new Rectangle(Game.player.getX() - 7, Game.player.getY() - 7, 16, 16);
		return currEnemy.intersects(player) && Game.player.z == this.z;
	}
	
	public void render (Graphics g) {
		if (!isDamaged) {
			/*if (dir == right_dir) {
				g.drawImage(enemyRight[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			} else if (dir == left_dir) {
				g.drawImage(enemyLeft[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			}*/
			if (dir == right_dir) {
				g.drawImage(enemyRight, this.getX() - Camera.x, this.getY() - Camera.y, null);
			} else if (dir == left_dir) {
				g.drawImage(enemyLeft, this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
		} else {
			g.drawImage(ENEMY_DAMAGE, this.getX() - Camera.x, this.getY() - Camera.y, null);
		}
	}
}
