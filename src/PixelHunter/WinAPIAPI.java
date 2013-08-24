package PixelHunter;


import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.PointerByReference;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class WinAPIAPI
{
	private static class Psapi
	{
		static {
			Native.register("psapi");
		}

		public static native int GetModuleBaseNameW(Pointer hProcess, Pointer hmodule, char[] lpBaseName, int size);
	}


	private static class Kernel32
	{
		static {
			Native.register("kernel32");
		}

		public static int PROCESS_QUERY_INFORMATION = 0x0400;
		public static int PROCESS_VM_READ           = 0x0010;

		public static native int GetLastError();

		public static native Pointer OpenProcess(int dwDesiredAccess, boolean bInheritHandle, Pointer pointer);
	}


	public static class User32DLL
	{
		static {
			Native.register("user32");
		}

		public static native int GetWindowThreadProcessId(WinDef.HWND hWnd, PointerByReference pref);

		public static native WinDef.HWND GetForegroundWindow();

		public static native int GetWindowTextW(WinDef.HWND hWnd, char[] lpString, int nMaxCount);
	}

	public static void setWindowPos(WinDef.HWND hwnd, int x, int y, int w, int h)
	{
		User32.INSTANCE.SetWindowPos(hwnd, hwnd, x, y, w, h, 0x0004);
	}


	public static void showMessage(String s)
	{//REDO!!
		System.out.println("Non-timed out message window: " + s);
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	public static void showMessage(String s, int t)
	{//REDO todo
		System.out.println("Timed out message window: " + s);
		try {
			TimeUnit.SECONDS.sleep(t);
		} catch (InterruptedException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			WinAPIAPI.showMessage("WARNING: some stupid caused interruption of sleeping. you should find out about the cause!!");
		}
	}

	public static void toolTip(String s, int x, int y)
	{    //REDO	todo
		System.out.println(s);
	}

	public static Point getMousePos()
	{
		return new Point((int) MouseInfo.getPointerInfo().getLocation().getX(), (int) MouseInfo.getPointerInfo().getLocation().getY());
	}

}
