
package Server;


public class Obstacle {
	public final static int WALL=-2;
	public final static int HEALTH=-3;
	public final static int BATTERY=-4;
	public final static int AMMO=-5;
	public final static int MINE=-6;
	public final static int SPECIAL=-100;
	
        public double damage;
        public double powervalue;
        public double x0;
        public double y0;
        public double x1;
        public double y1;
        public double radius;
	public int id;
	public int special;
	public boolean isLive;
}
