package PixelHunter;


import com.sun.jna.platform.win32.WinDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.pow;

/**
 * User: mrk
 * Date: 9/1/13; Time: 1:17 PM
 */
public abstract class Character extends LivingCreature
{

	private static final Logger logger                  = LoggerFactory.getLogger(Character.class);
	//	public timer    buffTimer, homerunTimer;
//	private int buffTime;
//	public  int farmMode, buffMode, homerunMode;
	private static final int    maxChatStartExpectation = 100;

	private Point chatStartingPoint;

	public  Pet    pet;    //remove public after tests are done
	private Target target;

	private Comparator<ChatMessage>    chatMessageComparator = new ChatMessage.MessagePriorityComparator();
	private PriorityQueue<ChatMessage> toDoList              = new PriorityQueue<ChatMessage>(GroupedVariables.projectConstants.CHAT_TASK_LIST_LENGTH, chatMessageComparator);

	private boolean
	isMacroFree = true,
	rebuff      = false;

	private Timer macroLockTimer;

	abstract void buffRebuff(); //should set rebuff to 0

	//	public boolean followFlag;
//	public void classSpecificDeed();//think of it twice
//	public void pvE();
//	public void runHome();
//	public void target(int id);
//	public void setMp();
//	public void setPetHp();
//	public void initializeWindow();    //in case of falling down of the prev window
//	//commands from chat exec part
//	public void follow(int id);
//
//	public void assistSender(int id);
//
//	public void assistSenderAndAttack(int id);
//
//	public void attackCurrentTarget();    //#4
//
//	//#5,6-reserved by now
//	public void petAssistAttack(int id);    //#7
//
//	public void petStop();
//
//	public void petFollow();
//
//	public void toggleBuffMode();
//
//	public void toggleFarmMode();    //#11
//
	public void spamSelf()
	{
		l2Window.keyClick(KeyEvent.VK_0);
	}

	private boolean chatCommandExecute(ChatMessage doIt)    //todo
	{
		logger.trace(".chatCommandExecute");
		if (!(isMacroFree && (target.isDead() || target.getHP() == 100))) {    //impossible to execute the order if is not mFree and hp magic
			return false;
		}
		switch (doIt.getCommand()) {
			case 0:
				//todo make a big switch for commands
				//each command can ask macroLocksActions
		}
		return true;
	}


	private void macroLocksActions(int milliseconds)
	{    //todo not tested
		logger.trace(".macroLocksActions");
		isMacroFree = false;
		macroLockTimer.schedule(new SetMacroFree(), milliseconds);
	}

	private boolean chatPatternMatches(int i, boolean equalsExpectedColor)
	{
		logger.trace(".chatPatternMatches with pixel number " + i + " and matching green color of " + equalsExpectedColor);
		logger.debug("first condition is " + (i % 5 < 3 && equalsExpectedColor) + " and second is " + (i % 5 >= 3 && !equalsExpectedColor));
		if ((i % 5 < 3 && equalsExpectedColor) || (i % 5 >= 3 && !equalsExpectedColor)) {    //is it magic? not yet. look int your chat
			return true;
		} else {
			logger.warn("Chat pattern match has failed. Generally this should not happen");
			return false;
		}
	}

	private ChatMessage readChat()
	{    //todo implement
		logger.trace(".readChat");
		Color messageColor = Color.CYAN;    //compiler doesn't know this is not necessary
		int senderSignature, receivedCode = 0, modeColor = 0;
		int i;
		Point currentPoint = new Point(0, this.chatStartingPoint.y + 3);//lower command pixel is 3 px under ':'

		logger.debug("now searching first pixel in signature");
		for (i = 50; i < this.maxChatStartExpectation; i++) {    //todo test the length
			currentPoint.x = i;
			if (l2Window.colorsAreClose(l2Window.getRelPixelColor(currentPoint), GroupedVariables.projectConstants.CHAT_COLOR_PARTY)) {
				messageColor = GroupedVariables.projectConstants.CHAT_COLOR_PARTY;
				modeColor = 0;
				break;
			}
			if (l2Window.colorsAreClose(l2Window.getRelPixelColor(currentPoint), GroupedVariables.projectConstants.CHAT_COLOR_PRIVATE)) {
				messageColor = GroupedVariables.projectConstants.CHAT_COLOR_PRIVATE;
				modeColor = 1;
				break;
			}
		}
		if (i == this.maxChatStartExpectation) {
			logger.info("Chat is empty");
			return null;
		}
		//found first pixel
		senderSignature = i;
		logger.debug("found signature " + i);
		//now check the pattern
		for (i = 0; i < 17; i++) {
			currentPoint.x = senderSignature + i;
			if (!chatPatternMatches(i, l2Window.colorsAreClose(l2Window.getRelPixelColor(currentPoint), messageColor))) {
				return null;
			}
		}
		//now we totally have in incoming
		currentPoint.y--;    //to be in the information zone
		// decoding
		l2Window.debugMode = 2;    //remove after debugged
		for (i = 0; i <= 5; i++) {//command binary capacity sits in this hardcoded 5
			currentPoint.x = senderSignature + i * 5;
			if (!l2Window.colorsAreClose(l2Window.getRelPixelColor(currentPoint), messageColor)) {//found bit true
				if (l2Window.colorsAreClose(l2Window.getRelPixelColor(new Point(currentPoint.x + 2, currentPoint.y)), messageColor)) {
					receivedCode += pow(2, i);
				}else {
					logger.warn(".ReadChat: Warning while decoding message: both left and right pivot points are empty");
					break;
				}
			}
			logger.debug(".readChat: decode. current val is " + receivedCode);
		}
		logger.info(".readChat: Received chat command number " + receivedCode + " from sig " + senderSignature);
		return new ChatMessage(receivedCode, senderSignature, modeColor);
	}

	public void chatReact()                   //todo not tested
	{
		logger.trace(".chatReact");

		if (!l2Window.isChatMode()) {//means error while setting chat, no need to waste time for all this
			return;
		}

		ChatMessage incomingMessage = readChat();
		if (incomingMessage != null) {
			spamSelf();
			toDoList.offer(incomingMessage);
		}

		boolean commandExecuted = false;
		if (!toDoList.isEmpty()) {
			commandExecuted = chatCommandExecute(toDoList.peek());
			if (commandExecuted) {
				logger.debug(".chatReact: Command executed");
				toDoList.poll();
			} else {
				logger.debug(".chatReact: Command was not executed.");
			}
			System.out.println("Todo list:\n"+toDoList.toString());
		}



		return;
	}

	public void setChat()
	{
		this.chatStartingPoint = l2Window.setChat();
		logger.info("Finished setChat. Now chatStartingPoint is " + this.chatStartingPoint);
	}

	public void setHP()
	{    //todo if needed

	}

	public int getHP()
	{    //todo when time comes don't forget to do this
		return 0;
	}


	public String toString()
	{

		return "Character. ID=" + this.id + ". Window " + l2Window.toString();
	}


	public Character(int thisid, WinDef.HWND hwnd)        //windownumber can only be 1 or 0
	{
		super(thisid);
		logger.trace("Inside Character constructor, finished making LC");

		this.l2Window = new L2Window(hwnd);

		l2Window.acceptWindowPosition();
		logger.debug("after acceptWindowPos. now it is h " + l2Window.h + " w " + l2Window.w + " top-left " + l2Window.windowPosition);

		pet = new Pet(l2Window);//including setHP
		target = new Target(l2Window);

		setChat();

		//initing all tough structures
		this.macroLockTimer = new Timer();
	}

//CLASSES


	private class SetMacroFree extends TimerTask
	{        //not tested

		@Override
		public void run()
		{
			logger.trace("resetting macroFree by timerTask");
			Character.this.isMacroFree = true;
			cancel();    //watch it. the correct behaviour is cancelling this very task, not the timer
		}
	}


	private class SetRebuff extends TimerTask
	{        //not tested

		@Override
		public void run()
		{
			logger.trace("resetting setRebuff by timerTask");
			Character.this.rebuff = true;
			cancel();    //watch it. the correct behaviour is cancelling this very task, not the timer
		}
	}

}
