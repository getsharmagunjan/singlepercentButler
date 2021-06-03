package butlerforfyers;

import java.util.ArrayList;
import java.util.List;
//import org.apache.logging.log4j.Logger;
import org.apache.log4j.Logger;

public class OrderProcessor 
{
	private String symbol;
	private int qty;
	private int type;
	//private int default_type;
	private int side;
	private String productType;
	private String default_productType;
	private float limitPrice;
	private float default_limitPrice;
	private float stopPrice;
	private float default_stopPrice;
	private int disclosedQty;
	private int default_disclosedQty;
	private String validity;
	private String default_validity;
	private String offlineOrder;
	private String default_offlineOrder;
	private float stopLoss;
	private float takeProfit;
	private float default_takeProfit;
	
	private OrderPlacer placeorder;
	private Logger butlog;
	private List<String> orders;
	private OtherPropertiesLoader property=OtherPropertiesLoader.initialize();
	
	public OrderProcessor()
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
		
		this.default_disclosedQty=Integer.parseInt(this.property.getPropertyValue("default_disclosedQty"));
		this.default_limitPrice=Float.parseFloat(this.property.getPropertyValue("default_limitPrice"));
		this.default_offlineOrder=this.property.getPropertyValue("default_offlineOrder");
		this.default_productType=this.property.getPropertyValue("default_productType");
		this.default_stopPrice=Float.parseFloat(this.property.getPropertyValue("default_stopPrice"));
		this.default_takeProfit=Float.parseFloat(this.property.getPropertyValue("default_takeProfit"));
		//this.default_type=Integer.parseInt(this.property.getPropertyValue("default_type"));
		this.default_validity=this.property.getPropertyValue("default_validity");
		
		this.placeorder=new OrderPlacer();
		this.orders=new ArrayList<String>();
		this.butlog=ButlerLogger.getButlerLogger(this.getClass().getName());
	}
	public void processOrder(List<String> orders)
	{
		//Sell POWERGRID QTY=83 SL=162.65
		//Buy GODREJCP QTY=27 SL=491.45
		this.orders=orders;
		if(this.default_productType.equalsIgnoreCase("CO"))
		{
			for (String root_order : this.orders)
			{			
				String[] order=root_order.split(" ");
				if(order[0].equalsIgnoreCase("Sell"))
				{
					this.side=-1;
				}
				else if(order[0].equalsIgnoreCase("Buy"))
				{
					this.side=1;
				}
				this.symbol=order[1];
				String[] qty=order[2].split("=");
				this.qty=Integer.parseInt(qty[1]);
				String[] stoploss=order[3].split("=");
				this.stopLoss=Float.parseFloat(stoploss[1]);
				this.disclosedQty=this.default_disclosedQty;
				this.limitPrice=this.default_limitPrice;
				this.offlineOrder=this.default_offlineOrder;
				this.productType="CO";
				this.stopPrice=this.default_stopPrice;
				this.takeProfit=this.default_takeProfit;
				this.type=2;
				this.validity=this.default_validity;
				this.butlog.warn("symbol="+this.symbol+"\t"+"Qty="+this.qty+"\t"+"Type="+this.type+"\t"+"Side="+this.side+"\t"+"Producttype="+this.productType+"\t"+"limitprice="+this.limitPrice+"\t"+"Stopprice="+this.stopPrice+"\t"+"Disclosed Qty="+this.disclosedQty+"\t"+"Validity="+this.validity+"\t"+"OfflineOrder="+this.offlineOrder+"\t"+"StopLoss="+this.stopLoss+"\t"+"TakeProfit="+this.takeProfit);
				this.placeorder.placeOrder(this.symbol, this.qty, this.type, this.side, this.productType, this.limitPrice, this.stopPrice, this.disclosedQty, this.validity, this.offlineOrder, this.stopLoss, this.takeProfit);
				this.butlog.info("CO Order Processing done.");
			}
		}
		if(this.default_productType.equalsIgnoreCase("BO"))
		{	//Sell AXISBANK QTY=14 SL=707.05 Trigger=702.7 Price=701.7 Target=673
			for (String root_order : this.orders)
			{			
				String[] order=root_order.split(" ");
				if(order[0].equalsIgnoreCase("Sell"))
				{
					this.side=-1;
				}
				else if(order[0].equalsIgnoreCase("Buy"))
				{
					this.side=1;
				}
				this.symbol=order[1];
				String[] qty=order[2].split("=");
				this.qty=Integer.parseInt(qty[1]);
				String[] stoploss=order[3].split("=");
				this.stopLoss=Float.parseFloat(stoploss[1]);
				this.disclosedQty=this.default_disclosedQty;
				String[] trigger=order[4].split("=");
				this.stopPrice=Float.parseFloat(trigger[1]);
				String[] price_to_buy_sell = order[5].split("=");
				this.limitPrice=Float.parseFloat(price_to_buy_sell[1]);
				this.offlineOrder=this.default_offlineOrder;
				this.productType="BO";
				String[] target = order[6].split("=");
				this.takeProfit=Float.parseFloat(target[1]);
				this.type=4; //1=Limit, 2=Market, 3=Stop(SL-M), 4=StopLimit(SL-L)
				this.validity=this.default_validity;
				this.butlog.warn("symbol="+this.symbol+"\t"+"Qty="+this.qty+"\t"+"Type="+this.type+"\t"+"Side="+this.side+"\t"+"Producttype="+this.productType+"\t"+"limitprice="+this.limitPrice+"\t"+"Stopprice="+this.stopPrice+"\t"+"Disclosed Qty="+this.disclosedQty+"\t"+"Validity="+this.validity+"\t"+"OfflineOrder="+this.offlineOrder+"\t"+"StopLoss="+this.stopLoss+"\t"+"TakeProfit="+this.takeProfit);
				this.placeorder.placeOrder(this.symbol, this.qty, this.type, this.side, this.productType, this.limitPrice, this.stopPrice, this.disclosedQty, this.validity, this.offlineOrder, this.stopLoss, this.takeProfit);
				this.butlog.info("BO Order Processing done.");
			}
		}
		this.butlog.info("Order Processing Completed Successfully.");
	}
}
