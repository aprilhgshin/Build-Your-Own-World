package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

public class TestInteractableWorld {
    public static void main(String[] args) {
        Engine eng = new Engine();
        eng.interactWithKeyboard();
        //testInput("SDDDDWWWWWWWWWWAAAWDDDWDDDAAWAAAAAAAAAAAAAAAWWWWWWD");
    }

    public static void testInteractWithInputString() {
        Engine eng = new Engine();

        TETile[][] world6 = eng.interactWithInputString("n8702095859193238354ssswadswwds");
        TETile[][] world10 = eng.interactWithInputString("lswwds");

        if (world10 != null) {
            TERenderer ter = new TERenderer();
            ter.initialize(Engine.WIDTH, Engine.HEIGHT);
            ter.renderFrame(world10);
        }
    }

    public static void testInput(String input) {
        Engine eng = new Engine();
        TETile[][] screen0 = eng.interactWithInputString(input);
        TERenderer ter = new TERenderer();
        ter.initialize(Engine.WIDTH, Engine.HEIGHT + 20);
        ter.renderFrame(screen0);
    }
}
