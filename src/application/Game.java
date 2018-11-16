package application;

import java.io.Serializable;
import java.security.InvalidParameterException;

public class Game implements Serializable {

    private static final long serialVersionUID = 6357204747280196819L;

    private static final String NEW_LINE_FEED = System.lineSeparator();

    private final String[][] board;

    private final int boardSize;

    private final int playerCount;

    private final int winSequence;

    private final String rowSeparator;

    private final String columnNumber;

    private int currentPlayer;

    public Game(Builder builder) {
        playerCount = builder.playerCount;
        boardSize = builder.boardSize;
        winSequence = builder.winSequence;

        currentPlayer = -1;
        columnNumber = getColumnNumber();
        rowSeparator = getRowSeparator();

        board = new String[boardSize][boardSize];

        initBoard();

        validate();
    }

    private void initBoard() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = " ";
            }
        }
    }

    public String[][] getBoard() {
        return board;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public int getWinSequence() {
        return winSequence;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public static class Builder {

        private int boardSize;

        private int playerCount;

        private int winSequence;

        public Game build() {
            return new Game(this);
        }

        public Builder withBoardSize(int boardSize) {
            this.boardSize = boardSize;
            return this;
        }

        public Builder withPlayerCount(int playerCount) {
            this.playerCount = playerCount;
            return this;
        }

        public Builder withWinSequence(int winSequence) {
            this.winSequence = winSequence;
            return this;
        }

    }

    private String getRowSeparator() {
        StringBuilder sb = new StringBuilder("   ---");

        for (int i = 1; i < boardSize; i++) {
            sb.append("+---");
        }

        return sb.append(NEW_LINE_FEED).toString();
    }

    private String getColumnNumber() {
        StringBuilder sb = new StringBuilder(" ");

        for (int i = 1; i <= boardSize; i++) {
            sb.append(getCorrectedSpace(i)).append(i);
        }

        return sb.append(NEW_LINE_FEED).toString();
    }

    private String getCorrectedSpace(int index) {
        if (index > 99) {
            return " ";
        }
        if (index > 9) {
            return "  ";
        }
        return "   ";
    }

    private String appendRowString(StringBuilder sb, int currentRowIndex) {
        return sb.append(currentRowIndex + 1)
                .append(getCorrectedSpace(currentRowIndex + 1))
                .append(String.join(" | ", board[currentRowIndex]))
                .append(NEW_LINE_FEED).toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(columnNumber);
        appendRowString(sb, 0);

        for (int i = 1; i < boardSize; i++) {
            sb.append(rowSeparator);
            appendRowString(sb, i);
        }

        return sb.toString();
    }

    private void validate() {
        StringBuilder sb = new StringBuilder();

        if (boardSize < 3 || boardSize > 999) {
            sb.append("Board size invalid (Should be between 3-999). ");
        }

        if (playerCount < 2 || playerCount > 26) {
            sb.append("Number of players invalid (Should be between 2-26). ");
        }

        if (winSequence < 3 || boardSize < winSequence
                || Math.sqrt(2 * boardSize * boardSize) < winSequence) {
            sb.append("Game not possible with this win sequence.");
        }

        if ((winSequence - 1) * playerCount + 1 > boardSize * boardSize) {
            sb.append("Total blocks not enough for winning. ");
        }

        if (sb.length() > 0) {
            throw new InvalidParameterException(sb.toString());
        }

        System.out.println(NEW_LINE_FEED + "Starting a new Game");
    }
}