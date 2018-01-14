import java.io.*;
import java.net.*; // For the Socket
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SimpleChatClientA {
	
	JTextArea incoming;
	JTextField outgoing;
	BufferedReader reader;
	PrintWriter writer;
	Socket sock;

	public void go() {
		// Make the GUI and register a listerner with the send button
		JFrame frame = new JFrame("Ludicrously Simple Chat Client");
		JPanel mainPanel = new JPanel();
		incoming
		JButton sendButton = new JButton("Send Message");
		sendButton.addActionListener(new SendButtonListener()); // build the GUI
		mainPanel.add(outgoing);
		mainPanel.add(sendButton);
		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);

		// Call the setUpNetworking() method
		setUpNetworking();
		frame.setSize(400, 500);
		frame.setVisible(true);
	}	

	private void setUpNetworking() {
		try {
			// Make a Socket, then make a PrintWriter
			sock = new Socket("127.0.0.1", 5000); // Using local host to test the client and the server on the same machine
												 // It's called from the go() method right before displaying the app GUI
			// Assign the PrintWriter to writer insstance variable	
			writer = new PrintWriter(sock.getOutputStream());
			System.out.println("networking established");
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	public class SendButtonListener implements ActionsListener {

		public void actionPerformed(ActionEvent ev) {
			try  { 
				// Get the text from the text field and sent it to the server
				// using the writer (a PrintWriter)
				writer.println(outgoing.getText()); // Now, actually doing the writing.
													// The writer is chained to the inpput stream from the Socket, so 
													// whenever we do a println(), it goes over the network to the server!
				writer.flush();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			outgoing.setText("");
			outgoing.requestFocus();
		}

	} // close SendButtonListener inner class

	public static void main(String[] args) {
		new SimpleChatClientA().go();
	}

} // close outer class