// This program explores menus and listeners.  It uses the swing set. The
// swing set is not 'peer-based'.  This means that instead of translating
// java stuff into host machine window stuff, java just draws pictures onto
// blank host machine windows.   This takes more time, but makes the look
// and feel of swing set applications much more platform independent.
// JFrame is actually a subclass of Frame.    And JPanel is a subframe of
// JComponent, which is a subclass of Component.

import javax.swing.*; // javax indicates an extension, rather than a core
    // package;  there was a lot of debate over the name
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
public class JFramMenu extends JFrame implements ActionListener {
  public JFramMenu() {
    setTitle ("A Menu for Dimitrios");
    setBounds(50,50,400,300);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent we) {
        dispose();
        System.exit(0);
      }
    } );
    Container cPane = getContentPane();
    jp = new JPanel();
    cPane.add(jp);
    JMenuBar mbar = new JMenuBar();
    setJMenuBar(mbar);
    JMenu colorMenu = new JMenu("color");
    JMenuItem hel = new JMenuItem("heliotrope");
    JMenuItem per = new JMenuItem("periwinkle");
    JMenuItem mar = new JMenuItem("maroon");
    hel.addActionListener(this);
    per.addActionListener(this);
    mar.addActionListener(this);
    colorMenu.add(hel);
    colorMenu.add(per);
    colorMenu.add(mar);
    colorMenu.addSeparator();
    JMenu optionsMenu = new JMenu("brown");
    colorMenu.add(optionsMenu);
    JMenuItem sep = new JMenuItem("sepia");
    JMenuItem sien = new JMenuItem("sienna");
    JMenuItem umb = new JMenuItem("umber");
    sep.addActionListener(this);
    sien.addActionListener(this);
    umb.addActionListener(this);
    optionsMenu.add(sep);
    optionsMenu.add(sien);
    optionsMenu.add(umb);
    JCheckBoxMenuItem jcbmi = new JCheckBoxMenuItem("white");
    jcbmi.addActionListener(this);
    colorMenu.add(jcbmi);

    mbar.add(colorMenu);
  }
  private JPanel jp;
  public void actionPerformed(ActionEvent evt) {
      Object source =  evt.getSource();  // returns name of button where event
                                     // occurred
      if (source instanceof JMenuItem) {
        String arg = evt.getActionCommand();
        System.out.println(arg);
        Color color = getBackground(); // must initiaize
        if (arg.equals("sepia"))
          color = new Color(100,50,0);   
        else if (arg.equals("sienna"))
          color = new Color(150,100,0);
        else if (arg.equals("umber"))
          color = new Color(100,55,25);
        else if (arg.equals("heliotrope"))
          color = new Color(200,100,200);
        else if (arg.equals("periwinkle"))
          color = new Color(150,150,250);
        else if (arg.equals("maroon"))
          color = new Color(250,0,100);
        else color = Color.white;
        jp.setBackground(color);
        //setColor(color);
        repaint();
        System.out.println("Background color is " + color); // toString is
                 // defined for the class Color
      }
  }
  public static void main(String [] aa) {
    Frame f = new JFramMenu();
    f.show();
  }
}
