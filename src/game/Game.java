package game;

import javax.swing.JFrame;

import utils.LoadSave;

import java.awt.CardLayout;
import java.awt.Dimension;

public class Game {
    private static final String TITLE = "Platform Game";
    private static final String MENU_CARD = "MENU";
    private static final String GAME_CARD = "GAME";
    private static final String Options_Card = "OPTIONS";

    private JFrame frame;
    private GamePanel gamePanel;
    private Menu menu;
    private CardLayout cardLayout;
    private SoundManager soundManager;
    private Options options;

    public Game() {
        this.soundManager = new SoundManager();
        initializeFrame();
        initializeComponents();
        setupFrame();
    }

    private void initializeFrame() {
        frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        cardLayout = new CardLayout();
        frame.setLayout(cardLayout);
    }

    private void initializeComponents() {
        menu = new Menu(this);

        frame.add(menu, MENU_CARD);

        options = new Options(this);

        frame.add(options, Options_Card);

        gamePanel = new GamePanel(LoadSave.DEFAULT_MAP, this);
        frame.add(gamePanel, GAME_CARD);
    }

    private void setupFrame() {
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        soundManager.playSong(1);
    }

    public void startGame() {
        soundManager.playSong(0);
        cardLayout.show(frame.getContentPane(), GAME_CARD);
        gamePanel.setRunning(true);
        gamePanel.requestFocusInWindow();
    }

    public void showMenu() {
        gamePanel.setRunning(false);
        cardLayout.show(frame.getContentPane(), MENU_CARD);
    }

    public void showOptions() {
        gamePanel.setRunning(false);
        cardLayout.show(frame.getContentPane(), Options_Card);
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }
}
