package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import java.util.Random;

/**
 *  Draws a world that is mostly empty except for a small region.
 */
public class TestDrawingWorld {
    public static void main(String[] args) {
        drawWorld(testLongConvert("n5546842467073412033s"));
    }

    private static long testLongConvert(String input) {
        String parsed = input.substring(1, input.length() - 1);
        long result = Long.valueOf(parsed);
        return result;
    }

    private static void drawWorld(long seed) {
        TERenderer ter;
        ter = new TERenderer();
        ter.initialize(Engine.WIDTH, Engine.HEIGHT + 20);
        TETile[][] world;
        Random r = new Random(seed);
        Factory fact = new Factory(r);
        world = fact.getWorld();
        ter.renderFrame(world);
    }

    private static TETile[][] getWorld(long seed) {
        TETile[][] world = null;
        Random r = new Random(seed);
        Factory fact = new Factory(r);
        return fact.getWorld();
    }

    private static boolean sameWorld(TETile[][] w1, TETile[][] w2) {
        for (int x = 0; x < w1.length; x += 1) {
            for (int y = 0; y < w1[0].length; y += 1) {
                if (w1[x][y] != w2[x][y]) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void generateWithSeedString(String s) {
        TERenderer ter;
        ter = new TERenderer();
        ter.initialize(Engine.WIDTH, Engine.HEIGHT);
        TETile[][] world = null;
        Long seed = testLongConvert(s);
        Random r = new Random(seed);
        Factory fact = new Factory(r);
        world = fact.getWorld();
        ter.renderFrame(world);
    }

    public static void singleSeedTest() {
        TERenderer ter;
        ter = new TERenderer();
        ter.initialize(Engine.WIDTH, Engine.HEIGHT);
        TETile[][] world = null;
        Long seed = 37L;
        Random r = new Random(seed);
        Factory fact = new Factory(r);
        world = fact.getWorld();
        ter.renderFrame(world);
    }

    private static void runMultipleWithPrints(int n) {
        TERenderer ter;
        ter = new TERenderer();
        ter.initialize(Engine.WIDTH, Engine.HEIGHT);
        for (int j = 1; j <= n; j += 1) {
            TETile[][] world = null;
            Random r = new Random();
            Factory fact = new Factory(new Random(r.nextLong()));
            world = fact.getWorld();
            ter.renderFrame(world);
        }
    }
}
