/* First step of the entire project is to create a Sequencer. */

import javax.sound.midi.*; // the sound package

public class MusicTest1 {

	public void play() {

		// Wrapping the risky Sequencer object request in a try/catch block to handle potential problems
		try {  
			Sequencer sequencer = MidiSystem.getSequencer(); // Ask the MidiSystem for a Sequencer object

			System.out.println("We got a sequencer");
		} catch (MidiUnavailableException ex) {
			System.out.println("Sequencer object not available due to resource restrictions." +
				                         "This could mean the Sequencer object is already in use.");
		}
	}

	public static void main(String[] args) {
		MusicTest1 mt = new MusicTest1(); 
		mt.play();
	}

}