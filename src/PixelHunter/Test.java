package PixelHunter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;

public class Test
{


	public static void main(String args[])
	{

		Logger logger = LoggerFactory.getLogger(GroupedVariables.ProjectConstants.class);
		logger.trace("tra");
		System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "info");
		logger.info("info");
		System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");
		logger.error("err");
		logger.warn("warn");

	}

}
