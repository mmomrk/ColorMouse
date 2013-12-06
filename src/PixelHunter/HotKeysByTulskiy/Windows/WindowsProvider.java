package PixelHunter.HotKeysByTulskiy.Windows;

import PixelHunter.HotKeysByTulskiy.Common.HotKey;
import PixelHunter.HotKeysByTulskiy.Common.HotKeyListener;
import PixelHunter.HotKeysByTulskiy.Common.MediaKey;
import PixelHunter.HotKeysByTulskiy.Common.Provider;

import javax.swing.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static PixelHunter.HotKeysByTulskiy.Windows.User32.*;

/**
 * Author: Denis Tulskiy
 * Date: 6/12/11
 */
public class WindowsProvider extends Provider
{
	private static volatile int idSeq = 0;

	private boolean listen;
	private       Boolean reset = false;
	private final Object  lock  = new Object();
	private Thread thread;

	private Map<Integer, HotKey> hotKeys       = new HashMap<Integer, HotKey>();
	private Queue<HotKey>        registerQueue = new LinkedList<HotKey>();

	public void init()
	{
		Runnable runnable = new Runnable()
		{
			public void run()
			{
				logger.info("Starting Windows global hotkey provider");
				User32.MSG msg = new User32.MSG();
				listen = true;
				while (listen) {
//					logger.info("while listen");
					while (PeekMessage(msg, null, 0, 0, PM_REMOVE)) {
						if (msg.message == WM_HOTKEY) {
							int id = msg.wParam.intValue();
							HotKey hotKey = hotKeys.get(id);

							if (hotKey != null) {
								fireEvent(hotKey);
							}
						}
					}

					synchronized (lock) {
						if (reset) {
							logger.info("Reset hotkeys");
							for (Integer id : hotKeys.keySet()) {
								UnregisterHotKey(null, id);
							}

							hotKeys.clear();
							reset = false;
							lock.notify();
						}

						while (!registerQueue.isEmpty()) {
							register(registerQueue.poll());
						}
						try {
							lock.wait(300);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				logger.info("Exit listening thread");
			}
		};

		thread = new Thread(runnable);
		thread.start();
	}

	private void register(HotKey hotKey)
	{
		int id = idSeq++;
		int code = KeyMap.getCode(hotKey);
		if (RegisterHotKey(null, id, KeyMap.getModifiers(hotKey.keyStroke), code)) {
//			logger.info("Registering hotkey: " + hotKey);
			hotKeys.put(id, hotKey);
		} else {
			logger.warn("Could not register hotkey: " + hotKey);
		}
	}

	public void register(KeyStroke keyCode, HotKeyListener listener)
	{
		synchronized (lock) {
			registerQueue.add(new HotKey(keyCode, listener));
		}
	}

	public void register(MediaKey mediaKey, HotKeyListener listener)
	{
		synchronized (lock) {
			registerQueue.add(new HotKey(mediaKey, listener));
		}
	}

	public void reset()
	{
		synchronized (lock) {
			reset = true;
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stop()
	{
		listen = false;
		if (thread != null) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		super.stop();
	}
}