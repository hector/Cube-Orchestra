package effects;

import java.util.ArrayList;

import graphics.Drawable;

import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

public class EffectsBar extends Drawable {

	MTColor[] colors;
	ArrayList<Effect> effects;

	public EffectsBar() {
		super();
		initColors();
		setPickable(false);
		unregisterAllInputProcessors();
		effects = new ArrayList<Effect>();
		// Bar
		float width = p5.width / 3f;
		float height = p5.height / 10f;
		MTRoundRectangle bar = new MTRoundRectangle(p5.width / 2 - width / 2,
				0, 0, width, height, width / 15, height / 4, p5);
		bar.setFillColor(new MTColor(50, 50, 50));
		bar.setStrokeColor(new MTColor(150, 150, 150));
		bar.setStrokeWeight(2);
		bar.unregisterAllInputProcessors();
		bar.setPickable(false);
		addChild(bar);
		// Effects
		Effect effect;
		Vector3D center;
		for (int i = 0; i < colors.length; i++) {
			center = new Vector3D(p5.width / 2 - width / 2 + width * (i + 1)
					/ 6, height / 2, 0);
			effect = new Effect(p5, center, height / 2.5f, colors[i], i);
			effects.add(effect);
			addChild(effect);
		}
	}
	
	public ArrayList<Effect> getEffects() {
		return effects;
	}

	private void initColors() {
		colors = new MTColor[5];
		colors[0] = new MTColor(100, 100, 255); // blue
		colors[1] = new MTColor(100, 255, 100); // green
		colors[2] = new MTColor(255, 100, 100); // red
		colors[3] = new MTColor(255, 255, 0); // yellow
		colors[4] = new MTColor(0, 255, 255);
//		colors[5] = new MTColor(255, 0, 255);
	}

}
