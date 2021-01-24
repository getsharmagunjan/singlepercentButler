package ButlerToken;

//import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.sql.PreparedStatement;
import java.sql.Types;
//import java.util.Calendar;

import java.text.SimpleDateFormat;

import org.apache.commons.lang3.exception.ExceptionUtils;
//import okhttp3.ResponseBody;
import org.apache.logging.log4j.Logger;
//import javax.json.stream.JsonParser;
//import javax.json.Json;
//import java.io.StringReader;
//import javax.json.stream.JsonParser.Event;

public class ButlerAuthenticator {

	private String authentication_req_url;
	private String app_id;
	private String secret_key;
	private Logger butlog;
		
	private static final char ch='"';
	private static final String authorization_code="authorization_code";
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	
	private OtherPropertiesLoader property=OtherPropertiesLoader.initialize();
	private PreparedStatement prepstmt;
	DBConnection dbconnection;
	OkHttpClient client;
	SimpleDateFormat sdf;
	//private String currentdatetime;
	//private Calendar cal = Calendar.getInstance();
	
	ButlerAuthenticator()
	{
		this.authentication_req_url=this.property.getPropertyValue("authentication_req_url");
		this.app_id=this.property.getPropertyValue("app_id");
		this.secret_key=this.property.getPropertyValue("secret_key");
		this.client=new OkHttpClient();
		this.butlog=ButlerLogger.getButlerLogger(this.getClass().getName());
		this.dbconnection=new DBConnection();
		this.sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//this.currentdatetime=null;
		this.prepstmt=null;
	}
	
	private String getJSONBodyParameters(String app_id, String secret_key)
	{
		String local;
		local = "{"
					+ch+"app_id"+ch+":"+ch+this.app_id+ch+","
					+ch+"secret_key"+ch+":"+ch+this.secret_key+ch+
				"}";
		//butlog.info("in getJSONBodyParameters : "+local);
		return local;
	}
	
	private RequestBody getJSONBody(String json)
	{
		RequestBody bd = RequestBody.create(JSON, json);
		//butlog.info("in getJSONBody : "+bd.toString());
		return bd;
	}
	
	public void authenticate()
	{
		String s=null;
		try
		{	
			String responsestring=null;
			ButlerResponseHandler brh=new ButlerResponseHandler();
			RequestBody body=this.getJSONBody(this.getJSONBodyParameters(app_id, secret_key));
			butlog.info("JSON body created.");
			Request request = new Request.Builder().url(this.authentication_req_url).post(body).build();
			butlog.info("Request body created.");
			Response response = client.newCall(request).execute();
			responsestring=response.body().string();
			butlog.info("Reponse received for Authentication.");
			s=brh.getResponseAttribute(responsestring, authorization_code);
			butlog.info("Response parsed.");
			prepstmt = dbconnection.getPreparedStatementforAuthInsert();
			//currentdatetime=sdf.format(date);
			prepstmt.setString(1,"Butler");
			//prepstmt.setString(2,"Now()");
			//java.sql.Date sqldate=new java.sql.Date(new java.util.Date().getTime());
			//java.sql.Date sqldate=java.sql.Date.valueOf(java.time.LocalDateTime.now().toLocalDate());
			java.sql.Timestamp sqlTS = new java.sql.Timestamp(new java.util.Date().getTime());
			//java.sql.Date caldate=new java.sql.Date(cal.getTime().getTime());
			prepstmt.setTimestamp(2, sqlTS);
			prepstmt.setString(3, "Butler");
			prepstmt.setTimestamp(4, sqlTS);
			prepstmt.setString(5, s);
			prepstmt.setNull(6, Types.NULL);
			prepstmt.setNull(7, Types.NULL);
			prepstmt.setNull(8, Types.NULL);
			prepstmt.setNull(9, Types.NULL);
			butlog.info("Statement Created");
			//boolean auth_result = prepstmt.execute();
			int auth_result = prepstmt.executeUpdate();
			prepstmt.close();
			if (auth_result==1)
			{
				butlog.info("DB insertion successful");
			}
			else { butlog.info("DB insertion Failed"); }
		}
		catch(Exception e)
		{
			butlog.error("Error/Exception in method: authenticate() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
		finally
		{
			butlog.info("Finally, Closing Authenticator Resources.");
			this.prepstmt=null;
		}
	}
}
