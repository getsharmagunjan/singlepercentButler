package ButlerToken;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class RESTConnectionHandler implements AutoCloseable
{
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	
	private String Authorization=null;
	private String request_payload_in_string=null;
	private String request_url;
	private int response_payload_response_code;
	private String response_payload_in_string;
	private String header_authorization_text;
	private DBConnection connection;
	private Logger butlog;
	private RequestBody requestbody;
	private Request request;
	private Response response;
	private OkHttpClient client;
	private OtherPropertiesLoader property=OtherPropertiesLoader.initialize();
	
	RESTConnectionHandler()
	{
		this.butlog=ButlerLogger.getButlerLogger(this.getClass().getName());
		this.client=new OkHttpClient();
		this.request_url=null;
		this.requestbody=null;
		this.request=null;
		this.response=null;
		this.response_payload_response_code=0;
		this.response_payload_in_string=null;
		this.connection=new DBConnection();
		this.header_authorization_text=this.property.getPropertyValue("header_authorization_text");
		if (this.Authorization==null)
		{
			this.getAuthorization();
		}
		this.connection=null;
		
	}
	@Override
	public void close()
	{
		this.butlog.info("Closing this RESTConnection Resources");
		this.client=null;
		this.request_url=null;
		this.requestbody=null;
		this.request=null;
		this.response=null;
		this.response_payload_in_string=null;
		this.response_payload_response_code=0;
		this.connection=null;
		System.gc();
	}
	private void getAuthorization()
	{
		try
		{
			this.Authorization=this.connection.getLastAuthAccessToken();
		}
		catch (Exception e)
		{
			this.butlog.error("Error/Exception in method:getAuthorization() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}
	public List<String> sendRequest(String request_url, String request_payload_in_string)
	{
		List<String> list_of_string = new ArrayList<String>();
		try
		{
			this.request_url=request_url;
			this.request_payload_in_string=request_payload_in_string;
			this.butlog.info("Sending request for RESTful Connection.");
			this.requestbody=RequestBody.create(JSON, this.request_payload_in_string);
			this.request=new Request.Builder().header(this.header_authorization_text, this.Authorization).url(this.request_url).post(this.requestbody).build();
			this.response=this.client.newCall(this.request).execute();
			this.response_payload_response_code=this.response.code();
			//this.butlog.warn("check response_payload_response_code ="+this.response_payload_response_code);
			this.response_payload_in_string=this.response.body().string();
			this.butlog.warn("CHECK THIS\nCheck response_payload_in_string ="+this.response_payload_in_string);
			//this.butlog.warn("Integer.toString(this.response_payload_response_code) ="+Integer.toString(this.response_payload_response_code));
			list_of_string.add(Integer.toString(this.response_payload_response_code));
			list_of_string.add(this.response_payload_in_string);
			this.butlog.warn("code in rest connection class = "+list_of_string.get(0)+" message in rest connection class = "+list_of_string.get(1));
			this.butlog.info("Request sent and response returned.");
			//this.close();
			return list_of_string;
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method: sendRequest(String request_url, String request_payload_in_string) in "+this.getClass().toString()+" is =\n"+ExceptionUtils.getStackTrace(e));
			this.close();
			System.exit(-1);
			return null;
		}
	}
	public List<String> modifyOrder(String request_url, String request_payload_in_string)
	{
		List<String> list_of_string = new ArrayList<String>();
		try
		{
			this.request_url=request_url;
			this.request_payload_in_string=request_payload_in_string;
			this.butlog.info("Sending request for RESTful Connection.");
			this.requestbody=RequestBody.create(JSON, this.request_payload_in_string);
			this.request=new Request.Builder().header(this.header_authorization_text, this.Authorization).url(this.request_url).put(this.requestbody).build();
			this.response=this.client.newCall(this.request).execute();
			this.response_payload_response_code=this.response.code();
			//this.butlog.warn("check response_payload_response_code ="+this.response_payload_response_code);
			this.response_payload_in_string=this.response.body().string();
			this.butlog.warn("CHECK THIS\nCheck response_payload_in_string ="+this.response_payload_in_string);
			//this.butlog.warn("Integer.toString(this.response_payload_response_code) ="+Integer.toString(this.response_payload_response_code));
			list_of_string.add(Integer.toString(this.response_payload_response_code));
			list_of_string.add(this.response_payload_in_string);
			this.butlog.warn("code in rest connection class = "+list_of_string.get(0)+" message in rest connection class = "+list_of_string.get(1));
			this.butlog.info("Request sent and response returned.");
			//this.close();
			return list_of_string;
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method: modifyOrder(String request_url, String request_payload_in_string) in "+this.getClass().toString()+" is =\n"+ExceptionUtils.getStackTrace(e));
			this.close();
			System.exit(-1);
			return null;
		}
	}
	public Response sendRequest(String request_url)
	{
		try
		{
			
			this.request_url=request_url;
			this.request=new Request.Builder().header(this.header_authorization_text, this.Authorization).url(this.request_url).get().build();
			this.response=this.client.newCall(this.request).execute();
			return this.response;
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method: sendRequest(String request_url) in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
			return null;
		}
	}
	
	
	
}
