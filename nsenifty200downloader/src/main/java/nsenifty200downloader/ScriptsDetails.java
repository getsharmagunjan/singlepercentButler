package nsenifty200downloader;

public class ScriptsDetails implements AutoCloseable 
{
	private String symbol;
	private float open;
	private float ltp;
	private float low;
	private float high;
	
	public ScriptsDetails()
	{
		this.setHigh(0.0f);
		this.setLow(0.0f);
		this.setLtp(0.0f);
		this.setOpen(0.0f);
		this.setSymbol(null);
	}
	
	@Override
	public void close()
	{
		this.setHigh(0.0f);
		this.setLow(0.0f);
		this.setLtp(0.0f);
		this.setOpen(0.0f);
		this.setSymbol(null);
		System.gc();
		Runtime.getRuntime().gc();
	}
	
	public String getSymbol() {
		return this.symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public float getOpen() {
		return this.open;
	}
	public void setOpen(float open) {
		this.open = open;
	}
	public float getLtp() {
		return this.ltp;
	}
	public void setLtp(float ltp) {
		this.ltp = ltp;
	}
	public float getLow() {
		return this.low;
	}
	public void setLow(float low) {
		this.low = low;
	}
	public float getHigh() {
		return this.high;
	}
	public void setHigh(float high) {
		this.high = high;
	}
}
