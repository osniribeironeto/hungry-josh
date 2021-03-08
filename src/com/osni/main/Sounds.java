package com.osni.main;

import java.applet.Applet;
import java.applet.AudioClip;

public class Sounds {
	
	private AudioClip clip;
	
	public static final Sounds backSound = new Sounds("/music.wav");
	public static final Sounds hitSound = new Sounds("/Hit_Hurt7.wav");
	public static final Sounds bombSound = new Sounds("/Explosion3.wav");
	public static final Sounds lifeSound = new Sounds("/lifepack.wav");
	public static final Sounds ammoSound = new Sounds("/ammo.wav");
	public static final Sounds gunSound = new Sounds("/bomb.wav");
	
	
	private Sounds (String name) {
		try {
			clip = Applet.newAudioClip(Sounds.class.getResource(name));
		} catch (Throwable e) {}
	}
	
	public void play () {
		try {
			new Thread () {
				public void run () {
					clip.play();
				}
			}.start();
		}catch (Throwable e) {}
	}
	
	public void loop () {
		try {
			new Thread () {
				public void run () {
					clip.loop();
				}
			}.start();
		}catch (Throwable e) {}
	}
}
