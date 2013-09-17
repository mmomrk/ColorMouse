package PixelHunter;

import java.util.HashMap;
import java.util.Map;

/**
 * User: mrk
 * Date: 8/23/13 * Time: 3:32 AM
 */


public abstract class LivingCreature
{
	public int id;     //0-99=>characters reserved; 100=>pet; 101=>target

	protected static final Map<String, Integer> iDValues = new HashMap<String, Integer>();

	public L2Window l2Window;

//	public GroupedVariables.ProjectConstants projectConstants;

	public abstract int getHP();

	public abstract void setHP();

	public LivingCreature(int thisid)     //do we need this??
	{
		this.id = thisid;
	}
	static {

		iDValues.put("prophet",GroupedVariables.ProjectConstants.ID_Prophet);
		iDValues.put("warcryer",GroupedVariables.ProjectConstants.ID_Warcryer);
		iDValues.put("bladedancer",GroupedVariables.ProjectConstants.ID_Bladedancer);
		iDValues.put("swordsinger",GroupedVariables.ProjectConstants.ID_Swordsinger);
		iDValues.put("warlord",GroupedVariables.ProjectConstants.ID_Warlord);
		iDValues.put("treasurehunter",GroupedVariables.ProjectConstants.ID_Spoiler);
		iDValues.put("warlock",GroupedVariables.ProjectConstants.ID_Warlock);
		iDValues.put("templeknight",GroupedVariables.ProjectConstants.ID_Templeknight);
		iDValues.put("defaultchar",GroupedVariables.ProjectConstants.ID_DefaultCharacter);
		iDValues.put("pet",GroupedVariables.ProjectConstants.ID_PET);
		iDValues.put("target",GroupedVariables.ProjectConstants.ID_TARGET);

	}
}
