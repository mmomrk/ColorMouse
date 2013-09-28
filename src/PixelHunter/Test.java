package PixelHunter;
import PixelHunter.HotKeysByTulskiy.Common.HotKey;
import PixelHunter.HotKeysByTulskiy.HotKeyHandler;

import javax.swing.*;
import java.lang.*;
import java.lang.Character;

public class Test
{
	static {
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
	}

	public static void main(String args[])
	{

		int a = WinAPIAPI.dialogWindow(0);
		System.out.println(a);
	}

}
