package PixelHunter;
import com.sun.jna.platform.win32.WinDef;

/**
 * User: mrk
 * Date: 9/3/13; Time: 2:13 AM
 */
public class DefaultCharacter extends Character
{


	@Override
	public int getHP()
	{
		return super.getHP();
	}

	@Override
	void buffRebuff()	//todo implement
	{
		return;
	}

	@Override
	public void setHP()
	{
		super.setHP();
	}

	public DefaultCharacter(WinDef.HWND hwnd)
	{
		super(0, hwnd);
	}
}
