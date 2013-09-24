package PixelHunter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

import static PixelHunter.GroupedVariables.ProjectConstants.*;


/**
 * User: mrk
 * Date: 8/24/13; Time: 6:01 AM
 */
public abstract class SecondaryLivingCreature extends LivingCreature
{
	private static final Logger logger = LoggerFactory.getLogger(LivingCreature.class);

	public HpConstants hpConstants;


	public boolean isDead()

	{
		if (l2Window.colorsAreClose(l2Window.getRelPixelColor(hpConstants.coordinateLeft), hpConstants.color)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public int getHP()
	{
		return this.l2Window.getHP(this.hpConstants);
	}

	@Override
	public void setHP()
	{
		this.l2Window.setHP(this.hpConstants, this.id);
	}

	protected SecondaryLivingCreature(int thisid, L2Window l2Window)
	{
		super(thisid);
		logger.trace("SecondaryLC constructor");
		if (thisid != ID_PET && thisid != ID_TARGET) {
			logger.error("error in constructor of secondary LC. wrong ID");
			this.id = thisid;
		}

		if (l2Window == null) {
			logger.error("window is null!!");
		}
		this.l2Window = l2Window;


		if (thisid == ID_PartyMembersPet) {
			hpConstants = new HpConstants(PARTY_MEMBERS_PET_HP_COLOR, new Point(0, 0), new Point(0, 0), thisid);
		} else {
			hpConstants = new HpConstants(SECONDARY_LIVING_CREATURE_HP_COLOR, new Point(0, 0), new Point(0, 0), thisid); //primary pet and target hp colors are equal
		}
		//there used to be setHP(), but it was removed because of need for simple partyMember second constructor
	}
}
