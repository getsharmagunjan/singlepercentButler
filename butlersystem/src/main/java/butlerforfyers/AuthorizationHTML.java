package butlerforfyers;

import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.apache.logging.log4j.Logger;
import org.apache.log4j.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.Request;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AuthorizationHTML 
{
	private String generate_token_req_url;
	private String app_id;
	private String user_id;
	private String user_password;
	private String user_pan_card_no;
	private String authorization_html_path;
	private String html_response_code;
	private String mod_req_url;
	private Logger butlog=null;
	private OkHttpClient client=null; 
	private DBConnection connection=null;
	private String auth_code;
	private Response response;
	private Request request;
	private OtherPropertiesLoader property=OtherPropertiesLoader.initialize();
	
	public AuthorizationHTML()
	{
		this.generate_token_req_url=this.property.getPropertyValue("generate_token_req_url");
		this.app_id=this.property.getPropertyValue("app_id");
		this.user_id=this.property.getPropertyValue("user_id");
		this.user_password=this.property.getPropertyValue("user_password");
		this.user_pan_card_no=this.property.getPropertyValue("user_pan_card_no");
		this.authorization_html_path=this.property.getPropertyValue("authorization_html_path");
		
		this.butlog=ButlerLogger.getButlerLogger(this.getClass().getName());
		this.client =new OkHttpClient();
		this.connection=new DBConnection();
		this.auth_code=null;
		this.request=null;
		this.response=null;
		this.mod_req_url=null;
		this.html_response_code=null;
	}
	
	private void getAuthCodefrmDB()
	{
		this.auth_code=this.connection.getAuthCode();
		butlog.info("Authorization code retrived.");
	}
	
	private void setModReqUrl()
	{
		this.getAuthCodefrmDB();
		this.mod_req_url=this.generate_token_req_url+"?"+"authorization_code="+this.auth_code+"&"+"appId="+this.app_id;
		//butlog.info("modified req url = "+this.mod_req_url);
	}
	
	private void writeHTMLCodeToFile()
	{
		try
		{
			this.butlog.info("Going to write html code to file.");
			Files.write(Paths.get(this.authorization_html_path), this.html_response_code.getBytes());
			this.butlog.info("Successfully wrote html code to file");
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method: writeHTMLCodeToFile() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}
	
	private void modifyHTMLResponse()
	{
		try 
		{	
			this.butlog.info("Going to modify html response code");
			this.html_response_code=this.html_response_code.replaceAll("<body>", "<body onload=btn_id.click()>");
			this.html_response_code=this.html_response_code.replaceAll("id=\"fyers_id\" name=\"fyers_id\" type=\"text\"", "id=\"fyers_id\" name=\"fyers_id\" type=\"text\" value=\""+this.user_id+"\"");
			this.html_response_code=this.html_response_code.replaceAll("id=\"password\" name=\"password\" type=\"password\"", "id=\"password\" name=\"password\" type=\"password\" value=\""+this.user_password+"\"");
			this.html_response_code=this.html_response_code.replaceAll("name=\"pancard\" id=\"pancard\" type=\"text\"", "name=\"pancard\" id=\"pancard\" type=\"text\" value=\""+this.user_pan_card_no+"\"");
			this.butlog.info("Successfully modified html response code");
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method: modifyHTMLResponse() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}
	
	public void getAuthorizationHTML()
	{
		try
		{
			this.setModReqUrl();
			this.request=new Request.Builder().url(this.mod_req_url).get().build();
			this.response=this.client.newCall(this.request).execute();
			this.html_response_code=this.response.body().string();
			this.butlog.info("Successfully received html code in response.");
			this.modifyHTMLResponse();
			this.writeHTMLCodeToFile();
		}
		catch (Exception e)
		{
			butlog.error("Error/Exception in method: getAuthorizationHTML() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
		finally
		{
			this.butlog=null;
			this.client=null;
			this.connection=null;
			this.auth_code=null;
			this.request=null;
			this.response=null;
			this.mod_req_url=null;
			this.html_response_code=null;
		}
	}
}
