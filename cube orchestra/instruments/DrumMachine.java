package instruments;

import graphics.Colors;

public class DrumMachine extends Instrument {
  
  public DrumMachine(int size) throws Exception {
    super(size, Colors.gray);
  }
	
  public DrumMachine(int size, int color) throws Exception {
    super(size, color);
//    Control tempoFader = layout.getControl("/1/fader1");
//    tempoFader.setValues((float)0.5);
//    layout.getControl("/1/fader1").map(p5, "globalTempo", this);
  }  

}
