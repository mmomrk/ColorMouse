package PixelHunter;


import com.sun.jna.platform.win32.WinDef;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: mrk
 * Date: 8/21/13
 * Time: 3:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class L2Window
{

	public WinDef.HWND hwnd;

	public Point windowPosition;
	public int   h, w;//kinda bad. has to be refactored
	private final int frame_x = 8,
			frame_yt          = 30,
			frame_yb          = 8;

	public Color characterHpColorPositive, characterHpColorNegative;
	public Point characterHp_l, characterHp_r;
	public final Color petHpColorPositive = new Color(111, 23, 20), petHpColorNegative = new Color(47, 26, 24);
	public Point petHp_l, petHp_r;
	public Color targetHpColorPositive, targetHpColorNegative;
	public Point targetHp_l, targetHp_r;
	public Color partyHpColorPositive = new Color(111, 23, 20), partyHpColorNegative;//todo test if negative is the same as pet's
	public Point partyHp_l, partyHp_r;
	public int partyHp_ydelta, partyHp_pet_xdelta, partyHp_pet_ydelta;     //copy from existing

	public void moveResize(int x, int y, int w, int h)
	{
		this.windowPosition.x = x;
		this.windowPosition.y = y;
		this.w = w;
		this.h = h;
		WinAPIAPI.setWindowPos(hwnd, x, y, w, h);
	}


	public int debugMode = 2;

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
		Robot robot;

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
				WinAPIAPI.showMessage(color.toString());
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

		characterHp_l = new Point(0, 0);
		characterHp_r = new Point(0, 0);

		petHp_l = new Point(0, 0);
		petHp_r = new Point(0, 0);

		targetHp_l = new Point(0, 0);
		targetHp_r = new Point(0, 0);

		partyHp_l = new Point(0, 0);
		partyHp_r = new Point(0, 0);

		partyHp_ydelta = 0;
		partyHp_pet_xdelta = 0;
		partyHp_pet_ydelta = 0;
	}

}
