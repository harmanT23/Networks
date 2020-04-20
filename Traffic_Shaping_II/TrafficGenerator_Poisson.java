import java.io.*; 
import java.util.*;
import java.net.*;

class TrafficGenerator_Poisson {

    public static void main (String[] args) {

        //Params for reading/writing files
        BufferedReader bis = null; 
		String currentLine = null; 
        PrintStream pout = null;

        //Control Packets Sent
        int outputPackets = 10000; //10000;
        
        try 
        {
			/*
			 * Open input file as a BufferedReader
			 */ 
			File fin = new File("poisson3.data"); 
			FileReader fis = new FileReader(fin);  
            bis = new BufferedReader(fis); 
            
            //Set Up Address
            InetAddress addr = InetAddress.getByName("128.100.13.177");
            DatagramSocket socket = new DatagramSocket();
            
			/*
			 * Open file for output 
			 */
			FileOutputStream fout =  new FileOutputStream("output_poisson_sender.txt");
            pout = new PrintStream (fout);

            //Delay Params
            long startTime = System.nanoTime() + 273*1000; //Add delay of first packet in ns

            //long startTime = System.nanoTime(); //Time to execute
            while (outputPackets > 0 && (currentLine = bis.readLine()) != null) {
                
                outputPackets--;
				
				/*
				 *  Parse line and break up into elements 
				 */
				StringTokenizer st = new StringTokenizer(currentLine); 
				String col1 = st.nextToken(); //Sequence Number
				String col2 = st.nextToken(); //Time
				String col3 = st.nextToken(); //Packet Size
				
				/*
				 *  Convert each element to desired data type 
				 */
				int  SeqNo 	= Integer.parseInt(col1);
				long Ftime  = Long.parseLong(col2) * 1000; //Convert from ms to ns
                long Fsize 	= Long.parseLong(col3);
                
                //Create UDP datagram and transmit following delay
                byte[] buf  = new byte[(int)Fsize];
                DatagramPacket packet = new DatagramPacket(buf, buf.length, addr, 4444);

                //Compute delay then poll until elapsed time completed. 
                while (Ftime > (System.nanoTime() - startTime));
                
                socket.send(packet);
				
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