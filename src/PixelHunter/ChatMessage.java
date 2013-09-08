package PixelHunter;
import java.util.Comparator;

/**
 * User: mrk
 * Date: 9/4/13; Time: 1:18 PM
 */
public class ChatMessage
{
	private static int  currentID	=	1;
	public final int iD;
	private int senderID = 0;
	private int priority = 0;
	private int command;
	private int modeColor;    //0=party, 1=private


	public int getPriority()
	{
		return priority;
	}

	public int getCommand()
	{
		return command;
	}

	public int getSenderID()
	{
		return senderID;
	}

	public int getModeColor()
	{
		return modeColor;
	}

	public String toString(){
		return "Message. From: "+ this.senderID+", Command: "+this.command+", mode: "+this.modeColor+", priority: "+this.priority;
	}

	ChatMessage(int command, int senderSignature, int modeColor)
	{
		this.iD	=	currentID;
		if (currentID++==Integer.MAX_VALUE){
			currentID=1;
		};
		this.command = command;
		this.modeColor = modeColor;
		switch (senderSignature) {
			case 0:    //todo after can get these values first-hand
//				this.senderID = 554;
				break;
		}
		switch (command) {
			case 0:        //todo after commands are understood, documented and specified. default is 0
				this.priority = 0;
				break;
			case 3:
				this.priority	=	5;
				break;
			case  5:
				this.priority	=	6;
				break;
			case 7:
				this.priority	=	3;
				break;

		}
//		this.priority	*=	-1;	//priorityQueue starts with the lowest number.	//test it test it. watch current implementation of comparator

	}

	/**
	* User: mrk
	* Date: 9/8/13; Time: 6:37 AM
	*/
	static class MessagePriorityComparator implements Comparator<ChatMessage>    //not tested
	{
		@Override
		public int compare(ChatMessage chatMessage1, ChatMessage chatMessage2)
		{
			if (chatMessage1.getPriority() < chatMessage2.getPriority()) {
				return 1;
			} else if (chatMessage1.getPriority() > chatMessage2.getPriority()) {
				return -1;
			}	else {
				if (chatMessage1.iD > chatMessage2.iD){
					return 1;
				}	else {
					return -1;
				}
			}
		}
	}
}
