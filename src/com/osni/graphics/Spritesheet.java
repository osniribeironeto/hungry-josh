package com.osni.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Spritesheet {

	private BufferedImage spriteOne; // utilizado para carregar a imagem
	
	public Spritesheet (String path) {
		//The sprite (buffered image) will read the resource (folder res) from the class using the path passed in the parameter
		try {
			spriteOne = ImageIO.read(getClass().getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public BufferedImage getSprite(int x, int y, int width, int height){
		return spriteOne.getSubimage(x, y, width, height);
	}
}
