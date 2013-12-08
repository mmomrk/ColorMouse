package PixelHunter;
public class Test
{


	public static void main(String args[])
	{
		String parceNums = "0004",
		workSt = parceNums;
		while (workSt.length() > 0) {

			WinAPIAPI.showMessage(parceNums.substring(0, 2) + " " + Integer.parseInt(parceNums.substring(2, 4)));
			workSt = workSt.substring(1);
		}
		WinAPIAPI.showMessage("to", 1);
		WinAPIAPI.showMessage("nto");
		System.exit(0);

	}

}
