package PixelHunter;
/**
 * User: mrk
 * Date: 9/4/13; Time: 1:18 PM
 */
public class ChatMessage
{
	private int senderID	=	0;
	private int priority	=	0;
	private int	command;
	private int modeColor;	//0=party, 1=private


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

	ChatMessage(int command, int senderSignature, int modeColor)
	{
		this.command	=	command;
		this.modeColor	=	modeColor;
		switch (senderSignature){
			case	0:	//todo after can get these values first-hand
				this.senderID	=	554;
				break;
		}
		switch (command){
			case 0:		//todo after commands are understood, documented and specified. default is 0
				this.priority	=	0;
				break;
		}

	}
}
