package butlerforfyers;

import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.apache.logging.log4j.Logger;
import org.apache.log4j.Logger;
//import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

public class ButlerResponseHandler {

	private Logger butlog=ButlerLogger.getButlerLogger(this.getClass().getName());
	private JSONParser jsonparser=null;
	//private static JSONArray jsonarray=null;
	private JSONObject jsonobject=null;
	private String returnstring=null;
	
	public String getResponseAttribute(String response, String tofind)
	{
		
		try
		{
			//butlog.info("in getResponseAttribute");
			//System.out.println("I am here.");
			jsonparser = new JSONParser();
			Object obj = jsonparser.parse(response);
			//jsonarray = (JSONArray) obj;
			jsonobject = (JSONObject) obj;
			returnstring = jsonobject.get(tofind).toString();
			butlog.info("Response parsing...");
			//this.butlog.warn("Parsed Value of "+tofind+" is = "+returnstring);
			return returnstring;
		}
		catch (Exception e)
		{
			butlog.error("error in parsing response in method:getResponseAttribute(String response, String tofind) in"+this.getClass().toString()+" is=/n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
			return null;
		}
		/*finally
		{
			return returnstring;
		}*/
		
		
	}

}
