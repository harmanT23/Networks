package Estimator;

import java.io.*;
import java.util.*;

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

    // Computes the cumulative arrivals and departures times for Arr and Depar
    // respectively.
    // Note: Cumulative bytes for Arr or Depar is simply the
    // index times L (size of each packet in train)
    public void computeArrivalAndDepartures(long[] sendStamps, long[] recStamps, long[] Arr, long[] Depar) {

        long cumSent = 0L;
        long cumRec = 0L;

        // Ensure the sizes of Arr and Depar are the same
        if (Arr.length != Depar.length) {
            System.out.println(
                    "Arrival and departure curve have length mismatch i.e." + Arr.length + " != " + Depar.length);
        }

        for (int i = 0; i < sendStamps.length; i++) {
            cumSent += sendStamps[i];
            Arr[i] = cumSent;
        }

        for (int j = 0; j < recStamps.length; j++) {
            cumRec += recStamps[j];
            Depar[j] = cumRec;
        }

    }

    // Compute the B-Max from the arrival and departure functions
    public int computebMax(long[] Arr, long[] Depar) {

        int bMax = 0; // Largest bMax found
        long timeDiff = Long.MAX_VALUE;
        int closestArrival = 0;

        // For each departure time, find the closest arrival time then compute backlog
        // as
        // the difference in index values multiplied by (packet size * 8)
        for (int i = 0; i < Depar.length; i++) {
            timeDiff = Long.MAX_VALUE;
            closestArrival = 0;
            for (int j = 0; j < Arr.length; j++) {
                if (Depar[i] >= Arr[j] && Depar[i] - Arr[j] < timeDiff) {
                    timeDiff = Depar[i] - Arr[j];
                    closestArrival = j;
                }
            }
            bMax = Math.max(bMax, Math.abs(closestArrival - i) * L * 8);
        }

        return bMax;
    }

    // Debug function to print Arrival and Departures
    public void printArrivalAndDepartureToFile(long[] Arr, long[] Depar, int rate) {

        // Prints to file the arrival and departure curves with file name that includes
        // rate
        PrintStream pout = null;
        PrintStream pout2 = null;

        try {
            FileOutputStream fout = new FileOutputStream("arr_depar_" + rate + ".txt");
            FileOutputStream fout2 = new FileOutputStream("send_rec" + rate + ".txt");

            pout = new PrintStream(fout);
            pout2 = new PrintStream(fout2);

            for (int i = 0; i < Arr.length; i++) {
                // Sequence Number - Cumulative Arrival - Cumulative Arrival bytes - Cumulative
                // Departure - Cumulative Departure Bytes
                pout.println(i + "\t" + Arr[i] + "\t" + L * (i + 1) + "\t" + Depar[i] + "\t" + L * (i + 1));
            }

            for (int i = 0; i < N; i++) {
                pout2.println(i + "\t" + sendStamps[i] + "\t" + recStamps[i]);

            }

            pout.close();
            pout2.close();

        } catch (IOException e) {
            // catch io errors from FileInputStream or readLine()
            System.out.println("IOException: " + e.getMessage());
        }
    }

    // Starts Generator and Sink
    public void run() {

        // Note: Sequence number (i.e. index = 0 --> Seq No. 0)
        sendStamps = new long[N];
        recStamps = new long[N];

        // Rate for each iteration to try
        int rateBest = r;
        int rate_incr = 25; // increase rate in Kbps in each iteration
        List<Integer> rates = new ArrayList<>();

        int bMaxBest = 0; // Holds largest bMax
        int bMaxTemp = 0;
        List<Integer> bMaxes = new ArrayList<>();

        // Service curve estimates
        int serviceEstimate = 0;
        int serviceEstimateTemp = 0;
        int serviceEstimateLast = serviceEstimate;

        while (true) {
            System.out.println("---------------------------------------------------------------------------------");
            System.out.println("Estimating Service Curve with N = " + N + ", L = " + L + " B, r = " + r + " kbps");

            // INIT: Generator and Sink
            generator = new TrafficGenerator(listenPort, sendPort, ipAddress, maxPacketSize, N, L, r, sendStamps,
                    firstSentTimeStamp);

            sink = new TrafficSink(listenPort, maxPacketSize, N, recStamps);

            // Start Generator and Sink
            Thread G_ = new Thread(generator);
            Thread S_ = new Thread(sink);
            G_.start();
            S_.start();

            // Wait till packet train sent and received
            try {
                G_.join();
                S_.join();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Normalize received timestamps using first packets send time stamp
            for (int i = 0; i < recStamps.length; i++) {
                recStamps[i] -= firstSentTimeStamp[0];
            }

            // INIT: Cumulative Arrival and Depatures
            long[] Arr = new long[N];
            long[] Depar = new long[N];

            // Compute A(t) and D(t) then bMax
            computeArrivalAndDepartures(sendStamps, recStamps, Arr, Depar);
            printArrivalAndDepartureToFile(Arr, Depar, r);
            bMaxTemp = computebMax(Arr, Depar);
            System.out.println("B-Max is " + bMaxTemp + " bits");

            // Add rate and corresponding Bmax
            rates.add(r);
            bMaxes.add(bMaxTemp);

            // Compute S(t) = rt - Bmax(r) for each rate r where t = 100 milliseconds
            // i.e. Compute S(t) at the end of time interval [0, 100] ms
            for (int i = 0; i < rates.size(); i++) {

                serviceEstimateTemp = rates.get(i) * 100 - bMaxes.get(i);

                if (serviceEstimateTemp >= serviceEstimate) {
                    serviceEstimate = serviceEstimateTemp;
                    rateBest = rates.get(i);
                    bMaxBest = bMaxes.get(i);
                }
            }

            if (serviceEstimateLast == serviceEstimate) {
                System.out.println("Best service estimate is r = " + rateBest + " Kbps and Bmax(r) = " + bMaxBest + " bits");
                break;
            } else {
                serviceEstimateLast = serviceEstimate;
                System.out.println("Service estimate so far " + serviceEstimate);
            }

            // Update r and add existing r to list
            r += rate_incr;
        }
    }
}