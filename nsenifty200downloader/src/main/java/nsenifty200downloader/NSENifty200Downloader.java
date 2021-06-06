package nsenifty200downloader;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import butlerforfyers.ButlerLogger;
import butlerforfyers.OtherPropertiesLoader;
import spreadsheethandler.GoogleSpreadsheetHandler;
import org.jsoup.*;
import org.jsoup.Connection.Method;


public class NSENifty200Downloader implements AutoCloseable
{
	private Logger butlog;
	private Connection.Response response;
	private OtherPropertiesLoader property=OtherPropertiesLoader.initialize();
	String butler_system_spreadsheetId;
	private List<List<Object>> scriptsnames;
	private GoogleSpreadsheetHandler gsheet;
	private String forBOSheetFirstRowWithDataExceptHeader;
	private String forBOSheetSizeDeterminingRange;
	private String forBOSheetName;
	private String nseHomePageURL;
	private String nifty200JsonURL;
	private List<ScriptsDetails> scripts;
	
	public NSENifty200Downloader()
	{
		this.butlog=ButlerLogger.getButlerLogger(this.getClass().getName());
		this.gsheet=new GoogleSpreadsheetHandler();
		this.scriptsnames=null;
		this.response=null;
		this.forBOSheetName=this.property.getPropertyValue("forBOSheetName");
		this.forBOSheetSizeDeterminingRange=this.property.getPropertyValue("forBOSheetSizeDeterminingRange");
		this.butler_system_spreadsheetId=this.property.getPropertyValue("spreadsheetId");
		this.forBOSheetFirstRowWithDataExceptHeader=this.property.getPropertyValue("forBOSheetFirstRowWithDataExceptHeader");
		this.nseHomePageURL=this.property.getPropertyValue("nseHomePageURL");
		this.nifty200JsonURL=this.property.getPropertyValue("nifty200JsonURL");
		this.scripts=new ArrayList<ScriptsDetails>();
	}
	@Override
	public void close()
	{
		this.gsheet.close();
		this.forBOSheetFirstRowWithDataExceptHeader=null;
		this.forBOSheetName=null;
		this.response=null;
		this.forBOSheetSizeDeterminingRange=null;
		this.butler_system_spreadsheetId=null;
		this.scripts=null;
		this.butlog=null;
		this.scriptsnames=null;
		System.gc();
		Runtime.getRuntime().gc();
	}
	private void determineScriptNamesFromGSheet()
	{
		String range = this.forBOSheetName+"!"+this.forBOSheetSizeDeterminingRange;
		this.scriptsnames=this.gsheet.readSheetForPendingPositionHandler(this.butler_system_spreadsheetId, range);	
	}
	private void getNSECookies()
	{
		try
		{
			this.butlog.info("START");
			this.response=Jsoup.connect(this.nseHomePageURL)
								.ignoreContentType(true)
								.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36")
								.referrer("http://www.google.com")
								.timeout(10000)
								.followRedirects(true)
								.method(Method.GET)
								.execute();
			this.butlog.info("Cookies = /n"+this.response.cookies());
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method: getNSECookies() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			this.close();
			System.exit(0);
		}
	}
	public void runSequence()
	{
		this.getNSECookies();
	}
	public static void main(String args[])
	{
		NSENifty200Downloader object=new NSENifty200Downloader();
		object.runSequence();
		object.close();
	}
}
