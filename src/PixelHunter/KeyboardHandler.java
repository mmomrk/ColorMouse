package PixelHunter;

import java.awt.event.KeyEvent;

/**
 * Created with IntelliJ IDEA.
 * User: mrk
 * Date: 8/20/13
 * Time: 1:04 PM
 * To change this template use File | Settings | File Templates.
 */

public class KeyboardHandler
{

    private static int a   =  0;
    public void keyPressed(KeyEvent e)
    {
        System.out.println(e.getKeyCode());

    }
    public static void main(String[] args)
    {
        while (true){
            a++;

        }
    }
}
