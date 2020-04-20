import java.io.*; 
import java.util.*;
import java.net.*;
import java.lang.Math; 

class TrafficGenerator_Video {

    public static void main (String[] args) {

        //Params for reading/writing files
        BufferedReader bis = null; 
		String currentLine = null; 
        PrintStream pout = null;

        //Max UDP size 65,507 
        int MAX_PACKET_SIZE = 1024; //Using reference solution

        long fixedDelay = 33333; //~33 us

        int numberOfFrames = 1000;

        try 
        {
			/*
			 * Open input file as a BufferedReader
			 */ 
			File fin = new File("movietrace.data"); 
			FileReader fis = new FileReader(fin);  
            bis = new BufferedReader(fis); 
            
            //Set Up Address
            InetAddress addr = InetAddress.getByName("128.100.13.177");
            DatagramSocket socket = new DatagramSocket();
            
			/*
			 * Open file for output 
			 */
			FileOutputStream fout =  new FileOutputStream("output_video_sender.txt");
            pout = new PrintStream (fout);         

            //long startTime = System.nanoTime(); //Time to execute
            while (numberOfFrames > 0 && (currentLine = bis.readLine()) != null) {

                numberOfFrames--;
				
				/*
				 *  Parse line and break up into elements 
				 */
                StringTokenizer st = new StringTokenizer(currentLine); 
                String col1 = st.nextToken(); //Sequence number of the frame (in transmit sequence)
                String col2 = st.nextToken(); //Display time of the frame
                String col3 = st.nextToken(); //Frame type
                String col4 = st.nextToken(); //Frame size in bytes
				
				/*
				 *  Convert each element to desired data type 
				 */
				int  SeqNo 	 = Integer.parseInt(col1);
				Float Ftime   = Float.parseFloat(col2); //Convert from ms to ns
                String Ftype = col3;
                int Fsize 	 = Integer.parseInt(col4);

                //If frame size exceeds UDP payload size then split into multiple datagrams.
                if (Fsize > MAX_PACKET_SIZE)
                {
                    int num_datagrams = (int) Math.ceil(Fsize/(float) MAX_PACKET_SIZE);
                    int sizeSoFar = Fsize;
                    int packetSize = 0;

                    for (int i = 0; i < num_datagrams; i++)
                    {
                        packetSize = (sizeSoFar > MAX_PACKET_SIZE) ? MAX_PACKET_SIZE : sizeSoFar;
                        
                        sizeSoFar -= MAX_PACKET_SIZE; 

                        byte[] buf  = new byte[(int)packetSize];

                        DatagramPacket packet = new DatagramPacket(buf, buf.length, addr, 4444);

                        //Compute delay then poll until elapsed time completed.
                        long startTime = System.nanoTime(); //Add delay of first packet in ns 
                        while (fixedDelay > (System.nanoTime() - startTime));
                        
                        socket.send(packet);
                    }
                }

                else
                {
                    //Create UDP datagram and transmit following delay
                    byte[] buf  = new byte[(int)Fsize];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, addr, 4444);

                    //Compute delay then poll until elapsed time completed.
                    long startTime = System.nanoTime(); //Add delay of first packet in ns 
                    while (fixedDelay > (System.nanoTime() - startTime));
                    
                    socket.send(packet);

                }
                
				/*
				 *  Write line to output file 
				 */
				pout.println(SeqNo+ "\t"+  Ftime + "\t" + Fsize); 
				
            }
            socket.close();
        }
        
        catch (IOException e) 
        {  
			// catch io errors from FileInputStream or readLine()  
			System.out.println("IOException: " + e.getMessage());  
        }
     
        finally 
        {  
			// Close files   
            if (bis != null) 
            { 
                try 
                { 
					bis.close(); 
                    pout.close();
                } 
                catch (IOException e) 
                { 
					System.out.println("IOException: " +  e.getMessage());  
				} 
			} 
		}
    }
}