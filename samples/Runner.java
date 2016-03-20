import Bot.*;
import Server.*;
import java.util.*;

/* make sure your class is public */
public class Runner extends Bot {

/* add a constructor function to set the name of your
 * bot to personalize your javabot */
public Runner() {
        super.name = "Runner";
}

/** ai() is the brains of your JavaBot. */
public void ai() {
        /* (x,y) is the vector from you to your target */
        double x=0, y=0;

        /* dist is the distance between you and your target */
        double dist;
        /* predicted distance of projectile travel */
        double aiming_dist=1;

        /* (x0, y0) will hold the vector to your target from
         *  the last turn. */
        double x0=0, y0=0;

        /* (dx, dy) is the difference between the current and
         * previous target vectors */
        double dx=0, dy=0;

	boolean defensive = false;

        int notargets=0;
        boolean notarget=true;
        boolean targetfound=false;
        int targetID=-1;

        boolean success;
        int projID;

        /* Direction that you will scan */
        double dir=0;

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

                /* if there are no more info objects (i.e., there is
                 * only one element in the infolist), then stop and
                 * begin a counterclockwise sweep of the playing field */
                if (!targetfound) {
                        /* stop driving */
                        success = super.Drive(0, 0, 0);
                        /* increase the direction of scan by PI/20 */
                        dir += java.lang.Math.PI/20;
                }

                /* set targetfound to false before beginning a loop
                 * through the rest of the info objects */
                targetfound=false;

                /* start at the second info object and repeat
                 * there are no more objects */
                for (int i=1; i < infolist.size() && !defensive; i++) {

                        /* set info equal to the i'th object in infolist */
                        info = (Info) infolist.elementAt(i);

                        /* Don't process projectiles */
                        if (info.id < 0) break;

                        /* If there is no target, find the first available
                         * bot and make that your target */
                        if (notarget) {
                                targetID = info.id;
                                targetfound=true;
                                notarget=false;

                        /* else we have an existing target -- check to
                         * see if the current info object has the same
                         * id as our target's id. If not, continue to the
                         * next bot */
                        } else if (info.id != targetID) {
                                continue;
                        }

                        /* The info.id == targetID, so set the
                         * targetfound to true */
                        targetfound=true;

                        /* find the difference between the current
                         * and previous target coords */
                        dx = info.x - x0;
                        dy = info.y - y0;

                        /* set (x,y) to the vector pointing to the
                         * target bot */
                        x = (info.x) - me.x;
                        y = (info.y) - me.y;

                        /* find the distance to that bot */
                        dist = (double) java.lang.Math.sqrt(x*x + y*y);

                        if (dist <= 25) aiming_dist = 0;
                        else if (dist > 25) aiming_dist = 1;
                        else if (dist > 100) aiming_dist = dist / 50.0f;

                        /* use the provided arctan method NOT the atan or
                         * atan2 methods to convert the vector to an angle */
                        dir = arctan(x, y);

                        /* if the previous vector is equal to zero, just
                         * fire in the (x,y) direction */
                        if (x0 == 0 && y0 == 0) super.Fire(x, y);
                        /* if not, aim ahead of him */
                        else projID = super.Fire(x+(aiming_dist*dx), y+(aiming_dist*dy));

                        /* drive a little away from him */
                        if (dist > 100) {
                                success = Drive(x+50, y+50, info.speed*4);
			} else if (dist > 25) {
                                success = Drive(x+50, y+50, info.speed);
			}

                        /* set the old coordinates to (x,y) for the
                         * next turn. */
                        x0 = info.x;
                        y0 = info.y;

			/* break after finding target */
			break;
		}

		if (me.speed == 0) {
			if (me.times_collided > 0) {
				x *= -1;
                                y *= -1;
                                speed = 10;
				defensive = true;
			} else {
				speed = 5;
				defensive = false;
			}
                        if (me.battery > 50) {
				Drive(x, y, speed);
			}
		}

                if ((me.times_hit > 0) && (!me.iscloaked) && (me.cloaktimer==0)) {
			Cloak(true);
                        x*= -1;
                        Drive(x+50,y+50,1);
		} else if ((me.times_hit > 0) && me.iscloaked) {
			defensive = true;
			y*= -1;
                        Drive(x+50,y+50,10);
		} else if ((me.cloaktimer > 250) && me.iscloaked) {
			Cloak(false);
			defensive = false;
                        y*= -1;
                        Drive( x+50, y+50, 5);
		}

                /* if the target was not found for 10 turns, find
                 * a new target */
                if (!targetfound) {
                        if (notargets++ > 10) {
				defensive = false;
                                notarget=true;
                                notargets=0;
                                x0 = 0;
                                y0 = 0;
                        }
                } else {
                        notargets=0;
                }
	  }
}


}

