package PixelHunter;
import com.sun.glass.events.KeyEvent;

import java.awt.*;

/**
 * User: mrk
 * Date: 12/17/13; Time: 12:43 PM
 */
public class LittleInGameHelper
{
	private boolean cpCheckMode;

	private Point cpCheckPoint;

	private Color cpCheckColor;

	private void prepareCPCheckMode(){
		WinAPIAPI.showMessage("Mouse at CP control point");
		this.cpCheckPoint = WinAPIAPI.getMousePos();
		this.cpCheckColor = L2Window.getAbsPixelColor(this.cpCheckPoint);
	}

	public  void doTheJob()
	{
		while (true){
			if (this.cpCheckMode){
				while (!L2Window.colorsAreClose(this.cpCheckColor,L2Window.getAbsPixelColor(this.cpCheckPoint))){
					L2Window.keyClickStatic(KeyEvent.VK_F11);
					World.easySleep(500);
				}
			}
		}

	}

	public LittleInGameHelper(boolean cpCheck)
	{
		this.cpCheckMode =cpCheck;
		if (this.cpCheckMode){
			prepareCPCheckMode();
		}
	}
}
