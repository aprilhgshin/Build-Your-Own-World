package byow.Core;

import java.util.ArrayList;
import java.util.Random;

public class GenerateRooms {

    private AreasToCover allAreas;
    private ArrayList<DrawInstructions> areas;
    private Random generator;
    private int dimension;
    private boolean debug = GlobalDebugger.debugAll;

    private class Room {

        private DrawInstructions roomInstructions;
        private int width;
        private int height;

        Room(DrawInstructions roomInstructions, int width, int height) {
            this.roomInstructions = roomInstructions;
            this.width = width;
            this.height = height;
        }
    }

    public GenerateRooms(Random generator) {
        this.generator = generator;
        this.dimension = initializeSingleAreaDim();
        if (debug) {
            System.out.println("Randomly chosen dimension: " + dimension);
        }

        // Initializing single area dimensions
        allAreas = new AreasToCover(generator, dimension, dimension);
        areas = allAreas.generateAreasToCover();
    }

    // Randomly choose between dim of 10 or 15. The width of the world is 90. 90%10 and 90%15 = 0.
    public int initializeSingleAreaDim() {
        int value = generator.nextInt(2);
        if (value == 0) {
            return 10;
        } else {
            return 15;
        }
    }

    public int getDimensions() {
        return dimension;
    }

    /**
     * generateAllRooms() :
     * Generates rooms with randomly chosen coordinates and dimensions in each chosen area.
     * @return ArrayList of DrawInstructions objects that contain leftX, rightX, bottomY, topY
     *          coordinates of each room and corresponding single area ID.
     */
    public ArrayList<DrawInstructions> generateAllRooms() {
        if (debug) {
            System.out.println("Entering room generator: ");
        }
        ArrayList<Room> rooms = genRandomRoomDim();
        return choosePosition(rooms);
    }

    /**
     * generateAllRooms() Helper Method #1 : genRandomRoomDim()
     * Randomly generates dimensions of each Room object
     * @return rooms -- ArrayList of Room objects containing coordinates of single area
     *                  and new Room dimensions
     */
    private ArrayList<Room> genRandomRoomDim() {
        ArrayList<Room> rooms = new ArrayList<>();
        for (DrawInstructions singleArea: areas) {
            // Setting minimum width and height to be 4 units and maximum to be dim of single area
            int randomWidth = generator.nextInt((allAreas.getSingleAreaWidth() + 1) - 4) + 4;
            int randomHeight = generator.nextInt((allAreas.getSingleAreaHeight() + 1) - 4) + 4;
            // Simply passing in area coordinates as well as room dimensions
            Room room = new Room(singleArea, randomWidth, randomHeight);
            rooms.add(room);
        }
        return rooms;
    }

    /**
     * generateAllRooms() Helper Method #2 : choosePosition(ArrayList<Room> rooms)
     * Randomly generates left X and lower Y coordinates of new Room.
     * Determines right X and top Y coordinates using left X and lower Y.
     * Create new DrawInstructions with new coordinates and maintaining corresponding single area ID
     * @param rooms -- ArrayList of Room objects containing corresponding
     *                 single area coordinates and Room width/height.
     * @return newRoomInstructions -- ArrayList of DrawInstructions objects containing
     *          coordinates of each Room object and corresponding single area ID.
     */
    private ArrayList<DrawInstructions> choosePosition(ArrayList<Room> rooms) {
        ArrayList<DrawInstructions> newRoomInstructions = new ArrayList<>();
        if (debug) {
            System.out.println("\tNumber of Rooms to create: " + rooms.size());
        }
        for (Room room: rooms) {
            // Previously, area coordinates were passed into Room objects
            // Because each Room object do not have their own coordinates yet
            DrawInstructions areaInst = room.roomInstructions;

            int maxLeftX = areaInst.getRightX() - room.width;
            int maxBottomY = areaInst.getTopY() - room.height;

            int gLeftX = areaInst.getLeftX();
            int gBottomY = areaInst.getBottomY();
            int leftX = generator.nextInt((maxLeftX + 1) - gLeftX) + gLeftX;
            int bottomY = generator.nextInt((maxBottomY + 1) - gBottomY) + gBottomY;
            int rightX = leftX + room.width;
            int topY = bottomY + room.height;

            DrawInstructions newRoomInst;
            newRoomInst = new DrawInstructions(leftX, rightX, bottomY, topY, areaInst.getAreaID());
            newRoomInstructions.add(newRoomInst);
            if (debug) {
                System.out.println("\t\tCreating room object: ");
                System.out.println("\t\t\tLocated in area ID: " + newRoomInst.getAreaID());
                System.out.println("\t\t\tBottom left XY coordinates: [" + leftX
                        + " ," + bottomY + "]");
                System.out.println("\t\t\tTop Right XY coordinates: [" + rightX
                        + " ," + topY + "]");
            }
        }
        return newRoomInstructions;
    }

}
