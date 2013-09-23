package PixelHunter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: mrk
 * Date: 9/3/13; Time: 5:22 AM
 */
public class Target extends SecondaryLivingCreature
{
	//	public L2Window l2Window;
	private static final Logger logger = LoggerFactory.getLogger(PartyMember.class);

	public Target(L2Window l2Window)
	{
		super(GroupedVariables.projectConstants.ID_TARGET, l2Window);
		logger.trace("creating target in window" + l2Window);
		setHP();
	}

}