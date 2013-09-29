package PixelHunter;
import static PixelHunter.GroupedVariables.ProjectConstants.*;

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
			case 87:
			case 85:
			case 86:
				this.senderID=	ID_Warcryer;
				break;
			case 73:
			case 71:
			case 72:
				this.senderID=	ID_Spoiler;
				break;
			case 79:
			case 77:
			case 78:
				this.senderID=	ID_Swordsinger;
				break;
			case 97:
			case 95:
			case 96:
				this.senderID=	ID_Bladedancer;
				break;
			case 64:
			case 62:
			case 63:
				this.senderID=	ID_Warlock;
				break;
			case 83:
			case 81:
			case 82:
				this.senderID=	ID_Templeknight;
				break;
			case 92:
			case 90:
			case 91:
				this.senderID=	ID_Necromancer;
				break;
			case 69:
			case 67:
			case 68:
				this.senderID=	ID_Elvenelder;
				break;
			default:
				WinAPIAPI.showMessage("Unknown signature: "+senderSignature);
				this.senderID=0;
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
