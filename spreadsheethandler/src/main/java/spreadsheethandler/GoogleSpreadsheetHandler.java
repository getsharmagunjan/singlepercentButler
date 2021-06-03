package spreadsheethandler;

import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.apache.logging.log4j.Logger;
import org.apache.log4j.Logger;
import butlerforfyers.ButlerLogger;
import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import butlerforfyers.OtherPropertiesLoader;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;


public class GoogleSpreadsheetHandler implements AutoCloseable
{
	//private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
	private static List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);
    private static String CREDENTIALS_FILE_PATH;
    private List<ValueRange> data=new ArrayList<>();
	private Logger butlog=ButlerLogger.getButlerLogger();
	private static String APPLICATION_NAME;
    private static JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static String TOKENS_DIRECTORY_PATH;
    private static List<String> script_names_in_sheet=new ArrayList<String>();
    private BatchUpdateValuesRequest batchupdate;
    private BatchUpdateValuesResponse batchResult;
    private NetHttpTransport HTTP_TRANSPORT;
    private static String spreadsheetId;
    private static String range;
    private static String final_order_range;
    private static String fund_range;
    private Sheets sheetService;
    private OtherPropertiesLoader property=OtherPropertiesLoader.initialize();
    //private ValueRange reponse;
    
    public GoogleSpreadsheetHandler()
    {
    	try
    	{
    		this.batchResult=null;
    		this.butlog.info("Initiating Google SpreadSheet Handler Resources.");
    		CREDENTIALS_FILE_PATH=this.property.getPropertyValue("CREDENTIALS_FILE_PATH");
    		spreadsheetId=this.property.getPropertyValue("spreadsheetId");
    		range=this.property.getPropertyValue("range");
    		final_order_range=this.property.getPropertyValue("final_order_range");
    		APPLICATION_NAME=this.property.getPropertyValue("APPLICATION_NAME");
    		TOKENS_DIRECTORY_PATH=this.property.getPropertyValue("TOKENS_DIRECTORY_PATH");
    		fund_range=this.property.getPropertyValue("fund_range");
    		this.HTTP_TRANSPORT=GoogleNetHttpTransport.newTrustedTransport();
    		this.sheetService=new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
    	            			.setApplicationName(APPLICATION_NAME)
    	            			.build();
    		this.butlog.info("Google SpreadSheet Handler Resources Initiated.");
    	}
    	catch (Exception e)
    	{
    		this.butlog.error("Error/Exception in constructor: OpenHighLow_GoogleSpreadsheetHandler() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
    		System.exit(-1);
    	}
    }
    public void close()
    {
    	this.butlog.info("Closing SheetService");
    	this.sheetService=null;
    	this.butlog.info("Closed SheetService");
    	System.gc();
    }
    
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        //InputStream in = GoogleSpreadsheetHandler.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
        /*if (in==null) 
        {
        	throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }*/
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public void updateSheetValues(List<String> symbol, List<Double> ltp, List<Double> open, List<Double> high, List<Double> low)
    {
    	this.readSheet();
    	try
    	{
    		for(int i=0; i<script_names_in_sheet.size(); i++)
    		{
    			for (int j=0;j<symbol.size();j++)
    			{
    				if (symbol.get(j).equalsIgnoreCase(script_names_in_sheet.get(i)))
    				{
    					//this.butlog.warn("i="+i+" j="+j+" symbol="+symbol.get(j)+" script_names_in_sheet="+script_names_in_sheet.get(i));
    					this.data.add(new ValueRange().setRange("Only Nifty50!A"+(i+2)).setValues(Arrays.asList(Arrays.asList(symbol.get(j),ltp.get(j),open.get(j),high.get(j),low.get(j)))));
    				}
    			}
    		}
    		//this.butlog.warn(this.data.get(0));
    		//this.butlog.warn(this.data.get(1));
    		this.batchupdate=new BatchUpdateValuesRequest().setValueInputOption("RAW").setData(this.data);
    		this.batchResult=this.sheetService.spreadsheets().values().batchUpdate(spreadsheetId, this.batchupdate).execute();
    		if(!this.batchResult.isEmpty())
    		{
    			this.butlog.info("Updated Data to SpreadSheet Successfully.");
    		}
    		else
    		{
    			this.butlog.error("Data could not be updated to SpreadSheet. Need Manual Inspection.");
    		}
    	}
    	catch(Exception e)	
    	{
    		this.butlog.error("Error/Exception in method: updateSheetValues(List<String> symbol, List<Double> ltp, List<Double> open, List<Double> high, List<Double> low) in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
    		System.exit(-1);
    	}
    	finally
    	{
    		this.batchResult=null;
    	}
    }
    
    public void readSheet()
    {
    	try
    	{
    		ValueRange response = this.sheetService.spreadsheets().values()
            		.get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) 
            {
                //System.out.println("No data found.");
            	this.butlog.error("No data found in spreadsheet");
            } 
            else 
            {
               /* int i=0;
                for (List row : values) 
                {
                    // Print columns A and E, which correspond to indices 0 and 4.
                	script_names_in_sheet.add(i, row.get(0).toString());
                	//System.out.printf("%s\n",row);
                	i=i+1;
                } */
                for(int i=0; i<values.size(); i++)
                {
                	script_names_in_sheet.add(i, values.get(i).get(0).toString());
                }
                
            }
            this.butlog.info("Read Scripts from SpreadSheet Successfully.");
          
           /* for(int j=0;j<script_names_in_sheet.size();j++)
            {
            	this.butlog.warn("script_names("+j+","+script_names_in_sheet.get(j)+")");
            } */
            
    	}
    	catch(Exception e)
    	{
    		this.butlog.error("Error/Exception in method: readSheet() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
    		System.exit(-1);
    	}
    }
    
    public List<List<Object>> readSheetForPendingPositionHandler(String spreadsheetID, String range)
    {
    	List<List<Object>> values=null;
    	try
    	{
    		ValueRange response = this.sheetService.spreadsheets().values().get(spreadsheetID, range).execute();
    		values = response.getValues();
    		response=null;
    		return values;
    	}
    	catch (Exception e)
    	{
    		this.butlog.error("Error/Exception in method: readSheetForPendingPositionHandler(String spreadsheetID, String range) in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
    		return values;
    	}
    	
    }
    
    public int readCounterOfButlerBOPnLRecords(String spreadsheetId, String range)
    {
    	int counter=0;
    	try
    	{
    		ValueRange response = this.sheetService.spreadsheets().values()
            		.get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) 
            {
                //System.out.println("No data found.");
            	this.butlog.error("No data found in spreadsheet");
            } 
            else 
            {
                //int i=0;
                //for (List row : values) 
                //{
                    // Print columns A and E, which correspond to indices 0 and 4.
                	//script_names_in_sheet.add(i, row.get(0).toString());
                	//this.butlog.info("%s\n"+row+ "\t"+row.get(0).toString());
                	//i=i+1;
                //} 
                /*for(int i=0; i<values.size(); i++)
                {
                	script_names_in_sheet.add(i, values.get(i).get(0).toString());
                }*/
            	counter = Integer.parseInt(values.get(0).get(0).toString());                
            }
            this.butlog.info("readCounterOfButlerBOPnLRecords from SpreadSheet Successfully.");
            return counter;
          
           /* for(int j=0;j<script_names_in_sheet.size();j++)
            {
            	this.butlog.warn("script_names("+j+","+script_names_in_sheet.get(j)+")");
            } */
            
    	}
    	catch(Exception e)
    	{
    		this.butlog.error("Error/Exception in method: readCounterOfButlerBOPnLRecords(String spreadsheetId, String range) in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
    		System.exit(-1);
    		return counter;
    	}
    }
    public String readDecisionOfButlerBOPnLRecords(String spreadsheetId, String range)
    {
    	String decision=null;
    	try
    	{
    		ValueRange response = this.sheetService.spreadsheets().values()
            		.get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) 
            {
                //System.out.println("No data found.");
            	this.butlog.error("No data found in spreadsheet");
            } 
            else 
            {
                //int i=0;
                //for (List row : values) 
                //{
                    // Print columns A and E, which correspond to indices 0 and 4.
                	//script_names_in_sheet.add(i, row.get(0).toString());
                	//this.butlog.info("%s\n"+row+ "\t"+row.get(0).toString());
                	//i=i+1;
                //} 
                /*for(int i=0; i<values.size(); i++)
                {
                	script_names_in_sheet.add(i, values.get(i).get(0).toString());
                }*/
            	decision = values.get(0).get(0).toString();                
            }
            this.butlog.info("readDecisionOfButlerBOPnLRecords from SpreadSheet Successfully.");
            return decision;
          
           /* for(int j=0;j<script_names_in_sheet.size();j++)
            {
            	this.butlog.warn("script_names("+j+","+script_names_in_sheet.get(j)+")");
            } */
            
    	}
    	catch(Exception e)
    	{
    		this.butlog.error("Error/Exception in method: readDecisionOfButlerBOPnLRecords(String spreadsheetId, String range) in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
    		System.exit(-1);
    		return decision;
    	}
    }
    
    public String getFinalOrder()
    {
    	try
    	{
    		ValueRange response = this.sheetService.spreadsheets().values()
            		.get(spreadsheetId, final_order_range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) 
            {
                //System.out.println("No data found.");
            	this.butlog.error("No data found in spreadsheet");
            	return null;
            } 
            else 
            {
               //this.butlog.warn(values.get(0).get(0).toString());
            	this.butlog.info("Final Order Retrieved from SpreadSheet.");
            	return values.get(0).get(0).toString();
            }   
    	}
    	catch(Exception e)
    	{
    		this.butlog.error("Error/Exception in method: readSheet() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
    		System.exit(-1);
    		return null;
    	}
    }
    
    public void updateButlerBOPnLRecords(String spreadsheetId, String DateTimeRange, String totalPnLRange, String DateTime, double totalPnL, String counterRange, int counter)
    {
    	try
    	{
    		this.butlog.info("Going to update DateTime & totalPnL");
    		ValueRange body=new ValueRange().setValues(Arrays.asList(Arrays.asList(DateTime)));
    		UpdateValuesResponse result=this.sheetService.spreadsheets().values().update(spreadsheetId, DateTimeRange, body).setValueInputOption("RAW").execute();
    		if(!result.isEmpty())
    		{
    			this.butlog.info("DateTime Updated Successfully");
    		}
    		else
    		{
    			this.butlog.info("Some problem in updating DateTime. Manual intervention requested...");
    		}
    		body=new ValueRange().setValues(Arrays.asList(Arrays.asList(totalPnL)));
    		result=this.sheetService.spreadsheets().values().update(spreadsheetId, totalPnLRange, body).setValueInputOption("RAW").execute();
    		if(!result.isEmpty())
    		{
    			this.butlog.info("totalPnL Updated Successfully");
    		}
    		else
    		{
    			this.butlog.info("Some problem in updating totalPnL. Manual intervention requested...");
    		}
    		body=new ValueRange().setValues(Arrays.asList(Arrays.asList(counter)));
    		result=this.sheetService.spreadsheets().values().update(spreadsheetId, counterRange, body).setValueInputOption("RAW").execute();
    		if(!result.isEmpty())
    		{
    			this.butlog.info("counter Updated Successfully");
    		}
    		else
    		{
    			this.butlog.info("Some problem in updating counter. Manual intervention requested...");
    		}
    	}
    	catch (Exception e)
    	{
    		this.butlog.error("Error/Exception in method: updateButlerBOPnLRecords(String spreadsheetId, String DateTimeRange, String totalPnLRange, String DateTime, double totalPnL) in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
    		System.exit(-1);
    	}
    }
    
    public void updatePendingPositionHandlerSheet(String spreadsheetId, List<ValueRange> data)
    {
    	try
    	{
    		this.batchupdate = new BatchUpdateValuesRequest().setValueInputOption("USER_ENTERED").setData(data);
    		this.batchResult = this.sheetService.spreadsheets().values().batchUpdate(spreadsheetId, this.batchupdate).execute();
    		if(!this.batchResult.isEmpty())
    		{
    			//this.butlog.info("Updated Data to SpreadSheet Successfully.");
    		}
    		else
    		{
    			this.butlog.error("Data could not be updated to SpreadSheet. Need Manual Inspection.");
    		}
    	}
    	catch (Exception e)
    	{
    		this.butlog.error("Error/Exception in method: updatePendingPositionHandlerSheet(String spreadsheetId, List<ValueRange> data) in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
    		System.exit(-1);
    	}
    }
    
    public void appendSheetValues(String spreadsheetId, String range, ValueRange data)
    {
    	try
    	{
    		//this.batchupdate = new BatchUpdateValuesRequest().setValueInputOption("USER_ENTERED").setData(data);
    		AppendValuesResponse appendResult = this.sheetService.spreadsheets().values().append(spreadsheetId, range, data).setValueInputOption("USER_ENTERED").setInsertDataOption("INSERT_ROWS").setIncludeValuesInResponse(true).execute();
    		if(!appendResult.isEmpty())
    		{
    			this.butlog.info("Appending Data to SpreadSheet Successfully.");
    		}
    		else
    		{
    			this.butlog.error("Data could not be updated to SpreadSheet. Need Manual Inspection.");
    		}
    	}
    	catch (Exception e)
    	{
    		this.butlog.error("Error/Exception in method: appendSheetValues(String spreadsheetId, String range, ValueRange data) in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
    		System.exit(-1);
    	}
    }
    
    public void updateFunds(double fund)
    {
    	try
    	{
    		this.butlog.info("Going to update fund");
    		ValueRange body=new ValueRange().setValues(Arrays.asList(Arrays.asList(fund)));
    		UpdateValuesResponse result=this.sheetService.spreadsheets().values().update(spreadsheetId, fund_range, body).setValueInputOption("RAW").execute();
    		if(!result.isEmpty())
    		{
    			this.butlog.info("Funds Updated Successfully");
    		}
    		else
    		{
    			this.butlog.info("Some problem in updating funds. Manual intervention requested...");
    		}
    	}
    	catch (Exception e)
    	{
    		this.butlog.error("Error/Exception in method: updateFunds(parameters..) in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
    		System.exit(-1);
    	}
    }
    public static void main(String... args)  
    {
        // Build a new authorized API client service.
    	GoogleSpreadsheetHandler instance=new GoogleSpreadsheetHandler();
    	//instance.readSheet();
    	//instance.getFinalOrder();
    	instance.updateFunds(2000.00d);
    	instance.close();
    }
}

