import game.GamePanel;
import utils.LoadSave;
import java.lang.reflect.*;
import java.util.*;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;

/** Checks jump connectivity using the player's actual collision and gravity code. */
public class MapClimbCheck {
    record Surface(int left, int right, int row) {}

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try { check(); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    static void check() throws Exception {
        GamePanel panel = new GamePanel(LoadSave.FOREST_MAP, null);
        entities.Character player = panel.getPlayer();
        int[][] map = panel.getMapManager().getMapData();
        List<Surface> surfaces = new ArrayList<>();
        for (int row = 1; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                if (!player.isSolidTile(map[row][col]) || player.isSolidTile(map[row - 1][col])) continue;
                int left = col;
                while (col + 1 < map[row].length && player.isSolidTile(map[row][col + 1])
                        && !player.isSolidTile(map[row - 1][col + 1])) col++;
                surfaces.add(new Surface(left, col, row));
            }
        }
        Field x = field("x"), y = field("y"), xs = field("xSpeed"), ys = field("ySpeed"), ground = field("onGround");
        Method step = entities.Character.class.getDeclaredMethod("updatePosition");
        step.setAccessible(true);
        Set<Surface> reachable = new HashSet<>();
        boolean[][] reachableCoinTiles = new boolean[map.length][map[0].length];
        ArrayDeque<Surface> queue = new ArrayDeque<>();
        for (Surface s : surfaces) {
            if (s.row * 32 == player.getY() + 32 && player.getX() >= s.left * 32 && player.getX() <= s.right * 32) {
                reachable.add(s);
                queue.add(s);
            }
        }
        while (!queue.isEmpty()) {
            Surface source = queue.remove();
            for (int start = source.left * 32; start <= source.right * 32; start += 8) {
                for (int direction : new int[] {-1, 1}) {
                    for (int doubleFrame : new int[] {-1, 6, 9, 12, 16, 22}) {
                        x.setInt(player, start);
                        y.setInt(player, source.row * 32 - 32);
                        xs.setInt(player, 4 * direction);
                        ys.setInt(player, -10);
                        ground.setBoolean(player, false);
                        for (int frame = 0; frame < 180; frame++) {
                            if (frame == doubleFrame) ys.setInt(player, -12);
                            step.invoke(player);
                            for (int cy = player.getY() / 32; cy <= (player.getY() + 31) / 32; cy++) {
                                for (int cx = player.getX() / 32; cx <= (player.getX() + 31) / 32; cx++) {
                                    reachableCoinTiles[cy][cx] = true;
                                }
                            }
                            int waterRow = player.getY() / 32 + 1;
                            int waterCol = player.getX() / 32;
                            if (waterRow < map.length && map[waterRow][waterCol] == 18) break;
                            if (ground.getBoolean(player)) {
                                int row = (player.getY() + 32) / 32;
                                for (Surface target : surfaces) {
                                    if (target.row == row && player.getX() + 31 >= target.left * 32
                                            && player.getX() < (target.right + 1) * 32 && reachable.add(target)) queue.add(target);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        List<Surface> missing = surfaces.stream().filter(s -> !reachable.contains(s)).toList();
        if (!missing.isEmpty()) throw new AssertionError("Unreachable platforms: " + missing);
        Field coins = game.MapManager.class.getDeclaredField("coins");
        coins.setAccessible(true);
        for (int run = 0; run < 100; run++) {
            panel.selectMap(LoadSave.FOREST_MAP);
            for (Object item : (List<?>) coins.get(panel.getMapManager())) {
                game.Coin coin = (game.Coin) item;
                if (!reachableCoinTiles[coin.getY() / 32][coin.getX() / 32]) {
                    throw new AssertionError("Unreachable coin: " + coin.getX() + "," + coin.getY());
                }
            }
        }

        // Render at the bottom and top, verifying that vertical scrolling also moves the player.
        panel.selectMap(LoadSave.FOREST_MAP);
        panel.setSize(800, 480);
        BufferedImage image = new BufferedImage(800, 480, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D graphics = image.createGraphics();
        panel.paint(graphics);
        Field camera = GamePanel.class.getDeclaredField("cameraY");
        camera.setAccessible(true);
        if (camera.getInt(panel) <= 0) throw new AssertionError("Camera did not follow lower spawn");
        y.setInt(player, 100);
        panel.paint(graphics);
        if (camera.getInt(panel) != 0) throw new AssertionError("Camera did not follow upper floors");
        graphics.dispose();
        System.out.println("PASS: all " + surfaces.size() + " surfaces reachable; coins reachable over 100 resets; vertical camera at top and bottom");
    }

    static Field field(String name) throws Exception {
        Field result = entities.Character.class.getDeclaredField(name);
        result.setAccessible(true);
        return result;
    }
}
