package PixelHunter;
import com.sun.jna.platform.win32.WinDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.Timer;

/**
 * User: mrk
 * Date: 9/21/13; Time: 4:42 AM
 * <p/>
 * Buffer should command WC to heal if many p members are wounded.
 * command number 5:class specif
 */
public class Warcryer extends Character
{
	private static final Logger logger = LoggerFactory.getLogger(Warcryer.class);
	private final        ActionBuff    //second parameter is numpad key number
								buff1  = new ActionBuff("20-minute buff", 1, (20 * 60 - 15) * 1000, 20 * 1000);

	@Override
	protected void message5(int callerID)
	{
		logger.trace(".healAll");
		if (!this.target.isDead()) {
			this.l2Window.keyClick(KeyEvent.VK_F6);    //even if it sounds funny: call for help to kill to heal
		}
		todoOffer(new ActionHeal());
	}

//	@Override		//remove after test
//	protected void cancelAllBuffScheduledTasks()//not tested
//	{
//		logger.trace("cancelAllBuffScheduledTasks");
//		for (Map.Entry<ActionBuff, Timer> buffTimerEntry : this.buffTimerMap.entrySet()) {
//			buffTimerEntry.getValue().cancel();
//		}
//		this.buffTimerMap.clear();
//		setupBuffTimerMap();
//
//	}

	@Override
	protected void setupBuffTimerMap()    //not tested
	{
		logger.trace(".setupBuffTimerMap");
		this.buffTimerMap.put(this.buff1, new Timer());
	}

	@Override
	public void classSpecificLifeCycle()    //IMPlement
	{
		logger.trace(".classSpecificLifeCycle");

	}

	@Override
	public void onKill()
	{

	}

	public Warcryer(WinDef.HWND hwnd)
	{
		super(GroupedVariables.ProjectConstants.ID_Warcryer, hwnd);
		setupBuffTimerMap();
		isHomeRunner = false;
	}

	class ActionHeal extends Action
	{

		@Override
		public void perform()
		{
			Warcryer.this.l2Window.keyClick(KeyEvent.VK_NUMPAD2);
			logger.trace("ActionHeal:perform");
		}

		ActionHeal()
		{
			super();
			this.priority = 400;    //highest possible
			logger.trace("created action Heal ALL");
		}
	}
}
