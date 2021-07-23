package br.studio.calbertofilho.game.sounds;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;

public class MidiPlayer {

	private Sequencer player;
	private Synthesizer synthesizer;
	private MidiChannel[] channels;
	private Receiver receiver;
	private Sequence bgm;
	private boolean playing;
	private float streamLength;

	public MidiPlayer() {
		playing = false;
		player = null;
		synthesizer = null;
		receiver = null;
		createPlayer();
	}

	public MidiPlayer(String bgm) {
		this();
		loadMusic(bgm);
	}

	private void createPlayer() {
		try {
			player = MidiSystem.getSequencer();
			player.open();
			synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();
			channels = synthesizer.getChannels();
			if (synthesizer.getDefaultSoundbank() == null)
				receiver = MidiSystem.getReceiver();
			else
				receiver = synthesizer.getReceiver();
			player.getTransmitter().setReceiver(receiver);
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void loadMusic(String path) {
		try {
			bgm = MidiSystem.getSequence(new File(path));
			player.setSequence(bgm);
		} catch (InvalidMidiDataException | IOException e) {
			e.printStackTrace();
		}
	}

	public void playContinuousLoopMusic() {
		playMusic(Sequencer.LOOP_CONTINUOUSLY);
	}

	public void playMusic(int loops) {
		if (isReady()) {
			player.setLoopCount(loops);
			player.start();
			playing = true;
		}
	}

	public void pauseMusic() {
		if (isReady()) {
			streamLength = player.getTempoInMPQ();
			stopMusic();
		}
	}

	public void resumeMusic() {
		if (isReady()) {
			player.start();
			player.setTempoInMPQ(streamLength);
			playing = true;
		}
	}

	public void stopMusic() {
		if (isReady()) {
			player.stop();
			playing = false;
			player.setTempoInMPQ(0);
		}
	}

	public boolean isPlaying() {
		return playing;
	}

	public boolean isReady() {
		return ((player != null) && (player.getSequence() != null));
	}

	public void setVolume(double volume) {
		for (MidiChannel channel : channels) {
			channel.controlChange(7, (int) (volume * 127.0));
		}
	}

}
