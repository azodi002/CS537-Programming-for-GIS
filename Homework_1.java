
/*******************************************

Name:      Omid Azodi

RED ID:    816736590

Class:     CS-537

Professor: Eckberg

Due Date:  10/10/2017

Homework:  Homework#10/10/2017

Description: 
" Just adds a simple AcetateLayer to place a line & points on the
	map. Mercifully this works if the drawing is done in Java terms,
	using frame coordinates (pixels).  Using an AcetateLayer has the
	advantage that an AcetateLayer is transparent, and this solves
	a problem that transparent layers in Java are somewhat tricky
	to do, e.g. a canvas can not be made transparent. In addition,
	the line is drawn in stages by using a thread.  The thread is
	wise and necessary.  In this  version, the acetate layers
	are removed, within the loop, so they do not accumulate
	and slow things down. The loop should work with two arbitrary
	points!  Those points are parameters to the thread code. "	
	- Eckberg

*******************************************/

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
import javax.swing.JTextField;
import javax.swing.JLabel;


public class Homework_1 extends JFrame {
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
  ActionListener actlis;
  TocAdapter mytocadapter;

  // Labels for user to describe the field
  JLabel first_x  = new JLabel("X1:");
  JLabel first_y  = new JLabel("Y1:");
  JLabel second_x = new JLabel("X2:");
  JLabel second_y = new JLabel("Y2:");
  JLabel three_LX = new JLabel("X3:");
  JLabel three_LY = new JLabel("Y3:");
  
  
  // Text-field of what the user will enter
  // (X1,Y1), (X2,Y2) 
  // Lines will be drawn between these points
  JTextField firstPoint_x  = new JTextField(4);
  JTextField firstPoint_y  = new JTextField(4);
  JTextField secondPoint_x = new JTextField(4);
  JTextField secondPoint_y = new JTextField(4);
  JTextField three_x = new JTextField(4);
  JTextField three_y = new JTextField(4);
  
  JButton draw_button = new JButton("Draw Line");
  
  int x1 = 0;
  int y1 = 0;
  int x2 = 0;
  int y2 = 0;
  int x3 = 0;
  int y3 = 0;

  public Homework_1() {
	  
    super("Quick Start");
    this.setBounds(80,80,750, 550);
    zptb.setMap(map);
    stb.setMap(map);
	
	// adding the 4 text fields
	// for the first and second points
	stb.add(first_x);
	stb.add(firstPoint_x);
	
	stb.add(first_y);
	stb.add(firstPoint_y);
	
	stb.add(second_x);
	stb.add(secondPoint_x);
	
	stb.add(second_y);
	stb.add(secondPoint_y);	
	
	stb.add(three_LX);
	stb.add(three_x);
	stb.add(three_LY);
	stb.add(three_y);
		
    actlis = new ActionListener (){public void actionPerformed(ActionEvent ae){
		
		String get_first_x    = firstPoint_x.getText();
		String get_first_y    = firstPoint_y.getText();
		String get_second_x   = secondPoint_x.getText();
		String get_second_y   = secondPoint_y.getText();
		
		String get_three_x   = three_x.getText();
		String get_three_y   = three_y.getText();
				
		
		x1 = Integer.parseInt(get_first_x);
		y1 = Integer.parseInt(get_first_y);
		x2 = Integer.parseInt(get_second_x);
		y2 = Integer.parseInt(get_second_y);
		x3 = Integer.parseInt(get_three_x);
		y3 = Integer.parseInt(get_three_y);
		
	
		/***Debugging statements***/
		System.out.println("X1 = " + get_first_x);
		System.out.println("Y1 = " + get_first_y);
		System.out.println("X2 = " + get_second_x);
		System.out.println("Y2 = " + get_second_y);
		/***Debugging statements***/
		
		Flash flash = new Flash(x1,y1,x2,y2);
		Flash triangle_1 = new Flash(x1,y1,x3,y3);
		Flash triangle_2 = new Flash(x2,y2,x3,y3);
		flash.start();
		triangle_1.start();
		triangle_2.start();
		
      }};
    toc.setMap(map);
    mytocadapter = new TocAdapter() {
		public void click(TocEvent e) {
		  System.out.println("aloha");
		  stb.setSelectedLayer((e.getLegend()).getLayer());
	  	}
    };
    toc.addTocListener(mytocadapter);
    //myjb.addActionListener(actlis);
	draw_button.addActionListener(actlis);
	
    myjp.add(zptb); 
	myjp.add(stb); 
	myjp.add(draw_button);

    getContentPane().add(map, BorderLayout.CENTER);
    getContentPane().add(myjp,BorderLayout.NORTH);

    addShapefileToMap(layer,s1);
    addShapefileToMap(layer2,s2);
	
	
    getContentPane().add(toc, BorderLayout.WEST);
	
	}
  private void addShapefileToMap(Layer layer,String s) 
  {
    String datapath = s; //"C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\States.shp";
    layer.setDataset("0;"+datapath);
    map.add(layer);
  }

  public static void main(String[] args) {
    Homework_1 qstart = new Homework_1();
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
  //double x1,y1,x2,y2;
	int x1,y1,x2,y2;
  
  Flash(int x11,int y11,int x22, int y22) {
	x1 = x11;
	y1=y11;
	x2=x22;
	y2=y22;
  }
  public void run() {
	for (int i=0;i<21;i++) {
	  try {
		Thread.sleep(300);
		final int j = i;
		if (acetLayer != null) Homework_1.map.remove(acetLayer);
		acetLayer = new AcetateLayer() {
			public void paintComponent(java.awt.Graphics g) {
				java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
					Line2D.Double line = new Line2D.Double(x1,y1,x1+j*(x2-x1)/20.0,y1+j*(y2-y1)/20.0);
					g2d.setColor(new Color(0,0,250));
					g2d.draw(line);
					g2d.setColor(new Color(250,0,0));
					
					g2d.fillOval(x1,y1,5,5);
					g2d.fillOval(x2,y2,5,5);
		       }
		    };
		acetLayer.setMap(Homework_1.map);
        Homework_1.map.add(acetLayer);
	  } catch (Exception e) {}
    }
  }
}
