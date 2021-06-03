package strategies;

public class OpenHighLow implements AutoCloseable
{
	OpenHighLow_JSONDataFetcher fetch;
	OpenHighLow_JSONDataAnalyzer process;
	
	OpenHighLow()
	{
		this.fetch = new OpenHighLow_JSONDataFetcher();
		this.process=new OpenHighLow_JSONDataAnalyzer();
	}
	public void close()
	{
		this.fetch=null;
		this.process=null;
		System.gc();
	}
	public void runStrategy()
	{
		//this.fetch.fetchJSONData();
		this.process.analyzeDataAndPlaceOrder();
	}
	public static void main(String... args)
	{
		OpenHighLow strategy=new OpenHighLow();
		strategy.runStrategy();
		strategy.close();
	}
}
