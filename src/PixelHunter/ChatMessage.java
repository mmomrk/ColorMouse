package PixelHunter;
/**
 * User: mrk
 * Date: 9/4/13; Time: 1:18 PM
 */
public class ChatMessage
{
	private static int currentID = 1;
	public final int iD;
	private int senderID = 0;
	private int command;
	private int modeColor;    //0=party, 1=private


	public int getCommand()
	{
		return this.command;
	}

	public int getSenderID()
	{
		return this.senderID;
	}

	public int getModeColor()
	{
		return this.modeColor;
	}

	@Override
	public String toString()
	{
		return "Message from: " + this.senderID + ", command: " + this.command + ", mode: " + this.modeColor;
	}

	ChatMessage(int command, int senderSignature, int modeColor)
	{
		this.iD = currentID;
		if (currentID++ == Integer.MAX_VALUE) {
			currentID = 1;
		} ;
		this.command = command;
		this.modeColor = modeColor;
		switch (senderSignature) {
			case 83:    //todo after can get these values first-hand 83 is resdead
				this.senderID = 1;
				break;
			case 87:	//remove this after tests are over:87 is resdead          //my big bug
				this.senderID	=	1;
				break;
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

		ChatMessage that = (ChatMessage) o;

		if (command != that.command) {
			return false;
		}
		if (senderID != that.senderID) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = senderID;
		result = 31 * result + command;
		return result;
	}
}
