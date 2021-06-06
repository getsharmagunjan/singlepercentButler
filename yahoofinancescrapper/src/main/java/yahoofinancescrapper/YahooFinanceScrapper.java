package yahoofinancescrapper;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.google.api.services.sheets.v4.model.ValueRange;

import butlerforfyers.ButlerLogger;
import butlerforfyers.OtherPropertiesLoader;
import spreadsheethandler.GoogleSpreadsheetHandler;

public class YahooFinanceScrapper implements AutoCloseable
{
	private String forBOSheetSizeDeterminingRange;
	private String forBOSheetName;
	private GoogleSpreadsheetHandler gsheet;
	private List<List<Object>> values;
	private Logger butlog;
	int i;
	String butler_system_spreadsheetId;
	//private List<Scrapper> scrap;
	private Scrapper tempscrapper;
	private String forBOSheetFirstRowWithDataExceptHeader;
	private OtherPropertiesLoader property=OtherPropertiesLoader.initialize();
	
	public YahooFinanceScrapper()
	{
		this.forBOSheetName=this.property.getPropertyValue("forBOSheetName");
		this.i=0;
		//this.j=0;
		this.forBOSheetSizeDeterminingRange=this.property.getPropertyValue("forBOSheetSizeDeterminingRange");
		this.butlog=ButlerLogger.getButlerLogger(this.getClass().getName());
		this.gsheet=new GoogleSpreadsheetHandler();
		this.tempscrapper=null;
		this.butler_system_spreadsheetId=this.property.getPropertyValue("spreadsheetId");
		this.forBOSheetFirstRowWithDataExceptHeader=this.property.getPropertyValue("forBOSheetFirstRowWithDataExceptHeader");
		//this.scrap=new ArrayList<Scrapper>();
	}
	public void close()
	{
		this.forBOSheetName=null;
		this.forBOSheetSizeDeterminingRange=null;
		//this.scrap=null;
		this.values=null;
		this.gsheet.close();
		this.gsheet=null;
		this.i=0;
		//this.j=0;
		this.tempscrapper=null;
		this.forBOSheetFirstRowWithDataExceptHeader=null;
		this.butlog=null;
		System.gc();
		Runtime.getRuntime().gc();
		System.exit(0);
	}
	
	public void scrapFromYahooFinance()
	{
		try
		{
			String range = this.forBOSheetName+"!"+this.forBOSheetSizeDeterminingRange;
			int sheetRange;
			
			this.values=this.gsheet.readSheetForPendingPositionHandler(this.butler_system_spreadsheetId, range);
			int rowSizeOfForBOSheet = this.values.size();
			
			//this.butlog.warn(rowSizeOfForBOSheet);
			//this.butlog.warn(this.values.toString());
			//this.butlog.info("UPDATED"+"->"+"SYMBOL"+"\t\t"+"LTP"+"\t\t"+"OPEN"+"\t\t"+"LOW"+"-"+"HIGH");
			this.butlog.info(String.format("%10s  ->%15s%10s%10s%10s%10s%10s","UPDATED","SYMBOL","LTP","OPEN","LOW","HIGH","BUY/SELL")+"\t"+"HASH CODE");
			for(this.i=Integer.parseInt(this.forBOSheetFirstRowWithDataExceptHeader)-1; this.i<=rowSizeOfForBOSheet-1; this.i++)
			{
				try
				{
					this.tempscrapper=new Scrapper();
					this.tempscrapper.setSymbol(this.values.get(this.i).get(0).toString());
					//this.scrap.add(tempscrapper);
					//tempscrapper=null; //1
					List<ValueRange> yahooFinanceData=new ArrayList<>();
					this.tempscrapper.fetchAndSetData();
					sheetRange=this.i+1;
					yahooFinanceData.add(new ValueRange().setRange(this.forBOSheetName+"!A"+sheetRange).setValues(Arrays.asList(Arrays.asList(this.tempscrapper.getSymbol(),this.tempscrapper.getLtp(),this.tempscrapper.getOpen(),this.tempscrapper.getHigh(),this.tempscrapper.getLow()))));
					this.gsheet.updatePendingPositionHandlerSheet(this.butler_system_spreadsheetId, yahooFinanceData);
					//this.butlog.info("UPDATED"+"->"+this.tempscrapper.getSymbol()+"\t\t"+this.tempscrapper.getLtp()+"\t\t"+this.tempscrapper.getOpen()+"\t\t"+this.tempscrapper.getLow()+"-"+this.tempscrapper.getHigh());
					String buyOrsell="";
					if(this.tempscrapper.getOpen()==this.tempscrapper.getLow())
					{
						buyOrsell="BUY";
					}
					else if(this.tempscrapper.getOpen()==this.tempscrapper.getHigh())
					{
						buyOrsell="SELL";
					}
					this.butlog.info(String.format("%10s  ->%15s%10s%10s%10s%10s%10s","UPDATED",this.tempscrapper.getSymbol(),this.tempscrapper.getLtp(),this.tempscrapper.getOpen(),this.tempscrapper.getLow(),this.tempscrapper.getHigh(),buyOrsell)+"\t"+yahooFinanceData.hashCode());
					yahooFinanceData=null;
					//this.butlog.warn(i+"\t"+this.values.get(i).get(0).toString());
				}
				catch (Exception e)
				{
					this.butlog.error("Error/Exception in method: scrapFromYahooFinance() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
					this.butlog.warn("Since Exception Occured in Fetching "+this.tempscrapper.getSymbol()+". Hence placing GOOGLEFINANCE instead.");
					List<ValueRange> googleFinanceData=new ArrayList<>();
					sheetRange=this.i+1;
					googleFinanceData.add(new ValueRange().setRange(this.forBOSheetName+"!A"+sheetRange).setValues(Arrays.asList(Arrays.asList(this.tempscrapper.getSymbol(),"=GOOGLEFINANCE(\"NSE:\""+"&A"+sheetRange+",\"price\")","=GOOGLEFINANCE(\"NSE:\""+"&A"+sheetRange+",\"priceopen\")","=GOOGLEFINANCE(\"NSE:\""+"&A"+sheetRange+",\"high\")","=GOOGLEFINANCE(\"NSE:\""+"&A"+sheetRange+",\"low\")"))));
					this.gsheet.updatePendingPositionHandlerSheet(this.butler_system_spreadsheetId, googleFinanceData);
					googleFinanceData=null;
				}
				finally
				{
					this.tempscrapper.close();
					this.tempscrapper=null;
					System.gc();
					Runtime.getRuntime().gc();
					
				}
			}
			//this.butlog.warn("Symbol"+"\t"+"LTP"+"\t"+"Open"+"\t"+"Low-High");this.butlog.warn("Symbol"+"\t"+"LTP"+"\t"+"Open"+"\t"+"Low-High");
			/*for(this.j=0; this.j<this.scrap.size(); this.j++)
			{
				try
				{
					List<ValueRange> yahooFinanceData=new ArrayList<>();
					this.scrap.get(this.j).fetchAndSetData();
					sheetRange=Integer.parseInt(this.forBOSheetFirstRowWithDataExceptHeader)+this.j;
					yahooFinanceData.add(new ValueRange().setRange(this.forBOSheetName+"!A"+sheetRange).setValues(Arrays.asList(Arrays.asList(this.scrap.get(this.j).getSymbol(),this.scrap.get(this.j).getLtp(),this.scrap.get(this.j).getOpen(),this.scrap.get(this.j).getHigh(),this.scrap.get(this.j).getLow()))));
					//this.butlog.warn(yahooFinanceData.toString());
					this.gsheet.updatePendingPositionHandlerSheet(this.butler_system_spreadsheetId, yahooFinanceData);
					//this.butlog.warn(this.scrap.get(i).getSymbol()+"\t"+this.scrap.get(i).getLtp()+"\t"+this.scrap.get(i).getOpen()+"\t"+this.scrap.get(i).getLow()+"-"+this.scrap.get(i).getHigh());
					yahooFinanceData=null;
					this.scrap.get(this.j).close();//3
					System.gc();//4
				}
				catch(Exception e)
				{
					this.butlog.warn("i = "+this.i+" j = "+this.j);
					this.butlog.warn("Symbol(j) = "+this.scrap.get(this.j).getSymbol());
					this.butlog.warn("Symbol"+"\t"+"LTP"+"\t"+"Open"+"\t"+"Low-High");this.butlog.warn("Symbol"+"\t"+"LTP"+"\t"+"Open"+"\t"+"Low-High");
					//this.butlog.warn(this.scrap.get(i).getSymbol()+"\t"+this.scrap.get(i).getLtp()+"\t"+this.scrap.get(i).getOpen()+"\t"+this.scrap.get(i).getLow()+"-"+this.scrap.get(i).getHigh());
					this.butlog.warn(this.scrap.get(this.j).getSymbol()+"\t"+this.scrap.get(this.j).getLtp()+"\t"+this.scrap.get(this.j).getOpen()+"\t"+this.scrap.get(this.j).getLow()+"-"+this.scrap.get(this.j).getHigh());
					this.butlog.error("Error/Exception in method: scrapFromYahooFinance() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
					this.butlog.warn("Since Exception Occured in Fetching "+this.scrap.get(this.j).getSymbol()+". Hence placing GOOGLEFINANCE instead.");
					List<ValueRange> googleFinanceData=new ArrayList<>();
					sheetRange=Integer.parseInt(this.forBOSheetFirstRowWithDataExceptHeader)+this.j;
					googleFinanceData.add(new ValueRange().setRange(this.forBOSheetName+"!A"+sheetRange).setValues(Arrays.asList(Arrays.asList(this.scrap.get(this.j).getSymbol(),"=GOOGLEFINANCE(\"NSE:\""+"&A"+sheetRange+",\"price\")","=GOOGLEFINANCE(\"NSE:\""+"&A"+sheetRange+",\"priceopen\")","=GOOGLEFINANCE(\"NSE:\""+"&A"+sheetRange+",\"high\")","=GOOGLEFINANCE(\"NSE:\""+"&A"+sheetRange+",\"low\")"))));
					//this.butlog.warn(yahooFinanceData.toString());
					this.gsheet.updatePendingPositionHandlerSheet(this.butler_system_spreadsheetId, googleFinanceData);
					googleFinanceData=null;
					this.scrap.get(this.j).close(); //5
					System.gc(); //6
					
					//System.exit(-1);
				}	
			}*/	
		}
		catch(Exception e)
		{
			//this.butlog.warn("i = "+this.i+" j = "+this.j);
			//this.butlog.warn("Symbol(j) = "+this.scrap.get(this.j).getSymbol());
			//this.butlog.warn("Symbol"+"\t"+"LTP"+"\t"+"Open"+"\t"+"Low-High");this.butlog.warn("Symbol"+"\t"+"LTP"+"\t"+"Open"+"\t"+"Low-High");
			//this.butlog.warn(this.scrap.get(i).getSymbol()+"\t"+this.scrap.get(i).getLtp()+"\t"+this.scrap.get(i).getOpen()+"\t"+this.scrap.get(i).getLow()+"-"+this.scrap.get(i).getHigh());
			//this.butlog.warn(this.scrap.get(this.j).getSymbol()+"\t"+this.scrap.get(this.j).getLtp()+"\t"+this.scrap.get(this.j).getOpen()+"\t"+this.scrap.get(this.j).getLow()+"-"+this.scrap.get(this.j).getHigh());
			this.butlog.error("Error/Exception in method: scrapFromYahooFinance() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			
			//System.gc();
			//System.exit(-1);
		}
	}
	
	public static void main(String args[])
	{
		YahooFinanceScrapper yahooscrapper=new YahooFinanceScrapper();
		yahooscrapper.scrapFromYahooFinance();
		yahooscrapper.close();
	}
}
