package com.lamport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;

public class OutgoingQueueHandler implements Runnable {

	private DatagramSocket socket;
	private LinkedBlockingQueue<Message> queue;
	private Lamport lamport;

	public OutgoingQueueHandler(DatagramSocket Socket, Lamport lamport) {
		this.socket = Socket;
		this.lamport = lamport;
		queue = new LinkedBlockingQueue<Message>();
	}

	public void enqueue(Message msg) {
		queue.add(msg);
	}

	@Override
	public void run() {
		while (true) {

			Message m = null;
			if (!queue.isEmpty()) {
				try {
					m = queue.take();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				ObjectOutputStream out = null;
				try {
					out = new ObjectOutputStream(buffer);
					out.writeObject(m);
					out.close();
					buffer.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				DatagramPacket d = null;
				try {
					d = new DatagramPacket(buffer.toByteArray(), buffer.size(), 
							InetAddress.getLocalHost(),
							5000 + m.getDestinationId() * 2 + 1);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				
				try {
					this.socket.send(d);
					if (m.getCode() == 0) {
						Gui.textArea.append(
								"Transfer:k" + (m.getSourceId() + 1) + "->k" + (m.getDestinationId() + 1) + "(" + m.getData() + ")" + "\n");
						System.out.println("Transfer:k" + (m.getSourceId() + 1) + "->k" + (m.getDestinationId() + 1)); // Display on the
																										// screen.
					} else {
						Gui.textArea.append("Mark:k" + (m.getSourceId() + 1) + "->k" + (m.getDestinationId() + 1) + "\n");
						System.out.println("Mark:k" + (m.getSourceId() + 1) + "->k" + (m.getDestinationId() + 1));// Display on the screen
					}
					if (m.getCode() == 0)
						this.lamport.setMoney(this.lamport.getMoney() - m.getData()); // 10 is changed to m.data
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
