package main;

import org.mt4j.MTApplication;
import org.mt4j.input.inputSources.TuioInputSource;

public class StartCubeOrchestra extends MTApplication {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		initialize();
	}

	@Override
	public void startUp() {
		getInputManager().registerInputSource(new TuioInputSource(this));
		addScene(new CubeOrchestraScene(this, "Cube Orchestra Scene"));
	}
}
