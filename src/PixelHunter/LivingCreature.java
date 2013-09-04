package PixelHunter;

/**
 * User: mrk
 * Date: 8/23/13 * Time: 3:32 AM
 */


public abstract class LivingCreature
{
	public int id;     //0-99=>characters reserved; 100=>pet; 101=>target

	public L2Window l2Window;

//	public GroupedVariables.ProjectConstants projectConstants;

	public abstract int getHP();

	public LivingCreature(int thisid)     //do we need this??
	{
		id = thisid;
	}
}
