package PixelHunter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

import static PixelHunter.GroupedVariables.projectConstants;


/**
 * User: mrk
 * Date: 8/24/13; Time: 6:01 AM
 */
public abstract class SecondaryLivingCreature extends LivingCreature
{
	private static final Logger logger = LoggerFactory.getLogger(LivingCreature.class);

	public GroupedVariables.HpConstants hpConstants;


	public boolean isDead()	//todo: testmd

	{
		if (l2Window.colorsAreClose(l2Window.getRelPixelColor(hpConstants.coordinateLeft),hpConstants.color))
		{
			return false;
		}
		else {
			return true;
		}
	}

	public int getHP()
	{
		return this.l2Window.getHP(this.hpConstants);
	}

	public void setHP()
	{
		this.l2Window.setHP(this.hpConstants);
	}

	public SecondaryLivingCreature(int thisid, L2Window l2Window)
	{
		super(thisid);
		if (thisid != GroupedVariables.ProjectConstants.ID_PET && thisid != GroupedVariables.ProjectConstants.ID_TARGET) {
			logger.error("error in constructor of secondary LC. wrong ID");
			this.id = GroupedVariables.ProjectConstants.ID_PET;
		}
		hpConstants = new GroupedVariables.HpConstants(projectConstants.SECONDARY_LIVING_CREATURE_HP_COLOR, new Point(0, 0), new Point(0, 0)); //primary pet and target hp colors are equal
		if (l2Window == null){	//todo delete this if
			System.out.println("window is null!!");
		}
		this.l2Window = l2Window;
	}
}
