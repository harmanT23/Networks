import java.io.*;
import java.net.*;
import java.time.*;
import java.time.LocalTime;
import static java.time.temporal.ChronoUnit.SECONDS;

public class Receiver {
  public static void main(String[] args) throws IOException {
  		FileOutputStream fout =  new FileOutputStream("23_N1.txt");
  		PrintStream pout = null;
		pout = new PrintStream (fout);

	    DatagramSocket socket = new DatagramSocket(4445);
	    

	    int counter = 0;
		int lastdelay = 0;
		int delay = 0;

		int first = 0;
		long start = System.nanoTime();
		long lasttime = System.nanoTime();
		long record_time = 0;
		long senttime = System.nanoTime();
	while(counter<10000){
		counter++;

	    byte[] buf = new byte[655535];
	    DatagramPacket p = new DatagramPacket(buf, buf.length);
	    socket.receive(p);

	    String s = new String(p.getData(), 0, p.getLength());

		if(first == 0){
			start = System.nanoTime();
			first =1;
			record_time = 0;
		}else{
			senttime = System.nanoTime();
			record_time = (senttime - lasttime) / 1000;
			
		}
		pout.println(counter + "\t"+  record_time + "\t" + p.getLength()); 

	    lasttime = senttime;

	}
  }
}
