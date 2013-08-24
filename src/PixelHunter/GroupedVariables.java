package PixelHunter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.Properties;

/**
 * User: mrk
 * Date: 8/24/13; Time: 5:53 AM
 */
public class GroupedVariables
{
	public class HpConstants
	{
		public Color color;
		public Point coordinateLeft;
		public Point coordinateRight;
	}

	public class ChatConstants
	{
		public Color color;
		public Point coordinateLeft;
		public Point coordinateRight;
	}

	static final class ProjectConstants{

		static final int ID_PET;
		static final int ID_TARGET;

		static final Color SECONDARY_LIVING_CREATURE_HP_COLOR;
		static final Color PARTY_MEMBERS_PET_HP_COLOR;
		static final Color CHAT_COLOR_PARTY;
		static final Color CHAT_COLOR_PRIVATE;

		private static final Logger logger = LoggerFactory.getLogger(ProjectConstants.class);

		static {
			Properties properties = new Properties();
			try {
				properties.load(ProjectConstants.class.getClassLoader().getResourceAsStream("inputData"));
			} catch (IOException e) {
				logger.error("Failed to read input configuration file", e);
				throw new Error("Failed to read configuration", e);
			}

			ID_PET = Integer.parseInt(properties.get("ID_pet").toString());
			ID_TARGET = Integer.parseInt(properties.get("ID_target").toString());

			int red,green,blue;
			red	=	Integer.parseInt(properties.get("PetTargetPartyHpColor_red").toString());
			green	=	Integer.parseInt(properties.get("PetTargetPartyHpColor_green").toString());
			blue	=	Integer.parseInt(properties.get("PetTargetPartyHpColor_blue").toString());
			SECONDARY_LIVING_CREATURE_HP_COLOR = new Color(red,green,blue);

			red	=	Integer.parseInt(properties.get("PartyMembersPetHpColor_red").toString());
			green	=	Integer.parseInt(properties.get("PartyMembersPetHpColor_green").toString());
			blue	=	Integer.parseInt(properties.get("PartyMembersPetHpColor_blue").toString());
			PARTY_MEMBERS_PET_HP_COLOR= new Color(red,green,blue);

			red	=	Integer.parseInt(properties.get("ChatColorParty_red").toString());
			green	=	Integer.parseInt(properties.get("ChatColorParty_green").toString());
			blue	=	Integer.parseInt(properties.get("ChatColorParty_blue").toString());
			CHAT_COLOR_PARTY	=	new Color(red,green,blue);

			red	=	Integer.parseInt(properties.get("ChatColorParty_red").toString());
			green	=	Integer.parseInt(properties.get("ChatColorParty_green").toString());
			blue	=	Integer.parseInt(properties.get("ChatColorParty_blue").toString());
			CHAT_COLOR_PRIVATE	=	new Color(red,green,blue);
		}

	}
}
