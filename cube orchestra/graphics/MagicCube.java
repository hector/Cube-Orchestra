package graphics;

import org.mt4j.components.bounds.BoundingSphere;
import org.mt4j.util.math.Vector3D;

import processing.core.PVector;

public class MagicCube extends Cube {

	Pyramid[] pyramids;
	int[] colors;

	public MagicCube(float size) {
		super(size);
		colors = new int[6];
		createColors();
		pyramids = new Pyramid[6];
		createPyramids();
		setBoundingSphere();
	}

	private void setBoundingSphere() {
		BoundingSphere sphere = new BoundingSphere(this, pVector2Vector3D(vertices));
		setBounds(sphere);
	}

	private void createColors() {
		colors[0] = p5.color(64, 224, 208); // Turquoise
		colors[1] = p5.color(191, 236, 230); // Purple lavender
		colors[2] = p5.color(255, 170, 132); // Orange light salmon
		colors[3] = p5.color(255, 202, 213); // Pink
		colors[4] = p5.color(199, 255, 211); // Green mint
		colors[5] = p5.color(255, 255, 204); // Light yellow
	}

	private void createPyramids() {
		for (int i = 0; i < pyramids.length; i++) {
			pyramids[i] = new Pyramid((Square3D) polygons[i]);
			pyramids[i].visible(false);
			pyramids[i].setColor(colors[i]);
		}
	}

	public void pyramidVisible(boolean visible, int pyramid) {
		pyramids[pyramid].visible(visible);
	}

	public void pyramidHeight(float percentage, int pyramid) {
		Pyramid p = pyramids[pyramid];
		if (p != null)
			p.setHeightPercentage(percentage);
	}

	@Override
	public void selfDraw() {
		if (!visible)
			return;
		super.selfDraw();
		for (Pyramid pyramid : pyramids) {
			if (pyramid != null)
				pyramid.selfDraw();
		}
	}

	@Override
	public void setAlpha(float alpha) {
		super.setAlpha(alpha);
		for (Pyramid pyramid : pyramids) {
			pyramid.setAlpha(alpha);
		}
	}

}
