package PixelHunter;
import com.sun.jna.platform.win32.WinDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

import static PixelHunter.GroupedVariables.ProjectConstants.HEAL_TO;
import static PixelHunter.GroupedVariables.ProjectConstants.MAJOR_HEAL_TO;

/**
 * User: mrk
 * Date: 9/21/13; Time: 4:40 AM
 *
 * by now party heal system will freeze if he can't heal the member
 *
 * !!!!!!!!!!!!!!!!!!!!!!!have to think of selecting party members pets and buffing different buffs on them. Watch Party member class. needs to be done
 */
public class ElvenElder extends PixelHunter.Character    //todo
{
	private static final Logger logger = LoggerFactory.getLogger(ElvenElder.class);

	private final ActionSelfBuff    //second parameter is numpad key number
						  buff1       = new ActionSelfBuff("20-minute buff", 1, (20 * 60 - 15) * 1000, 20 * 1000);
	private       boolean iAmHealing  = false;
	private       Skill
						  GreaterHeal = new Heal(5, 2),    //watch it: 2 may not be true
	MajorHeal = new Heal(6, 1);    //watch this too. needs to be verified

	private List<PartyMember> partyStack = new LinkedList<PartyMember>();

	private void setPartyMembers()
	{
		logger.trace("setPartyMembers();");
		PartyMember member = new PartyMember(this.l2Window);

	}

	@Override
	protected void message5(int callerID)
	{

	}

	@Override
	protected void setupBuffTimerMap()    //not tested
	{
		logger.trace(".setupBuffTimerMap");

	}

	@Override
	public void classSpecificLifeCycle()
	{
		logger.trace(".classSpecificLifeCycle");

	}

	@Override
	public void onKill()
	{

	}

	public ElvenElder(WinDef.HWND hwnd)
	{
		super(GroupedVariables.ProjectConstants.ID_Warcryer, hwnd);
		setPartyMembers();
		setupBuffTimerMap();
		isHomeRunner = false;
	}

	class ActionBuffHeal extends Action
	{
		public final int
		targetID,
		buttonNumber,
		buffDelayMillis,
		macroDelayMillis;
		public final String buffName;

		@Override
		public void perform()
		{
			logger.trace("ActionBuffHeal.perform " + this.buffName);
			selectPartyMemberByID(this.targetID);

			if (buttonNumber == 5) {    //grHeal
				ElvenElder.this.iAmHealing = true;

				while (ElvenElder.this.getPartyMemberHP(this.targetID) < HEAL_TO) {    //ID here is party stack position!!!
					useSkill(ElvenElder.this.GreaterHeal);
				}

				ElvenElder.this.iAmHealing = false;
			} else if (buttonNumber == 6) {//majHeal
				ElvenElder.this.iAmHealing = true;

				while (ElvenElder.this.getPartyMemberHP(this.targetID) < MAJOR_HEAL_TO) {    //ID here is party stack position!!!
					useSkill(ElvenElder.this.MajorHeal);
				}

				ElvenElder.this.iAmHealing = false;
			} else {    //ordinary buff
				ElvenElder.this.l2Window.keyClick(96 + this.buttonNumber);	//1,2,3 or 4
				ElvenElder.this.macroLocksActions(this.macroDelayMillis);    //locks any action because macro is executed
				if (ElvenElder.this.modeBuff) {
					logger.debug(".perform: making a hard task of adding new buff. watch it. watch it!");
					ElvenElder.this.buffTimerMap.get(this).schedule(new BuffHealTask(this), this.buffDelayMillis);
				}
			}

		}

		ActionBuffHeal(String buffName, int buttonNumber, int buffDelaySeconds, int macroDelayMillis, int targetIdOrPartyMemberNumber)
		{
			super();
			this.isBuff = true;
			if (buttonNumber == 5) {    //grHeal
				this.priority = 370;
			} else if (buttonNumber == 6) {//majHeal
				this.priority = 400;
			} else {    //ordinary buff
				this.priority = 350;
			}

			this.buffName = buffName;
			this.buttonNumber = buttonNumber;
			this.buffDelayMillis = buffDelaySeconds * 1000;
			this.macroDelayMillis = macroDelayMillis;
			this.targetID = targetIdOrPartyMemberNumber;

			logger.trace("Created ActionBuffHeal. ID " + this.getID() + ", button Num_" + this.buttonNumber + ", macro delay " + this.macroDelayMillis);
		}

		ActionBuffHeal(ActionBuffHeal oldBuff, int targetID)                                        //TODO!!!!
		{
			super();
			this.isBuff = true;
			this.priority = oldBuff.getPriority();
			this.buffName = oldBuff.buffName;
			this.buttonNumber = oldBuff.buttonNumber;
			this.buffDelayMillis = oldBuff.buffDelayMillis;
			this.macroDelayMillis = oldBuff.macroDelayMillis;
			this.targetID = targetID;

			logger.trace("Created ActionBuffHeal. ID " + this.getID() + ", button Num_" + this.buttonNumber + ", macro delay " + this.macroDelayMillis);
		}
	}

	protected class BuffHealTask extends TimerTask
	{

		private final ActionBuffHeal addThisBuffHeal;

		@Override
		public void run()
		{
			logger.trace("BuffHealTask: adding ActionBuffHeal: " + this.addThisBuffHeal + " to todolist");
			todoOffer(addThisBuffHeal);
			cancel();
		}

		BuffHealTask(ActionBuffHeal specificBuff)
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

	private int getPartyMemberHP(int partyStackPosition)
	{
		//todo

	}
}
