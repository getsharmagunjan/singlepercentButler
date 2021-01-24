package butlerforfyers;

import java.io.FileInputStream;
//import java.io.FileInputStream;
import java.io.InputStream;
//import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.apache.logging.log4j.Logger;
import org.apache.log4j.Logger;

public class DBPropertiesLoader 
{
	//private static final String db_config_file_name="/home/ubuntu/butler-system/butler-resources/db_config.xml"; //ubuntu file
	//private static final String db_config_file_name="B:\\resources\\db_config.xml"; //local file
	private static final String db_config_file_name="C:\\butler-system\\butler-resources\\db_config.xml"; //windows server file
	//private static final String db_config_file_name="/db_config.xml";
	private static DBPropertiesLoader instance=new DBPropertiesLoader();
	private static Properties properties;
	//private InputStream in;
	private static InputStream fin;
	private String output=null;
	private Logger butlog;
	
	DBPropertiesLoader()
	{
		try
		{
			properties=new Properties();
			this.butlog=ButlerLogger.getButlerLogger(this.getClass().getName());
			//fin=new FileInputStream(db_config_file_name);
			//fin=this.getClass().getResourceAsStream(db_config_file_name);
			fin=new FileInputStream(db_config_file_name);
			properties.loadFromXML(fin);
			fin.close();
			this.butlog.info("Loaded DB Configurations, ready to return values");
		}
		catch (Exception e)
		{
			this.butlog.error("Error/Exception in constructor: DBPropertiesLoader() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}
	
	public static DBPropertiesLoader initialize()
	{
		return instance;
	}
	
	public String getDBPropertyfromPopertyLoader(String property_name)
	{
		this.output=properties.getProperty(property_name);
		//this.butlog.warn("Check "+property_name+" = "+this.output);
		//this.butlog.warn("Check ths also = \n"+this.properties.propertyNames().toString());
		return this.output;
	}
	
	/*public static void main(String args[])
	{
		DBPropertiesLoader load=new DBPropertiesLoader();
		load.getDBPropertyfromPopertyLoader("connectionURL");
	} */
}
