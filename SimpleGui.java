import javax.swing.*;

public class SimpleGui {
	
	public static void main(Strig[] args) {
		// Create a frame
		JFrame frame = new JFrame();
		// Create a new button
		JButton button = new JButton("Click me");

		// Make the program quit as soon as the user clicks on the close window mark
		// If left out, the window will be sit there forever
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add the button to the frame's content pane
		frame.getContentPane().add(button);

		// Give the frame a size in pixels
		frame.setSize(300, 300);

		// Finally, make it visible
		frame.setVisible(true);
	}
}