import Bot.*;
import Server.*;
import java.util.*;

/* make sure your class is public */
public class Aimer extends Bot {

/* add a constructor function to set the name of your
 * bot to personalize your javabot */
public Aimer() {
        super.name = "Aimer";
}

/** ai() is the brains of your JavaBot. */
public void ai() {
        /* (x,y) is the vector from you to your target */
        double x, y;

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
                if (infolist.size() == 1 || notarget) {
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
                for (int i=1; i < infolist.size(); i++) {

                        /* set info equal to the i'th object in infolist */
                        info = (Info) infolist.elementAt(i);

                        /* Don't process projectiles */
                        if (info.id < 0) continue;

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

			/** aiming ahead is based on distance (dist) and speed of target (dx). 3/16/2016 */
                        if (dist <= 25) aiming_dist = 0;
                        else if (dist > 25) aiming_dist = dist / 50.0f ;
//                        else if (dist > 25) aiming_dist = dist / 5.0f;

                        /* use the provided arctan method NOT the atan or
                         * atan2 methods to convert the vector to an angle */
                        dir = arctan(x, y);

                        /* if the previous vector is equal to zero, just
                         * fire in the (x,y) direction */
                        if (x0 == 0 && y0 == 0) super.Fire(x, y);
                        /* if not, aim ahead of him */
                        else projID = super.Fire(x+(aiming_dist*dx), y+(aiming_dist*dy));

                        /* don't get too close to him */
                        if (dist > 200) {
                                success = super.Drive(x, y, info.speed*4);
			} else if (dist > 100) {
				super.Drive(x, y, info.speed);
			} else if (dist > 25) {
                                success = super.Drive(0, 0, 0);
			} else if (dist < 25) {
				/** avoid collisions, move away if too close. 3/16/2016 */
				success = super.Drive(-y, -x, 2);
			}

                        /* set the old coordinates to (x,y) for the
                         * next turn. */
                        x0 = info.x;
                        y0 = info.y;
                }
                /* if the target was not found for 10 turns, find
                 * a new target */
                if (!targetfound) {
                        if (notargets++ > 10) {
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

