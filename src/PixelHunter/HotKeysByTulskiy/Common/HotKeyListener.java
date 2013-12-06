package PixelHunter.HotKeysByTulskiy.Common;

import PixelHunter.HotKeysByTulskiy.Common.HotKey;

import java.util.EventListener;

/**
 * Author: Denis Tulskiy
 * Date: 6/21/11
 */
public interface HotKeyListener extends EventListener
{
	public void onHotKey(HotKey hotKey);
}