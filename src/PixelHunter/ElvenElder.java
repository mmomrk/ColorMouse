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
	buff5 = new ActionBuff("20-minute buff for my hen", 3, (20 * 60 - 30), 15, 0, true),
	buff6 = new ActionBuff("20-minute buff for shielded party members", 1, (20 * 60 - 30), 5, 0, false);


	private boolean iAmHealing    = false;
	private Skill    //comment
					serenadeOfEva = new Skill(8, 4), //watch it
	invocation                    = new Skill(9, 20),    //watch it. nobody knows it
	greaterHeal                   = new Heal(5, 2),    //watch it: 2 may not be true
	majorHeal                     = new Heal(6, 1);    //watch this too. needs to be verified


	public Character.ActionAbstractBuff cloneBuff(ElvenElder.ActionBuff oldSelfBuff)
	{
		return new ElvenElder.ActionBuff(oldSelfBuff, oldSelfBuff.targetID);
	}

	@Override
	protected void message5(int callerID)
	{
		logger.trace(".message5(); from " + callerID);
		selectPartyMemberByID(callerID);
		useSkill(majorHeal);
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

		buffTimerMap.put(new ActionBuff(buff3, ID_Necromancer), new Timer());     //nuke

		buffTimerMap.put(new ActionBuff(buff4, ID_Warlock), new Timer());        //support protective
		buffTimerMap.put(new ActionBuff(buff4, this.id), new Timer());

		buffTimerMap.put(new ActionBuff(buff5, this.id), new Timer());       //for the chicken hen

		buffTimerMap.put(new ActionBuff(buff6, ID_Templeknight), new Timer());    //++shield
		buffTimerMap.put(new ActionBuff(buff6, ID_Swordsinger), new Timer());
		buffTimerMap.put(new ActionBuff(buff6, ID_Spoiler), new Timer());
		buffTimerMap.put(new ActionBuff(buff6, ID_Warlock), new Timer());

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
		int
		maxHP = 0,
		currentTotalHP = 0;

		for (PartyMember partyMember : partyStack) {

			int charHP = partyMember.getHP();
			ActionHealPartyMemberFromStack supposedHeal;

			maxHP += 100;
			currentTotalHP += charHP;

			if (charHP < GroupedVariables.ProjectConstants.MAJOR_HEAL_FROM) {
				supposedHeal = new ActionHealPartyMemberFromStack(6, partyMember, false);
				supposedHeal.increasePriority(100 - charHP);        //less hp=>higher priority
				todoOffer(supposedHeal);
			} else if (charHP < GroupedVariables.ProjectConstants.HEAL_FROM) {
				supposedHeal = new ActionHealPartyMemberFromStack(5, partyMember, false);
				supposedHeal.increasePriority(100 - charHP);
				todoOffer(supposedHeal);
			}

			if (!partyMember.isSingle) {

				int petHP = partyMember.getHP("pet");

				maxHP += 100;
				currentTotalHP += petHP;

				if (petHP < GroupedVariables.ProjectConstants.MAJOR_HEAL_FROM) {
					supposedHeal = new ActionHealPartyMemberFromStack(6, partyMember, true);
					supposedHeal.increasePriority(100 - petHP);        //less hp=>higher priority
					todoOffer(supposedHeal);
				} else if (petHP < GroupedVariables.ProjectConstants.HEAL_FROM) {
					supposedHeal = new ActionHealPartyMemberFromStack(5, partyMember, true);
					supposedHeal.increasePriority(100 - petHP);
					todoOffer(supposedHeal);
				}
			}

		}
		if (maxHP * 0.7 > currentTotalHP) {          //watch it
			this.l2Window.keyClick(KeyEvent.VK_MULTIPLY);//panic   .
		}

	}

	@Override
	public void onKill()
	{
		logger.trace(".onKill();..empty<-EE");
	}

	public ElvenElder(WinDef.HWND hwnd)
	{
		super(GroupedVariables.ProjectConstants.ID_Warcryer, hwnd);
		setPartyMembers();
		setupBuffTimerMap();
		this.isHomeRunner = false;
		this.isSupport = true;
	}

	/*should work only with party hp check.
	 no action heal from chat.
	 extends to member's pet with proper flag*/
	class ActionHealPartyMemberFromStack extends Action    //not tested. unlikely to work
	{
		public final int
		buttonNumber;

		public final boolean
		targetIsPet;

		public final PartyMember partyMember;

		@Override
		public void perform()
		{
			logger.trace("ActionHealPartyMemberFromStack.perform ");
			if (targetIsPet) {
				selectClickPartyMembersPetByPartyStackPlace(this.partyMember);
			} else {
				selectClickPartyMemberByPartyStackPlace(this.partyMember);
			}

			if (buttonNumber == 5) {    //grHeal
				ElvenElder.this.iAmHealing = true;

				while (ElvenElder.this.getPartyMemberHP(this.partyMember, this.targetIsPet) < HEAL_TO) {    //ID here is party stack position!!!
					useSkill(ElvenElder.this.greaterHeal);
				}

				ElvenElder.this.iAmHealing = false;

			} else if (buttonNumber == 6) {//majHeal
				ElvenElder.this.iAmHealing = true;

				while (ElvenElder.this.getPartyMemberHP(this.partyMember, this.targetIsPet) < MAJOR_HEAL_TO) {    //ID here is party stack position!!!
					useSkill(ElvenElder.this.majorHeal);
				}
			}
			ElvenElder.this.iAmHealing = false;
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

			ActionHealPartyMemberFromStack that = (ActionHealPartyMemberFromStack) o;

			if (buttonNumber != that.buttonNumber) {
				return false;
			}
			if (targetIsPet != that.targetIsPet) {
				return false;
			}
			if (!partyMember.equals(that.partyMember)) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode()
		{
			int result = buttonNumber;
			result = 31 * result + (targetIsPet ? 1 : 0);
			result = 31 * result + partyMember.hashCode();
			return result;
		}

		ActionHealPartyMemberFromStack(int buttonNumber, PartyMember partyMember, boolean targetIsPet)
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
			this.partyMember = partyMember;
			this.targetIsPet = targetIsPet;
			logger.trace("Created ActionHealPartyMemberFromStack. ID " + this.getID() + ", button Num_" + this.buttonNumber);
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

				if (this.targetID == ElvenElder.this.id) {
					selectClickPartyMembersPetByPartyStackPlace(10);
				} else {
					selectClickPartyMembersPetByPartyStackPlace(this.targetID);
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

}
