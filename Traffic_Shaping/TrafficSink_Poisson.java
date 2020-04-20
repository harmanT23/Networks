import java.io.*;
import java.net.*;


public class TrafficSink_Poisson {


    public static void main(String[] args) throws IOException {

        //Controls how many packets receiver will
        int receivePackets = 10000;

        //Create socket, receiver datagram and buffer.
        DatagramSocket socket = new DatagramSocket(4443);
        byte[] buf = new byte[1500];
        DatagramPacket p = new DatagramPacket(buf, buf.length);

        //Write results to file
        PrintStream pout = null;
        FileOutputStream fout =  new FileOutputStream("output_poisson_receiver.txt");
        pout = new PrintStream (fout);

        long lastTime = 0;
        long endTime = 0;

        boolean firstP = true;

        while (receivePackets > 0)
        {
            socket.receive(p);
            endTime = System.nanoTime();

            if (firstP)
            {
                pout.println("0" + " " + String.valueOf(p.getLength()));
                firstP = false;

            }
            else
            {
                pout.println(String.valueOf((endTime - lastTime)/1000) + " " + String.valueOf(p.getLength()));
            }

            lastTime = endTime;
            receivePackets--;
        }

        socket.close();
        pout.close();
    }
}