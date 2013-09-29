package PixelHunter;


import PixelHunter.HotKeysByTulskiy.HotKeyHandler;
import com.sun.jna.platform.win32.WinDef.HWND;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created with IntelliJ IDEA.
 * User: mrk
 * Date: 8/21/13
 * Time: 5:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class World
{
	private static int currentCharacterNumber = 0;
	private static ProcessIdentifier processIdentifier;
	private static ArrayList<HWND>   hwnds;
	private static boolean     singleWindowMode = false;
	private static Character[] characters       = new Character[2];

	public static void main(String[] args)
	{
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");

		HotKeyHandler hotKeyHandler = new HotKeyHandler();

		//WELCOME MESSAGE TODO would suit here. also, gui would be just nice
		ArrayList<String> argumentsList = new ArrayList<String>(Arrays.asList(args));
		GroupedVariables groupedVariables = new GroupedVariables();
		processIdentifier = new ProcessIdentifier();
		hwnds = ProcessIdentifier.getL2HwndArray();

		if (argumentsList.contains("-f")
			||
			argumentsList.contains("--fishing"))
		{    //fishing
			Fisher fisher = new Fisher(new L2Window(hwnds.get(0)));
			fisher.infiniteFish();
			return;
		}
		if (argumentsList.contains("-t")
			||
			argumentsList.contains("--talktome"))
		{    //talk to me mode
			GroupedVariables.Mediator.talkToMeMode = true;
		}
		if (argumentsList.contains("-np")
			||
			argumentsList.contains("--nopetmode")){
			GroupedVariables.Mediator.noPetMode=true;
		}

		int id = 0;
		if (hwnds.size() == 1) {
			singleWindowMode = true;
		} else {
			singleWindowMode = false;
		}

		singleWindowMode = true;//remove this after any tests are over

		if (singleWindowMode) {

			L2Window.initiateSize(0, hwnds.get(0));    //yes, static method access, not the class representative
			//0=fully on the screen
			WinAPIAPI.setActiveWindow(hwnds.get(0));
			id = WinAPIAPI.dialogWindow(0);
			characters[0] = CharacterFactory.getCharacter(id, hwnds.get(0));

		} else {    //todo change hwnds order: first is left. if one can do

			WinAPIAPI.setActiveWindow(hwnds.get(0));        //not implemented yet
			L2Window.initiateSize(1, hwnds.get(0));    //1=left on the screen
			id = WinAPIAPI.dialogWindow(1);
			characters[0] = CharacterFactory.getCharacter(id, hwnds.get(0));

			WinAPIAPI.setActiveWindow(hwnds.get(1));
			L2Window.initiateSize(2, hwnds.get(1));    //2=right on the screen
			id = WinAPIAPI.dialogWindow(2);
			characters[1] = CharacterFactory.getCharacter(id, hwnds.get(1));
		}

		if ((characters[0].id == GroupedVariables.ProjectConstants.ID_Swordsinger
			 ||
			 characters[0].id == GroupedVariables.ProjectConstants.ID_Bladedancer)
			&&
			(characters[1].id == GroupedVariables.ProjectConstants.ID_Swordsinger
			 ||
			 characters[1].id == GroupedVariables.ProjectConstants.ID_Bladedancer))
		{
			GroupedVariables.Mediator.BDSWSInDaHouse = true;
		} else {
			GroupedVariables.Mediator.BDSWSInDaHouse = false;
		}

		{
			while (characters.length > 0) {    //for the pure debug purpose
				if (GroupedVariables.Mediator.sleepRegime) {
					easySleep(400);
				} else {
					characters[0].lifeCycle();
				}
			}
		}



		return;
	}

	public static void easySleep(int timeMillis){
		try {
			Thread.sleep(timeMillis);
		} catch (InterruptedException e) {

			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	public static void pauseWorld(){
		GroupedVariables.Mediator.sleepRegime=true;
		WinAPIAPI.showMessage("You have pressed pause. press OK to unpause. do not press close");
	}

	public static void BDSWSBuff()    //implement todo
	{

		if (GroupedVariables.Mediator.BDSWSInDaHouse) {
			characters[0].nowWeWillSingDance();
			characters[1].nowWeWillSingDance();
			int bDPosition, sWSPosition;
			if (characters[0].id == GroupedVariables.ProjectConstants.ID_Bladedancer) {
				bDPosition = 0;
				sWSPosition = 1;
			} else {
				bDPosition = 1;
				sWSPosition = 0;
			}

			boolean finished = false;
			boolean bDFinished = false, sWSFinished = false;
			int i = 0;
			while (!finished) {
				if (i % 2 == 0) {
					bDFinished = characters[bDPosition].nextSongDance();
				} else {
					sWSFinished = characters[sWSPosition].nextSongDance();
				}
				finished = bDFinished && sWSFinished;
				i++;
			}
		} else {
			int bDSWSPosition;
			if (characters[0].id == GroupedVariables.ProjectConstants.ID_Bladedancer
				||
				characters[0].id == GroupedVariables.ProjectConstants.ID_Swordsinger)
			{
				bDSWSPosition = 0;
			} else if (characters[1].id == GroupedVariables.ProjectConstants.ID_Bladedancer
					   ||
					   characters[1].id == GroupedVariables.ProjectConstants.ID_Swordsinger)
			{
				bDSWSPosition = 1;
			} else {
				WinAPIAPI.showMessage("invalid BDSWS call", 5);
				return;
			}
			characters[bDSWSPosition].nowWeWillSingDance();
			while (!characters[bDSWSPosition].nextSongDance()) {

			}

		}
	}

	public static void forceRebuff()
	{
		characters[0].forceRebuff();
		if (!singleWindowMode) {
			characters[1].forceRebuff();
		}
	}

	public static void deactivateFarm()
	{
		characters[(0)].deactivateModeFarm();
		if (!singleWindowMode) {
			characters[(1)].deactivateModeFarm();
		}
	}

	public static void activateFarm()
	{
		characters[(0)].activateModeFarm();
		if (!singleWindowMode) {
			characters[(1)].activateModeFarm();
		}
	}

	public static void deactivateBuff()
	{
		characters[(0)].deactivateModeBuff();
		if (!singleWindowMode) {
			characters[(1)].deactivateModeBuff();
		}
	}

	public static void activateBuff()
	{
		characters[(0)].activateModeBuff();
		if (!singleWindowMode) {
			characters[(1)].activateModeBuff();
		}
	}

	public static void deactivateHomeRun()
	{
		characters[(0)].deactivateModeHomeRun();
		if (!singleWindowMode) {
			characters[(1)].deactivateModeHomeRun();
		}
	}

	public static void activateHomeRun()
	{
		characters[(0)].activateModeHomeRun();
		if (!singleWindowMode) {
			characters[(1)].activateModeHomeRun();
		}
	}


	public static int getCurrentCharacterNumber()
	{
		return currentCharacterNumber;
	}

	static {
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
	}

}
