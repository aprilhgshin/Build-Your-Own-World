package byow.Core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class AreasToCover {
    private Random generator;
    private int singleAreaWidth;
    private int singleAreaHeight;
    private boolean debug = GlobalDebugger.debugAll;
    private int minNumRooms = 5;
    private ArrayList<DrawInstructions> areasToCoverArrList;

    public AreasToCover(Random generator, int singleAreaWidth, int singleAreaHeight) {
        this.generator = generator;
        this.singleAreaWidth = singleAreaWidth;
        this.singleAreaHeight = singleAreaHeight;
        this.areasToCoverArrList = new ArrayList<>();
    }

    public int getSingleAreaWidth() {
        return singleAreaWidth;
    }

    public int getSingleAreaHeight() {
        return singleAreaHeight;
    }

    /*
    private class Area {
        // Area class contains all parameters needed to place restrictions
         // for generating rooms and hallways
        private int width;
        private int height;
        //private boolean isMerged;
        //private boolean canContainRooms;
        //private ArrayList<Area> neighbors;
        private DrawInstructions instruObj;

        public Area(DrawInstructions xyCoordinates) {
            this.width = 10; // width of single area
            this.height = 10; // length of single area
            this.instruObj = xyCoordinates;
            //this.canContainRooms = false;
        }
    }
    */

    /**
     * Find all neighbors of current Area object
     * If theoretical element number of neighbor exists, then neighbor exists:
     * if neighborElementNum < 0 or neighborElementNum > AreaArray.size()
     *       No neighbor exists
     * else
     *       Add neighbor at that neighborElementNum to neighbor array for currArea at elementNum
     * @param currArea - Area object of interest
     * @param areaObjects - list of all area objects that together span entire Map
     */

    /*
    private void findNeighbors(Area currArea, ArrayList<Area> areaObjects) {
        Engine window = new Engine();
        DrawInstructions tempInstruc = new DrawInstructions();
        Area areaDim = new Area(tempInstruc, 0);
        int numAreasWidth = (int) window.WIDTH / areaDim.width;

        int elementNum = currArea.elementNum;

        int top = elementNum + numAreasWidth;
        int bottom = elementNum - numAreasWidth;
        int right = elementNum + 1;
        int left = elementNum - 1;
        List<Integer> elementNumbers = List.of(top, bottom, right, left);


        for (int neighborNum: elementNumbers) {
            if (neighborNum >= 0 || neighborNum < areaObjects.size()) {
                currArea.neighbors.add(areaObjects.get(neighborNum));
            }
        }
    }
    */

    /**
     * partitionMap
     * chooseAreas
     * @return chosenAreas - ArrayList of chosen areas where Room(s) will be placed
     */
    public ArrayList<DrawInstructions> generateAreasToCover() {
        if (debug) {
            System.out.println("Entering Area generator: ");
        }
        ArrayList<DrawInstructions> allAreas = partitionMap();
        ArrayList<DrawInstructions> chosenAreas = chooseAreas(allAreas);

        return chosenAreas;
    }

    /**
     * generateAreasToCover() Helper method #1 -- ArrayList<Area> partitionMap():
     * Divides entire map into uniform sections.
     * Generates XY coordinates of each Area object (origin is at the bottom left).
     * Keeps track of element number: area object at the origin (bottom left) is element 1.
     * @return areas - ArrayList of Area objects corresponding to each section of partitioned window
     */
    private ArrayList<DrawInstructions> partitionMap() {
        Engine window = new Engine();
        ArrayList<DrawInstructions> areas = new ArrayList<>();

        // To get access to Area object's width and height dimensions
        //DrawInstructions tempInstruc = new DrawInstructions();
        //Area areaDim = new Area(tempInstruc);

        int elementNum = 0;

        for (int h = 0; h < window.HEIGHT; h += singleAreaHeight) {
            for (int w = 0; w < window.WIDTH; w += singleAreaHeight) {
                int leftX = w;
                int rightX = w + singleAreaWidth;
                int bottomY = h;
                int topY = h + singleAreaHeight;
                DrawInstructions newInstructions;
                newInstructions = new DrawInstructions(leftX, rightX, bottomY, topY, elementNum);

                // Keeping track of elementNum to use later for identifying neighboring Area objects
                areas.add(newInstructions);
                elementNum++;
            }
        }
        /*
        // Storing all neighbors for each Area object
        for (Area area: areas) {
            findNeighbors(area, areas);
        }
        */
        return areas;
    }

    /**
     * generateAreasToCover() Helper method #2 -- chooseAreas(ArrayList<Area> areas):
     * @param areasInstructions - ArrayList of all DrawInstructions obj that span the entire map
     * @return areasToCover - ArrayList of randomly chosen DrawInstruction obj to place Rooms in.
     */
    private ArrayList<DrawInstructions> chooseAreas(ArrayList<DrawInstructions> areasInstructions) {
        if (debug) {
            System.out.println("\tSelecting which Areas to cover: ");
        }
        HashSet<DrawInstructions> areasToCover = new HashSet<>();
        //Random generator = new Random(seed);
        int numAreas = generator.nextInt(areasInstructions.size() + 1); // Bound is exclusive
        while (numAreas <= minNumRooms) {
            numAreas = generator.nextInt(areasInstructions.size() + 1);
        }
        for (int i = 0; i < numAreas; i++) {
            int index = generator.nextInt(areasInstructions.size());
            DrawInstructions chosenArea = areasInstructions.get(index);
            //chosenArea.canContainRooms = true;
            if (!areasToCover.contains(chosenArea)) {
                areasToCover.add(chosenArea);
                areasToCoverArrList.add(chosenArea);
            }
        }

        if (debug) {
            System.out.println("\t\tNumber of Areas to cover: " + areasToCover.size());
            System.out.println("\t\tChosen area ID's: ");
            for (DrawInstructions area: areasToCoverArrList) {
                System.out.println("\t\t\tarea ID: " + area.getAreaID());
            }
            System.out.println("\n");
        }

        return areasToCoverArrList;
    }

    /**
     * generateAreasToCover() Helper method #3 -- mergeAreas(ArrayList<Area> areasToCover)
     * Destructive method - modifies parameter areasToCover.
     * Can only merge Area objects with another once.
     * Choose random max number of Area objects to merge: maximum = 1/2 of all Area objects.
     * If randomly chosen area from areasToCover is not already merged with another:
     *   If randomly chosen area has neighbor(s):
     *      Randomly choose one of those neighbors. If neighbor is not merged yet:
     *          Merge chosen area with chosen neighbor.
     * Remove neighbor from areasToCover.
     * @param areasToCover - ArrayList of randomly chosen Area obj in which Room(s) must be placed
     * @return areasToCover - ArrayList of randomly chosen Area obj
     *                        where randomly chosen adjacent Area obj are merged
     */
    /*
    private ArrayList<Area> mergeAreas(ArrayList<Area> areasToCover) {

        // If randomly chosen area has neighbor(s):
        // randomly choose one of those neighbors
        // merge source with chosen neighbor
        //Random generator = new Random(seed);
        int maxNumToMerge = generator.nextInt() + ((int) areasToCover.size() / 2);
        for (int i = 0; i < maxNumToMerge; i++) {
            int index = generator.nextInt() + (areasToCover.size()); // Bound is exclusive
            Area currArea = areasToCover.get(index);
            if (!currArea.isMerged) {
                if (currArea.neighbors.size() > 0) {
                    for (int j = 0; j < currArea.neighbors.size(); j++) {
                        int neighborElem = generator.nextInt(1) + (currArea.neighbors.size() + 1);
                        if (!currArea.neighbors.get(neighborElem).isMerged) {

                        }
                    }
                    //ArrayList<Area> neighbors = findNeighbors
                    // To find neighbors, EITHER iterate through areasToCover to find adjacent area?
                    // Or keep track of neighbors of every Area object in the first place.
                }
            }
        }

        return new ArrayList<>();
    }
    */

}
