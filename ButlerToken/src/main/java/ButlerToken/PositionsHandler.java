package ButlerToken;

import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Logger;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.github.wnameless.json.flattener.JsonFlattener;

//import java.nio.file.Files;
//import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.sql.ResultSet;

public class PositionsHandler implements AutoCloseable 
{
	int size;
	private Logger butlog;
	private String positions_url;
	private String modify_order_url;
	private String get_orders_url;
	private Response response;
	private String responseValue;
	private RESTConnectionHandler restconnection;
	Map<String, Object> flattenedJSONMap;
	List<String> symbol;
	List<String> dbsymbol;
	List<Integer> dbqty;
	List<String> dbside;
	List<Float> dbstoploss;
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
		this.dbstoploss=new ArrayList<Float>();
		this.dbpnl_percentage=new ArrayList<Float>();
		this.dbbroker_order_ref_no=new ArrayList<String>();
		this.orderId=new ArrayList<String>();
		this.orderFilledQty=new ArrayList<Integer>();
		this.dbconnection=new DBConnection();
		this.flattenedJSONMap=new HashMap<String, Object>();
		this.positions_url=this.property.getPropertyValue("positions_url");
		this.modify_order_url=this.property.getPropertyValue("modify_order_url");
		this.get_orders_url=this.property.getPropertyValue("get_orders_url");
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
		this.dbstoploss=null;
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
				this.sellAvg.add(Float.parseFloat(this.flattenedJSONMap.get("netPositions["+i+"].sellAvg").toString().trim()));
				this.buyAvg.add(Float.parseFloat(this.flattenedJSONMap.get("netPositions["+i+"].buyAvg").toString().trim()));
				this.pl.add(Float.parseFloat(this.flattenedJSONMap.get("netPositions["+i+"].pl").toString().trim()));
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
					this.dbstoploss.add(Float.parseFloat(this.rs.getString("stoploss")));
					this.dbpnl_percentage.add(Float.parseFloat(this.rs.getString("pnl_percentage")));
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
			this.butlog.error("Error/Exception in method: processResponseToList() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
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
						float total_buy_value=this.buyAvg.get(r)*this.dbqty.get(d);
						this.running_profit_percentage=((this.pl.get(r))/total_buy_value)*100;
						if(this.netQty.get(r)==0)
						{
							//this.butlog.warn("symbol : "+this.symbol.get(r)+" has net qty of = "+this.netQty.get(r)+". Hence sending for DB updation.");
							this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.running_profit_percentage, this.pl.get(r), this.mod_stoploss);
							this.butlog.info(this.symbol.get(r)+"("+this.netQty.get(r)+") - SL HIT -"+" data updated.");
						}
						else
						{
							this.butlog.warn("symbol : "+this.symbol.get(r)+" has running net qty of = "+this.netQty.get(r)+". Hence processing positions.");
							if(this.dbside.get(d).equalsIgnoreCase("Buy"))
							{
								//float total_buy_value=this.buyAvg.get(r)*this.dbqty.get(d);
								//this.running_profit_percentage=((this.pl.get(r))/total_buy_value)*100;
								this.butlog.warn("\npl is "+this.pl.get(r)+"\ntotal_buy_value is "+total_buy_value+"\nRunning profit percentage is "+this.running_profit_percentage);
								this.lockin_profit_percentage=this.getLockinProfitPercentage(this.running_profit_percentage);
								this.butlog.warn("lockin percentage is "+this.lockin_profit_percentage);
								if((this.running_profit_percentage>0.0f) && (this.running_profit_percentage>this.dbpnl_percentage.get(d)))
								{
									float lockin_profit=(total_buy_value*this.lockin_profit_percentage)/100;
									float stoploss_increment_points=lockin_profit/this.dbqty.get(d);
									this.butlog.warn("lockin profit"+lockin_profit);
									this.butlog.warn("stoploss increment points"+stoploss_increment_points);
									this.mod_stoploss=this.dbstoploss.get(d)+stoploss_increment_points;
									this.butlog.warn("Old stoploss "+this.dbstoploss.get(d)+" new stoploss "+this.mod_stoploss);
									this.modifyOrder(this.dbbroker_order_ref_no.get(d),this.mod_stoploss, this.dbsymbol.get(d));
									this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.lockin_profit_percentage, lockin_profit, this.mod_stoploss);
									this.butlog.info(this.symbol.get(r)+"("+this.netQty.get(r)+") - Buy -"+" data updated.");
								}
							}
							else if(this.dbside.get(d).equalsIgnoreCase("Sell"))
							{
								float total_sell_value=this.sellAvg.get(r)*this.dbqty.get(d);
								this.running_profit_percentage=((this.pl.get(r))/total_sell_value)*100;
								this.butlog.warn("\npl is "+this.pl.get(r)+"\ntotal_sell_value is "+total_sell_value+"\nRunning profit percentage is "+this.running_profit_percentage);
								this.lockin_profit_percentage=this.getLockinProfitPercentage(this.running_profit_percentage);
								this.butlog.warn("lockin percentage is "+this.lockin_profit_percentage);
								if((this.running_profit_percentage>0.0f) && (this.running_profit_percentage>this.dbpnl_percentage.get(d)))
								{
									float lockin_profit=(total_sell_value*this.lockin_profit_percentage)/100;
									float stoploss_increment_points=((lockin_profit)/(this.dbqty.get(d)));
									this.butlog.warn("lockin profit"+lockin_profit);
									this.butlog.warn("stoploss increment points"+stoploss_increment_points);
									this.mod_stoploss=this.dbstoploss.get(d)+stoploss_increment_points;
									this.butlog.warn("Old stoploss "+this.dbstoploss.get(d)+" new stoploss "+this.mod_stoploss);
									this.modifyOrder(this.dbbroker_order_ref_no.get(d),this.mod_stoploss, this.dbsymbol.get(d));
									this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.lockin_profit_percentage, lockin_profit, this.mod_stoploss);
									this.butlog.info(this.symbol.get(r)+"("+this.netQty.get(r)+") - Sell -"+" data updated.");
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
								+"\"id\" : "+this.orderId.get(i)+"," 
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
		int running_profit=Math.round(running_profit_percentage);
		if (running_profit>=100)
		{
			return 90.0f;
		}
		else if (running_profit>=90)
		{
			return 80.0f;
		}
		else if (running_profit>=80)
		{
			return 70.0f;
		}
		else if (running_profit>=70)
		{
			return 60.0f;
		}
		else if (running_profit>=60)
		{
			return 50.0f;
		}
		else if (running_profit>=50)
		{
			return 40.0f;
		}
		else if(running_profit>=45)
		{
			return 35.0f;
		}
		else if(running_profit>=40)
		{
			return 30.0f;
		}
		else if (running_profit>=35)
		{
			return 25.0f;
		}
		else if (running_profit>=30)
		{
			return 20.0f;
		}
		else if (running_profit>=25)
		{
			return 12.5f;
		}
		else if (running_profit>=20)
		{
			return 10.0f;
		}
		else if (running_profit>=15)
		{
			return 7.5f;
		}
		else if(running_profit>=10)
		{
			return 5.0f;
		}
		else if(running_profit>=5)
		{
			return 2.5f;
		}
		else if (running_profit>=2.5)
		{
			return 1.5f;
		}
		else if (running_profit>=1.25)
		{
			return 0.5f;
		}
		else
		{
			return 0.0f;
		}
	}
	public static void main(String... args)
	{
		PositionsHandler obj=new PositionsHandler();
		obj.handlePositions();
		obj.close();
	}
	
}
