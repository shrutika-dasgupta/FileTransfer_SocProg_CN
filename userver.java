import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.text.*;
 
class userver
{
	public static void main(String args[]) throws Exception
	{
		int clientNumber =0;
		

		
		//Create a welcome socket
		ServerSocket welcome = new ServerSocket(4924);

		
		

		
		try
		{

		
			while(true)
			{ 

				new SerThread(welcome.accept(),clientNumber++).start();

			}
		}
		finally
		{
			welcome.close();
		}
		
		
	}
}

class SerThread extends Thread
{
	
	Socket myServer;
	int clientNumber;
	int attempts=0;
	String filename="log.txt";
	String actuser="";
	String active="";
	String uname="";
	String users="";
	String dateU="";
	String line = null;
	String line2=null;
	String line3=null;
	String line4=null;
	Date dNow = new Date();
	String rep="";

	SimpleDateFormat ft = new SimpleDateFormat(" MM.dd.yyyy-hh.mm.ss");
	int ch;

	BufferedReader inFromClient;
	PrintWriter toClient;
	BufferedReader fileR;
	BufferedWriter file;




	String[] list={
		"Columbia 116bway",
		"SEAS summerisover",
		"csee4119 lotsofexams",
		"foobar passpass",
		"windows withglass",
		"Google hasglasses",
		"facebook wastingtime",
		"wikipedia donation",
		"network seemsez"};

		String ans;
		String authInput;
		int i;
		int flag=0;
		int c=0;
		int off=0;

	SerThread(Socket socket,int clientNumber)
	{
		myServer=socket;
		this.clientNumber = clientNumber;
	}

	public void run()
	{
		try
		{
		//create an  object for taking input from the client
			inFromClient = new BufferedReader(new InputStreamReader(myServer.getInputStream()));
			

			//Create an oject to send the output to the client
			toClient = new PrintWriter(myServer.getOutputStream(),true);

			file = new BufferedWriter( new FileWriter(filename,true));

			fileR = new BufferedReader(new FileReader(filename));




			while(attempts !=3 && flag!=1)
			{

				//Store the input from the client in authInput variable
				authInput = inFromClient.readLine();
			
				//To compare the input username and password with one in the list[]



				for(i=0;i<9;i++)
				{
					if(authInput.equals(list[i]))  //check if the username and password is valid or not
					{
						flag=1;
					}		
				}
				
 				if (flag==1)
 				{
 					
 					StringTokenizer tokens =new StringTokenizer(authInput," ");
					uname = tokens.nextToken();
					


 					while((line=fileR.readLine()) != null)
 					{
 						
 						String[] check = line.split(" ");
 						if( check[0].equals(uname) && check[1].equals("active"))
 						{
 							//check if the user is already present and logged in
 							c=1;
 							break;
 						}
 					}
 					fileR.close();
 					if(c==1)
 					{
 						
 						toClient.println("a");
 					}
 					else
 					{
 						ans="c";
 						toClient.println(ans);
 						
 						System.out.println("the clientNumber is:"+clientNumber);
 					
 						// write into the "log.txt" file
 						file.write(uname+" ");
 						file.write("active");
 						file.write(" "+clientNumber);
 						file.write(ft.format(dNow));
 						file.newLine();
 						file.flush();
 						file.close();
 						
 						

 					}

 					
 					
 				}
 				else
 				{
 					
 						ans="w";
 						attempts++;

 						toClient.println(ans);

 					 
 				}
 			}
 			
 		do
 		{
 			ch=Integer.parseInt(inFromClient.readLine());
 			
 			
 			switch(ch)
 			{
 				case 1:
 					toClient.flush();
 					active = whoelse(uname); //check for the active status of the client
 					
 				
 					toClient.println(active);
 				
 					break;

 				case 2:
 					fileR = new BufferedReader(new FileReader(filename));
 					while((line3=fileR.readLine()) !=null)
 					{
 						try
 						{
 							if( line3.contains(" active"))
 							{
 								String disUser[] = line3.split(" ");
 							
 						
 							
 							
 								String covDate=wholast(disUser[3], disUser[0]); //check for the last login and time stamp of the user clients
 							
 								if(covDate !=null)
 								dateU=dateU+" "+covDate;
 							}
 						}
 						catch(Exception e)
 						{
 							
 							System.out.println(e);
 						}
					}
 					fileR.close();
 					toClient.println(dateU);
 					break;

 				case 3:
 				String no ="nothing";
 				
 					break;

 				case 4: //logging of for the logged in user
 	
 					fileR = new BufferedReader(new FileReader(filename));
 					while((line4=fileR.readLine()) != null)
 					{
 						
 						try
 						{
 							if(line4.contains(uname))
 							{
 								
 								rep=line4; //
 								
 								
 								fileR.close();
 								break;

 							}
						}
 						catch(Exception e)
 						{
 							System.out.println(e);
 						}
 						
 					}
 					rep=rep.replace("active","deactive"); //check if the user is active or deactive
 					
 					file = new BufferedWriter( new FileWriter(filename,true));
 							file.write(rep);
 							file.newLine();
 							
 							file.flush();

 					file.close();
 					off=1;
 					toClient.println(off);
 					break;


 			}
 		}while((inFromClient.readLine()).equals("yes"));

 			

 			//myServer.close();

		}
		catch(IOException e)
		{
			System.out.println("Error handling client #:"+ clientNumber + ":"+e);
			e.printStackTrace();
		}
		
		
	}

	String whoelse(String urname) throws IOException 
	{	
		//function that performs the WHOELSE funtionality
		
		String username = urname;
		
		String actuser="";
		fileR = new BufferedReader(new FileReader(filename));


		while((line2=fileR.readLine()) != null)
		{
		
			if(line2.contains(" active") && !line2.contains(username))
			{
				String name[]=line2.split(" ");
				actuser=actuser+" "+name[0];

			}
		}
		fileR.close();
		
		
		
		
		
		
	
		
		return actuser;
	}

	String wholast (String uDate, String username) throws ParseException
	{ //function checking for the user activity.

		Date cD = new Date();
	
		SimpleDateFormat currD = new SimpleDateFormat("MM.dd.yyyy-hh.mm.ss");
		String d1 = currD.format(cD);
		Date dcurr = currD.parse(d1);
	

		Date usrdate = new SimpleDateFormat("MM.dd.yyyy-hh.mm.ss").parse(uDate);
		System.out.println(usrdate);

		long diff = dcurr.getTime() - usrdate.getTime();
		long diffMinutes = diff / (60 * 1000);
		long difr=Math.abs(diffMinutes);
		
		String conv=String.valueOf(difr);
		if(difr<=60)
			return (username+"-("+difr+".minutes)");
		else 
			return null;
	}

	
}


