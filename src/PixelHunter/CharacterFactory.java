package PixelHunter;
import com.sun.jna.platform.win32.WinDef.HWND;

/**
 * User: mrk
 * Date: 9/3/13; Time: 5:36 AM
 */
public class CharacterFactory
{
	public static Character getCharacter(int id, HWND hwnd)
	{
		switch (id) {
			case 0:
				return new DefaultCharacter(hwnd);

		}

		return new DefaultCharacter(hwnd);

	}
}
