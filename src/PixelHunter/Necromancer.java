package PixelHunter;
import com.sun.jna.platform.win32.WinDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static PixelHunter.GroupedVariables.ProjectConstants.HEAL_FROM;
import static PixelHunter.GroupedVariables.ProjectConstants.ID_Necromancer;

/**
 * User: mrk
 * Date: 9/27/13; Time: 5:20 AM
 */
public class Necromancer extends Character
{
	private static final Logger logger = LoggerFactory.getLogger(Necromancer.class);

	private Skill
	cursedBone   = new Skill(1, 3),    //watch it
	vampiricClaw = new Skill(2, 6)                //watch it
	{
		@Override
		public void perform()
		{
			if (this.isReady) {
				logger.trace("inside overriden .perform of skill Vampiric Claw with gethp of " + Necromancer.this.getHP());
				if (Necromancer.this.getHP() < HEAL_FROM) {
					Necromancer.this.l2Window.keyClick(key);
					this.isReady = false;
					setReadySetTimer();
				} else {
					logger.warn("Tried to use vampiric Claw with hp less than sufficient. denied use");
					this.isReady = true;
				}
			} else {
				logger.debug("got skill .perform that is not ready yet");
			}
		}
	},
	gloom        = new Skill(3, 13),    //watch it
	chaos        = new Skill(4, 14),    //watch it
	weakness     = new Skill(5, 15),    //watch it
	forget       = new Skill(6, 6),    //watch it
	bodyToMind   = new Skill(7, 4);    //watch it

	private ActionSelfBuff sitFor20Seconds = new ActionSelfBuff("Sitting for 20 seconds macro", 8, 0, 25 * 1000);

	private TargetIsAliveCondition targetIsAliveCondition = new TargetIsAliveCondition();

	private List<SkillDealAction> hardAttackSkillDeals = new LinkedList<SkillDealAction>();
	private SkillDealAction       bodyToMindSkillDeal  = new SkillDealAction(bodyToMind, new HPIsHigherCondition(HEAL_FROM), 0);
	private SkillDealAction       cursedBoneSkillDeal  = new SkillDealAction(cursedBone, targetIsAliveCondition, 0);

	private Timer skillDealTimer = new Timer();


	private void activateHardAttack()
	{
		logger.trace(".activateHardAttack()..be warned.. be prepared");
		for (SkillDealAction skillDeal : this.hardAttackSkillDeals) {
			todoOffer(skillDeal);
		}
	}

	protected void message6(int callerID)
	{
		logger.trace(".message6 means Soft Assist Attack");
		todoOffer(cursedBoneSkillDeal);
	}

	@Override
	protected void onChampion()
	{
		logger.trace(".onChampion();");

		if (this.championCallIsAllowed) {
			this.l2Window.keyClick(KeyEvent.VK_F7);
			this.championCallIsAllowed = false;
			this.timerChampionCallIsAllowed.schedule(new SetChampionCallIsAllowedToTrue(), 10 * 1000);

		}
		if (!isFightingChampion) {
			activateHardAttack();
		}
		this.isFightingChampion = true;
	}

	@Override
	protected void message5(int callerID)
	{    //he saw a champ!	or anything
		logger.trace(".message5 which is equivalent to champion behaviour");
		selectPartyMemberByID(callerID);
		assistTarget();
		activateHardAttack();
	}

	@Override
	protected void attack()
	{
		logger.trace(".attack()");
		useSkill(cursedBone);
	}

	@Override
	protected void setupBuffTimerMap()
	{
		logger.trace(".setupBuffTimerMap. it's trivial. no useful information");
	}

	@Override
	public void classSpecificLifeCycle()
	{
		logger.trace(".classSpecificLifeCycle()");
		if (getMP() < 30 && !this.followFlag) {
			if (getHP() > HEAL_FROM) {
				todoOffer(bodyToMindSkillDeal);
			} else {
				todoOffer(this.sitFor20Seconds);
			}
		}
		if (this.modeFarm
			&&
			!this.followFlag
			&&
			!this.target.isDead()
			&&
			getMP() > 50
			&&
			getHP() < 90)
		{
			useSkill(vampiricClaw);
		}
	}

	@Override
	public void onKill()
	{

	}

	public Necromancer(WinDef.HWND hwnd)
	{
		super(ID_Necromancer, hwnd);
		logger.trace("Necromancer constructor");
		this.isNuker = true;
		this.isHomeRunner = true;
		this.petUseIsAllowed = false;

		this.hardAttackSkillDeals.add(new SkillDealAction(this.weakness, this.targetIsAliveCondition, 8));
		this.hardAttackSkillDeals.add(new SkillDealAction(this.forget, this.targetIsAliveCondition, 10));
		this.hardAttackSkillDeals.add(new SkillDealAction(this.chaos, this.targetIsAliveCondition, 6));
		this.hardAttackSkillDeals.add(new SkillDealAction(this.gloom, this.targetIsAliveCondition, 4));
		this.hardAttackSkillDeals.add(new SkillDealAction(this.cursedBone, this.targetIsAliveCondition, 2));
	}


//CLASSES


	protected class SkillDealAction extends Action
	{
		protected Skill              skill;
		protected SkillDealCondition condition;


		@Override
		public void perform()
		{
			logger.trace("skillDeal action with skill " + this.skill);
			if (condition.isSatisfied()) {
				useSkill(skill);
				Necromancer.this.skillDealTimer.schedule(new SkillDealTask(this), skill.reuseTimeMillis);
			} else {
				return;    //test it
			}
		}

		public SkillDealAction(Skill skill, SkillDealCondition condition, int subPriority)
		{
			super();
			this.skill = skill;
			this.condition = condition;
			this.priority = 250 + subPriority;
		}

		public SkillDealAction(SkillDealAction skillDeal)
		{
			super();
			this.skill = skillDeal.skill;
			this.condition = skillDeal.condition;
			this.priority = skillDeal.getPriority();
		}

	}


	protected class SkillDealTask extends TimerTask
	{

		protected SkillDealAction skillDeal;

		@Override
		public void run()
		{
			logger.trace("adding skillDeal to todoOffer");
			todoOffer(skillDeal);
			cancel();
		}

		public SkillDealTask(SkillDealAction skillDeal)
		{
			this.skillDeal = skillDeal;
		}
	}


	protected class HPIsHigherCondition extends SkillDealCondition
	{
		private int threshold;

		@Override
		public boolean isSatisfied()
		{
			if (Necromancer.this.getHP() > threshold) {
				logger.debug("HP is higher than" + threshold + " condition is satisfied");
				return true;
			}
			logger.debug("HP is higher than" + threshold + " condition is not satisfied");
			return false;
		}

		public HPIsHigherCondition(int threshold)
		{
			this.threshold = threshold;
		}
	}


	protected class TargetIsAliveCondition extends SkillDealCondition
	{
		@Override
		public boolean isSatisfied()
		{
			if (!Necromancer.this.target.isDead()) {
				logger.debug("Target is alive condition is satisfied");
				return true;
			}
			logger.debug("Target is alive condition is not satisfied");
			return false;
		}
	}


	protected abstract class SkillDealCondition    //functional programming would suit here best
	{
		public abstract boolean isSatisfied();
	}
}
