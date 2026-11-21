package utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class LoadSave {
    public static final String Game_background = "BG.png";
    public static final String Menu_background = "menu/menu_background.png";
    public static final String Options_backgound = "options_background.png";
    public static final String Sound_buttons = "sound_button.png";
    public static final String Urm_buttons = "urm_buttons.png";
    public static final String Game_over_background = "game_over_background.png";
    public static final String Game_over_text = "game_over.png";
    public static final String Coin_image = "coin_image.png";
    public static final String DEFAULT_MAP = "assets/maps/map02.tmx";
    public static final String FOREST_MAP = "assets/maps/map03.tmx";
    public static final String BACKGROUND_PATH = "assets/menu/menu_background.png";
    private static final Map<String, BufferedImage> images = new HashMap<>();

    public static synchronized BufferedImage getSpriteAtlas(String fileName) {
        String path = fileName.startsWith("assets/") ? fileName.substring(7) : fileName;
        return images.computeIfAbsent(path, key -> {
            try (InputStream stream = LoadSave.class.getResourceAsStream("/" + key)) {
                if (stream == null) throw new IllegalStateException("Missing image: " + key);
                BufferedImage image = ImageIO.read(stream);
                if (image == null) throw new IllegalStateException("Invalid image: " + key);
                return image;
            } catch (IOException e) {
                throw new IllegalStateException("Cannot load image: " + key, e);
            }
        });
    }
}
