package PixelHunter;
import com.sun.jna.platform.win32.WinDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;

import static PixelHunter.GroupedVariables.Mediator.noPetMode;
import static java.awt.event.KeyEvent.*;

/**
 * User: mrk
 * Date: 9/21/13; Time: 4:28 PM
 */
public class Warlock extends Character
{
	private static final Logger logger = LoggerFactory.getLogger(Warlock.class);
	private final        ActionSelfBuff    //second parameter is numpad key number
								buff1  = new ActionSelfBuff("2-minute buff", 2, (2 * 60 - 15) * 1000, 20 * 1000),
	buffMassSummonStormCubic           = new ActionSelfBuff("Mass Summon Storm Cubic", 3, (15 * 60 - 15) * 1000, 5 * 1000),
	buffSummonBindingCubic             = new ActionSelfBuff("Summon Binding Cubic", 4, (15 * 60 - 15) * 1000, 5 * 1000);

	private final Skill
	healPet   = new Skill(1, 1),    //watch it. not checked
	summonCat = new Skill(5, 1),
	pickup    = new Skill(6, 4);


	@Override
	protected void setupBuffTimerMap()
	{
		logger.trace(".setupBuffTimerMap");
		this.buffTimerMap.put(this.buff1, new Timer());
		this.buffTimerMap.put(this.buffMassSummonStormCubic, new Timer());
		this.buffTimerMap.put(this.buffSummonBindingCubic, new Timer());
	}

	@Override
	public void classSpecificLifeCycle()
	{
		logger.trace(".classSpecificLifeCycle");

		if (this.modeFarm || this.modeRB) {
			if (!noPetMode && pet.isDead()) {
				useSkill(summonCat);
				return;
			}
			if (!noPetMode && pet.getHP() < 70) {
				useSkill(healPet);
			}
		}
		if (this.modeFarm) {
			useSkill(pickup);
		}
	}

	@Override
	public void onKill()
	{
		logger.trace(".onKill();");
	}

	public Warlock(WinDef.HWND hwnd)
	{
		super(GroupedVariables.ProjectConstants.ID_Warlock, hwnd);
		logger.trace(".warlock constructor");
		setupBuffTimerMap();
		this.isHomeRunner = true;
		this.isSummoner = true;
	}

}
