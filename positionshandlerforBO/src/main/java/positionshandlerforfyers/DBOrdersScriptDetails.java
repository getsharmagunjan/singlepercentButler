package positionshandlerforfyers;

public class DBOrdersScriptDetails 
{
	private String symbol;
	private int butler_record_id;
	private int qty;
	private String side;
	private float initialStoploss;
	private float pnlPercentage;
	private float stoplossModified;
	private String brokerOrderRefNo;
	private float limitPrice;
	private float pl;
	
	DBOrdersScriptDetails()
	{
		this.symbol=null;
		this.butler_record_id=0;
		this.qty=0;
		this.side=null;
		this.initialStoploss=0.0f;
		this.pnlPercentage=0.0f;
		this.stoplossModified=0.0f;
		this.brokerOrderRefNo=null;
		this.limitPrice=0.0f;
		this.pl=0.0f;
	}
	
	public float getPl() {
		return pl;
	}

	public void setPl(float pl) {
		this.pl = pl;
	}

	public float getLimitPrice() {
		return limitPrice;
	}

	public void setLimitPrice(float limitPrice) {
		this.limitPrice = limitPrice;
	}

	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public int getButler_record_id() {
		return butler_record_id;
	}
	public void setButler_record_id(int butler_record_id) {
		this.butler_record_id = butler_record_id;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public String getSide() {
		return side;
	}
	public void setSide(String side) {
		this.side = side;
	}
	public float getInitialStoploss() {
		return initialStoploss;
	}
	public void setInitialStoploss(float initialStoploss) {
		this.initialStoploss = initialStoploss;
	}
	public float getPnlPercentage() {
		return pnlPercentage;
	}
	public void setPnlPercentage(float pnlPercentage) {
		this.pnlPercentage = pnlPercentage;
	}
	public float getStoplossModified() {
		return stoplossModified;
	}
	public void setStoplossModified(float stoplossModified) {
		this.stoplossModified = stoplossModified;
	}
	public String getBrokerOrderRefNo() {
		return brokerOrderRefNo;
	}
	public void setBrokerOrderRefNo(String brokerOrderRefNo) {
		this.brokerOrderRefNo = brokerOrderRefNo;
	}
	
}
