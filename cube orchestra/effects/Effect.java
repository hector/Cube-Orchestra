package effects;

import instruments.Instrument;

import java.util.ArrayList;

import graphics.Circle;
import graphics.Drawable;
import main.CubeOrchestraScene;

import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import oscP5.OscMessage;

import processing.core.PApplet;
import processing.core.PVector;

public class Effect extends Circle {

	protected int id;
	
	public Effect(PApplet pApplet, Vector3D centerPoint, float radius,
			MTColor color, int id) {
		super(pApplet, centerPoint, radius, color);
		this.id = id;
		// Register input processors
		unregisterAllInputProcessors();
		removeAllGestureEventListeners();
		registerInputProcessor(new DragProcessor(this.getRenderer()));
		addGestureListener(DragProcessor.class, new EffectDragAction());
		registerInputProcessor(new ScaleProcessor(this.getRenderer()));
		addGestureListener(ScaleProcessor.class, new EffectScaleAction(this, 1, 10));
	}
	
	public int getId() {
		return id;
	}
	
	// Check which effects apply to which instruments
	public void checkEnabledInstruments() {
		ArrayList<Instrument> instruments = CubeOrchestraScene.scene.getInstruments();
		for(Instrument instrument : instruments) {
			instrument.setFxEnabled(this);
		}
	}

	// Send the amount of effect given its size (radius)
	public void setAmount() {
		OscMessage msg = new OscMessage("/effect/" + String.valueOf(id));
		msg.add(getAmount());
		CubeOrchestraScene.scene.oscSendPD(msg);
	}	
	
	// returns the amount of effect given the circle size (radius)
	public float getAmount() {
		float max = CubeOrchestraScene.app.height; // height is always smaller than width
		float amount = getRadius() / (max / 2f); // amount is 1 when circle has the full height
		if (amount > 1f) amount = 1f; // avoid values greater than 1
		return amount;
	}
	
	// Returns the global radius value (scaled)
	public float getRadius() {
		float scale = getLocalMatrix().getScale().x;
		return radius * scale;
	}
	
	@Override
	public void translateGlobal(Vector3D directionVect) {
		Vector3D newPosition = new Vector3D(directionVect);
		if(Drawable.pointInsideBoundaries(newPosition.addLocal(getCenterPointGlobal()))) {
			super.translateGlobal(directionVect);
		}		
	}	

}
