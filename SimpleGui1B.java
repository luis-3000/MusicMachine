import javax.swing.*;
import java.awt.event.*; // new inport statement for the package that actionListener and ActionEvent are in.

/* Any instance of SimpleGui1B IS-A ActionListener. */
public class SimpleGui1B implements ActionListener {
	
	JButton button;

	public static void main(String[] args) {
		SimpleGui1B gui = new SimpleGui1B();
		gui.go();
	}

	public void go() {
		JFrame frame = new JFrame();
		button = new JButton("Click me");

		/* Register my interest with the 'button'. This says to the button, "Add me to
		 * your list of 'listeners' objects. 
		 * The argument passed in MUST be an object from a class that implements ActionListener ! */
		button.addActionListener(this);

		frame.getContentPane().add(button);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300,300);
		frame.setVisible(true);
	}

	/* Implement the ActionListener interface's actionPerformed() method. This is the
	 * actual event-handling method. 
	 * The 'button' calls this method to let me know that an event happened. It sends me an
	 * ActionEvent object as the argument, which is not used for now. Knowing the event happened
	 * is enoughinfor for now. */
	public void actionPerformed(ActionEvent event) {
		button.setText("I've been clicked!");
	}
}