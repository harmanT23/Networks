import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Sender {

  public static void main(String[] args) throws IOException {
		
		
		BufferedReader bis = null; 
		String currentLine = null; 
		FileOutputStream fout =  new FileOutputStream("depature_23_N1_a.txt");
  		PrintStream pout = null;
		pout = new PrintStream (fout);


		
		try {  
			// InetAddress addr = InetAddress.getByName(args[0]);
			InetAddress addr = InetAddress.getByName("127.0.0.1");
			/*
			 * Open input file as a BufferedReader
			 */ 
			File fin = new File("poisson3.data"); 
			FileReader fis = new FileReader(fin);  
			bis = new BufferedReader(fis);  
			
			int counter = 0;
			int lastdelay = 0;
			int delay = 0;
			int N = 9;
			int first = 0;
			long start = System.nanoTime();
			long lasttime = System.nanoTime();
			long record_time = 0;
			long senttime = System.nanoTime();

			while ( (counter<10000)&&(currentLine = bis.readLine()) != null) { 
				counter++;
				/*
				 *  Parse line and break up into elements 
				 */
				StringTokenizer st = new StringTokenizer(currentLine); 
				String col1 = st.nextToken(); 
				String col2 = st.nextToken(); 
				String col4 = st.nextToken(); 
				
				/*
				 *  Convert each element to desired data type 
				 */
				int SeqNo 	= Integer.parseInt(col1);
				int Ftime 	= Integer.parseInt(col2);
				int Fsize 	= Integer.parseInt(col4);
				
			    byte buf[];
			    buf = new byte[Fsize+1];
				buf[0] = (byte)1;
			    DatagramPacket packet =
			                 new DatagramPacket(buf, buf.length, addr, 4444);
			    DatagramSocket socket = new DatagramSocket();
			    socket.send(packet);
				if(first == 0){
					start = System.nanoTime();
					first =1;
					delay = (Ftime) / N;
					record_time = 0;
				}else{
					senttime = System.nanoTime();
					delay = (Ftime - lastdelay) / N;
					record_time = (senttime - lasttime) / 1000;
					
				}

		    	try
				{
					TimeUnit.MICROSECONDS.sleep(delay);
					pout.println(counter + "\t"+  record_time + "\t" + Fsize); 
				}
				catch(InterruptedException ex)
				{
				    ;
				}

			    lasttime = senttime;
				lastdelay = Ftime;

			} 
		} catch (IOException e) {  
			// catch io errors from FileInputStream or readLine()  
			System.out.println("IOException: " + e.getMessage());  
		} finally {  
			// Close files   
			if (bis != null) { 
				try { 
					bis.close(); 
					pout.close();
				} catch (IOException e) { 
					System.out.println("IOException: " +  e.getMessage());  
				} 
			} 
		} 

    
  }
}
