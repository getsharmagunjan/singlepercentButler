package yahoofinancescrapper;
//import org.apache.log4j.Logger;
import org.jsoup.*;
//import org.jsoup.helper.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

//import com.google.common.io.Files;

//import java.io.*; // Only needed if scraping a local File.
//import java.nio.file.Paths;

public class Scrapper implements AutoCloseable
{

	private float open;
	private float ltp;
	private float low;
	private float high;
	private String symbol;
	private String url;
	private final String yahooURLPrefix="https://finance.yahoo.com/quote/";
	private final String yahooURLSuffix=".NS";
	private Document doc;
	//private Element table;
	private Element headerInfo;
	private Elements daysrange;
	private Elements openvalue;
	private Elements spanOfheaderInfo;
	
	public Scrapper()
	{
		this.doc=null;
		//this.table=null;
		this.headerInfo=null;
		this.daysrange=null;
		this.openvalue=null;
		this.spanOfheaderInfo=null;
	}
	
	public void close()
	{
		//this.doc.remove();
		this.doc=null;
		//this.table.remove();
		//this.headerInfo.remove();
		//this.table=null;
		this.headerInfo=null;
		this.daysrange=null;
		this.openvalue=null;
		this.spanOfheaderInfo=null;
		this.open=0.0f;
		this.ltp=0.0f;
		this.low=0.0f;
		this.high=0.0f;
		this.symbol=null;
		this.url=null;
		System.gc();
		
	}
	
	public float getOpen() {
		return open;
	}
	public void setOpen(float open) {
		this.open = open;
	}
	public float getLtp() {
		return ltp;
	}
	public void setLtp(float ltp) {
		this.ltp = ltp;
	}
	public float getLow() {
		return low;
	}
	public void setLow(float low) {
		this.low = low;
	}
	public float getHigh() {
		return high;
	}
	public void setHigh(float high) {
		this.high = high;
	}
	public void setSymbol(String symbol)
	{
		this.symbol=symbol;
	}
	public String getSymbol()
	{
		return this.symbol;
	}
	
	public void fetchAndSetData() 
	{
		//this.setSymbol(symbol);
		this.url = this.yahooURLPrefix+this.getSymbol()+this.yahooURLSuffix;
			
		try {
			this.doc = Jsoup.connect(this.url).get();
			/*BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\butler-system\\test\\doc.txt"));
			writer.write(doc.toString());
			writer.close();*/
		} 
		catch (Exception ioe) 
		{
			ioe.printStackTrace();
		}
		//System.out.println(doc.toString());
		
		this.daysrange = this.doc.getElementsByAttributeValue("data-test", "DAYS_RANGE-value");
		this.openvalue = this.doc.getElementsByAttributeValue("data-test", "OPEN-value");
		this.headerInfo=this.doc.getElementById("quote-header-info");
		this.spanOfheaderInfo=this.headerInfo.getElementsByClass("D(ib) Mend(20px)");
		this.setLtp(Float.parseFloat(this.spanOfheaderInfo.text().split("\\+|-")[0].replace(",", "").toString()));
		//System.out.println(daysrange.toString());
		for(Element value : daysrange)
		{
			//System.out.println("Data = "+ value.childNodes().get(0)+"\nLow = "+value.childNodes().get(0).toString().split("-")[0].trim()+"\nHigh = "+value.childNodes().get(0).toString().split("-")[1].trim());
			this.setLow(Float.parseFloat(value.childNodes().get(0).toString().split("-")[0].replace(",", "").trim()));
			this.setHigh(Float.parseFloat(value.childNodes().get(0).toString().split("-")[1].replace(",", "").trim()));
		}
		for(Element value : openvalue)
		{
			//System.out.println("Open = "+ value.childNodes().get(0).childNodes().get(0));
			this.setOpen(Float.parseFloat(value.childNodes().get(0).childNodes().get(0).toString().replace(",", "").trim()));
		}
			
		//System.out.println(this.getSymbol()+"\t"+this.getLtp()+"\t"+this.getOpen()+"\t"+this.getLow()+"-"+this.getHigh());
		
	}
	
	/*public static void main (String args[]) {

		Scrapper scrap = new Scrapper();
		String[] symbols= {"AARTIIND",
				"ACC",
				"ADANIENT",
				"ADANIPORTS",
				"AMARAJABAT",
				"AMBUJACEM",
				"APOLLOHOSP",
				"APOLLOTYRE",
				"ASHOKLEY",
				"ASIANPAINT",
				"AUROPHARMA",
				"AXISBANK",
				"BAJAJ-AUTO",
				"BAJAJFINSV",
				"BAJFINANCE",
				"BALKRISIND",
				"BANDHANBNK",
				"BANKBARODA",
				"BATAINDIA",
				"BEL",
				"BERGEPAINT",
				"BHARATFORG",
				"BHARTIARTL",
				"BHEL",
				"BIOCON",
				"BOSCHLTD",
				"BPCL",
				"BRITANNIA",
				"CADILAHC",
				"CANBK",
				"CHOLAFIN",
				"CIPLA",
				"COALINDIA",
				"COFORGE",
				"COLPAL",
				"CONCOR",
				"CUMMINSIND",
				"DABUR",
				"DIVISLAB",
				"DLF",
				"DRREDDY",
				"EICHERMOT",
				"ESCORTS",
				"EXIDEIND",
				"FEDERALBNK",
				"GAIL",
				"GLENMARK",
				"GMRINFRA",
				"GODREJCP",
				"GODREJPROP",
				"GRASIM",
				"HAVELLS",
				"HCLTECH",
				"HDFC",
				"HDFCAMC",
				"HDFCBANK",
				"HDFCLIFE",
				"HEROMOTOCO",
				"HINDALCO",
				"HINDPETRO",
				"HINDUNILVR",
				"IBULHSGFIN",
				"ICICIBANK",
				"ICICIGI",
				"ICICIPRULI",
				"IDEA",
				"IDFCFIRSTB",
				"IGL",
				"INDIGO",
				"INDUSINDBK",
				"INDUSTOWER",
				"INFY",
				"IOC",
				"ITC",
				"JINDALSTEL",
				"JSWSTEEL",
				"JUBLFOOD",
				"KOTAKBANK",
				"L&TFH",
				"LALPATHLAB",
				"LICHSGFIN",
				"LT",
				"LUPIN",
				"M&M",
				"M&MFIN",
				"MANAPPURAM",
				"MARICO",
				"MARUTI",
				"MCDOWELL-N",
				"MFSL",
				"MGL",
				"MINDTREE",
				"MOTHERSUMI",
				"MRF",
				"MUTHOOTFIN",
				"NATIONALUM",
				"NAUKRI",
				"NESTLEIND",
				"NMDC",
				"NTPC",
				"ONGC",
				"PAGEIND",
				"PEL",
				"PETRONET",
				"PFC",
				"PIDILITIND",
				"PNB",
				"POWERGRID",
				"PVR",
				"RAMCOCEM",
				"RBLBANK",
				"RECLTD",
				"RELIANCE",
				"SAIL",
				"SBILIFE",
				"SBIN",
				"SHREECEM",
				"SIEMENS",
				"SRF",
				"SRTRANSFIN",
				"SUNPHARMA",
				"SUNTV",
				"TATACHEM",
				"TATACONSUM",
				"TATAMOTORS",
				"TATAPOWER",
				"TATASTEEL",
				"TCS",
				"TECHM",
				"TITAN",
				"TORNTPHARM",
				"TORNTPOWER",
				"TVSMOTOR",
				"UBL",
				"ULTRACEMCO",
				"UPL",
				"VEDL",
				"VOLTAS",
				"WIPRO",
				"ZEEL"};
		
		for(int i=0; i<symbols.length; i++)
		{
			scrap.setSymbol(symbols[i]);
			//System.out.println("START");
			//scrap.setSymbol("BIOCON");
			scrap.fetchAndSetData();
			System.out.println("\n"+scrap.getSymbol());
			System.out.println("LTP = "+scrap.getLtp());
			System.out.println("Open = "+scrap.getOpen());
			System.out.println("Low = "+scrap.getLow());
			System.out.println("High = "+scrap.getHigh());
			scrap.close();
		}
	}*/
	
}