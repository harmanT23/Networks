import java.io.*; 
import java.util.*;
import java.net.*;

class TrafficGenerator_Ethernet {

    public static void main (String[] args) {

        //Params for reading/writing files
        BufferedReader bis = null; 
        String currentLine = null; 
        PrintStream pout = null;

        int ethernetPackets = 10000;
        
        try 
        {
            /*
             * Open input file as a BufferedReader
             */ 
            File fin = new File("BC-pAug89-small.TL.txt"); 
            FileReader fis = new FileReader(fin);  
            bis = new BufferedReader(fis); 
            
            //Set Up Address
            InetAddress addr = InetAddress.getByName("128.100.13.177");
            DatagramSocket socket = new DatagramSocket();
            
            /*
             * Open file for output 
             */
            FileOutputStream fout =  new FileOutputStream("output_ethernet_sender.txt");
            pout = new PrintStream (fout);

            //Delay Params
            long startTime = System.nanoTime() + 1340000; //Add delay of first packet in ns

            //long startTime = System.nanoTime(); //Time to execute
            while (ethernetPackets > 0 && (currentLine = bis.readLine()) != null) {
                ethernetPackets--;

                /*
                 *  Parse line and break up into elements 
                 */
                StringTokenizer st = new StringTokenizer(currentLine); 
                String col1 = st.nextToken(); //Time in seconds
                String col2 = st.nextToken(); //Packet size
                
                /*
                 *  Convert each element to desired data type 
                 */
                long Ftime  = (long) (Float.parseFloat(col1) * 1000000000.0); //Convert from s to ns
                long Fsize  = Long.parseLong(col2);
                
                //Create UDP datagram and transmit following delay
                byte[] buf  = new byte[(int)Fsize];
                DatagramPacket packet = new DatagramPacket(buf, buf.length, addr, 4444);

                //Compute delay then poll until elapsed time completed. 
                while (Ftime > (System.nanoTime() - startTime));
                
                socket.send(packet);
                
                /*
                 *  Write line to output file 
                 */
                pout.println(Ftime + "\t" + Fsize); 
                
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