package PixelHunter;
import java.awt.*;

/**
 * User: mrk
 * Date: 8/24/13; Time: 8:29 AM
 */
public class HpConstants
{
	public Color color;
	public Point coordinateLeft;
	public Point coordinateRight;

	public HpConstants(Color color, Point left, Point right)
	{
		this.color = color;
		this.coordinateLeft = left;
		this.coordinateRight = right;
	}
}
