package PixelHunter.HotKeysByTulskiy.Windows;

import PixelHunter.HotKeysByTulskiy.Common.HotKey;
import PixelHunter.HotKeysByTulskiy.Windows.User32.*;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.util.HashMap;
import java.util.Map;


import static java.awt.event.KeyEvent.*;

/**
 * Author: Denis Tulskiy
 * Date: 6/20/11
 */
public class KeyMap
{
	private static final Map<Integer, Integer> codeExceptions = new HashMap<Integer, Integer>()
	{{
			put(VK_INSERT, 0x2D);
			put(VK_DELETE, 0x2E);
			put(VK_ENTER, 0x0D);
			put(VK_COMMA, 0xBC);
			put(VK_PERIOD, 0xBE);
			put(VK_PLUS, 0xBB);
			put(VK_MINUS, 0xBD);
			put(VK_SLASH, 0xBF);
			put(VK_SEMICOLON, 0xBA);
			put(VK_PRINTSCREEN, 0x2C);
		}};

	public static int getCode(HotKey hotKey)
	{
		if (hotKey.isMedia()) {
			int code = 0;
			switch (hotKey.mediaKey) {
				case MEDIA_NEXT_TRACK:
					code = User32.VK_MEDIA_NEXT_TRACK;
					break;
				case MEDIA_PLAY_PAUSE:
					code = User32.VK_MEDIA_PLAY_PAUSE;
					break;
				case MEDIA_PREV_TRACK:
					code = User32.VK_MEDIA_PREV_TRACK;
					break;
				case MEDIA_STOP:
					code = User32.VK_MEDIA_STOP;
					break;
			}

			return code;
		} else {
			KeyStroke keyStroke = hotKey.keyStroke;
			Integer code = codeExceptions.get(keyStroke.getKeyCode());
			if (code != null) {
				return code;
			} else {
				return keyStroke.getKeyCode();
			}
		}
	}

	public static int getModifiers(KeyStroke keyCode)
	{
		int modifiers = 0;
		if (keyCode != null) {
			if ((keyCode.getModifiers() & InputEvent.SHIFT_DOWN_MASK) != 0) {
				modifiers |= User32.MOD_SHIFT;
			}
			if ((keyCode.getModifiers() & InputEvent.CTRL_DOWN_MASK) != 0) {
				modifiers |= User32.MOD_CONTROL;
			}
			if ((keyCode.getModifiers() & InputEvent.META_DOWN_MASK) != 0) {
				modifiers |= User32.MOD_WIN;
			}
			if ((keyCode.getModifiers() & InputEvent.ALT_DOWN_MASK) != 0) {
				modifiers |= User32.MOD_ALT;
			}
		}

		if (System.getProperty("os.name", "").startsWith("Windows 7")) {
			modifiers |= User32.MOD_NOREPEAT;
		}
		return modifiers;
	}
}