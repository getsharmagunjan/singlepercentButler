package ButlerToken;

import org.apache.logging.log4j.Logger;

public class ButlerMaster implements AutoCloseable {
	private ButlerAuthenticator ba=null;
	private AuthorizationHTML run_html=null;
	private HTMLRunning token=null;
	private AccessTokenHandler token_handler=null;
	private Logger butlog=null;
	
	public ButlerMaster()
	{
		this.ba=new ButlerAuthenticator();
		this.run_html=new AuthorizationHTML();
		this.butlog=ButlerLogger.getButlerLogger(this.getClass().getName());
		this.token=new HTMLRunning();
		this.token_handler=new AccessTokenHandler();
	}
	private void runButlerAuthenticator()
	{
		this.butlog.info("Running Butler Authenticator");
		this.ba.authenticate();
		this.butlog.info("Successful run Butler Authenticator");
	}
	private void runAuthorizationHTML()
	{
		this.butlog.info("Running Authorization HTML");
		this.run_html.getAuthorizationHTML();
		this.butlog.info("Successful run Authorization HTML");
	}
	private void runHTMLRunning()
	{
		this.butlog.info("Running Authorization Token");
		this.token.getHTMLRunning();
		this.butlog.info("Successful run Authorization Token");
	}
	private void runAccessTokenHandler()
	{
		this.butlog.info("Running Access Token Handler");
		this.token_handler.handleAccessToken();
		this.butlog.info("Successful run Access Token Handler");
	}
	
	public void masterCommands()
	{
		this.runButlerAuthenticator();
		this.runAuthorizationHTML();
		this.runHTMLRunning();
		//this.runAccessTokenHandler();
		//this.close();
	}
	
	@Override
	public void close()
	{
		this.butlog.info("Closing Butler Master's resources");
		this.ba=null;
		this.run_html=null;
		this.token=null;
		this.token_handler=null;
		this.butlog=null;	
	}
	
	public static void main(String[] args) 
	{
		ButlerMaster master = new ButlerMaster();
		master.masterCommands();
		master.close();
	}

}
