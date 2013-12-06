package PixelHunter.HotKeysByTulskiy.Common;

import PixelHunter.HotKeysByTulskiy.Windows.WindowsProvider;
import com.sun.jna.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Main interface to global hotkey providers
 * <p/>
 * Author: Denis Tulskiy
 * Date: 6/12/11
 */
public abstract class Provider
{
	public static final Logger logger = LoggerFactory.getLogger(Provider.class);

	private boolean useSwingEventQueue;

	/**
	 * Get global hotkey provider for current platform
	 *
	 * @param useSwingEventQueue whether the provider should be using Swing Event queue or a regular thread
	 * @return new instance of Provider, or null if platform is not supported
	 * @see WindowsProvider
	 */
	public static Provider getCurrentProvider(boolean useSwingEventQueue)
	{
		Provider provider;
		if (Platform.isWindows()) {
			provider = new WindowsProvider();

		} else {
			logger.warn("No suitable provider for " + System.getProperty("os.name"));
			return null;
		}
		provider.setUseSwingEventQueue(useSwingEventQueue);
		provider.init();
		return provider;

	}

	private ExecutorService eventQueue;


	/**
	 * Initialize provider. Starts main thread that will listen to hotkey events
	 */
	protected abstract void init();

	/**
	 * Stop the provider. Stops main thread and frees any resources.
	 * </br>
	 * all hotkeys should be reset before calling this method
	 *
	 * @see Provider#reset()
	 */
	public void stop()
	{
		if (eventQueue != null) {
			eventQueue.shutdown();
		}
	}

	/**
	 * Reset all hotkey listeners
	 */
	public abstract void reset();

	/**
	 * Register a global hotkey. Only keyCode and modifiers fields are respected
	 *
	 * @param keyCode  KeyStroke to register
	 * @param listener listener to be notified of hotkey events
	 * @see KeyStroke
	 */
	public abstract void register(KeyStroke keyCode, HotKeyListener listener);

	/**
	 * Register a media hotkey. Currently supported media keys are:
	 * <p/>
	 * <ul>
	 * <li>Play/Pause</li>
	 * <li>Stop</li>
	 * <li>Next track</li>
	 * <li>Previous Track</li>
	 * </ul>
	 *
	 * @param mediaKey media key to register
	 * @param listener listener to be notified of hotkey events
	 * @see MediaKey
	 */
	public abstract void register(MediaKey mediaKey, HotKeyListener listener);

	/**
	 * Helper method fro providers to fire hotkey event in a separate thread
	 *
	 * @param hotKey hotkey to fire
	 */
	protected void fireEvent(HotKey hotKey)
	{
		HotKeyEvent event = new HotKeyEvent(hotKey);
		if (useSwingEventQueue) {
			SwingUtilities.invokeLater(event);
		} else {
			if (eventQueue == null) {
				eventQueue = Executors.newSingleThreadExecutor();
			}
			eventQueue.execute(event);
		}
	}

	public void setUseSwingEventQueue(boolean useSwingEventQueue)
	{
		this.useSwingEventQueue = useSwingEventQueue;
	}

	private class HotKeyEvent implements Runnable
	{
		private HotKey hotKey;

		private HotKeyEvent(HotKey hotKey)
		{
			this.hotKey = hotKey;
		}

		public void run()
		{
			hotKey.listener.onHotKey(hotKey);
		}
	}

}