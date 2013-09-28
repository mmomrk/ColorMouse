package PixelHunter.HotKeysByTulskiy;
import PixelHunter.GroupedVariables;
import PixelHunter.HotKeysByTulskiy.Common.HotKey;
import PixelHunter.HotKeysByTulskiy.Common.HotKeyListener;
import PixelHunter.HotKeysByTulskiy.Common.Provider;
import PixelHunter.L2Window;
import PixelHunter.WinAPIAPI;
import PixelHunter.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * User: mrk
 * Date: 9/8/13; Time: 6:45 AM
 */
//public class HotKeyHandler extends JFrame implements HotkeyListener, IntellitypeListener
public class HotKeyHandler implements HotKeyListener
{
	private static final Logger logger = LoggerFactory.getLogger(HotKeyHandler.class);


	final Provider provider = Provider.getCurrentProvider(false);

	private static final int PAUSE        = 299;        //forgive me..
	private static final int ALT_PAUSE    = 300;    //in the name of sparta
	private static final int SHIFT_PAUSE  = 301;
	private static final int ALT_CTRL_F   = 302;
	private static final int ALT_CTRL_R   = 303;
	private static final int ALT_CTRL_B   = 304;
	private static final int CTRL_SHIFT_B = 305;


//	private final JPanel      bottomPanel = new JPanel();
//	private final JPanel      mainPanel   = new JPanel();
//	private final JPanel      topPanel    = new JPanel();
//	private final JScrollPane scrollPane  = new JScrollPane();


	private static boolean
	lastModeFarm    = false,
	lastModeBuff    = false,
	lastModeHomeRun = false;

//	public static void main(String args[]){
//		WinAPIAPI.User32DLL.MSG msg = new WinAPIAPI.User32DLL.MSG();
//		while (true) {
//			while (PeekMessage(msg, null, 0, 0, WinAPIAPI.User32DLL.PM_REMOVE)) {
//				if (msg.message == WinAPIAPI.User32DLL.WM_HOTKEY) {
//					System.out.println("Yattaaaa. Hotkey with id " + msg.wParam);
//				}
//			}
//
//			try {
//				Thread.sleep(300);
//			} catch (InterruptedException e) {
//				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//			}
//		}
//	}

	private void sleep()
	{
		logger.info("Sleep mode is "+GroupedVariables.Mediator.sleepRegime+". Press again to continue program execution");
		GroupedVariables.Mediator.sleepRegime =	!GroupedVariables.Mediator.sleepRegime;
	}

	public void onHotKey(HotKey hotKey)
	{
		logger.debug("message received " + hotKey);
		if (hotKey.keyStroke == KeyStroke.getKeyStroke("alt PAUSE")) {
			logger.info("Received alt+pause. Terminating");
			provider.stop();
//			cleanUp();
			System.exit(0);
		} else if (hotKey.keyStroke == KeyStroke.getKeyStroke("PAUSE")) {
			logger.debug("Got hotKey PAUSE. Executing");
			WinAPIAPI.showMessage("You have pressed pause. press OK to unpause. do not press close");
//			sleep();
		} else if (hotKey.keyStroke == KeyStroke.getKeyStroke("shift PAUSE")) {
			logger.debug("Got hotKey shift PAUSE. Executing");
			debugModeShift();
		} else if (hotKey.keyStroke == KeyStroke.getKeyStroke("alt control F")) {
			logger.debug("Got hotKey alt control F. Executing");
			toggleModeFarm();
		} else if (hotKey.keyStroke == KeyStroke.getKeyStroke("alt control R")) {
			logger.debug("Got hotKey alt control R. Executing");
			toggleModeHomeRun();
		} else if (hotKey.keyStroke == KeyStroke.getKeyStroke("alt control B")) {
			logger.debug("Got hotKey alt control B. Executing");
			toggleModeBuff();
		} else if (hotKey.keyStroke == KeyStroke.getKeyStroke("control shift B")) {
			logger.debug("Got hotKey shift B. Executing");
			forceRebuff();
		}


//		switch (hotKey.keyStroke) {
//			case ALT_PAUSE:
//				logger.info("Received alt+pause. Terminating");
//				cleanUp();
//				System.exit(0);
//			case PAUSE:
//				logger.debug("Got hotKey PAUSE. Executing");
//				sleep();
//				break;
//			case SHIFT_PAUSE:
//				logger.debug("Got hotKey SHIFT_PAUSE. Executing");
//				debugModeShift();
//				break;
//		case ALT_CTRL_F:
//		logger.debug("Got hotKey ALT_CTRL_F. Executing");
//		toggleModeFarm();
//		break;
//		case ALT_CTRL_R:
//		logger.debug("Got hotKey ALT_CTRL_R. Executing");
//		toggleModeHomeRun();
//		break;
//		case ALT_CTRL_B:
//		logger.debug("Got hotKey ALT_CTRL_B. Executing");
//		toggleModeBuff();
//		break;
//		case CTRL_SHIFT_B:
//		logger.debug("Got hotKey SHIFT_B. Executing");
//		forceRebuff();
//		break;
//		}
	}

	public HotKeyHandler()
	{
		logger.trace("Creating HotKeyHandler");
		registerHotKeys();

	}

	private void cleanUp()    //todo	in the deep future
	{
		WinAPIAPI.User32DLL.unregisterHotKey(null, PAUSE);
		WinAPIAPI.User32DLL.unregisterHotKey(null, ALT_PAUSE);
		WinAPIAPI.User32DLL.unregisterHotKey(null, SHIFT_PAUSE);
		WinAPIAPI.User32DLL.unregisterHotKey(null, ALT_CTRL_F);
		WinAPIAPI.User32DLL.unregisterHotKey(null, ALT_CTRL_R);
		WinAPIAPI.User32DLL.unregisterHotKey(null, ALT_CTRL_B);
		WinAPIAPI.User32DLL.unregisterHotKey(null, CTRL_SHIFT_B);

	}

	private void registerHotKeys()
	{
		logger.trace(".registerHotKeys");
		provider.register(KeyStroke.getKeyStroke("PAUSE"), this);
		provider.register(KeyStroke.getKeyStroke("alt PAUSE"), this);
		provider.register(KeyStroke.getKeyStroke("shift PAUSE"), this);
		provider.register(KeyStroke.getKeyStroke("alt control F"), this);
		provider.register(KeyStroke.getKeyStroke("alt control R"), this);
		provider.register(KeyStroke.getKeyStroke("alt control B"), this);
		provider.register(KeyStroke.getKeyStroke("control shift B"), this);

//		WinAPIAPI.User32DLL.registerHotKey(null, PAUSE, 0, KeyEvent.VK_PAUSE);
//		WinAPIAPI.User32DLL.registerHotKey(null, ALT_PAUSE, WinAPIAPI.User32DLL.MOD_ALT, KeyEvent.VK_PAUSE);
//		WinAPIAPI.User32DLL.registerHotKey(null, SHIFT_PAUSE, WinAPIAPI.User32DLL.MOD_SHIFT, KeyEvent.VK_PAUSE);
//		WinAPIAPI.User32DLL.registerHotKey(null, ALT_CTRL_F, WinAPIAPI.User32DLL.MOD_ALT + WinAPIAPI.User32DLL.MOD_CONTROL, KeyEvent.VK_F);
//		WinAPIAPI.User32DLL.registerHotKey(null, ALT_CTRL_R, WinAPIAPI.User32DLL.MOD_ALT + WinAPIAPI.User32DLL.MOD_CONTROL, KeyEvent.VK_R);
//		WinAPIAPI.User32DLL.registerHotKey(null, ALT_CTRL_B, WinAPIAPI.User32DLL.MOD_ALT + WinAPIAPI.User32DLL.MOD_CONTROL, KeyEvent.VK_B);
//		WinAPIAPI.User32DLL.registerHotKey(null, SHIFT_B, WinAPIAPI.User32DLL.MOD_SHIFT, KeyEvent.VK_B);
	}

	private void forceRebuff()
	{
		logger.trace(".forceRebuff");
		World.forceRebuff();
	}

	private void toggleModeFarm()
	{
		logger.trace(".toggleModeFarm");
		if (lastModeFarm) {
			World.deactivateFarm();
			lastModeFarm	=	false;
		} else {
			World.activateFarm();
			lastModeFarm	=	true;
		}
	}

	private void toggleModeBuff()
	{
		logger.trace(".toggleModeBuff");
		if (lastModeBuff) {
			World.deactivateBuff();
			lastModeBuff=false;
		} else {
			World.activateBuff();
			lastModeBuff=true;
		}
	}

	private void toggleModeHomeRun()
	{
		logger.trace(".toggleModeHomeRun");
		if (lastModeHomeRun) {
			World.deactivateHomeRun();
			lastModeHomeRun=false;
		} else {
			World.activateHomeRun();
			lastModeHomeRun=true;
		}
	}

	private void debugModeShift()
	{
		logger.trace(".debugModeShift");

		L2Window.debugMode++;
		if (L2Window.debugMode == 3) {
			L2Window.debugMode = 0;
		}
		logger.debug("Shifting debugmode to " + L2Window.debugMode);
	}



//	private void createGui()
//	{
//
//		mainPanel.setLayout(new BorderLayout());
//
//		bottomPanel.setLayout(new BorderLayout());
//		topPanel.setBorder(new EtchedBorder(1));
//		bottomPanel.setBorder(new EtchedBorder(1));
//		bottomPanel.add(scrollPane, BorderLayout.CENTER);
//		mainPanel.add(topPanel, BorderLayout.NORTH);
//		mainPanel.add(bottomPanel, BorderLayout.CENTER);
//
//		this.addWindowListener(new java.awt.event.WindowAdapter()
//		{
//			@Override
//			public void windowClosing(java.awt.event.WindowEvent evt)
//			{
//				// don't forget to clean up any resources before close
//				JIntellitype.getInstance().cleanUp();
//				System.exit(0);
//			}
//		});
//
//		this.getContentPane().add(mainPanel);
//		this.pack();
//		this.setSize(800, 600);
//		this.setTitle("Serius BOT");
//		this.setVisible(true);
//		this.initJIntellitype();
//		this.setState(Frame.ICONIFIED);
//	}

//	public void initJIntellitype()
//	{
//		try {
//
//			// initialize JIntellitype with the frame so all windows commands can
//			// be attached to this window
//			JIntellitype.getInstance().addHotKeyListener(this);
//			JIntellitype.getInstance().addIntellitypeListener(this);
//			logger.trace("JIntellitype initialized");
//		} catch (RuntimeException ex) {
//			logger.error("Either you are not on Windows, or there is a problem with the JIntellitype library!");
//		}
//	}

//	public HotKeyHandler()
//	{
//		if (JIntellitype.checkInstanceAlreadyRunning("JIntellitype Te st Application")) {
//			System.exit(1);
//		}
//
//		registerHotKeys();
//		createGui();
//		this.addWindowListener(new java.awt.event.WindowAdapter()
//		{
//			@Override
//			public void windowClosing(java.awt.event.WindowEvent evt)
//			{
//				// don't forget to clean up any resources before close
//				JIntellitype.getInstance().cleanUp();
//				System.exit(0);
//			}
//		});
//
//	}


//	@Override
//	public void onIntellitype(int command)
//	{
//		logger.warn("onIntellitype: I have caught come multimedia key. Not sure if it is good.. " + command);
//	}
}

//	used hotkeys
//PAUSE
//alt PAUSE
//shift PAUSE
//alt control F
//alt control R
//alt control B
//control shift B
