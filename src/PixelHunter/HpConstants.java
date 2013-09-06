package PixelHunter;
import java.awt.*;

/**
 * User: mrk
 * Date: 9/5/13; Time: 1:01 PM
 */
public class HpConstants
{
	public final Color   color;
	public final Point   coordinateLeft;
	public final Point   coordinateRight;
	public final boolean isPet;

	public HpConstants(Color color, Point left, Point right, int id)
	{
		this.color = color;
		this.coordinateLeft = left;
		this.coordinateRight = right;
		if (id == GroupedVariables.ProjectConstants.ID_TARGET) {
			this.isPet = true;
		} else {
			this.isPet = false;
		}
	}
}
