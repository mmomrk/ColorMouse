package PixelHunter;

import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.InputEvent;

import static PixelHunter.WinAPIAPI.getMousePos;
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

	public static int debugMode = GroupedVariables.ProjectConstants.INITIAL_DEBUG_MODE;

	private final int
	frame_x  = 8,
	frame_yt = 30,
	frame_yb = 8;

	private static final int activateDelay = GroupedVariables.ProjectConstants.WINDOW_ACTIVATE_DELAY_MILLIS;

	private static Robot robot;

	private Point chatStartingPoint;

	private boolean noChatMode = false;    //++getter

	public void activate()
	{
		logger.trace(".activate");
		WinAPIAPI.setActiveWindow(this.hwnd);
		try {
			Thread.sleep(activateDelay);
		} catch (InterruptedException e) {
			logger.error("error while sleeping in activate");
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	public void keyClick(int key)
	{
		logger.trace(".keyClick, non-static " + key);

//		WinAPIAPI.setActiveWindow(this.hwnd);    //watch it: this should not decrease productivity

		World.easySleep(150);

		robot.keyPress(key);
		World.easySleep(50);

		robot.keyRelease(key);
		World.easySleep(150);

		return;

	}


	public static void keyClickStatic(int key)
	{
		logger.trace(".keyClickStatic " + key);
		robot.keyPress(key);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		robot.keyRelease(key);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		return;
	}

	public void acceptWindowPosition()
	{
		logger.trace("Inside acceptWindowPosition");
		WinDef.RECT rect = WinAPIAPI.getWindowRect(this.hwnd);
		this.windowPosition = new Point(rect.left, rect.top);
		this.h = abs(rect.top - rect.bottom);
		this.w = abs(rect.right - rect.left);
		return;
	}


	public static void initiateSize(int windowNumber, HWND hwnd1)
	{
		logger.trace("Inside L2window initiateSize");
		Dimension screenDimentions = Toolkit.getDefaultToolkit().getScreenSize();
		switch (windowNumber) {
			case 0:
				WinAPIAPI.setWindowPos(hwnd1, -8, -25, screenDimentions.width + 16, screenDimentions.height - 7);    //8=frame width, 7is fine, having 50px win panel
				return;
			case 1:
				WinAPIAPI.setWindowPos(hwnd1, -8, -25, screenDimentions.width / 2 + 12, screenDimentions.height - 7);
				return;
			case 2:
				WinAPIAPI.setWindowPos(hwnd1, screenDimentions.width / 2 - 4, -25, screenDimentions.width / 2 + 12, screenDimentions.height - 7);
				return;
		}
		logger.warn("anomalous behaviour in l2window.initiateSize");
		return;
	}

	public Point setChat()
	{

		chatStartingPoint = new Point(114, -45);

		logger.trace("Entered find chat");
		WinAPIAPI.showMessage("Setting up chat properties. Enter _______ to party chat.");
		boolean againFlag = true;    //used to scan two lines in case of finding a spacebar in the first vertical
		Color currentColor;
		while (chatStartingPoint.y > -70) {
			currentColor = getRelPixelColor(chatStartingPoint);
			if (colorsAreClose(currentColor, GroupedVariables.ProjectConstants.CHAT_COLOR_PARTY)
				||
				colorsAreClose(currentColor, GroupedVariables.ProjectConstants.CHAT_COLOR_PRIVATE))
			{
				chatStartingPoint.y += 1;    //difference between underline symbol and lowest pixel in ':'
				logger.debug("Found chat line, " + chatStartingPoint);
				if (debugMode == 2) {
					WinAPIAPI.showMessage("Found chat line, " + chatStartingPoint);
				}
				return chatStartingPoint;
			}

			chatStartingPoint.y--;

			if (chatStartingPoint.y == -69 && againFlag == true) {
				chatStartingPoint.y = -45;    //maybe it is bad to use hardcoded constant twice discuss
				chatStartingPoint.x--;
				againFlag = false;
			}

		}
		logger.error("Could not find chat line");
		WinAPIAPI.showMessage("Failed to find chat line!!!");
		this.noChatMode = true;
		return new Point(-1, -1);
	}


	public boolean isChatMode()
	{
		return !noChatMode;
	}


	public static boolean colorsAreClose(Color color1, Color color2)
	{
		final int threshold = 4;
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

	public static boolean colorsAreClose(Color color1, Color color2, int thresholdIn)
	{
		final int threshold = thresholdIn;
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


	private boolean characterHPMPColorIsPositive(Color color, boolean checkHP)
	{
		if (checkHP) {
			if (color.getRed() < 55) {
				return false;
			}
		} else {
			if (color.getBlue() < 120) {
				return false;
			}
		}
		return true;
	}

	private int checkNextPixelsToTheHorizon(boolean toTheLeft, Point checkedCoordinate, Color neededColor)    //to be used with hp only
	{
		logger.trace(".checkNextPixelsToTheHorizon()");

		int
		x = checkedCoordinate.x,
		y = checkedCoordinate.y;

		if (toTheLeft) {
			for (int i = 0; i < 100; i++) {
				if (colorsAreClose(getRelPixelColor(new Point(x - i, y)), neededColor)) {
					logger.debug("checkNextPixelsToTheHorizon() returning "+ (i+1));
					return i + 1;
				}
				if (x-i <= 0) {
					break;
				}
			}
		} else {
			for (int i = 0; i < 100; i++) {
				if (colorsAreClose(getRelPixelColor(new Point(x + i, y)), neededColor)) {
					logger.debug("checkNextPixelsToTheHorizon() returning "+ (i+1));
					return i + 1;
				}
			}
		}
		logger.debug("checkNextPixelsToTheHorizon() returning zero. nothing red in this direction");
		return 0;
	}

	public int getCharacterHPMP(HpConstants hpConstants, boolean gettingHP)
	{
		int overallLength = hpConstants.coordinateRight.x - hpConstants.coordinateLeft.x;
		if (overallLength==0){
			logger.warn("got invalid hpConstatns in getCharacterHPMP");
			return 100;	//discuss.. but with whom?
		}
		Point currentCoordinate = hpConstants.coordinateRight;
		while (!characterHPMPColorIsPositive(getRelPixelColor(currentCoordinate), gettingHP)) {
			currentCoordinate.x--;
		}

		return 100 * (currentCoordinate.x - hpConstants.coordinateLeft.x) / overallLength;

	}

	public void setCharacterHP(HpConstants hpConstants, HpConstants mpConstants)    //not tested
	{
		logger.trace("setting Character HP");
		WinAPIAPI.showMessage("Setting HP for character. place mouse under fully healed hp bar and press ok");
		Point currentCoordinate = absoluteToRelativeCoordinates(getMousePos());
		logger.debug("got mouse position " + currentCoordinate);
		int i = 0, yLimit = 100;
		while (!colorsAreClose(getRelPixelColor(currentCoordinate), hpConstants.color)) {
			if (i >= yLimit) {   //overflow
				logger.error("Failed to find y ");
				hpConstants.coordinateRight.setLocation(-1, -1);
				hpConstants.coordinateLeft.setLocation(-1, -1);
				return;
			}
			currentCoordinate.y--;
			i++;
		}
		logger.debug("Successfully found y " + currentCoordinate.y);
		if (debugMode == 2) {
			advancedMouseMove(currentCoordinate);
			WinAPIAPI.showMessage("Successfully found y ");
		}

		hpConstants.coordinateLeft.y = currentCoordinate.y;
		hpConstants.coordinateRight.y = currentCoordinate.y;
		mpConstants.coordinateLeft.y = currentCoordinate.y + 13;    //magic numbers
		mpConstants.coordinateRight.y = currentCoordinate.y + 13;

		int temporaryX = currentCoordinate.x;

		boolean stop = false;
		int step;
		while (!stop) {
			step = checkNextPixelsToTheHorizon(true, currentCoordinate, hpConstants.color);
			if (step <= 0) {
				stop = true;
			} else {
				currentCoordinate.x -= step;
			}
		}
		hpConstants.coordinateLeft.x = currentCoordinate.x;
		mpConstants.coordinateLeft.x = currentCoordinate.x;
		logger.debug("Successfully found x left " + currentCoordinate.x);
		if (debugMode == 2) {
			advancedMouseMove(currentCoordinate);
			WinAPIAPI.showMessage("Successfully found x left ");
		}


		currentCoordinate.x = temporaryX;
		stop = false;
		while (!stop) {
			step = checkNextPixelsToTheHorizon(false, currentCoordinate, hpConstants.color);
			if (step <= 0) {
				stop = true;
			} else {
				currentCoordinate.x += step;
			}
		}
		hpConstants.coordinateRight.x = currentCoordinate.x;
		mpConstants.coordinateRight.x = currentCoordinate.x;
		logger.debug("Successfully found x right" + currentCoordinate.x);
		if (debugMode == 2) {
			advancedMouseMove(currentCoordinate);
			WinAPIAPI.showMessage("Successfully found x right");
		}
		logger.info("successfully found HP");

		return;
	}

	public void setHP(HpConstants hpConstants)    //warning designed to work only with pet, target and party member (not party pet or character)
	{
		logger.trace(".setHP");
		if (hpConstants.id == GroupedVariables.ProjectConstants.ID_PET) {
			WinAPIAPI.showMessage("Set HP bar for the pet. Place mouse under fully healed HP bar and press OK");
		} else if (hpConstants.id == GroupedVariables.ProjectConstants.ID_TARGET) {
			WinAPIAPI.showMessage("Set HP bar for the target. Place mouse under fully healed HP bar and press OK");
		} else if (hpConstants.id == GroupedVariables.ProjectConstants.ID_PartyMember) {
			WinAPIAPI.showMessage("Set HP bar for the party member. Place mouse under highest fully healed HP bar and press OK");
		} else if (hpConstants.id == GroupedVariables.ProjectConstants.ID_PartyMembersPet) {
			WinAPIAPI.showMessage("Set HP bar for the party member's pet. Place mouse under highest fully healed HP bar and press OK");
		} else {
			logger.error("wrong id passed to setHP: " + hpConstants.id);
			return;
		}

		Point currentCoordinate = absoluteToRelativeCoordinates(getMousePos());
		logger.debug("got mouse position " + currentCoordinate);
		int i = 0, yLimit = 100;
		while (!colorsAreClose(getRelPixelColor(currentCoordinate), hpConstants.color)) {
			if (i >= yLimit) {   //overflow
				logger.error("Failed to find y ");
				hpConstants.coordinateRight.setLocation(-1, -1);
				hpConstants.coordinateLeft.setLocation(-1, -1);
				return;
			}
			currentCoordinate.y--;
			i++;
		}
		logger.debug("Successfully found y " + currentCoordinate.y);
		if (debugMode == 2) {
			advancedMouseMove(currentCoordinate);
			WinAPIAPI.showMessage("Successfully found y ");
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
		logger.info("successfully found HP");

		return;
	}

	public static void mouseClick_Absolute(Point absolutePoint)
	{
		logger.trace(".mouseClick Absolute to " + absolutePoint);

		Point currentMousePosition = getMousePos();
		robot.mouseMove(absolutePoint.x, absolutePoint.y);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		if (debugMode == 0) {
			robot.mouseMove(currentMousePosition.x, currentMousePosition.y);
		}
	}

	public void mouseClick_Relative(Point relativePoint)//not tested
	{
		logger.trace(".mouseClick Relative");

		Point currentMousePosition = getMousePos();
		advancedMouseMove(relativePoint);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		if (debugMode == 0) {
			robot.mouseMove(currentMousePosition.x, currentMousePosition.y);
		}
	}

	public int getHP(HpConstants hpConstants)
	{
		logger.trace(".getHP");
		Color hpColor = hpConstants.color;
		Point coordinateHp_l = hpConstants.coordinateLeft;
		Point coordinateHp_r = hpConstants.coordinateRight;
		double deltaX = coordinateHp_r.x - coordinateHp_l.x;
		final int ticks = 50;
		Point currentPoint = new Point(coordinateHp_r);

		if (hpConstants.isPet) {
			logger.debug(".getHP: checking for the pet=>click on the frame");
			mouseClick_Relative(new Point(hpConstants.coordinateLeft.x - 10, hpConstants.coordinateLeft.y));
		}

		int i = 0;
		for (; i <= ticks; i++) {
			currentPoint.x = (int) (hpConstants.coordinateRight.x - (float) deltaX * i / (float) ticks);
			if (colorsAreClose(getRelPixelColor(currentPoint), hpConstants.color)) {
				return 100 * (ticks + 1 - i) / (ticks + 1);
			}
		}
		return 0;
	}


	public void moveResize(int x, int y, int w, int h)
	{
		logger.trace(".moveResize");
		this.windowPosition.x = x;
		this.windowPosition.y = y;
		this.w = w;
		this.h = h;
		WinAPIAPI.setWindowPos(hwnd, x, y, w, h);
	}

	public void advancedMouseMove(Point point)
	{
		if (debugMode == 3) {
			logger.trace(".advancedMouseMove to " + point);
		}
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
			logger.error("Absolute coordinate to rel is out of range!!!!!! requested " + absolutePoint);
			return new Point(-1, -1);
		}

		if (absolutePoint.x < 0 || absolutePoint.y < 0) {
			logger.error("Absolute coordinate to rel is NEGATIVE!!!. requested " + absolutePoint);
		}

		relativePoint.x = relativePoint.x - this.windowPosition.x - this.frame_x;

		relativePoint.y = relativePoint.y - this.windowPosition.y - this.frame_yt;

		return new Point(relativePoint.x, relativePoint.y);
	}

	public static Color getAbsPixelColor(Point absolutePoint)
	{


		Color color;
		color = robot.getPixelColor(absolutePoint.x, absolutePoint.y);
		switch (debugMode) {
			case 1:
//				WinAPIAPI.toolTip(color.toString(), absolutePoint.x, absolutePoint.y);
				robot.mouseMove(absolutePoint.x, absolutePoint.y);
				break;
			case 2:
				robot.mouseMove(absolutePoint.x, absolutePoint.y);
				WinAPIAPI.showMessage("getrelPixelColor at relative point " + absolutePoint + " and got color " + color.toString());
				break;
		}
		return color;
	}

	public Color getRelPixelColor(Point relativePoint)
	{
		Point absolutePoint = relativeToAbsoluteCoordinates(relativePoint);

		Color color;
		color = robot.getPixelColor(absolutePoint.x, absolutePoint.y);
		switch (debugMode) {
			case 1:
				advancedMouseMove(relativePoint);
//				WinAPIAPI.toolTip(color.toString(), absolutePoint.x, absolutePoint.y);
				break;
			case 2:
				advancedMouseMove(relativePoint);
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
		this.hwnd = new HWND(null);
		this.windowPosition = new Point(0, 0);
		this.h = 20;
		this.w = 20;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			logger.error("Failed to create robot in l2window. we're doomed");
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

		}
	}

	L2Window(HWND hwnd)
	{
		this.hwnd = hwnd;
		this.windowPosition = new Point(0, 0);
		this.h = 20;
		this.w = 20;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			logger.error("Failed to create robot in l2window. we're doomed");
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

}