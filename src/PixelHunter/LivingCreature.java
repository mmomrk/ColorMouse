package PixelHunter;

/** User: mrk
 * Date: 8/23/13 * Time: 3:32 AM */
//stupid commentary
import java.awt.Point;

public abstract class LivingCreature
{
	public int id;     //0-99=>characters reserved; 100=>pet; 101=>target

	public L2Window l2Window;

	public abstract int getHP();

	public abstract boolean isDead();

	public abstract Point[] setHP();     //point1, point2

	public LivingCreature(int thisid)     //do we need this??
	{
		id = thisid;
	}
}
