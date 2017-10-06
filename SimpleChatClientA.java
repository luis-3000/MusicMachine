public class SimpleChatClientA {
	
	JTextField outgoing;
	PrintWriter writer;
	Socket sock;

	public void go() {
		// This sections will be used to make the GUI and register a listerner with the send
		// button

		// Will call the setUpNetworking() method
	}	

	private void setUpNetworking() {
		// Make a Socket, then make a PrintWriter

		// Assign the PrintWriter to writer insstance variable
	}

	public class SendButtonListener implements ActionsListener {

		public void actionPerformed(ActionEvent ev) {
			// Get the text from the text field and sent it to the server
			// using the writer (a PrintWriter)
		}

	} // close SendButtonListener inner class

} // close outer class