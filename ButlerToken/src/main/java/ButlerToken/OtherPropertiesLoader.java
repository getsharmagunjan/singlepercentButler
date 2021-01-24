package ButlerToken;

//import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Logger;

public class OtherPropertiesLoader //implements AutoCloseable 
{
	private Logger butlog;
	//private String other_config_file_location="B:\\Eclipse Workplace\\ButlerToken\\src\\main\\resources\\other_config.xml";
	private String other_config_file_location="/other_config.xml";
	private static InputStream in;
	private static final Properties property=new Properties();
	private static final OtherPropertiesLoader instance= new OtherPropertiesLoader();
	
	private OtherPropertiesLoader()
	{
		try
		{
			//System.out.println(Class.class.getName());
			this.butlog=ButlerLogger.getButlerLogger(this.getClass().getName());
			//in=new FileInputStream(other_config_file_location);
			in=this.getClass().getResourceAsStream(other_config_file_location);
			property.loadFromXML(in);
			in.close();
			butlog.info("Loaded other Configurations, ready to return values");
		}
		catch (Exception e)
		{
			butlog.error("Error/Exception in constructor: OtherPropertiesLoader() in "+Class.class.getName()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}
	
	public static OtherPropertiesLoader initialize()
	{
		return instance;
	}
	
	public String getPropertyValue(String key)
	{
		String value;
		value=property.getProperty(key);
		return value;
	}
	
	/*public void close()
	{
		in=null;
		butlog=null;
		other_config_file_location=null;
		System.gc();
	}*/
	
	/*public static void main(String args[])
	{
		OtherPropertiesLoader loader=OtherPropertiesLoader.initialize();
				String s;
				s=loader.getPropertyValue("token_file_location");
		loader.butlog.warn("token_file_location = "+s);
		OtherPropertiesLoader loadernew=OtherPropertiesLoader.initialize();
		s=loadernew.getPropertyValue("token_file_location");
		loader.butlog.warn("token_file_location = "+s);
		//loader.close();
	} */
}
