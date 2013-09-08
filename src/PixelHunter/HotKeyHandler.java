package PixelHunter;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * User: mrk
 * Date: 9/8/13; Time: 6:45 AM
 */
public class HotKeyHandler extends JFrame implements HotkeyListener, IntellitypeListener
{
	private static final Logger logger = LoggerFactory.getLogger(HotKeyHolder.class);

	private static final int PAUSE       = 299;        //forgive me..
	private static final int ALT_PAUSE   = 300;    //in the name of sparta
	private static final int SHIFT_PAUSE = 301;
	private static final int ALT_CTRL_F  = 302;
	private static final int ALT_CTRL_R  = 303;
	private static final int ALT_CTRL_B  = 304;
	private static final int SHIFT_B     = 305;


	private final JPanel      bottomPanel = new JPanel();
	private final JPanel      mainPanel   = new JPanel();
	private final JPanel      topPanel    = new JPanel();
	private final JScrollPane scrollPane  = new JScrollPane();


	private void sleep()
	{
		WinAPIAPI.showMessage("Sleep mode is activated. Close this window to continue program execution");
	}

	public void onHotKey(int aIdentifier)
	{
		logger.debug("WM_HOTKEY message received " + Integer.toString(aIdentifier));
		switch (aIdentifier) {    //todo: implement
			case ALT_PAUSE:
				logger.info("Received alt+pause. Terminating");
				JIntellitype.getInstance().cleanUp();
				System.exit(0);
			case PAUSE:
				logger.debug("Got hotKey PAUSE. Executing");
				sleep();
				break;
			case SHIFT_PAUSE:
				logger.debug("Got hotKey SHIFT_PAUSE. Executing");
				debugModeShift();
//				break;
//			case ALT_CTRL_F:
//				logger.debug("Got hotKey ALT_CTRL_F. Executing");
//				toggleFarmMode();
//				break;
//			case ALT_CTRL_R:
//				logger.debug("Got hotKey ALT_CTRL_R. Executing");
//				toggleHomeRunMode();
//				break;
//			case ALT_CTRL_B:
//				logger.debug("Got hotKey ALT_CTRL_B. Executing");
//				toggleBuffMode();
//				break;
//			case SHIFT_B:
//				logger.debug("Got hotKey SHIFT_B. Executing");
//				forceRebuff();
				break;
		}
	}

	private void debugModeShift(){
		logger.trace(".debugModeShift");
		logger.debug("Shifting debugmode to "+L2Window.debugMode++);

		if (L2Window.debugMode == 3){
			L2Window.debugMode	=	0;
		}
	}

	private void registerHotKeys()
	{
		JIntellitype.getInstance().registerHotKey(ALT_PAUSE, JIntellitype.MOD_ALT, KeyEvent.VK_PAUSE);
		JIntellitype.getInstance().registerHotKey(SHIFT_PAUSE, JIntellitype.MOD_SHIFT, KeyEvent.VK_PAUSE);
		JIntellitype.getInstance().registerHotKey(ALT_CTRL_F, JIntellitype.MOD_ALT + JIntellitype.MOD_CONTROL, KeyEvent.VK_F);
		JIntellitype.getInstance().registerHotKey(ALT_CTRL_R, JIntellitype.MOD_ALT + JIntellitype.MOD_CONTROL, KeyEvent.VK_R);
		JIntellitype.getInstance().registerHotKey(ALT_CTRL_B, JIntellitype.MOD_ALT + JIntellitype.MOD_CONTROL, KeyEvent.VK_B);
		JIntellitype.getInstance().registerHotKey(SHIFT_B, JIntellitype.MOD_SHIFT, KeyEvent.VK_B);
	}

	private void createGui()
	{

		mainPanel.setLayout(new BorderLayout());

		bottomPanel.setLayout(new BorderLayout());
		topPanel.setBorder(new EtchedBorder(1));
		bottomPanel.setBorder(new EtchedBorder(1));
		bottomPanel.add(scrollPane, BorderLayout.CENTER);
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(bottomPanel, BorderLayout.CENTER);

		this.addWindowListener(new java.awt.event.WindowAdapter()
		{
			@Override
			public void windowClosing(java.awt.event.WindowEvent evt)
			{
				// don't forget to clean up any resources before close
				JIntellitype.getInstance().cleanUp();
				System.exit(0);
			}
		});

		this.getContentPane().add(mainPanel);
		this.pack();
		this.setSize(800, 600);
		this.setTitle("Serius BOT");
		this.setVisible(true);
		this.initJIntellitype();
	}

	public void initJIntellitype()
	{
		try {

			// initialize JIntellitype with the frame so all windows commands can
			// be attached to this window
			JIntellitype.getInstance().addHotKeyListener(this);
			JIntellitype.getInstance().addIntellitypeListener(this);
			logger.trace("JIntellitype initialized");
		} catch (RuntimeException ex) {
			logger.error("Either you are not on Windows, or there is a problem with the JIntellitype library!");
		}
	}

	public HotKeyHandler()
	{
		if (JIntellitype.checkInstanceAlreadyRunning("JIntellitype Test Application")) {
			System.exit(1);
		}

		registerHotKeys();
		createGui();
		this.addWindowListener(new java.awt.event.WindowAdapter()
		{
			@Override
			public void windowClosing(java.awt.event.WindowEvent evt)
			{
				// don't forget to clean up any resources before close
				JIntellitype.getInstance().cleanUp();
				System.exit(0);
			}
		});

	}


	@Override
	public void onIntellitype(int command)
	{
		logger.warn("onIntellitype: I have caught come multimedia key. Not sure if it is good.. " + command);
	}
}
