package utils;

import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImageUtils {
    public static Image createFlipped(Image img) {
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        BufferedImage flipped = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = flipped.createGraphics();
        g2.drawImage(img, 0, 0, w, h, w, 0, 0, h, null);
        g2.dispose();
        return flipped;
    }
} 