// In the Border layout, the NORTH and SOUTH areas go all the way across, and
// fill along the horiz axis;  the EAST and WEST areas fill along the vert.
// axis.  The center is whatever is left, and fills both ways.
// Preferred size determines the N and S ht, and the E and W width
import java.awt.*;
import java.awt.event.*;
public class LayoutBorder {
  public static void main(String [] aa) {
    Frame f = new Frame();
    f.setTitle("Border Layout");
    f.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent we){System.exit(0);} } );
    f.setBounds(50,50,300,300);
    f.setLayout(new BorderLayout());
    Button b1 = new Button("norte"); f.add(BorderLayout.NORTH,b1);
    Button b2 = new Button("sud"); f.add("South",b2); //
        //BorderLayout.SOUTH is a constant with value "South"
    Button b3 = new Button("este"); f.add(BorderLayout.EAST,b3);
    Button b4 = new Button("oeste"); f.add(BorderLayout.WEST,b4);
    Button b5 = new Button("centro");
    Color color = new Color(250,250,0);
    b5.setBackground(color); f.add(BorderLayout.CENTER,b5);
    f.show();
  }
}
