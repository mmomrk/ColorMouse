package PixelHunter;


import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: mrk
 * Date: 8/23/13
 * Time: 3:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class Pet extends LivingCreature
{
	public L2Window l2Window;

	public Pet()
	{
		super(100);
		l2Window = World.l2Window;
	}

	public Pet(int thisid)
	{
		super(thisid);
	}

	@Override
	public int getHP()
	{
		Color hpColorPositive = World.l2Window.petHpColorPositive;
		Color hpColorNegative = World.l2Window.petHpColorNegative;
		Point coordinateHp_l = World.l2Window.petHp_l;
		Point coordinateHp_r = World.l2Window.petHp_r;
		double deltaX = coordinateHp_r.x - coordinateHp_l.x;
		int ticks = 50;

		for (int i = ticks; i >= 0; i--) {
			Point currentPoint = new Point((int) (coordinateHp_l.x + deltaX * i / ticks), coordinateHp_l.y);

			if (World.l2Window.getRelPixelColor(currentPoint).equals( hpColorPositive)) {
				return (int) ((ticks - i) / ticks);
			}

		}
		return 0;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean isDead()
	{
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Point[] setHP()
	{
		Point[] returnArray = new Point[2];

		Robot robot;
		WinAPIAPI.showMessage("Set HP bar for pet. Place mouse under HP fully healed bar and press OK");
		Point currentCoordinate = WinAPIAPI.getMousePos();
		int i = 0, yLimit = 100;
		while (!l2Window.getRelPixelColor(currentCoordinate).equals(l2Window.petHpColorPositive)) {
			if (i >= yLimit) {   //overflow
				if (l2Window.debugMode == 2) {
					WinAPIAPI.showMessage("Failed to find y");
				}
				returnArray[0].setLocation(-1, -1);
				return returnArray;
			}
			currentCoordinate.y--;
		}
		if (l2Window.debugMode == 2) {
			l2Window.advancedMouseMove(currentCoordinate);
			WinAPIAPI.showMessage("Successfully found y");
		}


		return returnArray;
	}
}
