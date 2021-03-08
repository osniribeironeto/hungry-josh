package com.osni.main;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.osni.entities.Enemy;
import com.osni.entities.Entity;
import com.osni.entities.Player;
import com.osni.entities.Shoot;
import com.osni.graphics.Spritesheet;
import com.osni.graphics.UI;
import com.osni.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener, MouseMotionListener{

	/*
	 */
	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	public boolean isRunning;
	public static final int WIDTH = 240; //240
	public static final int HEIGHT = 160; //160
	public static final int SCALE = 3;
	
	private BufferedImage image;
	private int curLvl = 1;
	private int maxLvl = 2;
	
	public static List<Entity> entities;
	
	public static List<Enemy> enemies;
	
	public static List<Shoot> shoots;
	
	public static Spritesheet spritesheet;
	
	public static Player player;
	
	public static World world;
	
	public static Random rand;
	
	public static UI ui;
	
	public static String gameState = "MENU";
	
	private boolean showMsgGameOver = true;
	
	private int framesGameOver = 0;
	
	private boolean restartGame = false;
	
	public Menu menu;
	
	public boolean saveGame = false;	
	
	public int mx,my;//mouse positions
	
	public int[] pixels; //pixel manipulation
	
	public BufferedImage lightmap;
	public int[] lightmapPixels;

	public Game() {
		//Sounds.backSound.loop();
		rand = new Random();
		addKeyListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
		setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
		initFrame();
	
		//initializing objects
		image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		try {
			lightmap = ImageIO.read(getClass().getResource("/lightmap.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		lightmapPixels = new int[lightmap.getWidth()*lightmap.getHeight()];
		lightmap.getRGB(0, 0, lightmap.getWidth(), lightmap.getHeight(), lightmapPixels, 0, lightmap.getWidth());
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();//allocating data insite pixels array
		//initializing entities in an arraylist
		entities = new ArrayList<Entity>();
		
		enemies = new ArrayList<Enemy>();
		
		shoots = new ArrayList<Shoot>();
		//passing the sprite path (images) to spritesheet constructor
		
		ui = new UI();
		spritesheet = new Spritesheet("/sprites.png");
		/* 
		 * initializing a player (extended by entity)
		 * 0 -> initial position (x)
		 * 0 -> initial position (y)
		 * 16,16 -> size of character
		 * getSprite to get the image produced by spritesheet (image IO),
		 * params of this method are the positions inside the spritesheet
		 * adding the player to entity list
		 */
		player = new Player (0,0,16,16,spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		
		//init map
		world = new World("/map1.png");
		menu = new Menu();
	}
	
	public void initFrame() {		
		//creating the frame of the game
		frame = new JFrame("Hungry Josh");
		frame.add(this);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	//sync threads for start and stop
	public synchronized void start() {
		thread = new Thread(this);
		requestFocus();
		isRunning=true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning=false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//MAIN
	public static void main(String[] args) {
		Game game = new Game();	
		game.start();
		
	}
	
	public void applyLight () {
		/*for (int xx = 0 ; xx < Game.WIDTH; xx++) {
			for (int yy = 0; yy < Game.HEIGHT; yy++) {
				if (lightmapPixels[xx+(yy*Game.WIDTH)] == 0xffffffff) {
					pixels[xx + (yy*Game.WIDTH)] = 0;
				}
			}
		}*/
	}
	
	/*pixel manipulation
	*
	public void drawRectangleEx() {
		for (int xx = 0 ; xx < 32; xx++) {
			for (int yy = 0; yy < 32; yy++) {
				int xOff = xx + 66;
				int yOff = yy + 60;
				
				pixels[xOff + (yOff*WIDTH)] = 0xFF0000;
			}
		}
	}
	*/
	
	//game logic
	public void tick () {
		if (gameState == "NORMAL") {
			if (this.saveGame) {
				this.saveGame=false;
				String[] opt1 = {"map","hp"};
				int[] opt2 = {this.curLvl, player.hp};
				Menu.SaveGame(opt1,opt2,20);
				System.out.println("Saved!");
			}
			this.restartGame=false; //prevent enter
			for (int i = 0; i < entities.size(); i++) {
				//Will add to an local variable Entity the entities added to the array list
				//then, will call tick for the entity to create the logic of the entity (player, enemy, etc...)
				Entity e = entities.get(i);
				e.tick();
			}
			for (int i = 0; i < shoots.size(); i++) {
				shoots.get(i).tick();
			}
			
			if (enemies.size()==0) {
				//Next level
				curLvl++;
				if (curLvl > maxLvl) {
					curLvl=1;
				}
				String newWorld = "map"+curLvl+".png";
				World.restartGame(newWorld);
			} 
		} else if (gameState == "GAMEOVER") {
				this.framesGameOver++;
				if (framesGameOver == 20) {
					this.framesGameOver=0;
					if (this.showMsgGameOver) 
						this.showMsgGameOver=false;
					else 
						this.showMsgGameOver=true;
				}
				if (restartGame) {
					this.restartGame=false;
					Game.gameState="NORMAL";
					curLvl=1;
					String newWorld = "map"+curLvl+".png";
					World.restartGame(newWorld);
				}
		} else if (gameState == "MENU") {
			menu.tick();
		}
		

		
	}
	
	//rendering of images
	public void render () {
		BufferStrategy bs = this.getBufferStrategy();
		
		//init bs for the first time only, then break the method
		if (bs==null) {
			this.createBufferStrategy(3);
			return;
		}

		Graphics g = image.getGraphics();
		g.setColor(new Color(0,0,0));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		//rendering world
		world.render(g);
		
		//same idea of the tick loop, but here will render/create the image of the entity
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		
		for (int i = 0; i < shoots.size(); i++) {
			shoots.get(i).render(g);
		}

		applyLight();
		ui.render(g);
		
		/* final */
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH*SCALE+10, HEIGHT*SCALE+10, null);
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 24));
		g.drawString("Ammo: " + Game.player.ammo, 550, 28);
		g.drawString("HP", 45, 24);
		g.drawString(Game.player.hp +"/"+ Player.maxHp,64, 52);
		g.drawString("Score: "+ Enemy.score , 550, 52);
		if (gameState =="GAMEOVER") {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0,0,0,100));
			g2.fillRect(0, 0, WIDTH*SCALE+10, HEIGHT*SCALE+10);
			g.setColor(Color.white);
			g.setFont(new Font("arial", Font.BOLD, 50));
			g.drawString("GAME OVER",(WIDTH*SCALE)/2 - 150, (HEIGHT*SCALE)/2 + 50);
			g.setFont(new Font("arial", Font.BOLD, 20));
			if (showMsgGameOver) {
				g.drawString("Press Enter to restart",(WIDTH*SCALE)/2 - 105, (HEIGHT*SCALE)/2 + 90);
			}
		} else if (gameState =="MENU") {
			menu.render(g);
		}
		/*
		double angleMouse = Math.atan2(my-200+20,mx-200+25); // radians
		Graphics2D g2 = (Graphics2D) g;
		g2.rotate(angleMouse, 200+20, 200+20); //radians
		g.setColor(Color.red);
		g.fillRect(200, 200, 40, 40);
		*/
		bs.show();
	}
	
	//game run bases on fps
	@Override
	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		@SuppressWarnings("unused")
		int fps = 0;
		double timer = System.currentTimeMillis();
		
		while (isRunning) {
			long now = System.nanoTime();
			delta += (now-lastTime) / ns;
			lastTime = now;
			if (delta >= 1) {
				tick();
				render();
				fps++;
				delta--;
			}
			if (System.currentTimeMillis() - timer >= 1000) {
				//System.out.println("FPS: " + fps);
				fps=0;
				timer += 1000;
			}
		}
		stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || 
				e.getKeyCode() == KeyEvent.VK_D) {
			player.right=true;
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || 
				e.getKeyCode() == KeyEvent.VK_A) {
			player.left=true;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP || 
				e.getKeyCode() == KeyEvent.VK_W) {
			player.up=true;
			if (gameState == "MENU") 
				menu.up=true;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || 
				e.getKeyCode() == KeyEvent.VK_S) {
			player.down=true;
			if (gameState == "MENU") 
				menu.down=true;
		}
		//shooting
		if (e.getKeyCode() == KeyEvent.VK_SPACE) 
			player.shooting=true;
		
		//menu enter
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.restartGame = true;
			if (gameState == "MENU")
				menu.enter=true;
		}
		//menu key
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				gameState = "MENU";
				Menu.pause=true;
		}
		//fake jump
		if (e.getKeyCode() == KeyEvent.VK_E) {
			player.jump = true;
		}
		//save
		if (e.getKeyCode() == KeyEvent.VK_P) {
			if (gameState == "NORMAL")
				this.saveGame = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || 
				e.getKeyCode() == KeyEvent.VK_D) {
			player.right=false;
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || 
				e.getKeyCode() == KeyEvent.VK_A) {
			player.left=false;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_UP || 
				e.getKeyCode() == KeyEvent.VK_W) {
			player.up=false;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || 
				e.getKeyCode() == KeyEvent.VK_S) {
			player.down=false;
		}
		if (e.getKeyCode() == KeyEvent.VK_SPACE) 
			player.shooting=false;
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseShoot=true;		
		player.mx = ((e.getX()/3) - 10);
		player.my = ((e.getY()/3) - 10);
		//System.out.println(player.mx + " " + player.my);
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.mx = e.getX();
		this.my = e.getY();
	}
}
