import java.util.*;
import java.lang.*;
import java.io.*;
import java.text.*;

class time 
{
	public static void main( String args[] ) throws IOException
	{
		String filename = "file.txt" ;
		BufferedReader reader =  new BufferedReader ( new FileReader ( filename ) );
		String line = "";
		String lineR = "";
		int bytes;
		
		while((line = reader.readLine()) != null)
		{
			lineR = lineR+line;
		}
		line = lineR;
		int text_len = lineR.length();
		String[] s = new String[(text_len/576)+1] ;
		int count=0;
		for(int j=0; j <( text_len/576); j++)
		{
			System.out.println("j="+j);
			s[j] = lineR.substring(0,576);
			lineR = lineR.substring(576,lineR.length());
		}
		s[(text_len/576)] = lineR;
	}
}