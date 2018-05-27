// contains basic XY layer code, but without attribute table code and
// other stuff from earlier chapters
import javax.swing.*;
import java.io.*;
import java.util.StringTokenizer;
import java.awt.*;
import java.awt.event.*;
import com.esri.mo2.ui.bean.*; // beans used: Map,Layer,Toc,TocAdapter,Tool
        // TocEvent,Legend(a legend is part of a toc),ActateLayer
import com.esri.mo2.ui.tb.ZoomPanToolBar;
import com.esri.mo2.ui.tb.SelectionToolBar;
import com.esri.mo2.ui.ren.LayerProperties;
import javax.swing.table.AbstractTableModel;
import com.esri.mo2.data.feat.*; //ShapefileFolder, ShapefileWriter
import com.esri.mo2.map.dpy.FeatureLayer;
import com.esri.mo2.map.dpy.BaseFeatureLayer;
import com.esri.mo2.map.draw.SimpleMarkerSymbol;
import com.esri.mo2.map.draw.BaseSimpleRenderer;
import com.esri.mo2.file.shp.*;
import com.esri.mo2.map.dpy.Layerset;
import com.esri.mo2.ui.bean.Tool;
import com.esri.mo2.cs.geom.Point; //using Envelope, Point, BasePointsArray
import com.esri.mo2.cs.geom.BasePointsArray;
public class QuickStartXY extends JFrame {
  static Map map = new Map();
  Legend legend;
  Legend legend2;
  Layer layer = new Layer();
  Layer layer2 = new Layer();
  BasePointsArray bpa = new BasePointsArray();
  Layer layer3 = null;
  com.esri.mo2.map.dpy.Layer activeLayer;
  JMenuBar mbar = new JMenuBar();
  JMenu file = new JMenu("File");
  JMenu theme = new JMenu("Theme");
  JMenuItem printitem = new JMenuItem("print",new ImageIcon("print.gif"));
  JMenuItem addlyritem = new JMenuItem("add layer",new ImageIcon("addtheme.gif"));
  JMenuItem remlyritem = new JMenuItem("remove layer",new ImageIcon("delete.gif"));
  JMenuItem propsitem = new JMenuItem("Legend Editor",new ImageIcon("properties.gif"));
  Toc toc = new Toc();
  String s1 = "C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\States.shp";
  String s2 = "C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\capitals.shp"; String datapathname = "";
  String legendname = "";
  ZoomPanToolBar zptb = new ZoomPanToolBar();
  static SelectionToolBar stb = new SelectionToolBar();
  JToolBar jtb = new JToolBar();
  JPanel myjp = new JPanel();
  JPanel myjp2 = new JPanel();
  JButton prtjb = new JButton(new ImageIcon("print.gif"));
  JButton addlyrjb = new JButton(new ImageIcon("addtheme.gif"));
  JButton ptrjb = new JButton(new ImageIcon("pointer.gif"));
  JButton distjb = new JButton(new ImageIcon("measure_1.gif"));
  JButton XYjb = new JButton("XY");
  ActionListener layerlis;
  ActionListener xyListener;
  TocAdapter mytocadapter;
  public QuickStartXY() {
    super("Quick Start");
    this.setBounds(100,100,700,450);
    zptb.setMap(map);
    stb.setMap(map);
    setJMenuBar(mbar);
    xyListener = new ActionListener() {
	  public void actionPerformed(ActionEvent ae) {
	    JDialog jd = new JDialog();
	    JFileChooser jfc = new JFileChooser();
	    jfc.showOpenDialog(jd);
	    File file  = jfc.getSelectedFile();
	    String s; // = in.readLine();
		double x,y;
		int n = 0;
		try {
	      FileReader fred = new FileReader(file);
	      BufferedReader in = new BufferedReader(fred);
		  while ((s = in.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(s,",");
			x = Double.parseDouble(st.nextToken());
			y = Double.parseDouble(st.nextToken());
			bpa.insertPoint(n++,new com.esri.mo2.cs.geom.Point(x,y));
		    System.out.println("we are reading " + x + " " + y);
	      }
	    } catch (IOException e){}
	    MyBaseFeatureLayer xyfl = new MyBaseFeatureLayer(bpa,map);
	    xyfl.setVisible(true);
	    map.getLayerset().addLayer(xyfl);
	    map.redraw();
	    xyfl.setVisible(true);
	    map.repaint();
	    map.redraw();

	    }
	  };
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
			   legend.getLayer();
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
		legend = e.getLegend();
	    activeLayer = legend.getLayer();
	    stb.setSelectedLayer(activeLayer);
	    zptb.setSelectedLayer(activeLayer);
	    remlyritem.setEnabled(true);
	    propsitem.setEnabled(true);
	  }
    };
    toc.addTocListener(mytocadapter);
    remlyritem.setEnabled(false); // assume no layer initially selected
    propsitem.setEnabled(false);
    addlyritem.addActionListener(layerlis);
    XYjb.addActionListener(xyListener);
    remlyritem.addActionListener(layerlis);
    propsitem.addActionListener(layerlis);
    file.add(addlyritem);
    file.add(printitem);
    file.add(remlyritem);
    file.add(propsitem);
    mbar.add(file);
    mbar.add(theme);
    jtb.add(prtjb);
    jtb.add(addlyrjb);
    jtb.add(XYjb);
    myjp.add(jtb);
    myjp.add(zptb); myjp.add(stb);
    getContentPane().add(map, BorderLayout.CENTER);
    getContentPane().add(myjp,BorderLayout.NORTH);
    getContentPane().add(myjp2,BorderLayout.SOUTH);
    addShapefileToMap(layer,s1);
    addShapefileToMap(layer2,s2);
    getContentPane().add(toc, BorderLayout.WEST);
  }
  private void addShapefileToMap(Layer layer,String s) {
    String datapath = s; //"C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\States.shp";
    layer.setDataset("0;"+datapath);
    map.add(layer);
  }
  public static void main(String[] args) {
    QuickStartXY qstart = new QuickStartXY();
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

class MyBaseFeatureLayer extends BaseFeatureLayer {
  BaseFields fields;
  private java.util.Vector featureVector;
  public MyBaseFeatureLayer(BasePointsArray bpa,Map map) {
	createFeaturesAndFields(bpa,map);
	BaseFeatureClass bfc = getFeatureClass("MyPoints",bpa);
	setFeatureClass(bfc);
	BaseSimpleRenderer rd = new BaseSimpleRenderer();
	SimpleMarkerSymbol sms= new SimpleMarkerSymbol();
	//sms.setType(SimpleMarkerSymbol.CIRCLE_MARKER);
	sms.setSymbolColor(new Color(255,0,20));
	sms.setWidth(5);
	rd.setSymbol(sms);
	setRenderer(rd);
	layerCapabilities lc = new layerCapabilities();
	setCapabilities(lc);
  }
  private void createFeaturesAndFields(BasePointsArray bpa,Map map) {
	featureVector = new java.util.Vector();
	fields = new BaseFields();
	createDbfFields();
	for(int i=0;i<bpa.size();i++) {
	  BaseFeature feature = new BaseFeature();  //feature is a row
	  feature.setFields(fields);
	  Point p = new Point(bpa.getPoint(i));
	  //p = new Point(-117,34);
	  feature.setValue(0,p);
	  feature.setValue(1,new Integer(0));  // point data
	  feature.setDataID(new BaseDataID("myPoints",i));
	  featureVector.addElement(feature);
	  System.out.println("here we are");
    }
  }
  private void createDbfFields() {
	fields.addField(new BaseField("#SHAPE#",Field.ESRI_SHAPE,0,0));
	fields.addField(new BaseField("ID",java.sql.Types.INTEGER,9,0));
  }
  public BaseFeatureClass getFeatureClass(String Name,BasePointsArray bpa){
    com.esri.mo2.map.mem.MemoryFeatureClass featClass = null;
    try {
	  featClass = new com.esri.mo2.map.mem.MemoryFeatureClass(MapDataset.POINT,
	    fields);
    } catch (IllegalArgumentException iae) {}
    featClass.setName(Name);
    for (int i=0;i<bpa.size();i++) {
	  featClass.addFeature((Feature) featureVector.elementAt(i));
    }
    return featClass;
  }
  private final class layerCapabilities extends
       com.esri.mo2.map.dpy.LayerCapabilities {
    layerCapabilities() {
	  for (int i=0;i<this.size(); i++) {
		setAvailable(this.getCapabilityName(i),true);
		setEnablingAllowed(this.getCapabilityName(i),true);
		getCapability(i).setEnabled(true);
	  }
    }
  }
}