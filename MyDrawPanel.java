import java.awt.*;
import javax.swing.*;

/* Creating a subclass of JPanel, a widget that can be added to a frame.
 * This is a personally customized widget. */
class MyDrawPanel extends JPanel {
	
	/* The system calls the Graphics method which is a drawing surface of type Graphics
	 * that can be used to paint on it. */
	public void paintComponent(Graphics g) {
		//g.setColor(Color.orange);
		//g.fillRect(20, 50, 100, 100); // shape to paint

		//Image image = new ImageIcon("CatPicture.jpg").getImage();
		//g.drawImage(image,3,4,this);

		g.fillRect(0,0,this.getWidth(), this.getHeight());
		int red = (int) (Math.random() * 255);
		int green = (int) (Math.random() * 255);
		int blue = (int) (Math.random() * 255);

		Color randomColor = new Color(red, green, blue);
		g.setColor(randomColor);
		g.fillOval(70,70,100,100);
	}
}