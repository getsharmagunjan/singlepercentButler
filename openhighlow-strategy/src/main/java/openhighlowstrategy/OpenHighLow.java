package openhighlowstrategy;

public class OpenHighLow implements AutoCloseable
{
	private JSONDataFetcher fetch;
	private JSONDataAnalyzer process;
	
	OpenHighLow()
	{
		this.fetch = new JSONDataFetcher();
		this.process=new JSONDataAnalyzer();
	}
	public void close()
	{
		this.fetch=null;
		this.process=null;
		System.gc();
		System.exit(0);
	}
	public void runStrategy()
	{
		this.fetch.fetchJSONData(); //disabling this as on windows as well as ubuntu, json data is unable to load and giving error as SocketTimeoutException.
		this.process.analyzeDataAndPlaceOrder();
	}
	public static void main(String... args)
	{
		OpenHighLow strategy=new OpenHighLow();
		strategy.runStrategy();
		strategy.close();
	}
}
