package PixelHunter;

import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

import static java.lang.Math.abs;

/**
 * User: mrk
 * Date: 8/21/13* Time: 3:57 AM
 */
public class L2Window
{

	private static final Logger logger = LoggerFactory.getLogger(L2Window.class);

	public HWND hwnd;

	public Point windowPosition;
	public int   h, w;//kinda bad. has to be refactored

	public int debugMode = 0;

	private final int frame_x = 8,
			frame_yt          = 30,
			frame_yb          = 8;

	private Robot robot;

	private Point chatStartingPoint;

	private boolean noChatMode = false;    //++getter

	public void acceptWindowPosition()
	{
		logger.trace("Inside acceptWindowPosition");
		WinDef.RECT rect = WinAPIAPI.getWindowRect(this.hwnd);
		this.windowPosition = new Point(rect.left, rect.top);
		this.h = abs(rect.top - rect.bottom);
		this.w = abs(rect.right - rect.left);
		return;
	}


	public static void initiateSize(int windowNumber, HWND hwnd1)        //todo not tested
	{
		logger.trace("Inside L2window initiateSize");
		Dimension screenDimentions = Toolkit.getDefaultToolkit().getScreenSize();
		switch (windowNumber) {
			case 0:
				WinAPIAPI.setWindowPos(hwnd1, -8, -25, screenDimentions.width + 16, screenDimentions.height - 7);    //8=frame width, 7is fine, having 50px win panel
				return;
			case 1:
				WinAPIAPI.setWindowPos(hwnd1, -8, -25, screenDimentions.width/2 + 12, screenDimentions.height - 7);
				return;
			case 2:
				WinAPIAPI.setWindowPos(hwnd1, screenDimentions.width / 2 - 4, -25, screenDimentions.width/2 + 12, screenDimentions.height - 7);
				return;
		}
		logger.warn("anomalous behaviour in l2window.initiateSize");
		return;
	}

	public void setChat()
	{

		chatStartingPoint = new Point(112, -45);

		logger.trace("Entered find chat");
		WinAPIAPI.showMessage("Setting up chat properties. Enter _______ to party chat.");
		boolean againFlag = true;    //used to scan two lines in case of finding a spacebar in the first vertical
		while (chatStartingPoint.y > -70) {
			if (colorsAreClose(getRelPixelColor(chatStartingPoint), GroupedVariables.projectConstants.CHAT_COLOR_PARTY)) {
				chatStartingPoint.y -= 2;    //difference between underline symbol and lowest pixel in ':'
				logger.debug("Found chat line, " + chatStartingPoint);
				if (debugMode == 2) {
					WinAPIAPI.showMessage("Found chat line, " + chatStartingPoint);
				}
				return;
			}

			chatStartingPoint.y--;

			if (chatStartingPoint.y == -69 && againFlag == true) {
				chatStartingPoint.y = -45;    //maybe it is bad to use hardcoded constant twice todo:discuss
				chatStartingPoint.x--;
				againFlag = false;
			}

		}
		logger.error("Could not find chat line");
		WinAPIAPI.showMessage("Failed to find chat line!!!");
		noChatMode = true;
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


		if (abs(diffR) > threshold || abs(diffG) > threshold || abs(diffB) > threshold) {
			if (abs(diffR) < 2 * threshold && abs(diffG) < 2 * threshold && abs(diffB) < 2 * threshold) {
				logger.warn("probably two colors are close, but failed comparison. Recommended to increase threshold. " + color1 + " " + color2);
			}
			return false;
		} else {
			return true;
		}

	}

	public void setHP(GroupedVariables.HpConstants hpConstants, int id)    //warning designed to work only with pet, target and party member (not party pet or character)
	{
		logger.trace("l2Window.setHP, entered with id=" + id + ", pet id expected=" + GroupedVariables.projectConstants.ID_PET + ", targ id=" + GroupedVariables.projectConstants.ID_TARGET);
		logger.debug("id-ID_PET="+(id-GroupedVariables.projectConstants.ID_PET));
		if (id == GroupedVariables.projectConstants.ID_PET) {
			WinAPIAPI.showMessage("Set HP bar for the pet. Place mouse under fully healed HP bar and press OK");
		} else if (id == GroupedVariables.projectConstants.ID_TARGET) {
			WinAPIAPI.showMessage("Set HP bar for the target. Place mouse under fully healed HP bar and press OK");
		} else {
			logger.error("wrong id passed to setHP: " + id);
			return;
		}

		Point currentCoordinate = absoluteToRelativeCoordinates(WinAPIAPI.getMousePos());
		logger.debug("got mouse position " + currentCoordinate);
		int i = 0, yLimit = 100;
		while (!colorsAreClose(getRelPixelColor(currentCoordinate), hpConstants.color)) {
			if (i >= yLimit) {   //overflow
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

		while (colorsAreClose(getRelPixelColor(currentCoordinate), hpConstants.color)) {
			currentCoordinate.x--;
		}
		hpConstants.coordinateLeft.x = ++currentCoordinate.x;        //because it is in while. it stops at the first unequal
		logger.debug("Successfully found x left " + currentCoordinate.x);
		if (debugMode == 2) {
			advancedMouseMove(currentCoordinate);
			WinAPIAPI.showMessage("Successfully found x left ");
		}


		currentCoordinate.x = temporaryX;

		while (colorsAreClose(getRelPixelColor(currentCoordinate), hpConstants.color)) {
			currentCoordinate.x++;
		}
		hpConstants.coordinateRight.x = --currentCoordinate.x;
		logger.debug("Successfully found x right" + currentCoordinate.x);
		if (debugMode == 2) {
			advancedMouseMove(currentCoordinate);
			WinAPIAPI.showMessage("Successfully found x right");
		}


		return;
	}

	public int getHP(GroupedVariables.HpConstants hpConstants)
	{
		logger.trace("Inside l2window.getHP");
		Color hpColor = hpConstants.color;
		Point coordinateHp_l = hpConstants.coordinateLeft;
		Point coordinateHp_r = hpConstants.coordinateRight;
		double deltaX = coordinateHp_r.x - coordinateHp_l.x;
		final int ticks = 50;
		Point currentPoint = new Point(coordinateHp_r);
		int i = 0;
		for (; i <= ticks; i++) {
			currentPoint.x = (int) (hpConstants.coordinateRight.x - (float) deltaX * i / (float) ticks);
			logger.debug("coordinate hp R " + coordinateHp_r + " current coordinate " + currentPoint);
			logger.debug("getHP: tick " + i + "of " + ticks + "; deltaX=" + deltaX + " and I am subtracting " + deltaX * (float) i / (float) ticks);
			if (colorsAreClose(getRelPixelColor(currentPoint), hpConstants.color)) {
				return 100 * (ticks + 1 - i) / (ticks + 1);
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

	public Color getRelPixelColor(Point relativePoint)
	{
		Point absolutePoint = relativeToAbsoluteCoordinates(relativePoint);

		Color color;
		color = robot.getPixelColor(absolutePoint.x, absolutePoint.y);
		logger.debug("getrelPixelColor at relative point " + relativePoint + " and got color " + color.toString());
		switch (debugMode) {
			case 1:
				WinAPIAPI.toolTip(color.toString(), absolutePoint.x, absolutePoint.y);
				break;
			case 2:

				advancedMouseMove(new Point(relativePoint.x, relativePoint.y));
//				robot.mouseMove(x, y);
				WinAPIAPI.showMessage("getrelPixelColor at relative point " + relativePoint + " and got color " + color.toString());
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
		hwnd = new HWND(null);
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