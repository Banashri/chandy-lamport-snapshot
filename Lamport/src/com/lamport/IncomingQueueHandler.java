package com.lamport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class IncomingQueueHandler implements Runnable {

	private DatagramSocket socket;
	private Lamport lamport;

	public IncomingQueueHandler(DatagramSocket socket, Lamport l) {
		this.lamport = l;
		this.socket = socket;
	}

	@Override
	public void run() {
		while (true) {
			byte[] buffer = new byte[1000];
			DatagramPacket packet;
			packet = new DatagramPacket(buffer, buffer.length);

			try {
				this.socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			ByteArrayInputStream baos = new ByteArrayInputStream(buffer);
			ObjectInputStream oos = null;

			try {
				oos = new ObjectInputStream(baos);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			Message m = null;
			try {
				m = (Message) oos.readObject();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			this.lamport.receive(m);
		}
	}
}
