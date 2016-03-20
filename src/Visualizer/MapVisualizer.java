
package Visualizer;

import Server.*;
import java.util.Vector;
import java.awt.*;

/**
 * This class was generated by a SmartGuide.
 * 
 */
public class MapVisualizer implements java.awt.event.WindowListener {

	public void quit() 
	{
		ivjDialogBox.dispose();
	}

	private Canvas ivjArena = null;
	private Dialog ivjDialogBox = null;
	public java.util.Vector mapobjectlist;
	public Image image = null;
	public Image botimage = null;
        public int screenSize=500;
        public boolean hollowScan=false;
	public boolean noScan=false;
        public boolean drawDead=false;
	
	public Image imagebuffer;
	public Graphics g;
	public Graphics gbuffer;

public int xlaty (int y) {
        return screenSize-y;
}

public double xlaty (double y) {
        return (double) screenSize - y;
}

public MapVisualizer() {
	super();
	initialize();
}

public void toFront() {
        ivjDialogBox.toFront();
}

public void hide() {
        ivjDialogBox.setVisible(false);
}

public void show() {
        ivjDialogBox.show();
}

public void setDrawDead(boolean dead) {
        drawDead = dead;
}

public void setScreenSize(int size) {
        screenSize = size;
        ivjDialogBox.setBounds(0, 0, screenSize+15, screenSize+25);
        ivjDialogBox.setLocation(300, 0);
        ivjArena.setBounds(5, 25, screenSize+5, screenSize+25);
}

public void setHollowScan(boolean scan) {
        hollowScan = scan;
}

public void setNoScan(boolean scan) {
	noScan = scan;
}

/**
 * 
 * @param g java.awt.Graphics
 */
public void arenaPaint(Graphics g) {
//		getArena().paint(g);
//		UpdateMap(null, null);
}
/**
 * 
 * @param g java.awt.Graphics
 */
public void arenaUpdate(Graphics g) {
//		getArena().update(g);
//		UpdateMap(null, null);
}
/**
 * conn0:  (DialogBox.window.windowClosing(java.awt.event.WindowEvent) --> DialogBox.dispose())
 * @param arg1 java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void conn0(java.awt.event.WindowEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getDialogBox().dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * This method was created by a SmartGuide.
 */
public void drawBot (MapObject mobj, Color color) {
	g.setColor(color);
	// the radii of all bots are 10
        // also, the collide method in Server has this number hard-coded
	if (botimage != null) {
                g.drawImage(botimage, mobj.x-10, mobj.y-10, null);
        } else {
                Polygon poly = new Polygon();
                if (mobj.hit) {
                    if (drawDead) {
                        /* draw diamond */
                        poly.addPoint(mobj.x-(int) (15*screenSize/500), mobj.y-(int) (0*screenSize/500));
                        poly.addPoint(mobj.x-(int) (2*screenSize/500), mobj.y-(int) (2*screenSize/500));
                        poly.addPoint(mobj.x+(int) (0*screenSize/500), mobj.y-(int) (15*screenSize/500));
                        poly.addPoint(mobj.x+(int) (2*screenSize/500), mobj.y-(int) (2*screenSize/500));
                        poly.addPoint(mobj.x+(int) (15*screenSize/500), mobj.y-(int) (0*screenSize/500));
                        poly.addPoint(mobj.x+(int) (2*screenSize/500), mobj.y+(int) (2*screenSize/500));
                        poly.addPoint(mobj.x+(int) (0*screenSize/500), mobj.y+(int) (15*screenSize/500));
                        poly.addPoint(mobj.x-(int) (2*screenSize/500), mobj.y+(int) (2*screenSize/500));
                        g.fillPolygon(poly);
                    } else return;
                } else if (mobj.dir_x==0 && mobj.dir_y==0) {
                        g.fillOval(mobj.x-(int) (5*screenSize/500), mobj.y-(int) (5*screenSize/500), 10, 10);
                        /* draw health bar */
                        g.fillRect(mobj.x-(int) (24*screenSize/500), mobj.y-(int) (29*screenSize/500), (int) (39.0f*(screenSize/500)*mobj.health/100.0f), (int) 9 * screenSize/500);
                        g.setColor(Color.black);
                        g.drawRect(mobj.x-(int) (25*screenSize/500), mobj.y-(int) (30*screenSize/500), (int) (40*screenSize/500), (int) (10*screenSize/500));
                } else {
//                        poly.addPoint(mobj.x + (int) (100.0f * mobj.dir_x) - (int) (-10.0f * mobj.dir_y) - (int) (10.0f * mobj.dir_x), mobj.y - (int) (100.0f * mobj.dir_y) - (int) (-10.0f * mobj.dir_x) + (int) (10.0f * mobj.dir_y));
//                        poly.addPoint(mobj.x + (int) (100.0f * mobj.dir_x) - (int) (10.0f * mobj.dir_y)  - (int) (10.0f * mobj.dir_x), mobj.y - (int) (100.0f * mobj.dir_y) - (int) (10.0f * mobj.dir_x) + (int) (10.0f * mobj.dir_y));
                        poly.addPoint(mobj.x + (int) ((30.0f*screenSize/500) * mobj.dir_x) - (int) ((10.0f*screenSize/500)* mobj.dir_x), mobj.y - (int) ((30.0f*screenSize/500) * mobj.dir_y) + (int) ((10.0f*screenSize/500) * mobj.dir_y));
                        poly.addPoint(mobj.x - (int) ((5.0f*screenSize/500) * mobj.dir_y) - (int) ((10.0f*screenSize/500)* mobj.dir_x), mobj.y - (int) ((5.0f*screenSize/500) * mobj.dir_x) + (int) ((10.0f*screenSize/500) * mobj.dir_y));
                        poly.addPoint(mobj.x - (int) ((-5.0f*screenSize/500) * mobj.dir_y) - (int) ((10.0f*screenSize/500) * mobj.dir_x), mobj.y - (int) ((-5.0f*screenSize/500) * mobj.dir_x) + (int) ((10.0f*screenSize/500) * mobj.dir_y));
			if (mobj.iscloaked) g.drawPolygon(poly);
                        else g.fillPolygon(poly);

                        /* draw health bar */
                        g.fillRect(mobj.x-(int) (24*screenSize/500), mobj.y-(int) (29*screenSize/500), (int) ( (39*screenSize/500)*(mobj.health/100.0f) ), (int) (9*screenSize/500));
                        g.setColor(Color.black);
                        g.drawRect(mobj.x-(int) (25*screenSize/500), mobj.y-(int) (30*screenSize/500), (int) (40*screenSize/500), (int) (10*screenSize/500));

                        Polygon box = new Polygon();

			int ax, ay, aX, aY;
		        if (mobj.x0 <= mobj.x) { ax = mobj.x0; aX = mobj.x; } else { ax = mobj.x; aX = mobj.x0;}
			if (mobj.y0 <= mobj.y) { ay = mobj.y0; aY = mobj.y; } else { ay = mobj.y; aY = mobj.y0;}
                        box.addPoint(ax - (int) (5*screenSize/500), aY + (int) (5*screenSize/500)); box.addPoint(aX + (int) (5*screenSize/500), aY + (int) (5*screenSize/500));
                        box.addPoint(aX + (int) (5*screenSize/500), ay - (int) (5*screenSize/500)); box.addPoint(ax - (int) (5*screenSize/500), ay - (int) (5*screenSize/500));
//                        box.addPoint(mobj.x + (int) (100.0f * mobj.dir_x) + (int) (10.0f / java.lang.Math.sqrt(2) ), mobj.y - (int) (100.0f * mobj.dir_y) - (int) (10.0f * mobj.dir_y));
//                        box.addPoint(mobj.x + (int) (100.0f * mobj.dir_x) + (int) (10.0f / java.lang.Math.sqrt(2) ), mobj.y + (int) (10.0f * mobj.dir_y));
//                        box.addPoint(mobj.x - (int) (10.0f * mobj.dir_x), mobj.y + (int) (10.0f * mobj.dir_y));
//                        box.addPoint(mobj.x - (int) (10.0f * mobj.dir_x), mobj.y - (int) (100.0f * mobj.dir_y) - (int) (10.0f * mobj.dir_y));
                        g.drawPolygon(box);

                        g.drawOval(mobj.x-(int) (5*screenSize/500), mobj.y-(int) (5*screenSize/500), (int) (10*screenSize/500), (int) (10*screenSize/500));
                }
                /* draw scan arc */
	     if (!noScan) {
                if (hollowScan) {
//                        g.setColor(Color.black);
                        g.setColor(color);
                        g.drawArc(mobj.x-(int) (100*screenSize/500), mobj.y-(int) (100*screenSize/500), (int) (200*screenSize/500), (int) (200*screenSize/500),
                                (int) (mobj.scan_dir*180.0f/java.lang.Math.PI),
                                (int) (mobj.scan_arc*180.0f/java.lang.Math.PI));
                } else {
			g.setColor(Color.black);
                        g.setXORMode(Color.white);
                        g.fillArc(mobj.x-(int) (100*screenSize/500), mobj.y-(int) (100*screenSize/500), (int) (200*screenSize/500), (int) (200*screenSize/500),
                                (int) (mobj.scan_dir*180.0f/java.lang.Math.PI),
                                (int) (mobj.scan_arc*180.0f/java.lang.Math.PI));
                        g.setPaintMode();
                }
	     }
/*
                Polygon scan = new Polygon();
                scan.addPoint( mobj.x, mobj.y);
                scan.addPoint( mobj.x + (int) (500*mobj.scan1_x), mobj.y - (int) (500*mobj.scan1_y));
                scan.addPoint( mobj.x + (int) (500*mobj.scan2_x), mobj.y - (int) (500*mobj.scan2_y));
                g.fillPolygon(scan);
*/
//                g.drawLine(mobj.x, mobj.y, mobj.x+ mobj.dir_x, mobj.y-mobj.dir_y);
//                g.drawLine(mobj.x, mobj.y, mobj.x+mobj.scan1_x, mobj.y-mobj.scan1_y);
//                g.drawLine(mobj.x, mobj.y, mobj.x+mobj.scan2_x, mobj.y-mobj.scan2_y);
        }
	return;
}
/**
 * This method was created by a SmartGuide.
 */
public void drawGrid ( ) {
	int i;

	g.setColor(Color.black);
        for (i = 0; i <= screenSize; i+=10) {
                g.drawLine(i, 0, i, screenSize);
                g.drawLine(0, i, screenSize, i);
	}
	
	return;
}
/**
 * This method was created by a SmartGuide.
 */
public void drawProjectile(MapObject mobj, Color color) {
	g.setColor(color);
	// the radii of all projectiles are 3
        // also, the collide method in Server has this number hard-coded
//        g.fillOval(mobj.x-1, mobj.y-1, 2, 2);
        if (mobj.hit) {
                /* draw diamond */
                g.setColor(Color.red);
                Polygon poly = new Polygon();
                poly.addPoint(mobj.x-(int) (15*screenSize/500), mobj.y-(int) (0*screenSize/500));
                poly.addPoint(mobj.x-(int) (2*screenSize/500), mobj.y-(int) (2*screenSize/500));
                poly.addPoint(mobj.x+(int) (0*screenSize/500), mobj.y-(int) (15*screenSize/500));
                poly.addPoint(mobj.x+(int) (2*screenSize/500), mobj.y-(int) (2*screenSize/500));
                poly.addPoint(mobj.x+(int) (15*screenSize/500), mobj.y-(int) (0*screenSize/500));
                poly.addPoint(mobj.x+(int) (2*screenSize/500), mobj.y+(int) (2*screenSize/500));
                poly.addPoint(mobj.x+(int) (0*screenSize/500), mobj.y+(int) (15*screenSize/500));
                poly.addPoint(mobj.x-(int) (2*screenSize/500), mobj.y+(int) (2*screenSize/500));
                g.fillPolygon(poly);
                g.setColor(Color.black);
//                g.setXORMode(Color.white);
//                g.fillOval(mobj.x - 5, mobj.y - 5, 10, 10);
//                g.setPaintMode();
        } else {
                if (mobj.x0 == 0 || mobj.y0 == 0)
                        g.fillOval(mobj.x-1, mobj.y-1, 1, 1);
                else g.drawLine(mobj.x0, mobj.y0, mobj.x, mobj.y);
        }
	return;
}


public void drawObs(MapObject mobj) {
	switch (mobj.ID) {
        case Obstacle.WALL:
		g.setColor(Color.black);
		break;
        case Obstacle.HEALTH:
		g.setColor(Color.white);
		break;
        case Obstacle.BATTERY:
		g.setColor(Color.green);
		break;
        case Obstacle.AMMO:
		g.setColor(Color.orange);
		break;
        case Obstacle.MINE:
		g.setColor(Color.red);
		break;
	}

        if (mobj.ID == Obstacle.MINE && mobj.hit) {
                Polygon poly = new Polygon();
                poly.addPoint(mobj.x-(int) (15*screenSize/500), mobj.y-(int) (0*screenSize/500));
                poly.addPoint(mobj.x-(int) (2*screenSize/500), mobj.y-(int) (2*screenSize/500));
                poly.addPoint(mobj.x+(int) (0*screenSize/500), mobj.y-(int) (15*screenSize/500));
                poly.addPoint(mobj.x+(int) (2*screenSize/500), mobj.y-(int) (2*screenSize/500));
                poly.addPoint(mobj.x+(int) (15*screenSize/500), mobj.y-(int) (0*screenSize/500));
                poly.addPoint(mobj.x+(int) (2*screenSize/500), mobj.y+(int) (2*screenSize/500));
                poly.addPoint(mobj.x+(int) (0*screenSize/500), mobj.y+(int) (15*screenSize/500));
                poly.addPoint(mobj.x-(int) (2*screenSize/500), mobj.y+(int) (2*screenSize/500));
                g.fillPolygon(poly);
	} else g.fillOval(mobj.x-(int) (5*screenSize/500), mobj.y-(int) (5*screenSize/500), 10, 10);
}

/**
 * Return the Arena property value.
 * @return java.awt.Canvas
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private Canvas getArena() {
	if (ivjArena == null) {
		try {
			ivjArena = new java.awt.Canvas();
			ivjArena.setName("Arena");
                        ivjArena.setBackground(java.awt.Color.gray);
                        ivjArena.setBounds(5, 25, screenSize+5, screenSize+25);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	return ivjArena;
}
/**
 * Return the DialogBox property value.
 * @return java.awt.Dialog
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private Dialog getDialogBox() {
	if (ivjDialogBox == null) {
		try {
			ivjDialogBox = new java.awt.Dialog(new java.awt.Frame());
			ivjDialogBox.setName("DialogBox");
			ivjDialogBox.setLayout(null);
                        ivjDialogBox.setBounds(45, 0, screenSize+15, screenSize+25);
                        ivjDialogBox.setLocation(300, 0);
			ivjDialogBox.setTitle("JavaBot Arena");
			ivjDialogBox.add(getArena(), getArena().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	return ivjDialogBox;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() {
	// user code begin {1}
	// user code end
	getDialogBox().addWindowListener(this);
}
/**
 * Initialize class
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	// user code begin {1}
		Dialog dialog = getDialogBox();
		dialog.setVisible(true);
                gbuffer = ivjArena.getGraphics();
	// user code end
	initConnections();
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		Visualizer.MapVisualizer aMapVisualizer = new Visualizer.MapVisualizer();
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.lang.Object");
	}
}
/**
 * This method was created by a SmartGuide.
 */
public void UpdateMap (Vector updatedlist, Image img, Image imgbot) {
	int i, size;
	MapObject mobj;
	Color color;
	
	if (imagebuffer == null) {
		imagebuffer = ivjArena.createImage(500,500);
//                g = gbuffer;
                g = imagebuffer.getGraphics();
	}	

	if (img != null) {
		image = img;
	}
	if (imgbot != null) {
		botimage = imgbot;
	}
	if (image != null) {
		g.drawImage(image, 0, 0, null);
	} else {
                g.clearRect(0, 0, 500, 500);
//                g.setColor(Color.gray);
//                g.fillRect(0, 0, 500, 500);
//		drawGrid();
	}	
		
	if (updatedlist != null) {
		mapobjectlist = updatedlist;
	}
	
	size = mapobjectlist.size();
	if (mapobjectlist != null) {
		for (i=0; i < size; i++) {
			mobj = (MapObject) mapobjectlist.elementAt(i);
//			color = Color.gray;
//                      int r, g, b;
//			int r = (int) java.lang.Math.random()*255;
//			int g = (int) java.lang.Math.random()*255;
//			int b = (int) java.lang.Math.random()*255;
//			java.util.Random rnd = new java.util.Random(i*255);
//                      color = new Color(rnd.nextdouble(), rnd.nextdouble(), rnd.nextdouble());
			color = Color.black;
			if (i > 6)
			switch (i%3) {
				case 0:
					color = new Color((i+100)/(size+100)*255, 100, 50);
					break;
				case 1:
					color = new Color(50, (i+100)/(size+100)*255, 100);
					break;
				case 2:
					color = new Color(100, 100, (i+100)/(size+100)*255);
			}
				
			switch (i) {
				case 0:
					color = Color.black;
					break;
				case 1:
					color = Color.orange;
					break;
				case 2:
					color = Color.red;
					break;
				case 3:
					color = Color.blue;
					break;
				case 4:
					color = Color.green;
					break;
				case 5:
					color = Color.pink;
					break;
				case 6:
					color = Color.yellow;
					break;
			}
                        
			if (mobj.ID < 0) {
				drawObs(mobj);
			}
			else if ( mobj.ID < 1000 )  {
                                drawBot(mobj, color);
                        }
			// black is hard-coded as the projectile color
                        else if ( mobj.ID >= Server.PROJECTILE )  { drawProjectile(mobj, Color.black); }
		}
		for (i=0; i < size; i++) {
			mobj = (MapObject) mapobjectlist.elementAt(i);
			color = Color.black;
			if (i > 6)
			switch (i%3) {
				case 0:
					color = new Color((i+100)/(size+100)*255, 100, 50);
					break;
				case 1:
					color = new Color(50, (i+100)/(size+100)*255, 100);
					break;
				case 2:
					color = new Color(100, 100, (i+100)/(size+100)*255);
			}
			switch (i) {
				case 0:
					color = Color.black;
					break;
				case 1:
					color = Color.orange;
					break;
				case 2:
					color = Color.red;
					break;
				case 3:
					color = Color.blue;
					break;
				case 4:
					color = Color.green;
					break;
				case 5:
					color = Color.pink;
					break;
				case 6:
					color = Color.yellow;
					break;
                        }
                        if (mobj.ID < 1000 && mobj.ID >= 0) {
                                g.setColor(color);
                                g.drawString(mobj.name, 5, (i+1)*15);
                        }
                }
        }

        gbuffer.drawImage(imagebuffer, 0, 0, null);
	return;
}
/**
 * Method to handle events for the WindowListener interface.
 * @param e java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void windowActivated(java.awt.event.WindowEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the WindowListener interface.
 * @param e java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void windowClosed(java.awt.event.WindowEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the WindowListener interface.
 * @param e java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void windowClosing(java.awt.event.WindowEvent e) {
	// user code begin {1}
	// user code end
	if ((e.getSource() == getDialogBox()) ) {
		conn0(e);
	}
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the WindowListener interface.
 * @param e java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void windowDeactivated(java.awt.event.WindowEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the WindowListener interface.
 * @param e java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void windowDeiconified(java.awt.event.WindowEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the WindowListener interface.
 * @param e java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void windowIconified(java.awt.event.WindowEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the WindowListener interface.
 * @param e java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void windowOpened(java.awt.event.WindowEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
}