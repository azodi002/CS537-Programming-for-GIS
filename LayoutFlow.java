// In the Flow layout, preferred size is honored, and components are added 
// west to east, and then north to south
import java.awt.*;
import java.awt.event.*;
public class LayoutFlow {
  public static void main(String [] aa) {
    Frame f = new Frame();
    f.setTitle("Flow Layout");
    f.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent we){System.exit(0);} } );
    f.setBounds(50,50,100,200);
    f.setLayout(new FlowLayout());
    Button b1 = new Button("norte"); f.add(b1);
    Button b2 = new Button("sud"); f.add(b2);
    Button b3 = new Button("este"); f.add(b3);
    Button b4 = new Button("oeste"); f.add(b4);
    Button b5 = new Button("centro"); f.add(b5);
    f.show();
  }
}
