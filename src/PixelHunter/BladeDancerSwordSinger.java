package PixelHunter;
import com.sun.jna.platform.win32.WinDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * User: mrk
 * Date: 9/26/13; Time: 3:00 PM
 */
public class BladeDancerSwordSinger extends Character
{
	private static final Logger logger = LoggerFactory.getLogger(BladeDancerSwordSinger.class);

	private final boolean isMaster;
	private static boolean masterIsFree = true;

	private static boolean songDanceReady = false;

	private static List<SkillSongDance> songDanceSequence = new LinkedList<SkillSongDance>();

	private ActionSongDance songDance = new ActionSongDance();

	private void rebuff()
	{
		if (this.isMaster) {
			for (SkillSongDance songDance : songDanceSequence) {

			}
		} else {
			return;
		}
		World.
	}

	@Override
	protected void setupBuffTimerMap()    //implement
	{
		logger.trace(".setupBuffTimerMap()");
		this.buffTimerMap.put(this.songDance, new Timer());
	}

	@Override
	public void classSpecificLifeCycle()        //implement
	{
		logger.trace(".classSpecificLifeCycle");

	}

	@Override
	public void onKill()    //implement
	{

	}

	public BladeDancerSwordSinger(int hisID, WinDef.HWND hwnd)
	{
		super(hisID, hwnd);
		logger.trace("BDSWS constructor");
		this.isBDSWS = true;
		if (masterIsFree) {
			this.isMaster = true;
			masterIsFree = false;
		} else {
			this.isMaster = false;
		}
		setupBuffTimerMap();        //;)

	}

	//CLASSES
	private class SkillSongDance extends Skill
	{
		public final boolean performedByBD;

		public SkillSongDance(int key, int reuseTimeSecs, boolean performedByBD)
		{
			super(key, reuseTimeSecs);
			this.performedByBD = performedByBD;
		}
	}


	private class ActionSongDance extends ActionAbstractBuff
	{
		@Override
		public Action getNewCopy()
		{
			return new ActionSongDance();
		}

		@Override
		public void perform()
		{
			logger.trace(".perform()");
			BladeDancerSwordSinger.this.buffTimerMap.get(this).schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					todoOffer(new ActionSongDance());
				}
			}, (2 * 60 - 15) * 1000);
			rebuff();       //!!after timer reset

		}

		public ActionSongDance()
		{
			super();
			this.priority = 350;
		}
	}
}
