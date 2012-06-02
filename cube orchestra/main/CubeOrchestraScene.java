package main;

import oscP5.*;
import netP5.*;
import processing.core.*;
import instruments.*;
import effects.Effect;
import effects.EffectsBar;
import effects.TempoBar;
import graphics.*;

import java.util.*;

import org.mt4j.MTApplication;
import org.mt4j.components.MTCanvas;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.input.inputSources.TuioInputSource;
import org.mt4j.sceneManagement.AbstractScene;

public class CubeOrchestraScene extends AbstractScene {

	public static MTApplication app;
	public static CubeOrchestraScene scene;
	static int listeningPort = 9000;
	static int broadcastPort = 12000;
	int instrumentSize;
	float tempo, instrumentScale;
	Gradient grad;
	InstrumentSelection instrumentSelection;
	EffectsBar effectsBar;
	TempoBar tempoBar;
	NetAddress pureData;
	OscP5 oscP5 = null;
	ArrayList<Instrument> instruments;
	ArrayList<Synthesizer> synthesizers;
	ArrayList<DrumMachine> drumMachines;
	HashMap<String, Instrument> devices;
	List<String> clients;
	MTCanvas canvas;

	public CubeOrchestraScene(MTApplication app, String name) {
		super(app, name);
		this.app = app;
		this.scene = this;

		// Instantiate variables
		if (oscP5 == null)
			oscP5 = new OscP5(this, listeningPort, OscP5.UDP);
		pureData = new NetAddress("127.0.0.1", broadcastPort);
		devices = new HashMap<String, Instrument>();
		clients = new ArrayList<String>(4);
		instruments = new ArrayList<Instrument>(8);
		synthesizers = new ArrayList<Synthesizer>(4);
		drumMachines = new ArrayList<DrumMachine>(4);
		grad = new Gradient();
		instrumentSelection = new InstrumentSelection(clients, devices);
		effectsBar = new EffectsBar();
		// Set variables values
		tempo = 105;
		tempoBar = new TempoBar(tempo);
		instrumentSize = app.height / 3;
		instrumentScale = 1;
		canvas = this.getCanvas();

		// Set global canvas properties
		// app.frameRate(60);
		// app.hint(mtApplication.ENABLE_OPENGL_4X_SMOOTH);
		// app.noCursor();
		app.lights();

		// Add components to canvas
		canvas.addChild(grad);
		canvas.addChild(effectsBar);
		canvas.addChild(instrumentSelection);
		canvas.addChild(tempoBar);
//		try {
//			createSynthesizer("qweqwe");
//			synthesizers.get(0).sequencer(true);
//			createDrumMachine("asdsad");
//			createDrumMachine("asdsad2");
//		} catch (Exception e) {
//		}

		// Show touches
//		this.registerGlobalInputProcessor(new CursorTracer(app, this));
	}	
	
	@Override
	public void drawAndUpdate(PGraphics graphics, long timeDelta) { 
		try {
			super.drawAndUpdate(graphics, timeDelta);
		} catch (ConcurrentModificationException e) {}
	}	

	@Override
	public void init() {
	}

	@Override
	public void shutDown() {
	}

	public void oscEvent(OscMessage msg) throws Exception {
		try {
			String ip = msg.netAddress().address();
			if ("127.0.0.1".equals(ip)) {
				Instrument instr = instrumentFromPattern(msg);
				instr.bump();
			} else {
				// Handle instrument creation/swaping
				if (msg.checkAddrPattern("/1/push12")
						&& msg.get(0).floatValue() == 1.0) {
					createSynthesizer(ip);
				} else if (msg.checkAddrPattern("/1/push11")
						&& msg.get(0).floatValue() == 1.0) {
					createDrumMachine(ip);
				} else if (msg.checkAddrPattern("/1/push10")
						&& msg.get(0).floatValue() == 1.0) {
					changeInstrument(ip);
				}

				// Send message to the corresponding instrument
				Instrument instrument = devices.get(ip);
				if (instrument != null) {
					instrument.oscEvent(msg);
					// Forward all messages to pure data
					msg.setAddrPattern(ip2Pattern(ip) + msg.addrPattern());
					oscSendPD(msg);
				}
			}
		} catch (ConcurrentModificationException cme) {
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void createSynthesizer(String ip) throws Exception {
		if (synthesizers.size() >= 4)
			return;
		Synthesizer synth = new Synthesizer(instrumentSize);
		addInstrument(ip, synth);
		synthesizers.add(synth);
	}

	private void createDrumMachine(String ip) throws Exception {
		if (drumMachines.size() >= 4)
			return;
		DrumMachine drums = null;
		if (drumMachines.size() == 1) drums = new Sampler(instrumentSize);
		else drums = new DrumMachine(instrumentSize);
		addInstrument(ip, drums);
		drumMachines.add(drums);
	}

	private void addInstrument(String ip, Instrument instrument) {
		instrument.setPosition(emptyPosition());
		instrument.setBPM(tempo);
		devices.put(ip, instrument);
		addClient(ip);
		instruments.add(instrument);
		scaleInstruments();
		canvas.addChild(instrument);
		sendLayout(instrument, ip);
	}

	private PVector emptyPosition() {
		PVector position = new PVector(app.width / 2, app.height / 2, 0);
		float margin = instrumentSize * instrumentScale;
		float mWidth = app.width - 2 * margin;
		float mHeight = app.height - 2 * margin;
		float distance;
		boolean found = true;
		int i = 0;
		while (i < 100) {
			position = new PVector(mWidth * (float) Math.random() + margin,
					mHeight * (float) Math.random() + margin, 0);
			for (Instrument instr : instruments) {
				distance = (float) (instr.getSize() * instr.getScale() * 1.5);
				if (position.dist(instr.getPosition()) < distance) {
					found = false;
					break;
				}
			}
			if (found)
				i = 100;
			else
				found = true;
			i++;
		}
		return position;
	}

	private void scaleInstruments() {
		instrumentScale *= (float) 0.9;
		for (Instrument instr : instruments)
			instr.setScale(instrumentScale);
	}

	private void changeInstrument(String ip) {
		Instrument instrument = devices.get(ip);
		int size = instruments.size();
		int pos = (instrument == null) ? size : instruments.indexOf(instrument);
		int index = pos + 1;
		while (index != pos) {
			if (index >= size)
				index = 0;
			else {
				instrument = instruments.get(index);
				if (!devices.containsValue(instrument)) {
					devices.put(ip, instrument);
					addClient(ip);
					sendLayout(instrument, ip);
					index = pos;
				} else
					index += 1;
			}
		}
	}

	private void addClient(String ip) {
		if (!clients.contains(ip))
			clients.add(ip);
	}

	private String ip2Pattern(String ip) {
		Instrument instr = devices.get(ip);
		return instrument2Pattern(instr);
	}

	private String instrument2Pattern(Instrument instrument) {
		int num = drumMachines.indexOf(instrument) + 1;
		if (num == 0)
			num = synthesizers.indexOf(instrument) + 5;
		return "/" + num;
	}

	private Instrument instrumentFromPattern(OscMessage msg) {
		String pattern = msg.addrPattern();
		int instrNum = new Integer(pattern.substring(1, 2));
		if (instrNum > 4)
			return synthesizers.get(instrNum - 5);
		else
			return drumMachines.get(instrNum - 1);
	}
	
	public void globalTempo(float bpm) {
		this.tempo = bpm; // Save to global tempo
		tempoBar.setBPM(bpm); // Update tempoBar
		// Send new tempo to PD
		OscMessage msg = new OscMessage("/tempo");
		msg.add(bpm);
		oscSendPD(msg);
		// Set tempo for all instruments (important for rotation speed)
		for (Instrument instr : instruments) {
			instr.setBPM(bpm); // Set bpm to all instruments
		}		
	}

	// Not used
	public void globalTempo(float tempo, DrumMachine drums) {
		float bpm = tempo * 240; // Tempo from touchOSC slider to BPM
		globalTempo(bpm);
		for (Instrument instr : instruments) {
//			instr.setBPM(bpm); // Set bpm to all instruments
			if (instr.getClass().equals(DrumMachine.class) && instr != drums) {
				instr.getLayout().getControl("/1/fader1").setValues(tempo); 
			}
		}
		OscMessage msg;
		for (String ip : clients) {
			Instrument instr = devices.get(ip);
			if (instr.getClass().equals(DrumMachine.class) && instr != drums) {
				msg = instr.getLayout().getControl("/1/fader1").oscMessage();
				oscSend(msg, ip); // Send new tempo to clients with drumMachines
			}
		}
	}

	private void sendLayout(Instrument instrument, String ip) {
		ArrayList<OscMessage> msgs = instrument.oscLayoutMessages();
		oscSend(msgs, ip);
	}

	public void oscSend(OscMessage msg, NetAddress dest) {
		oscP5.send(msg, dest);
	}

	public void oscSend(OscMessage msg, String ip) {
		NetAddress dest = new NetAddress(ip, broadcastPort);
		oscSend(msg, dest);
	}

	public void oscSendPD(OscMessage msg) {
		oscP5.send(msg, pureData);
	}

	public void oscSendInstrumentPD(Instrument instrument, OscMessage msg) {
		msg.setAddrPattern(instrument2Pattern(instrument) + msg.addrPattern());
		oscSendPD(msg);
	}

	public void oscSend(ArrayList<OscMessage> msgs, String ip) {
		NetAddress dest = new NetAddress(ip, broadcastPort);
		// OscBundle bundle = new OscBundle();
		try {
			for (OscMessage msg : msgs) {
				oscSend(msg, dest);
				Thread.sleep(1);
			}
		} catch (Exception e) {
		}
		// bundle.add(msg);
		// oscP5.send(bundle, dest);
	}

	public ArrayList<Instrument> getInstruments() {
		return instruments;
	}
	
	public ArrayList<Effect> getEffects() {
		return effectsBar.getEffects();
	}

}
