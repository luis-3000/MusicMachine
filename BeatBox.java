import java.awt.*;
import javax.swing.*;
import javax.sound.midi.*;
import java.util.*;
import java.awt.event.*;

/* Author: Jose Luis Castillo
 * Date: 8/18/2017
*/

public class BeatBox {
	
	JPanel mainPanel;
	ArrayList<JCheckBox> checkboxList; // Store the features checkboxes in an ArrayList
	Sequencer sequencer;
	Sequence sequence;
	Track track;
	JFrame theFrame;

	String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acustic Snare",
								"Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo", 
								"Maracas", "Whistle", "Low Conga", "Cowbell", "Vibraslap",
							    "Low-mid Tom", "High Agogo", "open Hi Conga"};
	
	// These represent the actual drum keys.
	// The drum channel is like a piano, except each 'key' on the piano is a different drum.
	// So, the number '35' is the key for the Bass drum, 42 is Closed Hi-Hat, etc.						    
	int[] instruments = (35,42,46,38,49,39,50,50,70,72,64,56,58,47,67,63);							    	

	public static void main(String[] args) {
		new BeatBox2().buildGUI();
	}	

	public void buildGUI() {
		theFrame = new JFrame("Cyber BeatBox");
		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BorderLayout layout = new BorderLayout();
		JPanel background = new JPanel(layout);
		background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		checkboxList = new ArrayList<JCheckBox>();
		Box buttonBox = new Box(BoxLayout.Y_AXIS);

		JButton start = new JButton("Start");
		start.addActionListener(new MyStartListener());
		buttonBox.add(start);

		JButton stop = new JButton("Stop");
		stop.addActionListener(new MyStartListener());
		buttonBox.add(stop);

		JButton upTempo = new JButton("Tempo Up");
		upTempo.addActionListener(new MyUpTemptoListener());
		buttonBox.add(upTempo);

		JButton downTempo = new JButton("Tempo Down");
		downTempo.addActionListener(new MyDownTempoListener());
		buttonBox.add(downTempo);

		Box nameBox = new Box(BoxLayout.Y_AXIS);
		for(int i = 0; i < 16; i++) {
			nameBox.add(new Label(instrumentNames[i]));
		}

		background.add(BorderLayout.EAST, buttonBox);
		background.add(BorderLayout.WEST, nameBox);

		theFrame.getContentPane().add(background);

		GridLayout grid = new GridLayout(16,16);
		grid.setVGap(1);
		grid.setHgap(2);
		mainPanel = new JPanel(grid);
		background.add(BorderLayout.CENTER, mainPanel);

		// Make checkboxes, set them to false (so they aren't checked) and add them to
		// the ArrayList, AND to the GUI panel
		for(int i = 0; i < 256; i++) {
			JCheckBox c = new JCheckBox();
			c.setSelected(false);
			checkboxList.add(c);
			mainPanel.add(c);
		}

		setUpMidi();

		theFrame.setBounds(50,50,300,300);
		theFrame.pack();
		theFrame.setVisible(true);
	}

	
}