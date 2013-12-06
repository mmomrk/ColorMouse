package PixelHunter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: mrk
 * Date: 9/27/13; Time: 1:33 PM
 */
public class DialogWindow extends JFrame
{
	private static final Logger logger = LoggerFactory.getLogger(InfoFrame.class);

	private JLabel  label;
	private JButton okButton;

	private int answer;
	private boolean canQuit = false;

	public int whatIsTheAnswer()
	{
		while (!canQuit) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				logger.warn("exception in DialogWindow sleep");
			}
		}
		WinAPIAPI.frameExists = false;
		this.dispose();
		return answer;

	}

	public DialogWindow(int dialogCase)
	{
		super("Serius BOT");
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionOKButton());
		this.setLocationRelativeTo(null);
//		this.setSize(100,400);
		Container content = this.getContentPane();
		content.setLayout(new GridLayout(0, 2, 10, 10));
		if (dialogCase == 0 || dialogCase == 1 || dialogCase == 2) {

			content.setBackground(Color.white);
			JButton[] buttonsCharacters = {
										  new JButton("DefaultCharacter"),
										  new JButton("Warcryer"),
										  new JButton("Spoiler"),
										  new JButton("Prophet"),
										  new JButton("Warlord"),
										  new JButton("Swordsinger"),
										  new JButton("Bladedancer"),
										  new JButton("Warlock"),
										  new JButton("Templeknight"),
										  new JButton("Necromancer"),
										  new JButton("Elvenelder")};
			// place radio buttons into a single group
//			int x=0,y=40,h=20,w=100;
//			content.setBackground(Color.BLACK);

			for (JButton button : buttonsCharacters) {
//				logger.debug("adding butt "+button.getText());
				button.addActionListener(new ActionCharacterButton(button.getText()));
//				button.setBounds(x,y,w,h);
//				y+=h;
				content.add(button);
			}

		}

		switch (dialogCase) {
			case 0:    //right select char
				label = new JLabel("Select character in the only window");
				break;
			case 1:
				label = new JLabel("Select character in the left window");
				break;
			case 2:
				label = new JLabel("Select character in the left window");
				break;
		}

//		this.setLayout(null);

//		label.setBounds(0,0,120,30);
		content.add(label);
//		content.add(okButton);
		this.setVisible(true);
		this.pack();
	}

	class ActionCharacterButton implements ActionListener
	{
		int charactersID;

		public ActionCharacterButton(String buttonName)
		{
//			switch (buttonName){
			if (buttonName == "DefaultCharacter") {
				this.charactersID = GroupedVariables.ProjectConstants.ID_DefaultCharacter;
			} else if (buttonName == "Warcryer") {
				this.charactersID = GroupedVariables.ProjectConstants.ID_Warcryer;
			} else if (buttonName == "Spoiler") {
				this.charactersID = GroupedVariables.ProjectConstants.ID_Spoiler;
			} else if (buttonName == "Prophet") {
				this.charactersID = GroupedVariables.ProjectConstants.ID_Prophet;
			} else if (buttonName == "Warlord") {
				this.charactersID = GroupedVariables.ProjectConstants.ID_Warlord;
			} else if (buttonName == "Swordsinger") {
				this.charactersID = GroupedVariables.ProjectConstants.ID_Swordsinger;
			} else if (buttonName == "Bladedancer") {
				this.charactersID = GroupedVariables.ProjectConstants.ID_Bladedancer;
			} else if (buttonName == "Warlock") {
				this.charactersID = GroupedVariables.ProjectConstants.ID_Warlock;
			} else if (buttonName == "Templeknight") {
				this.charactersID = GroupedVariables.ProjectConstants.ID_Templeknight;
			} else if (buttonName == "Necromancer") {
				this.charactersID = GroupedVariables.ProjectConstants.ID_Necromancer;
			} else if (buttonName == "Elvenelder") {
				this.charactersID = GroupedVariables.ProjectConstants.ID_Elvenelder;
			} else {
				logger.warn("stupid in dialog");
				this.charactersID = GroupedVariables.ProjectConstants.ID_DefaultCharacter;
			}
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			DialogWindow.this.answer = this.charactersID;
			DialogWindow.this.canQuit = true;
		}
	}


	class ActionOKButton implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			logger.trace("OK button press performed");
			WinAPIAPI.frameExists = false;
			DialogWindow.this.dispose();

		}
	}
}
