package PixelHunter;

import java.util.HashMap;
import java.util.Map;

import static PixelHunter.GroupedVariables.ProjectConstants.*;

/**
 * User: mrk
 * Date: 8/23/13 * Time: 3:32 AM
 */


public abstract class LivingCreature
{
	public int id;     //0-99=>characters reserved; 100=>pet; 101=>target

//	protected static final Map<String, Integer> iDValues = new HashMap<String, Integer>();

	public L2Window l2Window;

//	public GroupedVariables.ProjectConstants projectConstants;

	public abstract int getHP();

	public abstract void setHP();

	public LivingCreature(int thisid)     //do we need this??
	{
		this.id = thisid;
	}

	static {

//		iDValues.put("prophet", ID_Prophet);
//		iDValues.put("warcryer", ID_Warcryer);
//		iDValues.put("bladedancer", ID_Bladedancer);
//		iDValues.put("swordsinger", ID_Swordsinger);
//		iDValues.put("warlord", ID_Warlord);
//		iDValues.put("treasurehunter", ID_Spoiler);
//		iDValues.put("warlock", ID_Warlock);
//		iDValues.put("templeknight", ID_Templeknight);
//		iDValues.put("defaultchar", ID_DefaultCharacter);
//		iDValues.put("pet", ID_PET);
//		iDValues.put("target", ID_TARGET);

	}
}
