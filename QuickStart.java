import javax.swing.JFrame;
import java.awt.event.*;
import java.awt.*;

import com.esri.mo2.ui.bean.Map;
import com.esri.mo2.ui.tb.ZoomPanToolBar;
import com.esri.mo2.ui.bean.Layer;
import com.esri.mo2.ui.bean.Toc;

public class QuickStart extends JFrame {
  Map map = new Map();
  Layer layer = new Layer();
  Layer layer2 = new Layer();
  Toc toc = new Toc();
  String s1 = "C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\States.shp";
  String s2 = "C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\capitals.shp";
  ZoomPanToolBar zptb = new ZoomPanToolBar();
  public QuickStart() {
    //add a title to the window
    super("Quick Start");
    //set the size
    this.setBounds(50,50,700, 500);
    //add the map to the frame
    zptb.setMap(map);
    toc.setMap(map);
    getContentPane().add(map, BorderLayout.CENTER);
    getContentPane().add(zptb,BorderLayout.NORTH);
    //add a shape file to the map
    addShapefileToMap(layer,s1);
    addShapefileToMap(layer2,s2);
    //add a Toc

    getContentPane().add(toc, BorderLayout.WEST);
  }
    private void addShapefileToMap(Layer layer,String s) {
    String datapath = s; //"C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\States.shp";
    layer.setDataset("0;"+datapath);
    map.add(layer);
  }

  public static void main(String[] args) {
    QuickStart qstart = new QuickStart();
    qstart.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            System.out.println("Thanks, Quick Start exits");
            System.exit(0);
        }
    });
    qstart.setVisible(true);
  }
}
