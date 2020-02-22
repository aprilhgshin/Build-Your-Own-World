package byow.Core;

/**
 * @author Alan Chen, April Shin
 */

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;

public class Factory implements java.io.Serializable {

    private ArrayList<DrawInstructions> roomInstructions;
    private Random rand;
    private TETile[][] theWorld;
    private boolean debug = GlobalDebugger.debugAll && GlobalDebugger.debugFactory;
    private String space = "      ";
    private RoomConnector thisConnector;

    public Factory(Random random) {
        rand = random;
        GenerateRooms gr = new GenerateRooms(rand);
        roomInstructions = gr.generateAllRooms();
    }

    public TETile[][] getWorld() {
        if (debug) {
            System.out.println("Entering inside GetWorld");
        }
        theWorld = emptyWorld();
        ArrayList<ConnectableRoom> conRooms = new ArrayList<>();
        if (debug) {
            System.out.println("Converting instructions to ConnectableRoom object");
        }
        for (DrawInstructions dr : roomInstructions) {
            conRooms.add(getConnectableRoom(dr));
        }
        if (debug) {
            System.out.println("Drawing all Rooms");
        }
        drawAllRooms(conRooms);
        if (debug) {
            System.out.println("Creating RoomConnector");
        }
        if (GlobalDebugger.drawCorridors) {
            thisConnector = new RoomConnector(theWorld, conRooms, rand);
        }
        if (debug) {
            System.out.println("Drawing walls");
        }
        if (GlobalDebugger.drawWalls) {
            drawWalls(theWorld);
        }

        //Inserts Avatar in world
        //Avatar avatar = new Avatar(rand);
        //avatar.initializeAvatarPos(theWorld, roomInstructions);
        return theWorld;
    }

    private void drawAllRooms(ArrayList<ConnectableRoom> arr) {
        for (ConnectableRoom room : arr) {
            drawRoom(room);
        }
    }

    private void drawRoom(ConnectableRoom room) {
        fill(theWorld, room.getLeft(), room.getRight(),
                room.getTop(), room.getBot(), Tileset.FLOOR);
    }

    private void drawWalls(TETile[][] canvas) {
        for (int x = 0; x < canvas.length; x += 1) {
            for (int y = 0; y < canvas[0].length; y += 1) {
                if (canvas[x][y] == Tileset.NOTHING) {
                    if (besideFloor(canvas, x, y)) {
                        canvas[x][y] = Tileset.WALL;
                    }
                }
            }
        }
    }

    public ConnectableRoom getConnectableRoom(DrawInstructions inst) {
        int left = convert(inst.getLeftX() + 1);
        int right = convert(inst.getRightX() - 1);
        int top = convert(inst.getTopY() - 1);
        int bot = convert(inst.getBottomY() + 1);
        ConnectableRoom result = new ConnectableRoom(top, bot, left, right);
        return result;
    }

    private boolean besideFloor(TETile[][] world, int x, int y) {
        if (validate(x - 1, y)) {
            if (world[x - 1][y] == Tileset.FLOOR) {
                return true;
            }
        }
        if (validate(x + 1, y)) {
            if (world[x + 1][y] == Tileset.FLOOR) {
                return true;
            }
        }
        if (validate(x, y - 1)) {
            if (world[x][y - 1] == Tileset.FLOOR) {
                return true;
            }
        }
        if (validate(x, y + 1)) {
            if (world[x][y + 1] == Tileset.FLOOR) {
                return true;
            }
        }
        return false;
    }

    /** fill area with designated tile */
    private void fill(TETile[][] canvas, int left, int right, int top, int bottom, TETile color) {
        for (int xx = left; xx <= right; xx += 1) {
            for (int yy = bottom; yy <= top; yy += 1) {
                canvas[xx][yy] = color;
            }
        }
    }

    /** returns empty world */
    private TETile[][] emptyWorld() {
        TETile[][] world = new TETile[Engine.WIDTH][Engine.HEIGHT];
        for (int x = 0; x < Engine.WIDTH; x += 1) {
            for (int y = 0; y < Engine.HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        return world;
    }

    private int convert(int xy) {
        int temp = xy - 1;
        if (temp <= 0) {
            temp = 1;
        }
        return temp;
    }

    private boolean validate(int x, int y) {
        if (x < 0 || x >= Engine.WIDTH) {
            return false;
        }
        if (y < 0 || y >= Engine.HEIGHT) {
            return false;
        }
        return true;
    }

    public RoomConnector getRoomConnector() {
        return thisConnector;
    }
}
