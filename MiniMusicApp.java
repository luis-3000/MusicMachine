import javax.sound.midi.*; // MIDI package

public class MiniMusicApp {
	
	public static void main(String[] args) {
		MiniMusicApp mini = new MiniMusicApp();
		mini.play();
	}

	public void play() {
		try {
			Sequencer player = MidiSystem.getSequencer(); // Get a sequencer
			player.open(); 

			Sequence seq = new Sequence(Sequence.PPQ, 4); // Maque a Sequence

			Track track = seq.createTrack(); // Ask the sequence for a track
											 // The track lives in the Sequence, and the
											 // MIDI track lives in the Track

			// Put some MidiEvents into the Track. 
			ShortMessage msg1 = new ShortMessage();
			msg1.setMessage(144, 1, 44, 100);
			MidiEvent noteOn = new MidiEvent(msg1, 1);
			track.add(noteOn);

			ShortMessage msg2 = new ShortMessage();
			msg2.setMessage(128, 1, 44, 100);
			MidiEvent noteOff = new MidiEvent(msg2, 16);
			track.add(noteOff);

			// Give the Sequenc to the Sequencer (similar to putting the CD in the CD player)
			player.setSequence(seq);

			//Start the Sequencer (akin to pushing PLAY)
			player.start();

		} catch (Exception ex) {
			ex.printStackTrace(); //Let the Sofware Engineer where something went wrong
		}
	}
}