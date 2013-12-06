package PixelHunter;
import com.sun.jna.platform.win32.WinDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * User: mrk
 * Date: 9/26/13; Time: 3:00 PM
 */
public abstract class SwordSingerBladeDancer extends Character
{
	protected static final Logger logger = LoggerFactory.getLogger(SwordSingerBladeDancer.class);

	protected final boolean isMaster;

	protected List<SkillSongDance> songDanceSequence = new LinkedList<SkillSongDance>();

	protected ActionSongDance songDance = new ActionSongDance();

	protected int currentSongDance = -1;

	protected boolean iAmSitting = false;

	protected void sitStand()
	{
		logger.trace(".sitStand();");
		iAmSitting = !iAmSitting;
		this.l2Window.keyClick(KeyEvent.VK_NUMPAD8);
	}

	@Override
	public void nowWeWillSingDance()
	{
		logger.trace(".nowWeWillSingDance");
		this.currentSongDance = -1;
	}

	@Override
	public boolean nextSongDance()    //retval is 'are you finished' answer
	{
		logger.trace(".nextSongDance current songdance is " + ++this.currentSongDance);
		this.iAmSitting = true;//i will be sitting after singing/dancing
		this.isSummoner = true;//funny but when sitting he is disabled, so attacking only through pet
		this.l2Window.activate();
		easySleep(800);
		if (this.currentSongDance < this.songDanceSequence.size() - 1) {
			useSkill(this.songDanceSequence.get(this.currentSongDance));
			return false;
		} else if (this.currentSongDance == this.songDanceSequence.size() - 1) {
			useSkill(this.songDanceSequence.get(this.currentSongDance));
			return true;
		} else {
			return true;
		}

	}

	@Override
	protected void setupBuffTimerMap()    //implement
	{

		this.buffTimerMap.put(this.songDance, new Timer());

	}

	@Override
	public void classSpecificLifeCycle()
	{
		logger.trace(".classSpecificLifeCycle");
		if (this.iAmSitting) {
			if (getMP() > 80) {
				sitStand();
				this.isSummoner = false;
			} else {
				logger.debug("i think i am sitting now");
			}
		}
	}

	@Override
	public void onKill()
	{

	}

	public SwordSingerBladeDancer(int hisID, WinDef.HWND hwnd)
	{
		super(hisID, hwnd);
		logger.trace("BDSWS constructor");
		this.isBDSWS = true;
		if (GroupedVariables.BDSWSBrains.masterIsFree) {
			this.isMaster = true;
			GroupedVariables.BDSWSBrains.masterIsFree = false;
		} else {
			this.isMaster = false;
		}
		setupBuffTimerMap();        //;)

	}

	//CLASSES
	protected class SkillSongDance extends Skill
	{
		public SkillSongDance(int key)
		{
			super(key, 3);    //hardcode is bad. but else it will be too stupid
		}
	}


	protected class ActionSongDance extends ActionAbstractBuff
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
			SwordSingerBladeDancer.this.buffTimerMap.get(this).schedule(new TimerTask()
			{
				@Override
				public void run()
				{    //error is here(todo,implement,remove after!!!
					if (SwordSingerBladeDancer.this.isMaster) {
						todoOffer(new ActionSongDance());
					} else {
						logger.warn("call for ActionSongDance from BDSWS slave. this should not be");
					}
				}
			}, (2 * 60 - 15) * 1000);
			World.BDSWSBuff();       //!!after timer reset

		}

		public ActionSongDance()
		{
			super();
			this.priority = 350;
		}
	}
}
