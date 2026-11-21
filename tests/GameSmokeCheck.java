import game.*;
import java.lang.reflect.*;
import java.util.*;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.event.KeyEvent;
public class GameSmokeCheck {
  public static void main(String[] args) throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      try {
        GamePanel panel = new GamePanel(utils.LoadSave.DEFAULT_MAP, null);
        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        TimerCheck((javax.swing.Timer) timerField.get(panel), false);
        panel.setRunning(true);
        TimerCheck((javax.swing.Timer) timerField.get(panel), true);
        panel.setRunning(false);
        Field coinsField = MapManager.class.getDeclaredField("coins");
        coinsField.setAccessible(true);
        for (int run = 0; run < 100; run++) {
          MapManager.gameWon = true;
          panel.keyPressed(new KeyEvent(panel, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_R, 'R'));
          Field data = entities.Character.class.getDeclaredField("mapData");
          data.setAccessible(true);
          if (data.get(panel.getPlayer()) != panel.getMapManager().getMapData()) throw new AssertionError("Stale player map");
          Set<String> positions = new HashSet<>();
          for (Coin coin : (List<Coin>) coinsField.get(panel.getMapManager())) {
            if (coin.getY() < 0 || !positions.add(coin.getX() + ":" + coin.getY())) throw new AssertionError("Invalid or duplicate coin");
          }
          if (positions.size() != 10) throw new AssertionError("Expected 10 coins");
        }
        if (utils.LoadSave.getSpriteAtlas("BG.png") != utils.LoadSave.getSpriteAtlas("BG.png")) throw new AssertionError("Image cache failed");
        System.out.println("PASS: timer lifecycle, 100 restarts, coin uniqueness, player map refresh, image cache");
      } catch (Exception e) { throw new RuntimeException(e); }
    });
    SoundManager sound = new SoundManager();
    for (String name : new String[] {"songs", "effects"}) {
      Field field = SoundManager.class.getDeclaredField(name);
      field.setAccessible(true);
      Clip[] clips = (Clip[])field.get(sound);
      for (Clip clip : clips) if (clip != null) clip.close();
      Arrays.fill(clips, null);
    }
    sound.playSong(0); sound.stopSong(); sound.playEffect(1); sound.stopEfect();
    sound.toggleSongMute(); sound.toggleSongMute(); sound.toggleEffectMute(); sound.toggleEffectMute();
    System.out.println("PASS: missing audio clips handled safely");
    System.exit(0);
  }
  static void TimerCheck(javax.swing.Timer timer, boolean expected) {
    if (timer.isRunning() != expected) throw new AssertionError("Unexpected timer state");
  }
}
