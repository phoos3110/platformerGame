package run;

import game.Game;

public class GameMain {

	public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(Game::new);
    }
}
