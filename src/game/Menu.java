package game;

import javax.swing.*;

import utils.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Menu extends JPanel implements MouseListener, MouseMotionListener {

    private static final int BUTTON_WIDTH = 140;
    private static final int BUTTON_HEIGHT = 56;
    private static final int START_BUTTON_Y = 112;
    private static final int QUIT_BUTTON_Y = START_BUTTON_Y+200;
    private static final int BUTTON_X = 330;
    private static final int OPTION_BUTTON_Y = START_BUTTON_Y+100;

    private int startState = 0;
    private int optionState = 0;
    private int quitState = 0;

    private Image backgroundImage;
    private Game game;
    private JComboBox<String> mapSelector;

    public Menu(Game game) {
        this.game = game;
        setLayout(null);

        JLabel mapLabel = new JLabel("CHON MAP / SELECT MAP", SwingConstants.CENTER);
        mapLabel.setForeground(Color.WHITE);
        mapLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mapLabel.setBounds(280, 28, 240, 24);
        add(mapLabel);
        mapSelector = new JComboBox<>(new String[] {"Map 1 - Original", "Map 2 - Underground Maze"});
        mapSelector.setBounds(280, 58, 240, 32);
        add(mapSelector);

        loadBackground();
        loadButtonImages();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private void loadBackground() {
        try {
            backgroundImage = LoadSave.getSpriteAtlas(LoadSave.BACKGROUND_PATH);
        } catch (Exception e) {
            System.err.println("Error loading menu background: " + e.getMessage());
        }
    }

    private ArrayList<Image> startButtonFrames = new ArrayList<>();
    private ArrayList<Image> optionButtonFrames = new ArrayList<>();
    private ArrayList<Image> quitButtonFrames = new ArrayList<>();

    private void loadButtonImages() {
        String[][] frames = {
            {"start", "assets/menu/1.png", "assets/menu/2.png", "assets/menu/3.png"},
            {"option", "assets/menu/4.png", "assets/menu/5.png", "assets/menu/6.png"},
            {"quit", "assets/menu/7.png", "assets/menu/8.png", "assets/menu/9.png"},
        };

        for (String[] f : frames) {
            String type = f[0];
            for (int i = 1; i < f.length; i++) {
                Image img = LoadSave.getSpriteAtlas(f[i]);
                switch (type) {
                    case "start":
                        startButtonFrames.add(img);
                        break;
                    case "option":
                        optionButtonFrames.add(img);
                        break;
                    case "quit":
                        quitButtonFrames.add(img);
                        break;
                }
            }
        }
    }

    public void drawButton(Graphics g, int x, int y, ArrayList<Image> typeButton, int index) {
        g.drawImage(typeButton.get(index), x, y, null); 
    }

    private boolean isMouseOver(int x, int y, int width, int height, float mouseX, float mouseY) {
        if (x < mouseX && mouseX < x + width && y < mouseY && mouseY < y + height) {
            return true;
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }
        
        drawButton(g, BUTTON_X, OPTION_BUTTON_Y, optionButtonFrames, optionState);
        drawButton(g, BUTTON_X, QUIT_BUTTON_Y, quitButtonFrames, quitState);
        drawButton(g, BUTTON_X, START_BUTTON_Y, startButtonFrames, startState);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        float mouseX = e.getX();
        float mouseY = e.getY();

        startState = 0;
        optionState = 0;
        quitState = 0;

        if (isMouseOver(BUTTON_X, OPTION_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, mouseX, mouseY)) {
            optionState = 1;
        }
        if (isMouseOver(BUTTON_X, QUIT_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, mouseX, mouseY)) {
            quitState = 1;
        }
        if (isMouseOver(BUTTON_X, START_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, mouseX, mouseY)) {
            startState = 1;
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        float mouseX = e.getX();
        float mouseY = e.getY();
        
        if (isMouseOver(BUTTON_X, OPTION_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, mouseX, mouseY)) {
            optionState = 2;
        }
        if (isMouseOver(BUTTON_X, QUIT_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, mouseX, mouseY)) {
            quitState = 2;
        }
        if (isMouseOver(BUTTON_X, START_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, mouseX, mouseY)) {
            startState = 2;
        }
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        float mouseX = e.getX();
        float mouseY = e.getY();

        startState = 0;
        optionState = 0;
        quitState = 0;
        if (isMouseOver(BUTTON_X, START_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, mouseX, mouseY)) {
            game.startGame(mapSelector.getSelectedIndex() == 1 ? LoadSave.FOREST_MAP : LoadSave.DEFAULT_MAP);
        } else if (isMouseOver(BUTTON_X, OPTION_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, mouseX, mouseY)) {
            game.showOptions();
        } else if (isMouseOver(BUTTON_X, QUIT_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, mouseX, mouseY)) {
            System.exit(0);
        }
        repaint();
    }
} 
