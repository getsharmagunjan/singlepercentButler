// OLD CODE

/*package positionshandlerforfyers;

import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.math3.util.Precision;
//import org.apache.logging.log4j.Logger;
import org.apache.log4j.Logger;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.github.wnameless.json.flattener.JsonFlattener;
import butlerforfyers.RESTConnectionHandler;
import butlerforfyers.DBConnection;
import butlerforfyers.OtherPropertiesLoader;
import butlerforfyers.ButlerLogger;
//import java.nio.file.Files;
//import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.sql.ResultSet;
//import java.time.LocalDateTime;

public class PositionsHandler implements AutoCloseable 
{
	int size;
	private Logger butlog;
	private String positions_url;
	private String modify_order_url;
	private String get_orders_url;
	private String exit_positions_url;
	private Response response;
	private String responseValue;
	private RESTConnectionHandler restconnection;
	Map<String, Object> flattenedJSONMap;
	List<String> symbol;
	List<String> dbsymbol;
	List<Integer> dbqty;
	List<String> dbside;
	List<Float> dbstoploss_initial;
	List<Float> dbstoploss_modified;
	List<String> dbbutler_record_id;
	List<Float> dbpnl_percentage;
	List<String> dbbroker_order_ref_no; 
	List<Integer> netQty;
	List<Float> sellAvg;
	List<Float> buyAvg;
	List<Float> pl;
	List<String> orderId;
	List<Integer> orderFilledQty;
	float running_profit_percentage;
	float lockin_profit_percentage;
	float mod_stoploss;
	float mod_stoploss_before_round;
	DBConnection dbconnection;
	ResultSet rs;
	int sizeOfOrders;
	private OtherPropertiesLoader property=OtherPropertiesLoader.initialize(); 
	
	PositionsHandler()
	{
		this.size=0;
		this.sizeOfOrders=0;
		this.running_profit_percentage=0.00f;
		this.lockin_profit_percentage=0.00f;
		this.mod_stoploss=0.00f;
		this.mod_stoploss_before_round=0.00f;
		this.response=null;
		this.responseValue=null;
		this.rs=null;
		this.symbol=new ArrayList<String>();
		this.dbsymbol=new ArrayList<String>();
		this.dbbutler_record_id=new ArrayList<String>();
		this.netQty=new ArrayList<Integer>();
		this.sellAvg=new ArrayList<Float>();
		this.buyAvg=new ArrayList<Float>();
		this.pl=new ArrayList<Float>();
		this.dbqty=new ArrayList<Integer>();
		this.dbside=new ArrayList<String>();
		this.dbstoploss_initial=new ArrayList<Float>();
		this.dbstoploss_modified=new ArrayList<Float>();
		this.dbpnl_percentage=new ArrayList<Float>();
		this.dbbroker_order_ref_no=new ArrayList<String>();
		this.orderId=new ArrayList<String>();
		this.orderFilledQty=new ArrayList<Integer>();
		this.dbconnection=new DBConnection();
		this.flattenedJSONMap=new HashMap<String, Object>();
		this.positions_url=this.property.getPropertyValue("positions_url");
		this.modify_order_url=this.property.getPropertyValue("modify_order_url");
		this.get_orders_url=this.property.getPropertyValue("get_orders_url");
		this.exit_positions_url=this.property.getPropertyValue("exit_positions_url");
		this.restconnection=new RESTConnectionHandler();
		this.butlog=ButlerLogger.getButlerLogger(this.getClass().getName());
	}
	public void close()
	{
		this.size=0;
		this.sizeOfOrders=0;
		this.running_profit_percentage=0.00f;
		this.lockin_profit_percentage=0.00f;
		this.mod_stoploss=0.00f;
		this.response.close();
		this.responseValue=null;
		this.rs=null;
		this.symbol=null;
		this.dbsymbol=null;
		this.dbbutler_record_id=null;
		this.netQty=null;
		this.sellAvg=null;
		this.buyAvg=null;
		this.pl=null;
		this.dbqty=null;
		this.dbside=null;
		this.dbstoploss_initial=null;
		this.dbstoploss_modified=null;
		this.dbpnl_percentage=null;
		this.dbbroker_order_ref_no=null;
		this.orderId=null;
		this.orderFilledQty=null;
		this.dbconnection.close();
		this.flattenedJSONMap=null;
		this.positions_url=null;
		this.modify_order_url=null;
		this.get_orders_url=null;
		this.restconnection.close();
		this.butlog.info("Closing Positions Resources");
		this.butlog=null;
		System.gc();
		System.exit(0);
	}
	public void handlePositions()
	{
		this.handlePositionsPrivately();
		//this.handlePositionsPrivatelyTest();
		this.processResponseToList();
		this.loadPresentOrders();
		this.getTodayPositions();
		this.processPositions();
	}
	private void handlePositionsPrivately()
	{
		try
		{
			this.response=this.restconnection.sendRequest(this.positions_url);
			this.responseValue=this.response.body().string();
			//this.butlog.warn("response is =\n"+this.responseValue);
			Object object=new Object();
			JSONParser parser=new JSONParser();
			JSONObject jsonobject=new JSONObject();
			object=parser.parse(this.responseValue);
			jsonobject=(JSONObject) object;
			this.flattenedJSONMap=JsonFlattener.flattenAsMap(jsonobject.toString());
			//flattenedJSONMap.forEach((k,v) -> this.butlog.warn(k+" : "+v));
			Iterator<Map.Entry<String, Object>> itr =this.flattenedJSONMap.entrySet().iterator();
			while(itr.hasNext())
			{
				Map.Entry<String, Object> entry=itr.next();
				if(entry.getKey().contains(".symbol"))
				{
					this.size++;
					//this.butlog.warn(entry.getKey());
				}
			}
			this.butlog.warn("size is ="+this.size);
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method: handlePositionsPrivately() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
		
	}
	//private void handlePositionsPrivatelyTest()
	//{
	//  try
	//	{
	//		this.responseValue=new String(Files.readAllBytes(Paths.get("B:\\Eclipse Workplace\\ButlerToken\\src\\main\\resources\\temp.txt")));
			//this.butlog.warn("response is =\n"+this.responseValue);
	//		Object object=new Object();
	//		JSONParser parser=new JSONParser();
	//		JSONObject jsonobject=new JSONObject();
	//		object=parser.parse(this.responseValue);
	//		jsonobject=(JSONObject) object;
	//		this.flattenedJSONMap=JsonFlattener.flattenAsMap(jsonobject.toString());
			//flattenedJSONMap.forEach((k,v) -> this.butlog.warn(k+" : "+v));
	//		Iterator<Map.Entry<String, Object>> itr =this.flattenedJSONMap.entrySet().iterator();
	//		while(itr.hasNext())
	//		{
	//			Map.Entry<String, Object> entry=itr.next();
	//			if(entry.getKey().contains(".symbol"))
	//			{
	//				this.size++;
					//this.butlog.warn(entry.getKey());
	//			}
	//		}
	//		this.butlog.warn("size is ="+this.size);
	//	}
	//	catch(Exception e)
	//	{
	//		this.butlog.error("Error/Exception in method: handlePositionsPrivately() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
	//		System.exit(-1);
	//	}
		
//	}
	private void processResponseToList()
	{
		String[] symbol_with_EQ=null;
		String[] symbol_without_EQ=null;
		String symbol=null;
		try
		{
			for(int i=0;i<this.size;i++)
			{
				symbol_with_EQ = this.flattenedJSONMap.get("netPositions["+i+"].symbol").toString().trim().split("NSE:");
				symbol_without_EQ=symbol_with_EQ[1].split("-EQ");
				symbol=symbol_without_EQ[0];
				this.symbol.add(symbol);
				this.netQty.add(Integer.parseInt(this.flattenedJSONMap.get("netPositions["+i+"].netQty").toString().trim()));
				this.sellAvg.add(Precision.round(Float.parseFloat(this.flattenedJSONMap.get("netPositions["+i+"].sellAvg").toString().trim()),2));
				this.buyAvg.add(Precision.round(Float.parseFloat(this.flattenedJSONMap.get("netPositions["+i+"].buyAvg").toString().trim()),2));
				this.pl.add(Precision.round(Float.parseFloat(this.flattenedJSONMap.get("netPositions["+i+"].pl").toString().trim()),2));
			}
	//		this.butlog.warn("symbol"+"\t"+"net qty"+"\t"+"sell avg"+"\t"+"buyAvg"+"\t"+"PL");
	//		for (int j=0; j<this.size; j++)
	//		{
	//			this.butlog.warn(this.symbol.get(j)+"\t"+this.netQty.get(j)+"\t"+this.sellAvg.get(j)+"\t"+this.buyAvg.get(j)+"\t"+this.pl.get(j));
		}
		catch (Exception e)
		{
			this.butlog.error("Error/Exception in method: processResponseToList() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}
	private void getTodayPositions()
	{
		try
		{
			String broker_ref_no=null;
			String[] temp=null;
			this.rs=this.dbconnection.positionSymbolsOfToday();
			if (!(this.rs==null))
			{
				while(this.rs.next())
				{
					this.dbsymbol.add(this.rs.getString("symbol"));
					this.dbbutler_record_id.add(Integer.toString(this.rs.getInt("butler_order_id")));
					this.dbqty.add(this.rs.getInt("qty"));
					this.dbside.add(this.rs.getString("side"));
					this.dbstoploss_initial.add(Precision.round(Float.parseFloat(this.rs.getString("stoploss")),2));
					this.dbpnl_percentage.add(Precision.round(Float.parseFloat(this.rs.getString("pnl_percentage")),2));
					this.dbstoploss_modified.add(Precision.round(Float.parseFloat(this.rs.getString("mod_stoploss")),2));
					temp=this.rs.getString("broker_order_ref_no").split("-");
					broker_ref_no=temp[0];
					//this.butlog.warn("Check this broker ref no for "+this.rs.getString("symbol")+" is = "+broker_ref_no);
					this.dbbroker_order_ref_no.add(broker_ref_no);
				}
			}
			else
			{
				this.butlog.error("No Orders for today. Good Bye..!!!");
				System.exit(1);
			}
		}
		catch (Exception e)
		{
			this.butlog.error("Error/Exception in method: getTodayPositions() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}
	private void processPositions()
	{
		try
		{
			for(int d=0;d<this.dbsymbol.size();d++)
			{
				for(int r=0; r<this.symbol.size();r++)
				{
					if(this.dbsymbol.get(d).equalsIgnoreCase(this.symbol.get(r)))
					{
						this.mod_stoploss=0.00f;
						float total_buy_value=Precision.round(this.buyAvg.get(r)*this.dbqty.get(d),2);
						this.running_profit_percentage=Precision.round((((this.pl.get(r))/total_buy_value)*100),2);
						if(this.netQty.get(r)==0)
						{
							this.butlog.warn("symbol : "+this.symbol.get(r)+" has net qty of = "+this.netQty.get(r)+". Hence sending for DB updation.");
							this.butlog.warn(this.dbbutler_record_id.get(d)+" "+this.running_profit_percentage+" "+this.pl.get(r)+" "+this.mod_stoploss);
							this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.running_profit_percentage, this.pl.get(r), this.mod_stoploss);
							this.butlog.info(this.symbol.get(r)+"("+this.netQty.get(r)+") - SL HIT or Forced Closed -"+" data updated.");
						}
						else
						{
							
							if(this.dbside.get(d).equalsIgnoreCase("Buy"))
							{
								//float total_buy_value=this.buyAvg.get(r)*this.dbqty.get(d);
								//this.running_profit_percentage=((this.pl.get(r))/total_buy_value)*100;
								this.butlog.warn("symbol : "+this.symbol.get(r)+" has BUY order with running net qty of = "+Math.abs(this.netQty.get(r))+". Hence processing positions.");
								this.butlog.warn("\npl is "+this.pl.get(r)+"\ntotal_buy_value is "+total_buy_value+"\nRunning profit percentage is "+this.running_profit_percentage+"%");
								this.lockin_profit_percentage=this.getLockinProfitPercentage(this.running_profit_percentage);
								this.butlog.warn("lockin percentage is "+this.lockin_profit_percentage);
								if((this.running_profit_percentage>0.00f) && (this.running_profit_percentage>this.dbpnl_percentage.get(d)))
								{
									float lockin_profit=Precision.round(((total_buy_value*this.lockin_profit_percentage)/100),2);
									float stoploss_increment_points=Precision.round((lockin_profit/this.dbqty.get(d)),2);
									this.butlog.warn("lockin profit = "+lockin_profit);
									this.butlog.warn("stoploss increment points = "+stoploss_increment_points);
									this.mod_stoploss_before_round=Precision.round((this.dbstoploss_initial.get(d)+stoploss_increment_points),2);
									this.mod_stoploss=Precision.round(this.closestModifiedStoploss(this.mod_stoploss_before_round, 0.05f),2);
									this.butlog.info("Modified Stoploss (before rounding) = "+this.mod_stoploss_before_round+" Modified Stoploss (after rounding) = "+this.mod_stoploss);
									this.butlog.warn("Initial stoploss = "+this.dbstoploss_initial.get(d)+" Modified stoploss = "+this.dbstoploss_modified.get(d)+" new stoploss = "+this.mod_stoploss);
									if((lockin_profit>0.00f) && (this.mod_stoploss>this.dbstoploss_initial.get(d)) && (this.mod_stoploss>this.dbstoploss_modified.get(d)))
									{
										this.modifyOrder(this.dbbroker_order_ref_no.get(d),this.mod_stoploss, this.dbsymbol.get(d));
										this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.lockin_profit_percentage, lockin_profit, this.mod_stoploss);
										this.butlog.info(this.symbol.get(r)+"("+this.netQty.get(r)+") - Buy -"+" data updated.");
									}
									else
									{
										this.butlog.info(this.symbol.get(r)+"("+this.netQty.get(r)+") - Buy -"+" has NO profit to lock (OR) Present stop-loss is sufficient.");
									}
								}
								else if(this.running_profit_percentage<=-1.00f)
								{
									this.butlog.info("Since "+this.symbol.get(r)+" has Loss of "+this.running_profit_percentage+"%, hence exiting this position.");
									this.exitPosition(this.symbol.get(r));
									this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.running_profit_percentage, this.pl.get(r), this.mod_stoploss);
									this.butlog.info(this.symbol.get(r)+" data updated to database.");
								}
								else
								{
									this.butlog.info("Since "+this.symbol.get(r)+"("+this.netQty.get(r)+") - Buy - has P&L of "+this.running_profit_percentage+"%, which is between (-1) and zero(0). hence updating to DB");
									this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.running_profit_percentage, this.pl.get(r), this.mod_stoploss);
								}
							}
							else if(this.dbside.get(d).equalsIgnoreCase("Sell"))
							{
								this.butlog.warn("symbol : "+this.symbol.get(r)+" has SELL order with running net qty of = "+Math.abs(this.netQty.get(r))+". Hence processing positions.");
								float total_sell_value=Precision.round((this.sellAvg.get(r)*this.dbqty.get(d)),2);
								this.running_profit_percentage=Precision.round((((this.pl.get(r))/total_sell_value)*100),2);
								this.butlog.warn("\npl is "+this.pl.get(r)+"\ntotal_sell_value is "+total_sell_value+"\nRunning profit percentage is "+this.running_profit_percentage+"%");
								this.lockin_profit_percentage=this.getLockinProfitPercentage(this.running_profit_percentage);
								this.butlog.warn("lockin percentage is "+this.lockin_profit_percentage);
								if((this.running_profit_percentage>0.00f) && (this.running_profit_percentage>this.dbpnl_percentage.get(d)))
								{
									float lockin_profit=Precision.round(((total_sell_value*this.lockin_profit_percentage)/100),2);
									float stoploss_increment_points=Precision.round(((lockin_profit)/(this.dbqty.get(d))),2);
									this.butlog.warn("lockin profit = "+lockin_profit);
									this.butlog.warn("stoploss increment points = "+stoploss_increment_points);
									this.mod_stoploss_before_round=Precision.round((this.dbstoploss_initial.get(d)+stoploss_increment_points),2);
									this.mod_stoploss=Precision.round(this.closestModifiedStoploss(this.mod_stoploss_before_round, 0.05f),2);
									this.butlog.info("Modified Stoploss (before rounding) = "+this.mod_stoploss_before_round+" Modified Stoploss (after rounding) = "+this.mod_stoploss);
									this.butlog.warn("Initial stoploss = "+this.dbstoploss_initial.get(d)+" Modified stoploss = "+this.dbstoploss_modified.get(d)+" new stoploss = "+this.mod_stoploss);
									if((lockin_profit>0.00f) && (this.mod_stoploss>this.dbstoploss_initial.get(d)) && (this.mod_stoploss>this.dbstoploss_modified.get(d)))
									{
										this.modifyOrder(this.dbbroker_order_ref_no.get(d),this.mod_stoploss, this.dbsymbol.get(d));
										this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.lockin_profit_percentage, lockin_profit, this.mod_stoploss);
										this.butlog.info(this.symbol.get(r)+"("+this.netQty.get(r)+") - Sell -"+" data updated.");
									}
									else
									{
										this.butlog.info(this.symbol.get(r)+"("+this.netQty.get(r)+") - Sell -"+" has NO profit to lock (OR) Present stop-loss is sufficient.");
									}
								}
								else if(this.running_profit_percentage<=-1.00f)
								{
									this.butlog.info("Since "+this.symbol.get(r)+" has Loss of "+this.running_profit_percentage+"%, hence exiting this position.");
									this.exitPosition(this.symbol.get(r));
									this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.running_profit_percentage, this.pl.get(r), this.mod_stoploss);
									this.butlog.info(this.symbol.get(r)+" data updated to database.");
								}
								else
								{
									this.butlog.info("Since "+this.symbol.get(r)+"("+this.netQty.get(r)+") - Sell - has P&L of "+this.running_profit_percentage+"%, which is between (-1) and zero(0). hence updating to DB");
									this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.running_profit_percentage, this.pl.get(r), this.mod_stoploss);
								}
							}
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method: processPositions() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}

	private void loadPresentOrders()
	{
		try
		{
			Response response=this.restconnection.sendRequest(this.get_orders_url);
			String responseValue=response.body().string();
			//this.butlog.warn("Present Orders are =\n"+responseValue);
			Object object=new Object();
			JSONParser parser=new JSONParser();
			JSONObject jsonobject=new JSONObject();
			object=parser.parse(responseValue);
			jsonobject=(JSONObject) object;
			Map<String, Object> flattenedJSONMap=JsonFlattener.flattenAsMap(jsonobject.toString());
			//flattenedJSONMap.forEach((k,v) -> this.butlog.warn(k+" : "+v));
			Iterator<Map.Entry<String, Object>> itr =flattenedJSONMap.entrySet().iterator();
			while(itr.hasNext())
			{
				Map.Entry<String, Object> entry=itr.next();
				if(entry.getKey().contains(".symbol"))
				{
					sizeOfOrders++;
					//this.butlog.warn(entry.getKey());
				}
			}
			this.butlog.warn("size of Orders are ="+sizeOfOrders);
			for (int i=0; i<sizeOfOrders; i++)
			{
				this.orderId.add(i, flattenedJSONMap.get("orderBook["+i+"].id").toString());
				this.orderFilledQty.add(i, Integer.parseInt(flattenedJSONMap.get("orderBook["+i+"].filledQty").toString()));
			}
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method: loadPresentOrders() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}	
	private void modifyOrder(String id, float stoploss, String symbol)
	{
		try
		{
			for(int i=0; i<sizeOfOrders; i++)
			{
				if ((this.orderId.get(i).contains(id)) && (this.orderFilledQty.get(i)==0))
				{
					this.butlog.warn(this.orderId.get(i)+"/t"+this.orderFilledQty.get(i));
					String request_payload_in_string=
							"{"
								+"\"id\" : "+"\""+this.orderId.get(i)+"\"," 
								+"\"stopPrice\" : "+stoploss+
							"}";
					this.butlog.warn(request_payload_in_string);
					List<String> response=this.restconnection.modifyOrder(this.modify_order_url, request_payload_in_string);
					this.butlog.info("Order Updated with Response Code "+response.get(0)+" and message = "+response.get(1));
				}
			}
		}
		catch (Exception e)
		{
			this.butlog.error("Error/Exception in method: modifyOrder() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}
	private float getLockinProfitPercentage(float running_profit_percentage)
	{
		//int running_profit=Math.round(running_profit_percentage);
		float running_profit=running_profit_percentage;
		if (running_profit>=100.00f)
		{
			return 100.00f;
		}
		else if (running_profit>=90.00f)
		{
			return 85.00f;
		}
		else if (running_profit>=80.00f)
		{
			return 75.00f;
		}
		else if (running_profit>=70.00f)
		{
			return 65.00f;
		}
		else if (running_profit>=60.00f)
		{
			return 55.00f;
		}
		else if (running_profit>=50.00f)
		{
			return 45.00f;
		}
		else if(running_profit>=45.00f)
		{
			return 40.00f;
		}
		else if(running_profit>=40.00f)
		{
			return 35.00f;
		}
		else if (running_profit>=35.00f)
		{
			return 30.00f;
		}
		else if (running_profit>=30.00f)
		{
			return 28.00f;
		}
		else if (running_profit>=25.00f)
		{
			return 22.00f;
		}
		else if (running_profit>=20.00f)
		{
			return 18.00f;
		}
		else if (running_profit>=15.00f)
		{
			return 13.00f;
		}
		else if(running_profit>=10.00f)
		{
			return 9.50f;
		}
		else if(running_profit>=9.50f)
		{
			return 9.00f;
		}
		else if(running_profit>=9.00f)
		{
			return 8.50f;
		}
		else if(running_profit>=8.50f)
		{
			return 8.00f;
		}
		else if(running_profit>=8.00f)
		{
			return 7.50f;
		}
		else if(running_profit>=7.50f)
		{
			return 7.00f;
		}
		else if(running_profit>=7.00f)
		{
			return 6.50f;
		}
		else if(running_profit>=6.50f)
		{
			return 6.00f;
		}
		else if(running_profit>=6.00f)
		{
			return 5.50f;
		}
		else if(running_profit>=5.50f)
		{
			return 5.00f;
		}
		else if(running_profit>=5.00f)
		{
			return 4.50f;
		}
		else if (running_profit>=4.50f)
		{
			return 4.00f;
		}
		else if (running_profit>=4.00f)
		{
			return 3.50f;
		}
		else if (running_profit>=3.50f)
		{
			return 3.00f;
		}
		else if (running_profit>=3.00f)
		{
			return 2.50f;
		}
		else if (running_profit>=2.50f)
		{
			return 2.00f;
		}
		else if (running_profit>=2.00f)
		{
			return 1.50f;
		}
		else if (running_profit>=1.50f)
		{
			return 1.00f;
		}
		else if (running_profit>=1.00f)
		{
			return 0.50f;
		}
		else if (running_profit>=0.50f)
		{
			return 0.30f;
		}
		else if (running_profit>=0.30f)
		{
			return 0.10f;
		}
		else
		{
			return 0.00f;
		}
		
	}
	private float closestModifiedStoploss(float a, float b) 
	{
	    float c1 = a - (a % b);
	    float c2 = (a + b) - (a % b);
	    if (a - c1 > c2 - a) {
	        return c2;
	    } else {
	        return c1;
	    }
	}
	private void exitPosition(String symbol)
	{
		List<String> list_of_string=new ArrayList<String>();
		String request_payload_in_string=
				"{"	 
						+"\"id\" : \"NSE:"+symbol+"-EQ-CO\""+ 
				"}";
		list_of_string=this.restconnection.exitPosition(this.exit_positions_url, request_payload_in_string);
		this.butlog.info(symbol+" exited with response code = "+list_of_string.get(0)+" and message = "+list_of_string.get(1));
	}
	public static void main(String... args)
	{
		PositionsHandler obj=new PositionsHandler();
		obj.handlePositions();
		obj.close();
	}
	
}*/

package positionshandlerforfyers;

import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.math3.util.Precision;
//import org.apache.logging.log4j.Logger;
import org.apache.log4j.Logger;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.github.wnameless.json.flattener.JsonFlattener;
import butlerforfyers.RESTConnectionHandler;
import butlerforfyers.DBConnection;
import butlerforfyers.OtherPropertiesLoader;
import butlerforfyers.ButlerLogger;
//import java.nio.file.Files;
//import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.sql.ResultSet;
import java.time.LocalDateTime;

public class PositionsHandler implements AutoCloseable 
{
	int size;
	private Logger butlog;
	private String positions_url;
	private String modify_order_url;
	private String get_orders_url;
	private String exit_positions_url;
	private Response response;
	private String responseValue;
	private RESTConnectionHandler restconnection;
	Map<String, Object> flattenedJSONMap;
	List<String> symbol;
	List<String> dbsymbol;
	List<Integer> dbqty;
	List<String> dbside;
	List<Float> dbstoploss_initial;
	List<Float> dbstoploss_modified;
	List<String> dbbutler_record_id;
	List<Float> dbpnl_percentage;
	List<String> dbbroker_order_ref_no; 
	List<Integer> netQty;
	List<Float> sellAvg;
	List<Float> buyAvg;
	List<Float> pl;
	List<String> orderId;
	List<Integer> orderFilledQty;
	float running_profit_percentage;
	float lockin_profit_percentage;
	float mod_stoploss;
	float mod_stoploss_before_round;
	DBConnection dbconnection;
	ResultSet rs;
	int sizeOfOrders;
	private OtherPropertiesLoader property=OtherPropertiesLoader.initialize(); 
	
	PositionsHandler()
	{
		this.size=0;
		this.sizeOfOrders=0;
		this.running_profit_percentage=0.00f;
		this.lockin_profit_percentage=0.00f;
		this.mod_stoploss=0.00f;
		this.mod_stoploss_before_round=0.00f;
		this.response=null;
		this.responseValue=null;
		this.rs=null;
		this.symbol=new ArrayList<String>();
		this.dbsymbol=new ArrayList<String>();
		this.dbbutler_record_id=new ArrayList<String>();
		this.netQty=new ArrayList<Integer>();
		this.sellAvg=new ArrayList<Float>();
		this.buyAvg=new ArrayList<Float>();
		this.pl=new ArrayList<Float>();
		this.dbqty=new ArrayList<Integer>();
		this.dbside=new ArrayList<String>();
		this.dbstoploss_initial=new ArrayList<Float>();
		this.dbstoploss_modified=new ArrayList<Float>();
		this.dbpnl_percentage=new ArrayList<Float>();
		this.dbbroker_order_ref_no=new ArrayList<String>();
		this.orderId=new ArrayList<String>();
		this.orderFilledQty=new ArrayList<Integer>();
		this.dbconnection=new DBConnection();
		this.flattenedJSONMap=new HashMap<String, Object>();
		this.positions_url=this.property.getPropertyValue("positions_url");
		this.modify_order_url=this.property.getPropertyValue("modify_order_url");
		this.get_orders_url=this.property.getPropertyValue("get_orders_url");
		this.exit_positions_url=this.property.getPropertyValue("exit_positions_url");
		this.restconnection=new RESTConnectionHandler();
		this.butlog=ButlerLogger.getButlerLogger(this.getClass().getName());
	}
	public void close()
	{
		this.size=0;
		this.sizeOfOrders=0;
		this.running_profit_percentage=0.00f;
		this.lockin_profit_percentage=0.00f;
		this.mod_stoploss=0.00f;
		this.response.close();
		this.responseValue=null;
		this.rs=null;
		this.symbol=null;
		this.dbsymbol=null;
		this.dbbutler_record_id=null;
		this.netQty=null;
		this.sellAvg=null;
		this.buyAvg=null;
		this.pl=null;
		this.dbqty=null;
		this.dbside=null;
		this.dbstoploss_initial=null;
		this.dbstoploss_modified=null;
		this.dbpnl_percentage=null;
		this.dbbroker_order_ref_no=null;
		this.orderId=null;
		this.orderFilledQty=null;
		this.dbconnection.close();
		this.flattenedJSONMap=null;
		this.positions_url=null;
		this.modify_order_url=null;
		this.get_orders_url=null;
		this.restconnection.close();
		this.butlog.info("Closing Positions Resources");
		this.butlog=null;
		System.gc();
		System.exit(0);
	}
	public void handlePositions()
	{
		this.handlePositionsPrivately();
		//this.handlePositionsPrivatelyTest();
		this.processResponseToList();
		this.loadPresentOrders();
		this.getTodayPositions();
		this.processPositions();
	}
	private void handlePositionsPrivately()
	{
		try
		{
			this.response=this.restconnection.sendRequest(this.positions_url);
			this.responseValue=this.response.body().string();
			//this.butlog.warn("response is =\n"+this.responseValue);
			Object object=new Object();
			JSONParser parser=new JSONParser();
			JSONObject jsonobject=new JSONObject();
			object=parser.parse(this.responseValue);
			jsonobject=(JSONObject) object;
			this.flattenedJSONMap=JsonFlattener.flattenAsMap(jsonobject.toString());
			//flattenedJSONMap.forEach((k,v) -> this.butlog.warn(k+" : "+v));
			Iterator<Map.Entry<String, Object>> itr =this.flattenedJSONMap.entrySet().iterator();
			while(itr.hasNext())
			{
				Map.Entry<String, Object> entry=itr.next();
				if(entry.getKey().contains(".symbol"))
				{
					this.size++;
					//this.butlog.warn(entry.getKey());
				}
			}
			this.butlog.warn("size is ="+this.size);
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method: handlePositionsPrivately() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
		
	}
	/*private void handlePositionsPrivatelyTest()
	{
		try
		{
			this.responseValue=new String(Files.readAllBytes(Paths.get("B:\\Eclipse Workplace\\ButlerToken\\src\\main\\resources\\temp.txt")));
			//this.butlog.warn("response is =\n"+this.responseValue);
			Object object=new Object();
			JSONParser parser=new JSONParser();
			JSONObject jsonobject=new JSONObject();
			object=parser.parse(this.responseValue);
			jsonobject=(JSONObject) object;
			this.flattenedJSONMap=JsonFlattener.flattenAsMap(jsonobject.toString());
			//flattenedJSONMap.forEach((k,v) -> this.butlog.warn(k+" : "+v));
			Iterator<Map.Entry<String, Object>> itr =this.flattenedJSONMap.entrySet().iterator();
			while(itr.hasNext())
			{
				Map.Entry<String, Object> entry=itr.next();
				if(entry.getKey().contains(".symbol"))
				{
					this.size++;
					//this.butlog.warn(entry.getKey());
				}
			}
			this.butlog.warn("size is ="+this.size);
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method: handlePositionsPrivately() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
		
	}*/
	private void processResponseToList()
	{
		String[] symbol_with_EQ=null;
		String[] symbol_without_EQ=null;
		String symbol=null;
		try
		{
			for(int i=0;i<this.size;i++)
			{
				symbol_with_EQ = this.flattenedJSONMap.get("netPositions["+i+"].symbol").toString().trim().split("NSE:");
				symbol_without_EQ=symbol_with_EQ[1].split("-EQ");
				symbol=symbol_without_EQ[0];
				this.symbol.add(symbol);
				this.netQty.add(Integer.parseInt(this.flattenedJSONMap.get("netPositions["+i+"].netQty").toString().trim()));
				this.sellAvg.add(Precision.round(Float.parseFloat(this.flattenedJSONMap.get("netPositions["+i+"].sellAvg").toString().trim()),2));
				this.buyAvg.add(Precision.round(Float.parseFloat(this.flattenedJSONMap.get("netPositions["+i+"].buyAvg").toString().trim()),2));
				this.pl.add(Precision.round(Float.parseFloat(this.flattenedJSONMap.get("netPositions["+i+"].pl").toString().trim()),2));
			}
			/*this.butlog.warn("symbol"+"\t"+"net qty"+"\t"+"sell avg"+"\t"+"buyAvg"+"\t"+"PL");
			for (int j=0; j<this.size; j++)
			{
				this.butlog.warn(this.symbol.get(j)+"\t"+this.netQty.get(j)+"\t"+this.sellAvg.get(j)+"\t"+this.buyAvg.get(j)+"\t"+this.pl.get(j));
			}*/
		}
		catch (Exception e)
		{
			this.butlog.error("Error/Exception in method: processResponseToList() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}
	private void getTodayPositions()
	{
		try
		{
			String broker_ref_no=null;
			String[] temp=null;
			this.rs=this.dbconnection.positionSymbolsOfToday();
			if (!(this.rs==null))
			{
				while(this.rs.next())
				{
					this.dbsymbol.add(this.rs.getString("symbol"));
					this.dbbutler_record_id.add(Integer.toString(this.rs.getInt("butler_order_id")));
					this.dbqty.add(this.rs.getInt("qty"));
					this.dbside.add(this.rs.getString("side"));
					this.dbstoploss_initial.add(Precision.round(Float.parseFloat(this.rs.getString("stoploss")),2));
					this.dbpnl_percentage.add(Precision.round(Float.parseFloat(this.rs.getString("pnl_percentage")),2));
					this.dbstoploss_modified.add(Precision.round(Float.parseFloat(this.rs.getString("mod_stoploss")),2));
					temp=this.rs.getString("broker_order_ref_no").split("-");
					broker_ref_no=temp[0];
					//this.butlog.warn("Check this broker ref no for "+this.rs.getString("symbol")+" is = "+broker_ref_no);
					this.dbbroker_order_ref_no.add(broker_ref_no);
				}
			}
			else
			{
				this.butlog.error("No Orders for today. Good Bye..!!!");
				System.exit(1);
			}
		}
		catch (Exception e)
		{
			this.butlog.error("Error/Exception in method: getTodayPositions() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}
	private void processPositions()
	{
		try
		{
			for(int d=0;d<this.dbsymbol.size();d++)
			{
				for(int r=0; r<this.symbol.size();r++)
				{
					if(this.dbsymbol.get(d).equalsIgnoreCase(this.symbol.get(r)))
					{
						this.mod_stoploss=0.00f;
						float total_buy_value=Precision.round(this.buyAvg.get(r)*this.dbqty.get(d),2);
						this.running_profit_percentage=Precision.round((((this.pl.get(r))/total_buy_value)*100),2);
						if(this.netQty.get(r)==0)
						{
							//this.butlog.warn("symbol : "+this.symbol.get(r)+" has net qty of = "+this.netQty.get(r)+". Hence sending for DB updation.");
							this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.running_profit_percentage, this.pl.get(r), this.dbstoploss_modified.get(d));
							this.butlog.info(this.symbol.get(r)+"("+this.netQty.get(r)+") - SL HIT or Forced Closed -"+" data updated.");
						}
						else
						{
							
							if(this.dbside.get(d).equalsIgnoreCase("Buy"))
							{
								//float total_buy_value=this.buyAvg.get(r)*this.dbqty.get(d);
								//this.running_profit_percentage=((this.pl.get(r))/total_buy_value)*100;
								this.butlog.warn("symbol : "+this.symbol.get(r)+" has BUY order with running net qty of = "+Math.abs(this.netQty.get(r))+". Hence processing positions.");
								this.butlog.warn("\npl is "+this.pl.get(r)+"\ntotal_buy_value is "+total_buy_value+"\nAvg Buy Price is "+this.buyAvg.get(r)+"\nRunning profit percentage is "+this.running_profit_percentage+"%");
								this.lockin_profit_percentage=this.getLockinProfitPercentage(this.running_profit_percentage);
								this.butlog.warn("lockin percentage is "+this.lockin_profit_percentage);
								if((this.running_profit_percentage>0.00f) && (this.running_profit_percentage>this.dbpnl_percentage.get(d)))
								{
									float lockin_profit=Precision.round(((total_buy_value*this.lockin_profit_percentage)/100),2);
									float stoploss_increment_points=Precision.round((lockin_profit/this.dbqty.get(d)),2);
									this.butlog.warn("lockin profit = "+lockin_profit);
									this.butlog.warn("stoploss increment points = "+stoploss_increment_points);
									//this.mod_stoploss_before_round=Precision.round((this.dbstoploss_initial.get(d)+stoploss_increment_points),2);
									this.mod_stoploss_before_round=Precision.round((this.buyAvg.get(r)+stoploss_increment_points),2);
									this.mod_stoploss=Precision.round(this.closestModifiedStoploss(this.mod_stoploss_before_round, 0.05f),2);
									this.butlog.info("Modified Stoploss (before rounding) = "+this.mod_stoploss_before_round+" Modified Stoploss (after rounding) = "+this.mod_stoploss);
									this.butlog.warn("Initial stoploss = "+this.dbstoploss_initial.get(d)+" Modified stoploss = "+this.dbstoploss_modified.get(d)+" new stoploss = "+this.mod_stoploss);
									if((lockin_profit>0.00f) && (this.mod_stoploss>this.dbstoploss_initial.get(d)) && (this.mod_stoploss>this.dbstoploss_modified.get(d)))
									{
										if (this.mod_stoploss>=this.buyAvg.get(r))
										{
											this.butlog.warn(this.dbsymbol.get(d)+" has modified Stoploss of "+this.mod_stoploss+". But since it will not be allowed hence..."); //These Lines should be removed as Fyers did some changes at their end
											this.mod_stoploss = this.buyAvg.get(r)-0.05f; //These Lines should be removed as Fyers did some changes at their end
											this.butlog.warn("New Stoploss for "+this.dbsymbol.get(d)+" is "+this.mod_stoploss); //These Lines should be removed as Fyers did some changes at their end
											this.modifyOrder(this.dbbroker_order_ref_no.get(d),this.mod_stoploss, this.dbsymbol.get(d));
											this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.lockin_profit_percentage, lockin_profit, this.mod_stoploss);
											this.butlog.info(this.symbol.get(r)+"("+this.netQty.get(r)+") - Buy -"+" data updated.");
										}
										else
										{
											this.modifyOrder(this.dbbroker_order_ref_no.get(d),this.mod_stoploss, this.dbsymbol.get(d));
											this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.lockin_profit_percentage, lockin_profit, this.mod_stoploss);
											this.butlog.info(this.symbol.get(r)+"("+this.netQty.get(r)+") - Buy -"+" data updated.");
										}
									}
									else
									{
										this.butlog.info(this.symbol.get(r)+"("+this.netQty.get(r)+") - Buy -"+" has NO profit to lock (OR) Present stop-loss is sufficient.");
									}
								}
								/*else if(this.running_profit_percentage<=-1.00f)
								{
									this.butlog.info("Since "+this.symbol.get(r)+" has Loss of "+this.running_profit_percentage+"%, hence exiting this position.");
									this.exitPosition(this.symbol.get(r));
									this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.running_profit_percentage, this.pl.get(r), this.mod_stoploss);
									this.butlog.info(this.symbol.get(r)+" data updated to database.");
								}*/
								else
								{
									this.butlog.info("Since "+this.symbol.get(r)+"("+this.netQty.get(r)+") - Buy - has running P&L of "+this.running_profit_percentage+"%, and DB P&L is "+this.dbpnl_percentage.get(d)+" Either running P&L between (-1) and zero(0) or running P&L < DB P&L. hence doing NOTHING.");
									//this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.running_profit_percentage, this.pl.get(r), this.mod_stoploss);
								}
							}
							else if(this.dbside.get(d).equalsIgnoreCase("Sell"))
							{
								this.butlog.warn("symbol : "+this.symbol.get(r)+" has SELL order with running net qty of = "+Math.abs(this.netQty.get(r))+". Hence processing positions.");
								float total_sell_value=Precision.round((this.sellAvg.get(r)*this.dbqty.get(d)),2);
								this.running_profit_percentage=Precision.round((((this.pl.get(r))/total_sell_value)*100),2);
								this.butlog.warn("\npl is "+this.pl.get(r)+"\ntotal_sell_value is "+total_sell_value+"\nAvg Sell Price is "+this.sellAvg.get(r)+"\nRunning profit percentage is "+this.running_profit_percentage+"%");
								this.lockin_profit_percentage=this.getLockinProfitPercentage(this.running_profit_percentage);
								this.butlog.warn("lockin percentage is "+this.lockin_profit_percentage);
								if((this.running_profit_percentage>0.00f) && (this.running_profit_percentage>this.dbpnl_percentage.get(d)))
								{
									float lockin_profit=Precision.round(((total_sell_value*this.lockin_profit_percentage)/100),2);
									float stoploss_increment_points=Precision.round(((lockin_profit)/(this.dbqty.get(d))),2);
									this.butlog.warn("lockin profit = "+lockin_profit);
									this.butlog.warn("stoploss increment points = "+stoploss_increment_points);
									//this.mod_stoploss_before_round=Precision.round((this.dbstoploss_initial.get(d)+stoploss_increment_points),2);
									//this.mod_stoploss_before_round=Precision.round((this.dbstoploss_initial.get(d)-stoploss_increment_points),2);
									this.mod_stoploss_before_round=Precision.round((this.sellAvg.get(r)-stoploss_increment_points),2);
									this.mod_stoploss=Precision.round(this.closestModifiedStoploss(this.mod_stoploss_before_round, 0.05f),2);
									this.butlog.info("Modified Stoploss (before rounding) = "+this.mod_stoploss_before_round+" Modified Stoploss (after rounding) = "+this.mod_stoploss);
									this.butlog.warn("Initial stoploss = "+this.dbstoploss_initial.get(d)+" Modified stoploss = "+this.dbstoploss_modified.get(d)+" new stoploss = "+this.mod_stoploss);
									//if((lockin_profit>0.00f) && (this.mod_stoploss>this.dbstoploss_initial.get(d)) && (this.mod_stoploss>this.dbstoploss_modified.get(d)))
									if(this.dbstoploss_modified.get(d)==0.00f)
									{
										if((lockin_profit>0.00f) && (this.mod_stoploss<this.dbstoploss_initial.get(d)) && ((this.mod_stoploss<this.dbstoploss_modified.get(d)) || (this.mod_stoploss>0.00f)))
										{
											if(this.mod_stoploss<=this.sellAvg.get(r))
											{
												this.butlog.warn(this.dbsymbol.get(d)+" has modified Stoploss of "+this.mod_stoploss+". But since it will not be allowed hence..."); //These Lines should be removed as Fyers did some changes at their end
												this.mod_stoploss = this.sellAvg.get(r)+0.05f; //These Lines should be removed as Fyers did some changes at their end
												this.butlog.warn("New Stoploss for "+this.dbsymbol.get(d)+" is "+this.mod_stoploss); //These Lines should be removed as Fyers did some changes at their end
												this.modifyOrder(this.dbbroker_order_ref_no.get(d),this.mod_stoploss, this.dbsymbol.get(d));
												this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.lockin_profit_percentage, lockin_profit, this.mod_stoploss);
												this.butlog.info(this.symbol.get(r)+"("+this.netQty.get(r)+") - Sell -"+" data updated.");
											}
											else
											{
												this.modifyOrder(this.dbbroker_order_ref_no.get(d),this.mod_stoploss, this.dbsymbol.get(d));
												this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.lockin_profit_percentage, lockin_profit, this.mod_stoploss);
												this.butlog.info(this.symbol.get(r)+"("+this.netQty.get(r)+") - Sell -"+" data updated.");
											}
										}
										else
										{
											this.butlog.info(this.symbol.get(r)+"("+this.netQty.get(r)+") - Sell -"+" has NO profit to lock (OR) Present stop-loss is sufficient.");
										}
									}
									else
									{
										if((lockin_profit>0.00f) && (this.mod_stoploss<this.dbstoploss_initial.get(d)) && (this.mod_stoploss<this.dbstoploss_modified.get(d)))
										{
											if(this.mod_stoploss<=this.sellAvg.get(r))
											{
												this.butlog.warn(this.dbsymbol.get(d)+" has modified Stoploss of "+this.mod_stoploss+". But since it will not be allowed hence..."); //These Lines should be removed as Fyers did some changes at their end
												this.mod_stoploss = this.sellAvg.get(r)+0.05f; //These Lines should be removed as Fyers did some changes at their end
												this.butlog.warn("New Stoploss for "+this.dbsymbol.get(d)+" is "+this.mod_stoploss); //These Lines should be removed as Fyers did some changes at their end
												this.modifyOrder(this.dbbroker_order_ref_no.get(d),this.mod_stoploss, this.dbsymbol.get(d));
												this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.lockin_profit_percentage, lockin_profit, this.mod_stoploss);
												this.butlog.info(this.symbol.get(r)+"("+this.netQty.get(r)+") - Sell -"+" data updated.");
											}
											else
											{
												this.modifyOrder(this.dbbroker_order_ref_no.get(d),this.mod_stoploss, this.dbsymbol.get(d));
												this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.lockin_profit_percentage, lockin_profit, this.mod_stoploss);
												this.butlog.info(this.symbol.get(r)+"("+this.netQty.get(r)+") - Sell -"+" data updated.");
											}
											
										}
										else
										{
											this.butlog.info(this.symbol.get(r)+"("+this.netQty.get(r)+") - Sell -"+" has NO profit to lock (OR) Present stop-loss is sufficient.");
										}
									}
								}
								/*else if(this.running_profit_percentage<=-1.00f)
								{
									this.butlog.info("Since "+this.symbol.get(r)+" has Loss of "+this.running_profit_percentage+"%, hence exiting this position.");
									this.exitPosition(this.symbol.get(r));
									this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.running_profit_percentage, this.pl.get(r), this.mod_stoploss);
									this.butlog.info(this.symbol.get(r)+" data updated to database.");
								}*/
								else
								{
									this.butlog.info("Since "+this.symbol.get(r)+"("+this.netQty.get(r)+") - Sell - has running P&L of "+this.running_profit_percentage+"%, and DB P&L is "+this.dbpnl_percentage.get(d)+" Either running P&L between (-1) and zero(0) or running P&L < DB P&L. hence doing NOTHING.");
									//this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.running_profit_percentage, this.pl.get(r), this.mod_stoploss);
								}
							}
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method: processPositions() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}

	private void loadPresentOrders()
	{
		try
		{
			Response response=this.restconnection.sendRequest(this.get_orders_url);
			String responseValue=response.body().string();
			//this.butlog.warn("Present Orders are =\n"+responseValue);
			Object object=new Object();
			JSONParser parser=new JSONParser();
			JSONObject jsonobject=new JSONObject();
			object=parser.parse(responseValue);
			jsonobject=(JSONObject) object;
			Map<String, Object> flattenedJSONMap=JsonFlattener.flattenAsMap(jsonobject.toString());
			//flattenedJSONMap.forEach((k,v) -> this.butlog.warn(k+" : "+v));
			Iterator<Map.Entry<String, Object>> itr =flattenedJSONMap.entrySet().iterator();
			while(itr.hasNext())
			{
				Map.Entry<String, Object> entry=itr.next();
				if(entry.getKey().contains(".symbol"))
				{
					sizeOfOrders++;
					//this.butlog.warn(entry.getKey());
				}
			}
			this.butlog.warn("size of Orders are ="+sizeOfOrders);
			for (int i=0; i<sizeOfOrders; i++)
			{
				this.orderId.add(i, flattenedJSONMap.get("orderBook["+i+"].id").toString());
				this.orderFilledQty.add(i, Integer.parseInt(flattenedJSONMap.get("orderBook["+i+"].filledQty").toString()));
			}
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method: loadPresentOrders() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}	
	private void modifyOrder(String id, float stoploss, String symbol)
	{
		try
		{
			for(int i=0; i<sizeOfOrders; i++)
			{
				if ((this.orderId.get(i).contains(id)) && (this.orderFilledQty.get(i)==0))
				{
					this.butlog.warn(this.orderId.get(i)+"/t"+this.orderFilledQty.get(i));
					String request_payload_in_string=
							"{"
								+"\"id\" : "+"\""+this.orderId.get(i)+"\"," 
								+"\"stopPrice\" : "+stoploss+
							"}";
					this.butlog.warn(request_payload_in_string);
					List<String> response=this.restconnection.modifyOrder(this.modify_order_url, request_payload_in_string);
					this.butlog.info("Order Updated with Response Code "+response.get(0)+" and message = "+response.get(1));
				}
			}
		}
		catch (Exception e)
		{
			this.butlog.error("Error/Exception in method: modifyOrder() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}
	private float getLockinProfitPercentage(float running_profit_percentage)
	{
		//int running_profit=Math.round(running_profit_percentage);
		float running_profit=running_profit_percentage;
		/*if (running_profit>=100.00f)
		{
			return 100.00f;
		}
		else if (running_profit>=90.00f)
		{
			return 85.00f;
		}
		else if (running_profit>=80.00f)
		{
			return 75.00f;
		}
		else if (running_profit>=70.00f)
		{
			return 65.00f;
		}
		else if (running_profit>=60.00f)
		{
			return 55.00f;
		}
		else if (running_profit>=50.00f)
		{
			return 45.00f;
		}
		else if(running_profit>=45.00f)
		{
			return 40.00f;
		}
		else if(running_profit>=40.00f)
		{
			return 35.00f;
		}
		else if (running_profit>=35.00f)
		{
			return 30.00f;
		}
		else if (running_profit>=30.00f)
		{
			return 28.00f;
		}
		else if (running_profit>=25.00f)
		{
			return 22.00f;
		}
		else if (running_profit>=20.00f)
		{
			return 18.00f;
		}
		else if (running_profit>=15.00f)
		{
			return 13.00f;
		}
		else if(running_profit>=10.00f)
		{
			return 9.50f;
		}
		else if(running_profit>=9.50f)
		{
			return 9.00f;
		}
		else if(running_profit>=9.00f)
		{
			return 8.50f;
		}
		else if(running_profit>=8.50f)
		{
			return 8.00f;
		}
		else if(running_profit>=8.00f)
		{
			return 7.50f;
		}
		else if(running_profit>=7.50f)
		{
			return 7.00f;
		}
		else if(running_profit>=7.00f)
		{
			return 6.50f;
		}
		else if(running_profit>=6.50f)
		{
			return 6.00f;
		}
		else if(running_profit>=6.00f)
		{
			return 5.50f;
		}
		else if(running_profit>=5.50f)
		{
			return 5.00f;
		}
		else if(running_profit>=5.00f)
		{
			return 4.50f;
		}
		else if (running_profit>=4.50f)
		{
			return 4.00f;
		}
		else if (running_profit>=4.00f)
		{
			return 3.50f;
		}
		else if (running_profit>=3.50f)
		{
			return 3.00f;
		}
		else if (running_profit>=3.00f)
		{
			return 2.50f;
		}
		else if (running_profit>=2.50f)
		{
			return 2.00f;
		}
		else if (running_profit>=2.00f)
		{
			return 1.50f;
		}
		else if (running_profit>=1.50f)
		{
			return 1.00f;
		}
		else if (running_profit>=1.00f)
		{
			return 0.50f;
		}
		else if (running_profit>=0.50f)
		{
			return 0.30f;
		}
		else if (running_profit>=0.30f)
		{
			return 0.10f;
		}
		else
		{
			return 0.00f;
		}*/
		//return (95*running_profit)/100;
		//return (100*running_profit)/100;
		int hour = LocalDateTime.now().getHour();
		if ((hour>=9)&& (hour<10))
		{
			this.butlog.warn("The Time Hour now is = "+hour+". Hence, locking 50% profits.");
			return (50*running_profit)/100;
		}
		else if ((hour>=10)&& (hour<11))
		{
			this.butlog.warn("The Time Hour now is = "+hour+". Hence, locking 60% profits.");
			return (60*running_profit)/100;
		}
		else if ((hour>=11)&& (hour<12))
		{
			this.butlog.warn("The Time Hour now is = "+hour+". Hence, locking 65% profits.");
			return (65*running_profit)/100;
		}
		else if ((hour>=12)&& (hour<13))
		{
			this.butlog.warn("The Time Hour now is = "+hour+". Hence, locking 70% profits.");
			return (70*running_profit)/100;
		}
		else if ((hour>=13)&& (hour<14))
		{
			this.butlog.warn("The Time Hour now is = "+hour+". Hence, locking 80% profits.");
			return (80*running_profit)/100;
		}
		else if ((hour>=14)&& (hour<15))
		{
			this.butlog.warn("The Time Hour now is = "+hour+". Hence, locking 90% profits.");
			return (90*running_profit)/100;
		}
		else
		{
			this.butlog.warn("The Time Hour now is = "+hour+". Hence, locking 100% profits.");
			return (100*running_profit)/100;
		}
	}
	private float closestModifiedStoploss(float a, float b) 
	{
	    float c1 = a - (a % b);
	    float c2 = (a + b) - (a % b);
	    if (a - c1 > c2 - a) {
	        return c2;
	    } else {
	        return c1;
	    }
	}
	private void exitPosition(String symbol)
	{
		List<String> list_of_string=new ArrayList<String>();
		String request_payload_in_string=
				"{"	 
						+"\"id\" : \"NSE:"+symbol+"-EQ-CO\""+ 
				"}";
		list_of_string=this.restconnection.exitPosition(this.exit_positions_url, request_payload_in_string);
		this.butlog.info(symbol+" exited with response code = "+list_of_string.get(0)+" and message = "+list_of_string.get(1));
	}
	public static void main(String... args)
	{
		PositionsHandler obj=new PositionsHandler();
		obj.handlePositions();
		obj.close();
	}
	
}

