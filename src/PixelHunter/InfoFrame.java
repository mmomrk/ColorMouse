package PixelHunter;

/**
 * Created with IntelliJ IDEA.
 * User: Arty
 * Date: 24.08.13
 * Time: 18:33
 * To change this template use File | Settings | File Templates.
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

public class InfoFrame extends JFrame{
    private JLabel label;
    private JButton okButton;

    public InfoFrame(String text)
    {

        super("InfoFrame");
        label = new JLabel(text);
        okButton = new JButton("OK");
        add(label, BorderLayout.NORTH);
        add(okButton, BorderLayout.SOUTH);
        this.setLocationRelativeTo(null);
    }
    public static void main(String[] args) {
        final InfoFrame frame= new InfoFrame("Test Run");
        frame.setVisible(true);
        frame.pack();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                frame.dispose();
            }
        }, 5*1000);

    }
}
