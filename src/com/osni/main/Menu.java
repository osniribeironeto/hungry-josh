package com.osni.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.osni.world.World;

public class Menu {
	public String[] options = {"New Game", "Load Game", "Exit"};
	
	public int currOption = 0;
	public int maxOption = options.length - 1;
	public boolean up, down,enter;
	public static boolean pause = false;
	public static boolean saveExists = false;
	public static boolean saveGame = false;
	
	public void tick () {
		File file = new File("save.txt");
		
		if (file.exists()) {
			saveExists = true;
		} else {
			saveExists = false;
		}
		
		if (up) {
			up = false;
			currOption--;
			if (currOption < 0)
				currOption = maxOption;
		} else if (down) {
			down = false;
			currOption++;
			if (currOption > maxOption)
				currOption = 0;
		}
		if  (enter) {
			enter = false;
			if (options[currOption] == "New Game") {
				Game.gameState="NORMAL";
				pause = false;
				file = new File ("save.txt");
				file.delete();
			} else if (options[currOption] == "Resume") {
				Game.gameState="NORMAL";
				pause = false;
			} else if (options[currOption] == "Exit") {
				System.exit(1);
			} else if (options[currOption] == "Load Game") {
				file = new File("save.txt");
				if (file.exists()) {
					String saver = LoadGame(20);//encoding
					applySave(saver);
				}
			}
		}
	}
	
	/////////SAVE
	public static void SaveGame (String[] value1, int[] value2, int encode) {
		BufferedWriter write = null;
		try {
			write = new BufferedWriter(new FileWriter ("save.txt")); //create file to write
		} catch (IOException e) {e.printStackTrace();}
		
		for (int i = 0; i < value1.length; i++) {
			String curr = value1[i];
			curr+=":";
			char[] value = Integer.toString(value2[i]).toCharArray();
			for (int j = 0 ; j < value.length; j++) {
				value[j]+=encode; //crypto
				curr+=value[j];
			}
			try {
				write.write(curr); //write to a file
				if (i < value1.length)
					write.newLine();
			} catch (IOException e) {}
		}
		
		try {
			write.flush();
			write.close();
		} catch (IOException e) {}
	}
	
	//////////LOAD
	public static String LoadGame(int encode) {
		String line = "";
		File file = new File("save.txt");
		if (file.exists()){
			try {
				String singleLine = null;
				BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
				try {
					while ((singleLine = reader.readLine()) != null) {
						String[] transition = singleLine.split(":");
						char[] value = transition[1].toCharArray();
						transition[1] = "";
						for (int i = 0; i < value.length; i++) {
							value[i] -= encode;
							transition[1]+=value[i];
						}
						line += transition[0];
						line += ":";
						line += transition[1];
						line += "/";
					}
				} catch (IOException e) {}
			} catch (FileNotFoundException e) {}
		}
		return line;
	}
	
	///// applying save on load game
	public static void applySave(String str) {
		String[] split = str.split("/");
		for (int i = 0; i < split.length; i++) {
			String[] split2 = split[i].split(":");
			switch (split2[0]) {
				case "map":
					World.restartGame("map" + split2[1] + ".png");
					Game.gameState="NORMAL";
					pause = false;
					break;
				case "hp":
					Game.player.hp = Integer.parseInt(split2[1]);
					break;
			}
		}
	}
	
	public void render (Graphics g) {
		g.setColor(new Color(0,0,0,100));
		g.fillRect(0, 0, Game.WIDTH*Game.SCALE+10, Game.HEIGHT*Game.SCALE+10);
		
		g.setColor(Color.CYAN);
		
		g.setFont(new Font("arial", Font.BOLD, 50));
		g.drawString("Hungry Josh", 200, 150);
		
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 30));
		
		if (pause) {
			g.drawString("Resume", 260, 230);
			g.drawString("Load Game", 260, 280);
			g.drawString("Exit", 260, 330);
			if (options[currOption] == "New Game") 
				g.drawString(">", 235, 230);
			 else if (options[currOption] == "Load Game") 
				g.drawString(">", 235, 280);
			 else if (options[currOption] == "Exit") 
				g.drawString(">", 235, 330);
		} else {
				g.drawString("New Game", 260, 230);
				g.drawString("Load Game", 260, 280);
				g.drawString("Exit", 260, 330);
				if (options[currOption] == "New Game") 
					g.drawString(">", 235, 230);
				else if (options[currOption] == "Load Game")
					g.drawString(">", 235, 280);
				else if (options[currOption] == "Exit")
					g.drawString(">", 235, 330);
			}
		}
}
