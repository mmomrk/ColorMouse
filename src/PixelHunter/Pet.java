package PixelHunter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: mrk
 * Date: 8/23/13
 */
public class Pet extends SecondaryLivingCreature
{
	//	public L2Window l2Window;
	private static final Logger logger = LoggerFactory.getLogger(Pet.class);

	public Pet(L2Window l2Window)
	{
		super(GroupedVariables.projectConstants.ID_PET, l2Window);
		logger.trace("creating pet in window" + l2Window);
		setHP();
	}

}
