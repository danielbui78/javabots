
package Bot;

import Server.*;
import java.util.*;

/**
*  Base Bot Class (interface?) for subclassing (implementing?)
* by high schools.
*/
public class Bot implements Runnable {
	/* from the CObstacle class for your convenience */
	public final static int WALL=-2;
	public final static int HEALTH=-3;
	public final static int BATTERY=-4;
	public final static int AMMO=-5;
	public final static int MINE=-6;
	public final static int SPECIAL=-100;

	//name of the bot
	public String name;
	public BotHandler handler;
        public double speed;

/**
 * This method was created by a SmartGuide.
 */
public Bot () {
        name = "Bot";
}

public Bot(String s, BotHandler bh)
{
	handler = bh;
	name = new String(s);
	speed = 5.0f;
}

final protected boolean Cloak(boolean engagingcloaking) 
{
	return handler.Cloak(engagingcloaking, this);
}

/**
 * This method was created by a SmartGuide.
 */
final protected boolean Drive (double d) {
        return handler.Drive(d, this);
}

final protected boolean Drive (double x, double y, double s) {
	return handler.Drive(x, y, s, this);
}

/**
 * This method was created by a SmartGuide.
 */
final protected boolean Drive (double x, double y) {
        return handler.Drive(x, y, this);
}

final protected int Fire(double direction)
{
        return handler.Fire(direction, this);
}

final protected int Fire(double x, double y)
{
        return handler.Fire(x, y, this);
}

public final void run() {
        ai();
}

public final double arctan(double x, double y) {
        double dir=0;

        if (x == 0) {
                if (y > 0) dir = (double) java.lang.Math.PI/2;
                if (y < 0) dir = (double) java.lang.Math.PI*3/2;
                if (y == 0) dir = 0.0f;
        } else if (y == 0) {
                if (x > 0) dir = (double) 0.0;
                else if (x < 0) dir = (double) java.lang.Math.PI;
        } else {
                dir = (double) java.lang.Math.atan(y/x);
                if (x < 0) dir += (double) java.lang.Math.PI;
                else if (x > 0 && y < 0) dir += (double) java.lang.Math.PI*2;
        }

        return dir;
}

/*This function over-rided by the High School Bots */
public void ai() {
		double w, h;
		int dw=1, dh=1;
                double dir=0;
                double speed;
                double cloaktime;
                double dot_product;
                double x, y;
                double dx=0, dy=0;
                double temp;
		Vector infolist;
		Info info;

                boolean skipx = false, skipy = false;

                Drive((double) (java.lang.Math.random()*2*java.lang.Math.PI));

        while (true) {
                infolist = Scan(dir);
                info = (Info) infolist.elementAt(0);

                dir = info.dir;
                x = (double) java.lang.Math.cos(dir);
                y = (double) java.lang.Math.sin(dir);

                if (info.x >= 950 || info.x <= 50) {
                        if (!skipx) {
				x *= -1;
				skipx = true;
			}
                } else {
                        skipx = false;
                }
                if (info.y >= 950 || info.y <= 50) {
                        if (!skipy) {
				y *= -1;
				skipy = true;
			}
                } else {
                        skipy = false;
                }
		if (skipx || skipy) {
			Drive(x, y);
		}

                if (info.speed == 0) {
                        if (info.times_collided > 0) {
                                x *= -1;
                                y *= -1;
                                speed = 10;
                        } else {
                                speed = 5;
                        }
                        if (info.battery > 50) {
                                Drive(x, y, speed);
                        }
                }
                if ((info.times_hit > 0) && (!info.iscloaked) && (info.cloaktimer==0))
		{
//			System.out.println(name + " attempts cloaking.");
			Cloak(true);
			x*= -1;
                        Drive(x,y,1);
                } else if ((info.times_hit > 0) && info.iscloaked) {
                        y*= -1;
                        Drive(x,y,10);
                } else if ((info.cloaktimer > 100) && info.iscloaked) {
                        Cloak(false);
                        y*= -1;
                        Drive( x, y, 5);
                }


        }
}

final protected Vector Scan(double dir)
{
	Vector infolist;

	infolist = handler.Scan(dir, this);

	return infolist;
}

final protected Info getProjectileInfo(int id)
{
        Info info;

        info = handler.getProjectileInfo(id, this);

        return info;
}


}
