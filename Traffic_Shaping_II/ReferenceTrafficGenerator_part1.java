import java.io.*; 
import java.util.*;
import java.net.*;

class ReferenceTrafficGenerator {

    public static void main (String[] args) {

        //Params for reading/writing files
    	long T = (long) (Double.parseDouble(args[0]) * 1000000); //Convert ms to ns
    	long N = Long.parseLong(args[1]);
        long L = Long.parseLong(args[2]);
        
        BufferedReader bis = null; 
        PrintStream pout = null;

        //Params for creating and sending packets
        byte[] buf;
        DatagramPacket packet;

        //Control Packets Sent
        int outputPackets = 10000;
        
        try 
        {            
            //Set Up Address
            InetAddress addr = InetAddress.getByName("127.0.0.1");
            DatagramSocket socket = new DatagramSocket();

            FileOutputStream fout =  new FileOutputStream("output_ref_traffic.txt");
        	pout = new PrintStream (fout);

        	long lastTime = 0;
        	long preTime = 0;
            boolean firstP = true;

            while (outputPackets > 0) {

                //Wait delay T
                while (T > (System.nanoTime() - preTime));

                //Send N packets of size L
                for (int i = 0; i < N; i++)
                {
                    outputPackets --;

                	buf  = new byte[(int) L];
                    packet = new DatagramPacket(buf, buf.length, addr, 4443);
                    
                    //Capture time this packet will be sent
                    preTime = System.nanoTime();
                    socket.send(packet);
                    
                    if (firstP)
                    {
                        pout.println("0" + " " + String.valueOf(N * L));
                        firstP = false;
                    }
                    else
                    {
                        pout.println(String.valueOf((preTime - lastTime) + " " + String.valueOf(N * L)));
                    }
    
                    lastTime = preTime; //Record last time packet was sent.
                }


	            
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