package openhighlowstrategy;

import java.io.FileReader;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.github.wnameless.json.flattener.JsonFlattener;
//import com.github.wnameless.json.unflattener.JsonUnflattener;
import butlerforfyers.OtherPropertiesLoader;
import butlerforfyers.ButlerLogger;
import spreadsheethandler.GoogleSpreadsheetHandler;
import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.apache.logging.log4j.Logger;
import org.apache.log4j.Logger;
import butlerforfyers.OrderProcessor;

public class JSONDataAnalyzer 
{
	//private JSONParser parser;
	//private JSONObject jsonobject;
	//private Object object;
	//private String flattenedJSON;
	//private String nestedJSON;
	//private Map<String, Object> flattenedJSONMap;
	private String nifty50_old_json_data_file_path;
	private String nifty50_new_json_data_file_path;
	private String nifty100_old_json_data_file_path;
	private String nifty100_new_json_data_file_path;
	private String nifty50_file_path;
	private String nifty100_file_path;
	private Logger butlog;
	private List<String> script_names;
	private List<Double> last_traded_prices;
	private List<Double> open_prices;
	private List<Double> high_prices;
	private List<Double> low_prices;
	private GoogleSpreadsheetHandler sheet;
	private List<String> orders;
	private OrderProcessor orderprocessor;
	private OtherPropertiesLoader property=OtherPropertiesLoader.initialize();
	
	public JSONDataAnalyzer()
	{
		//this.parser=new JSONParser();
		//this.jsonobject=new JSONObject();
		//this.object=null;
		//this.flattenedJSON=null;
		//this.nestedJSON=null;
		//this.flattenedJSONMap=null;
		this.butlog=ButlerLogger.getButlerLogger();
		this.script_names=new ArrayList<String>();
		this.last_traded_prices=new ArrayList<Double>();
		this.open_prices=new ArrayList<Double>();
		this.high_prices=new ArrayList<Double>();
		this.low_prices=new ArrayList<Double>();
		this.orders=new ArrayList<String>();
		this.sheet=new GoogleSpreadsheetHandler();
		this.orderprocessor=new OrderProcessor();
		this.nifty50_old_json_data_file_path=this.property.getPropertyValue("nifty50_old_json_data_file_path");
		this.nifty50_new_json_data_file_path=this.property.getPropertyValue("nifty50_new_json_data_file_path");
		this.nifty100_old_json_data_file_path=this.property.getPropertyValue("nifty100_old_json_data_file_path");
		this.nifty100_new_json_data_file_path=this.property.getPropertyValue("nifty100_new_json_data_file_path");
		this.nifty50_file_path=null;
		this.nifty100_file_path=null;
	}
	private void analyzeJSONData()
	{
		if(((double)(new File(this.nifty50_new_json_data_file_path).length()/1024) > 5.00d))
		{
			this.nifty50_file_path=this.nifty50_new_json_data_file_path;
			this.getNewNifty50Data(this.nifty50_file_path, 0, 50);
		}
		else
		{
			this.nifty50_file_path=this.nifty50_old_json_data_file_path;
			this.getOldNifty50Data(this.nifty50_file_path,0,50);
		}
		if(((double)(new File(this.nifty100_new_json_data_file_path).length()/1024) > 5.00d))
		{
			this.nifty100_file_path=this.nifty100_new_json_data_file_path;
			this.getNewNifty100Data(this.nifty100_file_path, 0, 100);
		}
		else
		{
			this.nifty100_file_path=this.nifty100_old_json_data_file_path;
			this.getOldNifty100Data(this.nifty100_file_path, 0, 100);
		}
		//this.butlog.warn(this.nifty50_file_path+"\n"+this.nifty100_file_path);
	}
	private void getOldNifty100Data(String file_path, int loop_start, int loop_end)
	{
		try
		{
			Object object=new Object();
			JSONParser parser=new JSONParser();
			JSONObject jsonobject=new JSONObject();
			object=parser.parse(new FileReader(file_path));
			jsonobject=(JSONObject) object;
			//this.flattenedJSON=JsonFlattener.flatten(this.jsonobject.toString());
			//this.butlog.warn("Flattened JSON: \n"+this.flattenedJSON);
			Map<String, Object> flattenedJSONMap=JsonFlattener.flattenAsMap(jsonobject.toString());
			//this.butlog.warn("Flattened JSON:");
			//flattenedJSONMap.forEach((k,v) -> this.butlog.warn(k+" : "+v));
			for (int i=loop_start; i<loop_end; i++)
			{
				this.script_names.add((i+50), flattenedJSONMap.get("data["+i+"].symbol").toString());
				//this.last_traded_prices.add(i, Integer.valueOf(this.flattenedJSONMap.get("data["+i+"].ltP").toString().replaceAll(",", "")));
				this.last_traded_prices.add((i+50), NumberFormat.getNumberInstance(java.util.Locale.US).parse(flattenedJSONMap.get("data["+i+"].ltP").toString()).doubleValue());
				//this.open_prices.add(i, Integer.valueOf(this.flattenedJSONMap.get("data["+i+"].open").toString().replaceAll(",", "")));
				this.open_prices.add((i+50), NumberFormat.getNumberInstance(java.util.Locale.US).parse(flattenedJSONMap.get("data["+i+"].open").toString()).doubleValue());
				//this.high_prices.add(i, Integer.valueOf(this.flattenedJSONMap.get("data["+i+"].high").toString().replaceAll(",", "")));
				this.high_prices.add((i+50), NumberFormat.getNumberInstance(java.util.Locale.US).parse(flattenedJSONMap.get("data["+i+"].high").toString()).doubleValue());
				//this.low_prices.add(i, Integer.valueOf(this.flattenedJSONMap.get("data["+i+"].low").toString().replaceAll(",", "")));
				this.low_prices.add((i+50), NumberFormat.getNumberInstance(java.util.Locale.UK).parse(flattenedJSONMap.get("data["+i+"].low").toString()).doubleValue());
			}
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method: getOldNifty100Data(Parameters...) in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}
	private void getNewNifty100Data(String file_path, int loop_start, int loop_end)
	{
		try
		{
			Object object=new Object();
			JSONParser parser=new JSONParser();
			JSONObject jsonobject=new JSONObject();
			object=parser.parse(new FileReader(file_path));
			jsonobject=(JSONObject) object;
			//this.flattenedJSON=JsonFlattener.flatten(this.jsonobject.toString());
			//this.butlog.warn("Flattened JSON: \n"+this.flattenedJSON);
			Map<String, Object> flattenedJSONMap=JsonFlattener.flattenAsMap(jsonobject.toString());
			//flattenedJSONMap.forEach((k,v) -> this.butlog.warn(k+" : "+v));
			for (int i=loop_start; i<loop_end; i++)
			{
				//this.butlog.warn("script_names "+this.script_names.toString());
				//this.butlog.warn("script_names size = "+this.script_names.size());
				//this.butlog.warn("data["+(i+1)+"].symbol = "+flattenedJSONMap.get("data["+(i+1)+"].symbol").toString());
				this.script_names.add((i+50), flattenedJSONMap.get("data["+(i+1)+"].symbol").toString());
				//this.last_traded_prices.add(i, Integer.valueOf(this.flattenedJSONMap.get("data["+i+"].ltP").toString().replaceAll(",", "")));
				this.last_traded_prices.add((i+50), NumberFormat.getNumberInstance(java.util.Locale.US).parse(flattenedJSONMap.get("data["+(i+1)+"].lastPrice").toString()).doubleValue());
				//this.open_prices.add(, Integer.valueOf(this.flattenedJSONMap.get("data["+i+"].open").toString().replaceAll(",", "")));
				this.open_prices.add((i+50), NumberFormat.getNumberInstance(java.util.Locale.US).parse(flattenedJSONMap.get("data["+(i+1)+"].open").toString()).doubleValue());
				//this.high_prices.add(i, Integer.valueOf(this.flattenedJSONMap.get("data["+i+"].high").toString().replaceAll(",", "")));
				this.high_prices.add((i+50), NumberFormat.getNumberInstance(java.util.Locale.US).parse(flattenedJSONMap.get("data["+(i+1)+"].dayHigh").toString()).doubleValue());
				//this.low_prices.add(i, Integer.valueOf(this.flattenedJSONMap.get("data["+i+"].low").toString().replaceAll(",", "")));
				this.low_prices.add((i+50), NumberFormat.getNumberInstance(java.util.Locale.UK).parse(flattenedJSONMap.get("data["+(i+1)+"].dayLow").toString()).doubleValue());
			}
		}
		catch (Exception e)
		{
			this.butlog.error("Error/Exception in method: getNewNifty100Data(Parameters...) in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}
	private void getNewNifty50Data(String file_path, int loop_start, int loop_end)
	{
		try
		{
			Object object=new Object();
			JSONParser parser=new JSONParser();
			JSONObject jsonobject=new JSONObject();
			object=parser.parse(new FileReader(file_path));
			jsonobject=(JSONObject) object;
			//this.flattenedJSON=JsonFlattener.flatten(this.jsonobject.toString());
			//this.butlog.warn("Flattened JSON: \n"+this.flattenedJSON);
			Map<String, Object> flattenedJSONMap=JsonFlattener.flattenAsMap(jsonobject.toString());
			//this.butlog.warn("Flattened JSON:");
			//flattenedJSONMap.forEach((k,v) -> this.butlog.warn(k+" : "+v));
			for (int i=loop_start; i<loop_end; i++)
			{
				this.script_names.add(i, flattenedJSONMap.get("data["+(i+1)+"].symbol").toString());
				//this.last_traded_prices.add(i, Integer.valueOf(this.flattenedJSONMap.get("data["+i+"].ltP").toString().replaceAll(",", "")));
				this.last_traded_prices.add(i, NumberFormat.getNumberInstance(java.util.Locale.US).parse(flattenedJSONMap.get("data["+(i+1)+"].lastPrice").toString()).doubleValue());
				//this.open_prices.add(i, Integer.valueOf(this.flattenedJSONMap.get("data["+i+"].open").toString().replaceAll(",", "")));
				this.open_prices.add(i, NumberFormat.getNumberInstance(java.util.Locale.US).parse(flattenedJSONMap.get("data["+(i+1)+"].open").toString()).doubleValue());
				//this.high_prices.add(i, Integer.valueOf(this.flattenedJSONMap.get("data["+i+"].high").toString().replaceAll(",", "")));
				this.high_prices.add(i, NumberFormat.getNumberInstance(java.util.Locale.US).parse(flattenedJSONMap.get("data["+(i+1)+"].dayHigh").toString()).doubleValue());
				//this.low_prices.add(i, Integer.valueOf(this.flattenedJSONMap.get("data["+i+"].low").toString().replaceAll(",", "")));
				this.low_prices.add(i, NumberFormat.getNumberInstance(java.util.Locale.UK).parse(flattenedJSONMap.get("data["+(i+1)+"].dayLow").toString()).doubleValue());
			}
		}
		catch (Exception e)
		{
			this.butlog.error("Error/Exception in method: getNewNifty50Data(Parameters...) in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}
	private void getOldNifty50Data(String file_path, int loop_start, int loop_end)
	{
		try
		{
			Object object=new Object();
			JSONParser parser=new JSONParser();
			JSONObject jsonobject=new JSONObject();
			object=parser.parse(new FileReader(file_path));
			jsonobject=(JSONObject) object;
			//this.flattenedJSON=JsonFlattener.flatten(this.jsonobject.toString());
			//this.butlog.warn("Flattened JSON: \n"+this.flattenedJSON);
			Map<String, Object> flattenedJSONMap=JsonFlattener.flattenAsMap(jsonobject.toString());
			//this.butlog.warn("Flattened JSON:");
			//this.flattenedJSONMap.forEach((k,v) -> this.butlog.warn(k+" : "+v));
			for (int i=loop_start; i<loop_end; i++)
			{
				this.script_names.add(i, flattenedJSONMap.get("data["+i+"].symbol").toString());
				//this.last_traded_prices.add(i, Integer.valueOf(this.flattenedJSONMap.get("data["+i+"].ltP").toString().replaceAll(",", "")));
				this.last_traded_prices.add(i, NumberFormat.getNumberInstance(java.util.Locale.US).parse(flattenedJSONMap.get("data["+i+"].ltP").toString()).doubleValue());
				//this.open_prices.add(i, Integer.valueOf(this.flattenedJSONMap.get("data["+i+"].open").toString().replaceAll(",", "")));
				this.open_prices.add(i, NumberFormat.getNumberInstance(java.util.Locale.US).parse(flattenedJSONMap.get("data["+i+"].open").toString()).doubleValue());
				//this.high_prices.add(i, Integer.valueOf(this.flattenedJSONMap.get("data["+i+"].high").toString().replaceAll(",", "")));
				this.high_prices.add(i, NumberFormat.getNumberInstance(java.util.Locale.US).parse(flattenedJSONMap.get("data["+i+"].high").toString()).doubleValue());
				//this.low_prices.add(i, Integer.valueOf(this.flattenedJSONMap.get("data["+i+"].low").toString().replaceAll(",", "")));
				this.low_prices.add(i, NumberFormat.getNumberInstance(java.util.Locale.UK).parse(flattenedJSONMap.get("data["+i+"].low").toString()).doubleValue());
			}
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method: getOldNifty50Data(Parameters...) in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}
	public void analyzeDataAndPlaceOrder()
	{
		this.analyzeJSONData();
		this.sheet.updateSheetValues(this.script_names, this.last_traded_prices, this.open_prices, this.high_prices, this.low_prices);
		String final_order=sheet.getFinalOrder();
		if(final_order.isEmpty()) { this.butlog.error("Fianl Order is NULL"); }
		else if(final_order.equalsIgnoreCase("#N/A")) { this.butlog.error("Final Order is #N/A. Need to check Sheet.."); } 
		else 
		{ 
			try
			{
				//this.butlog.warn("Final Order is =\n"+final_order);
				String[] splitted_final_order=final_order.split(";");
				for(String order : splitted_final_order)
				{
					this.orders.add(order);
				}
				this.butlog.info("Sending Orders to Order Processor...");
				/*for(int i=0;i<this.orders.size(); i++)
				{
					this.butlog.warn(i+" -> "+this.orders.get(i));
				}*/
				this.orderprocessor.processOrder(this.orders);
			}
			catch(Exception e)
			{
				this.butlog.error("Ërror/Exception in method: analyzeDataAndPlaceOrder() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
				System.exit(-1);
			}
			
		}
		
	}
	
	/*private void tempfunction()
	{
		this.butlog.warn("Symbol"+"\t+"+"LTP"+"\t"+"Open"+"\t"+"High"+"\t"+"Low");
		for(int i=0; i<this.script_names.size(); i++)
		{
			//this.butlog.warn(i+" -> "+this.script_names.get(i));
			this.butlog.warn(this.script_names.get(i)+"\t"+this.last_traded_prices.get(i)+"\t"+this.open_prices.get(i)+"\t"+this.high_prices.get(i)+"\t"+this.low_prices.get(i));
		}
		
		//try {this.butlog.warn((NumberFormat.getCurrencyInstance().parse("2,850.80")).intValue()); } catch(Exception e) { e.printStackTrace();}
	}*/
	
	/*public static void main(String args[])
	{
		JSONDataAnalyzer obj=new JSONDataAnalyzer(); 
		obj.analyzeDataAndPlaceOrder();
	}*/
}
