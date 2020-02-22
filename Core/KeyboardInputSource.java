package byow.Core;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class KeyboardInputSource implements InputSource {
    private static final boolean PRINT_TYPED_KEYS = false;
    public KeyboardInputSource() {
        if (!GlobalDebugger.autograding) {
            Font f = new Font("Comic Sans MS", Font.PLAIN, 25);
            StdDraw.setFont(f);
            StdDraw.text(0.5, 0.5, "Please Enter Seed");
        }
        //TODO update draw function as you need at the start menu
    }

    public char getNextKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (PRINT_TYPED_KEYS) {
                    System.out.print(c);
                }
                return c;
            }
        }
    }

    public void drawText(String text) {
        //TODO update draw function as you need at the start menu
        if (!GlobalDebugger.autograding) {
            StdDraw.clear();
            StdDraw.text(0.5, 0.5, text);
        }
    }

    public void drawWithCoords(double x, double y, String text) {
        //TODO update draw function as you need at the start menu
        if (!GlobalDebugger.autograding) {
            StdDraw.text(x, y, text);
        }
    }

    public void displayMainMenu() {
        StdDraw.setCanvasSize(1300, 700);
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Arial", Font.BOLD, 35);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);
        drawWithCoords(0.5, 0.7, "HugLife");

        Font subFonts = new Font("Arial", Font.PLAIN, 18);
        StdDraw.setFont(subFonts);
        StdDraw.setPenColor(Color.WHITE);
        drawWithCoords(0.5, 0.45, "New Game (N)");
        drawWithCoords(0.5, 0.41, "Load Game (L)");
        drawWithCoords(0.5, 0.37, "Quit (Q)");

        StdDraw.setPenColor(Color.BLACK);
    }

    public void displayGameOver() {
        StdDraw.clear(Color.BLACK);
        Font gameOverFont = new Font("Arial", Font.BOLD, 35);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(gameOverFont);
        drawWithCoords(45, 28, "GAME OVER");

        Font subFont = new Font("Arial", Font.PLAIN, 15);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(subFont);
        drawWithCoords(45, 26.5, "You have run out of health.");
        drawWithCoords(45, 17, "Enter 'Q' to quit.");
        StdDraw.show();
    }

    public boolean possibleNextInput() {
        return true;
    }
}
