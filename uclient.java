import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;

class uclient
{
	public static void main(String args[]) throws IOException
	{	
		InetAddress ip= InetAddress.getByName(args[0]); 	// main program that starts execution.
		new Soc_T(ip);		//creating a new thread
		
	}
}

class Soc_T extends Thread
{
	InetAddress ip;		// IP address of the server is taken as input from the command prompt.
	int attempts=0;		// keep track of the number of attempts
	int left=3;
	int in=0;
	int ch;
	String active=""; 	//checking the active statusof the client
	int toff=0;
	boolean quit= false;

	Soc_T(InetAddress ipd)
	{
		ip =ipd;
		System.out.println("Thread created");
		start(); 		//start of the thread process
	}
		
	public void run()
	{
		try
		{
			while(toff !=1)
			{
				/*Input from user*/
				BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));	

				/*Create socket for client*/
				Socket myClient = new Socket (ip,4924);		
		

				/* Recieve from the Server*/
				BufferedReader fromServer = new BufferedReader(new InputStreamReader(myClient.getInputStream())); 

				/*Create a recieving object from the Server*/
				PrintWriter toServer = new PrintWriter(myClient.getOutputStream(),true);	

				String username="";
				String password="";
				String unp="";
				do
				{

					/*Get the username and password information from the user*/
					System.out.println("Enter Username:");
					username=fromUser.readLine();
					System.out.println("Enter Password:");
					password=fromUser.readLine();

					unp=username+" "+password;
		

					/*Send the Username and Password info to server to verfy*/
					toServer.println(unp);		
		

					/*store the input recieved from the ser in the answer variable*/
					String ans = fromServer.readLine();
				

					if(ans.equals("c"))
					{ //check if the username and password is valid and the user is not already logged in
						System.out.println("______________________");
						System.out.println("     Welcome user");
						System.out.println("______________________");
						in=1;
						break;
					}
					else if(ans.equals("a"))
					{ 	//check if user is already logged in or not
						System.out.println("the user has already logged in from this username cannot grant access.\n");
						break;
					}
					else 
					{
						System.out.println("Incorrect Id.. Try again\n");
						attempts++;
						left--;
						System.out.println("Number of attempts left:"+left);
						System.out.println("");

						if(left==0)
						{
							System.out.println("Sorry try after 60 seconds\n");
							Thread.sleep(60000);  //blocking the user for 60 secs
							left=3;
						}
					}
				}
				while(attempts !=3);

				
			
				if(in==1)
				{
					do
					{
						System.out.println("________________________________");
						System.out.println("Perform the following functions:");
						System.out.println("________________________________");
						System.out.println("1.Check WHO ELSE is online.");
						System.out.println("2. Display the users logged on to the server in the 1 HOUR (WHO LAST THERE).");
						System.out.println("3. Broadcast a message to to all the clients who are logged on");
						System.out.println("4. Log out.");
						System.out.println("0.Exit.");
						System.out.println("Enter your choice:");
						System.out.println("________________________________");
						ch=Integer.parseInt(fromUser.readLine());
				

						switch(ch)
						{
							case 1:	//checking for who else has logged in and is active at the moment
					
							toServer.println(ch);
					

							active = fromServer.readLine();
					
							System.out.println("The number of users active are:");
							System.out.println("________________________________");
					
							StringTokenizer st = new StringTokenizer(active);
    						while (st.hasMoreTokens()) 
    						{
         						System.out.println(st.nextToken());
     						}

							if(active==null)
							{
								System.out.println("No users are active.");

							}
							System.out.println("________________________________");
							break;

							case 2: //checking who all have performed any activity for the past 1 hour
							toServer.println(ch);
							String wholast = fromServer.readLine();
							System.out.println("The number of users who were logged on from the past one hour are:");
							System.out.println("________________________________");
					
							StringTokenizer dl = new StringTokenizer(wholast);
    						while (dl.hasMoreTokens()) 
    				 		{
    				 	
         						System.out.println(dl.nextToken());
         					}

							if(wholast==null)
							{
								System.out.println("No users are active.");
							}
							System.out.println("________________________________");
							break;

							case 3:
							toServer.println(ch);
							break;

							case 4: //logging out for that user
 							toServer.println(ch);
 							String off;
 							if ((off=fromServer.readLine()).equals("1"))
 							{
 								toff=1;
 								System.out.println("________________________________");
 								System.out.println("the User has Logged out successfully");
 								System.out.println("________________________________");
 							}
 							else
 							{
 								System.out.println("Deactivation error occurred!");
 							}
 							break;
 							case 0:
 							quit=true;
 							break;

 							default:
 								System.out.println("Invalid Choice.");

 						}
 						if(ch !=0 )
 						{
 					
 							toServer.println("yes"); //keep taking inputs from the user for the functionalities

 						}

					}
					while(ch !=0 && ch !=4);

				
				
				}	
				//myClient.close();
			}	
		}	
		catch(Exception e)
			{System.out.println("Exception:" +e);}
	}
}