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
	summonStormCubic                            = new ActionSelfBuff("Storm Cube", 2, (15 * 60) * 1000, 0),    //todo:what's the time?
	summonAttractCubic                          = new ActionSelfBuff("Attract Cube", 3, (15 * 60) * 1000, 0);

	private boolean temporaryAggroMode = false;

	private void toggleGuardStance()
	{
		logger.trace("toggleGuardStance();");
		this.l2Window.keyClick(VK_NUMPAD0);
	}

	private void toggleShieldFortress()
	{
		logger.trace("toggleShieldFortress();");
		this.l2Window.keyClick(VK_NUMPAD9);
	}

	private void useAttackSkill()
	{
		logger.trace("useAttackSkill();");
		this.l2Window.keyClick(VK_NUMPAD6);
	}

	private void aggrHis(int callerID)
	{
		logger.trace(".aggrHis");
		selectPartyMemberByID(callerID);
		assistTarget();
		aggr();
		this.temporaryAggroMode = true;
	}

	private void ultimateDefence()
	{
		this.l2Window.keyClick(VK_NUMPAD5);
		logger.trace(".ultimateDefence();");
		this.temporaryAggroMode	=	true;
	}

	private void aggr(){
		logger.trace(".aggr");
		this.l2Window.keyClick(VK_NUMPAD7);
		attack();	//discuss if this is needed. watch it
	}

	protected void message5(int callerID){
		aggrHis(callerID);
	}

//	@Override		//remove after test
//	protected void cancelAllBuffScheduledTasks()
//	{
//		logger.trace("cancelAllBuffScheduledTasks");
//		for (Map.Entry<Character.ActionSelfBuff, Timer> buffTimerEntry : this.buffTimerMap.entrySet()) {
//			buffTimerEntry.getValue().cancel();
//		}
//		this.buffTimerMap.clear();
//		setupBuffTimerMap();
//
//		logger.trace("cancelAllBuffScheduledTasks");
//		for (Map.Entry<ActionSelfBuff, Timer> buffTimerEntry : this.buffTimerMap.entrySet()) {
//			buffTimerEntry.getValue().cancel();
//		}
//		this.buffTimerMap.clear();
//		setupBuffTimerMap();
//
//	}

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

		if (this.modeRB || this.temporaryAggroMode){
			aggr();
		}

		if (getHP()<25){	//very bad. very bad
			logger.warn("character is dying!!");
			ultimateDefence();
		}

	}

	@Override
	public void onKill()
	{
		if (this.temporaryAggroMode){
			this.temporaryAggroMode	=	false;
		}
	}

	public TempleKnight(WinDef.HWND hwnd)
	{
		super(GroupedVariables.ProjectConstants.ID_Templeknight, hwnd);
		setupBuffTimerMap();
		isHomeRunner = true;
	}
}
