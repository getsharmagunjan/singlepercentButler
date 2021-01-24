package ButlerToken;

//import java.io.FileReader;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.github.wnameless.json.flattener.JsonFlattener;
import strategies.OpenHighLow_GoogleSpreadsheetHandler;
import okhttp3.Response;

public class FundsHandler implements AutoCloseable
{
	private Logger butlog;
	private String funds_request_url;
	private Response response;
	private String responsestring;
	private RESTConnectionHandler restconnection;
	private ButlerResponseHandler responsehandler;
	private ButlerMaster master;
	private String response_message_text;
	private String response_message_value;
	private String invalid_token_message;
	private double availablefund;
	private OpenHighLow_GoogleSpreadsheetHandler gsheet;
	private OtherPropertiesLoader property=OtherPropertiesLoader.initialize();
	
	FundsHandler()
	{
		this.butlog=ButlerLogger.getButlerLogger(this.getClass().getName());
		this.funds_request_url=this.property.getPropertyValue("funds_request_url");
		this.response=null;
		this.responsestring=null;
		this.response_message_value=null;
		this.availablefund=0.0d;
		this.invalid_token_message=this.property.getPropertyValue("invalid_token_message");
		this.response_message_text=this.property.getPropertyValue("response_message_text");
		this.restconnection=new RESTConnectionHandler();
		this.responsehandler=new ButlerResponseHandler();
		this.master=new ButlerMaster();
		this.gsheet=new OpenHighLow_GoogleSpreadsheetHandler();
	}
	@Override
	public void close()
	{
		this.funds_request_url=null;
		this.response.close();
		this.responsestring=null;
		this.response_message_value=null;
		this.invalid_token_message=null;
		this.response_message_text=null;
		this.restconnection=null;
		this.responsehandler=null;
		this.master=null;
		this.butlog=null;
		this.availablefund=0.0d;
		this.gsheet=null;
		System.gc();
	}	
	public void getFunds()
	{
		try
		{
			this.butlog.info("Retriving Funds..");
			this.response=this.restconnection.sendRequest(this.funds_request_url);
			this.butlog.info("Funds Retrieved Successfully..");
			if(!(this.response==null))
			{
				this.responsestring=this.response.body().string();
				//this.butlog.warn("The funds response received is =\n"+this.responsestring);
				this.response_message_value=this.responsehandler.getResponseAttribute(this.responsestring, this.response_message_text);
				//this.butlog.warn(this.response_message_value);
				if(this.response_message_value.equalsIgnoreCase(this.invalid_token_message))
				{
					this.butlog.info("Refreshing Token..");
					this.master.masterCommands();
					this.butlog.info("Refreshing Token Completed.");
					this.getFunds();
				}
				else
				{
					this.processResponse();
					this.updateFundToSpreadsheet();
				}
			}
			else {	this.butlog.info("Received funds response is NULL. Request manual intervention..");}
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method: getFunds() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
		finally
		{
			this.butlog.info("Closing Funds Handler Resources..");
			this.close();
		}
		
	}
	private void processResponse()
	{
		try
		{
			Object object=new JSONParser().parse(this.responsestring);
			JSONObject jsonobject=new JSONObject();
			jsonobject=(JSONObject) object;	
			//this.flattenedJSON=JsonFlattener.flatten(this.jsonobject.toString());
			//this.butlog.warn("Flattened JSON: \n"+this.flattenedJSON);
			Map<String, Object> flattenedJSONMap=JsonFlattener.flattenAsMap(jsonobject.toString());
			//this.butlog.warn("Flattened JSON:");
			//flattenedJSONMap.forEach((k,v) -> this.butlog.warn(k+" : "+v));
			this.availablefund=Double.parseDouble(flattenedJSONMap.get("fund_limit[9].equityAmount").toString()); //this will get Available amount.
			this.butlog.warn("Available Balance is = "+this.availablefund);
		}
		catch (Exception e)
		{
			this.butlog.error("Error/Exception in method: processResponse() in "+this.getClass().toString()+" \n="+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}
	private void updateFundToSpreadsheet()
	{
		this.gsheet.updateFunds(this.availablefund);
	}
	public static void main(String... args)
	{
		FundsHandler fund=new FundsHandler();
		fund.getFunds();
		fund.close();
	}
}
