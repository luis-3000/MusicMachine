import java.awt.*;
import javax.swing.*;

/* Creating a subclass of JPanel, a widget that can be added to a frame.
 * This is a personally customized widget. */
class MyDrawPanel extends JPanel {
	
	/* The system calls the Graphics method which is a drawing surface of type Graphics
	 * that can be used to paint on it. */
	public void paintComponent(Graphics g) {
		g.setColor(Color.orange);
		g.fillRect(20, 50, 100, 100); // shape to paint
	}
}