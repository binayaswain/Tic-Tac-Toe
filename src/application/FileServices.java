package application;

import java.io.*;

public class FileServices {

    private FileServices() {
        // Nothing to Do
    }

    public static Game loadGame(String saveFileName) {
        try (FileInputStream fileIn = new FileInputStream(saveFileName);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            final Game game = (Game) objectIn.readObject();
            if (game == null) {
                throw new ClassNotFoundException("Not a valid save file");
            }
            System.out.println("\nSuccessfully loaded game from file " + saveFileName);
            return game;
        } catch (final IOException | ClassNotFoundException e) {
            System.out.println("Could not load the game from " + saveFileName);
            System.out.println("Error : " + e.getMessage());
            return null;
        }
    }

    public static void saveGame(Game game, String saveFileName) {
        try (FileOutputStream fileOut = new FileOutputStream(saveFileName);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(game);
            System.out.println("\nSuccessfully saved the game to " + saveFileName);
        } catch (final IOException e) {
            System.out.println("Could not save the game to " + saveFileName);
            System.out.println("Error : " + e.getMessage());
        }
    }

}
