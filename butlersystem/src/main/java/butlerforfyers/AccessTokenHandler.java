package butlerforfyers;

import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.apache.logging.log4j.Logger;
import org.apache.log4j.Logger;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Scanner;

public class AccessTokenHandler 
{
	private String token_file_location; 
	private String access_token;
	private String last_auth_code;
	private OtherPropertiesLoader property=OtherPropertiesLoader.initialize();
	
	private Scanner scanner;
	private Path path;
	private Logger butlog=null;
	private DBConnection dbconnection;
	private PreparedStatement prepstmt;
	
	AccessTokenHandler()
	{
		this.token_file_location=this.property.getPropertyValue("token_file_location");
		this.access_token=null;
		this.last_auth_code=null;
		this.butlog=ButlerLogger.getButlerLogger(this.getClass().getName());
		this.path=null;
		this.scanner=null;
		this.dbconnection=new DBConnection();
		this.prepstmt=null;
	}
	
	private void readAccessToken()
	{
		try
		{
			this.butlog.info("Reading from file");
			this.path=Paths.get(this.token_file_location);
			this.scanner=new Scanner(this.path);
			while(this.scanner.hasNextLine())
			{
				this.access_token=this.scanner.nextLine();
			}
			this.butlog.info("Read access token successfully.");
		}
		catch (Exception e)
		{
			this.butlog.error("Error/Exception in method: readAccessToken() of "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
		finally
		{
			this.scanner.close();
			this.path=null;
		}	
	}
	
	private void setNullTokenAfterSuccessfulRead()
	{
		String empty_value_string="empty";
		try
		{
			if(this.access_token.equals("empty"))
			{
				this.butlog.warn("No need to set empty to file as it is already empty");
			}
			else
			{
				Files.write(Paths.get(this.token_file_location),empty_value_string.getBytes());
				this.butlog.info("Empty token set after reading token.");
			}
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method:setNullTokenAfterSuccessfulRead() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
		finally
		{
			this.token_file_location=null;
		}
	}
	
	private void getLastAuthCode()
	{
		try
		{
			this.last_auth_code=dbconnection.getAuthCode();
			this.butlog.info("Got last authorization code");
		}
		catch (Exception e)
		{
			this.butlog.error("Error/Exception in method: getLastAuthCode()"+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}
	
	private void updateTokenValue()
	{
		try
		{
			//this.butlog.info("Access-token is :"+this.access_token);
			if(this.access_token.equals("empty"))
			{
				this.butlog.warn("This file does not contain any token (empty file). Not updating token value.");
			}
			else
			{
				this.prepstmt=this.dbconnection.getPreparedStatementforAuthUpdateAccessToken();
				java.sql.Timestamp sqlTS = new java.sql.Timestamp(new java.util.Date().getTime());
				this.butlog.info("Received PreparedStatement Object");
				this.prepstmt.setString(1, this.access_token);
				this.prepstmt.setTimestamp(2, sqlTS);
				this.prepstmt.setString(3, this.last_auth_code);
				this.butlog.info("Modified PreparedStatement Object");
				int chkcounter=this.prepstmt.executeUpdate();
				if(chkcounter==1)
				{
					this.butlog.info("Successfully updated token");
				}
				else
				{
					this.butlog.error("Failed updating Token value to DB in method:updateTokenValue() in "+this.getClass().toString());
				}
			}
		}
		catch(Exception e)
		{
			this.butlog.error("Failed updating Token value to DB in method:updateTokenValue() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
	}
	
	public void handleAccessToken()
	{
		try
		{
			this.readAccessToken();
			this.setNullTokenAfterSuccessfulRead();
			this.getLastAuthCode();
			this.updateTokenValue();
		}
		catch (Exception e)
		{
			this.butlog.error("Error/Exception in method:handleAccessToken() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
		}
		finally
		{
			this.butlog.info("Garbage Collection");
			this.dbconnection=null;
			this.prepstmt=null;
			this.access_token=null;
			this.last_auth_code=null;
			this.butlog=null;
			this.path=null;
			this.scanner.close();
		}
	}
	
}

