package byow.Core;

public class ConnectableRoom {
    private int top;
    private int bot;
    private int left;
    private int right;

    public ConnectableRoom(int top, int bot, int left, int right) {
        this.top = top;
        this.bot = bot;
        this.left = left;
        this.right = right;
    }

    public int getTop() {
        return top;
    }

    public int getLeft() {
        return left;
    }

    public int getBot() {
        return bot;
    }

    public int getRight() {
        return right;
    }

    public boolean contains(int x, int y) {
        if (x >= left && x <= right) {
            if (y <= top && y >= bot) {
                return true;
            }
        }
        return false;
    }
}
