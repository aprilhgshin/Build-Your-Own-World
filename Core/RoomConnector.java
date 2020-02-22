package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.util.ArrayList;
import java.util.Random;

public class RoomConnector {
    private ArrayList<ConnectableRoom> allRooms;
    private WeightedQuickUnionUF WQU;
    private String space = "      ";

    private Random rand;
    private TETile[][] theWorld;
    private boolean debug = GlobalDebugger.debugAll && GlobalDebugger.debugConnector;

    public RoomConnector(TETile[][] world, ArrayList<ConnectableRoom> rooms, Random r) {
        rand = r;
        theWorld = world;
        allRooms = rooms;
        WQU = new WeightedQuickUnionUF(allRooms.size());
        if (debug) {
            print("Entering connectAll()");
        }
        connectAll();
    }

    public TETile[][] getTileSet() {
        return theWorld;
    }

    private void connectAll() {
        int connectAllCount = 1;
        while (hasDisconnection()) {
            if (debug) {
                print(space + "hasDisconnection: so we run connectAll() the "
                        + connectAllCount + "th time");
            }
            ConnectableRoom randRoom = getRandomRoom();
            ConnectableRoom target = getRandomRoom();
            while (WQU.connected(getWQURoomID(randRoom),
                    getWQURoomID((target)))) {
                if (debug) {
                    print(space + space + "Connected: Ignoring "
                        + getWQURoomID(randRoom) + " and " + getWQURoomID((target)));
                }
                randRoom = getRandomRoom();
                target = getRandomRoom();
            }
            if (debug) {
                print(space + "Unconnected: Connecting "
                        + getWQURoomID(randRoom) + " and " + getWQURoomID((target)));
            }
            connectRooms(randRoom, target);
            connectAllCount += 1;
        }
        if (debug) {
            print("Successfully connected all rooms. Nice.");
        }
    }

    private void connectRooms(ConnectableRoom one, ConnectableRoom two) {
        if (debug) {
            print(space + space + "Executing connectRooms("
                    + getWQURoomID(one) + ", " + getWQURoomID((two)) + ")");
        }
        int x1 = randomX(one);
        int x2 = randomX(two);
        int y1 = randomY(one);
        int y2 = randomY(two);
        int left = Math.min(x1, x2);
        int right = Math.max(x1, x2);
        int top = Math.max(y1, y2);
        int bot = Math.min(y1, y2);
        if (debug) {
            print(space + space + space + "Drawing Corridor with dimensions");
        }
        if (debug) {
            print(space + space + space + space + left + " <--> " + right);
        }
        if (debug) {
            print(space + space + space + space + bot + " v--^ " + top);
        }
        drawX(theWorld, bot, left, right, Tileset.FLOOR);
        drawY(theWorld, left, top, bot, Tileset.FLOOR);
        drawY(theWorld, right, top, bot, Tileset.FLOOR);
        drawX(theWorld, top, left, right, Tileset.FLOOR);
        WQU.union(getWQURoomID(one), getWQURoomID(two));
        if (debug) {
            print(space + space + "Finished WQU and existing ConnectRooms "
                    + getWQURoomID(one) + " and " + getWQURoomID((two)));
        }
    }

    private boolean hasDisconnection() {
        for (int i = 1; i < allRooms.size(); i += 1) {
            for (int j = 0; j < allRooms.size(); j += 1) {
                if (!WQU.connected(0, i)) {
                    if (debug) {
                        print(space + "hasDisconnection at " + i + " and " + j);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private int getWQURoomID(ConnectableRoom room) {
        return allRooms.indexOf(room);
    }

    private ConnectableRoom getRandomRoom() {
        int temp = rand.nextInt(allRooms.size());
        return allRooms.get(temp);
    }

    public int randomX(ConnectableRoom room) {
        int x = -1;
        while (x < room.getLeft()) {
            x = rand.nextInt(room.getRight());
        }
        return x;
    }

    public int randomY(ConnectableRoom room) {
        int y = -1;
        while (y <= room.getBot()) {
            y = rand.nextInt(room.getTop() + 1);
        }
        return y;
    }

    /** draws a line vertically */
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

    private static void print(Object o) {
        System.out.println(o);
    }

    public ArrayList<ConnectableRoom> getAllConRooms() {
        return allRooms;
    }
}
