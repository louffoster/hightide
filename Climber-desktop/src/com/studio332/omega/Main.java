package com.studio332.omega;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Climber";
		cfg.useGL20 = true;
		cfg.width = 1024;
		cfg.height = 589;
		
		new LwjglApplication(new Climber(new WebLinkerStub()), cfg);
	}
}
