// This program give the ability to easily add a point theme from
// a file of points with lines in the form longitude,latitude
// in world coordinates.  A third name field is added.  File of points in
//  CSV form is called "data".  We illustrate an airplane symbol taken from
// a true type font supplied by ESRI.  See the class XYfeatureLayer.

import javax.swing.*;
import java.io.*;
import java.util.Vector;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.awt.*;
import java.awt.event.*;
import com.esri.mo2.ui.bean.*; // beans used: Map,Layer,Toc,TocAdapter,Tool
        // TocEvent,Legend(a legend is part of a toc),ActateLayer
import com.esri.mo2.ui.tb.ZoomPanToolBar;
import com.esri.mo2.ui.tb.SelectionToolBar;
import com.esri.mo2.ui.ren.LayerProperties;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import com.esri.mo2.data.feat.*; //ShapefileFolder, ShapefileWriter
import com.esri.mo2.map.dpy.FeatureLayer;
import com.esri.mo2.map.dpy.BaseFeatureLayer;
import com.esri.mo2.map.draw.SimpleMarkerSymbol;
import com.esri.mo2.map.draw.BaseSimpleRenderer;
import com.esri.mo2.map.draw.TrueTypeMarkerSymbol;
import com.esri.mo2.file.shp.*;
import com.esri.mo2.map.dpy.Layerset;
import com.esri.mo2.ui.bean.Tool;
import java.awt.geom.*;
import com.esri.mo2.cs.geom.*; //using Envelope, Point, BasePointsArray

public class QuickStartXY5 extends JFrame {
  static Map map = new Map();
  static boolean fullMap = true;  // Map not zoomed
  Legend legend;
  Legend legend2;
  Layer layer = new Layer();
  Layer layer2 = new Layer();
  Layer layer3 = null;
  static AcetateLayer acetLayer;
  static com.esri.mo2.map.dpy.Layer layer4;
  com.esri.mo2.map.dpy.Layer activeLayer;
  int activeLayerIndex;
  com.esri.mo2.cs.geom.Point initPoint,endPoint;
  double distance;
  JMenuBar mbar = new JMenuBar();
  JMenu file = new JMenu("File");
  JMenu theme = new JMenu("Theme");
  JMenu layercontrol = new JMenu("LayerControl");
  JMenuItem attribitem = new JMenuItem("open attribute table",
                            new ImageIcon("tableview.gif"));
  JMenuItem createlayeritem  = new JMenuItem("create layer from selection",
                    new ImageIcon("Icon0915b.jpg"));
  static JMenuItem promoteitem = new JMenuItem("promote selected layer",
                    new ImageIcon("promote.jpg"));
  JMenuItem demoteitem = new JMenuItem("demote selected layer",
                    new ImageIcon("demote.jpg"));
  JMenuItem printitem = new JMenuItem("print",new ImageIcon("print.gif"));
  JMenuItem addlyritem = new JMenuItem("add layer",new ImageIcon("addtheme.gif"));
  JMenuItem remlyritem = new JMenuItem("remove layer",new ImageIcon("delete.gif"));
  JMenuItem propsitem = new JMenuItem("Legend Editor",new ImageIcon("properties.gif"));
  Toc toc = new Toc();
  String s1 = "C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\alger_county2.shp";
  String s2 = "C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\capitals.shp"; String datapathname = "";
  String legendname = "";
  ZoomPanToolBar zptb = new ZoomPanToolBar();
  static SelectionToolBar stb = new SelectionToolBar();
  JToolBar jtb = new JToolBar();
  ComponentListener complistener;
  JLabel statusLabel = new JLabel("status bar    LOC");
  static JLabel milesLabel = new JLabel("   DIST:  0 mi    ");
  static JLabel kmLabel = new JLabel("  0 km    ");
  java.text.DecimalFormat df = new java.text.DecimalFormat("0.000");
  JPanel myjp = new JPanel();
  JPanel myjp2 = new JPanel();
  JButton prtjb = new JButton(new ImageIcon("print.gif"));
  JButton addlyrjb = new JButton(new ImageIcon("addtheme.gif"));
  JButton ptrjb = new JButton(new ImageIcon("pointer.gif"));
  JButton distjb = new JButton(new ImageIcon("measure_1.gif"));
  JButton XYjb = new JButton("XY");
  Arrow arrow = new Arrow();
  //DistanceTool distanceTool= new DistanceTool();
  ActionListener lis;
  ActionListener layerlis;
  ActionListener layercontrollis;
  TocAdapter mytocadapter;
  static Envelope env;
  public QuickStartXY5() {

    super("Quick Start");
    //distanceTool.setMeasureUnit(com.esri.mo.util.Units.MILES);
    //map.setMapUnit(com.esri.mo.util.Units.MILES);
    this.setBounds(100,100,700,450);
    zptb.setMap(map);
    stb.setMap(map);
    setJMenuBar(mbar);
    ActionListener lisZoom = new ActionListener() {
	  public void actionPerformed(ActionEvent ae){
	    fullMap = false;}}; // can change a boolean here
	ActionListener lisFullExt = new ActionListener() {
	  public void actionPerformed(ActionEvent ae){
	    fullMap = true;}};
	// next line gets ahold of a reference to the zoomin button
	JButton zoomInButton = (JButton)zptb.getActionComponent("ZoomIn");
	JButton zoomFullExtentButton =
	        (JButton)zptb.getActionComponent("ZoomToFullExtent");
	JButton zoomToSelectedLayerButton =
	      (JButton)zptb.getActionComponent("ZoomToSelectedLayer");
	zoomInButton.addActionListener(lisZoom);
	zoomFullExtentButton.addActionListener(lisFullExt);
	zoomToSelectedLayerButton.addActionListener(lisZoom);
	complistener = new ComponentAdapter () {
	  public void componentResized(ComponentEvent ce) {
	    if(fullMap) {
	      map.setExtent(env);
	      map.zoom(1.0);    //scale is scale factor in pixels
	      map.redraw();
	    }
	  }
	};
    addComponentListener(complistener);
    lis = new ActionListener() {public void actionPerformed(ActionEvent ae){
	  Object source = ae.getSource();
	  if (source == prtjb || source instanceof JMenuItem ) {
        com.esri.mo2.ui.bean.Print mapPrint = new com.esri.mo2.ui.bean.Print();
        mapPrint.setMap(map);
        mapPrint.doPrint();// prints the map
        }
      else if (source == ptrjb) {
		map.setSelectedTool(arrow);
		DistanceTool.resetDist();
	    }
	  else if (source == distjb) {
		DistanceTool distanceTool = new DistanceTool();
		map.setSelectedTool(distanceTool);
        }
	  else if (source == XYjb) {
		try {
		  AddXYtheme addXYtheme = new AddXYtheme();
		  addXYtheme.setMap(map);
		  addXYtheme.setVisible(false);// the file chooser needs a parent
		    // but the parent can stay behind the scenes
		  map.redraw();
		  } catch (IOException e){}
	    }
	  else
	    {
		try {
	      AddLyrDialog aldlg = new AddLyrDialog();
	      aldlg.setMap(map);
	      aldlg.setVisible(true);
	    } catch(IOException e){}
      }
    }};
    layercontrollis = new ActionListener() {public void
                actionPerformed(ActionEvent ae){
	  String source = ae.getActionCommand();
	  System.out.println(activeLayerIndex+" active index");
	  if (source == "promote selected layer")
		map.getLayerset().moveLayer(activeLayerIndex,++activeLayerIndex);
      else
        map.getLayerset().moveLayer(activeLayerIndex,--activeLayerIndex);
      enableDisableButtons();
      map.redraw();
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
			   legend.getLayer();
			map.getLayerset().removeLayer(dpylayer);
			map.redraw();
			remlyritem.setEnabled(false);
			propsitem.setEnabled(false);
			attribitem.setEnabled(false);
			promoteitem.setEnabled(false);
			demoteitem.setEnabled(false);
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
	    else if (arg == "open attribute table") {
	      try {
	        layer4 = legend.getLayer();
            AttrTab attrtab = new AttrTab();
            attrtab.setVisible(true);
	      } catch(IOException ioe){}
	    }
        else if (arg=="create layer from selection") {
	      com.esri.mo2.map.draw.BaseSimpleRenderer sbr = new
	        com.esri.mo2.map.draw.BaseSimpleRenderer();
		  com.esri.mo2.map.draw.SimpleFillSymbol sfs = new
		    com.esri.mo2.map.draw.SimpleFillSymbol();// for polygons
		  sfs.setSymbolColor(new Color(255,255,0)); // mellow yellow
		  sfs.setType(com.esri.mo2.map.draw.SimpleFillSymbol.FILLTYPE_SOLID);
		  sfs.setBoundary(true);
	      layer4 = legend.getLayer();
	      FeatureLayer flayer2 = (FeatureLayer)layer4;
	      // select, e.g., Montana and then click the
	      // create layer menuitem; next line verifies a selection was made
	      System.out.println("has selected" + flayer2.hasSelection());
	      //next line creates the 'set' of selections
	      if (flayer2.hasSelection()) {
		    SelectionSet selectset = flayer2.getSelectionSet();
	        // next line makes a new feature layer of the selections
	        FeatureLayer selectedlayer = flayer2.createSelectionLayer(selectset);
	        sbr.setLayer(selectedlayer);
	        sbr.setSymbol(sfs);
	        selectedlayer.setRenderer(sbr);
	        Layerset layerset = map.getLayerset();
	        // next line places a new visible layer, e.g. Montana, on the map
	        layerset.addLayer(selectedlayer);
	        //selectedlayer.setVisible(true);
	        if(stb.getSelectedLayers() != null)
	          promoteitem.setEnabled(true);
	        try {
	          legend2 = toc.findLegend(selectedlayer);
		    } catch (Exception e) {}

		    CreateShapeDialog csd = new CreateShapeDialog(selectedlayer);
		    csd.setVisible(true);
	        Flash flash = new Flash(legend2);
	        flash.start();
	        map.redraw(); // necessary to see color immediately

		  }
	    }
      }
    }};
    toc.setMap(map);
    mytocadapter = new TocAdapter() {
	  public void click(TocEvent e) {
		System.out.println(activeLayerIndex+ "dex");
	    legend = e.getLegend();
	    activeLayer = legend.getLayer();
	    stb.setSelectedLayer(activeLayer);
	    zptb.setSelectedLayer(activeLayer);
	    // get acive layer index for promote and demote
	    activeLayerIndex = map.getLayerset().indexOf(activeLayer);
	    // layer indices are in order added, not toc order.
	    System.out.println(activeLayerIndex + "active ndex");
	    remlyritem.setEnabled(true);
	    propsitem.setEnabled(true);
	    attribitem.setEnabled(true);
	    enableDisableButtons();
   	  }
    };
    map.addMouseMotionListener(new MouseMotionAdapter() {
	  public void mouseMoved(MouseEvent me) {
		com.esri.mo2.cs.geom.Point worldPoint = null;
		if (map.getLayerCount() > 0) {
		  worldPoint = map.transformPixelToWorld(me.getX(),me.getY());
		  String s = "X:"+df.format(worldPoint.getX())+" "+
		             "Y:"+df.format(worldPoint.getY());
		  statusLabel.setText(s);
	      }
	    else
	      statusLabel.setText("X:0.000 Y:0.000");
      }
    });

    toc.addTocListener(mytocadapter);
    remlyritem.setEnabled(false); // assume no layer initially selected
    propsitem.setEnabled(false);
    attribitem.setEnabled(false);
    promoteitem.setEnabled(false);
    demoteitem.setEnabled(false);
    printitem.addActionListener(lis);
    addlyritem.addActionListener(layerlis);
    remlyritem.addActionListener(layerlis);
    propsitem.addActionListener(layerlis);
    attribitem.addActionListener(layerlis);
    createlayeritem.addActionListener(layerlis);
    promoteitem.addActionListener(layercontrollis);
    demoteitem.addActionListener(layercontrollis);
    file.add(addlyritem);
    file.add(printitem);
    file.add(remlyritem);
    file.add(propsitem);
    theme.add(attribitem);
    theme.add(createlayeritem);
    layercontrol.add(promoteitem);
    layercontrol.add(demoteitem);
    mbar.add(file);
    mbar.add(theme);
    mbar.add(layercontrol);
    prtjb.addActionListener(lis);
    prtjb.setToolTipText("print map");
    addlyrjb.addActionListener(lis);
    addlyrjb.setToolTipText("add layer");
    ptrjb.addActionListener(lis);
    distjb.addActionListener(lis);
    XYjb.addActionListener(lis);
    XYjb.setToolTipText("add a layer of points from a file");
    prtjb.setToolTipText("pointer");
    distjb.setToolTipText("press-drag-release to measure a distance");
    jtb.add(prtjb);
    jtb.add(addlyrjb);
    jtb.add(ptrjb);
    jtb.add(distjb);
    jtb.add(XYjb);
    myjp.add(jtb);
    myjp.add(zptb); myjp.add(stb);
    myjp2.add(statusLabel);
    myjp2.add(milesLabel);myjp2.add(kmLabel);
    getContentPane().add(map, BorderLayout.CENTER);
    getContentPane().add(myjp,BorderLayout.NORTH);
    getContentPane().add(myjp2,BorderLayout.SOUTH);
    addShapefileToMap(layer,s1);
    addShapefileToMap(layer2,s2);
    getContentPane().add(toc, BorderLayout.WEST);
  }
  private void addShapefileToMap(Layer layer,String s) {
    String datapath = s; //"C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\alger_county2.shp";
    layer.setDataset("0;"+datapath);
    map.add(layer);
  }
  public static void main(String[] args) {
    QuickStartXY5 qstart = new QuickStartXY5();
    qstart.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            System.out.println("Thanks, Quick Start exits");
            System.exit(0);
        }
    });
    qstart.setVisible(true);
    env = map.getExtent();
  }
  private void enableDisableButtons() {
    int layerCount = map.getLayerset().getSize();
    if (layerCount < 2) {
      promoteitem.setEnabled(false);
      demoteitem.setEnabled(false);
      }
    else if (activeLayerIndex == 0) {
      demoteitem.setEnabled(false);
      promoteitem.setEnabled(true);
	  }
    else if (activeLayerIndex == layerCount - 1) {
      promoteitem.setEnabled(false);
      demoteitem.setEnabled(true);
	  }
	else {
	  promoteitem.setEnabled(true);
	  demoteitem.setEnabled(true);
    }
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
			if (QuickStartXY5.stb.getSelectedLayers() != null)
			  QuickStartXY5.promoteitem.setEnabled(true);
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
class AddXYtheme extends JDialog {
  Map map;
  Vector s2 = new Vector();
  JFileChooser jfc = new JFileChooser();
  BasePointsArray bpa = new BasePointsArray();
  AddXYtheme() throws IOException {
	setBounds(50,50,520,430);
	jfc.showOpenDialog(this);
	try {
	  File file  = jfc.getSelectedFile();
	  FileReader fred = new FileReader(file);
	  BufferedReader in = new BufferedReader(fred);
	  String s; // = in.readLine();
	  double x,y;
	  int n = 0;
	  while ((s = in.readLine()) != null) {
		StringTokenizer st = new StringTokenizer(s,",");
		x = Double.parseDouble(st.nextToken());
		y = Double.parseDouble(st.nextToken());
		bpa.insertPoint(n++,new com.esri.mo2.cs.geom.Point(x,y));
		s2.addElement(st.nextToken());
	  }
	} catch (IOException e){}
	XYfeatureLayer xyfl = new XYfeatureLayer(bpa,map,s2);
	xyfl.setVisible(true);
	map = QuickStartXY5.map;
	map.getLayerset().addLayer(xyfl);
	map.redraw();
  }
  public void setMap(com.esri.mo2.ui.bean.Map map1){
  	map = map1;
  }
}
class XYfeatureLayer extends BaseFeatureLayer {
  BaseFields fields;
  private java.util.Vector featureVector;
  public XYfeatureLayer(BasePointsArray bpa,Map map,Vector s2) {
	createFeaturesAndFields(bpa,map,s2);
	BaseFeatureClass bfc = getFeatureClass("MyPoints",bpa);
	setFeatureClass(bfc);
	BaseSimpleRenderer srd = new BaseSimpleRenderer();
	//SimpleMarkerSymbol sms= new SimpleMarkerSymbol();
	TrueTypeMarkerSymbol ttm = new TrueTypeMarkerSymbol();
	ttm.setFont(new Font("ESRI Transportation & Civic",Font.PLAIN,20));// aka esri_9
	//sms.setType(SimpleMarkerSymbol.CIRCLE_MARKER);
	ttm.setColor(new Color(255,0,0));
	//sms.setSymbolColor(new Color(255,0,0));
	//sms.setWidth(5);
	ttm.setCharacter("101"); //airplane
	srd.setSymbol(ttm);
	setRenderer(srd);
	// without setting layer capabilities, the points will not
	// display (but the toc entry will still appear)
	XYLayerCapabilities lc = new XYLayerCapabilities();
	setCapabilities(lc);
  }
  private void createFeaturesAndFields(BasePointsArray bpa,Map map,Vector s2) {
	featureVector = new java.util.Vector();
	fields = new BaseFields();
	createDbfFields();
	for(int i=0;i<bpa.size();i++) {
	  BaseFeature feature = new BaseFeature();  //feature is a row
	  feature.setFields(fields);
	  com.esri.mo2.cs.geom.Point p = new
	    com.esri.mo2.cs.geom.Point(bpa.getPoint(i));
	  feature.setValue(0,p);
	  feature.setValue(1,new Integer(0));  // point data
	  feature.setValue(2,(String)s2.elementAt(i));
	  feature.setDataID(new BaseDataID("MyPoints",i));
	  featureVector.addElement(feature);
	}
  }
  private void createDbfFields() {
	fields.addField(new BaseField("#SHAPE#",Field.ESRI_SHAPE,0,0));
	fields.addField(new BaseField("ID",java.sql.Types.INTEGER,9,0));
	fields.addField(new BaseField("Name",java.sql.Types.VARCHAR,16,0));
  }
  public BaseFeatureClass getFeatureClass(String name,BasePointsArray bpa){
    com.esri.mo2.map.mem.MemoryFeatureClass featClass = null;
    try {
	  featClass = new com.esri.mo2.map.mem.MemoryFeatureClass(MapDataset.POINT,
	    fields);
    } catch (IllegalArgumentException iae) {}
    featClass.setName(name);
    for (int i=0;i<bpa.size();i++) {
	  featClass.addFeature((Feature) featureVector.elementAt(i));
    }
    return featClass;
  }
  private final class XYLayerCapabilities extends
       com.esri.mo2.map.dpy.LayerCapabilities {
    XYLayerCapabilities() {
	  for (int i=0;i<this.size(); i++) {
		setAvailable(this.getCapabilityName(i),true);
		setEnablingAllowed(this.getCapabilityName(i),true);
		getCapability(i).setEnabled(true);
	  }
    }
  }
}
class AttrTab extends JDialog {
  JPanel panel1 = new JPanel();
  com.esri.mo2.map.dpy.Layer layer = QuickStartXY5.layer4;
  JTable jtable = new JTable(new MyTableModel());
  JScrollPane scroll = new JScrollPane(jtable);

  public AttrTab() throws IOException {
  	setBounds(70,70,450,350);
  	setTitle("Attribute Table");
  	addWindowListener(new WindowAdapter() {
  	  public void windowClosing(WindowEvent e) {
  	    setVisible(false);
  	  }
    });
    scroll.setHorizontalScrollBarPolicy(
	   JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	// next line necessary for horiz scrollbar to work
	jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

	TableColumn tc = null;
	int numCols = jtable.getColumnCount();
	//jtable.setPreferredScrollableViewportSize(
		//new java.awt.Dimension(440,340));
	for (int j=0;j<numCols;j++) {
	  tc = jtable.getColumnModel().getColumn(j);
	  tc.setMinWidth(50);
    }
    getContentPane().add(scroll,BorderLayout.CENTER);
  }
}
class MyTableModel extends AbstractTableModel {
 // the required methods to implement are getRowCount,
 // getColumnCount, getValueAt
  com.esri.mo2.map.dpy.Layer layer = QuickStartXY5.layer4;
  MyTableModel() {
	qfilter.setSubFields(fields);
	com.esri.mo2.data.feat.Cursor cursor = flayer.search(qfilter);
	while (cursor.hasMore()) {
		ArrayList inner = new ArrayList();
		Feature f = (com.esri.mo2.data.feat.Feature)cursor.next();
		inner.add(0,String.valueOf(row));
		for (int j=1;j<fields.getNumFields();j++) {
		  inner.add(f.getValue(j).toString());
		}
	    data.add(inner);
	    row++;
    }
  }
  FeatureLayer flayer = (FeatureLayer) layer;
  FeatureClass fclass = flayer.getFeatureClass();
  String columnNames [] = fclass.getFields().getNames();
  ArrayList data = new ArrayList();
  int row = 0;
  int col = 0;
  BaseQueryFilter qfilter = new BaseQueryFilter();
  Fields fields = fclass.getFields();
  public int getColumnCount() {
	return fclass.getFields().getNumFields();
  }
  public int getRowCount() {
	return data.size();
  }
  public String getColumnName(int colIndx) {
	return columnNames[colIndx];
  }
  public Object getValueAt(int row, int col) {
	  ArrayList temp = new ArrayList();
	  temp =(ArrayList) data.get(row);
      return temp.get(col);
  }
}
class CreateShapeDialog extends JDialog {
  String name = "";
  String path = "";
  JButton ok = new JButton("OK");
  JButton cancel = new JButton("Cancel");
  JTextField nameField = new JTextField("enter layer name here, then hit ENTER",25);
  com.esri.mo2.map.dpy.FeatureLayer selectedlayer;
  ActionListener lis = new ActionListener() {public void actionPerformed(ActionEvent ae) {
	Object o = ae.getSource();
	if (o == nameField) {
	  name = nameField.getText().trim();
	  path = ((ShapefileFolder)(QuickStartXY5.layer4.getLayerSource())).getPath();
	  System.out.println(path+"    " + name);
    }
	else if (o == cancel)
      setVisible(false);
	else {
	  try {
		ShapefileWriter.writeFeatureLayer(selectedlayer,path,name,2);
	  } catch(Exception e) {System.out.println("write error");}
	  setVisible(false);
    }
  }};

  JPanel panel1 = new JPanel();
  JLabel centerlabel = new JLabel();
  //centerlabel;
  CreateShapeDialog (com.esri.mo2.map.dpy.FeatureLayer layer5) {
	selectedlayer = layer5;
    setBounds(40,350,450,150);
    setTitle("Create new shapefile?");
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
	    setVisible(false);
	  }
    });
    nameField.addActionListener(lis);
    ok.addActionListener(lis);
    cancel.addActionListener(lis);
    String s = "<HTML> To make a new shapefile from the new layer, enter<BR>" +
      "the new name you want for the layer and click OK.<BR>" +
      "You can then add it to the map in the usual way.<BR>"+
      "Click ENTER after replacing the text with your layer name";
    centerlabel.setHorizontalAlignment(JLabel.CENTER);
    centerlabel.setText(s);
    getContentPane().add(centerlabel,BorderLayout.CENTER);
    panel1.add(nameField);
    panel1.add(ok);
    panel1.add(cancel);
    getContentPane().add(panel1,BorderLayout.SOUTH);
  }
}
class Arrow extends Tool {
}
class Flash extends Thread {
  Legend legend;
  Flash(Legend legendin) {
	legend = legendin;
  }
  public void run() {
	for (int i=0;i<12;i++) {
	  try {
		Thread.sleep(500);
		legend.toggleSelected();
	  } catch (Exception e) {}
    }
  }
}
class DistanceTool extends DragTool  {
  int startx,starty,endx,endy,currx,curry;
  com.esri.mo2.cs.geom.Point initPoint, endPoint, currPoint;
  double distance;
  public static void resetDist() { // undo measure tool residue
    QuickStartXY5.milesLabel.setText("DIST   0 mi   ");
    QuickStartXY5.kmLabel.setText("   0 km    ");
    QuickStartXY5.map.remove(QuickStartXY5.acetLayer);
    QuickStartXY5.acetLayer = null;
    QuickStartXY5.map.repaint();
  }
  public void mousePressed(MouseEvent me) {
	startx = me.getX(); starty = me.getY();
	initPoint = QuickStartXY5.map.transformPixelToWorld(me.getX(),me.getY());
  }
  public void mouseReleased(MouseEvent me) {
	  // now we create an acetatelayer instance and draw a line on it
	endx = me.getX(); endy = me.getY();
	endPoint = QuickStartXY5.map.transformPixelToWorld(me.getX(),me.getY());
    distance = (69.44 / (2*Math.PI)) * 360 * Math.acos(
				 Math.sin(initPoint.y * 2 * Math.PI / 360)
			   * Math.sin(endPoint.y * 2 * Math.PI / 360)
			   + Math.cos(initPoint.y * 2 * Math.PI / 360)
			   * Math.cos(endPoint.y * 2 * Math.PI / 360)
			   * (Math.abs(initPoint.x - endPoint.x) < 180 ?
                    Math.cos((initPoint.x - endPoint.x)*2*Math.PI/360):
                    Math.cos((360 - Math.abs(initPoint.x - endPoint.x))*2*Math.PI/360)));
    System.out.println( distance  );
    QuickStartXY5.milesLabel.setText("DIST: " + new Float((float)distance).toString() + " mi  ");
    QuickStartXY5.kmLabel.setText(new Float((float)(distance*1.6093)).toString() + " km");
    if (QuickStartXY5.acetLayer != null)
      QuickStartXY5.map.remove(QuickStartXY5.acetLayer);
    QuickStartXY5.acetLayer = new AcetateLayer() {
      public void paintComponent(java.awt.Graphics g) {
		java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
		Line2D.Double line = new Line2D.Double(startx,starty,endx,endy);
		g2d.setColor(new Color(0,0,250));
		g2d.draw(line);
      }
    };
    Graphics g = super.getGraphics();
    QuickStartXY5.map.add(QuickStartXY5.acetLayer);
    QuickStartXY5.map.redraw();
  }
  public void cancel() {};
}
