package PixelHunter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import static PixelHunter.L2Window.*;
import static java.lang.System.exit;

/**
 * User: mrk
 * Date: 9/6/13; Time: 2:11 AM
 */


public class Fisher
{
	private static final Logger logger = LoggerFactory.getLogger(Fisher.class);

	private L2Window l2Window;
	private static final Color
	colorHpBlueNegative         = new Color(53, 33, 26),
	colorHpOrangeNegative       = new Color(36, 14, 12),
	colorHpBlue                 = new Color(7, 103, 159),//(12, 104, 156),
	colorHpOrange               = new Color(144, 36, 7),
	colorControlFrame1          = new Color(178, 163, 141),
	colorControlFrame2          = new Color(97, 77, 59),
	colorControlOrange          = new Color(195, 55, 20),
	colorControlBlue            = new Color(0, 175, 246),
	colorFeedbackActionSuccess  = new Color(0, 255, 0),
	colorFeedbackActionFail     = new Color(123, 125, 66),
	colorFeedbackActionResist   = new Color(173, 154, 123),
	colorFeedbackFishingSuccess = new Color(255, 255, 0),
	colorFeedbackFishingFail    = new Color(255, 0, 123);
	private final Color colorControlFrame;

	private static boolean nightMode = false;

	private Point
	workingPoint,
	workingPoint1,
	feedbackControlPoint;

	private boolean
	firstFish                         = true,
	firstAnalysis                     = true,
	finishedWaitingForPumpingDecision = false,
	needToChangeLure                  = false,
	loggerToTheRight                  = false;

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
	//how much time  to wait before act
	timeToSleepMillis        = 200,//passed to sleep method
	timeInLoopDelayInAnalyze = 20,
	timeSkillsReuse          = 1500,    //watch it
	deltaX                   = 3;

	private static final long
	ingameTimeFishingStartsMilliseconds = (6 * 10) * 1000,
	ingameTimeFishingEndsMilliseconds   = (6 * 10 * 60 - 6 * 10) * 1000,
	ingameTime24HoursLengthMilliseconds = (4 * 60 * 60) * 1000;

	private final Point
	leftmostBluePixelCoordinate,
	controlFrameCoordinate,
	blinkControlPoint,
	manaControlPoint;

	private final int threshold = 10;    //default is 4

	private int
	ping                = 80,        //can be changed
	timeToWaitMillis    = 1350,    //can be changed
	numberOfFAilsInARow = 0,
	lastKeyPressed      = 0,
	currentFailLimit    = 0;

	private int[] numberOfFailsAllowed = new int[] {3, 20};

	private long
	lastPumpingTime = System.currentTimeMillis(),
	lastReelingTime = System.currentTimeMillis();

	private final  Color manaControlColor;
	private static Color workingColor;

	PrintWriter fileHandle;

	public void setFeedbackObtainingPosition()
	{
		logger.trace(".setFeedbackObtainingPosition");
		WinAPIAPI.showMessage("Mouse at feedback control point");
		this.feedbackControlPoint = WinAPIAPI.getMousePos();
	}

	public void setTimeToWaitForPumping(int newTime)
	{
		this.timeToWaitMillis = newTime;
	}

	public void setPing(int newPing)
	{
		this.ping = newPing;
	}


	public void finishFishing()
	{
		logger.info("Finishing fishing for some reason");
		fileHandle.close();
		exit(0);
	}

	private int analyzeFeedback()
	{
		logger.trace(".analyzeFeedback()");

		workingPoint1 = new Point(feedbackControlPoint);
		for (int i = 0; i <= 30; i++) {
			workingColor = L2Window.getAbsPixelColor(workingPoint1);
			if (L2Window.colorsAreClose(colorFeedbackActionSuccess, workingColor)) {
				return 1;        //everything's OK
			}
			if (L2Window.colorsAreClose(colorFeedbackActionFail, workingColor)) {
				return 2;        //wrong button
			}
			if (L2Window.colorsAreClose(colorFeedbackActionResist, workingColor)) {
				workingPoint1.y -= 15;
				workingPoint1.x = feedbackControlPoint.x;
				for (int j = 0; j < 30; j++) {
					workingColor = L2Window.getAbsPixelColor(workingPoint1);
					if (L2Window.colorsAreClose(colorFeedbackFishingSuccess, workingColor)) {
						return 4;        //did the job
					}
					if (L2Window.colorsAreClose(colorFeedbackFishingFail, workingColor)) {
						return 5;        //needs improvements
					}
					if (L2Window.colorsAreClose(colorFeedbackActionResist, workingColor)) {    //actually it is not really resist
						return 3;        //resisted attempt or reeling too early
					}
					workingPoint1.x++;
				}
				return 0;
			}

			workingPoint1.x++;
		}
		return 0;
	}


	private void checkForDisconnect()
	{
		if (nightMode) {
			currentFailLimit = numberOfFailsAllowed[0];
		} else {
			currentFailLimit = numberOfFailsAllowed[1];
		}

		if (this.lastKeyPressed == KeyEvent.VK_NUMPAD2) {
			numberOfFAilsInARow++;
		} else {
			numberOfFAilsInARow = 0;
		}

		if (numberOfFAilsInARow >= currentFailLimit) {
			if (nightMode) {
				needToChangeLure = true;
				numberOfFAilsInARow = 0;    //it's not night now
			} else {
				finishFishing();
			}
		}

	}

	public void infiniteFish()
	{
		while (true) {
			fish();
			checkForDisconnect();
			waitForMana();
			if (needToChangeLure) {
				needToChangeLure = false;
				if (nightMode) {
					L2Window.keyClickStatic(KeyEvent.VK_NUMPAD7);    //day lure
					logger.info("changing back to day mode");
					nightMode = false;

				} else {
					Fisher.logger.info("switching to nightmode");
					L2Window.keyClickStatic(KeyEvent.VK_NUMPAD8);    //night lure
					nightMode = true;
				}
				World.easySleep(600);
			}
		}

	}

	private void waitForMana()
	{

		logger.trace(".waitForMana");
		int count = 0;
		while (!colorsAreClose(manaControlColor, getAbsPixelColor(manaControlPoint))) {
			if (count > 20) {
				finishFishing();
			}
			WinAPIAPI.showMessage("Waiting for mana regeneration(20 for exit): " + count, 10);
			count++;

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
					analyzeResult = analyze(timeSkillsReuse - timeSkillsReuseLeft);
				}
			}
			act(analyzeResult);
			World.easySleep(this.ping * 2 + 20); //at least double-ping-sleep. or it becomes too fast
			int feedback =this.analyzeFeedback();
			fileHandle.print(this.loggerToTheRight + "\r\n" + System.currentTimeMillis() + "\t" + analyzeResult + "\t" + this.workingPoint.x + "\t" + feedback + "\t");
			if (feedback==5||feedback==4){	//watch it. remove after fail
				keyClickStatic(KeyEvent.VK_NUMPAD2); //next fishing act
			}
			timerWaitForPumpingSolution.cancel();
			timerWaitForPumpingSolution = new Timer();
		}

		logger.info("finished fishing");
	}


	private void waitForBlink()
	{
		int timePassed = 0;
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
		if (colorsAreClose(getAbsPixelColor(this.controlFrameCoordinate), colorControlFrame, threshold)) {
			return true;
		} else {
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
			this.loggerToTheRight = true;
			logger.debug("moving to the right");
		} else if (colorsAreClose(gotColor, this.colorHpBlueNegative, threshold)
				   ||
				   colorsAreClose(gotColor, this.colorHpOrangeNegative, threshold))
		{
			stayingOnPositive = false;
			this.loggerToTheRight = false;
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
					} else {
						return true;    //and go away
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
					movedLeft = true;
				} else {
					if (isFishingFrameExist()) {
						waitForBlink();
						if (!(colorsAreClose(blinkControlColor, this.colorControlBlue, threshold) || colorsAreClose(blinkControlColor, this.colorControlOrange, threshold))) {//bar is empty, fish needs last shot
							return false;//pump it and it's yours
						}
					} else {
						return false;    //and go away again//watch it. probably a source of bad behav
					}
				}

				if (workingPoint.x <= this.leftmostBluePixelCoordinate.x) {
//					workingPoint.x += deltaX;
					break;
				}
				gotColor = getAbsPixelColor(workingPoint);
				blinkControlColor = getAbsPixelColor(blinkControlPoint);

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
		WinAPIAPI.showMessage("Welcome to fishing. Move mouse UNDER fish hp bar");
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

	public void setSchedule(String currentIngameTime)
	{

		logger.trace(".setSchedule(); with " + currentIngameTime);

		long ingameCurrentTimeFromDayStartMilliseconds = Integer.parseInt(currentIngameTime.substring(0, 2)) * 10 * 60 * 1000 + Integer.parseInt(currentIngameTime.substring(2, 4)) * 10 * 1000;
		logger.debug("got ingame time " + Integer.parseInt(currentIngameTime.substring(0, 2)) + " " + Integer.parseInt(currentIngameTime.substring(2, 4)));

		if (ingameCurrentTimeFromDayStartMilliseconds > ingameTimeFishingStartsMilliseconds
			&&
			ingameCurrentTimeFromDayStartMilliseconds < ingameTimeFishingEndsMilliseconds)
		{
			taskTryNighMode.run();
			logger.debug("set nightmode to now");
		}

		long nextNoghtModeSwitchMilliseconds = (ingameTime24HoursLengthMilliseconds - ingameCurrentTimeFromDayStartMilliseconds + ingameTimeFishingStartsMilliseconds) % ingameTime24HoursLengthMilliseconds; //not lol. this should work just perfectly
		timerNightReminder.schedule(taskTryNighMode, nextNoghtModeSwitchMilliseconds, ingameTime24HoursLengthMilliseconds);
		logger.debug("next nightmode switch expected in " + String.valueOf(nextNoghtModeSwitchMilliseconds / 1000 / 60 / 60) + " hours " + String.valueOf((nextNoghtModeSwitchMilliseconds / 1000 / 60) % 60) + " minutes");

	}


	public Fisher()
	{
		try {
			fileHandle = new PrintWriter("resources\\" + String.valueOf(System.currentTimeMillis()), "UTF-8");
			fileHandle.println("System time\tAnalyze result\tCoordinate\tFeedback code\tMoving to the right");
		} catch (FileNotFoundException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

		WinAPIAPI.showMessage("Mouse at mana control point");
		this.manaControlPoint = WinAPIAPI.getMousePos();
		this.manaControlColor = L2Window.getAbsPixelColor(this.manaControlPoint);

		this.setFeedbackObtainingPosition();

		this.leftmostBluePixelCoordinate = findBar();
		this.controlFrameCoordinate = new Point(this.leftmostBluePixelCoordinate.x, this.leftmostBluePixelCoordinate.y + 35);

		Color colorAssumedFrame = Color.BLACK;
		boolean failInFindFrame = true;

		for (int i = 0; i < 70; i++) {
			this.controlFrameCoordinate.y = this.leftmostBluePixelCoordinate.y + 35 + i;
			if (colorsAreClose(getAbsPixelColor(this.controlFrameCoordinate), colorControlFrame1)) {
				logger.debug("control frame pixel is light");
				colorAssumedFrame = colorControlFrame1;
				failInFindFrame = false;
				break;
			} else if (colorsAreClose(getAbsPixelColor(this.controlFrameCoordinate), colorControlFrame2)) {
				logger.debug("control frame pixel is dark");
				colorAssumedFrame = colorControlFrame2;
				failInFindFrame = false;
				break;
			}
		}
		colorControlFrame = colorAssumedFrame;
		if (failInFindFrame) {
			logger.error("failed to calibrate in find window border. exiting");
			finishFishing();
		}
		this.blinkControlPoint = new Point(this.leftmostBluePixelCoordinate.x + 1, this.leftmostBluePixelCoordinate.y + 3);

	}

}
