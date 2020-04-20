package Estimator;

import java.io.*;
import java.util.*;
import java.net.*;

public class TrafficGenerator implements Runnable {

    private int dstPort; // Port number of host to send packets to
    private int srcPort; // Port of this host
    private String dstIPAddress; // IP address to send packets to
    private int N_; // Number of packets to transmit in 1 packet train
    private int L_; // Packet size (bytes) of each packet in the packet train
    private double r_; // average bit rate (kbps) of train;
    private long T_; // time interval (Ns) between transmission of two packets
    private long[] sendStamps; // Records send time stamps of every packet in Ns
    private long[] firstSentTimeStamp; // Records the first sent packets time stamp in Ns

    public TrafficGenerator(int srcPort, int sendPort, String sendIPAddress, int maxPacketSize, int N, int L, double r,
            long[] sendStamps, long[] firstSentTimeStamp) {

        this.srcPort = srcPort;
        this.dstPort = sendPort;
        this.dstIPAddress = sendIPAddress;

        this.N_ = N;
        this.L_ = L;
        this.r_ = r;
        this.T_ = (long) ((L * 8) / (r * 1000) * 1000000000L);
        this.sendStamps = sendStamps;
        this.firstSentTimeStamp = firstSentTimeStamp;
    }

    public void run() {

        System.out.println("Starting Traffic Generator...");
        System.out.println("Sending Packet Train with @PARAMS: N = " + N_ + " L = " + L_ + " B T = " + T_ + " Ns...");

        // Control Packets Sent
        int outputPackets = N_;

        try {

            // Set Up Address
            InetAddress addr = InetAddress.getByName(dstIPAddress);
            DatagramSocket socket = new DatagramSocket();

            // Start Time
            long startTime = System.nanoTime();
            long firstPacketTS = startTime; // Time stamp of first packet.
            int sequenceNum = 0;

            while (outputPackets > 0) {

                // Create UDP datagram and assign source port number to first 2 bytes of packet
                byte[] buf = new byte[(int) L_];
                System.arraycopy(toByteArray(srcPort), 2, buf, 0, 2);
                DatagramPacket packet = new DatagramPacket(buf, buf.length, addr, dstPort);

                // Add sequence numbers - copy over 4 bytes of int from buf[3] to buf[6]
                System.arraycopy(toByteArray(sequenceNum), 0, buf, 3, 4);

                // Delay T seconds
                while (T_ > (System.nanoTime() - startTime))
                    ;

                startTime = System.nanoTime();
                socket.send(packet);

                // Record time stamp of packet by its sequence number
                if (sequenceNum == 0) {
                    sendStamps[0] = 0;
                    firstPacketTS = startTime; // Update absolute time stamp of first packet
                    firstSentTimeStamp[0] = firstPacketTS;
                } else {
                    sendStamps[sequenceNum] = startTime - firstPacketTS;
                }

                // Update Params
                sequenceNum++;
                outputPackets--;
            }
            System.out.println("Traffic Generator Finished!");
            socket.close();
        }

        catch (IOException e) {
            // catch io errors from FileInputStream or readLine()
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * Converts an integer to a byte array.
     * 
     * @param value an integer
     * @return a byte array representing the integer
     */
    public static byte[] toByteArray(int value) {
        byte[] Result = new byte[4];
        Result[3] = (byte) ((value >>> (8 * 0)) & 0xFF);
        Result[2] = (byte) ((value >>> (8 * 1)) & 0xFF);
        Result[1] = (byte) ((value >>> (8 * 2)) & 0xFF);
        Result[0] = (byte) ((value >>> (8 * 3)) & 0xFF);
        return Result;
    }
}