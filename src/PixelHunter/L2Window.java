package PixelHunter;


import com.sun.jna.platform.win32.WinDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;

import static PixelHunter.GroupedVariables.*;

/**
 * User: mrk
 * Date: 8/21/13* Time: 3:57 AM
 */
public class L2Window
{
	private static final Logger logger = LoggerFactory.getLogger(L2Window.class);

	public WinDef.HWND hwnd;

	public Point windowPosition;
	public int   h, w;//kinda bad. has to be refactored
	private final int frame_x = 8,
			frame_yt          = 30,
			frame_yb          = 8;

	public int debugMode = 2;



//	public Color characterHpColor;//, characterHpColorNegative;	//    todo delete it when gethp and sethp works for all
//	public Point characterHp_l, characterHp_r;
//	public final Color petHpColorPositive = new Color(111, 23, 20), petHpColorNegative = new Color(47, 26, 24);
//	public Point petHp_l, petHp_r;
//	public Color targetHpColorPositive, targetHpColorNegative;
//	public Point targetHp_l, targetHp_r;
//	public Color partyHpColorPositive = new Color(111, 23, 20), partyHpColorNegative;
//	public Point partyHp_l, partyHp_r;
//	public int partyHp_ydelta, partyHp_pet_xdelta, partyHp_pet_ydelta;     //copy from existing

	@Override
	public String toString()
	{
		return "L2Window: HWND="+hwnd+"; top-left point is "+windowPosition;
	}

	public void setHP(GroupedVariables.HpConstants hpConstants)    //warning designed to work only with pet, target and party member (not party pet)
	{
		hpConstants.color = projectConstants.SECONDARY_LIVING_CREATURE_HP_COLOR;


		WinAPIAPI.showMessage("Set HP bar for secondary creature. Place mouse under HP fully healed bar and press OK");
		logger.info("Setting HP for a secondary LC");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		Point currentCoordinate = WinAPIAPI.getMousePos();
		int i = 0, yLimit = 100;
		while (!getRelPixelColor(currentCoordinate).equals(projectConstants.SECONDARY_LIVING_CREATURE_HP_COLOR)) {
			if (i >= yLimit) {   //overflow
				if (debugMode == 2) {            //discuss printing won't work in this
//					WinAPIAPI.showMessage("Failed to find y"); todo:return after showmessage is fixed
					logger.info("got "+getRelPixelColor(currentCoordinate)+" expected: projectConstants.SECONDARY_LIVING_CREATURE_HP_COLOR");
					try {
						System.in.read();
					} catch (IOException e) {
						e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
					}
				}
				logger.error("Failed to find y");
				hpConstants.coordinateRight.setLocation(-1, -1);
				hpConstants.coordinateLeft.setLocation(-1, -1);

				return ;
			}
			currentCoordinate.y--;
		}
		logger.debug("Successfully found y" + currentCoordinate.y);
		if (debugMode == 2) {
			advancedMouseMove(currentCoordinate);
			WinAPIAPI.showMessage("Successfully found y");
		}

		hpConstants.coordinateLeft.y = currentCoordinate.y;
		hpConstants.coordinateRight.y = currentCoordinate.y;
		int temporaryX = currentCoordinate.x;

		while (getRelPixelColor(currentCoordinate).equals(projectConstants.SECONDARY_LIVING_CREATURE_HP_COLOR)) {
			currentCoordinate.x--;
		}
		logger.debug("Successfully found x left" + currentCoordinate.x);
		if (debugMode == 2) {
			advancedMouseMove(currentCoordinate);
			WinAPIAPI.showMessage("Successfully found x left");
		}
		hpConstants.coordinateLeft.x = currentCoordinate.x;

		currentCoordinate.x = temporaryX;

		while (getRelPixelColor(currentCoordinate).equals(projectConstants.SECONDARY_LIVING_CREATURE_HP_COLOR)) {
			currentCoordinate.x++;
		}
		logger.debug("Successfully found x right" + currentCoordinate.x);
		if (debugMode == 2) {
			advancedMouseMove(currentCoordinate);
			WinAPIAPI.showMessage("Successfully found x right");
		}
		hpConstants.coordinateRight.x = currentCoordinate.x;

		return ;
	}

	public int getHP(GroupedVariables.HpConstants hpConstants)    //todo not tested
	{
		Color hpColor = hpConstants.color;
		Point coordinateHp_l = hpConstants.coordinateLeft;
		Point coordinateHp_r = hpConstants.coordinateRight;
		double deltaX = coordinateHp_r.x - coordinateHp_l.x;
		int ticks = 50;
		Point currentPoint = coordinateHp_l;
		logger.info(""+currentPoint);//todo remove after this code is debugged
		for (int i = ticks; i >= 0; i--) {
			System.out.println(i);//todo remove after this code is debugged
			currentPoint.x = (int) (coordinateHp_l.x + deltaX * i / ticks);
			try {	//todo remove this asap
				System.in.read();
			} catch (IOException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
			if (getRelPixelColor(currentPoint).equals(hpColor)) {
				return (int) ((i * 100) / ticks);
			}


		}
		return 0;
	}

	public void moveResize(int x, int y, int w, int h)
	{
		this.windowPosition.x = x;
		this.windowPosition.y = y;
		this.w = w;
		this.h = h;
		WinAPIAPI.setWindowPos(hwnd, x, y, w, h);
	}

	public void advancedMouseMove(Point point)
	{
		point = absoluteToRelativeCoordinates(point);
		Robot robot;
		try {
			robot = new Robot();
			robot.mouseMove(point.x, point.y);
		} catch (AWTException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	public Point relativeToAbsoluteCoordinates(Point relativePoint)
	{
		return new Point(this.windowPosition.x + this.frame_x + relativePoint.x, this.windowPosition.y + this.frame_yt + relativePoint.y);
	}

	public Point absoluteToRelativeCoordinates(Point absolutePoint)
	{
		int x = (int) absolutePoint.getX();
		int y = (int) absolutePoint.getY();
		if ((x > this.windowPosition.x + this.w) || (y > this.windowPosition.y + this.h)) {
			WinAPIAPI.showMessage("Coordinate out of range", 3);
			return new Point(-1, -1);
		}

		if (x < 0) {
			x = this.windowPosition.x + this.w - this.frame_x + x;
			if (x < 0) {
				WinAPIAPI.showMessage("Coordinate out of range" + x, 3);
				return new Point(-1, -1);
			}
		} else {
			x += this.windowPosition.x + this.frame_x;
		}

		if (y < 0) {
			y = this.windowPosition.y + this.h - this.frame_yb + y;
			if (y < 0) {
				WinAPIAPI.showMessage("Coordinate out of range" + y, 3);
				return new Point(-1, -1);
			}
		} else {
			y += this.windowPosition.y + this.frame_yt;
		}
		return new Point(x, y);
	}

	public Color getRelPixelColor(Point point)
	{
		int x = (int) point.getX();
		int y = (int) point.getY();
		if ((x > this.windowPosition.x + this.w) || (y > this.windowPosition.y + this.h)) {
			WinAPIAPI.showMessage("Coordinate out of range", 3);
			return Color.CYAN;
		}

		if (x < 0) {
			x = this.windowPosition.x + this.w - this.frame_x + x;
			if (x < 0) {
				WinAPIAPI.showMessage("Coordinate out of range" + x, 3);
				return Color.CYAN;
			}
		} else {
			x += this.windowPosition.x + this.frame_x;
		}

		if (y < 0) {
			y = this.windowPosition.y + this.h - this.frame_yb + y;
			if (y < 0) {
				WinAPIAPI.showMessage("Coordinate out of range" + y, 3);
				return Color.CYAN;
			}
		} else {
			y += this.windowPosition.y + this.frame_yt;
		}

		Color color;
		Robot robot;           //discuss is it ok to make a rbot every time we need a pixel?

		try {
			robot = new Robot();
			color = robot.getPixelColor(x, y);
		} catch (AWTException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			WinAPIAPI.showMessage("Robot error", 3);
			return Color.CYAN;
		}

		switch (debugMode) {
			case 1:
				WinAPIAPI.toolTip(color.toString(), x, y);
				break;
			case 2:
				robot.mouseMove(x, y);
//				WinAPIAPI.showMessage(color.toString());	todo return this asap
				break;
		}
		return color;
	}

	L2Window()
	{
		hwnd = new WinDef.HWND(null);
		windowPosition = new Point(0, 0);
		h = 20;
		w = 20;
	}

}
