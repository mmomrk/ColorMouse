package PixelHunter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

import static PixelHunter.GroupedVariables.ProjectConstants.*;

/**
 * User: mrk
 * Date: 9/23/13; Time: 2:12 PM
 */

public class PartyMember extends SecondaryLivingCreature       //not tested. check before use.
{
	public final  boolean     isSingle;
	public final HpConstants petHpConstants;

	public boolean nextExists()
	{
		if (L2Window.colorsAreClose(
								   l2Window.getRelPixelColor(
															new Point(hpConstants.coordinateLeft.x - 6, hpConstants.coordinateLeft.y+50)),
								   GroupedVariables.ProjectConstants.PARTY_MEMBERS_FRAME))
		{
			return true;
		}
		return false;
	}

	public int getHP(Object meansPetsGethp)    //overloaded
	{
		return this.l2Window.getHP(this.petHpConstants);
	}

	//	public L2Window l2Window;
	private static final Logger logger = LoggerFactory.getLogger(PartyMember.class);

	public PartyMember(L2Window l2Window)
	{
		super(GroupedVariables.ProjectConstants.ID_PartyMember, l2Window);
		logger.trace("creating PartyMember in window" + l2Window);
		setHP();
		if (L2Window.colorsAreClose(
								   this.l2Window.getRelPixelColor(
																 new Point(hpConstants.coordinateRight.x - 1, hpConstants.coordinateRight.y + 21)),
								   PARTY_MEMBERS_PET_HP_COLOR))  	//pet exists?
		{ //pet exists
			this.isSingle = false;
			this.petHpConstants = new HpConstants(PARTY_MEMBERS_PET_HP_COLOR,
												  new Point(hpConstants.coordinateLeft.x + 26, hpConstants.coordinateLeft.y + 21),
												  new Point(hpConstants.coordinateRight.x + 26, hpConstants.coordinateRight.y + 21),
												  ID_PartyMembersPet);
		} else {	//pet not exists
			this.isSingle = true;
			this.petHpConstants = new HpConstants(PARTY_MEMBERS_PET_HP_COLOR,
												  hpConstants.coordinateLeft,
												  hpConstants.coordinateRight,
												  ID_PartyMembersPet);
		}

	}

	public PartyMember(PartyMember higherMember)
	{
		super(ID_PartyMember, higherMember.l2Window);
		logger.trace("creating non-first PartyMember in window" + l2Window);
		if (!higherMember.isSingle) {//has pet
			this.hpConstants = new HpConstants(SECONDARY_LIVING_CREATURE_HP_COLOR,
										  new Point(higherMember.hpConstants.coordinateLeft.x,
													higherMember.hpConstants.coordinateLeft.y + 64),
										  new Point(higherMember.hpConstants.coordinateRight.x,
													higherMember.hpConstants.coordinateRight.y+64),
										  ID_PartyMember);
		}	else {
			this.hpConstants = new HpConstants(SECONDARY_LIVING_CREATURE_HP_COLOR,
											   new Point(higherMember.hpConstants.coordinateLeft.x,
														 higherMember.hpConstants.coordinateLeft.y + 46),//not an easter egg. just coincedence
											   new Point(higherMember.hpConstants.coordinateRight.x,
														 higherMember.hpConstants.coordinateRight.y+46),
											   ID_PartyMember);
		}

		//has pet check
		if (L2Window.colorsAreClose(
								   this.l2Window.getRelPixelColor(
																 new Point(hpConstants.coordinateRight.x - 1, hpConstants.coordinateRight.y + 21)),
								   PARTY_MEMBERS_PET_HP_COLOR))
		{ //pet exists
			this.isSingle = false;
			this.petHpConstants = new HpConstants(PARTY_MEMBERS_PET_HP_COLOR,
												  new Point(hpConstants.coordinateLeft.x + 26, hpConstants.coordinateLeft.y + 21),
												  new Point(hpConstants.coordinateRight.x + 26, hpConstants.coordinateRight.y + 21),
												  ID_PartyMembersPet);
		} else {
			this.isSingle = true;
			this.petHpConstants = new HpConstants(PARTY_MEMBERS_PET_HP_COLOR,
												  hpConstants.coordinateLeft,
												  hpConstants.coordinateRight,
												  ID_PartyMembersPet);
		}

	}

}
