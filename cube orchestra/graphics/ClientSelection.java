package graphics;

import instruments.Instrument;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;

public class ClientSelection extends Drawable {
	
	int[] colors;
	int startFrameMillis, time;
	HashMap<String, Instrument> devices;
	List<String> clients;	
	
	public ClientSelection(List<String> clients, HashMap<String, Instrument> devices) {
		this.devices = devices;
		this.clients = clients;
		initColors();
		time = 0;
		alpha = 75;
	}

	// @Override
	public void draw() {
		try {
			startFrameMillis = p5.millis();
			// Draw client coloured circles
			Instrument inst;
			float size;
			p5.stroke(255, alpha);
			for (int i = 0; i < clients.size(); i++) {
				inst = devices.get(clients.get(i));
				p5.fill(colors[i], alpha);
				size = inst.getSize() * inst.getScale() * 2;
				p5.ellipse(inst.getPosition().x, inst.getPosition().y, size, size);
			}
			time = p5.millis();
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
