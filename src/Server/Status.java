
package Server;

import Bot.*;
import java.util.*;

/**
*  Status Class contains all information pertinent to an individual bot.  Its Simulation
* representation
*/
class Status
{
        public int collisiondamage = 1;
	public int maxbattery = 500;
	public int maxcloaktime = 500;
	public boolean iscloaked = false;
	public int cloaktimer = 0;
        public boolean alive=true;
        public double battery = 500.0f;
        public double speed, health, dir;
        public double speed2, health2, dir2;

	/* new physics model */
        public double momentum_x, momentum_y;
        public double momentum_x2, momentum_y2;
        public double force_x, force_y;

        public double acceleration=1.0f;
        public double deceleration=1.0f;

        /* direction vector */
        public double dir_vx, dir_vy;
        /* scan vector */
        public double scan1_vx, scan1_vy;
        public double scan2_vx, scan2_vy;
        public double scan_dir;
        public double scan_arc;

        public double x0, y0;
        public double x1, y1;
        public double px, py;

        public double scandir;

        public String name;

	/* stores projectile log */
	public Vector projectilelist;
	public int projectileindex=0;
        public boolean fired=false;
	public int num_proj=0;
        public int max_proj=500;
        public int gunheat=0;
	public int overheat=40;

        public int times_hit=0;
        public int times_collided=0;
        public Vector hitlist;
        public Vector collidelist;

        public double max_speed=20.0f;

        public Bot bot;
	public Thread thread = null;
	boolean suspended=false;

        public int botID;
        public int botGID;

public Status(Bot ownerbot) {
	projectilelist = new Vector();
        hitlist = new Vector();
        collidelist = new Vector();
        bot = ownerbot;
        x0 = 0.0f;
        y0 = 0.0f;
        x1 = 0.0f;
        y1 = 0.0f;
        speed = 0.0f;
        health = 100.0f;
        health2 = health;
        dir = 0.0f;
        scandir = 0.0f;
        botID = -1;
        botGID = -1;
}
/**
 * This method was created by a SmartGuide.
 */
public void exeBot ( ) {
	if (thread == null) {
		thread = new Thread(bot);
		thread.start();
	} else {
//                thread.setPriority(Thread.NORM_PRIORITY);
		thread.resume();
	}	
	return;
}
}
