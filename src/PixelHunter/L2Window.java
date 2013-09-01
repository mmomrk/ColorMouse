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

	public int debugMode = 2;

	private final int frame_x = 8,
			frame_yt          = 30,
			frame_yb          = 8;

	private Robot robot;

	private boolean noChatMode = false;    //++getter



	public void findChat()		//add scan for chat status in char after window init
	{

		Point chatStartingPoint	=	new Point(112,-45); //to constructor

		logger.trace("Entered find chat");
		WinAPIAPI.showMessage("Setting up chat properties. Enter _______ to party chat.");
		boolean againFlag	=	true;	//used to scan two lines in case of finding a spacebar in the first vertical
		while (chatStartingPoint.y > -70)
		{
			if (colorsAreClose(getRelPixelColor(chatStartingPoint),GroupedVariables.projectConstants.CHAT_COLOR_PARTY))
			{
				chatStartingPoint.y	-=	2;	//difference between underline symbol and lowest pixel in ':'
				logger.debug("Found chat line, "+chatStartingPoint);
				if (debugMode	==	2)
				{
					WinAPIAPI.showMessage("Found chat line, "+chatStartingPoint);
				}
				return;
			}

			chatStartingPoint.y--;

			if (chatStartingPoint.y == -69 && againFlag == true)
			{
				chatStartingPoint.y	=	-45;	//maybe it is bad to use hardcoded constant twice todo:discuss
				chatStartingPoint.x--;
				againFlag	=	false;
			}

		}
		logger.error("Could not find chat line");
		WinAPIAPI.showMessage("Failed to find chat line!!!");
		noChatMode	=	true;
		return;
	}


	public boolean isChatMode()
	{
		return !noChatMode;
	}


	public static boolean colorsAreClose(Color color1, Color color2)
	{
		final int threshold = 4;    //test it, watch it
		int diffR = color1.getRed() - color2.getRed();
		int diffG = color1.getGreen() - color2.getGreen();
		int diffB = color1.getBlue() - color2.getBlue();


		if (Math.abs(diffR) > threshold || Math.abs(diffG) > threshold || Math.abs(diffB) > threshold) {
			if (Math.abs(diffR) < 2 * threshold && Math.abs(diffG) < 2 * threshold && Math.abs(diffB) < 2 * threshold) {
				logger.warn("probably two colors are close, but failed comparison. Recommended to increase threshold. " + color1 + " " + color2);
			}
			return false;
		} else {
			return true;
		}

	}

	public void setHP(GroupedVariables.HpConstants hpConstants)    //warning designed to work only with pet, target and party member (not party pet)
	{
		hpConstants.color = projectConstants.SECONDARY_LIVING_CREATURE_HP_COLOR;


		WinAPIAPI.showMessage("Set HP bar for secondary creature. Place mouse under HP fully healed bar and press OK");
		logger.info("Setting HP for a secondary LC");

		Point currentCoordinate = absoluteToRelativeCoordinates(WinAPIAPI.getMousePos());

		int i = 0, yLimit = 100;
		while (!colorsAreClose(getRelPixelColor(currentCoordinate), projectConstants.SECONDARY_LIVING_CREATURE_HP_COLOR)) {
			if (i >= yLimit) {   //overflow
				if (debugMode == 2) {            //discuss printing won't work in this
//					WinAPIAPI.showMessage("Failed to find y"); todo:return after showmessage is fixed
					logger.info("got " + getRelPixelColor(currentCoordinate) + " expected: projectConstants.SECONDARY_LIVING_CREATURE_HP_COLOR");
					try {
						System.in.read();
					} catch (IOException e) {
						e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
					}
				}
				logger.error("Failed to find y");
				hpConstants.coordinateRight.setLocation(-1, -1);
				hpConstants.coordinateLeft.setLocation(-1, -1);

				return;
			}
			currentCoordinate.y--;
			i++;
		}
		logger.debug("Successfully found y" + currentCoordinate.y);
		if (debugMode == 2) {
			advancedMouseMove(currentCoordinate);
			WinAPIAPI.showMessage("Successfully found y");
		}

		hpConstants.coordinateLeft.y = currentCoordinate.y;
		hpConstants.coordinateRight.y = currentCoordinate.y;
		int temporaryX = currentCoordinate.x;

		while (colorsAreClose(getRelPixelColor(currentCoordinate), projectConstants.SECONDARY_LIVING_CREATURE_HP_COLOR)) {
			currentCoordinate.x--;
		}
		logger.debug("Successfully found x left" + currentCoordinate.x);
		if (debugMode == 2) {
			advancedMouseMove(currentCoordinate);
			WinAPIAPI.showMessage("Successfully found x left");
		}
		hpConstants.coordinateLeft.x = currentCoordinate.x + 1;

		currentCoordinate.x = temporaryX;

		while (colorsAreClose(getRelPixelColor(currentCoordinate), projectConstants.SECONDARY_LIVING_CREATURE_HP_COLOR)) {
			currentCoordinate.x++;
		}
		logger.debug("Successfully found x right" + currentCoordinate.x);
		if (debugMode == 2) {
			advancedMouseMove(currentCoordinate);
			WinAPIAPI.showMessage("Successfully found x right");
		}
		hpConstants.coordinateRight.x = currentCoordinate.x - 1;

		return;
	}

	public int getHP(GroupedVariables.HpConstants hpConstants)    //todo not tested
	{
		Color hpColor = hpConstants.color;
		Point coordinateHp_l = hpConstants.coordinateLeft;
		Point coordinateHp_r = hpConstants.coordinateRight;
		double deltaX = coordinateHp_r.x - coordinateHp_l.x;
		int ticks = 50;
		Point currentPoint = coordinateHp_l;

		for (int i = 0; i <= ticks; i++) {
			currentPoint.x = (int) (coordinateHp_r.x - deltaX * i / ticks);
			try {    //todo remove this asap
				System.in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (colorsAreClose(getRelPixelColor(currentPoint), hpColor)) {
				return (int) ((ticks - i) * 100) / ticks;
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
		point = relativeToAbsoluteCoordinates(point);
		robot.mouseMove(point.x, point.y);
	}

	public Point relativeToAbsoluteCoordinates(Point relativePoint)
	{

		Point absolutePoint = new Point();
		absolutePoint.x = (int) relativePoint.getX();
		absolutePoint.y = (int) relativePoint.getY();
		if ((absolutePoint.x > this.windowPosition.x + this.w) || (absolutePoint.y > this.windowPosition.y + this.h)) {
			logger.error("RelativeToAbsolute Coordinate out of range, >0; rel point is" + relativePoint);
			WinAPIAPI.showMessage("RelativeToAbsolute Coordinate out of range, >0", 3);
			return new Point(-1, -1);
		}

		if (absolutePoint.x < 0) {
			absolutePoint.x = this.windowPosition.x + this.w - this.frame_x + absolutePoint.x;
			if (absolutePoint.x < 0) {
				logger.error("RelativeToAbsolute Coordinate out of range, <0; rel point is" + relativePoint);
				WinAPIAPI.showMessage("RelativeToAbsolute Coordinate out of range, <0" + absolutePoint.x, 3);
				return new Point(-1, -1);
			}
		} else {
			absolutePoint.x += this.windowPosition.x + this.frame_x;
		}

		if (absolutePoint.y < 0) {
			absolutePoint.y = this.windowPosition.y + this.h - this.frame_yb + absolutePoint.y;
			if (absolutePoint.y < 0) {
				WinAPIAPI.showMessage("Coordinate out of range" + absolutePoint.y, 3);
				return new Point(-1, -1);
			}
		} else {
			absolutePoint.y += this.windowPosition.y + this.frame_yt;
		}

		return absolutePoint;
	}

	public Point absoluteToRelativeCoordinates(Point absolutePoint)
	{
		Point relativePoint = new Point();
		relativePoint.x = (int) absolutePoint.getX();
		relativePoint.y = (int) absolutePoint.getY();
		if ((absolutePoint.x > this.windowPosition.x + this.w) || (absolutePoint.y > this.windowPosition.y + this.h)) {
			logger.error("Absolute coordinate to rel is out of range. requested " + absolutePoint);
			WinAPIAPI.showMessage("Absolute coordinate to rel is out of range. requested " + absolutePoint, 3);//todo: remove after logger is understood
			return new Point(-1, -1);
		}

		if (absolutePoint.x < 0 || absolutePoint.y < 0) {
			logger.error("Absolute coordinate to rel is NEGATIVE!!!. requested " + absolutePoint);
			WinAPIAPI.showMessage("Absolute coordinate to rel is NEGATIVE!!!. requested " + absolutePoint, 3);//todo: remove after logger is understood
		}

		relativePoint.x = relativePoint.x - this.windowPosition.x - this.frame_x;

		relativePoint.y = relativePoint.y - this.windowPosition.y - this.frame_yt;

		return new Point(relativePoint.x, relativePoint.y);
	}

	public Color getRelPixelColor(Point relativePoint)               //todo hard redo with reltoabs. we had to be drunk while writing it
	{
		Point absolutePoint = relativeToAbsoluteCoordinates(relativePoint);

		Color color;
		color = robot.getPixelColor(absolutePoint.x, absolutePoint.y);

		switch (debugMode) {
			case 1:
				WinAPIAPI.toolTip(color.toString(), absolutePoint.x, absolutePoint.y);
				break;
			case 2:

				advancedMouseMove(new Point(relativePoint.x, relativePoint.y));
//				robot.mouseMove(x, y);
				WinAPIAPI.showMessage("getrelPixelColor at relatice point" + relativePoint + " and got color " + color.toString());
				break;
		}
		return color;
	}


	@Override
	public String toString()
	{
		return "L2Window: HWND=" + hwnd + "; top-left point is " + windowPosition;
	}

	L2Window()
	{
		hwnd = new WinDef.HWND(null);
		windowPosition = new Point(0, 0);
		h = 20;
		w = 20;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

}
