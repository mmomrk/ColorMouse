package PixelHunter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

/**
 * User: mrk
 * Date: 9/9/13; Time: 5:15 AM
 */
abstract class Action
{
	protected static final Logger logger = LoggerFactory.getLogger(Action.class);

	private static int currentID = 1;
	protected final int iD;

	protected boolean
	isBuff,
	isPvE,
	isMessageReact,
	isSkill,
	isHomeRun;
//	protected L2Window l2Window;
	protected int      priority;    //pve-100;	homerun-200; message-300; buff-350

	public abstract void perform();    //returns macro delay

	public int increasePriority(int incrementPriority)
	{
		logger.trace(".increasePriority" + this.toString());
		this.priority += incrementPriority;
		return this.priority;
	}

	public int setPriority(int newPriority)
	{
		logger.trace(".increasePriority" + this.toString());
		this.priority = newPriority;
		return this.priority;
	}

	public boolean isBuff()
	{
		return isBuff;
	}

	public boolean isPvE()
	{
		return isPvE;
	}

	public boolean isMessageReact()
	{
		return isMessageReact;
	}

	public boolean isHomeRun()
	{
		return isHomeRun;
	}


	public int getID()
	{
		return this.iD;
	}

	public int getPriority()
	{
		return this.priority;
	}


	protected 	Action()
	{
		this.iD = currentID++;
		if (currentID == Integer.MAX_VALUE) {
			currentID = 1;
			logger.warn("ID has reached MAX VALUE. unexpected things may happen");
		}
//		this.l2Window = l2Window;
	}

	public static class ActionPriorityComparator implements Comparator<Action>
	{
		@Override
		public int compare(Action chatMessage1, Action chatMessage2)
		{
			if (chatMessage1.getPriority() < chatMessage2.getPriority()) {
				return 1;
			} else if (chatMessage1.getPriority() > chatMessage2.getPriority()) {
				return -1;
			} else {
				if (chatMessage1.iD > chatMessage2.iD) {
					return 1;
				} else {
					return -1;
				}
			}
		}
	}
}


