import java.net.*;
import java.lang.*;
import java.util.*;
import java.io.*;

class Receiver
{
	public static void attachShutDownHook()
	{
  		Runtime.getRuntime().addShutdownHook(new Thread() 
  		{
   			public void run() {
    		System.out.println("Inside Add Shutdown Hook");
   			}
   		});
  	}
	public static void main ( String args[] ) throws IOException
	{
		Receiver.attachShutDownHook();
		try
		{
			//validating the arguments;
			if(args.length < 5 )
			{
				System.out.println("Incomplete input arguments!");
			}
			else if( args.length > 5 )
			{
				System.out.println(" invalid input arguements!");
			}
			else
			{
				if( !(args[0].matches("^[a-zA-Z0-9.]+$")) )
				{
					System.out.println("Invalid filename.");
				}
				else
				{
					if( !(args[1].matches("^[0-9]+$")) )
					{
						System.out.println("Invalid Reciever Port Number.");
					}
					else
					{
						if( !( args[2].matches("^[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}$|^(localhost)$")))
						{
							System.out.println("Invalid IpAddress.");
						}
						else
						{
							if(!( args[3].matches("^[0-9]+$")) )
							{
								System.out.println("Invalid Sender Port Number.");
							}
							else
							{
								if( !(args[4].matches("^[a-zA-Z0-9.]+$")) )
								{
									System.out.println("Invalid logfile name.");
								}
								else
								{
									DatagramSocket receiver_UDP = new DatagramSocket( Integer.parseInt( args[1] ));				//receiver port : 4000

									InetAddress ipSend = InetAddress.getByName( "localhost" );									//sender IP address : "LOCALHOST"
									int sender_port = Integer.parseInt( args[3] );											//sender port :5000
									

									Socket receiver_TCP = new Socket (ipSend, sender_port );

									PrintWriter send_packet = new PrintWriter( receiver_TCP.getOutputStream(), true);

									BufferedReader from_sender = new BufferedReader( new InputStreamReader( receiver_TCP.getInputStream()));

									String logfile = args[4];
									String filename = args[0];
									String file_line = "";

									byte[] byte_send_packet = new byte [1024];												//bytes assigned for ack.
									byte[] byte_received_packet = new byte [1024];											//bytes assigned for recieved packets
									//DatagramPacket send_packet;							//sending ack +packet
									DatagramPacket received_packet;						// packet recieved.
									String recv_line = "";								//line in recieved file
									String ack_packet = "";								// ack_line 
									File file = new File( filename );					// to open file 

									int no_of_packets = Integer.parseInt(from_sender.readLine());
									System.out.println("no_of_packets:"+no_of_packets);


									//receiver.connect( ipSend, sender_port);																					//make connection with the sender
									received_packet = new DatagramPacket( byte_received_packet, byte_received_packet.length ); 		//create a datagrampacket for receiving packets from the sender.							
									int x=0;
									TCPHeader t =new TCPHeader();
									while(true)
									{
										if(recv_line != null)
										{
											receiver_UDP.receive(received_packet);														//receive packet from the sender
											ByteArrayInputStream bais = new ByteArrayInputStream( byte_received_packet );
											ObjectInputStream ois = new ObjectInputStream( bais );
											t = (TCPHeader)ois.readObject();
											t.print();
										//	recv_line = new String( received_packet.getData() );									//extract data from the packet and store it in recv_line
										//	System.out.println("THE TEXT IN THE FILE:"+(++x)+":"+ recv_line );								//verify the text sent
											System.out.println("ACKNOWLEDGEMENT SENT:"+(++x));
											ack_packet = "ACKNOWLEDGEMENT "+(x)+" RECEIVED";										//prepare to send the ack
											//byte_send_packet = ack_packet.getBytes(); 												// prepare th ack packet to send
											System.out.println("TCPHeader size:"+byte_received_packet.length);

											send_packet.println(ack_packet);

											//send_packet = new DatagramPacket( byte_send_packet, byte_send_packet.length, ipSend, sender_port);
											//receaiver.send( send_packet );															// send the packet.
										}
										else
										{
											System.out.println(" Packet: "+x+" is not received.");
											break;
										}
									}
									//receiver.disconnect();
									receiver_UDP.close();
									
								}
							}
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("EXCEPTION:"+ e);
			e.printStackTrace();
		}
	}
}