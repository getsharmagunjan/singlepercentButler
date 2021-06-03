package butlerforfyers;

//import org.apache.log4j.BasicConfigurator;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.apache.log4j.Logger;
import java.io.FileInputStream;
import java.io.InputStream;

//import org.apache.logging.log4j.core.config.properties.PropertiesConfigurationBuilder;
import org.apache.log4j.PropertyConfigurator;

public class ButlerLogger {

	
	//public static Logger butlog = LogManager.getLogger(ButlerLogger.class.getName());
	public static Logger butlog;
	private final static String logger_properties_path = "/butler-system/butler-resources/log4j2.properties"; //ubuntu file
	//private final static String logger_properties_path = "B:\\resources\\log4j2.properties"; //local file
	//private final static String logger_properties_path = "C:\\butler-system\\butler-resources\\log4j2.properties"; //windows server file
	//private static OtherPropertiesLoader property=OtherPropertiesLoader.initialize();
	private static void butlogPropertiesLoader()
	{
		try
		{
			//System.out.println("Initiating Logging protocol as 'butlog'...");
			//logger_properties_path=property.getPropertyValue("logger_properties_path");
			//System.out.println("check logger path "+logger_properties_path);
			InputStream in=new FileInputStream(logger_properties_path);
			PropertyConfigurator.configure(in);
		}
		catch(Exception e)
		{
			System.out.println("Error/Exception in loading logger object");
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	
	public static Logger getButlerLogger(String classname)
	{
		butlogPropertiesLoader();
		//butlog=LogManager.getLogger(classname);
		butlog=Logger.getLogger(classname);
		return butlog;
	}
	public static Logger getButlerLogger()
	{
		butlogPropertiesLoader();
		butlog=Logger.getLogger(ButlerLogger.class.getName());
		butlog.warn("Logger Initiated");
		return butlog;
	}
	/*public static void main(String... args)
	{
		BasicConfigurator.configure();
		Logger log=ButlerLogger.getButlerLogger();
		log.info("loaded succesfully...");
	}*/
	
}
