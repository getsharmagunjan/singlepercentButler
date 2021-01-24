package openhighlowstrategy;

import butlerforfyers.ButlerLogger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.apache.logging.log4j.Level;
//import org.apache.logging.log4j.Logger;
//import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butlerforfyers.OtherPropertiesLoader;

public class JSONDataFetcher implements AutoCloseable 
{
	private Logger butlog;
	//private OkHttpClient client;
	//private Request request;
	//private Response response;
	private String nifty50_old_url;
	private String nifty50_new_url;
	private String nifty100_old_url;
	private String nifty100_new_url;
	private String nifty50_old_json_data_file_path;
	private String nifty50_new_json_data_file_path;
	private String nifty100_old_json_data_file_path;
	private String nifty100_new_json_data_file_path;
	private String nifty_old_host;
	private String nifty_new_host;
	private String nifty_data;
	private List<String> nifty_urls;
	private List<String> nifty_files;
	private List<String> nifty_hosts;
	private OtherPropertiesLoader property=OtherPropertiesLoader.initialize();
	
	public JSONDataFetcher()
	{
		//System.out.println("here");
		//this.butlog=ButlerLogger.getButlerLogger(this.getClass().getName());
		//this.butlog.getLevel();
		this.butlog=ButlerLogger.getButlerLogger();
		//System.out.println("now here"+this.butlog.toString()+"\n"+this.butlog.getLevel()+"\n"+this.butlog.isDebugEnabled());
		//this.butlog.setLevel(Level.DEBUG);
		//System.out.println("now here"+this.butlog.toString()+"\n"+this.butlog.getLevel());
		//this.request=null;
		//this.response=null;
		
		this.nifty50_old_url=this.property.getPropertyValue("nifty50_old_url");
		this.nifty50_new_url=this.property.getPropertyValue("nifty50_new_url");
		this.nifty100_old_url=this.property.getPropertyValue("nifty100_old_url");
		this.nifty100_new_url=this.property.getPropertyValue("nifty100_new_url");
		this.nifty50_old_json_data_file_path=this.property.getPropertyValue("nifty50_old_json_data_file_path");
		this.nifty50_new_json_data_file_path=this.property.getPropertyValue("nifty50_new_json_data_file_path");
		this.nifty100_old_json_data_file_path=this.property.getPropertyValue("nifty100_old_json_data_file_path");
		this.nifty100_new_json_data_file_path=this.property.getPropertyValue("nifty100_new_json_data_file_path");
		this.nifty_old_host=this.property.getPropertyValue("nifty_old_host");
		this.nifty_new_host=this.property.getPropertyValue("nifty_new_host");
		this.nifty_data=null;
		this.nifty_urls=new ArrayList<String>();
		this.nifty_files=new ArrayList<String>();
		this.nifty_hosts=new ArrayList<String>();
	}	
	private void niftyUrlsFilesHostsSetter() throws Exception
	{
		this.nifty_urls.add(0, this.nifty50_old_url);
		this.nifty_urls.add(1, this.nifty50_new_url);
		this.nifty_urls.add(2, this.nifty100_old_url);
		this.nifty_urls.add(3,this.nifty100_new_url);
		this.nifty_files.add(0,this.nifty50_old_json_data_file_path);
		this.nifty_files.add(1, this.nifty50_new_json_data_file_path);
		this.nifty_files.add(2,this.nifty100_old_json_data_file_path);
		this.nifty_files.add(3, this.nifty100_new_json_data_file_path);
		this.nifty_hosts.add(0,this.nifty_old_host);
		this.nifty_hosts.add(1, this.nifty_new_host);
		this.nifty_hosts.add(2, this.nifty_old_host);
		this.nifty_hosts.add(3, this.nifty_new_host);
	}
	public void close()
	{
		this.nifty_data=null;
		System.gc();
	}
	public void fetchJSONData()
	{
		try
		{
			//System.out.println("in fetchJSONData");
			this.niftyUrlsFilesHostsSetter();
			for(int i=0; i<this.nifty_urls.size(); i++)
			{
				this.butlog.info("Round: "+(i+1));
				this.fetchJSONDataPrivately(this.nifty_urls.get(i), this.nifty_files.get(i), this.nifty_hosts.get(i));
			}
			
		}
		catch (Exception e)
		{
			this.butlog.error("error/exception in fetching JSON data privately"+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			this.close();
			System.exit(-1);
		}
		finally
		{
			this.butlog.info("Closing Resources");
			this.close();
		}
	}
	private void fetchJSONDataPrivately(String url, String file, String host) throws Exception
	{
		//OkHttpClient client=new OkHttpClient.Builder().retryOnConnectionFailure(true).build();
		OkHttpClient client=new OkHttpClient.Builder()
							.connectTimeout(2, TimeUnit.MINUTES)
							.writeTimeout(2, TimeUnit.MINUTES)
							.readTimeout(2, TimeUnit.MINUTES)
							.retryOnConnectionFailure(true).build();
		this.butlog.info("Going to fetch for:\n"+"URL: "+url+"\nFile: "+file+"\n Host: "+host);
		Request request=new Request.Builder()
				  .url(url)
				  .method("GET", null)
				 // .header("Host", host) //removed host, if required, add it again, commenting out code related to host.
				  .header("Connection", "Keep-Alive")
				  .header("User-Agent", "PostmanRuntime/7.24.1") //NSE checks User-Agent, so if you change/remove it will not work. if you want to change, change to "Tasker/5.9.3.beta.4 (Android/9)" or "PostmanRuntime/7.24.1" never use chrome, safari etc, as they know from where you are surfing.
				  .build(); 
		Response response=client.newCall(request).execute();
		this.nifty_data=response.body().string();
		this.butlog.info("Fetch complete, now storing");
		Files.write(Paths.get(file), this.nifty_data.getBytes());
		this.butlog.info("Store Complete");
		client=null;
		request=null;
		response=null;
		System.gc();
	}
	
	/*public static void main(String args[])
	{
		JSONDataFetcher obj=new JSONDataFetcher();
		//System.out.println("Hello");
		obj.fetchJSONData();
		obj.close();
	}*/
}
