package test;

import java.io.BufferedReader;
//import java.io.IOException;
import java.io.InputStreamReader;
//import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class NSEDataDownload 
{
	public static void main(String[] args) {

        URL url;

        try {
            // get URL content

            String a="https://www.nseindia.com/api/equity-stockIndices?index=NIFTY%2050";
            url = new URL(a);
            URLConnection conn = url.openConnection();
            System.out.println("Before Buffered Reader");
            // open the stream and put it into BufferedReader
            BufferedReader br = new BufferedReader(
                               new InputStreamReader(conn.getInputStream()));

            String inputLine;
            System.out.println("Going in while loop");
            while ((inputLine = br.readLine()) != null) {
                    System.out.println(inputLine);
            }
            br.close();
            System.out.println("Done");

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        } 
    }
}
