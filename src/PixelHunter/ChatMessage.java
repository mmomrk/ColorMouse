package PixelHunter;
/**
 * User: mrk
 * Date: 9/4/13; Time: 1:18 PM
 */
public class ChatMessage
{
	private int	command;
	private int senderID;
	private int modeColor;
	private int priority;

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

	ChatMessage(int command, int senderID, int modeColor)
	{
		this.command	=	command;
		this.senderID	=	senderID;
		this.modeColor	=	modeColor;
		switch (command){
			case 0:		//todo after commands are understood, documented and specified
				this.priority	=	0;
			case 1:
				this.priority	=	0;
		}
	}
}
