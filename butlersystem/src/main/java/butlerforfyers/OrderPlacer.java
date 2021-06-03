package butlerforfyers;


import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.math3.util.Precision;
//import org.apache.logging.log4j.Logger;

public class OrderPlacer 
{
	private String symbol;
	private int qty;
	private int type;
	private int side;
	private String productType;
	private float limitPrice;
	private float stopPrice;
	private int disclosedQty;
	private String validity;
	private String offlineOrder;
	private float stopLoss;
	private float takeProfit;
	private String request_payload_in_string;
	private String response_payload_in_string;
	private int response_payload_response_code;
	private String tofind_value;
	private String order_request_url;
	private String order_id;
	private String response_message_text;
	private String market_not_connected_message;
	private String response_id_text;
	private String order_status;
	private float pnl_percentage;
	private float pnl_amount;
	private String strategy_name;
	private float mod_stoploss;
	private String profit_gap_out_of_range_message;
	private String trigger_gap_out_of_range_message;
	private String order_not_enabled_message;
	private Logger butlog;
	private static DBConnection connection=new DBConnection();
	private ButlerResponseHandler handler;
	private List<String> list_of_string;
	private RESTConnectionHandler restconnection;
	OtherPropertiesLoader property=OtherPropertiesLoader.initialize();
	
	OrderPlacer()
	{
		this.symbol=null;
		this.disclosedQty=0;
		this.type=0;
		this.side=0;
		this.productType=null;
		this.limitPrice=0.0f;
		this.stopPrice=0.0f;
		this.disclosedQty=0;
		this.validity=null;
		this.offlineOrder=null;
		this.stopLoss=0.0f;
		this.takeProfit=0.0f;
		this.pnl_percentage=0.0f;
		this.pnl_amount=0.0f;
		this.mod_stoploss=0.00f;
		this.response_payload_in_string=null;
		this.response_payload_response_code=0;
		this.tofind_value=null;
		this.order_id=null;
		this.order_status=null;
		this.strategy_name=null;
		this.list_of_string=new ArrayList<String>();
		this.order_request_url=this.property.getPropertyValue("order_request_url");
		this.profit_gap_out_of_range_message=this.property.getPropertyValue("profit_gap_out_of_range_message");
		this.trigger_gap_out_of_range_message=this.property.getPropertyValue("trigger_gap_out_of_range_message");
		this.order_not_enabled_message=this.property.getPropertyValue("order_not_enabled_message");
		this.response_message_text=this.property.getPropertyValue("response_message_text");
		this.market_not_connected_message=this.property.getPropertyValue("market_not_connected_message");
		this.response_id_text=this.property.getPropertyValue("response_id_text");
		this.butlog=ButlerLogger.getButlerLogger(this.getClass().getName());
		this.handler=new ButlerResponseHandler();
		this.restconnection=new RESTConnectionHandler();
	}
	
	private String getRequestParametersInString() throws Exception
	{
		String request_payload=null;
		request_payload=
			"{" 
				+"\"symbol\":\""+this.symbol+"\"," 
				+"\"qty\":"+this.qty+"," 
				+"\"type\":"+this.type+"," 
				+"\"side\":"+this.side+"," 
				+"\"productType\":\""+this.productType+"\"," 
				+"\"limitPrice\":"+this.limitPrice+"," 
				+"\"stopPrice\":"+this.stopPrice+"," 
				+"\"disclosedQty\":"+this.disclosedQty+"," 
				+"\"validity\":\""+this.validity+"\","
				+"\"offlineOrder\":\""+this.offlineOrder+"\"," 
				+"\"stopLoss\":"+this.stopLoss+"," 
				+"\"takeProfit\":"+this.takeProfit+ 
			"}";
		return request_payload;
	}
	
	public void placeOrder(String symbol, int qty, int type, int side, String productType, float limitPrice, float stopPrice, int disclosedQty, String validity, String offlineOrder, float stopLoss, float takeProfit)
	{
		try
		{
			//assignment to class specific variables.
			this.symbol="NSE:"+symbol+"-EQ";
			this.qty=qty;
			this.type=type;
			this.side=side;
			this.productType=productType;
			this.limitPrice=limitPrice;
			this.stopPrice=stopPrice;
			this.disclosedQty=disclosedQty;
			this.validity=validity;
			this.offlineOrder=offlineOrder;
			this.stopLoss=stopLoss;
			this.takeProfit=takeProfit;
			//this.butlog.warn("Check if Symbol is correct :="+this.symbol);
			//setting authorization access token.
			this.butlog.warn("##################################################################################################################");
			this.butlog.warn("###############"+this.symbol+"#################");
			//this.butlog.warn("Check Request payload in String =\n"+this.request_payload_in_string);
			int counter=1;
			do
			{
				this.butlog.warn("counter = "+counter);
				this.request_payload_in_string=this.getRequestParametersInString();
				this.list_of_string=this.restconnection.sendRequest(this.order_request_url, this.request_payload_in_string);
				counter++;
				this.response_payload_in_string=this.list_of_string.get(1);
				this.tofind_value=this.handler.getResponseAttribute(this.response_payload_in_string, this.response_message_text);
				
				
				//this.butlog.warn(this.list_of_string.get(0).toString().trim().equalsIgnoreCase("200"));
				//this.butlog.warn(this.response_payload_in_string);
				//this.butlog.warn(this.tofind_value);
				//this.tofind_value="RMS:121052516818:NSE EQUITY 16713 UBL EQ DG00303 B 1 BO 1270.55 TRIGGER GAP IS OUT OF THE SPECIFIED GAP RANGE MAX & MIN TRIGGER GAP RANGE IS -1207.02-1269.91 TRIGGER PRICE-1270";
				
				this.order_id=this.handler.getResponseAttribute(this.response_payload_in_string, this.response_id_text);
				
				if(this.tofind_value.contains(this.market_not_connected_message))
				{
					this.butlog.error(this.market_not_connected_message);
					break;
				}
				else if(this.tofind_value.contains(this.order_not_enabled_message))
				{
					break;
				}
				else if(this.list_of_string.get(0).toString().trim().equalsIgnoreCase("200"))
				{
					break;
				}
				else if(this.order_id.isEmpty())
				{
					this.butlog.error("Order ID is NULL. Need Manual Intervention..");
					break;
				}
				else if(this.tofind_value.contains(this.profit_gap_out_of_range_message))
				{
					float target=this.reCalculateTarget(this.tofind_value, this.side);
					switch(this.side)
					{
					case 1:
						this.takeProfit=target-this.limitPrice;
						break;
					case -1:
						this.takeProfit=this.limitPrice-target;
						break;
					}
				}
				else if(this.tofind_value.contains(this.trigger_gap_out_of_range_message))
				{
					float newStopPrice=this.reCalculateStopPrice(this.tofind_value);
					this.stopPrice=newStopPrice;
					switch(this.side)
					{
					case 1:
						this.limitPrice=this.stopPrice+0.50f;
						break;
					case -1:
						this.limitPrice=this.stopPrice-0.50f;
					}
					this.butlog.warn("New StopPrice = "+this.stopPrice);
					this.butlog.warn("New Limit Price = "+this.limitPrice);
				}
				
			}while(counter<=3);
			this.butlog.warn("for symbol = "+symbol+"-----------------------------------------------------------------------------");
			this.butlog.warn("Check Response payload status code ="+this.response_payload_response_code);
			this.butlog.warn("Check response payload in string =\n"+this.response_payload_in_string);
			this.butlog.warn("REMOVE THIS: The value of "+this.response_message_text+" is ="+this.tofind_value+" And the Response Status Code is ="+this.response_payload_response_code);
			this.butlog.info("Finished placing order with Response="+this.response_payload_response_code+".");
			this.butlog.warn("----------------------------------------------------------------------------------------------------");
			this.butlog.warn("************************************************************************************************************");
			this.storeOrderToDB();
					
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method: placeOrder(parameters) in "+this.getClass().toString()+" is: \n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
		finally
		{
			this.list_of_string=null;
		}
	}
	public float reCalculateTarget(String responseMessage, int side)
	{
		float valueOne=0.0f;
		float valueTwo=0.0f;
		float highValue=0.0f;
		float lowValue=0.0f;
		valueOne=Float.parseFloat(responseMessage.split("-")[1].toString());
		valueTwo=Float.parseFloat(responseMessage.split("-")[2].split(" ")[0].toString());
		//this.butlog.info("value one = "+valueOne+" value Two = "+valueTwo);
		if(valueOne>valueTwo)
		{
			highValue=valueOne;
			lowValue=valueTwo;
		}
		else
		{
			highValue=valueTwo;
			lowValue=valueOne;
		}
		//this.butlog.info("high value = "+highValue+" low value = "+lowValue);
		if(side==1)
		{
			//this.butlog.info("return value  = "+Precision.round(closestModifiedToPointZeroFive(highValue-1,0.05f),2));
			return Precision.round(closestModifiedToPointZeroFive(highValue-0.05f,0.05f),2);
		}
		else
		{
			//this.butlog.info("return value  = "+Precision.round(closestModifiedToPointZeroFive(lowValue+1,0.05f),2));
			return Precision.round(closestModifiedToPointZeroFive(lowValue+0.05f,0.05f),2);
		}
	}
	public float reCalculateStopPrice(String responseMessage)
	{
		float valueOne=0.0f;
		float valueTwo=0.0f;
		valueOne=Float.parseFloat(responseMessage.split("-")[1].toString());
		valueTwo=Float.parseFloat(responseMessage.split("-")[2].split(" ")[0].toString());
		this.butlog.info("value one = "+valueOne+" value Two = "+valueTwo);
		float diffToOne=this.stopPrice-valueOne;
		float diffToTwo=this.stopPrice-valueTwo;
		this.butlog.info("diff one = "+Math.abs(diffToOne)+" diff two = "+Math.abs(diffToTwo));
		if(Math.abs(diffToOne)<Math.abs(diffToTwo))
		{
			return Precision.round(closestModifiedToPointZeroFive(valueOne,0.05f), 2);
		}
		else
		{
			return Precision.round(closestModifiedToPointZeroFive(valueTwo,0.05f), 2);
		}
		
	}
	private float closestModifiedToPointZeroFive(float a, float b) 
	{
	    float c1 = a - (a % b);
	    float c2 = (a + b) - (a % b);
	    if (a - c1 > c2 - a) {
	        return c2;
	    } else {
	        return c1;
	    }
	}
	private void storeOrderToDB()
	{
		String order_type=null;
		String side=null;
		float calculatedStopLoss=0.0f;
		float calculatedTakeProfit=0.0f;
		this.symbol=this.symbol.replaceAll("NSE:", "");
		this.symbol=symbol.replaceAll("-EQ", "");
		if(this.tofind_value.contains("Order Submitted Successfully."))
		{
			this.order_status="Success";
		}
		else
		{
			this.order_status="Failed";
		}
		//this.butlog.warn(this.order_status);
		try
		{
			//this.butlog.warn(this.type);
			this.strategy_name="OpenHighLow";
			switch (this.type)
			{
			case 1: order_type="Limit"; break;
			case 2: order_type="Market"; break;
			case 3: order_type="SL-M"; break;
			case 4: order_type="SL-L"; break;
			}
			//this.butlog.warn(order_type);
			switch(this.side)
			{
			case 1: side="Buy"; break;
			case -1: side="Sell"; break;
			}
			switch(side)
			{
			case "Buy": 
				calculatedStopLoss=limitPrice-stopLoss;
				calculatedTakeProfit=limitPrice+takeProfit;
				break;
			case "Sell":
				calculatedStopLoss=limitPrice+stopLoss;
				calculatedTakeProfit=limitPrice-takeProfit;
				break;
			}
			connection.storeOrderToDB(this.order_id, this.symbol, this.qty, order_type, side, this.productType, this.limitPrice, this.stopPrice, this.disclosedQty, this.validity, this.offlineOrder, calculatedStopLoss, calculatedTakeProfit, this.tofind_value, this.order_status, this.pnl_percentage, this.strategy_name, this.pnl_amount, this.mod_stoploss,null,null,null,null,null);
			//connection.storeOrderToDB("100001","TEST",10,"Market","Buy","CO",0.0f, 0.0f, 0,"DAY","False",100.50f,0.0f,"This is test record from code",null,null,null,null,null);
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method: storeOrderToDB() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}
	
	
	/*public static void main (String args[])
	{
		OrderPlacer order=new OrderPlacer();
		//order.placeOrder("BANKINDIA", 1, 2, 1, "CO", 0, 0, 0, "DAY", "False", 1.0f, 100f);
		order.placeOrder("UBL", 1, 4, 1, "BO", 1270.55f, 1270.05f, 0, "DAY", "False", 1.0f, 100f);
		//order.stopPrice=1270.05f;
		//float stopnew = order.reCalculateStopPrice("RMS:121052516818:NSE EQUITY 16713 UBL EQ DG00303 B 1 BO 1270.55 TRIGGER GAP IS OUT OF THE SPECIFIED GAP RANGE MAX & MIN TRIGGER GAP RANGE IS -1207.02-1269.91 TRIGGER PRICE-1270");
		//order.butlog.info("stopnew = "+stopnew);
	}*/
}
