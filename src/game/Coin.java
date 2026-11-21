package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

public class Coin {
    private int x, y;
    private Image coinImage;
    private boolean collected;
    private static final int COIN_SIZE = 32;

    public Coin(Image coInImage, int x, int y) {
        this.x = x;
        this.y = y;
        this.coinImage = coInImage;
        this.collected = false;
    }

    public void draw(Graphics g, int cameraX) {
        if (!collected) {
            g.drawImage(coinImage, x - cameraX, y, COIN_SIZE, COIN_SIZE, null);  
        }
    }

    // g.setColor(Color.RED);
            // g.drawRect(x - cameraX, y, COIN_SIZE, COIN_SIZE);

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return COIN_SIZE;
    }
}
