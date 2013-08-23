package PixelHunter;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class PixelColorGetter
{


    PixelColorGetter()
    {
    }

    public static Color getPixelColor(int x, int y)
    {
        Robot robot;
        try
        {
            robot = new Robot();
            return robot.getPixelColor(x, y);
        } catch (AWTException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println("EXCEPTION IN COLOR GETTING");
        return Color.black;

    }

    protected void keyPressedHandler(KeyEvent e)
    {


    }

    protected void printPixelColor()
    {
        int x, y;
        x = (int) MouseInfo.getPointerInfo().getLocation().getX();
        y = (int) MouseInfo.getPointerInfo().getLocation().getY();
        System.out.println(getPixelColor(x, y));
    }

    public static void main(String[] args)
    {


        while (true)
        {

            try
            {
                System.in.read();
            } catch (IOException e)
            {

            }
        }
    }
}
