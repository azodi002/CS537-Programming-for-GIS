// This version explores hotlink using Identify class, as well as
// a bolt cursor icon, and clicking on a state pops up a window
// showing the state bird.

import javax.swing.*;
import java.io.*;
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
import java.util.StringTokenizer;
import com.esri.mo2.map.dpy.BaseFeatureLayer;
import com.esri.mo2.map.draw.SimpleMarkerSymbol;
import com.esri.mo2.map.draw.BaseSimpleRenderer;
import com.esri.mo2.cs.geom.Point; //using Envelope, Point, BasePointsArray
import com.esri.mo2.cs.geom.BasePointsArray;
import javax.swing.table.DefaultTableModel;
import com.esri.mo2.map.draw.TrueTypeMarkerSymbol;
import com.esri.mo2.map.draw.RasterMarkerSymbol;


public class Homework3 extends JFrame {
  public static int pic_count = 0;
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
  JMenuItem printitem  = new JMenuItem("print",new ImageIcon("print.gif"));
  JMenuItem addlyritem = new JMenuItem("add layer",new ImageIcon("addtheme.gif"));
  JMenuItem remlyritem = new JMenuItem("remove layer",new ImageIcon("delete.gif"));
  JMenuItem propsitem  = new JMenuItem("Legend Editor",new ImageIcon("properties.gif"));
  
  ImageIcon contact_pic = new ImageIcon(new ImageIcon("help.png").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
  ImageIcon help_pic    = new ImageIcon(new ImageIcon("contact.png").getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
  
  JMenu help = new JMenu("Help");
  JMenuItem contact_us = new JMenuItem("Contact us", help_pic);
  JMenuItem help_tool = new JMenuItem("Help Tool",contact_pic);
  
  
  Toc toc = new Toc();
  String s1 = "C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\SAN DIEGO PROJECT.shp";
  //String s2 = "C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\capitals.shp";
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
  
  ImageIcon hotlink_pic = new ImageIcon(new ImageIcon("hot_icon.png").getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
  JButton hotjb = new JButton(hotlink_pic);

  /** added **/
  JButton XYjb = new JButton("XY");
  ActionListener xyListener;
  ActionListener help_lis;
  ActionListener contact_lis;
  BasePointsArray bpa = new BasePointsArray();
  /** added **/
  Arrow arrow = new Arrow();
  ActionListener lis;
  ActionListener layerlis;
  ActionListener layercontrollis;
  TocAdapter mytocadapter;Toolkit tk = Toolkit.getDefaultToolkit();
  Image bolt = tk.getImage("thunder.png");  // 16x16 gif file
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
	  System.out.println("IM INNNNNNNNNNNNNNNNNNNNNNN HEREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
      FeatureLayer flayer2 = (FeatureLayer) pe.getLayer();
      com.esri.mo2.data.feat.Cursor c = pe.getCursor();
      Feature f = null;
      Fields fields = null;
      if (c != null) {
        System.out.println("C NOT NULL");
		f = (Feature)c.next();
	  }
      fields = f.getFields();
      String sname = fields.getField(4).getName(); //gets col. name for state name
	  System.out.println("SNAME IS " + sname);
      mystate = (String)f.getValue(4);
	  System.out.println("MY STATE IS" + mystate);
      try {
	    HotPick hotpick = new HotPick();//opens dialog window with Duke in it
	    hotpick.setVisible(true);
      } catch(Exception e){}
    }
  };

  static Envelope env;
  public Homework3() {
    super("Quick Start");
    this.setBounds(150,150,900,650);
    zptb.setMap(map);
    stb.setMap(map);
    setJMenuBar(mbar);
		
	help_lis = new ActionListener() 
	{
		public void actionPerformed(ActionEvent ae)
		{
			System.out.println("INSIDE HELP LIS!!!!!!!!!!!!!!!!");
			JPanel pan = new JPanel();
			pan.setLayout(new FlowLayout());
			JLabel title_label  = new JLabel("Quick HELP INFORMATION");
			JLabel first_label  = new JLabel("[1]. The XY Button allows you to load a CSV and show ICONS on county map.");
			JLabel second_label = new JLabel("[2]. Click on layer selection allows you to activate (Theme->Open Attribute Table)");
			JLabel third_label  = new JLabel("[3]. COLOR SYSTEM of POINTS: \n * Red: Ratings of 4.0 - 4.3 \n * Yellow: Ratings of 4.4 - 4.6 * \n Green: Ratings of 4.7 - 5.0");
			JLabel fourth_label = new JLabel("[4]. Click each Kabob Skewer (points) to see image of location/restaurant, and information regarding the place");
			
			
			first_label.setFont(new Font("Serif", Font.PLAIN, 17));
			second_label.setFont(new Font("Serif", Font.PLAIN, 17));
			third_label.setFont(new Font("Serif", Font.PLAIN, 17));
			title_label.setFont(new Font("Serif", Font.PLAIN, 30));
			fourth_label.setFont(new Font("Serif", Font.PLAIN, 17));
			
			pan.add(title_label);
			pan.add(first_label);
			pan.add(second_label);
			pan.add(third_label);
			pan.add(fourth_label);
			
			JDialog jd = new JDialog();
			jd.add(pan);
			jd.setSize(500, 500);
			jd.setVisible(true);
		}
	};
	
	contact_lis = new ActionListener() 
	{
		public void actionPerformed(ActionEvent ae)
		{
			System.out.println("INSIDE CONTACT LIS!!!!!!!!!!!!!!!!");
			JPanel pan = new JPanel();
			pan.setLayout(new FlowLayout());
			JLabel title_label  = new JLabel("Contact Page \n");
			JLabel first_label  = new JLabel("Name of programmer: Omid Azodi");
			JLabel second_label = new JLabel("Class of development: CS-537 (Programming for GIS)");
			JLabel third_label  = new JLabel("Professor Eckberg contact @ ceckberg@mail.sdsu.edu");
			
			first_label.setFont(new Font("Serif", Font.PLAIN, 17));
			second_label.setFont(new Font("Serif", Font.PLAIN, 17));
			title_label.setFont(new Font("Serif", Font.PLAIN, 30));
			third_label.setFont(new Font("Serif", Font.PLAIN, 17));
			
			pan.add(title_label);
			pan.add(first_label);
			pan.add(second_label);
			pan.add(third_label);
			
			
			JDialog jd = new JDialog();
			jd.add(pan);
			jd.setSize(500, 500);
			jd.setVisible(true);
		}
	};

	xyListener = new ActionListener() {
	  public void actionPerformed(ActionEvent ae) {
	    JDialog jd = new JDialog();
		JFileChooser jfc = new JFileChooser();
		jfc.showOpenDialog(jd);
	
		File file = jfc.getSelectedFile();
		String s; // = in.readLine();
		double x,y;
		int n = 0;
		try 
		{
			FileReader fred = new FileReader(file);
			BufferedReader in = new BufferedReader(fred);
			while((s = in.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(s,",");
				x = Double.parseDouble(st.nextToken());
				y = Double.parseDouble(st.nextToken());
				bpa.insertPoint(n++, new com.esri.mo2.cs.geom.Point(x,y));
				System.out.println("We are reading points " + x + " " + y);
			}//while((s))				
		} catch(IOException e) {}
	
		BasePointsArray bpa_red = new BasePointsArray();
		BasePointsArray bpa_poi = new BasePointsArray();
		BasePointsArray third_poi = new BasePointsArray();
		System.out.println("BPA SIZE IS:" + bpa.size());
		/*for(int i = 0; i< bpa.size(); i++) 
		{
			System.out.println("INSIDE LOOOOOOOOOOOOP");
			Point p = new Point(bpa.getPoint(i));
			
			if(i < 4) 
			{
		      bpa_red.insertPoint(i,p);
			  System.out.println("RED BPA");			  
			}
			
			else if( i >=4 && i < 8)
			{
			  bpa_poi.insertPoint(i-4,p);
			  System.out.println("green BPA");
			}				
			
			else
			{
			  third_poi.insertPoint(i-8,p);
			  System.out.println("Yellow BPA");
			}
		}//for(int i = 0)*/

		MyBaseFeatureLayer xyf1 = new MyBaseFeatureLayer(bpa,map);
		//MyBaseFeatureLayer xyf1 = new MyBaseFeatureLayer(bpa_red,map);
		//MyBaseFeatureLayer xyf2 = new MyBaseFeatureLayer(bpa_poi,map);
		//MyBaseFeatureLayer xyf3 = new MyBaseFeatureLayer(third_poi,map);
		xyf1.setVisible(true);
		//xyf2.setVisible(true);
		//xyf3.setVisible(true);
		map.getLayerset().addLayer(xyf1);
		//map.getLayerset().addLayer(xyf2);
		//map.getLayerset().addLayer(xyf3);
		map.redraw();
		xyf1.setVisible(true);
		//xyf2.setVisible(true);
		//xyf3.setVisible(true);
		map.repaint();
		map.redraw();		
		
	    } //actionPerformed
	  };//ActionListener
    
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
	  String county_name = "       County Name: San Diego";
	  s += county_name;
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
	XYjb.addActionListener(xyListener);//added
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
	help_tool.addActionListener(help_lis);
	contact_us.addActionListener(contact_lis);
	help.add(help_tool);
	help.add(contact_us);
    mbar.add(file);
    mbar.add(theme);
    mbar.add(layercontrol);
	mbar.add(help);
    prtjb.addActionListener(lis);
    prtjb.setToolTipText("print map");
    addlyrjb.addActionListener(lis);
    addlyrjb.setToolTipText("add layer");
    hotlink.addPickListener(picklis);
    hotlink.setPickWidth(50);// sets tolerance for hotlink clicks
    hotjb.addActionListener(lis);
    hotjb.setToolTipText("hotlink tool--click somthing to maybe see a picture");
    ptrjb.addActionListener(lis);
    prtjb.setToolTipText("pointer");
    jtb.add(prtjb);
	jtb.add(addlyrjb);
    jtb.add(ptrjb);
	XYjb.setToolTipText("add csv"); //added tool tip text
	jtb.add(XYjb);//added
    jtb.add(hotjb);
    myjp.add(jtb);
    myjp.add(zptb);
    myjp.add(stb);
    myjp2.add(statusLabel);
    getContentPane().add(map, BorderLayout.CENTER);
    getContentPane().add(myjp,BorderLayout.NORTH);
    getContentPane().add(myjp2,BorderLayout.SOUTH);
    addShapefileToMap(layer,s1);
    //addShapefileToMap(layer2,s2);
    getContentPane().add(toc, BorderLayout.WEST);
  }
  private void addShapefileToMap(Layer layer,String s) {
    String datapath = s; //"C:\\ESRI\\MOJ10\\Samples\\Data\\USA\\States.shp";
    layer.setDataset("0;"+datapath);
    map.add(layer);
  }
  public static void main(String[] args) {
    Homework3 qstart = new Homework3();
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
            if (Homework3.stb.getSelectedLayers() != null){
               Homework3.promoteitem.setEnabled(true);}
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
  com.esri.mo2.map.dpy.Layer layer = Homework3.layer4;
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
      tc.setMinWidth(100);
    }
    getContentPane().add(scroll,BorderLayout.CENTER);
  }
}
class MyTableModel extends AbstractTableModel {
 // the required methods to implement are getRowCount,
 // getColumnCount, getValueAt
  com.esri.mo2.map.dpy.Layer layer = Homework3.layer4;
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
      path = ((ShapefileFolder)(Homework3.layer4.getLayerSource())).getPath();
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
  String mystate = Homework3.mystate;
  String mybird = null;
  String mybirdpic = null;
  String price = null;
  String color = null;
  JPanel jpanel = new JPanel();
  JPanel jpanel2 = new JPanel();
  JPanel jpanel3 = new JPanel();
//{"Alborz Restaurant", "Sufi", "North Park Bakery & Grill", "Bandar", "Darband", "The Kabob Shop", "Grill House Cafe", "Kolbeh", "Arianna Kabob House", "Pamir Kabob House", "Kabul West", "Khyber Pass"};

  String[][] stateBirds=
	{	{"Alborz Restaurant","Link: http://alborzinc.com/","alborz.jpg", "Price Range: $", "green_icon.png"},
		{"Sufi","Link: http://sufisd.com/","sufi.jpg", "Price Range: $$", "green_icon.png"},
		
		{"North Park Bakery & Grill","Link: http://www.northparkproducepoway.com/","north.jpg", "Price Range: $", "red_icon.png"},
		{"Bandar","Link: http://www.bandarrestaurant.com/","bandar.jpg","Price Range: $$$", "red_icon.png"},
		{"Darband","Link: http://www.5thavenuegrill.com/","darband.png", "Price Range: $$", "red_icon.png"},
		{"The Kabob Shop","Link: http://www.thekebabshop.com/menu/","kabob.jpg","Price Range: $", "red_icon.png"},
		{"Grill House Cafe","Link: https://www.grillhousecafe.com/","grill.jpg", "Price Range: $", "yellow_icon.png"},
		{"Kolbeh","Link: https://www.yelp.com/biz/kolbeh-san-diego","kolbeh.jpg", "Price Range: $$", "yellow_icon.png"},
		{"Arianna Kabob House","Link: https://www.yelp.com/biz/ariana-kabob-house-san-diego","arianna.jpg", "Price Range: $$", "yellow_icon.png"},
		{"Pamir Kabob House","Link: http://www.pamirkabobhouse.com/","pamir.jpg", "Price Range: $", "red_icon.png"},
		{"Kabul West","Link: http://kabulwestcatering.com/","kabul.jpg", "Price Range: $", "green_icon.png" },
		{"Khyber Pass","Link: https://www.khyberpasshillcrest.com/","pass.jpg", "Price Range: $$$", "green_icon.png"}
	};
  HotPick() throws IOException {
	setTitle("Restaurant");
    setBounds(600,600,650,650);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
	    setVisible(false);
	  }
    });
    for (int i = 0;i<51;i++)  {
	  if (stateBirds[i][0].equals(mystate)) {
        mybird = stateBirds[i][1];
	    mybirdpic = stateBirds[i][2];
		price = stateBirds[i][3];//price
		color = stateBirds[i][4];//color
		System.out.println("myBird" + mybird);
		System.out.println("myPic" + mybirdpic);
		System.out.println("price = " + price);
	    break;
	  }
    }
    JLabel label = new JLabel(mystate+":   ");
    JLabel label2 = new JLabel(mybird);
	JLabel price_label = new JLabel(price);
	ImageIcon birdIcon = new ImageIcon(new ImageIcon(mybirdpic).getImage().getScaledInstance(450, 450, Image.SCALE_DEFAULT));
	ImageIcon color_icon = new ImageIcon(new ImageIcon(color).getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
	
    JLabel birdLabel = new JLabel(birdIcon);
	JLabel colorLabel = new JLabel(color_icon);
	jpanel3.add(colorLabel);
    jpanel2.add(birdLabel);
    jpanel.add(label);
    jpanel.add(label2);
	jpanel.add(price_label);
    getContentPane().add(jpanel2,BorderLayout.CENTER);
    getContentPane().add(jpanel,BorderLayout.SOUTH);
	getContentPane().add(jpanel3,BorderLayout.NORTH);
  }
}

class MyBaseFeatureLayer extends BaseFeatureLayer {
  BaseFields fields;
  private java.util.Vector featureVector;
  public MyBaseFeatureLayer(BasePointsArray bpa,Map map) {
	createFeaturesAndFields(bpa,map);
	//BaseFeatureClass bfc = getFeatureClass("MyPoints",bpa);
	BaseFeatureClass bfc = getFeatureClass("TESTING_CSV.csv",bpa);
	setFeatureClass(bfc);
	BaseSimpleRenderer rd = new BaseSimpleRenderer();
	//SimpleMarkerSymbol sms= new SimpleMarkerSymbol();
	//TrueTypeMarkerSymbol ttm = new TrueTypeMarkerSymbol();
	//ttm.setFont(new Font("ESRI Transportation & Civic", Font.PLAIN, 30));
	//ttm.setColor(new Color(255,0,0));
	//sms.setType(SimpleMarkerSymbol.CIRCLE_MARKER);
	//sms.setSymbolColor(new Color(255,0,20));
	//sms.setWidth(5);
	//ttm.setCharacter("95");
	//rd.setSymbol(sms);
	//rd.setSymbol(ttm);
	RasterMarkerSymbol rms = new RasterMarkerSymbol();
	rms.setSizeX(30);
	rms.setSizeY(30);
	//if(Homework3.pic_count == 0) {
		rms.setImageString("C:/esri/MOJ20/examples/POI.png");
		//System.out.println("RED ICON");
	//}
	//else if(Homework3.pic_count == 1) {
		//rms.setImageString("C:/esri/MOJ20/examples/yellow_icon.png");
		//System.out.println("POI ICON");
	//}
	
	//else if(Homework3.pic_count == 2) {
		//rms.setImageString("C:/esri/MOJ20/examples/green_icon.png");
		//System.out.println("POI ICON");
	//}
	//Homework3.pic_count = Homework3.pic_count + 1;
	rd.setSymbol(rms);
	setRenderer(rd);
	layerCapabilities lc = new layerCapabilities();
	setCapabilities(lc);
  }
  private void createFeaturesAndFields(BasePointsArray bpa,Map map) {
	System.out.println("Inside creatFeaturesAndFields()");
	featureVector = new java.util.Vector();
	fields = new BaseFields();
	createDbfFields();
	String[] places = {"Alborz Restaurant", "Sufi", "North Park Bakery & Grill", "Bandar", "Darband", "The Kabob Shop", "Grill House Cafe", "Kolbeh", "Arianna Kabob House", "Pamir Kabob House", "Kabul West", "Khyber Pass"};
	String[] hours = {"11AM -  10PM", "11AM - 9PM", "10AM - 7:30PM", "12PM - 6PM", "11AM - 11PM", "11AM - 10:30PM", "10AM - 10PM" ,"11AM - 8PM", "10AM - 7PM", "10:30AM -8:00PM", "11:00AM-7:00PM", "10AM-7PM"};
	for(int i=0;i<bpa.size();i++) {
	  BaseFeature feature = new BaseFeature();  //feature is a row
	  feature.setFields(fields);
	  Point p = new Point(bpa.getPoint(i));
	  feature.setValue(0,p);
	  feature.setValue(1, new Integer(i));  // THIS IS ID
	  feature.setValue(2, new Double(p.getX()));
	  feature.setValue(3, new Double(p.getY()));
	  feature.setValue(4, new String(places[i]));
	  feature.setValue(5, new String(hours[i]));
	  feature.setDataID(new BaseDataID("TESTING_CSV.csv",i));
	  featureVector.addElement(feature);
	  System.out.println("here we are");
    }
  }
  private void createDbfFields() {
    System.out.println("Inside createDbfFields()");
	fields.addField(new BaseField("#SHAPE#",Field.ESRI_SHAPE,0,0));
	fields.addField(new BaseField("ID",java.sql.Types.INTEGER,9,0));
	fields.addField(new BaseField("Longitude",java.sql.Types.VARCHAR,12,0));
	fields.addField(new BaseField("Latitude",java.sql.Types.VARCHAR,12,0));
	fields.addField(new BaseField("Name of Place",java.sql.Types.VARCHAR,19,0));
	fields.addField(new BaseField("Hours Opened",java.sql.Types.VARCHAR,19,0));
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
