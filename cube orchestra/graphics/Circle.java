package graphics;

import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

public class Circle extends MTEllipse {

	static float alpha = 200;
	protected MTColor color;
	protected float radius;

	public Circle(PApplet pApplet, Vector3D centerPoint, float radius,
			MTColor color, int segments) {
		super(pApplet, centerPoint, radius, radius, segments);
		this.color = color;
		this.radius = radius;
		color.setAlpha(alpha);
		setFillColor(color);
		setStrokeColor(new MTColor(color.getR() + 150, color.getG() + 150,
				color.getB() + 150, color.getAlpha()));
		setStrokeWeight(2);
	}
	
	public Circle(PApplet pApplet, Vector3D centerPoint, float radius,
			MTColor color) {
		this(pApplet, centerPoint, radius, color, 45);
	}
	
	public MTColor getColor() {
		return color;
	}

}
