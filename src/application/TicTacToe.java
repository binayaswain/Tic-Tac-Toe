package application;

import java.util.Scanner;

public class TicTacToe {

    public static void main(String[] args) {

        try (Scanner sc = new Scanner(System.in)) {
            while(true) {
                GameEngine engine = new GameEngine(sc);
                System.out.println(engine.play());

                if (!engine.restartGame()) {
                    System.out.println(System.lineSeparator() + "Exiting the game");
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(System.lineSeparator() + "Invalid action/input");
            System.out.println("Error : " + e.getMessage());
            System.out.println(System.lineSeparator() + "Exiting the game");
        }
    }

}
