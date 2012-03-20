package OSC;

public class Accelerometer extends ControlXY {
  
  public Accelerometer() {
    super("", "");
  }  
  
  @Override
  protected String oscStr() {
    return "/accxyz";
  } 

}
