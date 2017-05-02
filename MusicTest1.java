/* First step of the entire project is to create a Sequencer. */

import javax.sound.midi.*; // the sound package

public class MusicTest1 {

	public void play() {
		Sequencer sequencer = MidiSystem.getSequencer();

		System.out.println("We got a sequencer");

	}

	public static void main(String[] args) {
		MusicTest1 mt = new MusicTest1(); // Ask the MidiSystem for a Sequencer object
		mt.play();
	}

}