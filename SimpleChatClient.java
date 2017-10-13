import java.io.*;
import java.net.*; // For the Socket
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SimpleChatClient {
	
	JTextArea incoming;
	JTextField outgoing;
	BufferedReader reader;
	PrintWriter writer;
	Socket sock;


	public static void main(String[] args) {
		SimpleChatClient client = new SimpleChatClient();
		client.go();
	}

	public void go() {
		// Make the GUI and register a listerner with the send button
		JFrame frame = new JFrame("Ludicrously Simple Chat Client");
		JPanel mainPanel = new JPanel();
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
		JScrollPane qScroller = new JScrollPane(incoming);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		outgoing = new JTextField(20);
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new SendButtonListener()); // build the GUI
		mainPanel.add(qScroller);
		mainPanel.add(outgoing);
		mainPanel.add(sendButton);

		// Call the setUpNetworking() method
		setUpNetworking();

		// Starting a new thread using a new inner class as the Runnable (job) for the thread.
		// The thread's job is to read from the server's socket strea, displaying any incoming messages in the
		// scrolling text area.
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();

		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		frame.setSize(400, 500);
		frame.setVisible(true);
	}	

	private void setUpNetworking() {
		try {
			// Make a Socket, then make a PrintWriter
			sock = new Socket("127.0.0.1", 5000); // Using local host to test the client and the server on the same machine
												 // It's called from the go() method right before displaying the app GUI
			InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
			reader = new BufferedReader(streamReader);
			// Assign the PrintWriter to writer insstance variable	
			writer = new PrintWriter(sock.getOutputStream());
			System.out.println("networking established");
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	public class SendButtonListener implements ActionListener {

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

	// This is what the thread does.
	// In the run() method, it says ina loop (as lon as what it gets form theserver is not null), reading a line
	// at a time and adding each line to the scroling text area (along with a new line character).
	public class IncomingReader implements Runnable {
		public void run() {
			String message;
			try {
				while ( (message = reader.readLine()) != null) {
					System.out.println("read " + message);
					incoming.append(message + "\n");
				}
			} catch (Exception ex) { ex.printStackTrace(); }
		}
	}


} // close outer class