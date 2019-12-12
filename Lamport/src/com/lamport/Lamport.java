package com.lamport;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Random;
//import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

public class Lamport implements Runnable {

	private final static int RUNNING = 0;
	private final static int RECORD = 1;
	private final static int RECEIVE = 0;
	private final static int SEND = 1;

	private static OutgoingQueueHandler queuehandler;

	private boolean isMessageSent;
	private int state;
	private int processId;
	private int money;
	private boolean isStateRecorded;
	private static int me;

	private int[][] lastMessage;
	private int[][] recorded;
	private int[][] marker;

	public boolean isMessageSent() {
		return isMessageSent;
	}

	public void setState(int state) {
		this.state = state;
	}

	public void setMessageSent(boolean isMessageSent) {
		this.isMessageSent = isMessageSent;
	}

	public int getProcessId() {
		return this.processId;
	}

	public int getMoney() {
		return money;
	}

	public boolean isStateRecorded() {
		return isStateRecorded;
	}

	public void setStateRecorded(boolean isStateRecorded) {
		this.isStateRecorded = isStateRecorded;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public Lamport() {
	}

	public Lamport(int processId, int money) {
		this.processId = processId;
		me = this.processId;
		this.money = money;
		initialize();
	}

	public void initialize() {

		this.lastMessage = new int[2][3];
		this.recorded = new int[2][3];
		this.marker = new int[2][3];

		Arrays.fill(marker[RECEIVE], -1);
		Arrays.fill(marker[SEND], -1);

		this.state = RUNNING;
		this.isMessageSent = true;
	}

	/*
	 * Handling of messages when there is normal transfer of money.
	 */
	public void running(Message msg) {
		if (msg.getCode() == 1) {
			if (isStateRecorded()) {

				this.recorded[RECEIVE][msg.getSourceId()] = this.lastMessage[RECEIVE][msg.getSourceId()];
				this.marker[RECEIVE][msg.getSourceId()] = 1;
			} else {
				setMessageSent(false); // Set to false for not sending normal money transfers until the marker messages are sent.
				this.recorded[RECEIVE][this.processId] = this.money;
				System.out.println("Current Money " + this.money);

				display(this.money);

				if (!msg.isSnapshot()) {// Distinguish between the marker messages initialization or the subsequent ones.

					this.recorded[RECEIVE][msg.getSourceId()] = 0;
					this.marker[RECEIVE][msg.getSourceId()] = 1;
				}

				sendMarker();
				setStateRecorded(true);
				setState(RECORD);
			}
		} else {
			System.out.println(" Receiving Money at " + getProcessId() + "money received " + msg.getData());

			setMoney(getMoney() + msg.getData());

			this.lastMessage[RECEIVE][msg.getSourceId()] = msg.getData();
		}
	}

	/*
	 * Display the current state on the Gui
	 */
	public void display(int currentmoney) {
		switch (getProcessId()) {
		case 0:
			Gui.konto1CurrBalance.setText(currentmoney + Gui.euro);
			break;
		case 1:
			Gui.konto2CurrBalance.setText(currentmoney + Gui.euro);
			break;
		case 2:
			Gui.konto3CurrBalance.setText(currentmoney + Gui.euro);
			break;
		}

		Integer bal = Gui.totalBalance();
		Gui.totalCurrBalance.setText(bal + Gui.euro);
	}

	/*
	 * Record the states of the processes and the channels
	 */
	public void record(Message msg) {
		if (msg.isSnapshot())
			return;
		if (msg.getCode() == 1) {
			if (this.isStateRecorded) {
				this.recorded[RECEIVE][msg.getSourceId()] = lastMessage[RECEIVE][msg.getSourceId()];
				this.marker[RECEIVE][msg.getSourceId()] = 1;
				if (finishedSnapshot()) {
					System.out.println("Initializing the process " + this.processId);
					initialize();
				}
			}
		} else {
			System.out.println(" Receiving Money at " + this.processId + "money received " + msg.getData());
			this.lastMessage[RECEIVE][msg.getSourceId()] = msg.getData();
			setMoney(getMoney() + msg.getData());
		}
	}

	/*
	 * Check if the snapshot has finished or not
	 */
	public boolean finishedSnapshot() {
		for (int i = 0; i < 3; i++)
			if (i != getProcessId())
				if ((this.marker[RECEIVE][i] < 0) || (this.marker[SEND][i] < 0))
					return false;
		return true;
	}

	/*
	 * Receive all the messages to this process
	 */
	public void receive(Message msg) {
		System.out.println("Received by" + getProcessId());
		
		if (isStateRecorded() && msg.isSnapshot()) return;
		switch (this.state) {
			case RUNNING:
				running(msg);
				break;
			case RECORD:
				record(msg);
				break;
		}
	}

	/*
	 * Send the marker messages to other processes.
	 */
	private void sendMarker() {
		for (int i = 0; i < 3; i++) {
			if (i != getProcessId()) {
				Message msg = new Message(false, 1, 0, getProcessId(), i);
				queuehandler.enqueue(msg);
				this.marker[SEND][i] = 1;
			}
		}
		setMessageSent(true);
	}

	/*
	 * public static void main(String[] args) throws SocketException { // TODO
	 * Auto-generated method stub Lamport acc1 = new Lamport(0,5000,200);
	 * DatagramSocket Socket = new DatagramSocket(acc1.port);
	 * 
	 * }
	 */
	@Override
	public void run() {
		try {
			DatagramSocket sendSocket = new DatagramSocket(9000 + getProcessId() * 2);
			int destinationId = 0;
			OutgoingQueueHandler outgoingQueue = new OutgoingQueueHandler(sendSocket, this);
			queuehandler = outgoingQueue;
			
			Thread t = new Thread(outgoingQueue);
			t.start();
			DatagramSocket receiveSocket = new DatagramSocket(5000 + getProcessId() * 2 + 1);
			IncomingQueueHandler I = new IncomingQueueHandler(receiveSocket, this);
			
			Thread t1 = new Thread(I);
			t1.start();
			while (true) {

				try {
					int randomDelay = 500 + new Random().nextInt(2000); // delay is kept between 500 ms to 2000 ms
					Thread.sleep(randomDelay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				int randomMoney = 1 + new Random().nextInt(100); // random money between 1 and 100

				System.out.println("randomMoney=" + randomMoney);
				if (isMessageSent()) {
					if (getMoney() - randomMoney > 0) {
						Random n = new Random();
						destinationId = n.nextInt(3);
						if (destinationId != getProcessId()) {
							Message msg = new Message(false, 0, randomMoney, getProcessId(), destinationId);
							outgoingQueue.enqueue(msg);
						}
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
}
