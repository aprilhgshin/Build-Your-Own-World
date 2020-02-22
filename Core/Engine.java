package byow.Core;

//import byow.Core.KeyboardInputSource;
//import edu.princeton.cs.introcs.StdDraw;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import javax.swing.*;
import java.awt.*;
import java.security.Key;
import java.util.HashMap;
import java.util.Random;

/**
 * @author Alan Chen, April Shin
 * @source Learn Java By Example: parsing, Stack Overflow
 */
public class Engine {
    TERenderer ter = new TERenderer();
    public static final int WIDTH = 90;
    public static final int HEIGHT = 30;
    private Factory fact;
    private Random rand;
    private WorldCombiner combiner = null;
    private RoomConnector rmConnector = null;
    private TETile[][] world;
    private TETile[][] window;
    private boolean autograding = GlobalDebugger.autograding;
    private boolean debug = GlobalDebugger.printInput;
    private HashMap<TETile, String> description = getDescription();
    private HashMap<Integer, String> diffExplain = getDiffExplain();
    private boolean seeding = false;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     *
     * Instructions:
     * Can type anything you'd like into the window to start the game.
     * Ex: asdw12214sadwwa:q
     * Write :q at the end to exit and save the game
     * To load the saved game, type L before whatever you want to type.
     * Ex: Lwasdwas
     */
    public void interactWithKeyboard() {
        KeyboardInputSource keyboard = new KeyboardInputSource();
        boolean generated = false;
        String inputString = "";
        char c;
        int prevX = -1;
        int prevY = -1;
        if (!autograding) {
            keyboard.displayMainMenu();
        }

        while (keyboard.possibleNextInput()) {
            if (gameOver()) {
                endGame(keyboard);
            } else {
                if (autograding || StdDraw.hasNextKeyTyped()) {
                    c = keyboard.getNextKey();
                    HashMap<String, Object> result = handleInputC(c, generated,
                            inputString, false, keyboard);
                    generated = (boolean) result.get("generated");
                    inputString = (String) result.get("inputString");
                    if (window != null && !autograding) {
                        StdDraw.clear();
                        ter.renderFrame(window);
                        drawFloorNumber(keyboard);
                        StdDraw.show();
                    }
                } else {
                    //display info / update mouseover
                    if (generated) {
                        int x = xBlock();
                        int y = yBlock();
                        if (!autograding && (x != prevX || y != prevY)) {
                            prevX = x;
                            prevY = y;
                            TETile[][] temp = copyGrid(window);
                            temp[x][y] = Tileset.FLOWER;
                            StdDraw.clear();
                            ter.renderFrame(temp);
                            drawFloorNumber(keyboard);
                            keyboard.drawWithCoords(70, 35, tileInformation(x, y));
                            StdDraw.show();
                        }
                    }
                }
            }
        }
    }
    private TETile[][] copyGrid(TETile[][] reference) {
        TETile[][] copy = new TETile[reference.length][reference[0].length];
        for (int x = 0; x < copy.length; x += 1) {
            for (int y = 0; y < copy[0].length; y += 1) {
                copy[x][y] = reference[x][y];
            }
        }
        return copy;
    }

    private boolean isInteger(char c) {
        String s = "";
        s += c;
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException n) {
            return false;
        }
    }


    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */

    public TETile[][] interactWithInputString(String input) {
        boolean generated = false;
        String cleaned = input.replaceAll(":QL", "");
        String inputString = "";
        for (int i = 0; i < input.length(); i += 1) {
            HashMap<String, Object> result = handleInputC(
                    input.charAt(i), generated, inputString, true, null);
            generated = (boolean) result.get("generated");
            inputString = (String) result.get("inputString");
        }
        return window;
    }

    private long parseInputToSeed(String input) {
        //String parsed = input.substring(1, input.length() - 1);
        String parsed = input.replaceAll("[^0-9]", "");
        if (parsed.length() > 0) {
            return Long.valueOf(parsed);
        }
        return 0;
    }

    private HashMap<String, Object> handleInputC(char c, boolean generated,
                                                 String inputString, boolean handlingString,
                                                 KeyboardInputSource key) {
        KeyboardInputSource keyboard = key;
        handlingString = handlingString || autograding;
        if (!isInteger(c)) {
            c = Character.toUpperCase(c);
        }
        if (debug) {
            print(inputString + " --> " + c);
        }
        inputString += c;
        if (!generated) {
            if (!handlingString && !autograding && c != 'L') {
                keyboard.drawText("Please input your Seed: " + inputString.replaceAll("N", ""));
            }
            if (c == 'L' && !seeding) {
                SaveLoadWorld toLoad = new SaveLoadWorld();
                String previous = toLoad.loadWorld();
                previous = previous.replaceAll("L", "");
                inputString += previous;
                window = interactWithInputString(previous);
                if (!autograding) {
                    ter.initialize(Engine.WIDTH, Engine.HEIGHT + 20);
                    ter.renderFrame(window);
                }
                generated = true;
            } else {
                if (c == 'N') {
                    seeding = true;
                } else if (c == 'S' && seeding) {
                    Random temp = new Random(parseInputToSeed(inputString));
                    Factory tempFact = new Factory(temp);

                    world = tempFact.getWorld();
                    rmConnector = tempFact.getRoomConnector();
                    combiner = new WorldCombiner(world, temp, rmConnector);

                    window = combiner.getWindow();
                    if (!handlingString) {
                        ter.initialize(Engine.WIDTH, Engine.HEIGHT + 20);
                        ter.renderFrame(window);
                    }
                    seeding = false;
                    generated = true;
                } else {
                    if (c == 'Q') {
                        System.exit(0);
                    }
                    if (!isInteger(c)) {
                        inputString = inputString.substring(0, inputString.length() - 1);
                        if (!autograding) {
                            keyboard.drawText("Please input your Seed: " + inputString);
                        }
                    }
                }
            } // if its just a number we ignore
        } else {
            if (inputString.charAt(inputString.length() - 2) == ':') {
                if (c == 'Q') {
                    SaveLoadWorld toSave = new SaveLoadWorld();
                    toSave.saveWorld(inputString);
                    generated = false;
                    if (!autograding) {
                        System.exit(0);
                    }
                }
            } else {
                print("previous not :");
                combiner.handleInput(c);
                window = combiner.getWindow();
                if (!handlingString) {
                    ter.renderFrame(window);
                }
            }
        }
        HashMap<String, Object> result = new HashMap<>();
        result.put("generated", generated);
        result.put("inputString", inputString);
        return result;
    }

    private int xBlock() {
        int x = (int) StdDraw.mouseX();
        if (x < 0) {
            return 0;
        }
        if (x >= window.length) {
            return window.length - 1;
        }
        return x;
    }

    private int yBlock() {
        int y = (int) StdDraw.mouseY();
        if (y < 0) {
            return 0;
        }
        if (y >= window[0].length) {
            return window[0].length - 1;
        }
        return y;
    }

    private String tileInformation(int x, int y) {
        if (outOfWorld(x, y)) {
            return "Out of world";
        } else {
            TETile curr = window[x][y];
            return description.get(curr);
        }
    }

    private HashMap<TETile, String> getDescription() {
        HashMap<TETile, String> result = new HashMap<>();
        result.put(Tileset.FLOOR, "This is a floor tile");
        result.put(Tileset.WATER, "Current vision is obscured");
        result.put(Tileset.NOTHING, "There's nothing");
        result.put(Tileset.AVATAR, "Avatar");
        result.put(Tileset.UNLOCKED_DOOR, "This is the exit tile");
        result.put(Tileset.WALL, "This is a wall");
        result.put(Tileset.LOCKED_DOOR, "This is the key");
        return result;
    }

    private HashMap<Integer, String> getDiffExplain() {
        HashMap<Integer, String> result = new HashMap<>();
        result.put(0, "Can see everything");
        result.put(1, "Can see items exit");
        result.put(2, "Infinite belly points");
        result.put(3, "Normal game currently");
        return result;
    }

    private boolean outOfWorld(int x, int y) {
        if (x >= world.length || x < 0) {
            return true;
        }
        if (y >= world[0].length || y < 0) {
            return true;
        }
        return false;
    }

    private void drawFloorNumber(KeyboardInputSource keyboard) {
        keyboard.drawWithCoords(70, 38, "Floor "
                + Integer.toString(combiner.getFloorNumber()));

        keyboard.drawWithCoords(70, 37, "Belly Points: "
                + Integer.toString(combiner.getAvatarHealth()));
        keyboard.drawWithCoords(70, 39, "HasKey: "
                + Boolean.toString(combiner.getAvatar().hasKey()));
        keyboard.drawWithCoords(70, 40, "Difficulty "
                + Integer.toString(combiner.getDifficulty()) + " " +
                "(press - / = to change)");
        keyboard.drawWithCoords(70, 41,
                diffExplain.get(combiner.getDifficulty()));
    }

    private static void print(Object o) {
        System.out.println(o);
    }

    private boolean gameOver() {
        if (combiner != null) {
            if (combiner.getAvatarHealth() == 0) {
                return true;
            }
        }
        return false;
    }

    private void endGame(KeyboardInputSource keyboard) {
        while (StdDraw.hasNextKeyTyped()) {
            if (keyboard.getNextKey() != 'Q') {
                keyboard.displayGameOver();
            } else {
                System.exit(0);
            }
        }
    }
}
