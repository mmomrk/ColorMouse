package PixelHunter;
public class Test
{
	static {
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
	}

	public static void main(String args[])
	{

		WinAPIAPI.showMessage("waagh");
		int a = WinAPIAPI.dialogWindow(0);
		System.out.println(a);
	}

}
