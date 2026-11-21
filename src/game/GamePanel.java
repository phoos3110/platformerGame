package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import entities.Character;
import utils.LoadSave;

public class GamePanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener {

    private MapManager mapManager;
    private Character player;
    private int cameraX;
    private Game game;
    private Timer timer;

    public void setRunning(boolean running) {
        if (running) timer.start();
        else {
            timer.stop();
            player.clearInput();
        }
    }

    public GamePanel(String mapFilePath, Game game) {
        this.game = game;
        initializeComponents(mapFilePath);
        setupGameLoop();
    }

    private void initializeComponents(String mapFilePath) {
        mapManager = new MapManager(mapFilePath, this);
        player = new Character(mapManager.tileSize, 9 * mapManager.tileSize, this);
        player.setMapData(mapManager.getMapData(), mapManager.tileSize);

        setPreferredSize(new Dimension(mapManager.mapWidth * mapManager.tileSize / 2,mapManager.mapHeight * mapManager.tileSize));

        addKeyListener(this);
        setFocusable(true);
    }

    private void setupGameLoop() {
        timer = new Timer(17, e -> {
            player.update();
            mapManager.update();
            repaint();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateCamera();
        mapManager.draw(g, cameraX);
        player.draw(g, cameraX);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Coins: " + mapManager.getCollectedCoins() + "/" + mapManager.getTotalCoins(), 20, 30);

        if (mapManager.isGameWon()) {
        	drawWinScreen(g);
        } else if (player.isDead()) {
        	drawGameOver(g);
        }
    }

    private void drawGameOver(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());

        BufferedImage gameOverBg = LoadSave.getSpriteAtlas(LoadSave.Game_over_background);
        int width = gameOverBg.getWidth() * 2;
        int height = gameOverBg.getHeight() * 2;
        int x = (getWidth() - width) / 2;
        int y = (getHeight() - height) / 2;
        g.drawImage(gameOverBg, x, y, width, height, null);

        g.setColor(Color.PINK);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String gameOverText = "GAME OVER";
        FontMetrics metrics = g.getFontMetrics();
        int textX = (getWidth() - metrics.stringWidth(gameOverText)) / 2;
        int textY = y + height / 2;
        g.drawString(gameOverText, textX, textY);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        String restartText = "Press R to Restart";
        metrics = g.getFontMetrics();
        textX = (getWidth() - metrics.stringWidth(restartText)) / 2;
        textY += 40;
        g.drawString(restartText, textX, textY);
        String escText = "Press ESC to get back to the menu";
        metrics = g.getFontMetrics();
        textX = (getWidth() - metrics.stringWidth(escText)) / 2;
        textY += 30;
        g.drawString(escText, textX, textY);
    }

    private void drawWinScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String winText = "YOU WIN!";
        FontMetrics metrics = g.getFontMetrics();
        int textX = (getWidth() - metrics.stringWidth(winText)) / 2;
        int textY = getHeight() / 2;
        g.drawString(winText, textX, textY);

        g.setFont(new Font("Arial", Font.PLAIN, 24));
        String restartText = "Press R to Play Again";
        metrics = g.getFontMetrics();
        textX = (getWidth() - metrics.stringWidth(restartText)) / 2;
        textY += 40;
        g.drawString(restartText, textX, textY);
        String escText = "Press ESC to get back to the menu";
        metrics = g.getFontMetrics();
        textX = (getWidth() - metrics.stringWidth(escText)) / 2;
        textY += 30;
        g.drawString(escText, textX, textY);
    }

    private void updateCamera() {
        int screenWidth = getWidth();
        cameraX = player.getX() - screenWidth / 2;
        cameraX = Math.max(0, Math.min(cameraX,mapManager.mapWidth * mapManager.tileSize - screenWidth));
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_R && (player.isDead() || mapManager.isGameWon())) {
            mapManager.reset(LoadSave.DEFAULT_MAP);
            player.setMapData(mapManager.getMapData(), mapManager.tileSize);
            player.reset();
            cameraX = 0;
            repaint();
        } 
        else if (e.getKeyCode() == KeyEvent.VK_ESCAPE && (player.isDead() || mapManager.isGameWon())) {
            mapManager.reset(LoadSave.DEFAULT_MAP);
            player.setMapData(mapManager.getMapData(), mapManager.tileSize);
            player.reset();
            cameraX = 0;
            repaint();
            getGame().getSoundManager().stopSong();
            getGame().getSoundManager().playSong(1);
            game.showMenu();
        } 
        else {
            player.keyPressed(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        player.keyReleased(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public Game getGame() {
        return game;
    }

    public Character getPlayer() {
        return player;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }
}
