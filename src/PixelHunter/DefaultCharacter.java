package PixelHunter;
import com.sun.jna.platform.win32.WinDef;

import java.awt.event.KeyEvent;

/**
 * User: mrk
 * Date: 9/3/13; Time: 2:13 AM
 */
public class DefaultCharacter extends Character
{
	private final ActionBuff
	buff1 = new ActionBuff("20-minute buff", KeyEvent.VK_1, (20*60 - 15)*1000, 20*1000 ),
	buff2 = new ActionBuff("5-minute buff", KeyEvent.VK_1, (5*60 - 15)*1000, 10*1000 ),
	buff3 = new ActionBuff("2-minute buff", KeyEvent.VK_1, (2*60 - 15)*1000, 4*1000 );



	@Override
	public void classSpecificLifeCycle()    //IMPlement
	{

	}

	public DefaultCharacter(WinDef.HWND hwnd)
	{
		super(0, hwnd);
		this.presetBuffs.add(this.buff1);
		this.presetBuffs.add(this.buff2);
		this.presetBuffs.add(this.buff3);
	}
}
