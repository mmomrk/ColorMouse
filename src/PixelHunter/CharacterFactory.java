package PixelHunter;
import com.sun.jna.platform.win32.WinDef.HWND;

import static PixelHunter.GroupedVariables.ProjectConstants.ID_DefaultCharacter;
import static PixelHunter.GroupedVariables.ProjectConstants.ID_Templeknight;

/**
 * User: mrk
 * Date: 9/3/13; Time: 5:36 AM
 */
public class CharacterFactory
{
	public static Character getCharacter(int id, HWND hwnd)
	{
		if (id == ID_DefaultCharacter) {
			return new DefaultCharacter(hwnd);
		} else if (id == ID_Templeknight) {

		}
		return new DefaultCharacter(hwnd);    //remove this after things are better

	}
}
