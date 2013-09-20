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

		System.out.println(new HotKey(KeyStroke.getKeyStroke("shift PAUSE"), null));

		HotKeyHandler hotKeyHandler = new HotKeyHandler();
		for (int i = 0; i < 20; i++) {
			System.out.println(i);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
		}

	}

}
