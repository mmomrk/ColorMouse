package PixelHunter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.KeyEvent;

import static PixelHunter.L2Window.*;
import static java.lang.System.exit;

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
	colorHpBlue           = new Color(4, 103, 159),//(12, 104, 156),
	colorHpOrange         = new Color(144, 36, 7),
	colorControlFrame     = new Color(178, 163, 141),
	colorControlOrange    = new Color(195, 55, 20),
	colorControlBlue      = new Color(0, 175, 246);

	private static boolean nightMode = false;
	private final Point blinkControlPoint;
	private       Point workingPoint;

	private boolean
	firstFish     = true,
	firstAnalysis = true;



	private static final int
	timeToWaitMillis  = 850,//how much time  to wait before act
	timeToSleepMillis = 300,//passed to sleep method
	timeInLoopDelayInAnalyze=20;

	private final Point
	leftmostBluePixelCoordinate,
	controlFrameCoordinate;

	private final int threshold = 10;    //default is 4

	private int numberOfFAilsInARow = 0,
	lastKeyPressed=0;
	private long failTime;

	private void checkForDisconnect(){
		if (this.lastKeyPressed==KeyEvent.VK_NUMPAD2){
			numberOfFAilsInARow++;
		}	else {
			numberOfFAilsInARow=0;
		}
		if (numberOfFAilsInARow>=40){

			exit(1);
		}

	}

	public void infiniteFish()
	{
		while (true) {
			fish();
			checkForDisconnect();
		}



	}

	public void fish()
	{
		logger.trace(".fish");

		if (!isFishingFrameExist()) {    //we killed the frame. correcting the mistake
			keyClickStatic(KeyEvent.VK_NUMPAD2);
			this.lastKeyPressed=KeyEvent.VK_NUMPAD2;
			logger.info("Throwing a bait");
		}

		this.firstAnalysis = true;
		if (this.firstFish) {
			this.firstFish = false;
		} else {
			waitForFishHp();
		}
		while (isFishingFrameExist()) {
			waitForBlink();
			act(analyze());
		}
		logger.info("finished fishing");
	}

	private boolean isNightTime()    //test it when nighttime support is added
	{
		DateTime currentDateTime = DateTime.now();
		int currentHour = currentDateTime.getHourOfDay();
		if (currentHour % 4 == 2) {
			return true;
		} else {
			return false;
		}
	}

	private void waitForBlink()
	{
		logger.trace(".waitForBlink");
		int timePassed = 0;
		try {
			Thread.sleep(300);    //yes.. my bot is too fast fot this game
		} catch (InterruptedException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		while (!(colorsAreClose(getAbsPixelColor(this.blinkControlPoint), this.colorControlBlue, threshold)
				 ||
				 colorsAreClose(getAbsPixelColor(this.blinkControlPoint), this.colorControlOrange, threshold)))
		{

			try {
				timePassed += timeToSleepMillis;
				Thread.sleep(timeToSleepMillis);
			} catch (InterruptedException e) {
				logger.error("sleep in waitForBlink was interrupted for some reason");
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
			if (timePassed > 3000 || !isFishingFrameExist()) {
				return;
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
				Thread.sleep(timeToSleepMillis);
			} catch (InterruptedException e) {
				logger.error("sleep in waitForFishHP was interrupted for some reason");
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
//			logger.debug("conditions for exit: time(10s)" + (timePassed < 10000));
			if (!isFishingFrameExist()) {
				return;
			}
		}
		while (!(timePassed > 10000
				 &&
				 (colorsAreClose(getAbsPixelColor(this.blinkControlPoint), this.colorControlBlue, threshold)
				  ||
				  colorsAreClose(getAbsPixelColor(this.blinkControlPoint), this.colorControlOrange, threshold))));
		logger.info("Fish HP bar finally appeared");
	}

	private boolean isFishingFrameExist()
	{
//		logger.trace(".isFishingFrameExist");
		if (colorsAreClose(getAbsPixelColor(this.controlFrameCoordinate), this.colorControlFrame, threshold)) {
//			logger.debug("yes. exists");
			return true;
		} else {
//			logger.debug("no. not exists");
			return false;
		}
	}

	private void act(boolean reel)
	{
		if (reel) {
			keyClickStatic(KeyEvent.VK_NUMPAD4);
			lastKeyPressed=KeyEvent.VK_NUMPAD4;
		} else {
			keyClickStatic(KeyEvent.VK_NUMPAD3);
			lastKeyPressed=KeyEvent.VK_NUMPAD3;
		}
		logger.info(".act " + reel);

	}


	private boolean analyze()
	{
		logger.trace(".analyze");
		if (this.firstAnalysis) {
			workingPoint = new Point(this.leftmostBluePixelCoordinate);
			firstAnalysis = false;
		}


		boolean stayingOnPositive;
		Color gotColor = getAbsPixelColor(workingPoint);
		if (colorsAreClose(gotColor, this.colorHpBlue, threshold)
			||
			colorsAreClose(gotColor, this.colorHpOrange, threshold))
		{
			stayingOnPositive = true;
			logger.debug("moving to the right");
		} else if (colorsAreClose(gotColor, this.colorHpBlueNegative, threshold)
				   ||
				   colorsAreClose(gotColor, this.colorHpOrangeNegative, threshold))
		{
			stayingOnPositive = false;
			logger.debug("moving to the left");
		} else {
			logger.error("Found invalid color in analyze: " + gotColor + " at point " + workingPoint);
			stayingOnPositive = false; //not very kind-hearted to let it go
		}

		if (stayingOnPositive) {
			while (colorsAreClose(getAbsPixelColor(workingPoint), this.colorHpBlue, threshold)
				   ||
				   colorsAreClose(getAbsPixelColor(workingPoint), this.colorHpOrange, threshold))
			{
				workingPoint.x++;
			}
		} else {
			boolean movedLeft = false;
			while (!
				   (colorsAreClose(getAbsPixelColor(workingPoint), this.colorHpBlue, threshold)
					||
					colorsAreClose(getAbsPixelColor(workingPoint), this.colorHpOrange, threshold)))
			{
				workingPoint.x--;
				if (workingPoint.x <= this.leftmostBluePixelCoordinate.x) {
					workingPoint.x = this.leftmostBluePixelCoordinate.x;
					break;
				}
				movedLeft = true;
			}
			if (movedLeft) {
				workingPoint.x++;
			}

		}

		int timePassed = 0;
		while (timePassed < this.timeToWaitMillis) {
			if (colorsAreClose(getAbsPixelColor(workingPoint), this.colorHpBlue, threshold) ||
				colorsAreClose(getAbsPixelColor(workingPoint), this.colorHpOrange, threshold))
			{
				return true;    //do the reeling
			}
			timePassed += timeInLoopDelayInAnalyze;
			logger.debug("now performing sleep for ms: " + timeInLoopDelayInAnalyze);
			World.easySleep(timeInLoopDelayInAnalyze);
		}

		return false;//pumping

	}

	private Point findBar()
	{
		logger.trace(".findBar");
		WinAPIAPI.showMessage("Wlcome to fishing. Move mouse UNDER fish hp bar");
		Point currentPoint = WinAPIAPI.getMousePos();
		logger.debug("got mouse position at "+currentPoint);
		while (!(colorsAreClose(getAbsPixelColor(currentPoint), this.colorHpBlue, threshold)
				 ||
				 colorsAreClose(getAbsPixelColor(currentPoint), this.colorHpOrange, threshold)))
		{
			currentPoint.y--;
		}
		logger.debug("found bar's Y at " + currentPoint.y);
		while (colorsAreClose(getAbsPixelColor(currentPoint), this.colorHpBlue, threshold)
			   ||
			   colorsAreClose(getAbsPixelColor(currentPoint), this.colorHpOrange, threshold))
		{
			currentPoint.x--;
		}
		currentPoint.x++;
		logger.debug("found bar's X at " + currentPoint.x);
		logger.info("Sucessfully found fish bar");
		return currentPoint;

	}


	public Fisher()
	{

		this.leftmostBluePixelCoordinate = findBar();
		this.controlFrameCoordinate = new Point(this.leftmostBluePixelCoordinate.x, this.leftmostBluePixelCoordinate.y + 40);
		this.blinkControlPoint = new Point(this.leftmostBluePixelCoordinate.x + 1, this.leftmostBluePixelCoordinate.y + 3);
		this.failTime=System.currentTimeMillis();
	}

}
