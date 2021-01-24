package butlerforfyers;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
			this.request_payload_in_string=this.getRequestParametersInString();
			//this.butlog.warn("Check Request payload in String =\n"+this.request_payload_in_string);
			this.list_of_string=this.restconnection.sendRequest(this.order_request_url, this.request_payload_in_string);
			//this.butlog.warn("Check List of String returned is ="+this.list_of_string.toString());
			this.response_payload_response_code=Integer.parseInt(this.list_of_string.get(0));
			this.butlog.warn("for symbol = "+symbol);
			this.butlog.warn("Check Response payload status code ="+this.response_payload_response_code);
			this.response_payload_in_string=this.list_of_string.get(1);
			this.butlog.warn("Check response payload in string =\n"+this.response_payload_in_string);
			this.tofind_value=this.handler.getResponseAttribute(this.response_payload_in_string, this.response_message_text);
			this.order_id=this.handler.getResponseAttribute(this.response_payload_in_string, this.response_id_text);
			this.butlog.info("REMOVE THIS: The value of "+this.response_message_text+"is ="+this.tofind_value+" And the Response Status Code is ="+this.response_payload_response_code);
			if(this.tofind_value.equalsIgnoreCase(this.market_not_connected_message))
			{
				this.butlog.error(this.market_not_connected_message);
			}
			else if(this.order_id.isEmpty())
			{
				this.butlog.error("Order ID is NULL. Need Manual Intervention..");
			}
			else
			{
				this.storeOrderToDB();
				this.butlog.info("Finished placing order with Response="+this.response_payload_response_code+".");
			}
			
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
	private void storeOrderToDB()
	{
		String order_type=null;
		String side=null;
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
			connection.storeOrderToDB(this.order_id, this.symbol, this.qty, order_type, side, this.productType, this.limitPrice, this.stopPrice, this.disclosedQty, this.validity, this.offlineOrder, this.stopLoss, this.takeProfit, this.tofind_value, this.order_status, this.pnl_percentage, this.strategy_name, this.pnl_amount, this.mod_stoploss,null,null,null,null,null);
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
		order.placeOrder("BANKINDIA", 1, 2, 1, "CO", 0, 0, 0, "DAY", "False", 180f, 0);
	}*/
}
