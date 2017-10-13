/* Author: Jose Luis Castillo
 * Date: 8/18/2017
*/

import java.awt.*;
import javax.swing.*;
import java.io.*;
import javax.sound.midi.*;
import java.util.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.event.*;

public class BeatBox {
	
	JFrame theFrame;
	JPanel mainPanel;
	JList incomingList;
	JTextField userMessage;
	ArrayList<JCheckBox> checkboxList; // Store the features checkboxes in an ArrayList
	String userName;
	ObjectOutputStrem out;
	ObjectInputStream in;
	HashMapr<String, boolean[]> otherSeqsMap = new HashMap<String, boolean[]>();

	Sequencer sequencer;
	Sequence sequence;
	Sequence mySequence = null;
	Track track;

	String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acustic Snare",
								"Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo", 
								"Maracas", "Whistle", "Low Conga", "Cowbell", "Vibraslap",
							    "Low-mid Tom", "High Agogo", "Open Hi Conga"};
	
	// These represent the actual drum keys.
	// The drum channel is like a piano, except each 'key' on the piano is a different drum.
	// So, the number '35' is the key for the Bass drum, 42 is Closed Hi-Hat, etc.						    
	int[] instruments = (35,42,46,38,49,39,50,50,70,72,64,56,58,47,67,63);							    	

	public static void main(String[] args) {
		new BeatBox().startUp(args[0]); // args[0] is the user ID/screen name
	}	

	public void startUp(String name) {
		userName = name;
		// Open connection to the server
		try {
			Socket sock = new Socket("127.0.0.1", 4242);
			out = new ObjectOutputStrem(sock.getOutputStream());
			in = new ObjectInputStream(sock.getInputStream());
			Thread remote = new Thread(new RemoteReader());
			remote.start();
		} catch (Exception ex)  { 
			System.out.println("couldn't connect - you'll have to play alone.")
		}
		setUpMidi();
		buildGUI();
	}

	public void buildGUI() {
		theFrame = new JFrame("Cyber BeatBox");
		BorderLayout layout = new BorderLayout();
		JPanel background = new JPanel(layout);
		background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		checkboxList = new ArrayList<JCheckBox>();

		Box buttonBox = new Box(BoxLayout.Y_AXIS);
		JButton start = new JButton("Start");
		start.addActionListener(new MyStartListener());
		buttonBox.add(start);

		JButton stop = new JButton("Stop");
		stop.addActionListener(new MyStopListener());
		buttonBox.add(stop);

		JButton upTempo = new JButton("Tempo Up");
		upTempo.addActionListener(new MyUpTempoListener());
		buttonBox.add(upTempo);

		JButton downTempo = new JButton("Tempo Down");
		downTempo.addActionListener(new MyDownTempoListener());
		buttonBox.add(downTempo);

		JButton sendIt = new JButton("sendIt");
		sendIt.addActionListener(new MySendListener());
		buttonBox.add(sendIt);

		userMessage = new JTextField();

		buttonBox.add(userMessage);

		/* This is where the incoming messages are displayed. Only instead of a normal chat where users just LOOK at the messages,
		in this app users can SELECT a message from the list to load and play the attached beat platform. */
		incomingList = new JList();
		incomingList.addListSelectionListener(new MyListSelectionListener());
		incomingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane theList = new JScrollPane(incomingList);
		buttonBox.add(theList);
		incomingList.setListData(listVector); // no data to start with


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

	/* This is where it all happens. Here I turn checkbox states into MIDI events, and add
	 * them to the Track
	*/	
	public void buildTrackAndStart() {
		ArrayList<Integer> trackList = null; // this will hold the instruments for each list
		sequence.deleteTrack(track); // get rid of the old track, make a fresh one
		track = sequence.createTrack();

		// Do this for each of the 16 ROWS (i.e., Bass, Congo, etc)
		// Build a track by wlaking through the checkboxes to get their state, and mapping that to an instrument (mand making the MidiEvent
		// for it). This is pretty complex, but required to make this work.
		for(int i = 0; i < 16; i++) {

			trackList = new ArrayList<Integer>();

			for(int j = 0; j < 16; j++) { // Do this for each fot the BEATS for this row
				JCheckBox jc  = (JCheckBox) checkboxList.get(j + (16*i));
				if(jc.isSelected()) {
					int key = instruments[i]; // Set the 'key' that represents which instrument this
									  		  // is (Bass, Hi-Hat, etx. the instruments array hols the
									  		  // actual MIDI numbers for each instrument)
					trackList.add(new Integer(key));
				} else {
					trackList.add(null); // because this slot should be emtpy in the track
				}
			}

			/* For this instrument, and for all 16 beats, make events and add them to the track*/
			makeTracks(trackList); 
			track.add(makeEvent(176,1,127,0,16));
		}

		/* I always want to make sure that there IS an event at beat 16 (it goes 0 to 15). 
		   Otherwise, the BeatBox might not go the full 16 beats before it starts over. */
		track.add(makeEvent(192,9,1,0,15)); // So we always go to the full 16 beats
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

	/* Code to Serialize a pattern */
	public class  MySendListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			// make an arraylist of just the STATE of the checkboxes
			boolean[] checkboxState = new boolean[256]; //state of each checkbox
			for(int i = 0; i < 256; i++) {
				JCheckBox check = (JCheckBox) checkboxList.get(i);
				if(check.isSelected()) {
					checkboxState[i] = true;
				}
			}

			// Here, we serialize two objects, the String message and the beat pattern, and write those two
			// objects to the socket output stream (to the server)
			Strring messageToSend = null;
			try {out.writeObject(userName + nextNum++ + ": " + userMessage.getText());
				out.writeObject(checkboxState);
			} catch (Exception ex) {
				System.out.println("Sorry user. Could not sent it to the server.");
			}
			userMessage.setText("");
		}
	}

	/* A ListSelectionListener tells us when the user made a selection on the list of messages.
	 * When the user selects a message, we IMMEDIATELY load the associated beat pattern (it's in the HashMap calle otherSeqsMap)
	 * and starts playing it. There's some if tests because of little quirky things about getting ListSelectionEvents. */
	public class MyListSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent le) {
			if (!le.getValueIsAdjusting()) {
				String selected = (String) incomingList.getSelectedValue();
				if (selected != null) {
					// now go to the map, and change the sequence
					boolean[] selectedState = (boolean[]) otherSeqsMap.get(selected);
					changeSequence(selectedState);
					sequencer.stop();
					buildTrackAndStart();
				}
			}
		}
	}

	public class RemoteReader implements Runnable {
		boolean[] checkboxState = null;
		String nameToShow = null;
		Object obj = null;
		public void run() {
			try {
				// this is the thread job -- read in data from the server. 'Data' will always be two serialized
				// objects: the String message and the beat pattern (and ArrayList of checkbox state values).
				while ((obj = in.readObject()) != null) {
					System.out.println("got an object from the server");
					System.out.println(obj.getClass());
					// When a message comes in, we read (deserialize) the two objects (the message and the ArrayList of Boolean
					// checkbox state values) and add it to the JList component. Adding to a JList is a two-step thing: you keep a Vector
					// of the lists data (Vector is an old-fashioned ArrayList), and then tell the JList to use that Vector as it's
					// source fow that to display in the list.
					String nameToShow = (String) obj;
					checkboxState = (boolean[]) in.readObject();
					otherSeqsMap.put(nameToShow, checkboxState);
					listVector.add(nameToShow);
					incomingList.setListData(listVector);
				}

			} catch (Exception ex) { ex.printStackTrace(); }
		}
	}

	public class MyPlayMineListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			if (mySequence != null) {
				sequence = mySequence; // restore to my original
			}
		}
	}

	/* This method is calle dwhen the user selects something form the list. We IMMEDIATELY change the
	 * pattern to the ont they selected. */
	public void changeSequence(boolean[] checkboxState) {
		for(int i = 0; i < 256; i++) {
			JCheckBox check = (JCheckBox) checkboxList.get(i);
			if (checkboxState[i]) {
				check.setSelected(true);
			} else {
				check.setSelected(false);
			}
		}
	}

	/* This makes events for one instrument at a time, for all 16 beats. So it might get an int[]
	   for the Bass drum, and each index in the array will hold either the key of that instrument,
	   or a zero. If it's a zero, the instrument isn't supposed to play at that beat. Otherwise,
	   I make an event and add it to the track. */
	public void makeTracks(int[] list) {
		Iterator it = list.iterator();
		for(int i = 0; i < 16; i++) {
			Integer num = (Integer) it.next();
			if(num != null) {
				int numKey = num.intValue();
				track.add(makeEvent(144, 9, numKey, 100, i)); // Make the NOTE ON, and 
				track.add(makeEvent(128, 9, numKey, 100, i + 1)); // NOTE OFF events, and add them
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
			return event;
		}
	}



	/* Code to restore a BeacBox pattern. */
	public class MyReadInListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			boolean[] checkboxState = null;
			try {
				FileInputStream fileIn = new FileInputStream(new File("Checkbox.ser"));
				ObjectInputStream is = new ObjectInputStream(fileIn);
				checkboxState = (boolean[]) is.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			}

			for(int i = 0; i < 256; i++) {
				JCheckBox check = (JCheckBox) checkboxList.get(i);
				if(checkboxState[i]) {
					check.setSelected(true);
				} else {
					check.setSelected(false);
				}
			}

			sequencer.stop();
			buildTrackAndStart();
		}
	}
}