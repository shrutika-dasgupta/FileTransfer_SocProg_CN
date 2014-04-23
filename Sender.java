import java.net.*;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

class TCPHeader implements Serializable
{
	short sourcePort;
	short destPort;
	int ackNo;
	int seqNo;
	byte flag;
	short window_Size;
	short checksum;
	String payload;

	TCPHeader()
	{
		sourcePort = 0;
		destPort = 0 ;
		ackNo = 0;
		seqNo = 0;
		flag = 0;
		window_Size = 1;
		checksum = 0;
		payload = "NULL";
	}

	TCPHeader(short sp, short dp, int ack_no, int seq_no, byte f, short w_s, short c, String pay)
	{
		sourcePort = sp;
		destPort = dp;
		ackNo = ack_no;
		seqNo = seq_no;
		flag = f;
		window_Size = w_s;
		checksum = c;
		payload = pay;
	}
	void print()
	{
		System.out.println(sourcePort+":"+destPort+":"+ackNo+":"+seqNo+":"+flag+":"+window_Size+":"+checksum+":"+payload);
	}
}

class Sender
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
		Sender.attachShutDownHook();
		try
		{
			//validating the arguments;
			if(args.length < 6 )
			{
				System.out.println("Incomplete input arguments!");
			}
			else if( args.length > 6 )
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
					if( !(args[1].matches("^[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}$|^(localhost)$")) )
					{
						System.out.println("Invalid IpAddress.");
					}
					else
					{
						if( !( args[2].matches("^[0-9]+$")))
						{
							System.out.println("Invalid Reciever Port Number.");
						}
						else
						{
							if(!( args[3].matches("^[0-9]+$")) )
							{
								System.out.println("Invalid Sender Port Number.");
							}
							else
							{
								if( !(args[4].matches("^[0-9]+$")) )
								{
									System.out.println("Invalid Window Size.");
								}
								else
								{
									if( !(args[5].matches("^[a-zA-Z0-9.]+$")))
									{
										System.out.println("Invalid logfile name.");
									}
									else
									{
										DatagramSocket sender_UDP = new DatagramSocket( Integer.parseInt( args[3] ) );				//create a socket for the sender with port: 5000
										
										ServerSocket welcome = new ServerSocket( Integer.parseInt( args[3] ));
										
										Socket sender_TCP = welcome.accept();

										BufferedReader received_packet;
										PrintWriter to_receiver = new PrintWriter( sender_TCP.getOutputStream(), true);

										InetAddress ipRecv = InetAddress.getByName("localhost" );
										int receiver_port = Integer.parseInt( args[2] );								//receiver port: 4000
										int sender_port = Integer.parseInt( args[3] );
										String filename = args[0] ;
										String logfile = args[5];
										short window_Size = Short.parseShort( args[4] );
										
										byte[] byte_send_Packet = new byte [1024];			//bytes for the packets to be sent
										byte[] byte_received_Packet = new byte [1024];		//bytes for the acknowledgement to tbe recieved
										int length_send_packet;								//length of the line to be sent
										String ack_from_receiver="";						//string to store the ack received from the receiver
										DatagramPacket send_packet;							//packet for sending the file
										//DatagramPacket received_packet;						//packet for the acknowlegement

										System.out.println("1");


										System.out.println("2: send");
										//sender.connect (ipRecv, receiver_port);											//making connection with the receiver
										BufferedReader reader =  new BufferedReader ( new FileReader ( filename ) ); 	// for reading the file
										System.out.println("3");

										ByteArrayOutputStream baos = new ByteArrayOutputStream();
										ObjectOutputStream oos = new ObjectOutputStream(baos);

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

										int len_s = s.length;
										String no_of_packets = Integer.toString(len_s);

										System.out.println("no_of_packets:"+ no_of_packets);
										to_receiver.println(no_of_packets);
										TCPHeader t = new TCPHeader();

										for(int i=0; i<s.length; i++)
										{
											System.out.println("inside for 1");
											t.print();
											//t[i] = new TCPHeader( (short)sender_port, (short)receiver_port, 0, i,(byte)1 , (short)1, (short)0, s[i]  );
											t.sourcePort = (short)sender_port;
											
											t.destPort = (short)receiver_port;
											t.ackNo = 0;
											t.seqNo = i;
											t.flag = (byte)1;
											t.window_Size = (short)1;
											t.checksum = (short)0;
											t.payload = s[i];
											oos.writeObject(t);
											oos.flush();

											byte_send_Packet = baos.toByteArray();

											length_send_packet = byte_send_Packet.length;
											System.out.println("TCPHeader size send:"+length_send_packet);
											send_packet = new DatagramPacket ( byte_send_Packet, length_send_packet, ipRecv, receiver_port );
											sender_UDP.send(send_packet);
											Thread.sleep(100);
										
										//sender.disconnect();

										//sender.connect (ipRecv, receiver_port);
										
											System.out.println("while receive ack");
											if(ack_from_receiver != null)
											{
												received_packet = new BufferedReader( new InputStreamReader( sender_TCP.getInputStream() ) );
												
												ack_from_receiver = received_packet.readLine();

												//received_packet = new DatagramPacket( byte_received_Packet, byte_received_Packet.length, ipRecv, receiver_port);
												//sender.receive( received_packet );
												//ack_from_receiver = new String( received_packet.getData() );
												System.out.println("FROM THE SENDER: "+ack_from_receiver );
											}
											else
											{
												System.out.println("THE ACKNOWLEDGEMENT NOT RECIEVED.");
												break;
											}
										}
										//sender.disconnect();
										sender_UDP.close();
										reader.close();
									}
								}
							}
						}
										
					}
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception:"+e);
			e.printStackTrace();
		}
		
	/*	catch(FileNotFoundException e)
		{
			System.out.println("File is not found.");
		}
	*/	
	}
}