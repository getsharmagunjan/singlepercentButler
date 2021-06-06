package positionshandlerforfyers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.math3.util.Precision;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.github.wnameless.json.flattener.JsonFlattener;
import butlerforfyers.RESTConnectionHandler;
import butlerforfyers.OtherPropertiesLoader;
import butlerforfyers.ButlerLogger;
import okhttp3.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.google.api.services.sheets.v4.model.ValueRange;
import spreadsheethandler.GoogleSpreadsheetHandler;
import butlerforfyers.DBConnection;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class PendingPositionsHandler implements AutoCloseable
{
	private int size_of_orders;
	private int sizeOfOrderbook;
	private String forBOSheetName;
	//private String default_productType;
	private int positionOfRemovedOrStayColumnInGSheet_0FirstColumn;
	private String forBOSheetSizeDeterminingRange;
	private String forBOSheetFirstRowWithDataExceptHeader;
	private String forBOSheetLastColumn;
	private String forBOSheetRemovedOrStayColumn;
	Map<String, Object> flattenedJSONMap;
	Map<String, Object> flattenedJSONMapForOrderbook;
	private List<PositionScriptDetails> positionScriptDetails;
	private List<BrokerOrdersScriptDetails> brokerOrdersScriptDetails;
	private List<ValueRange> data;
	private ValueRange appendDataToScriptsRecords;
	private String scriptsRecordsSpreadsheetID;
	private String scriptRecordsAppendRange;
	private List<List<Object>> values;
	private String positions_url;
	private String modify_order_url;
	private String get_orders_url;
	//private String exit_positions_url;
	private Logger butlog;
	private Response response;
	private Response orderbookResponse;
	private RESTConnectionHandler restconnection;
	private OtherPropertiesLoader property=OtherPropertiesLoader.initialize();
	private GoogleSpreadsheetHandler gsheet;
	String butler_system_spreadsheetId;
	private DBConnection dbconn;
	private ResultSet rs;
	private List<DBOrdersScriptDetails> dbOrdersScriptDetails;
	private List<OrdersWhichAreNotPositions> ordersWhichAreNotPositions;
	private List<String> responseOfCancellationRESTfulWebService;
	private String responseOfBrokerOrderbook;
	private String pending_position_handler_spreadsheetId;

	PendingPositionsHandler()
	{
		this.size_of_orders=0;
		this.sizeOfOrderbook=0;
		this.data = new ArrayList<>();
		this.butlog=ButlerLogger.getButlerLogger(this.getClass().getName());
		this.response=null;
		this.appendDataToScriptsRecords=null;
		this.scriptRecordsAppendRange=this.property.getPropertyValue("scripts_records_append_range");
		this.scriptsRecordsSpreadsheetID=this.property.getPropertyValue("scripts_records_spreadsheet_id");
		this.orderbookResponse=null;
		this.flattenedJSONMap=new HashMap<String, Object>();
		this.flattenedJSONMapForOrderbook=new HashMap<String, Object>();
		this.restconnection=new RESTConnectionHandler();
		this.gsheet=new GoogleSpreadsheetHandler();
		this.positionScriptDetails=new ArrayList<PositionScriptDetails>();
		this.dbOrdersScriptDetails=new ArrayList<DBOrdersScriptDetails>();
		this.brokerOrdersScriptDetails=new ArrayList<BrokerOrdersScriptDetails>();
		this.ordersWhichAreNotPositions=new ArrayList<OrdersWhichAreNotPositions>();
		this.positions_url=this.property.getPropertyValue("positions_url");
		//this.exit_positions_url=this.property.getPropertyValue("exit_positions_url");
		//this.default_productType=this.property.getPropertyValue("default_productType");
		this.forBOSheetName=this.property.getPropertyValue("forBOSheetName");
		this.forBOSheetSizeDeterminingRange=this.property.getPropertyValue("forBOSheetSizeDeterminingRange");
		this.pending_position_handler_spreadsheetId=this.property.getPropertyValue("pending_position_handler_spreadsheetId");
		this.forBOSheetLastColumn=this.property.getPropertyValue("forBOSheetLastColumn");
		this.forBOSheetFirstRowWithDataExceptHeader=this.property.getPropertyValue("forBOSheetFirstRowWithDataExceptHeader");
		this.forBOSheetRemovedOrStayColumn=this.property.getPropertyValue("forBOSheetRemovedOrStayColumn");
		this.positionOfRemovedOrStayColumnInGSheet_0FirstColumn = Integer.parseInt(this.property.getPropertyValue("positionOfRemovedOrStayColumnInGSheet_0FirstColumn").toString().trim());
		this.dbconn=new DBConnection();
		this.rs=null;
		this.butler_system_spreadsheetId=this.property.getPropertyValue("spreadsheetId");
		this.responseOfCancellationRESTfulWebService=new ArrayList<String>();
		this.responseOfBrokerOrderbook=null;
		this.modify_order_url=this.property.getPropertyValue("modify_order_url");
		this.get_orders_url=this.property.getPropertyValue("get_orders_url");
		this.getGoogleSheetForBOValues();
	}
	@Override
	public void close()
	{
		this.butlog.info("Closing PendingPositionHandler Resources");
		this.size_of_orders=0;
		this.data=null;
		this.sizeOfOrderbook=0;
		this.response.close();
		this.orderbookResponse.close();
		this.flattenedJSONMap=null;
		this.flattenedJSONMapForOrderbook=null;
		this.pending_position_handler_spreadsheetId=null;
		this.restconnection.close();
		this.gsheet.close();
		this.gsheet=null;
		this.positionScriptDetails=null;
		this.dbOrdersScriptDetails=null;
		this.brokerOrdersScriptDetails=null;
		this.ordersWhichAreNotPositions=null;
		//this.default_productType=null;
		this.positions_url=null;
		this.forBOSheetName=null;
		this.forBOSheetFirstRowWithDataExceptHeader=null;
		this.forBOSheetLastColumn=null;
		this.forBOSheetSizeDeterminingRange=null;
		this.positionOfRemovedOrStayColumnInGSheet_0FirstColumn=0;
		this.dbconn.close();
		this.rs = null;
		this.appendDataToScriptsRecords=null;
		this.scriptRecordsAppendRange=null;
		this.scriptsRecordsSpreadsheetID=null;
		this.butler_system_spreadsheetId=null;
		this.responseOfCancellationRESTfulWebService=null;
		this.responseOfBrokerOrderbook=null;
		this.modify_order_url=null;
		//this.exit_positions_url=null;
		this.butlog.info("Closed PendingPositionHandler Resources");
		this.values=null;
		this.butlog=null;
		Runtime.getRuntime().gc();
		System.gc();
		System.exit(0);
	}
	private void getGoogleSheetForBOValues()
	{
		try
		{
			String range = this.forBOSheetName+"!"+this.forBOSheetSizeDeterminingRange;
			this.values=this.gsheet.readSheetForPendingPositionHandler(this.butler_system_spreadsheetId, range);
			int rowSizeOfForBOSheet = this.values.size();
			
			//inserting "Removed/Stay" function to the cells to calculate the same:
			List<ValueRange> stayOrRemovedData=new ArrayList<>();
			for(int i=Integer.parseInt(this.forBOSheetFirstRowWithDataExceptHeader);i<=rowSizeOfForBOSheet;i++)
			{
				stayOrRemovedData.add(new ValueRange().setRange(this.forBOSheetName+"!"+this.forBOSheetRemovedOrStayColumn+i).setValues(Arrays.asList(Arrays.asList("=if(V"+i+"=\"\",\"Removed\",\"Stay\")"))));
			}
			this.gsheet.updatePendingPositionHandlerSheet(this.butler_system_spreadsheetId, stayOrRemovedData);
			
			//getting values of complete range of forBO
			range = this.forBOSheetName+"!"+"A"+this.forBOSheetFirstRowWithDataExceptHeader+":"+this.forBOSheetLastColumn+rowSizeOfForBOSheet;
			this.values=this.gsheet.readSheetForPendingPositionHandler(this.butler_system_spreadsheetId, range);
		}
		catch (Exception e)
		{
			this.butlog.error("Error/Exception in method: getGoogleSheetForBOValues() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.gc();
			System.exit(-1);
		}
	}
	public void handlePendingPositions()
	{
		try
		{
			this.getBrokerOrderbookAndSaveToBrokerOrdersScriptDetails();
			this.getOrdersFromDBandCreateObjects();
			this.getPositionsSizeAndCreateObjects();
			this.analyzeAndCreateOrdersWhichAreNotPositions();
			this.analyzeRemovedOrStayForOrdersWhichAreNotPositions();
			this.cancelOrdersWhichAreRemovedAndUpdateDB();
			this.analyzeAndIncreaseQuantityOfOrdersWhichAreStayButNotPositions();
			//this.analyzePresentlyOngoingPositionsAndAdjustSLAndTarget();
		}
		catch(Exception e)
		{
			this.butlog.error("Error/Exception in method: handlePendingPositions() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.gc();
			System.exit(-1);
		}
	}
	private void getOrdersFromDBandCreateObjects() throws Exception
	{
		//this.response=this.restconnection.sendRequest(this.orders_url);
		this.rs=this.dbconn.positionSymbolsOfToday();
		if(this.rs==null)
		{
			this.butlog.error("No Orders are Retrieved from DB for Today.");
		}
		else
		{
			int i=0;
			while(this.rs.next())
			{
				this.dbOrdersScriptDetails.add(i, new DBOrdersScriptDetails());
				this.dbOrdersScriptDetails.get(i).setSymbol(this.rs.getString("Symbol"));
				this.dbOrdersScriptDetails.get(i).setBrokerOrderRefNo(this.rs.getString("broker_order_ref_no"));
				this.dbOrdersScriptDetails.get(i).setButler_record_id(this.rs.getInt("butler_order_id"));
				this.dbOrdersScriptDetails.get(i).setInitialStoploss(this.rs.getFloat("stoploss"));
				this.dbOrdersScriptDetails.get(i).setPnlPercentage(this.rs.getFloat("pnl_percentage"));
				this.dbOrdersScriptDetails.get(i).setQty(this.rs.getInt("qty"));
				this.dbOrdersScriptDetails.get(i).setSide(this.rs.getString("side"));
				this.dbOrdersScriptDetails.get(i).setStoplossModified(this.rs.getFloat("mod_stoploss"));
				this.dbOrdersScriptDetails.get(i).setLimitPrice(this.rs.getFloat("limit_price"));
				this.dbOrdersScriptDetails.get(i).setPl(this.rs.getFloat("pnl_amount"));
				i++;
			}
		}
		
	}
	private void getPositionsSizeAndCreateObjects() throws Exception
	{
		this.response=this.restconnection.sendRequest(this.positions_url);
		
		String responseValue = this.response.body().string();
		//this.butlog.warn("response is =\n"+responseValue);
		Object object=new Object();
		JSONParser parser=new JSONParser();
		JSONObject jsonobject=new JSONObject();
		object=parser.parse(responseValue);
		jsonobject=(JSONObject) object;
		this.flattenedJSONMap=JsonFlattener.flattenAsMap(jsonobject.toString());
		//flattenedJSONMap.forEach((k,v) -> this.butlog.warn(k+" : "+v));
		Iterator<Map.Entry<String, Object>> itr =this.flattenedJSONMap.entrySet().iterator();
		while(itr.hasNext())
		{
			Map.Entry<String, Object> entry=itr.next();
			if(entry.getKey().contains(".symbol"))
			{
				this.size_of_orders++;
				//this.butlog.warn(entry.getKey());
			}
		}
		this.butlog.info("size of Orderbook is ="+this.size_of_orders);
		
		for (int i=0; i<this.size_of_orders; i++)
		{
			this.positionScriptDetails.add(i, new PositionScriptDetails());
			this.positionScriptDetails.get(i).setAvgPrice(Precision.round(Float.parseFloat(this.flattenedJSONMap.get("netPositions["+i+"].avgPrice").toString().trim()),2));
			this.positionScriptDetails.get(i).setBuyAug(Precision.round(Float.parseFloat(this.flattenedJSONMap.get("netPositions["+i+"].buyAvg").toString().trim()),2));
			this.positionScriptDetails.get(i).setBuyQty(Integer.parseInt(this.flattenedJSONMap.get("netPositions["+i+"].buyQty").toString().trim()));
			this.positionScriptDetails.get(i).setCrossCurrency(this.flattenedJSONMap.get("netPositions["+i+"].crossCurrency").toString().trim());
			this.positionScriptDetails.get(i).setFyToken(this.flattenedJSONMap.get("netPositions["+i+"].fyToken").toString().trim());
			this.positionScriptDetails.get(i).setId(this.flattenedJSONMap.get("netPositions["+i+"].id").toString().trim());
			this.positionScriptDetails.get(i).setNetAvg(Precision.round(Float.parseFloat(this.flattenedJSONMap.get("netPositions["+i+"].netAvg").toString().trim()),2));
			this.positionScriptDetails.get(i).setNetQty(Integer.parseInt(this.flattenedJSONMap.get("netPositions["+i+"].netQty").toString().trim()));
			this.positionScriptDetails.get(i).setPl(Precision.round(Float.parseFloat(this.flattenedJSONMap.get("netPositions["+i+"].pl").toString().trim()),2));
			this.positionScriptDetails.get(i).setProductType(this.flattenedJSONMap.get("netPositions["+i+"].productType").toString().trim());
			this.positionScriptDetails.get(i).setQty(Integer.parseInt(this.flattenedJSONMap.get("netPositions["+i+"].qty").toString().trim()));
			this.positionScriptDetails.get(i).setQtyMultiCom(Precision.round(Float.parseFloat(this.flattenedJSONMap.get("netPositions["+i+"].qtyMulti_com").toString().trim()),2));
			this.positionScriptDetails.get(i).setRbiRefRate(Precision.round(Float.parseFloat(this.flattenedJSONMap.get("netPositions["+i+"].rbiRefRate").toString().trim()),2));
			this.positionScriptDetails.get(i).setRealizedProfit(Precision.round(Float.parseFloat(this.flattenedJSONMap.get("netPositions["+i+"].realized_profit").toString().trim()),2));
			this.positionScriptDetails.get(i).setSegment(this.flattenedJSONMap.get("netPositions["+i+"].segment").toString().trim());
			this.positionScriptDetails.get(i).setSellAvg(Precision.round(Float.parseFloat(this.flattenedJSONMap.get("netPositions["+i+"].sellAvg").toString().trim()),2));
			this.positionScriptDetails.get(i).setSellQty(Integer.parseInt(this.flattenedJSONMap.get("netPositions["+i+"].sellQty").toString().trim()));
			this.positionScriptDetails.get(i).setSlNo(Integer.parseInt(this.flattenedJSONMap.get("netPositions["+i+"].slNo").toString().trim()));
			this.positionScriptDetails.get(i).setSymbol(this.flattenedJSONMap.get("netPositions["+i+"].symbol").toString().trim().split("NSE:|-EQ")[1]);
			this.positionScriptDetails.get(i).setUnrealizedProfit(Precision.round(Float.parseFloat(this.flattenedJSONMap.get("netPositions["+i+"].unrealized_profit").toString().trim()),2));
			if(NumberUtils.isParsable(this.flattenedJSONMap.get("netPositions["+i+"].side").toString().trim()))
			{
				this.positionScriptDetails.get(i).setSide(Integer.parseInt(this.flattenedJSONMap.get("netPositions["+i+"].side").toString().trim()));
			}
			else
			{
				this.positionScriptDetails.get(i).setSide(0);
			}
		
		}
		for(int i=0;i<this.values.size(); i++)
		{
			for (int j=0; j<this.positionScriptDetails.size(); j++)
			{
				if(this.positionScriptDetails.get(j).getSymbol().toString().trim().equalsIgnoreCase(this.values.get(i).get(0).toString().trim()))
				{
					this.positionScriptDetails.get(j).setStayOrRemoved(this.values.get(i).get(this.positionOfRemovedOrStayColumnInGSheet_0FirstColumn).toString().trim());
				}
			}
		}
		
		//for (int i=0; i<this.positionScriptDetails.size(); i++)
		//{
		//	this.butlog.warn(this.positionScriptDetails.get(i).getAvgPrice()+"\t"+this.positionScriptDetails.get(i).getBuyAug()+"\t"+this.positionScriptDetails.get(i).getBuyQty()+"\t"+this.positionScriptDetails.get(i).getCrossCurrency()+"\t"+this.positionScriptDetails.get(i).getFyToken()+"\t"+this.positionScriptDetails.get(i).getId()+"\t"+this.positionScriptDetails.get(i).getNetAvg()+"\t"+this.positionScriptDetails.get(i).getNetQty()+"\t"+this.positionScriptDetails.get(i).getPl()+"\t"+this.positionScriptDetails.get(i).getProductType()+"\t"+this.positionScriptDetails.get(i).getQty()+"\t"+this.positionScriptDetails.get(i).getQtyMultiCom()+"\t"+this.positionScriptDetails.get(i).getRbiRefRate()+"\t"+this.positionScriptDetails.get(i).getRealizedProfit()+"\t"+this.positionScriptDetails.get(i).getSegment()+"\t"+this.positionScriptDetails.get(i).getSellAvg()+"\t"+this.positionScriptDetails.get(i).getSellQty()+"\t"+this.positionScriptDetails.get(i).getSide()+"\t"+this.positionScriptDetails.get(i).getSlNo()+"\t"+this.positionScriptDetails.get(i).getSymbol()+"\t"+this.positionScriptDetails.get(i).getUnrealizedProfit()+"\t"+this.positionScriptDetails.get(i).getStayOrRemoved());
		//}
	}
	private void analyzeAndCreateOrdersWhichAreNotPositions() throws Exception
	{
		int pendingOrderCounter=0;
		boolean match;
		for(int o=0;o<this.dbOrdersScriptDetails.size();o++)
		{
			match=false;
			for(int p=0; p<this.positionScriptDetails.size();p++)
			{
				if((this.dbOrdersScriptDetails.get(o).getSymbol().equalsIgnoreCase(this.positionScriptDetails.get(p).getSymbol())))
				{
					match=true;
				}
			}
			if(match==false)
			{
				this.ordersWhichAreNotPositions.add(pendingOrderCounter, new OrdersWhichAreNotPositions());
				this.ordersWhichAreNotPositions.get(pendingOrderCounter).setSymbol(this.dbOrdersScriptDetails.get(o).getSymbol());
				this.ordersWhichAreNotPositions.get(pendingOrderCounter).setId(this.dbOrdersScriptDetails.get(o).getBrokerOrderRefNo());
				this.ordersWhichAreNotPositions.get(pendingOrderCounter).setButlerRecordId(this.dbOrdersScriptDetails.get(o).getButler_record_id());
				this.ordersWhichAreNotPositions.get(pendingOrderCounter).setSide(this.dbOrdersScriptDetails.get(o).getSide());
				this.ordersWhichAreNotPositions.get(pendingOrderCounter).setQty(this.dbOrdersScriptDetails.get(o).getQty());
				this.ordersWhichAreNotPositions.get(pendingOrderCounter).setLimitPrice(this.dbOrdersScriptDetails.get(o).getLimitPrice());
				this.ordersWhichAreNotPositions.get(pendingOrderCounter).setStopLoss(this.dbOrdersScriptDetails.get(o).getInitialStoploss());
				pendingOrderCounter++;
			}
		}
		for(int i=0;i<this.ordersWhichAreNotPositions.size();i++)
		{
			this.butlog.warn(this.ordersWhichAreNotPositions.get(i).getButlerRecordId()+"\t"+this.ordersWhichAreNotPositions.get(i).getId()+"\t"+this.ordersWhichAreNotPositions.get(i).getSymbol());
		}
	}
	private void analyzeRemovedOrStayForOrdersWhichAreNotPositions() throws Exception
	{
		for(int i=0;i<this.values.size(); i++)
		{
			for (int j=0; j<this.ordersWhichAreNotPositions.size(); j++)
			{
				if(this.ordersWhichAreNotPositions.get(j).getSymbol().toString().trim().equalsIgnoreCase(this.values.get(i).get(0).toString().trim()))
				{
					this.ordersWhichAreNotPositions.get(j).setStayOrRemoved(this.values.get(i).get(this.positionOfRemovedOrStayColumnInGSheet_0FirstColumn).toString().trim());
					this.butlog.info(this.ordersWhichAreNotPositions.get(j).getSymbol()+"\t\t"+this.values.get(i).get(this.positionOfRemovedOrStayColumnInGSheet_0FirstColumn));
				}
			}
		}
		this.butlog.warn("Values (hash code) = "+this.values.hashCode());
		this.butlog.warn("gsheet (hash code) = "+this.gsheet.hashCode());
		this.butlog.warn("orderswhicharenotpositions (hash code) = "+this.ordersWhichAreNotPositions.hashCode());
	}
	private void cancelOrdersWhichAreRemovedAndUpdateDB() throws Exception
	{
		String payload=null;
		for (int i=0; i<this.ordersWhichAreNotPositions.size();i++)
		{
			if(this.ordersWhichAreNotPositions.get(i).getStayOrRemoved().equalsIgnoreCase("Removed"))
			{
				payload=
						"{"
							//+"\"id\" : "+"\""+this.ordersWhichAreNotPositions.get(i).getId().split("-")[0]+"\""+","
							+"\"id\" : "+"\""+this.ordersWhichAreNotPositions.get(i).getId()+"\""+ //this one working
							//+"\"type\" : "+"\"4\""+
							//+"\"productType\" : "+"\"BO\""+
						"}";
				this.butlog.info("Cancelling "+this.ordersWhichAreNotPositions.get(i).getSymbol()+" (ID="+this.ordersWhichAreNotPositions.get(i).getId()+")");
				this.butlog.warn("Check Payload = "+payload);
				this.responseOfCancellationRESTfulWebService=this.restconnection.cancelOrder(payload);
				if(Integer.parseInt(this.responseOfCancellationRESTfulWebService.get(0).toString().trim())==200)
				{
					this.dbconn.updateOrdersWhenPendingPositionsRemoved(Integer.toString(this.ordersWhichAreNotPositions.get(i).getButlerRecordId()));
					this.butlog.info("Cancelled "+this.ordersWhichAreNotPositions.get(i).getSymbol());
				}
			}
		}
	}
	private void analyzeAndIncreaseQuantityOfOrdersWhichAreStayButNotPositions() throws Exception
	{
		int noOfStayingOrders=0;
		int noOfRemovedOrders=0;
		float totalAmountFreedByRemovedOrders=0.0f;
		String payload=null;
		List<String> responseOfModifiedQtyRestfulWebservice=new ArrayList<String>();
		for (int i=0;i<this.ordersWhichAreNotPositions.size(); i++)
		{
			if(this.ordersWhichAreNotPositions.get(i).getStayOrRemoved().equalsIgnoreCase("Stay"))
			{
				noOfStayingOrders++;
			}
			if(this.ordersWhichAreNotPositions.get(i).getStayOrRemoved().equalsIgnoreCase("Removed"))
			{
				noOfRemovedOrders++;
				totalAmountFreedByRemovedOrders=Precision.round(totalAmountFreedByRemovedOrders+(this.ordersWhichAreNotPositions.get(i).getQty()*this.ordersWhichAreNotPositions.get(i).getLimitPrice()),2);
			}
		}
		this.butlog.info("Total Staying Pending Orders = "+noOfStayingOrders);
		this.butlog.info("Total Removed Pending Orders = "+noOfRemovedOrders+" Total Amount Freed = "+totalAmountFreedByRemovedOrders);
		
		float amountAvailablePerStayingOrder = Precision.round((totalAmountFreedByRemovedOrders/noOfStayingOrders), 2);
		
		for(int i=0; i<this.ordersWhichAreNotPositions.size(); i++)
		{
			if(this.ordersWhichAreNotPositions.get(i).getStayOrRemoved().equalsIgnoreCase("Stay"))
			{
				this.ordersWhichAreNotPositions.get(i).setUpdatedQtyForStayingOrders(this.ordersWhichAreNotPositions.get(i).getQty()+(int)(amountAvailablePerStayingOrder/this.ordersWhichAreNotPositions.get(i).getLimitPrice()));
				if(this.ordersWhichAreNotPositions.get(i).getUpdatedQtyForStayingOrders()==this.ordersWhichAreNotPositions.get(i).getQty())
				{
					this.butlog.info(this.ordersWhichAreNotPositions.get(i).getSymbol()+" Initial Qty="+this.ordersWhichAreNotPositions.get(i).getQty()+" Updated Qty="+this.ordersWhichAreNotPositions.get(i).getUpdatedQtyForStayingOrders()+". Quantity remains same.");
					responseOfModifiedQtyRestfulWebservice.add(0, "404");
				}
				else
				{
					this.butlog.info(this.ordersWhichAreNotPositions.get(i).getSymbol()+" Initial Qty="+this.ordersWhichAreNotPositions.get(i).getQty()+" Updated Qty="+this.ordersWhichAreNotPositions.get(i).getUpdatedQtyForStayingOrders());
					payload=
							"{"
								//+"\"id\" : "+"\""+this.ordersWhichAreNotPositions.get(i).getId().split("-")[0]+"\""+","
								+"\"id\" : "+"\""+this.ordersWhichAreNotPositions.get(i).getId()+"\""+","
								+"\"qty\" : "+"\""+this.ordersWhichAreNotPositions.get(i).getUpdatedQtyForStayingOrders()+"\""+","
								+"\"limitPrice\" : "+"\""+this.ordersWhichAreNotPositions.get(i).getLimitPrice()+"\""+","
								+"\"stopLoss\" : "+"\""+this.ordersWhichAreNotPositions.get(i).getStopLoss()+"\""+								
							"}";		
					this.butlog.warn("Modify Qty payload is = \n"+payload);
					responseOfModifiedQtyRestfulWebservice=this.restconnection.modifyOrder(this.modify_order_url, payload);
				}
				if(Integer.parseInt(responseOfModifiedQtyRestfulWebservice.get(0).toString().trim())==200)
				{
					this.dbconn.updateOrdersWhenQtyOfStayingOrdersUpdated(this.ordersWhichAreNotPositions.get(i).getUpdatedQtyForStayingOrders(), Integer.toString(this.ordersWhichAreNotPositions.get(i).getButlerRecordId()));
					this.butlog.info("Updated DB for updated Qty of "+this.ordersWhichAreNotPositions.get(i).getSymbol());
				}		
			}
		}
	}
	private void analyzePresentlyOngoingPositionsAndAdjustSLAndTarget() throws Exception
	{
		// dc = DB Counter 
		// pc = positions counter
		float mod_stoploss=0.00f;
		float total_buy_value=0.0f;
		float running_profit_percentage=0.0f;
		float lockin_profit_percentage=0.0f;
		float mod_stoploss_before_round=0.0f;
		//float running_profit=0.0f;
		float lockin_profit=0.0f;
		float stoploss_increment_points =0.0f;
		for(int dc=0; dc<this.dbOrdersScriptDetails.size(); dc++)
		{
			for(int pc=0; pc<this.positionScriptDetails.size(); pc++)
			{
				if(this.dbOrdersScriptDetails.get(dc).getSymbol().trim().equalsIgnoreCase(this.positionScriptDetails.get(pc).getSymbol().trim()))
				{
					
					total_buy_value=Precision.round(this.positionScriptDetails.get(pc).getBuyAug()*this.dbOrdersScriptDetails.get(dc).getQty(),2);
					running_profit_percentage=Precision.round((((this.positionScriptDetails.get(pc).getPl())/total_buy_value)*100),2);
					if(this.positionScriptDetails.get(pc).getQty()==0)
					{
						//this.butlog.warn("symbol : "+this.symbol.get(r)+" has net qty of = "+this.netQty.get(r)+". Hence sending for DB updation.");
						mod_stoploss=0.00f;
						this.dbconn.updatePositions(Integer.toString(this.dbOrdersScriptDetails.get(dc).getButler_record_id()),running_profit_percentage, this.positionScriptDetails.get(pc).getPl(), this.dbOrdersScriptDetails.get(dc).getStoplossModified());
						this.createValueRangeObjectAndAppendScriptRecords(this.positionScriptDetails.get(pc).getSymbol(), this.positionScriptDetails.get(pc).getSide(), this.positionScriptDetails.get(pc).getPl(), this.dbOrdersScriptDetails.get(dc).getPl(), this.dbOrdersScriptDetails.get(dc).getInitialStoploss(), this.dbOrdersScriptDetails.get(dc).getStoplossModified(), mod_stoploss);
						this.butlog.info(this.positionScriptDetails.get(pc).getSymbol()+"("+this.positionScriptDetails.get(pc).getQty()+", "+this.positionScriptDetails.get(pc).getStayOrRemoved()+") - SL HIT or Forced Closed -"+" data updated.");
					}
					else
					{
						
						if(this.dbOrdersScriptDetails.get(dc).getSide().trim().equalsIgnoreCase("Buy"))
						{
							//float total_buy_value=this.buyAvg.get(r)*this.dbqty.get(d);
							//this.running_profit_percentage=((this.pl.get(r))/total_buy_value)*100;
							this.butlog.warn("symbol : "+this.positionScriptDetails.get(pc).getSymbol()+" has BUY order with running net qty of = "+Math.abs(this.positionScriptDetails.get(pc).getQty())+". Hence processing positions.");
							this.butlog.warn("\npl is "+this.positionScriptDetails.get(pc).getPl()+"\ntotal_buy_value is "+total_buy_value+"\nAvg Buy Price is "+this.positionScriptDetails.get(pc).getBuyAug()+"\nRunning profit percentage is "+running_profit_percentage+"%");
							lockin_profit=this.getLockinProfitPercentage(this.positionScriptDetails.get(pc).getPl());
							lockin_profit_percentage=Precision.round(((lockin_profit/total_buy_value)*100), 2);
							this.butlog.warn("lockin profit is "+lockin_profit+". Percentage = "+lockin_profit_percentage);
							if((this.positionScriptDetails.get(pc).getPl()>0.0f) && (lockin_profit>this.dbOrdersScriptDetails.get(dc).getPl()))
							{
								stoploss_increment_points=Precision.round((lockin_profit/this.dbOrdersScriptDetails.get(dc).getQty()),2);
								this.butlog.warn("lockin profit = "+lockin_profit);
								this.butlog.warn("stoploss increment points = "+stoploss_increment_points);
								//this.mod_stoploss_before_round=Precision.round((this.dbstoploss_initial.get(d)+stoploss_increment_points),2);
								mod_stoploss_before_round=Precision.round((this.positionScriptDetails.get(pc).getBuyAug()+stoploss_increment_points),2);
								mod_stoploss=Precision.round(this.closestModifiedStoploss(mod_stoploss_before_round, 0.05f),2);
								this.butlog.info("Modified Stoploss (before rounding) = "+mod_stoploss_before_round+" Modified Stoploss (after rounding) = "+mod_stoploss);
								this.butlog.warn("Initial stoploss = "+this.dbOrdersScriptDetails.get(dc).getInitialStoploss()+" Modified stoploss = "+this.dbOrdersScriptDetails.get(dc).getStoplossModified()+" new stoploss = "+mod_stoploss);
								if((lockin_profit>0.00f) && (mod_stoploss>this.dbOrdersScriptDetails.get(dc).getInitialStoploss()) && (mod_stoploss>this.dbOrdersScriptDetails.get(dc).getStoplossModified()))
								{
									this.modifyOrder(this.dbOrdersScriptDetails.get(dc).getBrokerOrderRefNo(),mod_stoploss, this.dbOrdersScriptDetails.get(dc).getSymbol(), pc);
									this.dbconn.updatePositions(Integer.toString(this.dbOrdersScriptDetails.get(dc).getButler_record_id()),lockin_profit_percentage, lockin_profit, mod_stoploss);
									this.createValueRangeObjectAndAppendScriptRecords(this.positionScriptDetails.get(pc).getSymbol(), this.positionScriptDetails.get(pc).getSide(), this.positionScriptDetails.get(pc).getPl(), lockin_profit, this.dbOrdersScriptDetails.get(dc).getInitialStoploss(), this.dbOrdersScriptDetails.get(dc).getStoplossModified(), mod_stoploss);
									this.butlog.info(this.positionScriptDetails.get(pc).getSymbol()+"("+this.positionScriptDetails.get(pc).getQty()+", "+this.positionScriptDetails.get(pc).getStayOrRemoved()+") - Buy -"+" data updated.");
									
								}
								else
								{
									this.butlog.info(this.positionScriptDetails.get(pc).getSymbol()+"("+this.positionScriptDetails.get(pc).getQty()+", "+this.positionScriptDetails.get(pc).getStayOrRemoved()+") - Buy -"+" has NO profit to lock (OR) Present stop-loss is sufficient.");
								}
							}
							//else if(this.running_profit_percentage<=-1.00f)
						//	{
							//	this.butlog.info("Since "+this.symbol.get(r)+" has Loss of "+this.running_profit_percentage+"%, hence exiting this position.");
							//	this.exitPosition(this.symbol.get(r));
							//	this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.running_profit_percentage, this.pl.get(r), this.mod_stoploss);
							//	this.butlog.info(this.symbol.get(r)+" data updated to database.");
							//}
							else
							{
								this.butlog.info("Since "+this.positionScriptDetails.get(pc).getSymbol()+"("+this.positionScriptDetails.get(pc).getQty()+", "+this.positionScriptDetails.get(pc).getStayOrRemoved()+") - Buy - has running P&L of "+running_profit_percentage+"%, and DB P&L is "+this.dbOrdersScriptDetails.get(dc).getPnlPercentage()+" Either running P&L between (-1) and zero(0) or running P&L < DB P&L. hence doing NOTHING.");
								//this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.running_profit_percentage, this.pl.get(r), this.mod_stoploss);
							}
						}
						else if(this.dbOrdersScriptDetails.get(dc).getSide().equalsIgnoreCase("Sell"))
						{
							this.butlog.warn("symbol : "+this.positionScriptDetails.get(pc).getSymbol()+" has SELL order with running net qty of = "+Math.abs(this.positionScriptDetails.get(pc).getQty())+". Hence processing positions.");
							float total_sell_value=Precision.round((this.positionScriptDetails.get(pc).getSellAvg()*this.dbOrdersScriptDetails.get(dc).getQty()),2);
							running_profit_percentage=Precision.round((((this.positionScriptDetails.get(pc).getPl())/total_sell_value)*100),2);
							this.butlog.warn("\npl is "+this.positionScriptDetails.get(pc).getPl()+"\ntotal_sell_value is "+total_sell_value+"\nAvg Sell Price is "+this.positionScriptDetails.get(pc).getSellAvg()+"\nRunning profit percentage is "+running_profit_percentage+"%");
							lockin_profit=this.getLockinProfitPercentage(this.positionScriptDetails.get(pc).getPl());
							lockin_profit_percentage=Precision.round(((lockin_profit/total_sell_value)*100), 2);
							this.butlog.warn("lockin profit is = "+lockin_profit+" Percentage = "+lockin_profit_percentage);
							if((this.positionScriptDetails.get(pc).getPl()>0.0f) && (lockin_profit>this.dbOrdersScriptDetails.get(dc).getPl()))
							{
								stoploss_increment_points=Precision.round(((lockin_profit)/(this.dbOrdersScriptDetails.get(dc).getQty())),2);
								this.butlog.warn("lockin profit = "+lockin_profit);
								this.butlog.warn("stoploss increment points = "+stoploss_increment_points);
								//this.mod_stoploss_before_round=Precision.round((this.dbstoploss_initial.get(d)+stoploss_increment_points),2);
								//this.mod_stoploss_before_round=Precision.round((this.dbstoploss_initial.get(d)-stoploss_increment_points),2);
								mod_stoploss_before_round=Precision.round((this.positionScriptDetails.get(pc).getSellAvg()-stoploss_increment_points),2);
								mod_stoploss=Precision.round(this.closestModifiedStoploss(mod_stoploss_before_round, 0.05f),2);
								this.butlog.info("Modified Stoploss (before rounding) = "+mod_stoploss_before_round+" Modified Stoploss (after rounding) = "+mod_stoploss);
								this.butlog.warn("Initial stoploss = "+this.dbOrdersScriptDetails.get(dc).getInitialStoploss()+" Modified stoploss = "+this.dbOrdersScriptDetails.get(dc).getStoplossModified()+" new stoploss = "+mod_stoploss);
								//if((lockin_profit>0.00f) && (this.mod_stoploss>this.dbstoploss_initial.get(d)) && (this.mod_stoploss>this.dbstoploss_modified.get(d)))
								if(this.dbOrdersScriptDetails.get(dc).getStoplossModified()==0.00f)
								{
									if((lockin_profit>0.00f) && (mod_stoploss<this.dbOrdersScriptDetails.get(dc).getInitialStoploss()) && ((mod_stoploss<this.dbOrdersScriptDetails.get(dc).getStoplossModified()) || (mod_stoploss>0.00f)))
									{
										this.modifyOrder(this.dbOrdersScriptDetails.get(dc).getBrokerOrderRefNo(),mod_stoploss, this.dbOrdersScriptDetails.get(dc).getSymbol(), pc);
										this.dbconn.updatePositions(Integer.toString(this.dbOrdersScriptDetails.get(dc).getButler_record_id()),lockin_profit_percentage, lockin_profit, mod_stoploss);
										this.createValueRangeObjectAndAppendScriptRecords(this.positionScriptDetails.get(pc).getSymbol(), this.positionScriptDetails.get(pc).getSide(), this.positionScriptDetails.get(pc).getPl(), lockin_profit, this.dbOrdersScriptDetails.get(dc).getInitialStoploss(), this.dbOrdersScriptDetails.get(dc).getStoplossModified(), mod_stoploss);
										this.butlog.info(this.positionScriptDetails.get(pc).getSymbol()+"("+this.positionScriptDetails.get(pc).getQty()+", "+this.positionScriptDetails.get(pc).getStayOrRemoved()+") - Sell -"+" data updated.");
										
									}
									else
									{
										this.butlog.info(this.positionScriptDetails.get(pc).getSymbol()+"("+this.positionScriptDetails.get(pc).getQty()+", "+this.positionScriptDetails.get(pc).getStayOrRemoved()+") - Sell -"+" has NO profit to lock (OR) Present stop-loss is sufficient.");
									}
								}
								else
								{
									if((lockin_profit>0.00f) && (mod_stoploss<this.dbOrdersScriptDetails.get(dc).getInitialStoploss()) && (mod_stoploss<this.dbOrdersScriptDetails.get(dc).getStoplossModified()))
									{
										this.modifyOrder(this.dbOrdersScriptDetails.get(dc).getBrokerOrderRefNo(),mod_stoploss, this.dbOrdersScriptDetails.get(dc).getSymbol(), pc);
										this.dbconn.updatePositions(Integer.toString(this.dbOrdersScriptDetails.get(dc).getButler_record_id()),lockin_profit_percentage, lockin_profit, mod_stoploss);
										this.createValueRangeObjectAndAppendScriptRecords(this.positionScriptDetails.get(pc).getSymbol(), this.positionScriptDetails.get(pc).getSide(), this.positionScriptDetails.get(pc).getPl(), lockin_profit, this.dbOrdersScriptDetails.get(dc).getInitialStoploss(), this.dbOrdersScriptDetails.get(dc).getStoplossModified(), mod_stoploss);
										this.butlog.info(this.positionScriptDetails.get(pc).getSymbol()+"("+this.positionScriptDetails.get(pc).getQty()+", "+this.positionScriptDetails.get(pc).getStayOrRemoved()+") - Sell -"+" data updated.");
									}
									else
									{
										this.butlog.info(this.positionScriptDetails.get(pc).getSymbol()+"("+this.positionScriptDetails.get(pc).getQty()+", "+this.positionScriptDetails.get(pc).getStayOrRemoved()+") - Sell -"+" has NO profit to lock (OR) Present stop-loss is sufficient.");
									}
								}
							}
							//else if(this.running_profit_percentage<=-1.00f)
						//	{
						//		this.butlog.info("Since "+this.symbol.get(r)+" has Loss of "+this.running_profit_percentage+"%, hence exiting this position.");
						//		this.exitPosition(this.symbol.get(r));
						//		this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.running_profit_percentage, this.pl.get(r), this.mod_stoploss);
						//		this.butlog.info(this.symbol.get(r)+" data updated to database.");
						//	}
							else
							{
								this.butlog.info("Since "+this.positionScriptDetails.get(pc).getSymbol()+"("+this.positionScriptDetails.get(pc).getQty()+", "+this.positionScriptDetails.get(pc).getStayOrRemoved()+") - Sell - has running P&L of "+running_profit_percentage+"%, and DB P&L is "+this.dbOrdersScriptDetails.get(dc).getPnlPercentage()+" Either running P&L between (-1) and zero(0) or running P&L < DB P&L. hence doing NOTHING.");
								//this.dbconnection.updatePositions(this.dbbutler_record_id.get(d),this.running_profit_percentage, this.pl.get(r), this.mod_stoploss);
							}
						}
					}
				}
			}
		}
	} 
	private void createValueRangeObjectAndAppendScriptRecords(String symbol, String side, float running_profit, float locked_profit, float initial_stoploss, float modified_stoploss, float new_stoploss)
	{
		this.appendDataToScriptsRecords=new ValueRange();
		Date date =Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm");
		String DateTime = dateFormat.format(date);
		this.appendDataToScriptsRecords.setValues(Arrays.asList(Arrays.asList(DateTime,symbol,side,running_profit,locked_profit,initial_stoploss,modified_stoploss,new_stoploss)));
		this.gsheet.appendSheetValues(this.scriptsRecordsSpreadsheetID, this.scriptRecordsAppendRange, this.appendDataToScriptsRecords);
		this.appendDataToScriptsRecords=null;
	}
	private float closestModifiedStoploss(float a, float b) 
	{
	    float c1 = a - (a % b);
	    float c2 = (a + b) - (a % b);
	    if (a - c1 > c2 - a) {
	        return c2;
	    } else {
	        return c1;
	    }
	}
	private float getLockinProfitPercentage(float running_profit)
	{
		int hour = LocalDateTime.now().getHour();
		float lockin_percentage=0.0f;
		if ((hour>=9)&& (hour<10))
		{
			lockin_percentage=30.00f;
			this.butlog.warn("The Time Hour now is = "+hour+". Hence, locking "+lockin_percentage+"% profits.");
			return (lockin_percentage*running_profit)/100;
		}
		else if ((hour>=10)&& (hour<11))
		{
			lockin_percentage=40.00f;
			this.butlog.warn("The Time Hour now is = "+hour+". Hence, locking "+lockin_percentage+"% profits.");
			return (lockin_percentage*running_profit)/100;
		}
		else if ((hour>=11)&& (hour<12))
		{
			lockin_percentage=50.00f;
			this.butlog.warn("The Time Hour now is = "+hour+". Hence, locking "+lockin_percentage+"% profits.");
			return (lockin_percentage*running_profit)/100;
		}
		else if ((hour>=12)&& (hour<13))
		{
			lockin_percentage=60.00f;
			this.butlog.warn("The Time Hour now is = "+hour+". Hence, locking "+lockin_percentage+"% profits.");
			return (lockin_percentage*running_profit)/100;
		}
		else if ((hour>=13)&& (hour<14))
		{
			lockin_percentage=70.00f;
			this.butlog.warn("The Time Hour now is = "+hour+". Hence, locking "+lockin_percentage+"% profits.");
			return (lockin_percentage*running_profit)/100;
		}
		else if ((hour>=14)&& (hour<15))
		{
			lockin_percentage=80.00f;
			this.butlog.warn("The Time Hour now is = "+hour+". Hence, locking "+lockin_percentage+"% profits.");
			return (lockin_percentage*running_profit)/100;
		}
		else
		{
			lockin_percentage=100.00f;
			this.butlog.warn("The Time Hour now is = "+hour+". Hence, locking "+lockin_percentage+"% profits.");
			return (lockin_percentage*running_profit)/100;
		}
	}
	private void modifyOrder(String id, float stoploss, String symbol, int pc)
	{
		try
		{
			//pc = position counter
			//oc = orderbook counter
			this.butlog.warn("Inside MMMMM");
			for(int oc=0; oc<this.brokerOrdersScriptDetails.size();oc++)
			{
				//if ((this.positionScriptDetails.get(pc).getSymbol().equalsIgnoreCase(this.brokerOrdersScriptDetails.get(oc).getSymbol())) && (this.brokerOrdersScriptDetails.get(oc).getFilledQty()==0) && (this.brokerOrdersScriptDetails.get(oc).getParentId().equalsIgnoreCase(this.positionScriptDetails.get(pc).getId())) && (this.brokerOrdersScriptDetails.get(oc).getStopPrice()>0) && (this.brokerOrdersScriptDetails.get(oc).getStatus().equalsIgnoreCase("Pending")))
				//this.butlog.warn("position Symbol = "+this.positionScriptDetails.get(pc).getSymbol());
				//this.butlog.warn("broker Symbol = "+this.brokerOrdersScriptDetails.get(oc).getSymbol());
				//this.butlog.warn("broker filled Qty = "+this.brokerOrdersScriptDetails.get(oc).getFilledQty());
				//this.butlog.warn("broker parent ID = "+this.brokerOrdersScriptDetails.get(oc).getParentId());
				//this.butlog.warn("position ID = "+this.positionScriptDetails.get(pc).getId());
				//this.butlog.warn("broker stopPrice = "+this.brokerOrdersScriptDetails.get(oc).getStopPrice());
				//this.butlog.warn("broker status = "+this.brokerOrdersScriptDetails.get(oc).getStatus());
				if((this.positionScriptDetails.get(pc).getSymbol().contains(this.brokerOrdersScriptDetails.get(oc).getSymbol())) && (this.brokerOrdersScriptDetails.get(oc).getFilledQty()==0) && (this.brokerOrdersScriptDetails.get(oc).getStopPrice()>0) && (this.brokerOrdersScriptDetails.get(oc).getStatus().equalsIgnoreCase("Pending")))
				{
					this.butlog.warn("Inside modifyOrder biggest IF");
					String request_payload_in_string=
							"{"
									+"\"id\" : "+"\""+this.brokerOrdersScriptDetails.get(oc).getId()+"\"," 
									+"\"stopPrice\" : "+stoploss+
							"}";
					this.butlog.warn(request_payload_in_string);
					List<String> response=this.restconnection.modifyOrder(this.modify_order_url, request_payload_in_string);
					this.butlog.info("Order Updated with Response Code "+response.get(0)+" and message = "+response.get(1));
				}
			}
			
		}
		catch (Exception e)
		{
			this.butlog.error("Error/Exception in method: modifyOrder() in "+this.getClass().toString()+" is=\n"+ExceptionUtils.getStackTrace(e));
			System.gc();
			System.exit(-1);
		}
	}
	private void getBrokerOrderbookAndSaveToBrokerOrdersScriptDetails() throws Exception
	{
		
		this.orderbookResponse=this.restconnection.sendRequest(this.get_orders_url);
		this.responseOfBrokerOrderbook=this.orderbookResponse.body().string();
		//this.butlog.warn(this.responseOfBrokerOrderbook);
		Object object=new Object();
		JSONParser parser=new JSONParser();
		JSONObject jsonobject=new JSONObject();
		object=parser.parse(this.responseOfBrokerOrderbook);
		jsonobject=(JSONObject) object;
		this.flattenedJSONMapForOrderbook=JsonFlattener.flattenAsMap(jsonobject.toString());
		//flattenedJSONMap.forEach((k,v) -> this.butlog.warn(k+" : "+v));
		Iterator<Map.Entry<String, Object>> itr =this.flattenedJSONMapForOrderbook.entrySet().iterator();
		while(itr.hasNext())
		{
			Map.Entry<String, Object> entry=itr.next();
			if(entry.getKey().contains(".symbol"))
			{
				sizeOfOrderbook++;
				//this.butlog.warn(entry.getKey());
			}
		}
		this.butlog.warn("size of Broker Orderbook is ="+sizeOfOrderbook);
			
		for (int i=0; i<sizeOfOrderbook; i++)
		{
			this.brokerOrdersScriptDetails.add(i, new BrokerOrdersScriptDetails());
			this.brokerOrdersScriptDetails.get(i).setId(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].id").toString().trim());
			this.brokerOrdersScriptDetails.get(i).setExchOrdId(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].exchOrdId").toString().trim());
			this.brokerOrdersScriptDetails.get(i).setSymbol(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].symbol").toString().trim().split("NSE:|-EQ")[1]);
			this.brokerOrdersScriptDetails.get(i).setMessage(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].message").toString().trim());
			this.brokerOrdersScriptDetails.get(i).setSegment(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].segment").toString().trim());
			this.brokerOrdersScriptDetails.get(i).setOrderValidity(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].orderValidity").toString().trim());
			this.brokerOrdersScriptDetails.get(i).setOrderDateTime(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].orderDateTime").toString().trim());
			this.brokerOrdersScriptDetails.get(i).setOrderNumStatus(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].orderNumStatus").toString().trim());
			this.brokerOrdersScriptDetails.get(i).setFyToken(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].fyToken").toString().trim());
			this.brokerOrdersScriptDetails.get(i).setInstrument(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].instrument").toString().trim());
			this.brokerOrdersScriptDetails.get(i).setSource(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].source").toString().trim());
			this.brokerOrdersScriptDetails.get(i).setProductType(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].productType").toString().trim());
			this.brokerOrdersScriptDetails.get(i).setQty(Integer.parseInt(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].qty").toString().trim()));
			this.brokerOrdersScriptDetails.get(i).setRemainingQuantity(Integer.parseInt(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].remainingQuantity").toString().trim()));
			this.brokerOrdersScriptDetails.get(i).setFilledQty(Integer.parseInt(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].filledQty").toString().trim()));
			this.brokerOrdersScriptDetails.get(i).setStatus(Integer.parseInt(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].status").toString().trim()));
			this.brokerOrdersScriptDetails.get(i).setSlNo(Integer.parseInt(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].slNo").toString().trim()));
			this.brokerOrdersScriptDetails.get(i).setType(Integer.parseInt(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].type").toString().trim()));
			this.brokerOrdersScriptDetails.get(i).setSide(Integer.parseInt(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].side").toString().trim()));
			this.brokerOrdersScriptDetails.get(i).setDisclosedQty(Integer.parseInt(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].discloseQty").toString().trim()));
			this.brokerOrdersScriptDetails.get(i).setDqQtyRem(Integer.parseInt(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].dqQtyRem").toString().trim()));
			this.brokerOrdersScriptDetails.get(i).setLimitPrice(Float.parseFloat(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].limitPrice").toString().trim()));
			this.brokerOrdersScriptDetails.get(i).setStopPrice(Float.parseFloat(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].stopPrice").toString().trim()));
			this.brokerOrdersScriptDetails.get(i).setTradedPrice(Float.parseFloat(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].tradedPrice").toString().trim()));
			this.brokerOrdersScriptDetails.get(i).setOfflineOrder(Boolean.parseBoolean(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].offlineOrder").toString().trim()));
			if(this.flattenedJSONMapForOrderbook.containsKey("orderBook["+i+"].parentId"))
			{
				this.brokerOrdersScriptDetails.get(i).setParentId(this.flattenedJSONMapForOrderbook.get("orderBook["+i+"].parentId").toString().trim());
			}
			else
			{
				this.brokerOrdersScriptDetails.get(i).setParentId("Parent ID not available");
			}
		}
		//this.butlog.info("\tid\texchOrdId\tsymbol\tqty\tremainingQuantity\tfilledQty\tstatus\tslNo\tmessage\tsegment\tlimitPrice\tstopPrice\ttype\tside\tdisclosedQty\torderValidity\torderDateTime\tparentId\ttradedPrice\torderNumStatus\tdqQtyRem\tfyToken\tinstrument\tsource\tofflineOrder\tproductType");
		//for (int i=0; i<this.sizeOfOrderbook; i++)
		//{
		//	this.butlog.info(this.brokerOrdersScriptDetails.get(i).getId()+"\t"+this.brokerOrdersScriptDetails.get(i).getExchOrdId()+"\t"+this.brokerOrdersScriptDetails.get(i).getSymbol()+"\t"+this.brokerOrdersScriptDetails.get(i).getQty()+"\t"+this.brokerOrdersScriptDetails.get(i).getQty()+"\t"+this.brokerOrdersScriptDetails.get(i).getRemainingQuantity()+"\t"+this.brokerOrdersScriptDetails.get(i).getFilledQty()+"\t"+this.brokerOrdersScriptDetails.get(i).getStatus()+"\t"+this.brokerOrdersScriptDetails.get(i).getSlNo()+"\t"+this.brokerOrdersScriptDetails.get(i).getMessage()+"\t"+this.brokerOrdersScriptDetails.get(i).getSegment()+"\t"+this.brokerOrdersScriptDetails.get(i).getLimitPrice()+"\t"+this.brokerOrdersScriptDetails.get(i).getStopPrice()+"\t"+this.brokerOrdersScriptDetails.get(i).getType()+"\t"+this.brokerOrdersScriptDetails.get(i).getSide()+"\t"+this.brokerOrdersScriptDetails.get(i).getDisclosedQty()+"\t"+this.brokerOrdersScriptDetails.get(i).getOrderValidity()+"\t"+this.brokerOrdersScriptDetails.get(i).getOrderDateTime()+"\t"+this.brokerOrdersScriptDetails.get(i).getParentId()+"\t"+this.brokerOrdersScriptDetails.get(i).getTradedPrice()+"\t"+this.brokerOrdersScriptDetails.get(i).getOrderNumStatus()+"\t"+this.brokerOrdersScriptDetails.get(i).getDqQtyRem()+"\t"+this.brokerOrdersScriptDetails.get(i).getFyToken()+"\t"+this.brokerOrdersScriptDetails.get(i).getInstrument()+"\t"+this.brokerOrdersScriptDetails.get(i).getSource()+"\t"+this.brokerOrdersScriptDetails.get(i).isOfflineOrder()+"\t"+this.brokerOrdersScriptDetails.get(i).getProductType());			
		//}
		int rowcounter=0;
		for (int i=0; i<this.sizeOfOrderbook;i++)
		{
			rowcounter=i+2;
			this.data.add(new ValueRange().setRange("A"+rowcounter).setValues(Arrays.asList(Arrays.asList(this.brokerOrdersScriptDetails.get(i).getId(),this.brokerOrdersScriptDetails.get(i).getExchOrdId(), this.brokerOrdersScriptDetails.get(i).getSymbol(), this.brokerOrdersScriptDetails.get(i).getQty(), this.brokerOrdersScriptDetails.get(i).getRemainingQuantity(), this.brokerOrdersScriptDetails.get(i).getFilledQty(), this.brokerOrdersScriptDetails.get(i).getStatus(), this.brokerOrdersScriptDetails.get(i).getSlNo(), this.brokerOrdersScriptDetails.get(i).getMessage(), this.brokerOrdersScriptDetails.get(i).getSegment(), this.brokerOrdersScriptDetails.get(i).getLimitPrice(), this.brokerOrdersScriptDetails.get(i).getStopPrice(), this.brokerOrdersScriptDetails.get(i).getType(), this.brokerOrdersScriptDetails.get(i).getSide(), this.brokerOrdersScriptDetails.get(i).getDisclosedQty(), this.brokerOrdersScriptDetails.get(i).getOrderValidity(), this.brokerOrdersScriptDetails.get(i).getOrderDateTime(), this.brokerOrdersScriptDetails.get(i).getParentId(), this.brokerOrdersScriptDetails.get(i).getTradedPrice(), this.brokerOrdersScriptDetails.get(i).getOrderNumStatus(), this.brokerOrdersScriptDetails.get(i).getDqQtyRem(), this.brokerOrdersScriptDetails.get(i).getFyToken(), this.brokerOrdersScriptDetails.get(i).getInstrument(), this.brokerOrdersScriptDetails.get(i).getSource(), this.brokerOrdersScriptDetails.get(i).isOfflineOrder(), this.brokerOrdersScriptDetails.get(i).getProductType()))));
		}
		this.gsheet.updatePendingPositionHandlerSheet(this.pending_position_handler_spreadsheetId, this.data);	
	}
	/*private int exitPosition(String symbol)
	{
		List<String> list_of_string=new ArrayList<String>();
		String request_payload_in_string=
				"{"	 
						+"\"id\" : \"NSE:"+symbol+"-EQ-"+this.default_productType+"\""+ 
				"}";
		list_of_string=this.restconnection.exitPosition(this.exit_positions_url, request_payload_in_string);
		this.butlog.info(symbol+" exited with response code = "+list_of_string.get(0)+" and message = "+list_of_string.get(1));
		return Integer.parseInt(list_of_string.get(0));
	}*/
	public static void main(String args[])
	{
		PendingPositionsHandler pending_positions_handler=new PendingPositionsHandler();
		pending_positions_handler.handlePendingPositions();
		pending_positions_handler.close();
		pending_positions_handler=null;
		System.out.println("Collecting Garbage");
		System.gc();
	}
}
