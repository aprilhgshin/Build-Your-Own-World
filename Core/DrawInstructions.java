package byow.Core;

public class DrawInstructions {
    // DrawInstructions class contains the leftmost and rightmost X and Y coordinates of object.

    private int leftX;
    private int rightX;
    private int bottomY;
    private int topY;
    private int areaID;
    //areaID is the element number of Area object (starting from 0 at the bottom left)


    public DrawInstructions() {
        this.leftX = 0;
        this.rightX = 0;
        this.bottomY = 0;
        this.topY = 0;
        this.areaID = 0;
    }

    public DrawInstructions(int leftX, int rightX, int bottomY, int topY, int areaID) {
        this.leftX = leftX;
        this.rightX = rightX;
        this.bottomY = bottomY;
        this.topY = topY;
        this.areaID = areaID;
    }

    public int getLeftX() {
        return leftX;
    }

    public int getRightX() {
        return rightX;
    }

    public int getBottomY() {
        return bottomY;
    }

    public int getTopY() {
        return topY;
    }

    public int getAreaID() {
        return areaID;
    }
}
