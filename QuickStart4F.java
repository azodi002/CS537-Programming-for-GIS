// Just adds a simple AcetateLayer to place a line & points on the
// map. Mercifully this works if the drawing is done in Java terms,
// using frame coordinates (pixels).  Using an AcetateLayer has the
// advantage that an AcetateLayer is transparent, and this solves
// a problem that transparent layers in Java are somewhat tricky
// to do, e.g. a canvas can not be made transparent. In addition,
// the line is drawn in stages by using a thread.  The thread is
// wise and necessary.  In this  version, the acetate layers
// are removed, within the loop, so they do not accumulate
// and slow things down. The loop should work with two arbitrary
// points!  Those points are parameters to the thread code.

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.*;
import com.esri.mo2.ui.bean.*;
import com.esri.mo2.map.draw.*;
import com.esri.mo2.cs.geom.Point;
import com.esri.mo2.ui.tb.ZoomPanToolBar;
import com.esri.mo2.ui.tb.SelectionToolBar;

public class QuickStart4f extends JFrame {
  static Map map = new Map();
  Layer layer = new Layer();
  Layer layer2 = new Layer();
  com.esri.mo2.cs.geom.Envelope r;
  AcetateLayer acetLayer = null;
  Toc toc = new Toc();
  String s1 = "C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\States.shp";
  String s2 = "C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\capitals.shp";
  ZoomPanToolBar zptb = new ZoomPanToolBar();
  SelectionToolBar stb = new SelectionToolBar();
  JPanel myjp = new JPanel();
  JButton myjb = new JButton("select[1]");
  ActionListener actlis;
  TocAdapter mytocadapter;

  public QuickStart4f() {
    super("Quick Start");
    this.setBounds(80,80,750, 550);
    zptb.setMap(map);
    stb.setMap(map);
    //map.addLayerListener(toc);
    actlis = new ActionListener (){public void actionPerformed(ActionEvent ae){
		stb.setSelectedLayer(map.getLayer(1));
		System.out.println(map.getLayer(1).getName());//this is same
		    // as the name that appears in the table of contents
      }};
    toc.setMap(map);
    mytocadapter = new TocAdapter() {
		public void click(TocEvent e) {
		  System.out.println("aloha");
		  stb.setSelectedLayer((e.getLegend()).getLayer());
	  	}
    };
    toc.addTocListener(mytocadapter);
    myjb.addActionListener(actlis);
    myjp.add(zptb); myjp.add(stb); myjp.add(myjb);
    getContentPane().add(map, BorderLayout.CENTER);
    getContentPane().add(myjp,BorderLayout.NORTH);
    //getContentPane().add(stb,BorderLayout.SOUTH);
    addShapefileToMap(layer,s1);
    addShapefileToMap(layer2,s2);
    final Point pt = new Point(-117,33);
    Flash flash = new Flash(300.0,200.0,500.0,400.0);
    //Flash flash = new Flash(500.0,400.0,300.0,200.0);
	flash.start();
    getContentPane().add(toc, BorderLayout.WEST);
  }
  private void addShapefileToMap(Layer layer,String s) {
    //stb.setSelectedLayer(map.getLayer(0));
    String datapath = s; //"C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\States.shp";
    layer.setDataset("0;"+datapath);
    map.add(layer);
  }

  public static void main(String[] args) {
    QuickStart4f qstart = new QuickStart4f();
    qstart.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            System.out.println("Thanks, Quick Start exits");
            System.exit(0);
        }
    });
    qstart.setVisible(true);
  }
}
class Flash extends Thread {
  AcetateLayer acetLayer = new AcetateLayer();
  double x1,y1,x2,y2;

  Flash(double x11,double y11,double x22, double y22) {
	x1 = x11;y1=y11;x2=x22;y2=y22;
  }
  public void run() {
	for (int i=0;i<21;i++) {
	  try {
		Thread.sleep(300);
		final int j = i;
		if (acetLayer != null) QuickStart4f.map.remove(acetLayer);
		acetLayer = new AcetateLayer() {
			public void paintComponent(java.awt.Graphics g) {
				java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
					//Line2D.Double line = new Line2D.Double(startx,starty,endx,endy);
					Line2D.Double line = new Line2D.Double(x1,y1,x1+j*(x2-x1)/20.0,y1+j*(y2-y1)/20.0);
					g2d.setColor(new Color(0,0,250));
					g2d.draw(line);
					g2d.setColor(new Color(250,0,0));
					g2d.fillOval(298,198,5,5);
					g2d.fillOval(500,400,5,5);
		       }
		    };
		acetLayer.setMap(QuickStart4f.map);
        QuickStart4f.map.add(acetLayer);
	  } catch (Exception e) {}
    }
  }
}
