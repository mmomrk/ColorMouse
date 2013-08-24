package PixelHunter;


/**
 * Created with IntelliJ IDEA.
 * User: mrk
 * Date: 8/23/13
 * Time: 3:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class Pet extends SecondaryLivingCreature
{
//	public L2Window l2Window;

	public Pet(L2Window l2Window)
	{
		super(GroupedVariables.projectConstants.ID_PET, l2Window);
	}


	@Override
	public int getHP()    //todo first
	{
		return 0;
	}

	@Override
	public boolean isDead()    //todo
	{
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}



}
