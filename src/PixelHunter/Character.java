package PixelHunter;


import com.sun.jna.platform.win32.WinDef;

/**
 * User: mrk
 * Date: 9/1/13; Time: 1:17 PM
 */
public abstract class Character extends LivingCreature
{


	public L2Window l2window;
//	public timer    buffTimer, homerunTimer;
//	private int buffTime;
//	public  int farmMode, buffMode, homerunMode;
	private Pet     pet;
	private Target  target;
//	public boolean followFlag;


//	public void classSpecificDeed();//think of it twice

//	public void pvE();

//	public void buff();


	public void setChat()
	{
		l2Window.setChat();
	}

	public int getHP(){	//todo when time comes don't forget to do this
		return 0;
	}

//	public void readchat();
//
//	public void runHome();
//
//	public void spamSelf();
//
//	public void target(int id);
//
//	public void setMp();
//
//	public void setPetHp();
//
//	public void initializeWindow();    //in case of falling down of the prev window
//
//	//commands from chat exec part
//	public void follow(int id);
//
//	public void assistSender(int id);
//
//	public void assistSenderAndAttack(int id);
//
//	public void attackCurrentTarget();    //#4
//
//	//#5,6-reserved by now
//	public void petAssistAttack(int id);    //#7
//
//	public void petStop();
//
//	public void petFollow();
//
//	public void toggleBuffMode();
//
//	public void toggleFarmMode();    //#11
//

	public Character(int thisid, WinDef.HWND hwnd)		//windownumber can only be 1 or 0
	{
		super(thisid);
		this.l2Window =	new L2Window();
		this.l2Window.hwnd	=	hwnd;



//		WinAPIAPI.showMessage("you can move/resize window now");	//todo test world class approach before deleting this
//		l2window.x  ,y  ,h  ,w

		pet	=	new Pet(l2Window);
		pet.setHP();

		target	=	new Target(l2Window);
		target.setHP();

		l2window.setChat();


	}
}
