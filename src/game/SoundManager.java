package game;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class SoundManager {
    public static int playing = 0, menu = 1;
    public static int collect = 0, jump = 1, die = 2;
    private final Clip[] songs, effects;
    private int currentEffectId, currentSongId;
    private boolean effectMute, songMute;

    public SoundManager() {
        songs = new Clip[] { getClip("playing_theme"), getClip("menu_theme") };
        effects = new Clip[] { getClip("collect"), getClip("jump"), getClip("die") };
    }

    private Clip getClip(String name) {
        URL url = getClass().getResource("/sounds/" + name + ".wav");
        if (url == null) {
            System.err.println("Missing sound: " + name);
            return null;
        }
        Clip clip = null;
        try (AudioInputStream audio = AudioSystem.getAudioInputStream(url)) {
            clip = AudioSystem.getClip();
            clip.open(audio);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | IllegalArgumentException | SecurityException e) {
            if (clip != null) clip.close();
            System.err.println("Cannot load sound " + name + ": " + e.getMessage());
            return null;
        }
    }

    public void playEffect(int effect) {
        currentEffectId = effect;
        Clip clip = effects[effect];
        if (clip == null || effectMute) return;
        clip.stop();
        clip.setMicrosecondPosition(0);
        clip.start();
    }

    public void stopEfect() {
        Clip clip = effects[currentEffectId];
        if (clip != null) clip.stop();
    }

    public void playSong(int song) {
        stopSong();
        currentSongId = song;
        Clip clip = songs[song];
        if (clip == null || songMute) return;
        clip.setMicrosecondPosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stopSong() {
        Clip clip = songs[currentSongId];
        if (clip != null) clip.stop();
    }

    public int getCurrentEffectId() { return currentEffectId; }
    public int getCurrentSongId() { return currentSongId; }

    public void toggleEffectMute() {
        effectMute = !effectMute;
        if (effectMute) {
            for (Clip clip : effects) if (clip != null) clip.stop();
        }
    }

    public void toggleSongMute() {
        songMute = !songMute;
        if (songMute) stopSong();
        else playSong(currentSongId);
    }
}
