//import java.net.URL;
//import java.net.URLConnection;
//import java.math.BigDecimal;

import org.apache.commons.math3.util.Precision;

public class Test 
{
	public static void main(String... args)
	{
		String symbol = "NSE:TCS-EQ";
		System.out.println("Symbol is = "+symbol.split("NSE:|-EQ")[1]);
		
	/*	try
		{
			final String path="B:\\Authorization\\authorization.html";
			//final String path="/home/ubuntu/butler-system/butler-additional-resources/authorization.html";
			System.out.println("path = "+path);
			//URL myurl=new URL(path);
			//URLConnection con=myurl.openConnection();
			//con.connect();
			//System.out.println("Connected = "+con.toString());
			float d=1254.5754f;
			float fd=Precision.round(d, 2);
			System.out.println("before format = "+d+" after format = "+fd);
			float decimal=fd-(int)fd;
			decimal=Precision.round(decimal,2);
			System.out.println(decimal+"fd%(int)fd = "+fd%(int)fd);
			System.out.println(fd);
			String symbol="BANKINDIA";
			String request_payload_in_string=
					"{"	 
							+"\"id\" : \"NSE:"+symbol+"-EQ-BO\""+ 
					"}";
			System.out.println(request_payload_in_string);
			//BigDecimal bd=new BigDecimal(String.valueOf(fd));
			//System.out.println(closestInteger(1254.73f,0.05f));
			/*int n=1254;
			float i=0.00f;
			float val;
			float nval;
			do
			{
				val=n+i;
				nval=closestInteger(val,0.05f);
				System.out.println("value = "+val+" closest = "+Precision.round(nval, 2));
				i=i+0.01f;
			}while(i<=0.99f);*/
			
	/*	}
		catch (Exception e)
		{
			e.printStackTrace();
		} */ 
		
	}
	static float closestInteger(float a, float b) {
	    float c1 = a - (a % b);
	    float c2 = (a + b) - (a % b);
	    if (a - c1 > c2 - a) {
	        return c2;
	    } else {
	        return c1;
	    }
	}
}
