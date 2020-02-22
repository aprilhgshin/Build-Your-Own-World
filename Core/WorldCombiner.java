package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.security.Key;
import java.util.ArrayList;
import java.util.Random;

public class WorldCombiner implements java.io.Serializable {
    private Random rand;
    private TETile[][] world;
    private boolean[][] hasSeen;
    private TETile[][] status;
    private TETile[][] window;
    private Avatar avatar;
    private ArrayList<ConnectableRoom> rooms;
    private RoomConnector roomConnector;
    private boolean debug = GlobalDebugger.debugKeyboardInput;
    private int floorNumber;
    private KeyboardInputSource keyboard;
    private int difficulty = 3;


    WorldCombiner(TETile[][] w, Random rand, RoomConnector r) {
        this.world = w;
        this.rand = rand;
        this.roomConnector = r;
        this.rooms = r.getAllConRooms();
        this.hasSeen = new boolean[w.length][w[0].length];
        status = getInitialStatus();
        avatar = newAvatar();
        drawAvatar(avatar);
        //TODO here we initilize the world and update status in some way obeying spec
        floorNumber = 1;
        updateStatus();
        drawExit();
        drawKey();
        window = combine(); // do not modify just call before exiting to update
    }


    public TETile[][] getWorld() {
        return world;
    }

    public TETile[][] getStatus() {
        return status;
    }

    public void handleInput(char c) {
        //TODO depending on the c, we modify the world in some way obeying spec
        if (c == '=') {
            difficulty = Math.min(3, difficulty + 1);
        } else if (c == '-') {
            difficulty = Math.max(0, difficulty - 1);
        }
        resetDifficulty();
        avatar.moveByKey(c);
        avatar.updateLocation();
        if (avatar.onExit()) {
            if (avatar.hasKey()) {
                Random newRand = new Random(rand.nextLong());
                Factory fact = new Factory(newRand);
                world = fact.getWorld();
                this.rand = newRand;
                this.roomConnector = fact.getRoomConnector();
                this.rooms = roomConnector.getAllConRooms();
                this.hasSeen = new boolean[world.length][world[0].length];
                status = getInitialStatus();
                drawExit();
                drawKey();
                // resets health
                Avatar old = avatar;
                avatar = newAvatar();
                avatar.setHealth(old.getHealthStatus());
                //TODO here we initilize the world and update status in some way obeying spec
                floorNumber += 1;
            }
            drawAvatar(avatar);
            updateStatus();
            window = combine(); // do not modify just call before exiting to update
        } else {
            drawAvatar(avatar);
            updateStatus();
            window = combine(); // do not modify just call before exiting to update
        }
    }

    public TETile[][] getWindow() {
        return window;
    }

    private TETile[][] combine() {
        TETile[][] result = new TETile[Engine.WIDTH][Engine.HEIGHT + 20];
        TETile[][] unobscured = getObscuredWorld();
        for (int x = 0; x < Engine.WIDTH; x += 1) {
            for (int y = 0; y < Engine.HEIGHT + 20; y += 1) {
                if (y < 30) {
                    result[x][y] = unobscured[x][y];
                } else {
                    result[x][y] = status[x][y - 30];
                }
            }
        }
        return result;
    }

    private TETile[][] getObscuredWorld() {
        if (GlobalDebugger.showWorld) {
            return world;
        } else {
            TETile[][] unobscured = new TETile[world.length][world[0].length];
            for (int x = 0; x < world.length; x += 1) {
                for (int y = 0; y < world[0].length; y += 1) {
                    if (hasSeen[x][y]) {
                        unobscured[x][y] = world[x][y];
                    } else {
                        if (world[x][y] == Tileset.UNLOCKED_DOOR ||
                                world[x][y] == Tileset.SAND ||
                                world[x][y] == Tileset.LOCKED_DOOR) {
                            if (GlobalDebugger.showExit) {
                                unobscured[x][y] = world[x][y];
                            } else {
                                unobscured[x][y] = Tileset.WATER;
                            }
                        } else {
                            unobscured[x][y] = Tileset.WATER;
                        }
                    }
                }
            }
            unobscured[avatar.getX()][avatar.getY()] = Tileset.FLOOR;
            return unobscured;
        }
    }

    private Avatar newAvatar() {
        Avatar temp = new Avatar(rand, roomConnector, world);
        return temp;
    }

    private TETile[][] getInitialStatus() {
        TETile[][] result = new TETile[Engine.WIDTH][20];
        for (int x = 0; x < Engine.WIDTH; x += 1) {
            for (int y = 0; y < 20; y += 1) {
                result[x][y] = Tileset.NOTHING;
            }
        }
        TETile color = Tileset.TREE; // we can change color here
        drawX(result, 0, 0, Engine.WIDTH - 1, color);
        drawX(result, 19, 0, Engine.WIDTH - 1, color);
        drawY(result, 0, 19, 0, Tileset.TREE);
        drawY(result, Engine.WIDTH - 1, 19, 0, color);
        drawY(result, 30, 19, 0, color);
        drawY(result, 60, 19, 0, color);
        return result;
    }

    private void drawY(TETile[][] canvas, int xx, int top, int bottom, TETile color) {
        for (int curr = bottom; curr <= top; curr += 1) {
            canvas[xx][curr] = color;
        }
    }

    /** draws a line horizontally */
    private void drawX(TETile[][] canvas, int yy, int left, int right, TETile color) {
        for (int curr = left; curr <= right; curr += 1) {
            canvas[curr][yy] = color;
        }
    }

    private void drawAvatar(Avatar av) {
        world[av.getX()][av.getY()] = Tileset.AVATAR;
    }

    private void drawExit() {
        ConnectableRoom r = getRandRoom();
        int x = roomConnector.randomX(r);
        int y = roomConnector.randomY(r);
        while(world[x][y] != Tileset.FLOOR) {
            x = roomConnector.randomX(r);
            y = roomConnector.randomY(r);
        }
        world[x][y] = Tileset.UNLOCKED_DOOR;
    }

    private void drawKey() {
        ConnectableRoom r = getRandRoom();
        int x = roomConnector.randomX(r);
        int y = roomConnector.randomY(r);
        while(world[x][y] != Tileset.FLOOR) {
            x = roomConnector.randomX(r);
            y = roomConnector.randomY(r);
        }
        world[x][y] = Tileset.LOCKED_DOOR;
    }

    private void drawVision() {
        TETile[][] vision = avatar.getVision(); // #thanosdidnothingwrong
        for (int x = 0; x < 11; x += 1) {
            for (int y = 0; y < 11; y += 1) {
                status[40 + x][4 + y] = vision[x][y];
            }
        }
        drawX(status, 15, 39, 51, Tileset.TREE);
        drawX(status, 3, 39, 51, Tileset.TREE);
        drawY(status, 39, 15, 3, Tileset.TREE);
        drawY(status, 51, 15, 3, Tileset.TREE);
    }

    private void updateStatus() {
        avatar.changeSeen(hasSeen);
        drawVision();
    }

    private ConnectableRoom getRandRoom() {
        int curr = rand.nextInt(rooms.size());
        return rooms.get(curr);
    }
    public Avatar getAvatar() {
        return avatar;
    }
    public int getFloorNumber() {
        return floorNumber;
    }

    public int getAvatarHealth() {
        return avatar.getHealthStatus();
    }

    public int getDifficulty() {
        return difficulty;
    }

    private void resetDifficulty() {
        if (difficulty == 0) {
            GlobalDebugger.showWorld = true;
            GlobalDebugger.showExit = true;
            avatar.setdecreasingHealth(false);
        }
        if (difficulty == 1) {
            GlobalDebugger.showExit = true;
            GlobalDebugger.showWorld = false;
            avatar.setdecreasingHealth(false);
        }
        if (difficulty == 2) {
            GlobalDebugger.showExit = false;
            GlobalDebugger.showWorld = false;
            avatar.setdecreasingHealth(false);
        }
        if (difficulty == 3) {
            GlobalDebugger.showExit = false;
            GlobalDebugger.showWorld = false;
            avatar.setdecreasingHealth(true);
        }
    }
}
