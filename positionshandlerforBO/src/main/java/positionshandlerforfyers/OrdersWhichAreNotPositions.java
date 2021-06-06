package positionshandlerforfyers;

public class OrdersWhichAreNotPositions 
{
	private String symbol;
	private String id;
	private String stayOrRemoved;
	private int butlerRecordId;
	private String side;
	private float limitPrice;
	private int qty;
	private int updatedQtyForStayingOrders;
	private float stopLoss;
	private int type;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public float getStopLoss() {
		return stopLoss;
	}
	public void setStopLoss(float stopLoss) {
		this.stopLoss = stopLoss;
	}
	public int getUpdatedQtyForStayingOrders() {
		return updatedQtyForStayingOrders;
	}
	public void setUpdatedQtyForStayingOrders(int updatedQtyForStayingOrders) {
		this.updatedQtyForStayingOrders = updatedQtyForStayingOrders;
	}
	public String getSide() {
		return side;
	}
	public void setSide(String side) {
		this.side = side;
	}
	public float getLimitPrice() {
		return limitPrice;
	}
	public void setLimitPrice(float limitPrice) {
		this.limitPrice = limitPrice;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public String getSymbol() {
		return symbol;
	}
	public int getButlerRecordId() {
		return butlerRecordId;
	}
	public void setButlerRecordId(int butlerRecordId) {
		this.butlerRecordId = butlerRecordId;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStayOrRemoved()
	{
		return this.stayOrRemoved;
	}
	public void setStayOrRemoved(String stayOrRemoved)
	{
		this.stayOrRemoved=stayOrRemoved;
	}
}
