package PixelHunter;


import com.sun.jna.platform.win32.WinDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

import static PixelHunter.GroupedVariables.*;
import static PixelHunter.GroupedVariables.ProjectConstants.*;
import static java.lang.Math.pow;

/**
 * User: mrk
 * Date: 9/1/13; Time: 1:17 PM
 * <p/>
 * manor mode is not implemented
 */
public abstract class Character extends LivingCreature
{

	private static final Logger logger = LoggerFactory.getLogger(Character.class);

	private static final int minChatStartExpectation = 50;
	private static final int maxChatStartExpectation = 130;

	private static final int[] homesToRun        = {
												   ID_Elvenelder,
												   ID_Swordsinger,
												   ID_Bladedancer};
	private              int   homerunQueueDepth = 3;


	protected boolean
	isSupport        = false,                //no attacks from chat messages
	isSummoner       = false,        //only pet attacks from chat messages
	isBDSWS          = false,        //funny buffs
	isTank           = false,        //comments?
	isPhysicAttacker = false,    //everything else
	isNuker          = false,    //almost everything
	isHomeRunner     = true,

	modeFarm    = false,
	modeBuff    = false,
	modeHomeRun = false,     //is used, bit not tested
	modeManor   = false,        //not yet implemented
	modeRB      = false,    //lol. should only activate some additional skilluses in classspecific

	followFlag  = false,
	isMacroFree = true,

	targetWasAlive  = false,
	petUseIsAllowed = true;


	private Point chatStartingPoint;

	protected Pet         pet;    //remove public after tests are done
	protected Target      target;
	protected HpConstants hpConstants;
	protected HpConstants mpConstants;
	protected List<PartyMember> partyStack = new LinkedList<PartyMember>();

	private static Action.ActionPriorityComparator actionComparator = new PixelHunter.Action.ActionPriorityComparator();
	private        PriorityQueue<Action>           toDoList         = new PriorityQueue<Action>(ProjectConstants.CHAT_TASK_LIST_LENGTH, actionComparator);

	protected final Timer        //discuss think of those protected after chars are done
	macroLockTimer,
	timerPvEAdd,
	timerHomeRunAdd,
	timerPetUseIsAllowed;

	protected Map<ActionAbstractBuff, Timer> buffTimerMap = new HashMap<ActionAbstractBuff, Timer>();

	protected int homeRunNumber = 0;
	protected int homeRunDelay  = ProjectConstants.HOMERUN_TIME * 1000;    //can be set individually btw
	private long iStartedToKillTargetAt;


//	public void classSpecificDeed();//think of it twice

//	public void setMp();
//	public void setPetHp();
//	public void initializeWindow();    //in case of falling down of the prev window

//	public void follow(int id);                                 y

	protected void cancelAllBuffScheduledTasks()
	{
		logger.trace("cancelAllBuffScheduledTasks");
		for (Map.Entry<ActionAbstractBuff, Timer> buffTimerEntry : this.buffTimerMap.entrySet()) {
			buffTimerEntry.getValue().cancel();
		}
		this.buffTimerMap.clear();
		setupBuffTimerMap();

	}

	protected abstract void setupBuffTimerMap();

	public abstract void classSpecificLifeCycle();

	public abstract void onKill();


	protected void everyonesOnKill()
	{
		logger.trace(".everyonesOnKill");
		this.targetWasAlive = false;
		onKill();    //each has it overriden
	}

	protected boolean iThinkIAmFacingAChampion()
	{
		logger.trace(".iThinkIAmFacingAChampion()");
		if (System.currentTimeMillis() - this.iStartedToKillTargetAt > ProjectConstants.CHAMPION_SUSPICION_TIME_SECONDS * 1000) {
			if (this.targetWasAlive) {
				return true;
			}
		}
		return false;
	}

	public void nowWeWillSingDance()
	{
		logger.warn(".nowWeWillSingDance --default character's. this should not be");

	}

	public boolean nextSongDance()
	{
		logger.warn("inside characters nestSongDance. this should not be!!!");
		return true;
	}

	protected void deselect()
	{
		logger.trace(".deselect();");
		this.l2Window.keyClick(KeyEvent.VK_ESCAPE);
	}

	protected void setPartyMembers()    //not tested
	{
		logger.trace("setPartyMembers();");
		PartyMember member = new PartyMember(this.l2Window);
		this.partyStack.add(member);
		while (member.nextExists()) {
			member = new PartyMember(member);
			this.partyStack.add(member);
		}

	}

	protected static void easySleep(int timeMillis)
	{
		try {
			Thread.sleep(timeMillis);
		} catch (InterruptedException e) {
			logger.error("Exceprion while sleeping");
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	protected void useSkill(Skill skill)
	{
		skill.perform();
	}

	public void lifeCycle()
	{
		logger.trace("Character's " + this.id + " lifecycle");
		this.l2Window.activate();
		boolean targetIsDeadInThisLifecycle = this.target.isDead();
		if (this.l2Window.isChatMode()) {
			readChatAndOfferToDo();
		}
		//checkToDoSanity()	//hope i dont need this
		if (this.isMacroFree) {
			if (this.targetWasAlive && targetIsDeadInThisLifecycle) {
				everyonesOnKill();    //also checks for isDead
			}
		}
		doTheToDo();

		if (this.isMacroFree) {
			classSpecificLifeCycle();
		}

		if (this.isMacroFree) {
			if (this.modeFarm) {
				if (targetIsDeadInThisLifecycle) {        //i am farming and i see a dead target after i've done onkill
					todoOffer(new ActionPvE());
				} else {
					if (!this.isSupport) {        //no killing for supports. only attackers attack
						attack();
					} else {
						logger.warn("for some reason support has non-zero hp target.maybe it's ok");
					}
					if (iThinkIAmFacingAChampion()) {
						onChampion();
					}
				}
			}
		}

		if (!targetIsDeadInThisLifecycle) {        //targ is alive
			if (!this.targetWasAlive) {      //and targ was not alive
				this.iStartedToKillTargetAt = System.currentTimeMillis();
			}
			this.targetWasAlive = true;
		}
	}

	protected void onChampion()    //overriden for necr
	{
		logger.trace(".onChampion();");
		if (this.isTank) {
			message5(this.id);
		}
		if (this.isSupport) {
			logger.warn("support is facing a champion. things are pretty bad");
			return;
		}
		this.l2Window.keyClick(KeyEvent.VK_F7);

	}

	/*checks for allowness of tod o*/
	private void doTheToDo()
	{
		logger.trace(".doTheToDo");
		logger.debug(".doTheToDo: todolist: " + this.toDoList);
		if ((this.target.isDead()
			 ||
			 this.target.getHP() >= 100)
			&&
			!toDoList.isEmpty()
			&&
			this.isMacroFree)
		{
			toDoList.poll().perform();
		}
	}

	private void readChatAndOfferToDo()
	{
		logger.trace(".redChatAndOfferToDo");
		ChatMessage freshMessage = this.getChatMessage();
		logger.debug(".redChatAndOfferToDo got " + freshMessage);
		if (freshMessage != null) {
			todoOffer(new ActionChatCommand(freshMessage));
			spamSelf();
		}
	}

	public void forceRebuff()
	{
		logger.trace(".forceRebuff");
		cancelAllBuffScheduledTasks();
		for (Map.Entry<ActionAbstractBuff, Timer> buffTimerEntry : this.buffTimerMap.entrySet()) {
			todoOffer(buffTimerEntry.getKey().getNewCopy());
//			todoOffer(new ActionAbstractBuff(buffTimerEntry.getKey()));    //watch for proper todo offers in all cases. not this->todoOffer(currentBuff);
		}
	}

	protected void todoOffer(Action action)
	{
		logger.trace(".todoOffer");
		if (!toDoList.contains(action)) {
			logger.debug(".todoOffer adding " + action);
			toDoList.offer(action);
		} else {
			logger.warn(".todoOffer todolist contains this action. this should not be");
		}
		return;
	}

	public void deactivateModeFarm()
	{
		logger.trace(".deactivateModeFarm");
		this.modeFarm = false;
		this.timerPvEAdd.cancel();
	}

	public void activateModeFarm()
	{
		logger.trace(".activateModeFarm");
		this.modeFarm = true;
		this.todoOffer(new ActionPvE());
	}

	public void deactivateModeBuff()                    //not tested
	{
		logger.trace(".deactivateModeBuff");
		this.modeBuff = false;
		for (Map.Entry<ActionAbstractBuff, Timer> buffTimerEntry : this.buffTimerMap.entrySet()) {
			buffTimerEntry.getValue().cancel();

		}
	}

	public void activateModeBuff()
	{
		logger.trace(".activateModeBuff");
		this.modeBuff = true;
		forceRebuff();
	}

	public void deactivateModeHomeRun()    //todo on all deactiates: cancel all timers
	{
		logger.trace(".deactivateModeHomeRun");
		this.modeHomeRun = false;
		this.timerHomeRunAdd.cancel();
	}

	public void activateModeHomeRun()
	{
		logger.trace(".activateModeHomeRun");
		this.modeHomeRun = true;
		this.todoOffer(new ActionHomeRun());
	}

	public void spamSelf()
	{
		logger.trace(".spamSelf");
		l2Window.keyClick(KeyEvent.VK_0);
	}

	protected void macroLocksActions(int milliseconds)
	{
		logger.trace(".macroLocksActions for " + milliseconds + " millis");
		isMacroFree = false;
		macroLockTimer.schedule(new SetMacroFree(), milliseconds);
	}

	private boolean chatPatternMatches(int i, boolean equalsExpectedColor)
	{
		logger.trace(".chatPatternMatches with pixel number " + i + " and matching green color of " + equalsExpectedColor);
//		logger.debug("first condition is " + (i % 5 < 3 && equalsExpectedColor) + " and second is " + (i % 5 >= 3 && !equalsExpectedColor));
		if ((i % 5 < 3 && equalsExpectedColor) || (i % 5 >= 3 && !equalsExpectedColor)) {    //is it magic? not yet. look int your chat
			return true;
		} else {
			logger.warn("Chat pattern match has failed. Generally this should not happen");
			return false;
		}
	}

	private ChatMessage getChatMessage()
	{
		logger.trace(".getChatMessage");
		Color messageColor = Color.CYAN;    //compiler doesn't know this is not necessary
		int senderSignature, receivedCode = 0, modeColor = 0;
		int i;
		Point currentPoint = new Point(0, this.chatStartingPoint.y);//lower command pixel is 3 px under ':'
		Color currentColor;
		logger.debug("now searching first pixel in signature");
		for (i = maxChatStartExpectation; i >= minChatStartExpectation; i -= 3) {
			currentPoint.x = i;
			currentColor = l2Window.getRelPixelColor(currentPoint);
			if (l2Window.colorsAreClose(currentColor, ProjectConstants.CHAT_COLOR_PARTY)) {
				messageColor = ProjectConstants.CHAT_COLOR_PARTY;
				modeColor = 0;
				break;
			}
			if (l2Window.colorsAreClose(currentColor, ProjectConstants.CHAT_COLOR_PRIVATE)) {
				messageColor = ProjectConstants.CHAT_COLOR_PRIVATE;
				modeColor = 1;
				break;
			}
		}
		if (i > this.maxChatStartExpectation
			||
			i < this.minChatStartExpectation)
		{
			logger.info("Chat is empty");
			return null;
		} else {
			do {
				currentPoint.x--;
			}
			while (L2Window.colorsAreClose(l2Window.getRelPixelColor(currentPoint), messageColor));
			currentPoint.x++;
			//now we are at first pixel of lower horizontal thingy
			do {
				currentPoint.x -= 5;
			}
			while (L2Window.colorsAreClose(l2Window.getRelPixelColor(currentPoint), messageColor));
			currentPoint.x += 5;
			//now we are at first pixel of the first pseudo-bit. congrats?
		}
		//probably found first pixel

		//now check the pattern
		int startingX = currentPoint.x;
		for (i = 15; i >= 0; i--) {    //bad things fail faster
			currentPoint.x = startingX + i;
			if (!chatPatternMatches(i, l2Window.colorsAreClose(l2Window.getRelPixelColor(currentPoint), messageColor))) {
				return null;
			}
		}
		currentPoint.x = startingX;         //half an hour is gone
		//now we totally have in incoming
		//now we have a correct pattern, but not sure if it is the real first command bit
		do {
			currentPoint.x -= 5;
		} while (l2Window.colorsAreClose(l2Window.getRelPixelColor(currentPoint), messageColor));

		currentPoint.x += 5;

		senderSignature = currentPoint.x;    //foolproof

		logger.debug("found signature " + senderSignature + ", found mod " + modeColor);


		currentPoint.y--;    //to be in the information zone
		// decoding
		for (i = 0; i <= 5; i++) {//command binary capacity sits in this hardcoded 5, means n+1 bits
			currentPoint.x = senderSignature + i * 5;
			if (!l2Window.colorsAreClose(l2Window.getRelPixelColor(currentPoint), messageColor)) {//found bit true
				if (l2Window.colorsAreClose(l2Window.getRelPixelColor(new Point(currentPoint.x + 2, currentPoint.y)), messageColor)) {
					receivedCode += pow(2, i);
				} else {
					logger.warn(".ReadChat: Warning while decoding message: both left and right pivot points are empty. this should not be");
					break;
				}
			}
			logger.debug(".getChatMessage: decode. current val is " + receivedCode);
		}
		logger.info(".getChatMessage: Received chat command number " + receivedCode + " from sig " + senderSignature);
		return new ChatMessage(receivedCode, senderSignature, modeColor);
	}

	public void setChat()
	{
		this.chatStartingPoint = l2Window.setChat();
		logger.info("Finished setChat. Now chatStartingPoint is " + this.chatStartingPoint);
	}

	public void setHP()    //not tested
	{
		logger.trace(".setHP(); for character");

		this.hpConstants = new HpConstants(ProjectConstants.CHARACTER_HP_COLOR, new Point(0, 0), new Point(0, 0), this.id);
		this.mpConstants = new HpConstants(ProjectConstants.CHARACTER_MP_COLOR, new Point(0, 0), new Point(0, 0), this.id);
		this.l2Window.setCharacterHP(hpConstants, mpConstants);
	}

	public int getHP()    //not tested
	{
		return this.l2Window.getCharacterHPMP(this.hpConstants, true);
	}

	protected int getMP()//test it
	{
		return this.l2Window.getCharacterHPMP(this.mpConstants, false);
	}

	public String toString()
	{
		return "Character. ID=" + this.id + ". Window " + l2Window.toString();
	}

	protected int getPartyMemberHP(PartyMember partyMember, boolean petFlag)
	{
		if (petFlag) {
			return partyMember.getHP("pet");
		} else {
			return partyMember.getHP();
		}
	}

	/*
	*   works nice for positive as expected.
	*   for negative: abs is number in party stack
	* */
	protected void selectClickPartyMembersPetByPartyStackPlace(int id)    //10 is reserved for self pet.
	{
		logger.trace(".selectPartyMembers__PET__ByID: " + id);
		if (id != 10) {
			this.l2Window.mouseClick_Relative(this.partyStack.get(id).petHpConstants.coordinateLeft);//not tested
		} else {
			if (this.isSupport) {
				this.l2Window.keyClick(KeyEvent.VK_DIVIDE);    //'/' is for the pet(num11)
			} else {
				logger.warn("non-support wanted to select its pet. this should not be!!");
			}
		}
	}

	protected void selectClickPartyMemberByPartyStackPlace(int id)    //10 is reserved for self.
	{
		logger.trace(".selectPartyMembers__PET__ByID: " + id);
		if (id != 10) {
			this.l2Window.mouseClick_Relative(this.partyStack.get(id).petHpConstants.coordinateLeft);//not tested
		} else {
			if (this.isSupport) {
				this.l2Window.keyClick(KeyEvent.VK_NUMPAD0);
			} else {
				logger.warn("non-support wanted to select self. this should not be!!");
			}
		}
	}

	protected void selectClickPartyMembersPetByPartyStackPlace(PartyMember partyMember)
	{
		logger.trace("selectClickPartyMembers__Pet__ByPartyStackPlace");
		this.l2Window.mouseClick_Relative(partyMember.petHpConstants.coordinateLeft);//not tested
	}

	protected void selectClickPartyMemberByPartyStackPlace(PartyMember partyMember)
	{
		logger.trace(".selectClickPartyMemberByPartyStackPlace(); --partyMember access");
		this.l2Window.mouseClick_Relative(partyMember.hpConstants.coordinateLeft);//not tested
	}


	protected void selectPartyMemberByID(int id)
	{
		if (id != this.id) {
			logger.trace(".selectPartyMemberByID: " + id);
			this.l2Window.keyClick(48 + ProjectConstants.partyPanelMatch.get(id));    //VK_0 is 48
			World.easySleep(300);
		} else {
			logger.warn("attempt to select self through selectPartyMemberByID: id "+id);
		}

	}

	protected void attackWithoutPet()
	{
		logger.trace(".attackWithoutPet");
		this.l2Window.keyClick(KeyEvent.VK_F8);
	}

	protected void assistTarget()
	{
		logger.trace(".assistTarget");
		this.l2Window.keyClick(48 + ProjectConstants.partyPanelMatch.get(this.id));    //assi button in the right spot
	}

	protected void attack()        //is overriden for necr
	{
		logger.trace(".attack");
		if (Mediator.noPetMode || !this.petUseIsAllowed) {
			logger.trace(".attack without pet redirect");
			attackWithoutPet();
		}
		if (this.isSupport) {
			return;
		}
		if (this.isSummoner) {
			petAttack();
		}
		this.l2Window.keyClick(KeyEvent.VK_F1);
	}

	protected void petAttack()
	{
		if (!Mediator.noPetMode && this.petUseIsAllowed) {
			logger.trace(".petAttack");
			this.l2Window.keyClick(KeyEvent.VK_F2);
		}

	}

	protected void petStop()
	{
		logger.trace(".petStop");
		this.l2Window.keyClick(KeyEvent.VK_F3);
	}

	protected void petFollow()
	{
		logger.trace(".petFollow");
		this.l2Window.keyClick(KeyEvent.VK_F4);
	}

	protected void message6(int callerID)
	{
		logger.trace("message6();. the empty one");
	}

	protected void message5(int callerID)
	{
		logger.trace("message5();. the empty one");
	}

	protected void championCallReaction(int callerID)
	{
		logger.trace(".championCallReaction();--default");
		if (this.isSupport) {    //sad but true
			return;
		}
		selectPartyMemberByID(callerID);
		assistTarget();
		if (!this.isHomeRunner) {        //can't move. go and help him
			petAttack();
			this.petUseIsAllowed = false;
			this.timerPetUseIsAllowed.schedule(new SetPetUseIsAllowedToTrue(), 30 * 1000);
			deselect();
			return;
		}
		if (this.isTank) {
			message5(callerID);    //active aggro-mode on his target by assist
			return;
		}
		if (this.id == ProjectConstants.ID_Spoiler) {
			message5(callerID);    //spoilMyChamp
			return;
		}
		if (this.isNuker) {
			message5(callerID);
			return;
		}

		attack();    //totally running criminal

	}

	protected void toggleBuffMode()
	{
		this.modeBuff = !this.modeBuff;
		logger.info(".toggleBuffMode. Now buffmode is" + this.modeBuff);
		forceRebuff();
	}


	public Character(int hisID, WinDef.HWND hwnd)        //windownumber can only be 1 or 0
	{
		super(hisID);
		logger.trace("Inside Character constructor, finished making LC");

		this.l2Window = new L2Window(hwnd);

		l2Window.acceptWindowPosition();
		logger.debug(".Character after acceptWindowPos. now it is h " + l2Window.h + " w " + l2Window.w + " top-left " + l2Window.windowPosition);
		if (!Mediator.noPetMode) {
			pet = new Pet(l2Window);//including setHP
		}
		target = new Target(l2Window);

		setHP();

		setChat();

		//initing all tough structures
		logger.trace(".Character: initializing timers ");
		this.macroLockTimer = new Timer();
		this.timerHomeRunAdd = new Timer();
		this.timerPvEAdd = new Timer();
		this.timerPetUseIsAllowed = new Timer();
	}


	//CLASSES


	public abstract class ActionAbstractBuff extends Action
	{
		public abstract Action getNewCopy();
	}


	protected class Skill extends Action
	{

		protected final int key;
		public final    int reuseTimeMillis;

		protected boolean isReady = true;

		protected Timer readySetTimer = new Timer();

		protected void setReadySetTimer()    //allows execution after reuseTime timeout
		{
			this.readySetTimer.schedule
							   (new TimerTask()
							   {
								   @Override
								   public void run()
								   {
									   Skill.this.isReady = true;
									   cancel();
								   }
							   }, this.reuseTimeMillis);
		}

		@Override
		public void perform()
		{
			if (this.isReady) {
				logger.trace(".perform();");
				Character.this.l2Window.keyClick(key);
				this.isReady = false;
				setReadySetTimer();
			} else {
				logger.debug("got skill .perform that is not ready yet");
			}

		}

		public Skill(int keyInNumpadLine, int reuseTimeSecs)
		{
			super();
			this.isSkill = true;
//			this.priority	=	//skill has no priority. it is not to be used in queue
			this.key = KeyEvent.VK_NUMPAD0 + keyInNumpadLine;
			this.reuseTimeMillis = reuseTimeSecs * 1000;

		}

	}


	protected class ActionHomeRun extends Action    //todo!!!
	{

		@Override
		public void perform()
		{
			if (!Character.this.isHomeRunner) {
				return;
			}
			logger.trace("ActionHomeRun.perform");

			int homeToRunTo = Character.this.homeRunNumber++ % Character.homesToRun.length;

			Character.this.selectPartyMemberByID(Character.homesToRun[homeToRunTo]);        //test this
			Character.this.selectPartyMemberByID(Character.homesToRun[homeToRunTo]);
			if (Character.this.isHomeRunner) {
				timerHomeRunAdd.schedule(new HomeRunTask(), Character.this.homeRunDelay);
			}
		}

		ActionHomeRun()
		{
			super();
			this.isHomeRun = true;
			this.priority = 200;
			logger.trace("Created ActionHomeRun. ID " + this.getID());
		}
	}


	/**
	 * User: mrk
	 * Date: 9/14/13; Time: 6:35 AM
	 */
	protected class ActionChatCommand extends Action
	{
		public final ChatMessage message;

		@Override
		public boolean equals(Object o)
		{
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			ActionChatCommand that = (ActionChatCommand) o;

			if (!message.equals(that.message)) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode()
		{
			return message.hashCode();
		}

		@Override
		public void perform()
		{
			logger.trace("Action Chat.performing this: " + this.message);
			if (this.message.getSenderID() == id) {    //this living creature id
				logger.debug("Action chat: not executing my commands");
				return;
			}
			if (Character.this.followFlag && this.message.getCommand() != 1) {    //any command resets follow mode
				Character.this.followFlag = false;
			}

			switch (this.message.getCommand()) {
				case 0:
					logger.warn("command 0. Normally this should not be");
					break;
				case 1:    //follow
					selectPartyMemberByID(this.message.getSenderID());
					easySleep(500);
					selectPartyMemberByID(this.message.getSenderID());
					Character.this.modeFarm = false;
					Character.this.modeBuff = false;
					Character.this.modeHomeRun = false;
					Character.this.followFlag = true;
					break;
				case 2:    //assi
					selectPartyMemberByID(this.message.getSenderID());
					assistTarget();
					break;
				case 3:    //assi atta
					if (Character.this.isSupport) {
						break;
					}
					selectPartyMemberByID(this.message.getSenderID());
					assistTarget();
					if (Character.this.isSummoner) {
						petAttack();
						break;
					}
					if (Character.this.isNuker) {
						message6(this.message.getSenderID());
						break;
					}
					attack();

					break;
				case 4:    //attack current target --use for peace purpose only
					attack();
					break;
				case 5:    //class-specific, reserved
					message5(message.getSenderID());
					break;
				case 6:    //reserved
					message6(message.getSenderID());
					break;
				case 7:    //pet assist atta
					selectPartyMemberByID(this.message.getSenderID());
					assistTarget();
					petAttack();
					break;
				case 8:    //pet stop
					petStop();
					break;
				case 9:    //pet follow
					petFollow();
					break;
				case 10:        //toggle buff
					toggleBuffMode();
					break;
				case 11:        //activate mode PvE
					Character.this.activateModeFarm();
					Character.this.activateModeHomeRun();
					break;
				case 12:        //deactivate HR
					Character.this.deactivateModeHomeRun();
					break;
				case 13:        //call for help.. should be different from assi atta
					if (Math.random() > 0.7) {    //assi atta
						Character.this.selectPartyMemberByID(message.getSenderID());
						Character.this.assistTarget();
						Character.this.attack();
					} else {
						Character.this.selectPartyMemberByID(message.getSenderID());
						Character.this.selectPartyMemberByID(message.getSenderID());
						Character.this.easySleep(4000);
						new ActionPvE().perform();
					}
					break;
				case 14:    //champion Detected
					championCallReaction(message.getSenderID());
					break;
				case 15:    //trust me and talk to this npc
//					selectPartyMemberByID(this.message.getSenderID());
					assistTarget();
					Character.this.l2Window.keyClick(KeyEvent.VK_MINUS);    //maybe to separate function. discuss
					Character.this.l2Window.keyClick(KeyEvent.VK_MINUS);    //maybe to separate function. discuss
				default:
					break;
				//each command can ask macroLocksActions
			}
		}

		public ActionChatCommand(ChatMessage message)
		{
			super();
			this.priority = 300;
			this.message = message;
			this.isMessageReact = true;
			logger.trace("Created ActionChatCommand. ID " + this.getID() + ", message " + this.message);
		}
	}


	/**
	 * User: mrk
	 * Date: 9/14/13; Time: 6:35 AM
	 */
	protected class ActionSelfBuff extends PixelHunter.Character.ActionAbstractBuff
	{
		public final int    buttonNumber;
		public final int    macroDelayMillis;
		public final String buffName;
		public final int    buffDelay;

		@Override
		public void perform()
		{
			if (buffName == null) {
				Action.logger.warn("cancelled buff execution.. maybe this means something");
				return;
			}
			Action.logger.trace("ActionAbstractBuff.perform" + this.toString());
			Character.this.l2Window.keyClick(KeyEvent.VK_NUMPAD0 + this.buttonNumber); //96 is num pad 0. increments lineary
			macroLocksActions(this.macroDelayMillis);    //locks any action because macro is executed
			if (Character.this.modeBuff) {
				Action.logger.debug(".perform: making a hard task of adding new buff. watch it. watch it!");
				Character.this.buffTimerMap.get(this).schedule(new SelfBuffTask(this), this.buffDelay);
			}
		}

		@Override
		public String toString()
		{
			return ("Buff action " + this.buffName + ". ID " + this.getID() + ", priority " + this.priority + ", button Num_" + this.buttonNumber + ", delayTime " + this.buffDelay + ", macro delay " + this.macroDelayMillis);
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

			ActionSelfBuff that = (ActionSelfBuff) o;

			if (buttonNumber != that.buttonNumber) {
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
			int result = buttonNumber;
			result = 31 * result + buffName.hashCode();
			return result;
		}

		public ActionSelfBuff(String buffName, int buttonNumber, int buffDelayMillis, int macroDelayMillis)//btns from numpad
		{
			super();    //auto setting ID

			this.buffName = buffName;
			this.isBuff = true;
			this.priority = 350;
			this.buttonNumber = buttonNumber;
			this.buffDelay = buffDelayMillis;
			this.macroDelayMillis = macroDelayMillis;

			Action.logger.trace("Created ActionAbstractBuff. ID " + this.getID() + ", button Num_" + this.buttonNumber + ", macro delay " + this.macroDelayMillis);
		}

		public ActionSelfBuff(ActionSelfBuff buffExample)
		{
			super();
			this.buffName = buffExample.buffName;
			this.isBuff = true;
			this.priority = buffExample.getPriority();
			this.buttonNumber = buffExample.buttonNumber;
			this.buffDelay = buffExample.buffDelay;
			this.macroDelayMillis = buffExample.macroDelayMillis;

			Action.logger.trace("Created ActionAbstractBuff. ID " + this.getID() + ", button Num_" + this.buttonNumber + ", macro delay " + this.macroDelayMillis);
		}

		@Override
		public Action getNewCopy()
		{
			return new ActionSelfBuff(this);
		}
	}


	protected class ActionPvE extends Action
	{

		@Override
		public void perform()
		{
			logger.trace("ActionPvE.perform");
			if (Character.this.target.isDead()) {
				Character.this.l2Window.keyClick(KeyEvent.VK_F5);
			} else {
				Character.this.attack();
			}

		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {    //todo find a bug. this case does not return false
				return false;
			}

			try {
				ActionChatCommand that = (ActionChatCommand) o;
			} catch (Exception e) {
				return false;
			}

			ActionChatCommand that = (ActionChatCommand) o;    //very bad.. very bad.. discuss
			if (this.isPvE() != that.isPvE()) {
				return false;
			}

			return true;
		}

		public ActionPvE()
		{
			super();
			this.isPvE = true;
			this.priority = 100;
			logger.trace("Created ActionPvE. ID " + this.getID());
		}
	}


	protected class SetPetUseIsAllowedToTrue extends TimerTask    //test
	{
		@Override
		public void run()
		{
			logger.trace("SetPetUseIsAllowedToTrue");
			Character.this.petUseIsAllowed = true;
			cancel();
		}
	}


	private class SetMacroFree extends TimerTask              //not tested
	{
		@Override
		public void run()
		{
			logger.trace("setting isMacroFree to true by timerTask");
			Character.this.isMacroFree = true;
			cancel();    //watch it. the correct behaviour is cancelling this very task, not the timer
		}
	}


	protected class SelfBuffTask extends TimerTask
	{

		private final ActionAbstractBuff addThisBuff;

		@Override
		public void run()
		{
			logger.trace("SelfBuffTask: adding ActionAbstractBuff: " + this.addThisBuff + " to todolist");
			todoOffer(addThisBuff);
			cancel();
		}

		SelfBuffTask(ActionAbstractBuff specificBuff)
		{
			this.addThisBuff = specificBuff;
		}
	}


	protected class PvETask extends TimerTask //not tested
	{
		@Override
		public void run()
		{
			logger.trace("PvETask: adding Action PvE to todolist");
			todoOffer(new ActionPvE());
			cancel();
		}
	}


	protected class HomeRunTask extends TimerTask               //not tested
	{

		@Override
		public void run()
		{
			logger.trace("HomeRunTask: adding ActionHomeRun to todolist");
			todoOffer(new ActionHomeRun());
			cancel();    //watch it. the correct behaviour is cancelling this very task, not the timer
		}
	}
}
