package PixelHunter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * User: mrk
 * Date: 8/24/13; Time: 5:53 AM
 */
public class GroupedVariables
{

	public static GroupedVariables.ProjectConstants projectConstants;


	public static class Mediator
	{
		public static boolean talkToMeMode = false;
		public static boolean sleepRegime	=	false;
	}


	public static final class ProjectConstants
	{
		private static final Logger logger = LoggerFactory.getLogger(GroupedVariables.ProjectConstants.class);
		//refactor all these were static.note this
		public static final int HOMERUN_TIME;

		public static final int ID_PET;
		public static final int ID_TARGET;
		public static final int ID_DefaultCharacter;
		public static final int ID_Warcryer;
		public static final int ID_Spoiler;
		public static final int ID_Prophet;
		public static final int ID_Warlord;
		public static final int ID_Swordsinger;
		public static final int ID_Bladedancer;
		public static final int ID_Warlock;
		public static final int ID_Templeknight;
		public static final int ID_Necromancer;
		public static final int ID_Elvenelder;
		public static final Map<Integer,Integer> partyPanelMatch	=	new HashMap<Integer, Integer>();	//filled in static..todo dynamic filling. later

		public static final Color SECONDARY_LIVING_CREATURE_HP_COLOR;
		public static final Color PARTY_MEMBERS_PET_HP_COLOR;
		public static final Color CHAT_COLOR_PARTY;
		public static final Color CHAT_COLOR_PRIVATE;

		public static final int CHAT_TASK_LIST_LENGTH;
		public static final int INITIAL_DEBUG_MODE;
		public static final int WINDOW_ACTIVATE_DELAY_MILLIS;

		private void main()
		{
			logger.trace("GroupedVariables.main.. congratulations!! now tell me how did you get there");
		}

		ProjectConstants() {}

		static {
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream("resources/inputData"));
			} catch (IOException e) {
				logger.error("Failed to read input configuration file", e);
				throw new Error("Failed to read configuration", e);
			}

			HOMERUN_TIME = Integer.parseInt(properties.get("HOMERUN_TIME").toString());    //seconds

			ID_PET = Integer.parseInt(properties.get("ID_pet").toString());
			ID_TARGET = Integer.parseInt(properties.get("ID_target").toString());

			ID_DefaultCharacter = Integer.parseInt(properties.get("ID_DefaultCharacter").toString());
			ID_Warcryer = Integer.parseInt(properties.get("ID_Warcryer").toString());
			ID_Spoiler = Integer.parseInt(properties.get("ID_Spoiler").toString());
			ID_Prophet = Integer.parseInt(properties.get("ID_Prophet").toString());
			ID_Warlord = Integer.parseInt(properties.get("ID_Warlord").toString());
			ID_Swordsinger = Integer.parseInt(properties.get("ID_Swordsinger").toString());
			ID_Bladedancer = Integer.parseInt(properties.get("ID_Bladedancer").toString());
			ID_Warlock = Integer.parseInt(properties.get("ID_Warlock").toString());
			ID_Templeknight = Integer.parseInt(properties.get("ID_Templeknight").toString());
			ID_Necromancer = Integer.parseInt(properties.get("ID_Necromancer").toString());
			ID_Elvenelder = Integer.parseInt(properties.get("ID_Elvenelder").toString());

			partyPanelMatch.put(ID_DefaultCharacter,0);
			partyPanelMatch.put(ID_Elvenelder,1);
			partyPanelMatch.put(ID_Warcryer,2);
			partyPanelMatch.put(ID_Bladedancer,3);
			partyPanelMatch.put(ID_Swordsinger,4);
			partyPanelMatch.put(ID_Necromancer,5);
			partyPanelMatch.put(ID_Spoiler,6);
			partyPanelMatch.put(ID_Warlock,7);
			partyPanelMatch.put(ID_Templeknight,8);

			CHAT_TASK_LIST_LENGTH = Integer.parseInt(properties.get("CHAT_TASK_LIST_LENGTH").toString());
			INITIAL_DEBUG_MODE = Integer.parseInt(properties.get("INITIAL_DEBUG_MODE").toString());
			WINDOW_ACTIVATE_DELAY_MILLIS = Integer.parseInt(properties.get("WINDOW_ACTIVATE_DELAY_MILLIS").toString());

			int red, green, blue;
			red = Integer.parseInt(properties.get("PetTargetPartyHpColor_red").toString());
			green = Integer.parseInt(properties.get("PetTargetPartyHpColor_green").toString());
			blue = Integer.parseInt(properties.get("PetTargetPartyHpColor_blue").toString());
			SECONDARY_LIVING_CREATURE_HP_COLOR = new Color(red, green, blue);

			red = Integer.parseInt(properties.get("PartyMembersPetHpColor_red").toString());
			green = Integer.parseInt(properties.get("PartyMembersPetHpColor_green").toString());
			blue = Integer.parseInt(properties.get("PartyMembersPetHpColor_blue").toString());
			PARTY_MEMBERS_PET_HP_COLOR = new Color(red, green, blue);

			red = Integer.parseInt(properties.get("ChatColorParty_red").toString());
			green = Integer.parseInt(properties.get("ChatColorParty_green").toString());
			blue = Integer.parseInt(properties.get("ChatColorParty_blue").toString());
			CHAT_COLOR_PARTY = new Color(red, green, blue);

			red = Integer.parseInt(properties.get("ChatColorPrivate_red").toString());
			green = Integer.parseInt(properties.get("ChatColorPrivate_green").toString());
			blue = Integer.parseInt(properties.get("ChatColorPrivate_blue").toString());
			CHAT_COLOR_PRIVATE = new Color(red, green, blue);

		}

	}

	public GroupedVariables()
	{
		projectConstants = new GroupedVariables.ProjectConstants();
	}

	public static class ChatConstants
	{
		//		public final
		public Point coordinateLeft;
		public Point coordinateRight;

		//		ChatConstants()	todo: set all coordinates for decyphering caller name in static{}
		public static int convertSignatureToID(int signature)	//todo implement
		{
			return 0;
		}
	}
}
