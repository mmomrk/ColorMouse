package PixelHunter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.KeyEvent;

import static PixelHunter.L2Window.*;
import static java.lang.Thread.sleep;

/**
 * User: mrk
 * Date: 9/6/13; Time: 2:11 AM
 */
public class Fisher    //todo finished making this class super cool
{
	private static final Logger logger = LoggerFactory.getLogger(Fisher.class);

	private L2Window l2Window;
	private static final Color
	colorHpBlueNegative   = new Color(53, 33, 26),
	colorHpOrangeNegative = new Color(36, 14, 12),
	colorHpBlue           = new Color(0, 103, 159),//(12, 104, 156),
	colorHpOrange         = new Color(144, 36, 7),
	colorControlFrame     = new Color(178, 163, 141);
	private static boolean nightMode = false;
	private Point workingPoint;
	private boolean firstAnalysis = true;


	private static final int
	timeToWaitMillis  = 1000,//how much time  to wait before act
	timeToSleepMillis = 50;//passed to sleep method

	private final Point
	leftmostBluePixelCoordinate,
	controlFrameCoordinate;

	private final int threshold = 10;    //default is 4


	public void fish()
	{
		logger.trace(".fish");
		keyClick(KeyEvent.VK_NUMPAD1);    //not safe but anyway
		easySleep(1000);
		if (!isFishingFrameExist()) {    //we killed the frame. correcting the mistake
			keyClick(KeyEvent.VK_NUMPAD1);
		}
		waitForFishHp();
		while (isFishingFrameExist()) {
			waitForBlink();
			act(analyze());
		}
		logger.info("finished fishing");
	}

	private boolean isForNightTime()    //todo test it when nighttime support is added
	{
		DateTime currentDateTime = DateTime.now();
		int currentHour = currentDateTime.getHourOfDay();
		if (currentHour % 4 == 1) {
			return true;
		} else {
			return false;
		}


		//todo implement
	}

	private void waitForBlink()
	{
		while (!
			   (colorsAreClose(getAbsPixelColor(this.leftmostBluePixelCoordinate), this.colorHpBlue, threshold)
				||
				colorsAreClose(getAbsPixelColor(this.leftmostBluePixelCoordinate), this.colorHpOrange, threshold)
				||
				colorsAreClose(getAbsPixelColor(this.leftmostBluePixelCoordinate), this.colorHpOrangeNegative, threshold)
				||
				colorsAreClose(getAbsPixelColor(this.leftmostBluePixelCoordinate), this.colorHpBlueNegative, threshold))
		)
		{
			try {        //one can refactor all these trey sleep to use easy sleep from window
				sleep(timeToSleepMillis);
			} catch (InterruptedException e) {
				logger.error("sleep in waitForBlink was interrupted for some reason");
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
		}
	}

	private void waitForFishHp()
	{
		logger.trace(".waitforFishHP");
		int timePassed = 0;
		do {
			timePassed += this.timeToSleepMillis;
			try {
				sleep(timeToSleepMillis);
			} catch (InterruptedException e) {
				logger.error("sleep in waitForFishHP was interrupted for some reason");
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
		}
		while (!(colorsAreClose(getAbsPixelColor(this.leftmostBluePixelCoordinate), this.colorHpBlue, threshold)
				 ||
				 colorsAreClose(getAbsPixelColor(this.leftmostBluePixelCoordinate), this.colorHpOrange, threshold))
		);
		logger.info("Fish HP bar finally appeared");
	}

	private boolean isFishingFrameExist()
	{
		if (colorsAreClose(getAbsPixelColor(this.controlFrameCoordinate), this.colorControlFrame, threshold)) {
			return true;
		} else {
			return false;
		}
	}

	private void act(boolean reel)
	{
		logger.trace(".act " + reel);
		if (reel) {
			keyClick(KeyEvent.VK_NUMPAD3);
		} else {
			keyClick(KeyEvent.VK_NUMPAD2);
		}

	}


	private boolean analyze()
	{
		logger.trace(".analyze");
		if (firstAnalysis) {
			workingPoint = new Point(this.leftmostBluePixelCoordinate);
		}


		boolean stayingOnPositive;
		if (colorsAreClose(getAbsPixelColor(workingPoint), this.colorHpBlue, threshold)
			||
			colorsAreClose(getAbsPixelColor(workingPoint), this.colorHpOrange, threshold))
		{
			stayingOnPositive = true;
		} else if (colorsAreClose(getAbsPixelColor(workingPoint), this.colorHpBlueNegative, threshold)
				   ||
				   colorsAreClose(getAbsPixelColor(workingPoint), this.colorHpOrangeNegative, threshold))
		{
			stayingOnPositive = false;
		} else {
			logger.error("Found invalid color in analyze");
			return false;
		}

		if (stayingOnPositive) {
			while (colorsAreClose(getAbsPixelColor(workingPoint), this.colorHpBlue, threshold)
				   ||
				   colorsAreClose(getAbsPixelColor(workingPoint), this.colorHpOrange, threshold))
			{
				workingPoint.x++;
			}
		} else {
			while (colorsAreClose(getAbsPixelColor(workingPoint), this.colorHpBlueNegative, threshold)
				   ||
				   colorsAreClose(getAbsPixelColor(workingPoint), this.colorHpOrangeNegative, threshold))
			{
				workingPoint.x--;
			}

		}
		int timePassed = 0;
		while (timePassed < this.timeToWaitMillis) {
			if (colorsAreClose(getAbsPixelColor(workingPoint), this.colorHpBlue, threshold) ||
				colorsAreClose(getAbsPixelColor(workingPoint), this.colorHpOrange, threshold))
			{
				return true;    //do the reeling
			}
			timePassed += this.timeToSleepMillis;
			try {
				sleep(timeToSleepMillis);
			} catch (InterruptedException e) {
				logger.error("sleep in check was interrupted for some reason");
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
		}

		return false;//pumping

	}

	private Point findBar()
	{
		logger.trace(".findBar");
		WinAPIAPI.showMessage("Wlcome to fishing. Move mouse UNDER BLUE fish hp bar");
		Point currentPoint = WinAPIAPI.getMousePos();
		while (!colorsAreClose(getAbsPixelColor(currentPoint), this.colorHpBlue, threshold)) {
			currentPoint.y--;
		}
		logger.debug("found bar's Y at " + currentPoint.y);
		while (colorsAreClose(getAbsPixelColor(currentPoint), this.colorHpBlue, threshold)) {
			currentPoint.x--;
		}
		currentPoint.x++;
		logger.debug("found bar's X at " + currentPoint.x);
		logger.info("Sucessfully found fish bar");
		return currentPoint;

	}


	public Fisher(L2Window l2Window)
	{
		this.l2Window = l2Window;
		this.leftmostBluePixelCoordinate = findBar();
		this.controlFrameCoordinate = new Point(this.leftmostBluePixelCoordinate.x, this.leftmostBluePixelCoordinate.y + 40);

	}

}
