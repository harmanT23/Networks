package Estimator;

import java.net.*;

public class TrafficSink implements Runnable {

    private int listenPort; // Port we expect packets from.
    private int maxPacketSize; // Max packet we will accept.
    private int trainLength; // Length of packet train in packets
    private long[] recStamps; // Records receive time stamps of every packet in us

    public TrafficSink(int listenPort, int maxPacketSize, int trainLength, long[] recStamps) {
        this.listenPort = listenPort;
        this.maxPacketSize = maxPacketSize;
        this.trainLength = trainLength;
        this.recStamps = recStamps;
    }

    public void run() {
        DatagramSocket socket = null;
        int num_iters = trainLength;

        try {
            // Create socket, receiver datagram and buffer.
            socket = new DatagramSocket(listenPort);
            byte[] buf = new byte[maxPacketSize];
            DatagramPacket p = new DatagramPacket(buf, buf.length);

            long endTime = 0;
            int seqNumber = -1;

            while (num_iters > 0) {
                socket.receive(p);

                // Get sequence number of packet and assign receive time stamp in Ns. Will
                // normalize timestamps later.
                endTime = System.nanoTime();
                seqNumber = fromByteArray(p.getData(), 3, 4);
                recStamps[seqNumber] = endTime;

                num_iters--;
            }

            System.out.println("Traffic Sink Finished - Received the entire packet train!");
            socket.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static int fromByteArray(byte[] value, int start, int length) {
        int Return = 0;
        for (int i = start; i < start + length; i++) {
            Return = (Return << 8) + (value[i] & 0xff);
        }
        return Return;
    }
}