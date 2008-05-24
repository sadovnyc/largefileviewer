package test;

import java.io.UnsupportedEncodingException;

public class StringBytesTest {

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String prova = "こんにちは";
		byte[] provaBytes;
		try {
			provaBytes = prova.getBytes("UTF-8");
			for (byte b : provaBytes) {
				System.out.print( (char) b );
			}
			System.out.println( );
		} catch (UnsupportedEncodingException e) {
			System.out.println("Could not convert to UTF-8");
		}
	}

}
