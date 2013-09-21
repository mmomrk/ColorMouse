package PixelHunter;
import com.sun.jna.platform.win32.WinDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Timer;

/**
 * User: mrk
 * Date: 9/3/13; Time: 2:13 AM
 */
public class DefaultCharacter extends Character
{
	private static final Logger logger = LoggerFactory.getLogger(DefaultCharacter.class);
	private final        ActionBuff    //second parameter is numpad key number
								buff1  = new ActionBuff("20-minute buff", 1, (20 * 60 - 15) * 1000, 20 * 1000),
	buff2                              = new ActionBuff("5-minute buff", 2, (5 * 60 - 15) * 1000, 10 * 1000),
	buff3                              = new ActionBuff("2-minute buff", 3, (2 * 60 - 15) * 1000, 4 * 1000);


	@Override
	protected void cancelAllBuffScheduledTasks()//not tested
	{
		logger.trace("cancelAllBuffScheduledTasks");
		for (Map.Entry<ActionBuff, Timer> buffTimerEntry : this.buffTimerMap.entrySet()) {
			buffTimerEntry.getValue().cancel();

		}
		this.buffTimerMap.clear();
		setupBuffTimerMap();

	}

	@Override
	protected void setupBuffTimerMap()	//not tested
	{
		logger.trace(".setupBuffTimerMap");
		this.buffTimerMap.put(this.buff1, new Timer());
		this.buffTimerMap.put(this.buff2, new Timer());
		this.buffTimerMap.put(this.buff3, new Timer());
	}

	@Override
	public void classSpecificLifeCycle()    //IMPlement
	{
		logger.trace(".classSpecificLifeCycle");
	}

	public DefaultCharacter(WinDef.HWND hwnd)
	{
		super(GroupedVariables.ProjectConstants.ID_DefaultCharacter, hwnd);
	    setupBuffTimerMap();

	}
}
