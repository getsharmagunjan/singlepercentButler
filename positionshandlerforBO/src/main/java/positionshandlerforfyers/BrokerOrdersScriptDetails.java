package positionshandlerforfyers;

public class BrokerOrdersScriptDetails 
{
	private String id; //broker ID
	private String exchOrdId; //Exchange Order ID
	private String symbol;
	private int qty;
	private int remainingQuantity;
	private int filledQty;
	private int status;
	private int slNo;
	private String message;
	private String segment;
	private float limitPrice;
	private float stopPrice;
	private int type;
	private int side;
	private int disclosedQty;
	private String orderValidity;
	private String orderDateTime;
	private String parentId;
	private float tradedPrice;
	private String orderNumStatus;
	private int dqQtyRem;
	private String fyToken;
	private String instrument;
	private String source;
	private boolean offlineOrder;
	private String productType;
	
	BrokerOrdersScriptDetails()
	{
		this.id=null;
		this.exchOrdId=null;
		this.symbol=null;
		this.qty=0;
		this.remainingQuantity=0;
		this.filledQty=0;
		this.status=0;
		this.slNo=0;
		this.message=null;
		this.segment=null;
		this.limitPrice=0.0f;
		this.stopPrice=0.0f;
		this.type=0;
		this.side=0;
		this.disclosedQty=0;
		this.orderValidity=null;
		this.orderDateTime=null;
		this.parentId=null;
		this.tradedPrice=0.0f;
		this.orderNumStatus=null;
		this.dqQtyRem=0;
		this.fyToken=null;
		this.instrument=null;
		this.source=null;
		this.offlineOrder=false;
		this.productType=null;
	}
	
	public String getSide() 
	{
		if(this.side==1)
		{
			return "Buy";
		}
		else if(this.side==-1)
		{
			return "Sell";
		}
		else
		{
			return "Side not available";
		}
	}
	public void setSide(int side) 
	{
		this.side = side;
	}
	public int getDisclosedQty() 
	{
		return this.disclosedQty;
	}
	public void setDisclosedQty(int disclosedQty) 
	{
		this.disclosedQty = disclosedQty;
	}
	public String getOrderValidity() 
	{
		return this.orderValidity;
	}
	public void setOrderValidity(String orderValidity) 
	{
		this.orderValidity = orderValidity;
	}
	public String getOrderDateTime() 
	{
		return this.orderDateTime;
	}
	public void setOrderDateTime(String orderDateTime) 
	{
		this.orderDateTime = orderDateTime;
	}
	public String getParentId() 
	{
		return this.parentId;
	}
	public void setParentId(String parentId) 
	{
		this.parentId = parentId;
	}
	public float getTradedPrice() 
	{
		return this.tradedPrice;
	}
	public void setTradedPrice(float tradedPrice) 
	{
		this.tradedPrice = tradedPrice;
	}
	public String getOrderNumStatus() 
	{
		return this.orderNumStatus;
	}
	public void setOrderNumStatus(String orderNumStatus) 
	{
		this.orderNumStatus = orderNumStatus;
	}
	public int getDqQtyRem() 
	{
		return this.dqQtyRem;
	}
	public void setDqQtyRem(int dqQtyRem) 
	{
		this.dqQtyRem = dqQtyRem;
	}
	public String getFyToken() 
	{
		return this.fyToken;
	}
	public void setFyToken(String fyToken) 
	{
		this.fyToken = fyToken;
	}
	public String getInstrument() 
	{
		return this.instrument;
	}
	public void setInstrument(String instrument) 
	{
		this.instrument = instrument;
	}
	public String getSource() 
	{
		return this.source;
	}
	public void setSource(String source) 
	{
		this.source = source;
	}
	public boolean isOfflineOrder() 
	{
		return this.offlineOrder;
	}
	public void setOfflineOrder(boolean offlineOrder) 
	{
		this.offlineOrder = offlineOrder;
	}
	public String getProductType() 
	{
		return this.productType;
	}
	public void setProductType(String productType) 
	{
		this.productType = productType;
	}
	public void setId(String id)
	{
		this.id=id;
	}
	public String getId()
	{
		return this.id;
	}
	public void setExchOrdId(String exchOrdId)
	{
		this.exchOrdId=exchOrdId;
	}
	public String getExchOrdId()
	{
		return this.exchOrdId;
	}
	public void setSymbol(String symbol)
	{
		this.symbol=symbol;
	}
	public String getSymbol()
	{
		return this.symbol;
	}
	public void setQty(int qty)
	{
		this.qty=qty;
	}
	public int getQty()
	{
		return this.qty;
	}
	public void setRemainingQuantity(int remainingQuantity)
	{
		this.remainingQuantity=remainingQuantity;
	}
	public int getRemainingQuantity()
	{
		return this.remainingQuantity;
	}
	public void setFilledQty(int filledQty)
	{
		this.filledQty=filledQty;
	}
	public int getFilledQty()
	{
		return this.filledQty;
	}
	public void setStatus(int status)
	{
		this.status=status;
	}
	public String getStatus()
	{
		if (this.status==1)
		{
			return "Canceled";
		}
		else if (this.status ==2)
		{
			return "Traded";
		}
		else if (this.status==3)
		{
			return "Not used currently";
		}
		else if (this.status==4)
		{
			return "Transit";
		}
		else if (this.status==5)
		{
			return "Rejected";
		}
		else if (this.status==6)
		{
			return "Pending";
		}
		else
		{
			return "Status not available";
		}
		
	}
	public void setSlNo(int slNo)
	{
		this.slNo=slNo;
	}
	public int getSlNo()
	{
		return this.slNo;
	}
	public void setMessage(String message)
	{
		this.message=message;
	}
	public String getMessage()
	{
		return this.message;
	}
	public void setSegment(String segment)
	{
		this.segment=segment;
	}
	public String getSegment()
	{
		if (this.segment.equalsIgnoreCase("E"))
		{
			return "Equity";
		}
		else if(this.segment.equalsIgnoreCase("D"))
		{
			return "F&O";
		}
		else if (this.segment.equalsIgnoreCase("C"))
		{
			return "Currency";
		}
		else if (this.segment.equalsIgnoreCase("M")) 
		{
			return "Commodity";
		}
		else
		{
			return "Segment not available";
		}
	}
	public void setLimitPrice(float limitPrice)
	{
		this.limitPrice=limitPrice;
	}
	public float getLimitPrice()
	{
		return this.limitPrice;
	}
	public void setStopPrice(float stopPrice)
	{
		this.stopPrice=stopPrice;
	}
	public float getStopPrice()
	{
		return this.stopPrice;
	}
	public void setType(int type)
	{
		this.type=type;
	}
	public String getType()
	{
		if(this.type==1)
		{
			return "Limit Order";
		}
		else if(this.type==2)
		{
			return "Market Order";
		}
		else if(this.type==3)
		{
			return "Stop order (SL-M)";
		}
		else if(this.type==4)
		{
			return "Stoplimit order (SL-L)";
		}
		else
		{
			return "Type not available";
		}
	}
}
