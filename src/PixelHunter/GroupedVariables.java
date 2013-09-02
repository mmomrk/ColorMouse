package PixelHunter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * User: mrk
 * Date: 8/24/13; Time: 5:53 AM
 */
public class GroupedVariables
{

	public static GroupedVariables.ProjectConstants projectConstants;


	public static class HpConstants
	{
		public final Color color;
		public final Point coordinateLeft;
		public final Point coordinateRight;

		public HpConstants(Color color, Point left, Point right)
		{
			this.color = color;
			this.coordinateLeft = left;
			this.coordinateRight = right;
		}
	}


	public static class ChatConstants
	{
		//		public final
		public Point coordinateLeft;
		public Point coordinateRight;
//		ChatConstants()	todo: set all coordinates for decyphering caller name in static{}
	}

	public static final class ProjectConstants
	{
		//refactor all these were static.note this
		public static final int ID_PET;
		public static final int ID_TARGET;

		public static final Color SECONDARY_LIVING_CREATURE_HP_COLOR;
		public static final Color PARTY_MEMBERS_PET_HP_COLOR;
		public static final Color CHAT_COLOR_PARTY;
		public static final Color CHAT_COLOR_PRIVATE;

		public ProjectConstants()
		{

		}

		private static final Logger logger = LoggerFactory.getLogger(GroupedVariables.ProjectConstants.class);


		private void main()
		{

			System.out.println("main of grouped variables" + ID_PET + " " + ID_TARGET);
		}

		static {
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream("resources/inputData"));
//				properties.load(GroupedVariables.ProjectConstants.class.getClassLoader().getResourceAsStream("inputData"));
			} catch (IOException e) {
				logger.error("Failed to read input configuration file", e);
				throw new Error("Failed to read configuration", e);
			}

			ID_PET = Integer.parseInt(properties.get("ID_pet").toString());
			ID_TARGET = Integer.parseInt(properties.get("ID_target").toString());

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

			red = Integer.parseInt(properties.get("ChatColorParty_red").toString());
			green = Integer.parseInt(properties.get("ChatColorParty_green").toString());
			blue = Integer.parseInt(properties.get("ChatColorParty_blue").toString());
			CHAT_COLOR_PRIVATE = new Color(red, green, blue);

		}

	}

	public GroupedVariables()
	{
		projectConstants	=	new GroupedVariables.ProjectConstants();
	}
}
