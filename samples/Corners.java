import Bot.*;
import Server.*;
import java.util.*;

/* make sure your class is public */
public class Corners extends Bot {

/* add a constructor function to set the name of your
 * bot to personalize your javabot */
public Corners() {
        super.name = "Corners";
}

/** ai() is the brains of your JavaBot. */
public void ai() {
        /* (x,y) is the vector from you to your target */
        double x, y;

        /* dist is the distance between you and your target */
        double dist;

        /* (x0, y0) will hold the vector to your target from
         *  the last turn. */
        double x0=0, y0=0;

        /* (dx, dy) is the difference between the current and
         * previous target vectors */
        double dx=0, dy=0;

        int notargets=0;
        boolean notarget=true;
        boolean targetfound=false;
        int targetID=-1;

        /* Direction that you will scan */
        double dir=0;
        double maxdir=0;
        double mindir=(double) java.lang.Math.PI*2;
	boolean corner = false;

        int scandir=1;

        /* Vector of info objects that you've scanned */
        Vector infolist;

        /* An info object containing information on your target */
        Info info;

        /* Info object with information about you */
        Info me;

        /* This is the main loop of your javabot */
        while (true) {
                /* scan your surroundings to get info */
                infolist = super.Scan(dir);

                /* the first element is your info */
                me = (Info) infolist.elementAt(0);

                if ((50-me.x > 5 && 50-me.y > 5) || (50-me.x > 5 && 950-me.y > 5)
			|| (950-me.x > 5 && 50-me.y > 5) || (950-me.x > 5 && 950-me.y > 5)) {
                        if (me.x > 500) {
                                if (me.y > 500) {
                                        Drive (950-me.x,950-me.y);
                                        maxdir = (double) java.lang.Math.PI*3/2;
                                        mindir = (double) java.lang.Math.PI;
                                        dir = mindir;
                                } else if (me.y < 500) {
                                        Drive (950-me.x, 50-me.y);
                                        maxdir = (double) java.lang.Math.PI;
                                        mindir = (double) java.lang.Math.PI/2;
                                        dir = mindir;
                                }
                        } else if (me.x < 500) {
                                if (me.y > 500) {
                                        Drive (50-me.x, 950-me.y);
                                        maxdir = (double) java.lang.Math.PI*2;
                                        mindir = (double) java.lang.Math.PI*3/2;
                                        dir = mindir;
                                } else if (me.y < 500) {
                                        Drive (50-me.x, 50-me.y);
                                        maxdir = (double) java.lang.Math.PI/2;
                                        mindir = (double) 0.0f;
                                        dir = mindir;
                                }
                        }
                } else {
                        Drive (0, 0, 0);
			corner = true;
                }

                /* if there are no more info objects (i.e., there is
                 * only one element in the infolist), then stop and
                 * begin a counterclockwise sweep of the playing field */
                if (infolist.size() == 1) {
                        /* increase the direction of scan by PI/20 */
                        if (!corner) dir += (double) java.lang.Math.PI/4;
                        else {
				if (dir > maxdir) scandir = -1;
				else if (dir < mindir) scandir = 1;
				dir += (scandir*0.1);
			}
                }

                /* set targetfound to false before beginning a loop
                 * through the rest of the info objects */
                targetfound=false;

                /* start at the second info object and repeat
                 * there are no more objects */
                for (int i=1; i < infolist.size(); i++) {

                        /* set info equal to the i'th object in infolist */
                        info = (Info) infolist.elementAt(i);

                        /* skip projectiles */
                        if (info.id == -1) break;

                        /* set (x,y) to the vector pointing to the
                         * target bot */
                        x = info.x - me.x;
                        y = info.y - me.y;

                        /* find the difference between the current
                         * and previous vector */
                        dx = x - x0;
                        dy = y - y0;

                        /* find the distance to that bot */
                        dist = (double) java.lang.Math.sqrt(x*x + y*y);

                        /* use the provided arctan method NOT the atan or
                         * atan2 methods to convert the vector to an angle */
                        dir = arctan(x, y);

                        /* if the previous vector minus current is equal to zero,
			 * just fire in the (x,y) direction */
                        if (dx == 0 && dy == 0) super.Fire(x, y);
                        /* if not, aim ahead of him */
                        else super.Fire(x+dx, y+dy);

                        /* set the old coordinates to (x,y) for the
                         * next turn. */
                        x0 = x;
                        y0 = y;
                }
	  }
}


}

