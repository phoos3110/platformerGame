import game.GamePanel;
import utils.LoadSave;
import java.lang.reflect.*;

public class TileCollisionCheck {
    public static void main(String[] args) throws Exception {
        GamePanel panel = new GamePanel(LoadSave.FOREST_MAP, null);
        entities.Character player = panel.getPlayer();
        int[][] map = new int[16][16];
        map[8][8] = 14;
        player.setMapData(map, 32);
        Method step = entities.Character.class.getDeclaredMethod("updatePosition");
        step.setAccessible(true);

        // The image ends at local y=22; its transparent bottom nine pixels are empty.
        place(player, 256, 288, 0, -10);
        step.invoke(player);
        step.invoke(player);
        if (player.getY() != 279 || field("ySpeed").getInt(player) != 0) {
            throw new AssertionError("Jump must reach the visible underside and stop there");
        }

        place(player, 224, 279, 4, 0);
        step.invoke(player);
        if (player.getX() != 228) throw new AssertionError("Transparent tile corner blocked horizontal movement");

        place(player, 224, 256, 4, 0);
        step.invoke(player);
        if (player.getX() != 224) throw new AssertionError("Visible platform side did not block movement");

        place(player, 256, 216, 0, 10);
        for (int frame = 0; frame < 4 && !field("onGround").getBoolean(player); frame++) step.invoke(player);
        if (!field("onGround").getBoolean(player)) throw new AssertionError("Cannot land on visible platform top");

        // Full terrain blocks still stop a jump at the bottom of the tile.
        map[8][8] = 5;
        place(player, 256, 288, 0, -10);
        step.invoke(player);
        if (player.getY() != 288) throw new AssertionError("Jump passed through solid ceiling");
        System.out.println("PASS: transparent underside and corners are clear; visible sides, landing, and solid ceilings still collide");
    }

    static void place(entities.Character player, int x, int y, int dx, int dy) throws Exception {
        field("x").setInt(player, x);
        field("y").setInt(player, y);
        field("xSpeed").setInt(player, dx);
        field("ySpeed").setInt(player, dy);
        field("onGround").setBoolean(player, false);
    }

    static Field field(String name) throws Exception {
        Field field = entities.Character.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }
}
