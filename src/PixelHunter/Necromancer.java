package PixelHunter;
import com.sun.jna.platform.win32.WinDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: mrk
 * Date: 9/27/13; Time: 5:20 AM
 */
public class Necromancer extends Character
{
	private static final Logger logger = LoggerFactory.getLogger(Necromancer.class);

	@Override
	protected void setupBuffTimerMap()
	{

	}

	@Override
	public void classSpecificLifeCycle()
	{

	}

	@Override
	public void onKill()
	{

	}

	public Necromancer(int hisID, WinDef.HWND hwnd)
	{
		super(hisID, hwnd);
		logger.trace("Necromancer constructor");
		this.isNuker=true;
		this.isHomeRunner=true;
		this.petUseIsAllowed=false;
	}
}
