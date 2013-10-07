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
import java.util.TimerTask;

public class InfoFrame extends JFrame implements WindowListener
{
	private static final Logger logger = LoggerFactory.getLogger(InfoFrame.class);

	private JLabel  label;
	private JButton okButton;

	private boolean frameExists = false;

	java.util.Timer timer = new java.util.Timer();

	public void display(String newLabel, int delaySeconds)
	{
		logger.trace(".display timed-out frame");
		this.timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				logger.trace(".run schedule timed-out frame");
				InfoFrame.this.frameExists = false;
				GroupedVariables.Mediator.sleepRegime=false;
				InfoFrame.this.setVisible(false);
			}
		}, delaySeconds * 1000);
		display(newLabel);
	}

	public void display(String newLabel)
	{

		this.label.setText(newLabel);    //questioned
		this.pack();
		this.setVisible(true);
		this.frameExists = true;
		while (frameExists) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				logger.warn("exception in infoFrame sleep");
			}
		}
	}


	@Override
	public void windowOpened(WindowEvent e)
	{

	}

	public void windowClosing(WindowEvent e)
	{
		logger.trace("window Closed is called. set frame exists to false");
		WinAPIAPI.frameExists = false;
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


		WinAPIAPI.frameExists = true;
	}

	class ActionOKButton implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
//			logger.trace("OK button press performed");
			InfoFrame.this.frameExists = false;
			GroupedVariables.Mediator.sleepRegime=false;
			InfoFrame.this.setVisible(false);
//			dispose();

		}
	}

}
