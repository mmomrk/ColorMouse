package PixelHunter;


import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.*;

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
	private        HWND[]            hwnds;
	public static  L2Window          l2Window;

	public static void main(String[] args)
	{

		//WELCOME MESSAGE TODO would suit here. also, gui would be just nice
		l2Window = new L2Window();

		processIdentifier = new ProcessIdentifier();
		WinDef.HWND[] hwnds = new HWND[2];
		hwnds = processIdentifier.getL2HwndArray();

		System.out.println(hwnds);
		l2Window.hwnd = hwnds[0];
		l2Window.moveResize(-8, -16, 508, 800);
		Pet testPet = new Pet();
		testPet.setHP();

	}
}
