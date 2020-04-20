import java.io.*;
import java.net.*;

public class ReferenceTrafficSink {

    public static void main(String[] args) throws IOException {

        // Create socket, receiver datagram and buffer.
        DatagramSocket socket = new DatagramSocket(4445);
        byte[] buf = new byte[1500];
        DatagramPacket p = new DatagramPacket(buf, buf.length);

        // Write results to file
        PrintStream pout = null;
        PrintStream pout2 = null;
        PrintStream pout3 = null;

        FileOutputStream fout = new FileOutputStream("output_ref_sink1.txt");
        FileOutputStream fout2 = new FileOutputStream("output_ref_sink2.txt");
        FileOutputStream fout3 = new FileOutputStream("output_ref_sink3.txt");

        pout = new PrintStream(fout);
        pout2 = new PrintStream(fout2);
        pout3 = new PrintStream(fout3);

        long lastTime1 = 0;
        long lastTime2 = 0;
        long lastTime3 = 0;
        long endTime = 0;

        boolean firstP = true;
        boolean firstP2 = true;
        boolean firstP3 = true;

        while (true) {
            socket.receive(p);
            endTime = System.nanoTime();

            if (p.getData()[0] == 0x01) {
                if (firstP) {
                    pout.println("0" + " " + String.valueOf(p.getLength()));
                    firstP = false;
                } else {
                    pout.println(String.valueOf((endTime - lastTime1)) + " " + String.valueOf(p.getLength()));
                }
                lastTime1 = endTime;
            }

            else if (p.getData()[0] == 0x02) {
                if (firstP2) {
                    pout2.println("0" + " " + String.valueOf(p.getLength()));
                    firstP2 = false;
                } else {
                    pout2.println(String.valueOf((endTime - lastTime2)) + " " + String.valueOf(p.getLength()));
                }
                lastTime2 = endTime;
            }

            else if (p.getData()[0] == 0x03) {
                if (firstP3) {
                    pout3.println("0" + " " + String.valueOf(p.getLength()));
                    firstP3 = false;
                } else {
                    pout3.println(String.valueOf((endTime - lastTime3)) + " " + String.valueOf(p.getLength()));
                }
                lastTime3 = endTime;
            }
        }
    }
}