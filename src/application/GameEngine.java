package application;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameEngine {

    private static final String NEW_LINE_FEED = System.lineSeparator();

    private final Game game;

    private final int lastPlayerCode;

    private String winner;

    private Scanner sc;

    public GameEngine(Scanner sc) {
        this.sc = sc;
        game = initGame();
        lastPlayerCode = getLastPlayerCode();
    }

    public String play() {
        displayBoard();

        while (winner == null) {
            if (isTie()) {
                return "Wining is no longer possible. It's a tie.";
            }

            playerMove();

            displayBoard();
        }

        return "Player " + winner + " wins!!!";
    }

    private void displayBoard() {
        System.out.println();
        System.out.println(game.toString());
    }

    private void playerMove() {
        final String playerName = getPlayerName(game.getCurrentPlayer());

        final Integer[] coordinates = getSafeNextMove(playerName);

        game.getBoard()[coordinates[0]][coordinates[1]] = playerName;

        if (isWin(playerName, false)) {
            winner = playerName;
            return;
        }

        game.setCurrentPlayer(getNextPlayerCode(game.getCurrentPlayer()));
    }

    private Game initGame() {
        System.out.println();
        return getSafeYNInput("Do you want to resume a saved game [Y/N]? ") ? loadGame() : newGame();
    }

    private Game loadGame() {
        String saveFileName = getSafeUserInput("Enter the name of the save file or 'N' for new game : ", true, false);

        if ("N".equalsIgnoreCase(saveFileName)) {
            return newGame();
        }

        return FileServices.loadGame(saveFileName);

    }

    private Game newGame() {
        System.out.println();
        return new Game.Builder()
                .withPlayerCount(getIntegerInput("Enter the number of players : "))
                .withBoardSize(getIntegerInput("Enter the size of the board : "))
                .withWinSequence(getIntegerInput("Enter the win sequence : "))
                .build();
    }

    private int getNextPlayerCode(int currentPlayerCode) {
        if (lastPlayerCode == currentPlayerCode) {
            return -1;
        }

        if (currentPlayerCode == 13 || currentPlayerCode == 22) {
            return currentPlayerCode + 2;
        }

        return currentPlayerCode + 1;
    }

    private int getLastPlayerCode() {
        int lastCode = game.getPlayerCount() - 2;

        if (lastCode >= 13) {
            lastCode++;
        }

        if (lastCode >= 22) {
            lastCode++;
        }

        return lastCode;
    }

    private String getPlayerName(int playerCode) {
        switch (playerCode) {
            case -1:
                return "X";
            case 0:
                return "O";
            default:
                return String.valueOf((char) (playerCode + 64));
        }
    }

    /**
     * To check the win condition, all the rows, columns, diagonals and
     * anti-diagonals are separated by a ";" and appended into a string called
     * searchScape. A win pattern is created based on the on the win sequence and
     * currentPlayerName. After each player's move, his win pattern is searched for
     * in the searchSpace.
     *
     * @param playerName         : the name of the current player.
     * @param replaceEmptyFields : used to check tie scenario
     * @return true if playerName is the winner
     */
    public boolean isWin(String playerName, boolean replaceEmptyFields) {
        StringBuilder winString = new StringBuilder();
        for (int i = 0; i < game.getWinSequence(); i++) {
            winString.append(playerName);
        }

        final Pattern winPattern = Pattern.compile(winString.toString(), Pattern.CASE_INSENSITIVE);
        String separator = ";";

        StringBuilder searchSpaceBuilder = new StringBuilder();

        appendAllRowsAsString(searchSpaceBuilder, separator);
        searchSpaceBuilder.append(separator);
        appendAllColumnAsString(searchSpaceBuilder, separator);
        searchSpaceBuilder.append(separator);

        // Diagonals
        for (int i = 0; i < game.getBoardSize(); i++) {

            // Upper Half Diagonals
            for (int j = 0; j + i < game.getBoardSize(); j++) {
                searchSpaceBuilder.append(game.getBoard()[j][i + j]);
            }
            searchSpaceBuilder.append(separator);

            // Upper Half Anti-Diagonals
            for (int j = game.getBoardSize() - i - 1; j >= 0; j--) {
                searchSpaceBuilder.append(game.getBoard()[j][game.getBoardSize() - j - i - 1]);
            }
            searchSpaceBuilder.append(separator);

            // Lower Half Diagonals
            for (int j = game.getBoardSize() - 1; j - i - 1 >= 0; j--) {
                searchSpaceBuilder.append(game.getBoard()[j][j - i - 1]);
            }
            searchSpaceBuilder.append(separator);

            // Lower Half Anti-Diagonals
            for (int j = i + 1; j < game.getBoardSize(); j++) {
                searchSpaceBuilder.append(game.getBoard()[j][game.getBoardSize() - j + i]);
            }
            searchSpaceBuilder.append(separator);
        }


        String searchSpace = searchSpaceBuilder.toString();

        if (replaceEmptyFields) {
            searchSpace = searchSpace.replace(" ", playerName);
        }

        final Matcher matcher = winPattern.matcher(searchSpace);

        return matcher.find();
    }

    /**
     * To check the tie condition, all the rows, columns, diagonals and
     * anti-diagonals are separated by a ";" and appended into a string called
     * searchScape. Iterating over all players, a win pattern is created, the empty
     * blocks are replaced with the player name and a check is performed if win is
     * possible. if no player has a win possibility the game is declared a tie.
     *
     * @return true if the game is a tie
     */
    private boolean isTie() {
        int playerCode = -1;
        do {
            String playerName = getPlayerName(playerCode);

            if (isWin(playerName, true)) {
                return false;
            }

            playerCode = getNextPlayerCode(playerCode);
        } while (playerCode != -1);

        return true;
    }

    private void appendAllColumnAsString(StringBuilder sb, String separator) {
        sb.append(String.join("", Arrays.stream(game.getBoard()).map(arr -> arr[0]).toArray(size -> new String[size])));

        for (int i = 1; i < game.getBoardSize(); i++) {
            final int j = i;
            sb.append(separator).append(String.join("",
                    Arrays.stream(game.getBoard()).map(arr -> arr[j]).toArray(size -> new String[size])));
        }
    }

    private void appendAllRowsAsString(StringBuilder sb, String separator) {
        sb.append(String.join("", game.getBoard()[0]));

        for (int i = 1; i < game.getBoardSize(); i++) {
            sb.append(separator).append(String.join("", game.getBoard()[i]));
        }
    }

    private Integer[] getSafeNextMove(String playerName) {
        String message = "Enter row and column separated by space. Enter Q to quit" + NEW_LINE_FEED
                + "Player " + playerName + " move : ";

        try {
            do {
                System.out.print(message);
                String response = sc.nextLine();

                if (response == null || response.isEmpty()) {
                    System.out.println("Invalid input" + NEW_LINE_FEED);
                    continue;
                }

                if ("Q".equalsIgnoreCase(response)) {
                    quitGame();
                }

                String[] inputStringArray = response.trim().split(" ");

                if (inputStringArray.length != 2 || !String.join("", inputStringArray).matches("\\d+")) {
                    System.out.println("Invalid input" + NEW_LINE_FEED);
                    continue;
                }

                Integer row = Integer.valueOf(inputStringArray[0]) - 1;
                Integer column = Integer.valueOf(inputStringArray[1]) - 1;

                if (game.getBoard()[row][column].equals(" ")) {
                    return new Integer[] { row, column };
                }

                System.out.println("Block already taken" + NEW_LINE_FEED);

            } while (true);

        } catch (Exception e) {
            System.out.println("Invalid input");
            return getSafeNextMove(playerName);
        }
    }

    private boolean getSafeYNInput(String message) {
        try {
            while (true) {
                System.out.print(message);
                String response = sc.nextLine();

                if ("Y".equalsIgnoreCase(response)) {
                    return true;
                }

                if ("N".equalsIgnoreCase(response)) {
                    return false;
                }

                System.out.println("Invalid input. Only Y/N are valid inputs");
            }
        } catch (Exception e) {
            System.out.println("Invalid input");
            return getSafeYNInput(message);
        }
    }

    private String getSafeUserInput(String message, boolean checkFileExists, boolean onlyIntegers) {
        try {
            while (true) {
                System.out.print(message);
                String response = sc.nextLine();

                if (response == null || response.isEmpty() || onlyIntegers && !response.matches("\\d+")) {
                    System.out.println("Invalid input");
                    continue;
                }

                if (!checkFileExists || !onlyIntegers && "N".equalsIgnoreCase(response)) {
                    return response;
                }

                if (new File(response).exists()) {
                    return response;
                }

                System.out.println("File does not exist");
            }
        } catch (Exception e) {
            System.out.println("Invalid input");
            return getSafeUserInput(message, checkFileExists, onlyIntegers);
        }
    }

    private Integer getIntegerInput(String message) {
        return Integer.valueOf(getSafeUserInput(message, false, true));
    }

    public void quitGame() {
        System.out.println();
        if (getSafeYNInput("Do you want to save the game [Y/N]? ")) {
            String fileName = getSafeUserInput("Enter the file name or 'Q' to exit without saving : ", false, false);

            if (!"Q".equalsIgnoreCase(fileName)) {
                FileServices.saveGame(game, fileName);
            }
        }

        System.out.println(NEW_LINE_FEED + "Exiting the game");
        System.exit(0);
    }

    public boolean restartGame() {
        System.out.println();
        return getSafeYNInput("Do you want to start another game [Y/N]? ");
    }

}
