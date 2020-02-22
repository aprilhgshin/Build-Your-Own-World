package byow.Core;


import java.io.*;

/**
 * @source SaveDemo Main.java
 */
public class SaveLoadWorld {


    // When Q is pressed, SaveLoadWorld will be called to save the current world
    // Create file if file already doesn't exist
    // ObjectOutputStream writeObjects the objects you know we need to save

    private boolean debug = GlobalDebugger.debugSaveLoad;
    private boolean autograder = GlobalDebugger.autograding;

    public SaveLoadWorld() {
    }

    /**
     * Saving string input into file.
     * @param input
     */
    public void saveWorld(String input) { //(Random generator, Avatar avatar, TETile[][] world) {

        String newInput = input.replaceAll(":q", "");
        newInput = newInput.replaceAll(":Q", "");
        File f = new File("./save_world.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(newInput);
            if (debug) {
                System.out.println("\t\tSaving " + newInput + " into save_world.txt\n\n");
            }
        }  catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        }  catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }

    }

    /**
     * If the previous string input was saved, then will be used to regenerate the same world
     * and continue on from there.
     * @return
     */
    public String loadWorld() {
        File f = new File("./save_world.txt");
        if (f.exists()) {
            if (debug) {
                System.out.println("\t\tsave_world.txt file exists. Reading file.");
            }
            try {
                //FileReader fr = new FileReader(f);
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                String s = (String) os.readObject();
                if (debug) {
                    System.out.println(s);
                }
                return s;
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }

        if (!autograder) {
            System.exit(0);
        }
        return null;
    }

    public void deleteFile() {
        File f = new File("./save_world.txt");
        if (f.exists()) {
            f.delete();
        }
    }
}


