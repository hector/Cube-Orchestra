package instruments;

import graphics.Colors;
import OSC.Control;

public class Synthesizer extends Instrument {

  public Synthesizer(int size) throws Exception {
    super(size, Colors.skyBlue);
    Control octaveFader = layout.getControl("/1/fader1");
    octaveFader.setValues((float)0.5);
  }

}
