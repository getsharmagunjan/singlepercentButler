package butlerforfyers;
//http://localhost:8080/authorization/authorization.html



import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.apache.logging.log4j.Logger;
import org.apache.log4j.Logger;
/*import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader; */
import java.util.concurrent.TimeUnit;
import java.io.File;
//import java.net.URI;
//import java.net.URI;
//import java.net.URL;
import java.awt.Desktop;

public class HTMLRunning 
{
	//private final String host_name="http://localhost";
	//private final String host_port="8080";
	//private final String url_path_after_host_name="/authorization/authorization.html";
	//private String redirected_url;
	private String authorization_html_path;
	//private String final_url=null;
	private Logger butlog=null;
	//private URL url=null; 
	//HttpURLConnection connection=null;
	File newfile=null;
	private OtherPropertiesLoader property=OtherPropertiesLoader.initialize();
	
	HTMLRunning()
	{
		this.butlog=ButlerLogger.getButlerLogger(this.getClass().getName());
		this.authorization_html_path=this.property.getPropertyValue("authorization_html_path");
		//this.redirected_url=null;
	}
	
	/*private void setFinalURL()
	{
		this.final_url=this.host_name+":"+this.host_port+this.url_path_after_host_name;
	}*/
	
	public void getHTMLRunning()
	{
		try
		{
			//this.setFinalURL();
			//this.butlog.info("Final URL set done which is ="+this.final_url);
			/*
			this.url=new URL(this.final_url);
			this.connection= (HttpURLConnection) this.url.openConnection();
			this.butlog.info("URL connection open with response code ="+this.connection.getResponseCode()+ " and response message ="+this.connection.getResponseMessage());
			//this.connection.setReadTimeout(5000);
			this.butlog.info("Read timeout set for 5 seconds and remaining are ="+this.connection.getReadTimeout()+" milliseconds.");
			this.redirected_url=this.connection.getURL().toString();
			this.butlog.info("The redirected Url is ="+this.redirected_url+"\nResponse Code is ="+this.connection.getResponseCode()); */
			this.newfile=new File(this.authorization_html_path);
			this.butlog.info("going to sleep for 10 seconds");
			TimeUnit.SECONDS.sleep(10);
			//this.butlog.info("I just woke up");
			//URI uri=new URL(this.authorization_html_path).toURI();
			Desktop desktop=Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			this.butlog.warn("is desktop supported = "+Desktop.isDesktopSupported());
			if (desktop!=null && desktop.isSupported(Desktop.Action.BROWSE))
			{
				this.butlog.info("Running using Desktop.");
				//desktop.browse(uri);
				desktop.browse(this.newfile.toURI());
			}
			//else
			//{
				
			/*this.butlog.info("Running using Runtime.");
			this.butlog.info("method one");
			Runtime.getRuntime().exec("cmd /c start "+this.authorization_html_path);
			Runtime rt=Runtime.getRuntime();
			this.butlog.info("method two");
			rt.exec("rundll32 url.dll,FileProtocolHandler "+this.authorization_html_path);*/
			//}
			this.butlog.info("Successfully opened Authorization HTML in browser. Waiting for 1 minute before closing the browser.");
			TimeUnit.MINUTES.sleep(1);
			Runtime rt = Runtime.getRuntime();
			rt.exec("taskkill /F /IM brave.exe");
			this.butlog.info("Browser Closed");
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method: getHTMLRunning() in "+this.getClass().toString()+" is =\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
		finally
		{
			this.butlog=null;
			this.authorization_html_path=null;
		}
	}
	
	public static void main(String args[])
	{
		HTMLRunning htmlrunning = new HTMLRunning();
		htmlrunning.getHTMLRunning();
	}
}
