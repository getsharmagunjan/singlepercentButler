package ButlerToken;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ButlerLogger {

	
	//public static Logger butlog = LogManager.getLogger(ButlerLogger.class.getName());
	public static Logger butlog;
	
	public static Logger getButlerLogger(String classname)
	{
		butlog=LogManager.getLogger(classname);
		return butlog;
	}
	public static Logger getButlerLogger()
	{
		butlog=LogManager.getLogger(ButlerLogger.class.getName());
		return butlog;
	}
	
}
