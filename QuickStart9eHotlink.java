// This version explores hotlink using Identify class, as well as
// a bolt cursor icon, and clicking on a state pops up a window
// showing the state bird.

import javax.swing.*;
import java.io.IOException;
import java.awt.event.*;
import java.awt.*;
import com.esri.mo2.ui.bean.*; // beans used: Map,Layer,Toc,TocAdapter,
        // TocEvent,Legend(a legend is part of a toc)
import com.esri.mo2.ui.tb.ZoomPanToolBar;
import com.esri.mo2.ui.tb.SelectionToolBar;
import com.esri.mo2.ui.ren.LayerProperties;
import com.esri.mo2.cs.geom.Envelope;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import com.esri.mo2.data.feat.*; //ShapefileFolder, ShapefileWriter
import com.esri.mo2.map.dpy.FeatureLayer;
import com.esri.mo2.file.shp.*;
import com.esri.mo2.map.dpy.Layerset;
import com.esri.mo2.map.draw.*;
import com.esri.mo2.ui.bean.Tool;
import java.util.ArrayList;
public class QuickStart9eHotlink extends JFrame {
  static Map map = new Map();
  static boolean fullMap = true;  // Map not zoomed
  Legend legend;
  Legend legend2;
  Layer layer = new Layer();
  Layer layer2 = new Layer();
  Layer layer3 = null;
  static com.esri.mo2.map.dpy.Layer layer4;
  com.esri.mo2.map.dpy.Layer activeLayer;
  int activeLayerIndex;
  JMenuBar mbar = new JMenuBar();
  JMenu file = new JMenu("File");
  JMenu theme = new JMenu("Theme");
  JMenu layercontrol = new JMenu("LayerControl");
  JMenuItem attribitem = new JMenuItem("open attribute table",
                            new ImageIcon("tableview.gif"));
  JMenuItem createlayeritem  = new JMenuItem("create layer from selection",
                    new ImageIcon("Icon0915b.jpg"));
  static JMenuItem promoteitem = new JMenuItem("promote selected layer",
                    new ImageIcon("promote1.gif"));
  JMenuItem demoteitem = new JMenuItem("demote selected layer",
                    new ImageIcon("demote1.gif"));
  JMenuItem printitem = new JMenuItem("print",new ImageIcon("print.gif"));
  JMenuItem addlyritem = new JMenuItem("add layer",new ImageIcon("addtheme.gif"));
  JMenuItem remlyritem = new JMenuItem("remove layer",new ImageIcon("delete.gif"));
  JMenuItem propsitem = new JMenuItem("Legend Editor",new ImageIcon("properties.gif"));
  Toc toc = new Toc();
  String s1 = "C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\States.shp";
  String s2 = "C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\capitals.shp";
  String datapathname = "";
  String legendname = "";
  ZoomPanToolBar zptb = new ZoomPanToolBar();
  static SelectionToolBar stb = new SelectionToolBar();
  JToolBar jtb = new JToolBar();
  ComponentListener complistener;
  JLabel statusLabel = new JLabel("status bar    LOC");
  java.text.DecimalFormat df = new java.text.DecimalFormat("0.000");
  JPanel myjp = new JPanel();
  JPanel myjp2 = new JPanel();
  JButton prtjb = new JButton(new ImageIcon("print.gif"));
  JButton addlyrjb = new JButton(new ImageIcon("addtheme.gif"));
  JButton ptrjb = new JButton(new ImageIcon("pointer.gif"));
  JButton hotjb = new JButton(new ImageIcon("hotlink.gif"));
  Arrow arrow = new Arrow();
  ActionListener lis;
  ActionListener layerlis;
  ActionListener layercontrollis;
  TocAdapter mytocadapter;Toolkit tk = Toolkit.getDefaultToolkit();
  Image bolt = tk.getImage("hotlink_32x32-32.gif");  // 16x16 gif file
  java.awt.Cursor boltCursor = tk.createCustomCursor(bolt,new java.awt.Point(11,26),"bolt");
  MyPickAdapter picklis = new MyPickAdapter();
  Identify hotlink = new Identify(); //the Identify class implements a PickListener,
  static String mystate = null;
  class MyPickAdapter implements PickListener {   //implements hotlink
    public void beginPick(PickEvent pe){};
    // this fires even when you click outside the states layer
    public void endPick(PickEvent pe){}
    public void foundData(PickEvent pe){
      //fires only when a layer feature is clicked
      FeatureLayer flayer2 = (FeatureLayer) pe.getLayer();
      com.esri.mo2.data.feat.Cursor c = pe.getCursor();
      Feature f = null;
      Fields fields = null;
      if (c != null)
        f = (Feature)c.next();
      fields = f.getFields();
      String sname = fields.getField(5).getName(); //gets col. name for state name
      mystate = (String)f.getValue(5);
      try {
	    HotPick hotpick = new HotPick();//opens dialog window with Duke in it
	    hotpick.setVisible(true);
      } catch(Exception e){}
    }
  };

  static Envelope env;
  public QuickStart9eHotlink() {
    super("Quick Start");
    this.setBounds(150,150,900,650);
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
      }
      else if (source == hotjb) {
		hotlink.setCursor(boltCursor);
        map.setSelectedTool(hotlink);
      }
      else {
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
	    stb.setSelectedLayers(null);
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
		  System.out.println("OPENING ATTRIBUTE TABLE");
	    layer4 = legend.getLayer();
          AttrTab attrtab = new AttrTab();
          attrtab.setVisible(true);
	  } catch(IOException ioe){}
	}
    else if (arg=="create layer from selection") {
	  System.out.println("create layer from selection");
	  BaseSimpleRenderer sbr = new BaseSimpleRenderer();
	  SimplePolygonSymbol sps = new SimplePolygonSymbol();
	  sps.setPaint(AoFillStyle.getPaint(
	  	AoFillStyle.SOLID_FILL,new java.awt.Color(255,255,0)));
	  sps.setBoundary(true);
	  layer4 = legend.getLayer();
	  FeatureLayer flayer2 = (FeatureLayer)layer4;
	  // select, e.g., Montana and then click the
	  // create layer menuitem; next line verifies a selection was made
	  System.out.println("has selected" + flayer2.hasSelection());
	  //next line creates the 'set' of selections
	  if (flayer2.hasSelection()) {
		System.out.println("layer2 has selection");
	    SelectionSet selectset = flayer2.getSelectionSet();
	    // next line makes a new feature layer of the selections
	    FeatureLayer selectedlayer = flayer2.createSelectionLayer(selectset);
	    sbr.setLayer(selectedlayer);
	    sbr.setSymbol(sps);
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
	    legend = e.getLegend();
        activeLayer = legend.getLayer();
        stb.setSelectedLayer(activeLayer);
        zptb.setSelectedLayer(activeLayer);
        // get active layer index for promote and demote
        activeLayerIndex = map.getLayerset().indexOf(activeLayer);
        // layer indices are in order added, not toc order.
        com.esri.mo2.map.dpy.Layer[] layers = {activeLayer};
        hotlink.setSelectedLayers(layers);// replaces setToc from MOJ10
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
    hotlink.addPickListener(picklis);
    hotlink.setPickWidth(5);// sets tolerance for hotlink clicks
    hotjb.addActionListener(lis);
    hotjb.setToolTipText("hotlink tool--click somthing to maybe see a picture");
    ptrjb.addActionListener(lis);
    prtjb.setToolTipText("pointer");
    jtb.add(prtjb);
    jtb.add(addlyrjb);
    jtb.add(ptrjb);
    jtb.add(hotjb);
    myjp.add(jtb);
    myjp.add(zptb);
    myjp.add(stb);
    myjp2.add(statusLabel);
    getContentPane().add(map, BorderLayout.CENTER);
    getContentPane().add(myjp,BorderLayout.NORTH);
    getContentPane().add(myjp2,BorderLayout.SOUTH);
    addShapefileToMap(layer,s1);
    addShapefileToMap(layer2,s2);
    getContentPane().add(toc, BorderLayout.WEST);
  }
  private void addShapefileToMap(Layer layer,String s) {
    String datapath = s; //"C:\\ESRI\\MOJ10\\Samples\\Data\\USA\\States.shp";
    layer.setDataset("0;"+datapath);
    map.add(layer);
  }
  public static void main(String[] args) {
    QuickStart9eHotlink qstart = new QuickStart9eHotlink();
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
            if (QuickStart9eHotlink.stb.getSelectedLayers() != null){
               QuickStart9eHotlink.promoteitem.setEnabled(true);}
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
class AttrTab extends JDialog {
  JPanel panel1 = new JPanel();
  com.esri.mo2.map.dpy.Layer layer = QuickStart9eHotlink.layer4;
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
  com.esri.mo2.map.dpy.Layer layer = QuickStart9eHotlink.layer4;
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
      path = ((ShapefileFolder)(QuickStart9eHotlink.layer4.getLayerSource())).getPath();
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
  public void mouseClicked(MouseEvent me){
  }
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
class HotPick extends JDialog {
  String mystate = QuickStart9eHotlink.mystate;
  String mybird = null;
  String mybirdpic = null;
  JPanel jpanel = new JPanel();
  JPanel jpanel2 = new JPanel();
  String[][] stateBirds={{"Alabama","Yellowhammer","yellowhammer.jpg"},{"Alaska","Willow Ptarmigan","willow.jpg"},
    {"Arizona","Cactus Wren","cactuswren.jpg"},{"Arkansas","Northern Mockingbird","mockingbird.jpg"},
    {"California","California Quail","califquail.jpg"},{"Colorado","Lark Bunting","larkbunting.jpg"},
    {"Connecticut","American Robin","robin.jpg"},{"Delaware","Blue Hen Chicken","bluehenchicken.jpg"},
    {"District of Columbia","Wood Thrush","woodthrush.jpg"},{"Florida","Northern Mockingbird","mockingbird.jpg"},
    {"Georgia","Brown Thrasher","brownthrasher.jpg"},{"Hawaii","Hawaiian Goose","goose.jpg"},
    {"Idaho"," Mountain Bluebird","mtnbluebird.jpg"},{"Illinois","Northern Cardinal","cardinal.jpg"},
    {"Indiana","Northern Cardinal","cardinal.jpg"},{"Iowa","Eastern Goldfinch","goldfinch.jpg"},
    {"Kansas","Western Meadowlark","meadowlark.jpg"},{"Kentucky","Northern Cardinal","cardinal.jpg"},
    {"Louisiana","Brown Pelican","pelican.jpg"},{"Maine","Black-capped Chickadee","chickadee.jpg"},
    {"Maryland","Baltimore Oriole","oriole.jpg"},{"Massachusetts","Black-capped Chickadee","chickadee.jpg"},
    {"Michigan","American Robin","robin.jpg"},{"Minnesota","Common Loon","loon.jpg"},
    {"Mississippi","Northern Mockingbird","mockingbird.jpg"},{"Missouri","Eastern Bluebird","bluebird.jpg"},
    {"Montana","Northern Meadowlark","meadowlark.jpg"},{"Nebraska","Northern Meadowlark","meadowlark.jpg"},
    {"Nevada","Mountain Bluebird","mtnbluebird.jpg"},{"New Hampshire","Purple Finch","purplefinch.jpg"},
    {"New Jersey","Eastern Goldfinch","goldfinch.jpg"},{"New Mexico","Roadrunner","roadrunner.jpg"},
    {"New York","Eastern Bluebird","bluebird.jpg"},{"North Carolina","Northern Cardinal","cardinal.jpg"},
    {"North Dakota","Western Meadowlark","meadowlark.jpg"},{"Ohio","Northern Cardinal","cardinal.jpg"},
    {"Oklahoma","Scissor-tailed Flycatcher","flycatcher.jpg"},{"Oregon","Western Meadowlark","meadowlark.jpg"},
    {"Pennsylvania","Ruffed Grouse","grouse.jpg"},{"Rhode Island","Rhode Island Red Chicken","redchicken.jpg"},
    {"South Carolina","Northern Mockingbird","mockingbird.jpg"},{"South Dakota","Common Pheasant","pheasant.jpg"},
    {"Tennessee","Northern Mockingbird","mockingbird.jpg"},{"Texas","Northern Mockingbird","mockingbird.jpg"},
    {"Utah","California Gull","gull.jpg"},{"Vermont","Hermit Thrush","hermitthrush.jpg"},
    {"Virginia","Northern Cardinal","cardinal.jpg"},{"Washington","Willow Goldfinch","willowgoldfinch.jpg"},
    {"West Virginia","Northern Cardinal","cardinal.jpg"},
    {"Wisconsin","American Robin","robin.jpg"},{"Wyoming","Western Meadowlark","meadowlark.jpg"}};
  HotPick() throws IOException {
	setTitle("This was your pick");
    setBounds(250,250,350,350);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
	    setVisible(false);
	  }
    });
    for (int i = 0;i<51;i++)  {
	  if (stateBirds[i][0].equals(mystate)) {
        mybird = stateBirds[i][1];
	    mybirdpic = stateBirds[i][2];
	    break;
	  }
    }
    JLabel label = new JLabel(mystate+":   ");
    JLabel label2 = new JLabel(mybird);
    ImageIcon birdIcon = new ImageIcon(mybirdpic);
    JLabel birdLabel = new JLabel(birdIcon);
    jpanel2.add(birdLabel);
    jpanel.add(label);
    jpanel.add(label2);
    getContentPane().add(jpanel2,BorderLayout.CENTER);
    getContentPane().add(jpanel,BorderLayout.SOUTH);
  }
}
