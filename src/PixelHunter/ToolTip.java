package PixelHunter;

import com.sun.awt.AWTUtilities;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Arty
 * Date: 24.08.13
 * Time: 19:27
 * To change this template use File | Settings | File Templates.
 */
public class ToolTip extends JFrame
{
	JLabel label;

	public ToolTip(String text, Point point)
	{
		super();
		label = new JLabel(text);
		add(label, BorderLayout.CENTER);
		this.setLocation(point);
		this.setUndecorated(true);
		this.pack();

		AWTUtilities.setWindowOpacity(this, 0.8f);
		this.setVisible(true);

	}

	public void setPosition(Point point)
	{
		this.setLocation(point);
	}

	public void setText(String s)
	{
		this.label.setText(s);
	}

//    public static void main(String[] args) {
//        ToolTip tt=new ToolTip("test run", new Point(100,200));
//
//    }
}
