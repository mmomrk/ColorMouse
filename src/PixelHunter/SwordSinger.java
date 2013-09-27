package PixelHunter;
import com.sun.jna.platform.win32.WinDef;

/**
 * User: mrk
 * Date: 9/27/13; Time: 5:17 AM
 */
public class SwordSinger extends SwordSingerBladeDancer
{
	public SwordSinger(WinDef.HWND hwnd)
	{
		super(GroupedVariables.ProjectConstants.ID_Swordsinger, hwnd);
		logger.trace("creating SWS");
		for (int i=1;i<=6;i++){
			this.songDanceSequence.add(new SkillSongDance(i));//i don't care
		}
	}
}
