package PixelHunter;
//
//
//import static PixelHunter.EnumerateWindows.ProcessIdentifier.Kernel32.OpenProcess;
//import static PixelHunter.EnumerateWindows.ProcessIdentifier.Psapi.GetModuleBaseNameW;
//import static PixelHunter.EnumerateWindows.ProcessIdentifier.User32DLL.GetForegroundWindow;
//import static PixelHunter.EnumerateWindows.ProcessIdentifier.User32DLL.GetWindowTextW;
//import static PixelHunter.EnumerateWindows.ProcessIdentifier.User32DLL.GetWindowThreadProcessId;
////import static enumeration.EnumerateWindows.Kernel32.*;
////import static enumeration.EnumerateWindows.Psapi.*;
////import static enumeration.EnumerateWindows.User32DLL.*;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.PointerByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


//
//public class EnumerateWindows {
//    private static final int MAX_TITLE_LENGTH = 1024;
//
public class ProcessIdentifier
{

	private static final Logger logger = LoggerFactory.getLogger(L2Window.class);

//    public static void main(String[] args) throws Exception {
//        char[] buffer = new char[MAX_TITLE_LENGTH * 2];
//        GetWindowTextW(GetForegroundWindow(), buffer, MAX_TITLE_LENGTH);
//        System.out.println("Active window title: " + Native.toString(buffer));
//
//        PointerByReference pointer = new PointerByReference();
//        GetWindowThreadProcessId(GetForegroundWindow(), pointer);
//        Pointer process = OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, false, pointer.getValue());
//        GetModuleBaseNameW(process, null, buffer, MAX_TITLE_LENGTH);
//        System.out.println("Active window process: " + Native.toString(buffer));
//    }
//
//    static class Psapi {
//        static { Native.register("psapi"); }
//        public static native int GetModuleBaseNameW(Pointer hProcess, Pointer hmodule, char[] lpBaseName, int size);
//    }
//
//    static class Kernel32 {
//        static { Native.register("kernel32"); }
//        public static int PROCESS_QUERY_INFORMATION = 0x0400;
//        public static int PROCESS_VM_READ = 0x0010;
//        public static native int GetLastError();
//        public static native Pointer OpenProcess(int dwDesiredAccess, boolean bInheritHandle, Pointer pointer);
//    }
//
//    static class User32DLL {
//        static { Native.register("user32"); }
//        public static native int GetWindowThreadProcessId(HWND hWnd, PointerByReference pref);
//        public static native HWND GetForegroundWindow();
//        public static native int GetWindowTextW(HWND hWnd, char[] lpString, int nMaxCount);
//    }
//}
//}


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


	private static class User32DLL
	{
		static {
//            Native.register("user32");
		}

		public static native int GetWindowThreadProcessId(WinDef.HWND hWnd, PointerByReference pref);

		public static native WinDef.HWND GetForegroundWindow();

		public static native int GetWindowTextW(WinDef.HWND hWnd, char[] lpString, int nMaxCount);
	}


	private static final int MAX_TITLE_LENGTH = 1024;

	public static void qmain()//(String[] args)
	{

		Psapi ps = new Psapi();
		Kernel32 kr = new Kernel32();
		User32DLL us = new User32DLL();

		char[] buffer = new char[MAX_TITLE_LENGTH * 2];
		String nameApp;

//        us.GetWindowTextW(us.GetForegroundWindow(), buffer, MAX_TITLE_LENGTH);
//        System.out.println("Active window title: " + Native.toString(buffer));
//
//
//        PointerByReference pointer = new PointerByReference();
//        us.GetWindowThreadProcessId(us.GetForegroundWindow(), pointer);
//        Pointer process = kr.OpenProcess(kr.PROCESS_QUERY_INFORMATION | kr.PROCESS_VM_READ, false, pointer.getValue());
//        ps.GetModuleBaseNameW(process, null, buffer, MAX_TITLE_LENGTH);
//        System.out.println("Active window process: " + Native.toString(buffer));
//
//
//        final WinDef.HWND[] windowHandles = new WinDef.HWND[1];
//        User32.INSTANCE.EnumWindows(new WinUser.WNDENUMPROC()
//        {
//            @Override
//            public boolean callback(WinDef.HWND h, Pointer pointer)
//            {
//                if (false)
//                {
//                    windowHandles[0] = h;
//                    return false;
//                }
//                return true;
//            }
//        }, Pointer.NULL);
//        User32.INSTANCE.EnumWindows;
//        User32.INSTANCE.SetWindowPos(windowHandles[0], windowHandles[0], 100, 100, 300, 300, 0x0004);
		final User32 user32 = User32.INSTANCE;

		user32.EnumWindows(new User32.WNDENUMPROC()
		{

			int count;

			public boolean callback(WinDef.HWND hWnd, Pointer userData)
			{
				char[] windowText = new char[512];
				user32.GetWindowText(hWnd, windowText, 512);
				String wText = Native.toString(windowText);
				wText = (wText.isEmpty()) ? "" : "; text: " + wText;
				System.out.println("Found window " + hWnd + ", total " + ++count + wText);
				return true;
			}
		}, null);
	}

	public static ArrayList<WinDef.HWND> getL2HwndArray()    //return array lenght 2
	{
		final ArrayList<WinDef.HWND> hwnds;
		hwnds = new ArrayList<WinDef.HWND>();

		Psapi ps = new Psapi();
		Kernel32 kr = new Kernel32();
		User32DLL us = new User32DLL();

		char[] buffer = new char[MAX_TITLE_LENGTH * 2];
		String nameApp;
		final User32 user32 = User32.INSTANCE;

		user32.EnumWindows(new User32.WNDENUMPROC()
		{

			int count;
			int i = 0;

			public boolean callback(WinDef.HWND hWnd, Pointer userData)
			{
				char[] windowText = new char[512];
				user32.GetWindowText(hWnd, windowText, 512);
				String wText = Native.toString(windowText);
				if (wText.equals("Shot00069.bmp - Paint")) {      //- Windows Photo Viewer	- Paint
					hwnds.add(hWnd);
					logger.debug("Process IDer: found proper window, HWND=" + hWnd);
				}
				wText = (wText.isEmpty()) ? "" : "; text: " + wText;

//				System.out.println("Found window " + hWnd + ", total " + ++count + wText);
				return true;
			}
		}, null);
		return hwnds;
	}

}
//todo: make this class a cleaner place