package br.studio.calbertofilho.game.sounds;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;

public class MidiPlayer {

	private Sequence bgm;
	private Sequencer player;
	private Synthesizer synthesizer;
	private boolean playing;
	private float streamLength;
	private MidiChannel[] channels;

	public MidiPlayer() {
		createPlayer();
		playing = false;
	}

	public MidiPlayer(String bgm) {
		this();
		loadMusic(bgm);
	}

	private void createPlayer() {
		try {
			player = MidiSystem.getSequencer();
			synthesizer = MidiSystem.getSynthesizer();
			player.open();
			channels = synthesizer.getChannels();
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
		if ((player != null) && (player.getSequence() != null)) {
			player.setLoopCount(loops);
			player.start();
			playing = true;
		}
	}

	public void pauseMusic() {
		if ((player != null) && (player.getSequence() != null)) {
			streamLength = player.getTempoInMPQ();
			stopMusic();
		}
	}

	public void resumeMusic() {
		if ((player != null) && (player.getSequence() != null)) {
			player.start();
			player.setTempoInMPQ(streamLength);
			playing = true;
		}
	}

	public void stopMusic() {
		if ((player != null) && (player.getSequence() != null)) {
			player.stop();
			playing = false;
			player.setTempoInMPQ(0);
		}
	}

	public boolean isPlaying() {
		return playing;
	}

	public void setVolume(double volume) {
		for (MidiChannel channel : channels) {
			channel.controlChange(7, (int) (volume * 127.0));
		}
	}

}
