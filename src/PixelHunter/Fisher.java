package PixelHunter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

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
	firstFish                         = true,
	firstAnalysis                     = true,
	finishedWaitingForPumpingDecision = false,
	needToChangeLure = false;

	private Timer timerWaitForPumpingSolution = new Timer();

	private static final Timer timerNightReminder = new Timer();

	private final TimerTask taskTryNighMode = new TimerTask()
	{
		@Override
		public void run()
		{
			if (!nightMode) {
				Fisher.this.needToChangeLure = true;
			}
		}
	};

	private static final int
	timeToWaitMillis         = 1250,//how much time  to wait before act
	timeToSleepMillis        = 200,//passed to sleep method
	timeInLoopDelayInAnalyze = 20,
	timeSkillsReuse          = 1700,    //watch it
	deltaX                   = 3;

	private final Point
	leftmostBluePixelCoordinate,
	controlFrameCoordinate;

	private final int threshold = 10;    //default is 4

	private int
	numberOfFAilsInARow = 0,
	lastKeyPressed      = 0;

	private long
	lastPumpingTime = System.currentTimeMillis(),
	lastReelingTime = System.currentTimeMillis();


	private void checkForDisconnect()
	{
		if (this.lastKeyPressed == KeyEvent.VK_NUMPAD2) {
			numberOfFAilsInARow++;
		} else {
			numberOfFAilsInARow = 0;
		}
		if (numberOfFAilsInARow >= 20) {
			if (nightMode) {
				needToChangeLure = true;
				numberOfFAilsInARow = 0;    //it's not night now
			} else {
				exit(1);
			}
		}

	}

	public void infiniteFish()
	{
		while (true) {
			fish();
			checkForDisconnect();

			if (needToChangeLure) {
				needToChangeLure = false;
				if (nightMode) {
					L2Window.keyClickStatic(KeyEvent.VK_NUMPAD7);    //day lure
					logger.info("changing back to day mode");
					nightMode = false;

				} else {
					Fisher.logger.info("trying to switch to nightmode");
					L2Window.keyClickStatic(KeyEvent.VK_NUMPAD8);    //night lure
					nightMode = true;
				}
				World.easySleep(600);
			}
		}

	}

	public void fish()
	{
		logger.trace(".fish");

		if (!isFishingFrameExist()) {    //we killed the frame. correcting the mistake
			keyClickStatic(KeyEvent.VK_NUMPAD2);
			this.lastKeyPressed = KeyEvent.VK_NUMPAD2;
			logger.info("Throwing a bait");
			World.easySleep(500);    //or he may cancel it immediately
		}

		this.firstAnalysis = true;
		if (this.firstFish) {
			this.firstFish = false;
		} else {
			waitForFishHp();
		}
		boolean analyzeResult;
		long timeSkillsReuseLeft;
		while (isFishingFrameExist()) {
//			logger.trace("frame exists. next analyze after wait for blink");
			waitForBlink();
			analyzeResult = analyze();

			if (analyzeResult) {    //reeling
				timeSkillsReuseLeft = System.currentTimeMillis() - this.lastReelingTime;
				if (timeSkillsReuseLeft < timeSkillsReuse) {
					timerWaitForPumpingSolution.cancel();
					timerWaitForPumpingSolution = new Timer();
				}
			} else {    //pumping
				timeSkillsReuseLeft = System.currentTimeMillis() - this.lastPumpingTime;
				if (timeSkillsReuseLeft < timeSkillsReuse) {
					timerWaitForPumpingSolution.cancel();
					timerWaitForPumpingSolution = new Timer();
					analyzeResult = analyze(timeSkillsReuseLeft * 2);
				}
			}
			act(analyzeResult);
			timerWaitForPumpingSolution.cancel();
			timerWaitForPumpingSolution = new Timer();
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
//		logger.trace(".waitForBlink");
		int timePassed = 0;
//		World.easySleep(800);
		while (!(colorsAreClose(getAbsPixelColor(this.blinkControlPoint), this.colorControlBlue, threshold)
				 ||
				 colorsAreClose(getAbsPixelColor(this.blinkControlPoint), this.colorControlOrange, threshold)))
		{


			timePassed += timeToSleepMillis;
			World.easySleep(timeToSleepMillis);

			if (timePassed > 2500 || !isFishingFrameExist()) {
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
			lastKeyPressed = KeyEvent.VK_NUMPAD4;
			this.lastReelingTime = System.currentTimeMillis();
		} else {
			keyClickStatic(KeyEvent.VK_NUMPAD3);
			lastKeyPressed = KeyEvent.VK_NUMPAD3;
			this.lastPumpingTime = System.currentTimeMillis();
		}
		logger.info(".act " + reel);
		World.easySleep(160); //at least ping-sleep. or it becomes too fast

	}

	private boolean analyze()
	{
		return analyze(this.timeToWaitMillis);
	}

	private boolean analyze(long timeToWaitForPumping)
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

		gotColor = getAbsPixelColor(workingPoint);
		Color blinkControlColor = getAbsPixelColor(blinkControlPoint);
		if (stayingOnPositive) {
			while (colorsAreClose(gotColor, this.colorHpBlue, threshold)
				   ||
				   colorsAreClose(gotColor, this.colorHpOrange, threshold))
			{

				if (colorsAreClose(blinkControlColor, this.colorControlBlue, threshold) || colorsAreClose(blinkControlColor, this.colorControlOrange, threshold)) {
					workingPoint.x += deltaX;
				} else {
					if (isFishingFrameExist()) {
						waitForBlink();
					}
				}
				gotColor = getAbsPixelColor(workingPoint);
				blinkControlColor = getAbsPixelColor(blinkControlPoint);

			}
		} else {

			boolean movedLeft = false;
			while (!
				   (colorsAreClose(gotColor, this.colorHpBlue, threshold)
					||
					colorsAreClose(gotColor, this.colorHpOrange, threshold)))
			{
				if (colorsAreClose(blinkControlColor, this.colorControlBlue, threshold) || colorsAreClose(blinkControlColor, this.colorControlOrange, threshold)) {
					workingPoint.x -= deltaX;
				} else {
					if (isFishingFrameExist()) {
						waitForBlink();
					}
				}
				if (!isFishingFrameExist()) {
					return false;
				}
				if (workingPoint.x <= this.leftmostBluePixelCoordinate.x + deltaX) {
//					workingPoint.x += deltaX;
					break;
				}
				gotColor = getAbsPixelColor(workingPoint);
				blinkControlColor = getAbsPixelColor(blinkControlPoint);
				movedLeft = true;
			}
			if (movedLeft) {
				workingPoint.x += deltaX;
			}

		}

//		int timePassed = 0;
		this.finishedWaitingForPumpingDecision = false;
		this.timerWaitForPumpingSolution.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				Fisher.this.finishedWaitingForPumpingDecision = true;
//				logger.debug("Timer task: setting finished for pumping to true");
			}
		}, timeToWaitForPumping);
//		while (timePassed < this.timeToWaitMillis) {
		while (!this.finishedWaitingForPumpingDecision) {
			waitForBlink();
			if (colorsAreClose(getAbsPixelColor(workingPoint), this.colorHpBlue, threshold)
				||
				colorsAreClose(getAbsPixelColor(workingPoint), this.colorHpOrange, threshold))
			{
				return true;    //do the reeling
			}
//			timePassed += timeInLoopDelayInAnalyze;
//			logger.debug("checking for pumping decision");
//			World.easySleep(timeInLoopDelayInAnalyze);
		}

		return false;//pumping

	}

	private Point findBar()
	{
		logger.trace(".findBar");
		WinAPIAPI.showMessage("Wlcome to fishing. Move mouse UNDER fish hp bar");
		Point currentPoint = WinAPIAPI.getMousePos();
		logger.debug("got mouse position at " + currentPoint);
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
//		this.failTime=System.currentTimeMillis();
		timerNightReminder.schedule(taskTryNighMode, 1 * 60 * 1000, 10 * 60 * 1000);//once every ten minutes, first try in one minute after start
	}

	public void setSchedule(String time)
	{
		sdfc

	}
}
