package ButlerToken;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.time.LocalDate;
//import java.text.DecimalFormat;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Logger;

public class DBConnection implements AutoCloseable {

	private static String connectionURL;
	private static String hostURL;
	private static String hostPort;
	private static String dbname;
	private static String username;
	private static String password;
	private static String get_last_auth_code_query;
	private static String get_last_auth_access_token_query;
	private static String get_auth_update_query_for_access_token;
	private static String store_order_query;
	private static String today_symbol_retrieve_query;
	private static String zero_qty_update_query;
	private Logger butlog;
	
	private static String auth_table_name;
	
	private Connection con;
	private PreparedStatement prepstmt;
	private String authinsertquery;
	private ResultSet rs;
	//private DecimalFormat df;
	private DBPropertiesLoader property=DBPropertiesLoader.initialize();
	
	public DBConnection()
	{
		this.loadDBProperties();
		this.con=null;
		this.prepstmt=null;
		this.authinsertquery=null;
		this.rs=null;
		//this.df=new DecimalFormat("0.00");
		this.butlog=ButlerLogger.getButlerLogger(this.getClass().getName());
	}
	private void loadDBProperties()
	{
		connectionURL=this.property.getDBPropertyfromPopertyLoader("connectionURL");
		hostURL=this.property.getDBPropertyfromPopertyLoader("hostURL");
		hostPort=this.property.getDBPropertyfromPopertyLoader("hostPort");
		dbname=this.property.getDBPropertyfromPopertyLoader("dbname");
		username=this.property.getDBPropertyfromPopertyLoader("username");
		password=this.property.getDBPropertyfromPopertyLoader("password");
		auth_table_name=this.property.getDBPropertyfromPopertyLoader("auth_table_name");
		store_order_query="insert into "+this.property.getDBPropertyfromPopertyLoader("order_table_name")+this.property.getDBPropertyfromPopertyLoader("store_order_query");
		today_symbol_retrieve_query="select "+this.property.getDBPropertyfromPopertyLoader("today_symbol_retrieve_query_attributes")+" from "+this.property.getDBPropertyfromPopertyLoader("order_table_name")+" "+this.property.getDBPropertyfromPopertyLoader("today_symbol_retrieve_query");
		zero_qty_update_query="update "+this.property.getDBPropertyfromPopertyLoader("order_table_name")+" "+this.property.getDBPropertyfromPopertyLoader("zero_qty_update_query");
		get_last_auth_code_query="select auth_code from "+auth_table_name+" order by rec_time desc limit 1";
		get_last_auth_access_token_query="select token from "+auth_table_name+" order by mod_time desc limit 1";
		get_auth_update_query_for_access_token="update "+auth_table_name+" set token=?, mod_time=? where auth_code=?";
		
	}
	
	@Override
	public void close()
	{
		this.butlog.info("Closing resources in DBConnection class");
		this.con=null;
		this.prepstmt=null;
		this.authinsertquery=null;
		this.rs=null;
		this.butlog=null;		
	}
	
	private String getDBname()
	{		
		String s;
		s = "jdbc:mysql://"+hostURL+":"+hostPort+"/"+dbname;
		//butlog.info("DBName retrived =" +s);
		return s;
	}
	
	private void getDBConnetion()
	{
		if(this.con==null)
		{
			try
			{
				//this.loadDBProperties();
				//this.butlog.warn("connection url = "+connectionURL);
				Class.forName(connectionURL).newInstance();
				//this.butlog.warn("over here 1");
				//this.butlog.warn(this.getDBname()+"\r"+username+"\r"+password);
				this.con = DriverManager.getConnection(this.getDBname(),username,password);
				//this.butlog.warn("over here 2");
				butlog.info("DB connection established.");
				//butlog.info(con.toString());
				//butlog.info("Returning DB Connection");
				//return this.con;
			}
			catch (Exception e)
			{
				this.butlog.error("Error/Exception in method: getDBConnetion() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
				this.close();
				System.exit(-1);
				//return this.con;
			}
		}
	}	
	public PreparedStatement getPreparedStatementforAuthInsert()
	{
		try
		{
			this.getDBConnetion();
			this.authinsertquery="insert into "+auth_table_name+" (rec_id, rec_time, mod_id, mod_time, auth_code, token, free_text_1, free_text_2, free_text_3)"+" values(?,?,?,?,?,?,?,?,?)";
			this.prepstmt=this.con.prepareStatement(this.authinsertquery);
			//this.butlog.info("PrepareStatement done"+this.prepstmt.toString());
			return this.prepstmt;
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method: getPreparedStatementforAuthInsert() in"+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			this.close();
			System.exit(-1);
			return null;
		}
		
	}
	public PreparedStatement getPreparedStatementforAuthUpdateAccessToken()
	{
		try
		{
			this.getDBConnetion();
			this.prepstmt=this.con.prepareStatement(get_auth_update_query_for_access_token);
			this.butlog.info("Returning PreparedStatement Object for token update");
			return this.prepstmt;
		}
		catch (Exception e)
		{
			this.butlog.error("Error/Exception in method: getPreparedStatementforAuthUpdateAccessToken() in"+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			this.close();
			System.exit(-1);
			return null;
		}
	}
	
	public String getAuthCode()
	{
		String authcode=null;
		try
		{
			this.getDBConnetion();
			this.prepstmt=this.con.prepareStatement(get_last_auth_code_query);
			this.rs=this.prepstmt.executeQuery();
			if(this.rs.next())
			{
				authcode=this.rs.getString(1);
			}
			//butlog.info("authentication code retrived from DB is ="+authcode);
			return authcode;
		}
		catch (Exception e)
		{
			this.butlog.error("Error/Exception in method: getAuthCode() in"+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			this.close();
			System.exit(-1);
			//e.printStackTrace();
			return null;
		}
	}
	public String getLastAuthAccessToken()
	{
		String accesstoken=null;
		try
		{
			this.getDBConnetion();
			this.prepstmt=this.con.prepareStatement(get_last_auth_access_token_query);
			this.rs=this.prepstmt.executeQuery();
			if(this.rs.next())
			{
				accesstoken=this.rs.getString(1);
			}
			this.butlog.info("Access Token Retrived and now, returning the same");
			return accesstoken;
		}
		catch (Exception e)
		{
			this.butlog.error("Error/Exception in method: getLastAuthAccessToken() in"+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			this.close();
			System.exit(-1);
			return null;
		}
	}
	public void storeOrderToDB(String broker_order_ref_no, String symbol, int qty, String order_type, String side, String product_type, float limit_price, float stop_price, int disclosed_qty, String validity, String offline_order, float stoploss, float takeprofit, String message, String order_status, float pnl_percentage, String strategy_name, float pnl_amount, float mod_stoploss, String ft1, String ft2, String ft3, String ft4, String ft5)
	{
		try
		{
			this.getDBConnetion();
			this.prepstmt=this.con.prepareStatement(store_order_query);
			this.prepstmt.setString(1, broker_order_ref_no);
			this.prepstmt.setString(2, symbol);
			this.prepstmt.setInt(3, qty);
			this.prepstmt.setString(4, order_type);
			this.prepstmt.setString(5, side);
			this.prepstmt.setString(6, product_type);
			this.prepstmt.setFloat(7, limit_price);
			this.prepstmt.setFloat(8, stop_price);
			this.prepstmt.setInt(9, disclosed_qty);
			this.prepstmt.setString(10, validity);
			this.prepstmt.setString(11, offline_order);
			this.prepstmt.setFloat(12, stoploss);
			this.prepstmt.setFloat(13, takeprofit);
			this.prepstmt.setString(14, message);
			this.prepstmt.setString(15, order_status);
			this.prepstmt.setFloat(16, pnl_percentage);
			this.prepstmt.setString(17, strategy_name);
			this.prepstmt.setFloat(18, pnl_amount);
			this.prepstmt.setFloat(19, mod_stoploss);
			if(ft1==null) {	this.prepstmt.setNull(20, Types.VARCHAR);	}
			else {	this.prepstmt.setString(20, ft1);	}
			if(ft2==null) {	this.prepstmt.setNull(21, Types.VARCHAR);	}
			else {	this.prepstmt.setString(21, ft2);	}
			if(ft3==null) {	this.prepstmt.setNull(22, Types.VARCHAR);	}
			else {	this.prepstmt.setString(22, ft3);	}
			if(ft4==null) {	this.prepstmt.setNull(23, Types.VARCHAR);	}
			else {	this.prepstmt.setString(23, ft4);	}
			if(ft5==null) {	this.prepstmt.setNull(24, Types.VARCHAR);	}
			else {	this.prepstmt.setString(24, ft5);	}
			//this.butlog.warn(ft1);
			int result=this.prepstmt.executeUpdate();
			if (!(result==0)) { this.butlog.info("Order Stored to Database.");}
		}
		catch (Exception e)
		{
			this.butlog.error("Error/Exception in method: storeOrderToDB(Parameters...) in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			this.close();
			System.exit(-1);
		}
	}
	public ResultSet positionSymbolsOfToday()
	{
		try
		{
			this.getDBConnetion();
			this.prepstmt=this.con.prepareStatement(today_symbol_retrieve_query);
			String today=LocalDate.now().toString();
			this.prepstmt.setString(1, today);
			//this.prepstmt.setString(1,"2020-05-13"); //remove this after testing and enable above two
			this.rs=this.prepstmt.executeQuery();
			this.butlog.info("Retrieved Positions for today..");
			return this.rs;
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Excetion in method: positionSymbolsOfToday in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			this.close();
			System.exit(-1);
			return null;
		}
	}
	public void updatePositions(String dbbutler_record_id, float running_profit_percentage, float pl, float mod_stoploss)
	{
		try
		{
			this.getDBConnetion();
			this.prepstmt=this.con.prepareStatement(zero_qty_update_query);
			this.prepstmt.setFloat(1, running_profit_percentage);
			this.prepstmt.setFloat(2, pl);
			if(mod_stoploss==0.00f) { this.prepstmt.setNull(3, Types.FLOAT);	}
			else { this.prepstmt.setFloat(3, mod_stoploss);	}
			this.prepstmt.setString(4, dbbutler_record_id);
			int result=this.prepstmt.executeUpdate();
			if(result==0)
			{
				this.butlog.warn("\nTHIS IS A WARNING\nPlease Check that No Script Updated");
			}
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method: updatePositionWhereQtyZero() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			this.close();
			System.exit(-1);
		}
	}
}
