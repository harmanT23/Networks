package Estimator;

import java.io.*;
import java.net.*;

public class Estimator implements Runnable {

    // System params for packet trains
    public int listenPort = 4445;
    public int N; // Number of packets to transmit in 1 packet train
    public int L; // Packet size in bytes of each packet in the packet train
    public int r; // average bit rate of train in kbps;
    public int maxPacketSize; // In bytes

    // System params for communicating with BlackBox
    public String ipAddress; // IP address of BlackBox
    public int sendPort; // Port number of BlackBox

    // Traffic Generator and Sink for sending/receiving packet trains
    private TrafficGenerator generator;
    private TrafficSink sink;

    // Data objects for storing send/receive time stamps and normalizing time stamp
    public long[] firstSentTimeStamp = new long[1];
    public long[] sendStamps;
    public long[] recStamps;

    public static void main(String[] args) {

        String inIPAddress; // IP address of BlackBox
        int inSendPort; // Port number of BlackBox
        int inMaxPacketSize = 1480; // mac packet size in bytes

        // Default system params overwritten by user
        int rate = 10;
        int N_packets = 100;
        int L_length = 400;

        if (args.length < 5) // If no args then use defaults
        {
            // Defaults
            inIPAddress = "127.0.0.1";
            inSendPort = 4444;
        } else {
            inIPAddress = args[0];
            inSendPort = Integer.parseInt(args[1]);
            rate = Integer.parseInt(args[2]);
            N_packets = Integer.parseInt(args[3]);
            L_length = Integer.parseInt(args[4]);
        }

        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("Estimator PARAMS: Dest IP - " + inIPAddress + ", Dest Port - " + inSendPort);
        Estimator e_ = new Estimator(inIPAddress, inSendPort, inMaxPacketSize, rate, N_packets, L_length);
        new Thread(e_).start();

    }

    public Estimator(String inSendIPAddress, int inSendPort, int inMaxPacketSize, int rate, int N_, int L_) {
        // Assign system params
        ipAddress = inSendIPAddress;
        sendPort = inSendPort;
        maxPacketSize = inMaxPacketSize;
        r = rate;
        N = N_;
        L = L_;

    }

    // Starts Generator and Sink
    public void run() {

        // Arrays for Generator and Sink to store time stamps by index. Where index
        // corresponds to sequence number (i.e. index = 0 --> Seq No. 0)
        sendStamps = new long[N];
        recStamps = new long[N];

        // Set up generator and sink with parameters
        generator = new TrafficGenerator(listenPort, sendPort, ipAddress, maxPacketSize, N, L, r, sendStamps,
                firstSentTimeStamp);

        sink = new TrafficSink(listenPort, maxPacketSize, N, recStamps);

        /// Start Generator and Sink and send packet train.
        Thread G_ = new Thread(generator);
        Thread S_ = new Thread(sink);

        G_.start();
        S_.start();

        try {
            G_.join();
            S_.join();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Normalize recStamps using first packets send time stamp
        for (int i = 0; i < recStamps.length; i++) {
            recStamps[i] -= firstSentTimeStamp[0];
        }

        try {
            PrintStream pout = null;
            FileOutputStream fout = new FileOutputStream("estimator.txt");
            pout = new PrintStream(fout);

            if (recStamps.length != sendStamps.length) {
                System.out.println("LENGTH MISMATCH");
                pout.close();
                return;
            }

            for (int i = 0; i < recStamps.length; i++) {
                pout.println(i + "\t" + sendStamps[i] / 1000 + "\t" + recStamps[i] / 1000);
            }

            pout.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}