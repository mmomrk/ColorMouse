package PixelHunter;
import com.sun.jna.platform.win32.WinDef.HWND;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static PixelHunter.GroupedVariables.ProjectConstants.*;

/**
 * User: mrk
 * Date: 9/3/13; Time: 5:36 AM
 */
public class CharacterFactory
{
	private static final Logger logger = LoggerFactory.getLogger(CharacterFactory.class);

	public static Character getCharacter(int id, HWND hwnd)
	{
		if (id == ID_DefaultCharacter) {
			return new DefaultCharacter(hwnd);
		} else if (id == ID_Templeknight) {
			return new TempleKnight(hwnd);
		} else if (id == ID_Warcryer) {
			return new Warcryer(hwnd);
		} else if (id == ID_Warlock) {
			return new Warlock(hwnd);
		} else if (id == ID_Spoiler) {
			return new Spoiler(hwnd);
		} else if (id == ID_Elvenelder) {
			return new ElvenElder(hwnd);
		} else if (id == ID_Bladedancer) {
			return new BladeDancer(hwnd);
		} else if (id == ID_Swordsinger) {
			return new SwordSinger(hwnd);
		} else if (id == ID_Necromancer) {
			return new Necromancer(hwnd);
		}


		logger.warn("DefaultCharacter has been created");
		return new DefaultCharacter(hwnd);
	}
}
