// Has code to make color ramp without legend editor,.
// Includes code to make the toc wider.
// Key classes: BaseClassBreaks Renderer, BaseRange
// Breaks are given by hard coding the correct values.
import javax.swing.*;
import java.io.IOException;
import java.awt.event.*;
import java.awt.*;
import com.esri.mo2.ui.bean.*; // beans used: Map,Layer,Toc,TocAdapter,
        // TocEvent,Legend(a legend is part of a toc)
import com.esri.mo2.ui.tb.ZoomPanToolBar;
import com.esri.mo2.ui.tb.SelectionToolBar;
import com.esri.mo2.ui.ren.LayerProperties;
import com.esri.mo2.map.dpy.BaseFeatureLayer;
import com.esri.mo2.data.feat.*;
import com.esri.mo2.map.draw.*;
public class QuickStart7bRamp extends JFrame {
  Map map = new Map();
  Legend legend;
  Layer layer = new Layer();
  Layer layer2 = new Layer();
  JMenuBar mbar = new JMenuBar();
  BaseClassBreaksRenderer cbr = new BaseClassBreaksRenderer();
  JMenu file = new JMenu("File");
  JMenuItem printitem = new JMenuItem("print",new ImageIcon("print.gif"));
  JMenuItem addlyritem = new JMenuItem("add layer",new ImageIcon("addtheme.gif"));
  JMenuItem remlyritem = new JMenuItem("remove layer",new ImageIcon("delete.gif"));
  JMenuItem propsitem = new JMenuItem("Legend Editor",new ImageIcon("properties.gif"));
  Toc toc = new Toc();
  String s1 = "C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\States.shp";
  String s2 = "C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\capitals.shp";
  ZoomPanToolBar zptb = new ZoomPanToolBar();
  SelectionToolBar stb = new SelectionToolBar();
  JToolBar jtb = new JToolBar();
  JPanel myjp = new JPanel();
  JButton prtjb = new JButton(new ImageIcon("print.gif"));
  JButton addlyrjb = new JButton(new ImageIcon("addtheme.gif"));
  ActionListener lis;
  ActionListener layerlis;
  TocAdapter mytocadapter;
  public QuickStart7bRamp() {
    super("Quick Start");
    this.setSize(650, 475);
    zptb.setMap(map);
    stb.setMap(map);
    setJMenuBar(mbar);

    lis = new ActionListener() {public void actionPerformed(ActionEvent ae){
	  Object source = ae.getSource();
	  if (source == prtjb || source instanceof JMenuItem ) {
        com.esri.mo2.ui.bean.Print mapPrint = new com.esri.mo2.ui.bean.Print();
        mapPrint.setMap(map);
        mapPrint.doPrint();// prints the map
        }
      else {
		try {
	      AddLyrDialog aldlg=  new AddLyrDialog();
	      aldlg.setMap(map);
	      aldlg.setVisible(true);
	    } catch(IOException e){}
      }
    }};
    layerlis = new ActionListener() {public void actionPerformed(ActionEvent ae){
	  Object source = ae.getSource();
	  if (source instanceof JMenuItem) {
		String arg = ae.getActionCommand();
		if(arg == "add layer") {
          try {
	        AddLyrDialog aldlg = new AddLyrDialog();
	        aldlg.setMap(map);
	        aldlg.setVisible(true);
          } catch(IOException e){}
	      }
	    else if(arg == "remove layer") {
	      try {
			com.esri.mo2.map.dpy.Layer dpylayer =
			  (com.esri.mo2.map.dpy.Layer) legend.getLayer();
			map.getLayerset().removeLayer(dpylayer);
			map.redraw();
			remlyritem.setEnabled(false);
			propsitem.setEnabled(false);
			stb.setSelectedLayer(null);
			zptb.setSelectedLayer(null);
	      } catch(Exception e) {}
	      }
	    else if(arg == "Legend Editor") {
          LayerProperties lp = new LayerProperties();
          lp.setLegend(legend);
          lp.setSelectedTabIndex(0);
          lp.setVisible(true);
	    }
      }
    }};
    toc.setMap(map);
    mytocadapter = new TocAdapter() {
	  public void click(TocEvent e) {
	    System.out.println("aloha");
	    legend = e.getLegend();
	    stb.setSelectedLayer((e.getLegend()).getLayer());
	    remlyritem.setEnabled(true);
	    propsitem.setEnabled(true);
   	  }
    };
    toc.addTocListener(mytocadapter);
    remlyritem.setEnabled(false); // assume no layer initially selected
    propsitem.setEnabled(false);
    printitem.addActionListener(lis);
    addlyritem.addActionListener(layerlis);
    remlyritem.addActionListener(layerlis);
    propsitem.addActionListener(layerlis);
    file.add(addlyritem);
    file.add(printitem);
    file.add(remlyritem);
    file.add(propsitem);
    mbar.add(file);
    prtjb.addActionListener(lis);
    prtjb.setToolTipText("print map");
    addlyrjb.addActionListener(lis);
    addlyrjb.setToolTipText("add layer");
    jtb.add(prtjb);
    jtb.add(addlyrjb);
    myjp.add(jtb);
    myjp.add(zptb); myjp.add(stb);
    getContentPane().add(map, BorderLayout.CENTER);
    getContentPane().add(myjp,BorderLayout.NORTH);
    addShapefileToMap(layer,s1);
    addShapefileToMap(layer2,s2);
    getContentPane().add(toc, BorderLayout.WEST);
    com.esri.mo2.map.dpy.Layer dpylayer = map.getLayer("States");
    com.esri.mo2.map.dpy.FeatureLayer flayer = (BaseFeatureLayer) dpylayer;
    Fields fields =
        flayer.getFeatureClass().getFields();
    int i = fields.findField("POP1990");
    Field f = fields.getField(i);
    cbr.setField(f);
    // intervals are [0,1109252),[1109252,2776755),[2776755,4375099),
    // [4375099,6628637),[6628637,40000000)
    // adding a break to cbr means adding a symbol and a range
    int breaks [] = {0,1109252,2776755,4375099,6628637,40000000};
    int numClasses = 5;
    Comparable low,high = null;
    for (int j = 0;j<numClasses;j++) {
	  low = (Comparable) f.parse(String.valueOf(breaks[j]));
	  high = (Comparable) f.parse(String.valueOf(breaks[j+1]));
	  BaseRange b = new BaseRange(low,high);
   	  SimplePolygonSymbol sps = new SimplePolygonSymbol();
	  int r = 255 - (255/(numClasses-1))*j; // white is Color(255,255,255)
	  sps.setPaint(new Color(255,r,r));
	  cbr.addBreak(sps,b);
    }
    flayer.setRenderer(cbr);
    // System.out.println(toc.getSize().width);//found the old height and
    // width of the toc, and I guessed a better size in the line below....this
    // enabled the user to see the new wider graduated color legend;
    // note that the MOJO Legend Editor does not get the toc width adjusted;
    // nothing else seemed to adjust the WEST width, including toc.refresh()
    // below, or re-adding the toc to BorderLayout.WEST
    toc.setPreferredSize(new Dimension(toc.getSize().width + 25,
                                       toc.getSize().height));
    map.redraw();
    toc.refresh();
    //getContentPane().add(toc,BorderLayout.WEST);map.redraw();
  }
  private void addShapefileToMap(Layer layer,String s) {
    String datapath = s; //"C:\\ESRI\\MOJ10\\Samples\\Data\\USA\\States.shp";
    layer.setDataset("0;"+datapath);
    map.add(layer);
  }

  public static void main(String[] args) {
    QuickStart7bRamp qstart = new QuickStart7bRamp();
    qstart.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            System.out.println("Thanks, Quick Start exits");
            System.exit(0);
        }
    });
    qstart.setVisible(true);
  }
}
// following is an Add Layer dialog window
class AddLyrDialog extends JDialog {
  Map map;
  ActionListener lis;
  JButton ok = new JButton("OK");
  JButton cancel = new JButton("Cancel");
  JPanel panel1 = new JPanel();
  com.esri.mo2.ui.bean.CustomDatasetEditor cus = new com.esri.mo2.ui.bean.
    CustomDatasetEditor();
  AddLyrDialog() throws IOException {
	setBounds(50,50,520,430);
	setTitle("Select a theme/layer");
	addWindowListener(new WindowAdapter() {
	  public void windowClosing(WindowEvent e) {
	    setVisible(false);
	  }
    });

	lis = new ActionListener() {
	  public void actionPerformed(ActionEvent ae) {
	    Object source = ae.getSource();
	    if (source == cancel)
	      setVisible(false);
	    else {
	      try {
			setVisible(false);
			map.getLayerset().addLayer(cus.getLayer());
			map.redraw();
		  } catch(IOException e){}
	    }
	  }
    };
    ok.addActionListener(lis);
    cancel.addActionListener(lis);
    getContentPane().add(cus,BorderLayout.CENTER);
    panel1.add(ok);
    panel1.add(cancel);
    getContentPane().add(panel1,BorderLayout.SOUTH);
  }
  public void setMap(com.esri.mo2.ui.bean.Map map1){
	map = map1;
  }
}