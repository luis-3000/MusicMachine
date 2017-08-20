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

	public void setUpMidi() {
		// The usual MIDI set-up for getting the Sequencer, the Sequence, and the Track.
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequence = new Sequence(Sequence.PPQ,4);
			track.sequence.crateTrack();
			sequencer.setTemptoInBPM(120);
		} catch (Exception e) { e.printStackTrace(); }
	}

	/* This is qhere it all happens. here I cturn checkbox state into MIDI events, and add
	 * them to the Track
	*/	
	public void buildTrackAndStart() {
		int[] trackList = null;

		sequence.deleteTrack(track); // get rid of the old track, make a fresh one
		track = sequence.createTrack();

		// Do this for each of the 16 ROWS (i.e., Bass, Congo, etc)
		for(int i = 0; i < 16; i++) {
			trackList = new int[16];

			int key = instruments[i]; // Set the 'key' that represents which instrument this
									  // is (Bass, Hi-Hat, etx. the instruments array hols the
									  // actual MIDI numbers for each instrument)

			for(int j = 0; j < 16; j++) { // Do this for each fot the BEATS for this row
				JCheckBox jc  = (JCheckBox) checkboxList.get(j + (16*i));
				if(jc.isSelected()) {
					trackList[i] = key;
				} else {
					trackList[j] = 0;
				}
			}

			/* For this instrument, and for all 16 beats, make events and add them to the track*/
			makeTracks9trackList); 
			track.add(makeEvent(176,1,127,0,16));
		}

		/* I always want to make sure that there IS an event at beat 16 (it goes 0 to 15). 
		   Otherwise, the BeatBox might not go the full 16 beats before it starts over. */
		track.add(makeEvent(192,9,1,0,15));
		try {
			sequencer.setSequence(sequence);
			sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
			sequencer.start();
			sequencer.setTempoInBPM(120);
		} catch (Exception e) { e.printStackTrace(); }
	}

	// INNER CLASS
	public class MyStartListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			buildTrackAndStart();
		}
	}

	// ANOTHER INNER CLASS
	public class MyStopListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			sequencer.stop();
		}
	}

	/* The Tempo Factor scales the sequencer's tempo by the factor provided. The default is 
	   1.0, so I'm adjusting +/- 3% per click.   */
	public class MyUpTempoListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float) (tempoFactor * 1.03));
		}
	}

	public class MyDownTempoListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float) (tempoFactor * 0.97));
		}
	}

	/* This makes events for one instrument at a time, for all 16 beats. So it might get an int[]
	   for the Bass drum, and each indes in the array will hold either the key of that instrument,
	   or a zero. If it's a zero, the instrument isn't supposed to play at that beat. Otherwise,
	   I make an event and add it to the track. */
	public void makeTracks(int[] list) {
		for(int i = 0; i < 16; i++) {
			int key = list[i];
			if(key != 0) {
				track.add(makeEvent(144,9,key,100,i)); // Make the NOTE ON, and 
				track.add(makeEvent(128,9,key,100,i+1)); // NOTE OFF events, and add them
														 // to the track.
			}
		} 
	}

	public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
		MidiEvent event  = null;
		try {
			ShortMessage a = new ShortMessage();
			a.setMessage(comd, chan, one, two);
			event = new MidiEvent(a, tick);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}