package PixelHunter;


import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.PointerByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.TimerTask;

//import static PixelHunter.WinAPIAPI.User32DLL.GetWindowRect;


public class WinAPIAPI
{

	private static final Logger logger = LoggerFactory.getLogger(WinAPIAPI.class);


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


	interface MyListener extends WinDef.StdCallCallback
	{
		WinDef.LRESULT callback(WinDef.HWND hWnd, int uMsg, WinDef.WPARAM uParam, WinDef.LPARAM lParam);
	}


	public static class User32DLL
	{
		static {
			Native.register("user32");
		}

		public static native int GetWindowThreadProcessId(WinDef.HWND hWnd, PointerByReference pref);

		public static native WinDef.HWND GetForegroundWindow();

		public static native int GetWindowTextW(WinDef.HWND hWnd, char[] lpString, int nMaxCount);

		public static native int SetWindowLong(WinDef.HWND hWnd, int nIndex, Callback callback);

//		public static native boolean GetWindowRect(WinDef.HWND hwnd, WinDef.RECT rect);

	}


	public static WinDef.RECT getWindowRect(WinDef.HWND hwnd)
	{
		WinDef.RECT returnRectangle = new WinDef.RECT();
		boolean b = User32.INSTANCE.GetWindowRect(hwnd, returnRectangle);
		return returnRectangle;
	}

	public static void bringToFront(WinDef.HWND hwnd)
	{    //todo. needs to todo
		return;
	}

	public static int dialogWindow(String s)
	{//todo: very much todo
		System.out.println(s);
		int answer;
		try {
			answer = System.in.read();
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			answer = -1;
		}
		return answer;
	}

	public static void setWindowPos(WinDef.HWND hwnd, int x, int y, int w, int h)
	{
		User32.INSTANCE.SetWindowPos(hwnd, hwnd, x, y, w, h, 0x0004);
	}


	public static void showMessage(String s)
	{
//       InfoFrame frame= new InfoFrame(s);
//       frame.setVisible(true);
//       frame.pack();
		System.out.println("Non-timed-out info frame. Text:\n" + s);

		try {        //todo remove this after showmessage is fixed
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

	}

	public static void showMessage(String s, int t)
	{//REDO todo
//		System.out.println("Timed out message window: " + s);
//		try {
//			TimeUnit.SECONDS.sleep(t);
//		} catch (InterruptedException e) {
//			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//			WinAPIAPI.showMessage("WARNING: some stupid caused interruption of sleeping. you should find out about the cause!!");
//		}
		final InfoFrame frame = new InfoFrame(s);
		frame.setVisible(true);
		frame.pack();

		java.util.Timer timer = new java.util.Timer();
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				frame.dispose();
			}
		}, 5 * 1000);

		try {        //todo remove this after showmessage is fixed
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	public static void toolTip(String s, int x, int y)
	{    //REDO	todo
		//System.out.println(s);
		final ToolTip tt = new ToolTip(s, new Point(x, y));
		java.util.Timer timer = new java.util.Timer();
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				tt.dispose();
			}
		}, 5 * 1000);
	}

	public static Point getMousePos()
	{
		logger.trace("inside Winapiapi getMousePos");
		return new Point((int) MouseInfo.getPointerInfo().getLocation().getX(), (int) MouseInfo.getPointerInfo().getLocation().getY());
	}

}
