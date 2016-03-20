
package Server;


/* Server.java */

import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.util.*;
import java.applet.*;
import Bot.*;
import Visualizer.*;

/**
* JavaBot Client/Server stuff.
* @author ACM, et. al.
* @version 09/23/97
*
* TODO:
*   A. Implement Status & passivelist/activelist<br>
*   B. Implement collide() and commit()<br>
*   C. Implement "client methods" (fire, scan, etc.)<br>
*/
public class Server extends Applet implements Runnable, WindowListener, ActionListener, FilenameFilter, AdjustmentListener {
/**
* TODO:
*   A. Implement Status & passivelist/activelist<br>
*   B. Implement collide() and commit()<br>
*   C. Implement "client methods" (fire, scan, etc.)<br>
*/

  public boolean isrunning;
  /** Check for applet or application mode */
  public boolean isapplet;
  /** BotHandler for I/O with bots */
  public BotHandler handler;
  /** The scheduler's Thread. */
  public Thread sthread;
  boolean suspended=false;

  /** The GUI frame. */
  public Frame f;
  public MenuBar mbar;
  public Menu mfile;
  public Menu mextra;
  public Menu mconfig;
  public Menu mhelp;
  public TextArea text;
  public MapVisualizer mapvisualizer;
  public double mapScale = 0.5f;
  public Dialog dconfig;
  public Dialog dhelp;

  /** Config widgets */
  public Checkbox cbsmallArena;
  boolean smallArena=false;
  public Checkbox cbfastCollide;
  boolean fastCollide=false;
  public Checkbox cbaccurateCollide;
  boolean accurateCollide=false;
  public Checkbox cbhollowScan;
  boolean hollowScan=false;
  public Checkbox cbnoScan;
  boolean noScan=false;
  public Checkbox cbdrawDead;
  boolean drawDead=false;
  public Scrollbar sbscrollTime;
  int pausetime=5;
  public Checkbox cbdisableArena;
  boolean disableArena=false;
  public Label lscrollTime;

  /** List of bots. */
  public java.awt.List list;	    
  public int botindex;
  public int projectileindex;
  /** int value indexing the currently running bot in the botlist. */
  public int activebot;	

  /** List of mapobject Coordinates & IDs */
  public Vector mapobjectlist;
  /** List of bots */
  public Vector botlist;
  /** List of threads for each bot. (deprecated: Now contained within botlist.) */
  //  public Vector threadlist; 
  /** Contains status information (health, position, velocity, etc.) for each bot. */
  public Vector statuslist;
  /** Contains status info on all <I>passive</I> objects. (barriers, dead bots, etc.) */
  public Vector passivelist;
  /** Contains status info on all moving non-bot entities. */
  public Vector activelist;
  /** List of projectiles */
  public Vector projectilelist;

  /* ID system for objects. No checks between ID ranges so be careful */
  public static int BOT=0;
  public static int PROJECTILE=2000;

  /** global ID counter for bots */
  private int botIDCount=0;
  /** global GroupID counter for bot groups (not implemented) */
  private int botGIDCount=1000;
  /** global ID counter for other objects */
  public int objIDCount=2000;

/**
*  The Server constructor function, initializes important data objects
*/ 
public Server()
{
	/** isapplet defaults to true; main() will reset it to false */
	isapplet=true;
	isrunning=false;
	activebot=-1;
	handler = new BotHandler(this);
	mapvisualizer = new MapVisualizer();
	botindex = 0;
	projectileindex = 0;
        mapobjectlist = new Vector();
        botlist = new Vector();
        statuslist = new Vector();
//      threadlist = new Vector();
        passivelist = new Vector();
//        activelist = new Vector();
        projectilelist = new Vector();
}                                  

/**
*  main gets executed by java interpreter
* Contains GUI initialization code
*/
public static void main(String args[])
{
        Server server = new Server();
	server.isapplet=false;
	server.init();
//    server.start();
}              

public void run() {

        if (activebot == -1) activebot = 0;
        scheduler();
}                                                  

public void start () {
	return;
}

/**
*  Creates menu bar and other GUI components
*/
  public void gui_init()
  {
	f = new Frame("JavaBot Server");
//        f.setVisible(true);
//        f.setVisible(false);
        f.setSize(300,300);
        f.addWindowListener(this);

	mbar = new MenuBar();

	mfile = new Menu("File");

        MenuItem mquickstart = new MenuItem("Quick Start");
        mquickstart.addActionListener(this);

	MenuItem mexit = new MenuItem("Exit");
	mexit.addActionListener(this);

	MenuItem mload = new MenuItem("Load Bot");
	mload.addActionListener(this);

	MenuItem msimstart = new MenuItem("Start Sim");
	msimstart.addActionListener(this);

	MenuItem msimstop = new MenuItem("Stop Sim");
        msimstop.addActionListener(this);

	mextra = new Menu("Extras");

	MenuItem madd = new MenuItem("Add Bot");
	madd.addActionListener(this);

        MenuItem madd4 = new MenuItem("Add 4 Corners");
        madd4.addActionListener(this);

	MenuItem maddten = new MenuItem("Add 10 Bots");
	maddten.addActionListener(this);

	MenuItem maddobs = new MenuItem("Add Obstacles");
	maddobs.addActionListener(this);

	mconfig = new Menu("Options");

	MenuItem mopencfg = new MenuItem("Open Config Window");
	mopencfg.addActionListener(this);

	mhelp = new Menu("Help");

	MenuItem mgetHelp = new MenuItem("Help");
        mgetHelp.addActionListener(this);
	
        mfile.add(mquickstart);
	mfile.add(mload);
	mfile.add(msimstart);
	mfile.add(msimstop);
	mfile.add(mexit);

	mextra.add(madd);
	mextra.add(madd4);
	mextra.add(maddten);
	mextra.add(maddobs);

	mconfig.add(mopencfg);

	mhelp.add(mgetHelp);
	
	mbar.add(mfile);
	mbar.add(mextra);
	mbar.add(mconfig);
	mbar.add(mhelp);

	GridBagLayout gbl = new GridBagLayout();
	setLayout(gbl);
        GridBagConstraints c1 = new GridBagConstraints();

        c1.weightx = 0.2;
        c1.weighty = 1.0;
        c1.anchor = c1.WEST;
        c1.fill = c1.BOTH;

	list = new java.awt.List();
        gbl.setConstraints(list, c1);

        GridBagConstraints c2 = new GridBagConstraints();

        c2.weightx = 0.8;
	c2.weighty = 1.0;
        c2.anchor = c2.EAST;
        c2.fill = c2.BOTH;
        c2.gridwidth = c2.REMAINDER;

	text = new TextArea();
        gbl.setConstraints(text, c2);

	add(list);
	add(text);

	f.setMenuBar(mbar);
	f.add("Center", this);
	f.setVisible(true);
        f.repaint();

  }                                      

/**
 * This method was created by a SmartGuide.
 */
public void init ( ) {
	gui_init();
}

public void adjustmentValueChanged(AdjustmentEvent e) {
        lscrollTime.setText("Timing = " + Integer.toString(e.getValue()) + " millisec.");
}

public void windowActivated(WindowEvent e) {
}

public void windowClosed(WindowEvent e) {
        System.exit(0);
}

public void windowClosing(WindowEvent e) {
        f.dispose();
}

public void windowDeactivated(WindowEvent e) {
}

public void windowDeiconified(WindowEvent e) {
}

public void windowIconified(WindowEvent e) {
}

public void windowOpened(WindowEvent e) {
}

private void exit ( ) {
	if (isapplet) {
		mapvisualizer.quit();
		f.dispose();
		stop();
	}
	else System.exit(0);
}

/**
*  Start starts a new thread for the scheduler if no scheduler Thread exists
*/
private void startsim()
{
    if (!isrunning) {
	if (sthread == null)
	{
		sthread = new Thread(this);
		sthread.start();
	} else {
		sthread.resume();
	}
        isrunning = true;
    }
}            

private void quickstart()
{
//        loadBot("demos\\ShooterIII.class");
//        addBot();
        addShooter();
        add4();
        startsim();

}

/**
* Add bot creates a new instance of class bot and adds the bot the the botlist
* vector and creates a status for that bot and puts that into the status vector*/   
private void addBot ( ) {
        double x, y;
        Bot bot = new Bot("Random", handler);
	botlist.addElement(bot);

        Status status = new Status(bot);
	status.botID = botIDCount++;

        x = (double) java.lang.Math.random()*900 + 50;
	status.x0 = x;
        y = (double) java.lang.Math.random()*900 + 50;
	status.y0 = y;
        status.name = new String("Bot" + Integer.toString(botindex) + "-" + bot.name);

	statuslist.addElement(status);
        list.add(status.name);

	botindex++;

	return;
}

private void addShooter ( ) {
        double x, y;
	Hunter bot = new Hunter();
	bot.handler = handler;
	botlist.addElement(bot);

        Status status = new Status(bot);
	status.botID = botIDCount++;

        x = (double) java.lang.Math.random()*900 + 50;
	status.x0 = x;
        y = (double) java.lang.Math.random()*900 + 50;
	status.y0 = y;
	status.name = new String("Bot" + Integer.toString(botindex) + "-" + bot.name);

	statuslist.addElement(status);
	list.add(status.name);

	botindex++;

	return;
}

private void addBot4 (double x, double y, String string) {
        Bot bot = new Bot(string, handler);
	botlist.addElement(bot);

        Status status = new Status(bot);
	status.botID = botIDCount++;

	status.x0 = x;
	status.y0 = y;
	status.speed = 0.0f;
	status.name = new String("Bot" + Integer.toString(botindex) + "-" + bot.name);

	statuslist.addElement(status);
	list.add(status.name);

	botindex++;

	return;
}

private void add4() {
        addBot4(50, 50, "LowerLeft");
        addBot4(950, 50, "LowerRight");
        addBot4(950, 950, "UpperRight");
        addBot4(50, 950, "UpperLeft");
}

private void addObs() {
	int r;
	for (int i=0; i < 50; i++) {
                Obstacle obs = new Obstacle();
                obs.x0 = (double) java.lang.Math.random()*900 + 50;
                obs.y0 = (double) java.lang.Math.random()*900 + 50;
		obs.x1 = obs.x0;
		obs.y1 = obs.y0;
		obs.radius = 10.0f;
		obs.isLive = true;
		r = i%5;
                if (r == 0) {
			obs.id = obs.WALL;
			obs.damage = 3.0f;
                } else if (r == 1) {
			obs.id = obs.HEALTH;
			obs.powervalue = 10.0f;
		} else if (r == 2) {
			obs.id = obs.BATTERY;
			obs.powervalue = 100.0f;
		} else if (r == 3) {
			obs.id = obs.AMMO;
			obs.powervalue = 50.0f;
		} else if (r == 4) {
			obs.id = obs.MINE;
			obs.damage = 10.0f;
		}
		passivelist.addElement(obs);
	}
}


/**
* boolean FilenameFilter.accept(File dir, String name)
*  Implementation of FilenameFilter interface.  This is basically a callback method which is called for
*  each file in a directory to test if it should be included (return true) or filtered out (return false).
*  3/16/2016 
*/
public boolean accept(File dir, String name)
{
	if (name.matches(".+class"))
		return true;
	else
		return false;
}

/** This is the old FilenameFilter.accept() implementation.  I don't know what I was trying to do trying
*   to do by modifying the name argument rather than testing to see if it contained the right extension.
*   The documentation makes perfect sense in hindsight.
*
public boolean accept(File dir, String name) {
//        text.append(name + "-> " + name.endsWith(".class") + "\n");
	return false;
}
**/


/**
* Add bot creates a new instance of class bot and adds the bot the the botlist
* vector and creates a status for that bot and puts that into the status vector*/
private void loadBot (String str) {
        double x, y;
        Object bot = null;
        String name = null;
	loaderClass loader = new loaderClass();

     if (str == null) {
	FileDialog fd = new FileDialog(f, "Load A Bot", FileDialog.LOAD);
	/* this is a hack to filter class files -- the FilenameFilter is not working yet */ 
	/** Actually, it is working now (19 years later, without needing any hack. See callback method above. 3/16/16 */
//	fd.setFile("*.class");
        fd.setFilenameFilter(this);
	fd.show();
	str = fd.getDirectory() + fd.getFile();
	if ( str.equals("nullnull") ) {
		return;
	}
      }

//        System.out.println("FileDialog says: " + str);
  try {
        Class importBot = loader.loadClass(str);
	
//      text.append(importBot.getName() + "\n");
//        System.out.println(importBot.toString() + " loaded.\n");
	try {
		try {
                        bot = importBot.newInstance();
                        Bot Bot = (Bot) bot;
                        Bot.handler = handler;
                        name = Bot.name;
                        botlist.addElement(bot);
		} catch (java.lang.InstantiationException e) {
			text.append("Instantiation error.\n");
		} 
        } catch (java.lang.IllegalAccessException e) {
                text.append("Illegal Access\n");
	}	
        Status status = new Status((Bot) bot);
        status.botID = botIDCount++;

        x = (double) java.lang.Math.random()*900+50;
		status.x0 = x;
        status.x1 = x;
        y = (double) java.lang.Math.random()*900+50;
		status.y0 = y;
        status.y1 = y;
        status.name = "Bot" + Integer.toString(botindex) + "-" + name;
		statuslist.addElement(status);
        list.add(status.name);

	botindex++;
  } catch (ClassNotFoundException ce) {
        return;
  }
  return;
}


private void stopsim()
{
   if (isrunning) {
	isrunning = false;
	sthread.suspend();

        Bot bot;
        Status status = (Status) statuslist.elementAt(activebot-1);
	bot = status.bot;
	status.thread.suspend();
   }
}                  

/**
* Function that gets run when anything is done with Menu Bar
*/
public void actionPerformed(ActionEvent event)
{
	String cmd = event.getActionCommand();
	if (cmd == "Exit") {
		exit();
	} else if (cmd == "Load Bot") {
                loadBot(null);
	} else if (cmd == "Add Bot") {
		addBot();
	} else if (cmd == "Add 10 Bots") {
		for (int x = 0;x<10;x++) { addBot(); }
	} else if (cmd == "Start Sim") {
		startsim();
	} else if (cmd == "Stop Sim") {
		stopsim();
        } else if (cmd == "Quick Start") {
                quickstart();
        } else if (cmd == "Add 4 Corners") {
                add4();
	} else if (cmd == "Add Obstacles") {
		addObs();
	} else if (cmd == "Open Config Window") {
		config();
	} else if (cmd == "Help") {
		help();
	} else if (cmd == "Accept Changes") {
                dlgAccept();
                mapvisualizer.toFront();
	} else if (cmd == "Cancel") {
//                dconfig.dispose();
                dconfig.setVisible(false);
                mapvisualizer.toFront();
	} else if (cmd == "Close Help") {
//                dhelp.dispose();
                dhelp.setVisible(false);
                mapvisualizer.toFront();
	} else {
                text.append("\nUnkown Message Command:" + cmd + "\n");
	}
}                          

private void dlgAccept() {
        if (!smallArena && cbsmallArena.getState() ) {
                mapvisualizer.setScreenSize(400);
                mapScale = (double) 400/1000;
        } else if (smallArena && !cbsmallArena.getState() ) {
                mapvisualizer.setScreenSize(500);
                mapScale = (double) 500/1000;
        }

        disableArena = cbdisableArena.getState();
        if (disableArena) mapvisualizer.hide();
        else mapvisualizer.show();
        smallArena = cbsmallArena.getState();
        accurateCollide = cbaccurateCollide.getState();
	fastCollide = cbfastCollide.getState();
        hollowScan = cbhollowScan.getState();
        noScan = cbnoScan.getState();
        drawDead = cbdrawDead.getState();
        mapvisualizer.setDrawDead(drawDead);
        mapvisualizer.setHollowScan(hollowScan);
        mapvisualizer.setNoScan(noScan);
        pausetime = sbscrollTime.getValue();

//        dconfig.dispose();
        dconfig.setVisible(false);
}

private void config() {
        boolean restart=false;

    if (dconfig==null) {
        dconfig = new Dialog(f, "Config Window", true);
        dconfig.setSize(250, 200);
        Panel p2 = new Panel();
        Panel p2a = new Panel(new GridLayout(0,2));
        Panel p2b = new Panel(new GridLayout(0,1));
	Panel p = new Panel();

        cbsmallArena = new Checkbox("Small Arena", smallArena);
	cbfastCollide = new Checkbox("Fast Collisions", fastCollide);
        cbaccurateCollide = new Checkbox("Accurate Collisions", accurateCollide);
        cbhollowScan = new Checkbox("Hollow Scans", hollowScan);
        cbnoScan = new Checkbox("No Scans", noScan);
        cbdrawDead = new Checkbox("Draw Dead Bots", drawDead);
        cbdisableArena = new Checkbox("Disable Arena", disableArena);
        sbscrollTime = new Scrollbar(Scrollbar.HORIZONTAL, pausetime, 150, 1, 1500);
        sbscrollTime.addAdjustmentListener(this);
        lscrollTime = new Label("Delay Timing = " + Integer.toString(pausetime) + " millisec.");

	Button bOK = new Button("Accept Changes");
	bOK.addActionListener(this);
	Button bcancel = new Button("Cancel");
	bcancel.addActionListener(this);

        p2a.add(cbsmallArena);
        p2a.add(cbfastCollide);
//        p2a.add(cbaccurateCollide);
        p2a.add(cbhollowScan);
        p2a.add(cbnoScan);
        p2a.add(cbdisableArena);
        p2a.add(cbdrawDead);
        p2b.add(sbscrollTime);
        p2b.add(lscrollTime);

	GridBagLayout gbl = new GridBagLayout();
        p2.setLayout(gbl);
        GridBagConstraints c1 = new GridBagConstraints();
        c1.weightx = 1.0;
        c1.weighty = 1.0;
        c1.anchor = c1.NORTH;
        c1.gridwidth = c1.REMAINDER;
        c1.fill = c1.BOTH;
        gbl.setConstraints(p2a, c1);
        GridBagConstraints c2 = new GridBagConstraints();
        c2.weightx = 1.0;
        c2.weighty = 0.2;
        c2.anchor = c2.SOUTH;
        c2.fill = c2.HORIZONTAL;
        c2.gridwidth = c2.REMAINDER;
        gbl.setConstraints(p2b, c2);
        p2.add(p2a);
        p2.add(p2b);

	p.add(bOK);
	p.add(bcancel);
        dconfig.add(p2, "North");
        dconfig.add(p, "South");
    }

        if (isrunning) {
                stopsim();
                restart = true;
        }
        dconfig.show();

        if (restart) startsim();
}

private void help() {
   if (dhelp==null) {
        dhelp = new Dialog(f, "Help for JavaBots", false);
        dhelp.setSize(400, 400);
	Panel p = new Panel();
	TextArea ta = new TextArea();
	Button bClose = new Button("Close Help");
	ta.append(
	  "File:\n"
		+ "\tQuick Start: load a compiled class bot along with 4 passive bots.\n"
		+ "\tLoad Bot: load compiled bot class from disk.\n"
		+ "\tStart Sim: starts/resumes the simulator.\n"
		+ "\tStop Sim: stops/pauses the simulator.\n"
		+ "\tExit: exits the simulator.\n"

	+ "Extras:\n"
		+ "\tAdd Bot: adds a passive bot.\n"
		+ "\tAdd 4 Corners: adds 4 passive bots to each corner of the arena.\n"
		+ "\tAdd 10 bots: adds 10 passive bots to random positions.\n"
		+ "\tAdd Obstacles: add walls, mines, and health, battery, and ammo powerups.\n"
	+ "Options: Open Config Window\n"
		+ "\tSmall Arena: switch to a smaller arena size (for 640x480 screens).\n"
		+ "\tFast Collisions: switch to a (possibly) faster, but less precise\n"
			+ "\t\tcollision detection algorithm.\n"
		+ "\tHollow Scans: turn off the (distracting/slow) scan() display and use a\n"
			+ "\t\tline instead.\n"
		+ "\tNo Scans: turn off scan() display completely.\n"
		+ "\tDisable Arena: Don't display the sim -- continues to output text.\n"
		+ "\tDraw Dead Bots: Draws a triangle where dead bots should be.\n"
		+ "\tDelay Timing/Scrollbar: sets the amount of time to pause between turns.\n"
	+ "\n"
	+ "Health is displayed as a solid bar above a bot's image.\n"
	+ "Cloaking is portrayed as a hollow triangle (vs. the normal filled triangle).\n"
	+ "Explosions are seen as red 'stars'.\n"
	+ "\n"
	+ "Powerups/Obstacles:\n"
		+ "\tWhite: Health\tRed: Mine\n"
		+ "\tGreen: Battery\tOrange: Ammo\n"
		+ "\tBlack: Wall\n"
		);

	bClose.addActionListener(this);

	p.add(bClose);
	dhelp.add(ta, "Center");
        dhelp.add(p, "South");
   }
        dhelp.show();
}

public boolean lineintersection (double ax0, double ay0, double ax1, double ay1,
        double bx0, double by0, double bx1, double by1) {
	
        double am, bm; /* slopes */
        double ac, bc; /* intercepts */
        double ix, iy; /* intersection point */

	/* check for dx & dy */
	if ( (ax0-ax1) == 0) {
/********** MAJOR PROBLEM!!!!!!!!! ***********/
//                text.append("Error: infinite slope\n");
		return false;
	}
	if ( (ay0-ay1) == 0) {
//              text.append("Error: zero slope\n");
		return false;
	}
	
	/* slopes */	
	am = (ay1-ay0)/(ax1-ax0);
	bm = (by1-by0)/(bx1-bx0);
	
	/* intercepts */
	ac = ay0-am*ax0;
	bc = by0-bm*bx0;
	
	/* intersection */
	ix = (bc-ac)/(am-bm);
	iy = am*ix+ac;
	
	/*	check bounding conditions */
	/* y */
	if ( (ix < ax1 && ix > ax0) || (ix < ax0 && ix > ax1) &&
		(iy < ay1 && iy > ay0) || (iy < ay0 && iy > ay1) ) {
		if ( (ix < bx1 && ix > bx0) || (ix < bx0 && ix > bx1) &&
		(iy < by1 && iy > by0) || (iy < by0 && iy > by1) ) {
				return true;
		}
	}			
	
	return false;
}

public double linedist(double x0, double y0, double x1, double y1) {
        double c_squared;
        double dist;

        c_squared = (x1-x0)*(x1-x0) + (y1-y0)*(y1-y0);
        dist = (double) java.lang.Math.sqrt(c_squared);

        return dist;
}

public double point_linedist(double theta, double x0, double y0, double a, double b) {
        double r;
        double phi = 0;
        double phi_prime;
        double dist;
        double x = a-x0;
        double y = b-y0;

        if (x == 0) {
                if (y > 0) phi = (double) java.lang.Math.PI/2;
                if (y < 0) phi = (double) java.lang.Math.PI*3/2;
        } else if (y == 0) {
                if (x > 0) phi = (double) 0.0f;
                else if (x < 0) phi = (double) java.lang.Math.PI;
        } else {
                phi = (double) java.lang.Math.atan(y/x);
                if (x < 0) phi += (double) java.lang.Math.PI;
                else if (x > 0 && y < 0) phi += (double) java.lang.Math.PI*2;
        }

        phi_prime = phi - theta;
	r = linedist(x0, y0, a, b);

        /* yes, the following is a lame hack, but I don't won't to add
           extra overhead for such trivial scenarios as points lying opposite
           the vector */
        if (java.lang.Math.abs(phi_prime) > (double) java.lang.Math.PI/2) {
/*
                System.out.println("\nSupposedly, phi = " + double.toString(phi*180.0f / (double) java.lang.Math.PI)
                        + " and theta = " + double.toString(theta*180.0f / (double) java.lang.Math.PI)
                        + "\nand phi (" + double.toString(phi*180.0f / (double) java.lang.Math.PI) + ") >< "
                        + "theta (" + double.toString(theta*180.0f / (double) java.lang.Math.PI) + ") +- 90.0");
*/
                return 9999;
        }

        dist = r * (double) java.lang.Math.sin(phi_prime);
/*
        if (java.lang.Math.abs(dist) < 5)
                System.out.println("\nphi-prime = " + double.toString(phi_prime*180/(double) java.lang.Math.PI)
                  + " ; x = " + double.toString(x) + " ; y = " + double.toString(y) + " ; r = " + double.toString(r) );
*/
        return dist;
}

public Vector scan(double dir, int botid) {
        int i;
        double x0, y0;
        double offset_angle;
        //scanarc in radians
        double scanarc = (double) java.lang.Math.PI/4;
	//starting sweep angle
        double base_dir=dir - (scanarc/2);
        double scandist=2000;
        double offset;

        Status stat, tempstat;
        Projectile ptemp;
        Obstacle obs;

        Vector scanlist = new Vector();
        stat = (Status) statuslist.elementAt(botid);
        stat.scan_dir = base_dir;
        stat.scan_arc = scanarc;
        stat.scan1_vx = (double) java.lang.Math.cos(base_dir);
        stat.scan1_vy = (double) java.lang.Math.sin(base_dir);
        stat.scan2_vx = (double) java.lang.Math.cos(base_dir + scanarc);
        stat.scan2_vy = (double) java.lang.Math.sin(base_dir + scanarc);

        Info info = new Info(stat.botID, stat.x0, stat.y0, stat.health);
	// personal info on bot
        info.speed = stat.speed;
        info.dir = stat.dir;
        info.times_collided = stat.times_collided;
        info.times_hit = stat.times_hit;
        info.collidelist = stat.collidelist;
        info.hitlist = stat.hitlist;
        info.battery = stat.battery;
        info.gunheat = stat.gunheat;
	info.iscloaked = stat.iscloaked;
	info.cloaktimer = stat.cloaktimer;
        scanlist.addElement(info);

	x0 = stat.x0; y0 = stat.y0;

        Polygon poly = new Polygon();
        poly.addPoint( (int) stat.x0, (int) stat.y0);
        poly.addPoint( (int) (stat.x0 + (scandist*stat.scan1_vx)), (int) (stat.y0 + (scandist*stat.scan1_vy)));
        poly.addPoint( (int) (stat.x0 + (scandist*stat.scan2_vx)), (int) (stat.y0 + (scandist*stat.scan2_vy)));

        // begin scanning all bots
        for (i=0; i < statuslist.size(); i++) {
                if (i == botid) continue;

                tempstat = (Status) statuslist.elementAt(i);
                if (tempstat.health <= 0) continue;
		if (tempstat.iscloaked) continue;

                if (poly.contains( (int) tempstat.x0, (int) tempstat.y0)) {
                        /* TODO: throw or keep */
			/* The following are addition crap we may want to throw out *
			 * if it makes the game too easy. */
                        info = new Info(tempstat.botID, tempstat.x0, tempstat.y0, tempstat.health);
                        info.speed = tempstat.speed;
			info.dir = tempstat.dir;
                        scanlist.addElement(info);
                }
        }

        for (i=0; i < projectilelist.size(); i++) {
                ptemp = (Projectile) projectilelist.elementAt(i);
                if (!ptemp.isLive) continue;
                if (poly.contains( (int) ptemp.x0, (int) ptemp.y0)) {
                        info = new Info(-1, ptemp.x0, ptemp.y0, 1);
                        info.dir = ptemp.dir;
                        scanlist.addElement(info);
                }
        }

        for (i=0; i < passivelist.size(); i++) {
                obs = (Obstacle) passivelist.elementAt(i);
                if (poly.contains( (int) obs.x0, (int) obs.y0)) {
                        info = new Info(obs.id, obs.x0, obs.y0, 1);
			scanlist.addElement(info);
                }
        }

	return scanlist;
}

public boolean boundingCollision(double x0, double y0, double x1, double y1,
        double x2, double y2, double x3, double y3, double radius) {

        double ax, ay, aX, aY;
        double bx, by, bX, bY;

        if (x0 <= x1) { ax = x0; aX = x1; } else { ax = x1; aX = x0;}
        if (y0 <= y1) { ay = y0; aY = y1; } else { ay = y1; aY = y0;}
        if (x2 <= x3) { bx = x2; bX = x3; } else { bx = x3; bX = x2;}
        if (y2 <= y3) { by = y2; bY = y3; } else { by = y3; bY = y2;}

        if ( (((bx+radius>=ax-10)&&(bx-radius<=aX+10)) || ((bX+radius>=ax-10)&&(bX-radius<=aX+10)))
                && (((ay+10>=by-radius)&&(ay-10<=bY+radius)) || ((aY+10>=by-radius)&&(aY-10<=bY+radius))) )
/*
		if (accurateCollide) {
			return gridCollision(x0, y0, x1, y1, x2, y2, x3, y3, radius);
                } else { return true;}
*/
		return true;

        if ( (((ax+10>=bx-radius)&&(ax-10<=bX+radius)) || ((aX+10>=bx-radius)&&(aX-10<=bX+radius)))
                && (((by+radius>=ay-10)&&(by-radius<=aY+10)) || ((bY+radius>=ay-10)&&(bY-radius<=aY+10))) )
/*
		if (accurateCollide) {
                        return gridCollision(x0, y0, x1, y1, x2, y2, x3, y3, radius);
                } else { return true;}
*/
		return true;

        return false;
}                

public boolean sphereCollision(double x0, double y0, double x1, double y1,
                                double x2, double y2, double x3, double y3, double radius) {
        int precision = 2;
        double dist, dist2;
        double x=x0, y=y0;
        double a=x2, b=y2;
	int divs, divs2; /* divisions */
        int di, dj;
        double dx, dy, da, db;

	dx = x1-x0;
	dy = y1-y0;
	da = x3-x2;
	db = y3-y2;

	dist = linedist(x0, y0, x1, y1);
	dist2 = linedist(x2, y2, x3, y3);
	divs = (int) (dist/(10/precision));
	if (radius != 0) divs2 = (int) (dist2/(radius/precision));
        else divs2 = 40;

	/* "X" < 1  -> "X" == 0 , "X" >= 1 -> X != 0 */
	if (divs < 1 && divs2 < 1) {
		dist = linedist(x1, y1, x3, y3);
		if (dist < (10+radius)) return true;
	} else if (divs < 1 && divs2 >= 1) {
		for (int j=0; java.lang.Math.abs(j) <= java.lang.Math.abs(divs2); j++) {
                        a = x2 + da*(double)((double)j/(double)divs2);
                        b = y2 + db*(double)((double)j/(double)divs2);
			dist = linedist(x1, y1, a, b);
			if (dist < (10+radius)) return true;
		}
	} else if (divs >= 1 && divs2 < 1) {
		for (int i=0; java.lang.Math.abs(i) <= java.lang.Math.abs(divs); i++) {
                        x = x0 + dx*(double)((double)i/(double)divs);
                        y = y0 + dy*(double)((double)i/(double)divs);
			dist = linedist(x, y, x3, y3);
			if (dist < (10+radius)) return true;
		}
	} else {
		for (int i=0; java.lang.Math.abs(i) <= java.lang.Math.abs(divs); i++) {
                        x = x0 + dx*(double)((double)i/(double)divs);
                        y = y0 + dy*(double)((double)i/(double)divs);
			for (int j=0; java.lang.Math.abs(j) <= java.lang.Math.abs(divs2); j++) {
                                a = x2 + da*(double)((double)j/(double)divs2);
                                b = y2 + db*(double)((double)j/(double)divs2);
				dist = linedist(x, y, a, b);
				if (dist < (10+radius)) return true;
			}
		}
	}

	return false;
}

private boolean gridCollision(double x0, double y0, double x1, double y1,
                                double x2, double y2, double x3, double y3, double radius) {
	int w=0, h=0;
	/* TODO: ratio = 1000 */
        double ratio=4.0f;

//	BitSet bitmap1, bitmap2;
	int bitmap1[], bitmap2[];

        double minx, miny, maxx, maxy;
        double tx, ty;

	int x, y;
	int dx, dy;
	int di;

	int i;

        double ax, ay, aX, aY;
        double bx, by, bX, bY;

        if (x0 <= x1) { ax = x0; aX = x1; } else { ax = x1; aX = x0;}
        if (y0 <= y1) { ay = y0; aY = y1; } else { ay = y1; aY = y0;}
        if (x2 <= x3) { bx = x2; bX = x3; } else { bx = x3; bX = x2;}
        if (y2 <= y3) { by = y2; bY = y3; } else { by = y3; bY = y2;}

	/* since projectile maybe _inside_ bot, +/- 10 on both sides */
        if (ax <= bx) minx = ax-12; else minx = bx-12;
        if (aX >= bX) maxx = aX+12; else maxx = bX+12;
        if (ay <= by) miny = ay-12; else miny = by-12;
        if (aY >= bY) maxy = aY+12; else maxy = bY+12;

        w = (int) (ratio * (maxx - minx));
	h = (int) (ratio * (maxy - miny));
	/* translation */
	tx = minx;
	ty = miny;

//	bitmap1 = new BitSet(w*h);
//	bitmap2 = new BitSet(w*h);
        bitmap1 = new int[(int) (w*h/32)];
        bitmap2 = new int[(int) (w*h/32)];

	int temp;

	int j, k, offset, bitshift;
	
	/* start at x0 and trace to x1 */
	dx = (int) (ratio * (x1-x0));
	dy = (int) (ratio * (y1-y0));
        if (dx > 0) di = 1; else di = -1;
        /* skip the i == dx condition */
        for (i = 0; java.lang.Math.abs(i) < java.lang.Math.abs(dx); i += di) {
                x = (int) (ratio*(x0-tx)) + dx*(i/(dx));
                y = (int) (ratio*(y0-ty)) + dy*(i/(dx));
                /* draw in a 20x20 square */
		for (j=-10*(int)ratio; j < 10*(int)ratio; j++) {
                        for (k=-10*(int)ratio; k <= 10*(int)ratio; k++) {
//				bitmap1.set( ((y+k)*(w-1))+x+j );
                                offset = ((y+k)*(w-1))+(x+j);
				bitshift = offset % 32;
                                offset = offset / 32;
				temp = 1;
				temp = temp<<bitshift;
                                if (offset == 0 || offset < 7) bitmap1[offset] = bitmap1[offset]|temp;
                                else if (bitshift >= 7) bitmap1[offset-1] = bitmap1[offset-1]|temp;
			}
		}
	}
        /* do i == dx here */
        x = (int) (ratio * (x1-tx));
        y = (int) (ratio * (y1-ty));
//      bitmap1.set( (y*(w-1))+x );
	for (j=-10*(int)ratio; j < 10*(int)ratio; j++) {
		for (k=-10*(int)ratio; k <= 10*(int)ratio; k++) {
//			bitmap1.set( ((y+k)*(w-1))+x+j );
                        offset = ((y+k)*(w-1))+(x+j);
			bitshift = offset % 32;
                        offset = offset / 32;
			temp = 1;
			temp = temp<<bitshift;
                        if (offset == 0 || offset < 7) bitmap1[offset] = bitmap1[offset]|temp;
                        else if (bitshift >= 7) bitmap1[offset-1] = bitmap1[offset-1]|temp;
		}
	}

	/* do the same for x2 to x3 */
	dx = (int) (ratio * (x3-x2));
	dy = (int) (ratio * (y3-y2));
        if (dx >= 0) di = 1; else di = -1;
        /* skip the i == dx condition */
        for (i = 0; java.lang.Math.abs(i) < java.lang.Math.abs(dx); i += di) {
                x = (int) (ratio*(x2-tx)) + dx*(i/(dx));
                y = (int) (ratio*(y2-ty)) + dy*(i/(dx));
		/* draw in a 10x10 square */
                for (j=(int)-radius*(int)ratio; j <= (int)radius*(int)ratio; j++) {
                        for (k=(int)-radius*(int)ratio; k < (int)radius*(int)ratio; k++) {
//				bitmap2.set( ((y+k)*(w-1))+x+j );
                                offset = ((y+k)*(w-1))+(x+j);
				bitshift = offset % 32;
                                offset = offset / 32;
				temp = 1;
				temp = temp<<bitshift;
                                if (offset == 0 || offset < 7) bitmap2[offset] = bitmap2[offset]|temp;
                                else if (bitshift >= 7) bitmap2[offset-1] = bitmap2[offset-1]|temp;
			}
		}
	}
        /* do i == dx here */
        x = (int) (ratio * (x3-tx));
        y = (int) (ratio * (y3-ty));
//	bitmap2.set( (y*(w-1))+x );
	for (j=-10*(int)ratio; j < 10*(int)ratio; j++) {
		for (k=-10*(int)ratio; k <= 10*(int)ratio; k++) {
//			bitmap2.set( ((y+k)*(w-1))+x+j );
                        offset = ((y+k)*(w-1))+(x+j);
			bitshift = offset % 32;
                        offset = offset / 32;
			temp = 1;
			temp = temp<<bitshift;
                        if (offset == 0 || offset < 7) bitmap2[offset] = bitmap2[offset]|temp;
                        else if (bitshift >= 7) bitmap2[offset-1] = bitmap2[offset-1]|temp;
		}
	}

        for (i = 0; i < w*h/32; i++) {
//		if (bitmap1.get(i)) return true;
		if ((bitmap1[i] & bitmap2[i])!=0) return true;
	}

	return false;
}

/** This is the main collision detection system for objects */
private void collide() {
        int i, j, k;
        Status stat, tempstat;
        Projectile ptemp;
        Obstacle obs;
        Info info;
        boolean collide=false, hit=false;

        /* stop projectiles hitting Obstacles and blowup powerups */
        for (i=0; i < passivelist.size(); i++) {
		hit = false;
                obs = (Obstacle) passivelist.elementAt(i);
		for (k=0; k < projectilelist.size(); k++) {
                        ptemp = (Projectile) projectilelist.elementAt(k);
			if (!ptemp.isLive) continue;
                        if (fastCollide) {
                            if ( boundingCollision(obs.x0, obs.y0, obs.x1, obs.y1,
                                        ptemp.x0, ptemp.y0, ptemp.x1, ptemp.y1, 0) )
                                        hit = true;
                        } else {
                                if ( sphereCollision(obs.x0, obs.y0, obs.x1, obs.y1,
                                        ptemp.x0, ptemp.y0, ptemp.x1, ptemp.y1, 0) )
                                        hit = true;
                        }
			if (hit) {
				ptemp.isLive = false;
				ptemp.times_hit++;
				info = new Info(obs.id, obs.x0, obs.y0, -1);
				ptemp.hitlist.addElement(info);
				if (obs.id < obs.WALL) obs.isLive = false;
			}
                }
        }

	// go through all bots
	for (i=0; i < statuslist.size(); i++) {
                stat = (Status) statuslist.elementAt(i);
		// check current bot with all other bots
		if (stat.health <= 0) {
			continue;
                }
		for (j=0; j < statuslist.size(); j++) {
			collide = false;

                        if (j==i) continue;
                        tempstat = (Status) statuslist.elementAt(j);
			if (tempstat.health <= 0) {
				continue;
			}
			
                        if (fastCollide) {
                                if ( boundingCollision(stat.x0, stat.y0, stat.x1, stat.y1,
					tempstat.x0, tempstat.y0, tempstat.x1, tempstat.y1, 10) )
                                        collide=true;
                        } else {
                                if ( sphereCollision(stat.x0, stat.y0, stat.x1, stat.y1,
					tempstat.x0, tempstat.y0, tempstat.x1, tempstat.y1, 10) )
                                        collide=true;
                        }
                        if (collide) {
//                                text.append(stat.name + " collides with " + tempstat.name + "\n");

                                /* decrement health */
                                if ( (stat.health2 -= stat.collisiondamage) <= 0 && stat.alive) {
                                        text.append(stat.name + " destroyed.\n");
                                        stat.alive = false;
                                }
                                stat.times_collided++;
                                info = new Info(tempstat.botID, tempstat.x0, tempstat.y0, tempstat.health);
                                info.dir = tempstat.dir;
                                info.speed = tempstat.speed;
                                stat.collidelist.addElement(info);
				/* reset the momenta */
				stat.momentum_x = 0;
				stat.momentum_y = 0;
				stat.x1 = stat.x0;
				stat.y1 = stat.y0;

				/* old stuff to maintain compatibility*/
                                stat.speed = 0;
                                stat.speed2 = 0;

                                /* bounce bots */
                                /* double temp = stat.dir;
                                 * stat.dir = tempstat.dir;
                                 * tempstat.dir = temp;
                                 */
                        }
                } /* end chech with bots */

		// check current bot with projectiles
		for (k=0; k < projectilelist.size(); k++) {
			hit = false;

                        if (stat.health <= 0) {
                                break;
                        }
                        ptemp = (Projectile) projectilelist.elementAt(k);
                        /* bots cannot hit themselves */
                        if (ptemp.ownerbotid == i) continue;

			/* keep this off so a dying projectile can take out
			 * more than one bot */
//			if (!ptemp.isLive) continue;

                    if (fastCollide) {
                        if ( boundingCollision(stat.x0, stat.y0, stat.x1, stat.y1, ptemp.x0, ptemp.y0, ptemp.x1, ptemp.y1, 0) )
                                hit = true;
                    } else {
                        if ( sphereCollision(stat.x0, stat.y0, stat.x1, stat.y1, ptemp.x0, ptemp.y0, ptemp.x1, ptemp.y1, 0) )
                                hit = true;
                    }
                    if (hit) {
//				text.append(ptemp.ownerBotName + " hits " + stat.name + "\n");
				/* decrement health */
                                if ( (stat.health2 -= ptemp.damage) <= 0 && stat.alive) {
					text.append(stat.name + " destroyed.\n");
                                        stat.alive = false;
					/* reset the momenta */
					stat.momentum_x = 0;
					stat.momentum_y = 0;
				        stat.x1 = stat.x0;
			                stat.y1 = stat.y0;

					/* old stuff to maintain compatibility*/
	                                stat.speed = 0;
                                        stat.speed2 = 0;
		                }
                                /* update projectile stats: projectile is
                                 * given info before update so it gets
                                 * unadjusted health */
                                ptemp.times_hit++;
                                info = new Info(stat.botID, stat.x0, stat.y0, stat.health);
                                ptemp.hitlist.addElement(info);

                                /* update bot stats with the attacking bot */
                                stat.times_hit++;
                                tempstat = (Status) statuslist.elementAt(ptemp.ownerbotid);
                                info = new Info(tempstat.botID, ptemp.owner_x, ptemp.owner_y, -1);
                                info.dir = -1;
                                info.speed = -1;
                                stat.hitlist.addElement(info);

			        // (later) remove projectile
//				ptemp.speed = 0;
				ptemp.isLive = false;
                    }
                } /* done check with projectilelist */

		// check current bot with obstacles
		for (k=0; k < passivelist.size(); k++) {
			collide = false;

                        if (stat.health <= 0) {
                                break;
                        }
                        obs = (Obstacle) passivelist.elementAt(k);
//			if (!obs.isLive) continue;

			if (fastCollide) {
				if ( boundingCollision(stat.x0, stat.y0, stat.x1, stat.y1,
					obs.x0, obs.y0, obs.x1, obs.y1, obs.radius) )
					collide = true;
	                    } else {
		                if ( sphereCollision(stat.x0, stat.y0, stat.x1, stat.y1,
					obs.x0, obs.y0, obs.x1, obs.y1, obs.radius) )
			                collide = true;
			}
			if (collide) {
//                                text.append(stat.name + " collides with " + obs.id + ".\n");
				switch (obs.id) {
				case Obstacle.WALL:
                                        /* decrement health */
                                        if ( (stat.health2 -= obs.damage) <= 0 && stat.alive) {
                                                text.append(stat.name + " destroyed.\n");
                                                stat.alive = false;
                                        }
					/* reset the momenta */
					stat.momentum_x = 0;
					stat.momentum_y = 0;
				        stat.x1 = stat.x0;
			                stat.y1 = stat.y0;

					/* old stuff to maintain compatibility*/
					stat.speed = 0;
					stat.speed2 = 0;
					break;
				case Obstacle.HEALTH:
					if (stat.health2 < 100) {
						obs.isLive = false;
						if ((stat.health2 += obs.powervalue) > 100)
							stat.health2 = 100;
					}
					break;
				case Obstacle.BATTERY:
					if (stat.battery < stat.maxbattery) {
						obs.isLive = false;
						if ((stat.battery += obs.powervalue) > stat.maxbattery)
							stat.battery = stat.maxbattery;
					}
					break;
				case Obstacle.AMMO:
					obs.isLive = false;
					stat.max_proj += obs.powervalue;
					break;
				case Obstacle.MINE:
                                        obs.isLive = false;
					/* decrement health */
                                        if ( (stat.health2 -= obs.damage) <= 0 && stat.alive) {
                                                text.append(stat.name + " destroyed.\n");
                                                stat.alive = false;
                                        }
					break;
				}
				stat.times_collided++;
				info = new Info(obs.id, obs.x0, obs.y0, -1);
				stat.collidelist.addElement(info);
			}
		} /* end of obstacle collide */

                if ( (stat.x1 >= 1000) || (stat.x1 <= 0) || (stat.y1 >= 1000) || (stat.y1 <= 0) ) {
			/* reset the momenta */
			stat.momentum_x = 0;
			stat.momentum_y = 0;
                        stat.x1 = stat.x0;
                        stat.y1 = stat.y0;
			/** do a sanity check to make sure that x1,y1 are within range */
			if (stat.x1 > 999) stat.x1 = 999;
			if (stat.x1 < 1) stat.x1 = 1;
			if (stat.y1 > 999) stat.y1 = 999;
			if (stat.y1 < 1) stat.y1 = 1;

			/** Treat out of arena as a collision with wall */
                        stat.times_collided++;
                        info = new Info(stat.botID, stat.x1, stat.y1, stat.health);
                        info.dir = stat.dir;
                        info.speed = stat.speed;
                        stat.collidelist.addElement(info);

			/* old stuff to maintain compatibility*/
                        stat.speed = 0.0f;
                        stat.speed2 = 0.0f;
                }
        } /* move to next bot */
}

private void commit()
{
	  	int i;
                Status stat;
                Projectile tempproj;
	  	
	  	for (i=0; i < statuslist.size(); i++) {
                        stat = (Status) statuslist.elementAt(i);
			if (stat.health <= 0) {
				continue;
			}
                        /* set previous coords */
                        stat.px = stat.x0;
                        stat.py = stat.y0;
			stat.px = stat.x0;
  			stat.x0 = stat.x1;
			stat.py = stat.y0;
  			stat.y0 = stat.y1;
		        stat.health = stat.health2;
	  	}	

	  	for (i=0; i < projectilelist.size(); i++) {
                        tempproj = (Projectile) projectilelist.elementAt(i);
			if (!tempproj.isLive) continue;
                        /* added yet another crude hack to fix the problems with
                           "lost" projectiles roaming around */
                        if (tempproj.x1 >= 1000 || tempproj.x1 <= 0 || tempproj.y1 >= 1000 || tempproj.x1 <= 0 ) {
                                stat = (Status) statuslist.elementAt(tempproj.ownerbotid);
				stat.num_proj--;
                                // don't remove dead projectiles until map
                                // has drawn them
                                tempproj.isLive = false;
	  			continue;
                        }
			tempproj.px = tempproj.x0;
	  		tempproj.x0 = tempproj.x1;
			tempproj.py = tempproj.y0;
	  		tempproj.y0 = tempproj.y1;
	  	}
}

private void updateprojectiles ( ) {
        Projectile tempproj;

	for (int loopvar = 0;loopvar < projectilelist.size();loopvar++) {
                tempproj = (Projectile) projectilelist.elementAt(loopvar);
                /* take out projectiles if they are currently outside of bounds */
                if (tempproj.x0 >= 1000 || tempproj.y0 >= 1000 || tempproj.x0 <= 0 || tempproj.y0 <= 0) {
                        Status stat = (Status) statuslist.elementAt(tempproj.ownerbotid);
//			stat.num_proj--;
                        tempproj.isLive = false;
                        continue;
                }
		if (tempproj.isLive) {
                        tempproj.x1 = tempproj.x0 + (tempproj.speed * (double) java.lang.Math.cos(tempproj.dir));
                        tempproj.y1 = tempproj.y0 + (tempproj.speed * (double) java.lang.Math.sin(tempproj.dir));
                        tempproj.vx = (double) java.lang.Math.cos(tempproj.dir);
                        tempproj.vy = (double) java.lang.Math.sin(tempproj.dir);
		}
	}	
	return;
}

private void cleanprojectiles() {
        Projectile ptemp;
        Obstacle obs;
	int i;

        for (i=0; i < projectilelist.size(); i++) {
                ptemp = (Projectile) projectilelist.elementAt(i);
                if (!ptemp.isLive) {
                        projectilelist.removeElementAt(i);
                        continue;
                }
        }
        for (i=0; i < passivelist.size(); i++) {
                obs = (Obstacle) passivelist.elementAt(i);
                if (!obs.isLive) {
                        passivelist.removeElementAt(i);
                        continue;
                }
        }
}

/**
 * This method was created by a SmartGuide.
 */
public void exec_nextbot (Bot callingbot) {
	int id;
	
	id = botlist.indexOf(callingbot);
        Status stat = (Status) statuslist.elementAt(id);

        sthread.interrupt();
        stat.thread.suspend();

	return;
}

/**
 Scheduler function is here:
	run_bots() sequentially executes the code for each bot (starting and stopping each 
			one according the actions it performs
	collide() checks to see if any collisions have occured between the bots, or if a 
			bot hit another bot with fire
	commit() sets the current status
*/
private void scheduler ( ) {

	if (activebot == -1) return;

        activebot = 0;
        boolean timeout=true;

        do {
		int id;
                Status stat = (Status) statuslist.elementAt(activebot++);
                if (stat.health > 0) {
                        stat.exeBot();

                        try {
                                Thread.sleep(3000);
                        } catch (InterruptedException e) {
        //                        System.out.println("bot ends early.");
                                timeout=false;
                                e = null;
                        }

                        if (timeout) {
                                System.out.println("Pre-empting bot. (Bot restarting)");

                                /** setPriority won't work because scheduler will not
                                    be interrupted sequentially by each bot.
                                **/
                                /** Priority should be returned to NORM_PRIORITY in
                                    stat.exeBot()
                                **/
        //                        stat.thread.setPriority(Thread.MIN_PRIORITY);
        //                        stat.thread.suspend();
                                stat.thread.stop();
                                text.append("bot" + Integer.toString(stat.botID)
                                        + " has timed out, check to see if you are\n"
                                        + "in an infinite loop or running in a Java\n"
                                        + "debugger. If not, notify:\n"
                                        + "acm-discuss@cise.ufl.edu with information\n"
                                        + "on your computer system and what you are\n"
                                        + "doing in your ai().\n");
                        }
                }

                if (statuslist.size() <= activebot) {
			if (pausetime > 0) {
				try {
					Thread.sleep(pausetime);
	                        } catch (InterruptedException e) {
		                        e = null;
			        }
			}
			next_turn();
			activebot = 0;
			continue;
		}

        } while (true);

}

public void next_turn ( ) {
	updateprojectiles();
	collide();
	commit();
	
	if (!disableArena) UpdateMap();
        cleanprojectiles();

	return;
}

/**
 * This method was created by a SmartGuide.
 */
private void UpdateMap ( ) {
	int index;
        Status status;
        Projectile tempproj;
        Obstacle obs;

	mapobjectlist.removeAllElements();

	for (index = 0; index < statuslist.size(); index++) {
		MapObject mapobject = new MapObject();
                status = (Status) statuslist.elementAt(index);
		
                mapobject.x = (int) (status.x0*mapScale);
                mapobject.y = (int) mapvisualizer.xlaty(status.y0*mapScale);
                mapobject.x0 = (int) (status.px*mapScale);
                mapobject.y0 = (int) mapvisualizer.xlaty(status.py*mapScale);
                mapobject.name = status.name;
                mapobject.ID = status.botID;
                if (status.health <= 0) {
                        mapobject.hit = true;
                } else {
			mapobject.iscloaked = status.iscloaked;
                        mapobject.scan_dir = status.scan_dir;
                        mapobject.scan_arc = status.scan_arc;
                        mapobject.dir_x = status.dir_vx;
                        mapobject.dir_y = status.dir_vy;
                        mapobject.scan1_x = status.scan1_vx;
                        mapobject.scan1_y = status.scan1_vy;
                        mapobject.scan2_x = status.scan2_vx;
                        mapobject.scan2_y = status.scan2_vy;
                        mapobject.health = status.health;
                }
		mapobjectlist.addElement(mapobject);
	}

	for (index = 0; index < projectilelist.size(); index++) {
		MapObject mapobject = new MapObject();
                tempproj = (Projectile) projectilelist.elementAt(index);

		if (tempproj.times_hit > 0) mapobject.hit = true;

		mapobject.x = (int) (tempproj.x0*mapScale);
		mapobject.y = (int) mapvisualizer.xlaty(tempproj.y0*mapScale);
		mapobject.x0 = (int) (tempproj.px*mapScale);
                mapobject.y0 = (int) mapvisualizer.xlaty(tempproj.py*mapScale);
                mapobject.name = tempproj.ownerBotName;
                mapobject.ID = tempproj.objID;
                mapobject.dir_x = tempproj.vx;
                mapobject.dir_y = tempproj.vy;

		mapobjectlist.addElement(mapobject);
	}

	for (index = 0; index < passivelist.size(); index++) {
		MapObject mapobject = new MapObject();
                obs = (Obstacle) passivelist.elementAt(index);

		mapobject.x = (int) (obs.x0*mapScale);
		mapobject.y = (int) mapvisualizer.xlaty(obs.y0*mapScale);
		mapobject.x0 = (int) (obs.x0*mapScale);
		mapobject.y0 = (int) mapvisualizer.xlaty(obs.y0*mapScale);
		mapobject.ID = obs.id;

		if (obs.id == obs.MINE && !obs.isLive) mapobject.hit = true;

		mapobjectlist.addElement(mapobject);
	}

	java.net.URL url=null;
	java.net.URL url2=null;

        mapvisualizer.UpdateMap(mapobjectlist, null, null);
/*
	if (isapplet) {
		try {
			url = new java.net.URL("file:/C:/Images/pool.jpg");
			url2 = new java.net.URL("file:/C:/Images/jug.gif");
		} catch (java.net.MalformedURLException e) {
			e = null;
		}
		mapvisualizer.UpdateMap(mapobjectlist, getImage(url), getImage(url2) );
	} else {
		mapvisualizer.UpdateMap(mapobjectlist, null, null);
	}
*/

	return;
}

}
