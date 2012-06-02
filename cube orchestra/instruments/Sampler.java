package instruments;

import graphics.Colors;

public class Sampler extends DrumMachine {
  
  public Sampler(int size) throws Exception {
    super(size, Colors.red);
//    Control tempoFader = layout.getControl("/1/fader1");
//    tempoFader.setValues((float)0.5);
//    layout.getControl("/1/fader1").map(p5, "globalTempo", this);
  }  

}
