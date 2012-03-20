package main;

import org.mt4j.MTApplication;

public class StartCubeOrchestra extends MTApplication {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		initialize();
	}

	@Override
	public void startUp() {
		addScene(new CubeOrchestraScene(this, "Cube Orchestra Scene"));
	}
}
