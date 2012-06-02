package graphics;

import instruments.Instrument;

import org.mt4j.components.bounds.BoundingSphere;
import org.mt4j.input.gestureAction.DefaultDragAction;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.util.math.Vector3D;

import processing.core.PVector;

public class MagicCube extends Cube {

	Instrument instrument;
	Pyramid[] pyramids;
	int[] colors;
	BoundingSphere sphere;
//	OrientedBoundingBox box;

	public MagicCube(float size, Instrument instrument) {
		super(size);
		this.instrument = instrument;
		colors = new int[6];
		createColors();
		pyramids = new Pyramid[6];
		createPyramids();
		// Pickable behavior
		setBoundingSphere();
		unregisterAllInputProcessors();
		removeAllGestureEventListeners();
		registerInputProcessor(new DragProcessor(this.getRenderer()));
		addGestureListener(DragProcessor.class, new DefaultDragAction());
	}
	
	public MagicCube(float size) {
		this(size, null);
	}	

	private void setBoundingSphere() {
		Vector3D center = pVector2Vector3D(getPosition());
		float radius = getSize() * getScale() * (float)Math.sqrt(2) / 2f;
		sphere = new BoundingSphere(this, center, radius);
		setBounds(sphere);
//		box = new OrientedBoundingBox(this, globalVertices());
//		setBounds(box);
	}
	
	// Return an approximation of global vertices (only scale and translation, not rotation)
	private Vector3D[] globalVertices() {
		Vector3D[] vectors = pVector2Vector3D(vertices);
		Vector3D position = pVector2Vector3D(getPosition());
		for(int i = 0; i < vectors.length; i++) {
			vectors[i] = vectors[i].getScaled(getScale()).getAdded(position);
		}
		return vectors;
	}

//	@Override
//	public void drawComponent(PGraphics g) {
//		super.drawComponent(g);
//		sphere.drawBounds(g);
//	}	
	
	@Override
	public void translateGlobal(Vector3D directionVect) {
//		super.translateGlobal(directionVect);
		PVector newPosition = vector3D2PVector(directionVect);
		newPosition.add(getPosition());
		if(pointInsideBoundaries(newPosition)) {
			setPosition(newPosition); // Avoid setter to avoid infinite recursion
		}		
	}
	
	@Override
	public void setPosition(PVector position) {
		super.setPosition(position);
		setBoundingSphere(); // recalculate for new position
		
		if (instrument != null) {
			instrument.sendPosition2PD(); // send position to PD for the panning FX
			instrument.setFXsEnabled();
		}
	}	
	
	@Override
	public void setScale(float scale) {
		super.setScale(scale);
		setBoundingSphere(); // recalculate for new scale
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
