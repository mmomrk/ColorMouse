package PixelHunter;


import com.sun.jna.*;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.W32APIOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
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


	public static class Kernel32
	{
		static {
			Native.register("kernel32");
		}

		public static int PROCESS_QUERY_INFORMATION = 0x0400;
		public static int PROCESS_VM_READ           = 0x0010;

		public static native int GetLastError();
		public static native boolean Beep(double freq,double duration);

		public static native Pointer OpenProcess(int dwDesiredAccess, boolean bInheritHandle, Pointer pointer);
	}


	interface MyListener extends WinDef.StdCallCallback
	{
		WinDef.LRESULT callback(WinDef.HWND hWnd, int uMsg, WinDef.WPARAM uParam, WinDef.LPARAM lParam);
	}


	public static class User32DLL		//used for both hotkeys and window management
	{
		public static final int MOD_ALT = 0x0001;
		public static final int MOD_CONTROL = 0x0002;
		public static final int MOD_NOREPEAT = 0x4000;
		public static final int MOD_SHIFT = 0x0004;
		public static final int MOD_WIN = 0x0008;
		public static final int WM_HOTKEY = 0x0312;
		public static final int VK_MEDIA_NEXT_TRACK = 0xB0;
		public static final int VK_MEDIA_PREV_TRACK = 0xB1;
		public static final int VK_MEDIA_STOP = 0xB2;
		public static final int VK_MEDIA_PLAY_PAUSE = 0xB3;
		public static final int PM_REMOVE = 0x0001;


		static {
			Native.register(NativeLibrary.getInstance("user32", W32APIOptions.DEFAULT_OPTIONS));

//			Native.register("user32");
		}

		public static native int GetWindowThreadProcessId(WinDef.HWND hWnd, PointerByReference pref);

		public static native WinDef.HWND GetForegroundWindow();

		public static native int GetWindowTextW(WinDef.HWND hWnd, char[] lpString, int nMaxCount);

		public static native int SetWindowLong(WinDef.HWND hWnd, int nIndex, Callback callback);

		public static native boolean registerHotKey(Pointer hWnd, int id, int fsModifiers, int vk);

		public static native boolean unregisterHotKey(Pointer hWnd, int id);

		public static native boolean PeekMessage(MSG lpMsg, Pointer hWnd, int wMsgFilterMin, int wMsgFilterMax, int wRemoveMsg);

		public static native WinDef.HWND ShowWindow(WinDef.HWND hwnd, int cmdShow);  //not as wanted

		public static native boolean BringWindowToTop(WinDef.HWND hwnd);	//invalid

		public static native boolean  SetForegroundWindow(WinDef.HWND hwnd);

		@SuppressWarnings({"UnusedDeclaration"})
		public static class MSG extends Structure
		{
			public Pointer hWnd;
			public int message;
			public Parameter wParam;
			public Parameter lParam;
			public int time;
			public int x;
			public int y;

			@Override
			protected java.util.List getFieldOrder() {
				return Arrays.asList("hWnd", "message", "wParam", "lParam", "time", "x", "y");
			}
		}
		public static class Parameter extends IntegerType
		{
			@SuppressWarnings("UnusedDeclaration")
			public Parameter() {
				this(0);
			}

			public Parameter(long value) {
				super(Pointer.SIZE, value);
			}
		}

//		public static native boolean GetWindowRect(WinDef.HWND hwnd, WinDef.RECT rect);

	}

	public static void setActiveWindow(WinDef.HWND hwnd)
	{
		logger.info("User32.INSTANCE.SetForegroundWindow(hwnd);");
//		User32.INSTANCE.SetFocus(hwnd);
//		User32.INSTANCE.ShowWindow(hwnd,1);
//		User32.INSTANCE.BringWindowToTop ();	//invalid
		User32.INSTANCE.SetForegroundWindow(hwnd);	//after a long struggle
//		logger.debug("User32.INSTANCE.UpdateWindow(hwnd);");
		User32.INSTANCE.UpdateWindow(hwnd);
//		logger.debug("User32.INSTANCE... finished");
//		if (User32.INSTANCE.SetFocus(hwnd)==null){
//			logger.error(".setActiveWindow failed to activate window");
//		}

	}


	public static WinDef.RECT getWindowRect(WinDef.HWND hwnd)
	{
		WinDef.RECT returnRectangle = new WinDef.RECT();
		boolean b = User32.INSTANCE.GetWindowRect(hwnd, returnRectangle);
		return returnRectangle;
	}

	public static void bringToFront(WinDef.HWND hwnd)	//todo delete it. is obsolete
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


	static boolean frameExists=false;

	public static void showMessage(String s)
	{
       InfoFrame frame= new InfoFrame(s);
       frame.setVisible(true);
       frame.pack();
		frameExists=true;
		while (frameExists){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				logger.warn("exception in infoframe sleep");
			}
		}
//		System.out.println("Non-timed-out info frame. Text:\n" + s);
//
//		try {        //todo remove this after showmessage is fixed
//			System.in.read();
//		} catch (IOException e) {
//			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//		}

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
		while (frameExists){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				logger.warn("exception in infoframe sleep");
			}

		}

		java.util.Timer timer = new java.util.Timer();
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				frame.dispose();
				frameExists=false;
			}
		}, t * 1000);

//		try {        //todo remove this after showmessage is fixed
//			System.in.read();
//		} catch (IOException e) {
//			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//		}
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
