package PixelHunter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;


/**
 * User: mrk
 * Date: 8/24/13; Time: 6:01 AM
 */
public abstract class SecondaryLivingCreature extends LivingCreature
{
	private static final Logger logger = LoggerFactory.getLogger(LivingCreature.class);

	public GroupedVariables.HpConstants hpConstants;


	public abstract int getHP();

	public abstract boolean isDead();

	public void setHP()
	{
		this.l2Window.setHP(this.hpConstants);
	}

	public SecondaryLivingCreature(int thisid, L2Window l2Window)
	{
		super(thisid);
		if (thisid != projectConstants.ID_PET && thisid != projectConstants.ID_TARGET) {
			logger.error("error in constructor of secondary LC. wrong ID");
			this.id = projectConstants.ID_PET;
		}
		hpConstants = new GroupedVariables.HpConstants(Color.black, new Point(0, 0), new Point(0, 0));
		if (l2Window == null){	//todo delete this if
			System.out.println("window is null!!");
		}
		this.l2Window = l2Window;
	}
}
