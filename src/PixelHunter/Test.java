package PixelHunter;
public class Test
{


	public static void main(String args[])
	{
		String parceNums="k01",
		workSt=parceNums;
		while (workSt.length()>0){
		WinAPIAPI.showMessage(parceNums.substring(1, 1)+" "+Integer.parseInt(parceNums.substring(1, 2)));
			workSt=workSt.substring(1);
		}
		WinAPIAPI.showMessage("to",1);
		WinAPIAPI.showMessage("nto");
		System.exit(0);



	}

}
