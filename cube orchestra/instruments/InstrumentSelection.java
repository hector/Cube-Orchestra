package instruments;

import graphics.Drawable;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;

public class InstrumentSelection extends Drawable {
	
	int[] colors;
	HashMap<String, Instrument> devices;
	List<String> clients;	
	
	public InstrumentSelection(List<String> clients, HashMap<String, Instrument> devices) {
		this.devices = devices;
		this.clients = clients;
		initColors();
		alpha = 75;
		setPickable(false);
	}

	// @Override
	public void draw() {
		try {
			// Draw client coloured circles
			Instrument inst;
			float size;
//			p5.stroke(255, alpha);
			for (int i = 0; i < clients.size(); i++) {
				inst = devices.get(clients.get(i));
//				p5.fill(colors[i], alpha);
				p5.noFill();
				p5.stroke(colors[i], 255);
				size = inst.getSize() * inst.getScale() * 1.5f;
				p5.ellipse(inst.getPosition().x, inst.getPosition().y, size, size);
//				p5.rect(inst.getPosition().x - size/2, inst.getPosition().y - size/2, size, size);
			}
		} catch (ConcurrentModificationException cme) {
		}
	}
	
	private void initColors() {
		colors = new int[4];
		colors[0] = p5.color(100, 100, 255); // blue
		colors[1] = p5.color(100, 255, 100); // green
		colors[2] = p5.color(255, 100, 100); // red
		colors[3] = p5.color(255, 255, 0); // yellow
	}	
	
}
