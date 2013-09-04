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

	ChatMessage(int command,int senderID,int modeColor){
		this.command	=	command;
		this.senderID	=	senderID;
		this.modeColor	=	modeColor;
	}
}
