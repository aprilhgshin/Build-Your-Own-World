package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Avatar {
    private HashMap<Character, HashMap<String, Integer>> controlKeys;
    private int xPos; // avatar X coordinate in world ("mini map")
    private int yPos; // avatar Y coordinate in world ("mini map")
    private Random generator;
    private TETile[][] world;
    private RoomConnector roomConnector;
    private boolean debug = GlobalDebugger.debugKeyboardInput;
    private TETile previousBlock;
    private int maxHealth;
    private int stepsTaken;
    private int healthStatus;
    private boolean hasKey;
    private boolean decreasingHealth;

    /**
     *
     * @param g - Random generator produced from string input seed
     * @param r - RoomConnector object to access RandomX, RandomY, conRooms
     */
    public Avatar(Random g, RoomConnector r, TETile[][] world) {
        this.generator = g;
        this.roomConnector = r;
        this.world = world;
        // TODO Change maxHealth to increase the number of steps Avatar can take before dying.
        maxHealth = 100;
        healthStatus = maxHealth;
        stepsTaken = 0;
        controlKeys = new HashMap<>();
        initializeKeys();
        initializeAvatarPos();
        decreasingHealth = true;
        previousBlock = world[xPos][yPos];
        placeHealthSource();
    }

    // Define directions of W,A,S,D keys.
    private void initializeKeys() {
        if (debug) {
            System.out.println("initialized keys");
        }
        char[] verticalKeys = {'W', 'S'};
        char[] horizontalKeys = {'D', 'A'};

        int vertical = 1;
        for (char key: verticalKeys) {
            HashMap<String, Integer> move = new HashMap<>();
            move.put("Vertical", vertical);
            move.put("Horizontal", 0);
            vertical *= -1;
            controlKeys.put(key, move);
        }

        int horizontal = 1;
        for (char key: horizontalKeys) {
            HashMap<String, Integer> move = new HashMap<>();
            move.put("Vertical", 0);
            move.put("Horizontal", horizontal);
            horizontal *= -1;
            controlKeys.put(key, move);
        }
    }

    /**
     * Placing Avatar randomly in a room in the world
     */

    public ArrayList<Integer> initializeObjectPos() {
        ArrayList<Integer> randPos = new ArrayList<>();
        //Randomly choose one room
        ArrayList<ConnectableRoom> conRooms = roomConnector.getAllConRooms();
        int randRoom = generator.nextInt(conRooms.size());
        ConnectableRoom room = conRooms.get(randRoom);
        int x = roomConnector.randomX(room);
        int y = roomConnector.randomY(room);
        randPos.add(x);
        randPos.add(y);
        return randPos;
    }

    public void initializeAvatarPos() {
        /*
        if (debug) {
            System.out.println("Choosing random room to place avatar.");
        }
        //Randomly choose one room
        ArrayList<ConnectableRoom> conRooms = roomConnector.getAllConRooms();
        int randRoom = generator.nextInt(conRooms.size());
        ConnectableRoom room = conRooms.get(randRoom);
        xPos = roomConnector.randomX(room);
        yPos = roomConnector.randomY(room);
        if (debug) {
            System.out.println("xWorld: " + xPos + " ,yWorld: " + yPos);
        }
        */
        ArrayList<Integer> xy = initializeObjectPos();
        xPos = xy.get(0);
        yPos = xy.get(1);
    }

    public void placeHealthSource() {
        int randInt = generator.nextInt(5);
        for (int i = 0; i < randInt; i++) {
            ArrayList<Integer> xy = initializeObjectPos();
            world[xy.get(0)][xy.get(1)] = Tileset.SAND;
        }
    }

    public void updateLocation() {
        world[xPos][yPos] = Tileset.AVATAR;
    }

    public void moveByKey(char c) {
        if (!controlKeys.containsKey(c)) {
            return;
        }

        HashMap<String, Integer> direction = controlKeys.get(c);
        int x = xPos + direction.get("Horizontal");
        int y = yPos + direction.get("Vertical");

        if (isValidTile(x, y)) {
            if (debug) {
                System.out.println("current position: " + xPos + " " + yPos);
                System.out.println("next position: " + x + " " + y);
            }

            // If the planned movement takes the avatar to a walkable tile, move the avatar
            if (debug) {
                System.out.println("new x: " + x);
                System.out.println("new y: " + y);
            }
            if (previousBlock == Tileset.SAND ||
                    previousBlock == Tileset.LOCKED_DOOR) { //Picking up health source
                previousBlock = Tileset.FLOOR;
            }
            world[xPos][yPos] = previousBlock;
            previousBlock = world[x][y];
            if (onFruit()) {
                setHealth(healthStatus + 20);
            }
            if (onKey()) {
                this.hasKey = true;
            }
            world[x][y] = Tileset.AVATAR;
            xPos = x;
            yPos = y;

            stepsTaken += 1;
            if ((stepsTaken % 5) == 0) {
                if (decreasingHealth) {
                    healthStatus--;
                }
            }
        }
    }
    private void regenerateHealth(byow.TileEngine.TETile currBlock, int healthChange) {
        //If consumed health source, regenerate health by changeHealth amount
        if (currBlock == Tileset.SAND) {
            // TODO Can change how health reacts to health source later on
            int newHealth = healthStatus + healthChange;
            healthStatus = newHealth - (newHealth % maxHealth);
            if (healthStatus > 100) {
                healthStatus = 100;
            }
        }
    }

    public int getX() {
        return xPos;
    }

    public int getY() {
        return yPos;
    }


    public int getHealthStatus() {
        return healthStatus;
    }

    public void setHealth(int point) {
        if (point <= 100 && point > 0) {
            this.healthStatus = point;
        }
    }

    private boolean isValidTile(int x, int y) {
        TETile target = world[x][y];
        if (target == Tileset.FLOOR || target == Tileset.UNLOCKED_DOOR ||
                target == Tileset.SAND || target == Tileset.LOCKED_DOOR) {
            //TODO check later to validate other kinds of tiles
            return true;
        }
        return false;
    }

    public TETile[][] getVision() {
        TETile[][] result = new TETile[11][11];
        for (int x = -5; x <= 5; x += 1) {
            for (int y = -5; y <= 5; y += 1) {
                if (isOutsideOfWorld(xPos + x, y + yPos)) {
                    result[x + 5][y + 5] = Tileset.NOTHING;
                } else {
                    result[x + 5][y + 5] = world[xPos + x][yPos + y];
                }
            }
        }
        return result;
    }

    private boolean isOutsideOfWorld(int x, int y) {
        if (x < 0 || x >= world.length) {
            return true;
        }
        if (y < 0 || y >= world[0].length) {
            return true;
        }
        return false;
    }

    public void changeSeen(boolean[][] hasSeen) {
        for (int x = -5; x <= 5; x += 1) {
            for (int y = -5; y <= 5; y += 1) {
                if (!isOutsideOfWorld(xPos + x, y + yPos)) {
                    hasSeen[xPos + x][yPos + y] = true;
                }
            }
        }
    }

    public boolean onExit() {
        if (previousBlock == Tileset.UNLOCKED_DOOR) {
            return true;
        }
        return false;
    }

    private boolean onKey() {
        if (previousBlock == Tileset.LOCKED_DOOR) {
            return true;
        }
        return false;
    }

    public boolean hasKey() {
        return hasKey;
    }

    private boolean onFruit() {
        if (previousBlock == Tileset.SAND) {
            return true;
        }
        return false;
    }

    public void setdecreasingHealth(boolean input) {
        decreasingHealth = input;
    }
}
