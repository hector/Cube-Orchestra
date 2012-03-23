package effects;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import graphics.Drawable;

import main.CubeOrchestraScene;

import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.shapes.MTRectangle.PositionAnchor;
import org.mt4j.components.visibleComponents.widgets.MTSlider;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

public class TempoBar extends Drawable {

	MTSlider slider;
	MTTextArea textField;
	float bpm;

	public TempoBar(float bpm) {
		super();
		this.bpm = bpm;
		// Slider
		float width = p5.width / 4f;
		float height = p5.height / 15f;
		slider = new MTSlider(0, 0, width, height, 5, 240, p5);
		slider.setValue(bpm);
		slider.setFillColor(new MTColor(50, 50, 50, 100));
		slider.setStrokeColor(new MTColor(150, 150, 150, 100));
		slider.setStrokeWeight(2);
		slider.addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
				String property = propertyChangeEvent.getPropertyName();
				if ("value".equals(property)) {
					float tempo = ((Float) propertyChangeEvent.getNewValue())
							.floatValue();
					CubeOrchestraScene.scene.globalTempo(tempo);
				}
			}
		});
		addChild(slider);
		// Text
		textField = new MTTextArea(p5, FontManager
				.getInstance().createFont(p5, "arial.ttf", 36,
						new MTColor(50, 50, 50, 150), // Font fill color
						new MTColor(50, 50, 50, 150))); // Font outline color
		textField.setNoFill(true);
		textField.setNoStroke(true);
		textField.setAnchor(PositionAnchor.UPPER_LEFT);
		setTextBPM();
		textField.unregisterAllInputProcessors();
		textField.setPickable(false);
		addChild(textField);
		textField.setPositionGlobal(new Vector3D(0, height, 0));
	}

	public void setBPM(float bpm) {
		this.bpm = bpm;
		setTextBPM();
	}
	
	public void setTextBPM() {
		DecimalFormat format = new DecimalFormat("###");
		textField.setText(String.valueOf(format.format(bpm)) + " BPM");
	}

}
