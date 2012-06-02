package graphics;

import main.CubeOrchestraScene;

import org.mt4j.MTApplication;

public final class Colors {
	
	static final MTApplication p5 = CubeOrchestraScene.app;
	public static final int blue = p5.color(100, 100, 255); 
	public static final int green = p5.color(100, 255, 100);
	public static final int red = p5.color(255, 100, 100); 
	public static final int yellow = p5.color(255, 255, 0);
	
	public static final int pastelYellow = p5.color(254,249,157);
	public static final int skyBlue = p5.color(141,211,247);
	public static final int black = p5.color(30,30,30);
	public static final int gray = p5.color(90,90,90);
	public static final int brightGray = p5.color(240,240,240);
}
