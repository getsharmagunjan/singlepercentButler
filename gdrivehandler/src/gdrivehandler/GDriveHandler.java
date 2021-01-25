package gdrivehandler;

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
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import butlerforfyers.OtherPropertiesLoader;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.List;
import butlerforfyers.ButlerLogger;
//import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
//import java.security.GeneralSecurityException;
import java.util.Collections;
//import java.util.List;

//import org.apache.log4j.Logger;


public class GDriveHandler
{
	
    private String APPLICATION_NAME;
    private JsonFactory JSON_FACTORY;
    private String GDRIVE_TOKENS_DIRECTORY_PATH;
    private List<String> nifty_filenames;
    private Logger butlog;
    private OtherPropertiesLoader property=OtherPropertiesLoader.initialize();
    private List<String> SCOPES;
    private String GDRIVE_CREDENTIALS_FILE_PATH;
    
    public GDriveHandler()
    {
    	this.butlog=ButlerLogger.getButlerLogger(this.getClass().getName());
    	this.APPLICATION_NAME="Google Drive Handler for Butler-System";
    	this.JSON_FACTORY=JacksonFactory.getDefaultInstance();
    	this.nifty_filenames=new ArrayList<String>();
    	this.GDRIVE_TOKENS_DIRECTORY_PATH=this.property.getPropertyValue("GDRIVE_TOKENS_DIRECTORY_PATH");
    	this.GDRIVE_CREDENTIALS_FILE_PATH=this.property.getPropertyValue("GDRIVE_CREDENTIALS_FILE_PATH");
    	this.SCOPES=Collections.singletonList(DriveScopes.DRIVE);
    	nifty_filenames.add(0, "nifty50_old.txt");
		nifty_filenames.add(1, "nifty50_new.txt");
		nifty_filenames.add(2, "nifty100_old.txt");
		nifty_filenames.add(3, "nifty100_new.txt");
    }
    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws Exception 
    {
        // Load client secrets.
        //InputStream in = GDriveHandler.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    	InputStream in = new FileInputStream(GDRIVE_CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(GDRIVE_TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
    private void downloadFilesfromDrive(String outfilename, String fileid, Drive service) throws Exception
    {
    	String path = "C:\\butler-system\\butler-additional-resources\\"+outfilename;
    	java.io.File out = new java.io.File(path);
    	OutputStream outputStream = new FileOutputStream(out);
    	service.files().get(fileid).executeMediaAndDownloadTo(outputStream);
    	butlog.info("File = "+outfilename+" downloaded successfully.");
    	//System.out.println(out.getAbsolutePath());
    }
    private void handleGDrive()
    {
    	try
    	{
    		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    		Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
    				.setApplicationName(APPLICATION_NAME)
    				.build();
        
	        // Print the names and IDs for up to 10 files.
	        FileList result = service.files().list()
	                .setPageSize(10)
	                .setFields("nextPageToken, files(id, name)")
	                .execute();
	        List<File> files = result.getFiles();
	        if (files == null || files.isEmpty()) 
	        {
	            System.out.println("No files found.");
	        } 
	        else 
	        {
	            System.out.println("Files:");
	            for (File file : files) 
	            {
	                for (int i=0; i<nifty_filenames.size(); i++)
	                {
	                	if(file.getName().equalsIgnoreCase(nifty_filenames.get(i).toString()))
	                	{
	                		downloadFilesfromDrive(nifty_filenames.get(i).toString(), file.getId(), service);
	                	}
	                }
	            	
	            	//System.out.printf("%s (%s)\n", file.getName(), file.getId());
	            }
	        }
    	}
        catch (Exception e)
        {
        	this.butlog.error("Error/Exception in method: handleGDrive() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.exit(-1);
        }
    }

    public static void main (String... args) 
    {
        GDriveHandler handle = new GDriveHandler();
        handle.handleGDrive();
    	
    }
}