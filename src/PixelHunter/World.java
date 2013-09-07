package PixelHunter;


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
	private static ProcessIdentifier processIdentifier;
	private static ArrayList<HWND>   hwnds;
	private static boolean     singleWindowMode = false;
	private static Character[] characters       = new Character[2];
	public static L2Window l2Window;


	public static void main(String[] args)
	{
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");

		//WELCOME MESSAGE TODO would suit here. also, gui would be just nice
		ArrayList<String> argumentsList = new ArrayList<String>(Arrays.asList(args));
		GroupedVariables groupedVariables = new GroupedVariables();
		processIdentifier = new ProcessIdentifier();
		hwnds = ProcessIdentifier.getL2HwndArray();


		if (argumentsList.contains("-f")) {    //fishing
			Fisher fisher = new Fisher(new L2Window(hwnds.get(0)));
			fisher.infiniteFish();
			return;
		}


		System.out.println("World found quantity of proper windows: " + hwnds.size());
		int id = 0;
		if (hwnds.size() == 1){
			singleWindowMode = true;
		}   else {
			singleWindowMode = false;
		}

		singleWindowMode	=	true;//remove this after any tests are over

		if (singleWindowMode) {

			L2Window.initiateSize(0, hwnds.get(0));    //yes, static method access, not the class representative
			//0=fully on the screen
			id = WinAPIAPI.dialogWindow("You can resize the window now in case you are not satisfied with its dimensions.\nEnter Character ID in the right window");
			characters[1] = CharacterFactory.getCharacter(id, hwnds.get(0));

		} else {	//todo change hwnds order: first is left. if one can do

			WinAPIAPI.bringToFront(hwnds.get(0));
			L2Window.initiateSize(1, hwnds.get(0));    //1=left on the screen
			id = WinAPIAPI.dialogWindow("You can resize the window now in case you are not satisfied with its dimensions.\nEnter Character ID in the left window");
			characters[0] = CharacterFactory.getCharacter(id, hwnds.get(0));
			System.out.println(characters[0].pet.getHP());//todo delete it if you see it

			WinAPIAPI.bringToFront(hwnds.get(1));
			L2Window.initiateSize(2, hwnds.get(1));    //2=right on the screen
			id = WinAPIAPI.dialogWindow("You can resize the window now in case you are not satisfied with its dimensions.\nEnter Character ID in the right window");
			characters[1] = CharacterFactory.getCharacter(id, hwnds.get(1));
			WinAPIAPI.showMessage("now getHP for pet");
			System.out.println(characters[1].pet.getHP());//todo delete it if you see it
		}

		while (characters.length>0){
			characters[1].chatReact();
		}



		return;
	}

	static {
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
	}
}
