package graphics;

import java.util.ConcurrentModificationException;

import main.CubeOrchestraScene;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.util.math.Vector3D;

import processing.core.*;

public abstract class Drawable extends MTComponent {

	static float PI = PConstants.PI;
	static float TWO_PI = PConstants.TWO_PI;
	int color, strokeColor;
	PVector position; // center of the drawable
	protected float alpha, angle, angleY, bpm, scale, tempScale;
	boolean sequencer, visible;
	protected MTApplication p5;

	public Drawable() {
		super(CubeOrchestraScene.app);
		this.p5 = CubeOrchestraScene.app;
		color = 255;
		alpha = 255;
		position = new PVector(0, 0, 0);
		angle = 0;
		angleY = 0;
		bpm = 0;
		scale = 1;
		tempScale = 1;
		sequencer = false;
		visible = true;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
		p5.colorMode(PConstants.HSB);
		strokeColor = p5.color(p5.hue(color), p5.saturation(color) + 50,
				p5.brightness(color) + 80);
		p5.colorMode(PConstants.RGB);
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getScale() {
		return scale;
	}

	public void setTempScale(float tempScale) {
		this.tempScale = tempScale;
	}

	public float getTempScale() {
		return tempScale;
	}

	public PVector getPosition() {
		return position;
	}

	public void setPosition(PVector position) {
		this.position = position;
	}

	public void setAngleY(float angle) {
		this.angleY = angle;
	}
	
	@Override
	public void drawComponent(PGraphics g) {
		try {
			super.drawComponent(g);
			draw();
		} catch (ConcurrentModificationException e) {}
	}

	public void draw() {
		if (!visible)
			return;
		p5.pushMatrix();
		translate();
		rotate();
		scale();
		selfDraw();
		p5.popMatrix();
	}

	// Place here the specific code for subclasses drawing
	protected void selfDraw() {
		if (!visible)
			return;
		p5.fill(color, alpha);
		p5.stroke(strokeColor, alpha);
	}

	protected void translate() {
		p5.translate(position.x, position.y, position.z);
	}

	protected void rotate() {
		if (bpm != 0 && sequencer) {
//			 angle += (CubeOrchestraScene.scene.spentTime() * bpm * PI ) / 60000 ; // 180 degrees
//			 per beat
			angle += 0.07f * (bpm / 120f);
			if (angle > TWO_PI)
				angle = 0;
		}
		p5.rotateX(angle);
		p5.rotateY(angleY);
	}

	protected void scale() {
		float s = scale;
		if (tempScale > scale) {
			s = tempScale;
			tempScale *= (float) 0.96;
		}
		p5.scale(s);
	}

	public void bump() {
		bump((float) 0.5);
	}

	// Percentage that the drawable increases (1 -> 100%)
	public void bump(float bumpScale) {
		tempScale = (float) (scale * (1 + bumpScale));
	}

	public void setBPM(float bpm) {
		this.bpm = bpm;
	}

	public void sequencer(boolean on) {
		this.sequencer = on;
	}

	public void visible(boolean visible) {
		this.visible = visible;
	}

	// FUNCTIONS FOR TESTING
	protected void rotateMouse() {
		p5.rotateX(radians(p5.mouseX) % (2 * PI));
		p5.rotateY(radians(p5.mouseY) % (2 * PI));
	}

	protected void translateMouse() {
		p5.translate(p5.mouseX, p5.mouseY, 0);
	}

	protected void translateCenter() {
		p5.translate(p5.width / 2f, p5.height / 2f, position.z);
	}

	// Math functions

	protected static float cos(float angle) {
		return PApplet.cos(angle);
	}

	protected static float sin(float angle) {
		return PApplet.sin(angle);
	}

	protected static float radians(float degrees) {
		return PApplet.radians(degrees);
	}

	protected static float radians(double degrees) {
		return PApplet.radians((float) degrees);
	}

	// Callback of the drag action
	@Override
	public void translateGlobal(Vector3D directionVect) {
		super.translateGlobal(directionVect);
		PVector newPosition = vector3D2PVector(directionVect);
		newPosition.add(getPosition());
		setPosition(newPosition);
	}
	
	public static Vector3D[] pVector2Vector3D(PVector[] pVectors) {
		Vector3D[] vectors3D = new Vector3D[pVectors.length];
		for(int i = 0; i < pVectors.length; i++) {
			vectors3D[i] = pVector2Vector3D(pVectors[i]);
		}
		return vectors3D;
	}	
	
	public static Vector3D pVector2Vector3D(PVector pVector) {
		return new Vector3D(pVector.x, pVector.y, pVector.z);
	}
	
	public static PVector vector3D2PVector(Vector3D vector3D) {
		return new PVector(vector3D.x, vector3D.y, vector3D.z);
	}	
}
