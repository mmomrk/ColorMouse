package PixelHunter;

/**
 * Created with IntelliJ IDEA.
 * User: Arty
 * Date: 24.08.13
 * Time: 18:33
 * To change this template use File | Settings | File Templates.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class InfoFrame extends JFrame	implements WindowListener
{
	private static final Logger logger = LoggerFactory.getLogger(InfoFrame.class);

	private JLabel  label;
	private JButton okButton;

	@Override
	public void windowOpened(WindowEvent e)
	{

	}

	public void windowClosing(WindowEvent e)
	{

	}

	@Override
	public void windowClosed(WindowEvent e)
	{
		logger.trace("window Closed is called. set frame exists to false");
		WinAPIAPI.frameExists = false;
	}

	@Override
	public void windowIconified(WindowEvent e)
	{

	}

	@Override
	public void windowDeiconified(WindowEvent e)
	{

	}

	@Override
	public void windowActivated(WindowEvent e)
	{

	}

	@Override
	public void windowDeactivated(WindowEvent e)
	{

	}

	public InfoFrame(String text)
	{

		super("InfoFrame");
		label = new JLabel(text);
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionOKButton());
		add(label, BorderLayout.NORTH);
		add(okButton, BorderLayout.SOUTH);

		this.setLocationRelativeTo(null);
	}

	class ActionOKButton implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			logger.trace("OK button press performed");
			WinAPIAPI.frameExists = false;
			InfoFrame.this.dispose();

		}
	}
//    public static void main(String[] args) {
//        final InfoFrame frame= new InfoFrame("T est Run");
//        frame.setVisible(true);
//        frame.pack();
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                frame.dispose();
//            }
//        }, 5*1000);
//
//    }
}
