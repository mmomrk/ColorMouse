package PixelHunter;
import com.sun.jna.platform.win32.WinDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;
import java.util.Timer;

/**
 * User: mrk
 * Date: 9/21/13; Time: 5:36 AM
 */
public class Spoiler extends Character
{
	private static final Logger logger = LoggerFactory.getLogger(Spoiler.class);

	private final ActionSelfBuff buffStub  = new ActionSelfBuff(null, 0, 0, 0);
	private       boolean
									 modeSpoil = false,
	temporarySpoil                             = false,
	iSpoiledTheMob                             = false;

	protected void message5(int caller)    //spoil my mob
	{
		logger.trace(".Spoil my mob Mode");
		this.temporarySpoil = true;
		selectPartyMemberByID(caller);
		assistTarget();
		spoil();
	}

	@Override
	public void activateModeBuff()
	{
		logger.trace(".activateModeSpoil");
		this.modeSpoil = true;
	}

	@Override
	public void deactivateModeBuff()
	{
		logger.trace(".deactivateModeSpoil");
		this.modeSpoil = false;
	}

	@Override
	protected void toggleBuffMode(){
		this.modeSpoil	=	!this.modeSpoil;
		logger.info("toggling Spoil mode. now it is "+this.modeSpoil);
	}

	@Override
	protected void cancelAllBuffScheduledTasks() {}   //has no buffs

	@Override
	protected void setupBuffTimerMap() {}

	@Override
	public void forceRebuff() {}


	@Override
	public void classSpecificLifeCycle()
	{
		if (!this.iSpoiledTheMob
			&&
			(!this.target.isDead() || this.target.getHP() < 99))
		{
			spoil();    //should contain macro spoil-attack
//			this.iSpoiledTheMob	= true;
		}
	}

	@Override
	public void onKill()
	{
		if (!this.target.isDead()) {          //looks stupid but that's the architecture
			return;
		}

		logger.trace(".onKill");

		if (this.temporarySpoil){
			this.temporarySpoil	=	false;
		}

		if (this.iSpoiledTheMob) {
			sweep();
		}
	}

	private void spoil()
	{
		if (!(this.modeSpoil || this.temporarySpoil)) {
			return;
		}
		logger.trace(".spoil");
		this.l2Window.keyClick(KeyEvent.VK_NUMPAD1);
		this.iSpoiledTheMob = true;
	}

	private void sweep()
	{
		logger.trace(".sweep");
		this.l2Window.keyClick(KeyEvent.VK_NUMPAD2);
	}

	public Spoiler(WinDef.HWND hwnd)
	{
		super(GroupedVariables.ProjectConstants.ID_Spoiler, hwnd);
		this.buffTimerMap.put(this.buffStub, new Timer());
		this.isHomeRunner = true;
		this.isPhysicAttacker	=	true;
	}
}
