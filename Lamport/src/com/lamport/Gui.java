package com.lamport;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class Gui extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private static JFrame frame;
	private static JPanel markerPanel;

	private static JSeparator separator;
	private static JLabel heading;

	private static JLabel konto1; // for displaying text as "konto#" on GUI
	private static JLabel konto2;
	private static JLabel konto3;
	private static JLabel totalBalance;

	public static JLabel konto1CurrBalance; // for displaying current balance when markers are sent
	public static JLabel konto2CurrBalance; // and snapshots are taken. Also, use
	public static JLabel konto3CurrBalance; // konto#CurrBalance.setText(whatEverBalance + euro);
	public static JLabel totalCurrBalance;

	private static JButton konto1SnapshotButton; // Snapshot buttons for respective kontos'
	private static JButton konto2SnapshotButton;
	private static JButton konto3SnapshotButton;

	public static JTextArea textArea; // Text Area to display account movement information
	private static JScrollPane scrollPane;

	public static String euro = " \u20AC";
	private static String newline = "\n";

	private static Lamport process1, process2, process3;
	private static Thread thread1, thread2, thread3; // 3 threads for each Lamport instances
	
	public static void addComponentsToPane(final Container pane) {

		System.out.println("STARTED: addComponentsToPane");

		markerPanel = new JPanel(null);

		heading = new JLabel("SnapShot-Alogrithm by Chandy and Lamport");
		heading.setFont(new Font("", Font.BOLD, 18));
		heading.setBounds(55, 30, 390, 80);
		markerPanel.add(heading);

		textArea = new JTextArea(5, 30);
		scrollPane = new JScrollPane(textArea);
		textArea.setEditable(false);
		scrollPane.setBounds(50, 110, 550, 400);
		// textArea.append(text + "\n");
		markerPanel.add(scrollPane);

		separator = new JSeparator(SwingConstants.VERTICAL);
		separator.setBounds(640, 50, 1, 500);
		markerPanel.add(separator);

		/**
		 * 
		 * Konto1 information. Use konto1CurrBalance.setText(balance + euro); to
		 * dynamically display values
		 */

		konto1 = new JLabel("konto1");
		konto1.setFont(new Font("", Font.BOLD, 12));
		konto1.setBounds(680, 80, 200, 100);
		markerPanel.add(konto1);

		konto1CurrBalance = new JLabel("0.00" + euro);
		konto1CurrBalance.setText("3000" + euro);
		konto1CurrBalance.setBounds(750, 80, 200, 100);
		markerPanel.add(konto1CurrBalance);

		konto1SnapshotButton = new JButton("Snapshot");
		konto1SnapshotButton.setBounds(750, 140, 100, 25);
		markerPanel.add(konto1SnapshotButton);

		konto1SnapshotButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("konto1 Snapshot Button clicked");

				Message msg = new Message(true, 1, 0, 20, 20);
				process1.receive(msg);
			}
		});

		/**
		 * 
		 * Konto1 information. Use konto2CurrBalance.setText(balance + euro); to
		 * dynamically display values
		 */

		konto2 = new JLabel("konto2");
		konto2.setFont(new Font("", Font.BOLD, 12));
		konto2.setBounds(680, 160, 200, 100);
		markerPanel.add(konto2);

		konto2CurrBalance = new JLabel("0.00" + euro);
		konto2CurrBalance.setText("5000" + euro);
		konto2CurrBalance.setBounds(750, 160, 200, 100);
		markerPanel.add(konto2CurrBalance);

		konto2SnapshotButton = new JButton("Snapshot");
		konto2SnapshotButton.setBounds(750, 220, 100, 25);
		markerPanel.add(konto2SnapshotButton);

		konto2SnapshotButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("konto2 Snapshot Button clicked");

				Message msg = new Message(true, 1, 0, 20, 20);
				process2.receive(msg);
			}
		});

		/**
		 * 
		 * Konto3 information. Use konto3CurrBalance.setText(balance + euro); to
		 * dynamically display values
		 */

		konto3 = new JLabel("konto3");
		konto3.setFont(new Font("", Font.BOLD, 12));
		konto3.setBounds(680, 240, 200, 100);
		markerPanel.add(konto3);

		konto3CurrBalance = new JLabel("0.00" + euro);
		konto3CurrBalance.setText("7000" + euro);
		konto3CurrBalance.setBounds(750, 240, 200, 100);
		markerPanel.add(konto3CurrBalance);

		konto3SnapshotButton = new JButton("Snapshot");
		konto3SnapshotButton.setBounds(750, 300, 100, 25);
		markerPanel.add(konto3SnapshotButton);

		konto3SnapshotButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("konto3 Snapshot Button clicked");

				Message msg = new Message(true, 1, 0, 20, 20);
				process3.receive(msg);

			}
		});

		totalBalance = new JLabel("Balance");
		totalBalance.setFont(new Font("", Font.BOLD, 12));
		totalBalance.setBounds(680, 320, 200, 100);
		markerPanel.add(totalBalance);

		totalCurrBalance = new JLabel("0.00" + euro);
		totalCurrBalance.setText("15000" + euro);
		totalCurrBalance.setBounds(750, 320, 200, 100);
		markerPanel.add(totalCurrBalance);

		pane.add(markerPanel);

	}

	public static Integer totalBalance() {

		String k1 = konto1CurrBalance.getText();
		String k2 = konto2CurrBalance.getText();
		String k3 = konto3CurrBalance.getText();

		String[] str = k1.split(" ");
		final Integer konto1Balance = Integer.parseInt(str[0]);
		str = k2.split(" ");
		final Integer konto2Balance = Integer.parseInt(str[0]);
		str = k3.split(" ");
		final Integer konto3Balance = Integer.parseInt(str[0]);

		return konto1Balance + konto2Balance + konto3Balance;
	}

	private static void createAndShowGUI() {
		System.out.println("STARTED: createAndShowUI");

		frame = new JFrame("Lamport");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		addComponentsToPane(frame.getContentPane());
		frame.pack();

		Insets insets = frame.getInsets();
		frame.setSize(new Dimension(insets.left + insets.right + 1800, insets.top + insets.bottom + 600));

		frame.setVisible(true);
		frame.setLocationRelativeTo(null);

		System.out.println("ENDED: createAndShowUI");

		process1 = new Lamport(0, 3000);
		process2 = new Lamport(1, 5000);
		process3 = new Lamport(2, 7000);

		thread1 = new Thread(process1);
		thread1.start();

		thread2 = new Thread(process2);
		thread2.start();

		thread3 = new Thread(process3);
		thread3.start();

		System.out.println("Completed creation of 3 Lamport instaces and threads");
	}

	public static void main(String[] args) {
		System.out.println("calling createAndShowGUI()");
		createAndShowGUI();
	}
}
