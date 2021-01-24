package ButlerToken;

public class Test 
{
	public static void main(String... args)
	{
		String symbol="NSE:BANKBARODA-EQ";
		symbol=symbol.replaceAll("NSE:", "");
		symbol=symbol.replaceAll("-EQ", "");
		System.out.println(symbol);
		System.out.println("\"id\" : "+"\""+"kuch-bhi"+"\"," );
	}
}
