package PixelHunter;
import com.sun.jna.platform.win32.WinDef;

/**
 * User: mrk
 * Date: 9/27/13; Time: 4:59 AM
 */
public class BladeDancer extends SwordSingerBladeDancer
{


	public BladeDancer(WinDef.HWND hwnd)
	{
		super(GroupedVariables.ProjectConstants.ID_Bladedancer, hwnd);
		logger.trace("creating BD");
		for (int i = 1; i <= 6; i++) {
			this.songDanceSequence.add(new SkillSongDance(i));//i don't care
		}
	}
}
