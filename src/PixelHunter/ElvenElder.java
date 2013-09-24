package PixelHunter;
import com.sun.jna.platform.win32.WinDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

import static PixelHunter.GroupedVariables.ProjectConstants.*;

/**
 * User: mrk
 * Date: 9/21/13; Time: 4:40 AM
 * <p/>
 * by now party heal system will freeze if he can't heal the member
 * <p/>
 * !!!!!!!!!!!!!!!!!!!!!!!have to think of selecting party members pets and buffing different buffs on them. Watch Party member class. needs to be done
 */
public class ElvenElder extends PixelHunter.Character    //todo
{
	private static final Logger logger = LoggerFactory.getLogger(ElvenElder.class);

	private final ActionBuff    //second parameter is numpad key number
	buff1 = new ActionBuff("20-minute buff for pAttackers", 1, (20 * 60 - 30), 20, 0, false),    //todo: very much todo: set all macro times
	buff2 = new ActionBuff("20-minute buff for pAttackers' PETS", 1, (20 * 60 - 30), 20, 0, true),
	buff3 = new ActionBuff("20-minute buff for nukers", 2, (20 * 60 - 30), 20, 0, false),
	buff4 = new ActionBuff("20-minute buff for supports and summoners", 3, (20 * 60 - 30), 15, 0, false),
	buff5 = new ActionBuff("20-minute buff for shielded party members", 1, (20 * 60 - 30), 5, 0, true);


	private boolean iAmHealing  = false;
	private Skill
					GreaterHeal = new Heal(5, 2),    //watch it: 2 may not be true
	MajorHeal                   = new Heal(6, 1);    //watch this too. needs to be verified


	public Character.ActionAbstractBuff cloneBuff(ElvenElder.ActionBuff oldSelfBuff)
	{
		return new ElvenElder.ActionBuff(oldSelfBuff, oldSelfBuff.targetID);
	}

	@Override
	protected void message5(int callerID)
	{
		logger.trace(".message5(); from " + callerID);
		selectPartyMemberByID(callerID);
		useSkill(MajorHeal);
		todoOffer(new ActionHeal(6, 10, false));
	}

	@Override
	protected void setupBuffTimerMap()    //not tested	//implemented.finished here
	{
		logger.trace(".setupBuffTimerMap");
		buffTimerMap.put(new ActionBuff(buff1, ID_Spoiler), new Timer());
		buffTimerMap.put(new ActionBuff(buff1, ID_Warcryer), new Timer());
		buffTimerMap.put(new ActionBuff(buff1, ID_Templeknight), new Timer());
		buffTimerMap.put(new ActionBuff(buff1, ID_Swordsinger), new Timer());
		buffTimerMap.put(new ActionBuff(buff1, ID_Bladedancer), new Timer());
		buffTimerMap.put(new ActionBuff(buff3, ID_Necromancer), new Timer()); 	//nuke
		buffTimerMap.put(new ActionBuff(buff4, ID_Warlock), new Timer());		//support protective
		buffTimerMap.put(new ActionBuff(buff4, this.id), new Timer());

		buffTimerMap.put(new ActionBuff(buff5, ID_Templeknight), new Timer());	//++shield
		buffTimerMap.put(new ActionBuff(buff5, ID_Swordsinger), new Timer());
		buffTimerMap.put(new ActionBuff(buff5, ID_Spoiler), new Timer());
		buffTimerMap.put(new ActionBuff(buff5, ID_Warlock), new Timer());

		int t = 0;
		for (PartyMember member : partyStack) {    //for all party members' pets
			buffTimerMap.put(new ActionBuff(buff2, t++), new Timer());
		}
		buffTimerMap.put(new ActionBuff(buff4, 10), new Timer());  //plus self pet
	}

	@Override
	public void classSpecificLifeCycle()
	{
		logger.trace(".classSpecificLifeCycle");

	}

	@Override
	public void onKill()
	{
		logger.warn("EE.onKill.. this normally should not happen");
	}

	public ElvenElder(WinDef.HWND hwnd)
	{
		super(GroupedVariables.ProjectConstants.ID_Warcryer, hwnd);
		setPartyMembers();
		setupBuffTimerMap();     //todo
		this.isHomeRunner = false;
		this.isSupport = true;
	}

	class ActionHeal extends Action    //not tested. unlikely to work
	{
		@Override
		public boolean equals(Object o)
		{
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			ActionHeal that = (ActionHeal) o;

			if (buttonNumber != that.buttonNumber) {
				return false;
			}
			if (targetID != that.targetID) {
				return false;
			}
			if (targetIsPet != that.targetIsPet) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode()
		{
			int result = targetID;
			result = 31 * result + buttonNumber;
			result = 31 * result + (targetIsPet ? 1 : 0);
			return result;
		}

		public final int
		targetID,
		buttonNumber;

		public final boolean
		targetIsPet;

		@Override
		public void perform()
		{
			logger.trace("ActionHeal.perform ");
			if (targetIsPet) {
				selectPartyMembersPetByID(this.targetID);
			} else {
				selectPartyMemberByID(this.targetID);
			}

			if (buttonNumber == 5) {    //grHeal
				ElvenElder.this.iAmHealing = true;

				while (ElvenElder.this.getPartyMemberHP(this.targetID, this.targetIsPet) < HEAL_TO) {    //ID here is party stack position!!!
					useSkill(ElvenElder.this.GreaterHeal);
				}

				ElvenElder.this.iAmHealing = false;

			} else if (buttonNumber == 6) {//majHeal
				ElvenElder.this.iAmHealing = true;

				while (ElvenElder.this.getPartyMemberHP(this.targetID, this.targetIsPet) < MAJOR_HEAL_TO) {    //ID here is party stack position!!!
					useSkill(ElvenElder.this.MajorHeal);
				}
			}
			ElvenElder.this.iAmHealing = false;
		}

		ActionHeal(int buttonNumber, int targetIdOrPartyMemberNumber, boolean targetIsPet)
		{
			super();
			this.isBuff = true;
			if (buttonNumber == 5) {    //grHeal
				this.priority = 370;
			} else if (buttonNumber == 6) {//majHeal
				this.priority = 400;
			} else {
				logger.warn("actionheal with button " + buttonNumber + ".. this should not happen");
			}
			this.buttonNumber = buttonNumber;
			this.targetID = targetIdOrPartyMemberNumber;
			this.targetIsPet = targetIsPet;

			logger.trace("Created ActionHeal. ID " + this.getID() + ", button Num_" + this.buttonNumber);
		}
	}


	public class ActionBuff extends ActionAbstractBuff
	{
		public final int
		targetID,
		buttonNumber,
		buffDelayMillis,
		macroDelayMillis;

		public final boolean
		targetIsPet;

		public final String buffName;

		@Override
		public void perform()
		{
			logger.trace("ActionBuff.perform " + this.buffName);
			if (targetIsPet) {
				if (targetID == 10) {    //my hen
					ElvenElder.this.l2Window.keyClick(KeyEvent.VK_DIVIDE);    //select hen, numpad'/'//watch it. not sure
				} else {
					selectPartyMembersPetByID(this.targetID);
				}
			} else if (this.targetID == ElvenElder.this.id) {    //selfbuff
				ElvenElder.this.l2Window.keyClick(KeyEvent.VK_NUMPAD0);//target self
			} else {
				selectPartyMemberByID(this.targetID);
			}

			ElvenElder.this.l2Window.keyClick(96 + this.buttonNumber);    //supposed 1,2,3 or 4
			ElvenElder.this.macroLocksActions(this.macroDelayMillis);    //locks any action because macro is executed
			if (ElvenElder.this.modeBuff) {
				logger.debug(".perform: making a hard task of adding new buff. watch it. watch it!");
				ElvenElder.this.buffTimerMap.get(this).schedule(new BuffTask(this), this.buffDelayMillis);
			}
		}


		@Override
		public boolean equals(Object o)
		{
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			ActionBuff that = (ActionBuff) o;

			if (buffDelayMillis != that.buffDelayMillis) {
				return false;
			}
			if (buttonNumber != that.buttonNumber) {
				return false;
			}
			if (macroDelayMillis != that.macroDelayMillis) {
				return false;
			}
			if (targetID != that.targetID) {
				return false;
			}
			if (targetIsPet != that.targetIsPet) {
				return false;
			}
			if (!buffName.equals(that.buffName)) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode()
		{
			int result = targetID;
			result = 31 * result + buttonNumber;
			result = 31 * result + buffDelayMillis;
			result = 31 * result + macroDelayMillis;
			result = 31 * result + (targetIsPet ? 1 : 0);
			result = 31 * result + buffName.hashCode();
			return result;
		}

		public ActionBuff(String buffName, int buttonNumber, int buffDelaySeconds, int macroDelaySeconds, int targetIdOrPartyMemberNumber, boolean targetIsPet)
		{
			super();
			this.isBuff = true;
			this.priority = 350;

			this.buffName = buffName;
			this.buttonNumber = buttonNumber;
			this.buffDelayMillis = buffDelaySeconds * 1000;
			this.macroDelayMillis = macroDelaySeconds * 1000;
			this.targetID = targetIdOrPartyMemberNumber;    //id for party member and number in partyStack for pet
			this.targetIsPet = targetIsPet;

			logger.trace("Created ActionBuff. ID " + this.getID() + ", button Num_" + this.buttonNumber + ", macro delay " + this.macroDelayMillis);
		}

		public ActionBuff(ActionBuff oldBuff, int targetID)                                        //TODO!!!!
		{
			super();
			this.isBuff = true;
			this.priority = oldBuff.getPriority();
			this.buffName = oldBuff.buffName;
			this.buttonNumber = oldBuff.buttonNumber;
			this.buffDelayMillis = oldBuff.buffDelayMillis;
			this.macroDelayMillis = oldBuff.macroDelayMillis;
			this.targetIsPet = oldBuff.targetIsPet;
			this.targetID = targetID;

			logger.trace("Created ActionBuff. ID " + this.getID() + ", button Num_" + this.buttonNumber + ", macro delay " + this.macroDelayMillis);
		}

		@Override
		public Action getNewCopy()
		{
			return new ActionBuff(this, this.targetID);
		}
	}


	protected class BuffTask extends TimerTask
	{

		private final ActionBuff addThisBuffHeal;

		@Override
		public void run()
		{
			logger.trace("BuffHealTask: adding ActionBuff: " + this.addThisBuffHeal + " to todolist");
			todoOffer(addThisBuffHeal);
			cancel();
		}

		BuffTask(ActionBuff specificBuff)
		{
			this.addThisBuffHeal = specificBuff;
		}
	}


	private class Heal extends Skill
	{
		@Override
		public void perform()
		{
			logger.trace("ElvenElder.Heal");
			ElvenElder.this.l2Window.keyClick(this.key);
			if (ElvenElder.this.iAmHealing) {
				easySleep(this.reuseTimeMillis);
			}
		}

		public Heal(int key, int reuseTimeSecs)
		{
			super(key, reuseTimeSecs);
		}

	}

	private int getPartyMemberHP(int partyStackPosition, boolean petFlag)
	{
		if (petFlag) {
			return this.partyStack.get(partyStackPosition).getHP("pet");
		} else {
			return this.partyStack.get(partyStackPosition).getHP();
		}
	}
}
