package PacketScheduler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Removes and sends packets from buffers to a given address and port.
 */
public class SchedulerSender implements Runnable {
	/**
	 * senderActiveUntil holds the time (in ns) when next packet can be sent, i.e.
	 * time when last sending ends. NOTE: this is not the actual time it takes to
	 * send packet, it is the time it would take to send packet at given link
	 * capacity.
	 */
	private long senderActiveUntil;

	// destination port
	private int destPort;
	// destination address
	private InetAddress destAddress;
	// socket used to send packets
	private DatagramSocket socket;
	// buffers from which packets are sent
	private Buffer[] buffers;
	// link capacity at which packet scheduler operates (pbs)
	private long linkCapacity;

	/**
	 * Constructor. Creates socket.
	 * 
	 * @param buffers      Buffers from which packets are sent.
	 * @param destAddress  IP address to which packets are sent.
	 * @param destPort     Port to which packets are sent.
	 * @param linkCapacity Link capacity at which FIFO scheduler operates (bps).
	 */
	public SchedulerSender(Buffer[] buffers, InetAddress destAddress, int destPort, long linkCapacity) {
		this.senderActiveUntil = 0l;
		this.buffers = buffers;
		this.destAddress = destAddress;
		this.destPort = destPort;
		this.linkCapacity = linkCapacity;

		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Send packet using socket.
	 * 
	 * @param packet    Packet to send.
	 * @param startTime Time when sending of this packet was started.
	 */
	public synchronized void sendPacket(DatagramPacket packet, long startTime) {
		try {
			// change destination of packet (do forwarding)
			packet.setAddress(destAddress);
			packet.setPort(destPort);

			// time it would take to send packet with given link capacity
			long sendingTime = (long) ((((float) packet.getLength() * 8) / linkCapacity) * 1000000000);

			socket.send(packet);

			// time before next packet can be sent (simulate link capacity)
			long timeToWait = sendingTime - (System.nanoTime() - startTime);

			// set when next packet can be sent
			senderActiveUntil = startTime + timeToWait;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Remove packets form buffers and send them. This method is invoked when
	 * starting a thread for this class.
	 */
	public void run() {
		// Weights, Quanti and defecit of Flows
		int[] W = { 1, 1, 1 };
		int[] Qi = { 1500, 1500, 1500 };
		int[] DC = { 0, 0, 0 };

		int DRR_Round_Buffer = 0;
		int MAX_BUFS = buffers.length;

		int noEmpty = 0;

		// Get minimum weight and update quanti of each flow
		int minQ = Math.min(Math.min(Qi[0], Qi[1]), Qi[2]);

		for (int i = 0; i < 3; i++) {
			Qi[i] = W[i] * minQ;
		}

		while (true) {
			DatagramPacket packet = null;

			// reset empty count when round complete
			if (DRR_Round_Buffer == 0)
				noEmpty = 0;

			// get time when next packet can be sent
			long startTime = System.nanoTime();
			long nextSendOK = senderActiveUntil;

			// if no packet is in transmission look for next packet to send
			if (System.nanoTime() >= nextSendOK) {
				/*
				 * Check if there is a packet in queue. If there is send packet, remove it form
				 * queue. If there is no packet increase noEmpty that keeps track of number of
				 * empty queues
				 */

				// Send packets for as long as there is a backlog for a flow and enough credits
				if ((packet = buffers[DRR_Round_Buffer].peek()) != null
						&& (DC[DRR_Round_Buffer] - packet.getLength()) >= 0) {
					sendPacket(packet, startTime);
					DC[DRR_Round_Buffer] -= packet.getLength();
					buffers[DRR_Round_Buffer].removePacket();
				} else {
					// Update credits of this if it's still active before moving to next buffer.
					if (buffers[DRR_Round_Buffer].getSize() > 0) {
						DC[DRR_Round_Buffer] += Qi[DRR_Round_Buffer];
					} else {
						noEmpty++;
					}

					DRR_Round_Buffer = (DRR_Round_Buffer + 1) % MAX_BUFS;
				}

				/*
				 * TODO: Implement sending of a SINGLE packet from packet scheduler. Variable
				 * noEmpty must be set to total number of queues if all are empty.
				 *
				 * NOTE: The code you are adding sends at most one packet!
				 *
				 * Look at the example above to find out how to check if a particular queue is
				 * empty. Once you have found from which queue to send, send a packet and remove
				 * it from that queue (as in example above).
				 */
			} else {
				// wait until it is possible to send
				long timeToWait = nextSendOK - startTime;

				// To improve results we switched to a busy wait as thread sleep adds more time
				// to put thread to sleep then wake.
				// Specifically much fewer packets were dropped when using busy wait versus
				// thread sleep.
				// while (timeToWait > (System.nanoTime() - startTime));
				// continue;

				try {
					Thread.sleep(timeToWait / 1000000, (int) timeToWait % 1000000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			// there are no packets in buffers to send. Wait for one to arrive to buffer.
			// (busy wait)
			if (noEmpty == buffers.length) {
				boolean anyNotEmpty = false;
				for (int i = 0; i < buffers.length; i++) {
					if (buffers[i].getSize() > 0) {
						anyNotEmpty = true;
					}
				}
				while (!anyNotEmpty) {
					for (int i = 0; i < buffers.length; i++) {
						if (buffers[i].getSize() > 0) {
							anyNotEmpty = true;
						}
					}
				}
			}
		}
	}
}
