package PixelHunter;
import com.sun.jna.platform.win32.WinDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;

import static java.awt.event.KeyEvent.*;

/**
 * User: mrk
 * Date: 9/21/13; Time: 6:12 AM
 */
public class TempleKnight extends Character
{
	private static final Logger logger          = LoggerFactory.getLogger(TempleKnight.class);
	private final        ActionSelfBuff                //second parameter is numpad key number
								summonLifeCubic = new ActionSelfBuff("Life Cube", 1, (15 * 60) * 1000, 0),
	summonStormCubic                            = new ActionSelfBuff("Storm Cube", 2, (15 * 60) * 1000, 0),    //watch:what's the time?
	summonAttractCubic                          = new ActionSelfBuff("Attract Cube", 3, (15 * 60) * 1000, 0);

	private boolean
	canUseUD           = false,
	temporaryAggroMode = false;

	private Skill
	ultimateDefence      = new Skill(VK_NUMPAD5, 30),
	attackSkill          = new Skill(VK_NUMPAD6, 3),
	aggro                = new Skill(VK_NUMPAD7, 15 * 60),
	massAggro            = new Skill(VK_NUMPAD8, 40),
	shieldFortressToggle = new Skill(VK_NUMPAD9, 1),
	guardStanceToggle    = new Skill(VK_NUMPAD0, 1);


	@Override
	protected void message6(int callerID)
	{    //everything is bad. mass aggr
		logger.trace(".message6();");
		useSkill(massAggro);
		this.temporaryAggroMode = true;
		this.canUseUD = true;

	}

	protected void message5(int callerID)
	{
		aggrHis(callerID);
	}


	private void aggrHis(int callerID)
	{
		logger.trace(".aggrHis");
		if (callerID != this.id) {
			selectPartyMemberByID(callerID);
			assistTarget();
		}
		aggr();
		this.temporaryAggroMode = true;
	}

	private void aggr()
	{
		logger.trace(".aggr");
		this.l2Window.keyClick(VK_NUMPAD7);
		attack();    //discuss if this is needed. watch it
	}


	@Override
	protected void setupBuffTimerMap()
	{
		logger.trace(".setupBuffTimerMap");
		this.buffTimerMap.put(this.summonLifeCubic, new Timer());
		this.buffTimerMap.put(this.summonStormCubic, new Timer());
		this.buffTimerMap.put(this.summonAttractCubic, new Timer());
	}

	@Override
	public void classSpecificLifeCycle()
	{
		logger.trace(".classSpecificLifeCycle");

		if (this.modeRB || this.temporaryAggroMode) {
			aggr();
		}

		if (getHP() < 25) {    //very bad. very bad
			logger.warn("character is dying!!");
			useSkill(ultimateDefence);
			this.l2Window.keyClick(VK_F6);    //chat help
		}

		if (this.canUseUD && getHP() < 50) {
			useSkill(ultimateDefence);
			this.canUseUD = false;
		}

	}

	@Override
	public void onKill()
	{
		if (this.temporaryAggroMode) {
			this.temporaryAggroMode = false;
		}
	}

	public TempleKnight(WinDef.HWND hwnd)
	{
		super(GroupedVariables.ProjectConstants.ID_Templeknight, hwnd);
		setupBuffTimerMap();
		this.isHomeRunner = true;
		this.isPhysicAttacker = true;
		this.isTank = true;
	}
}
