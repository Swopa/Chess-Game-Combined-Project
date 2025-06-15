// In: src/main/com/ShavguLs/chess/logic/Main.java
package main.com.ShavguLs.chess.logic;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // --- CONFIGURATION ---
        // IMPORTANT: Change this to the actual path of your PGN file.
        // Use double backslashes on Windows, or a single forward slash.
        String pgnFilePath = "src/Tbilisi2015.pgn";

        try {
            // 1. Read all games from the file using YOUR PGNFileReader.
            // This calls your PGNFileReader.readGames() method.
            System.out.println("Reading games from: " + pgnFilePath);
            List<String> allGames = PGNFileReader.readGames(pgnFilePath);
            System.out.println("Found " + allGames.size() + " games to test.");

            int gamesPassed = 0;
            int gamesFailed = 0;

            // 2. Loop through each game and test it
            for (int i = 0; i < allGames.size(); i++) {
                String pgnGame = allGames.get(i);
                System.out.printf("\n========================================\n");
                System.out.printf("   TESTING GAME #%d / %d\n", (i + 1), allGames.size());
                System.out.printf("========================================\n");

                // We pass each game string to our test runner method
                boolean gameSucceeded = runSingleGameTest(pgnGame);

                if (gameSucceeded) {
                    gamesPassed++;
                    System.out.println("\n--- GAME #" + (i + 1) + " PASSED ---");
                } else {
                    gamesFailed++;
                    System.err.println("\n--- GAME #" + (i + 1) + " FAILED ---");
                    // OPTIONAL: Stop after the first failure.
                    // To enable this, uncomment the line below.
                    // break;
                }
            }

            // 3. Print a final summary of the results
            System.out.printf("\n\n========================================\n");
            System.out.println("           TESTING COMPLETE");
            System.out.println("----------------------------------------");
            System.out.println("  Games Passed: " + gamesPassed);
            System.out.println("  Games Failed: " + gamesFailed);
            System.out.println("========================================\n");

        } catch (IOException e) {
            System.err.println("FATAL ERROR: Could not read the PGN file at: " + pgnFilePath);
            e.printStackTrace();
        }
    }

    /**
     * Executes the test for a single PGN game string.
     * @param pgnGame The string containing one full PGN game.
     * @return true if the game was processed successfully, false otherwise.
     */
    private static boolean runSingleGameTest(String pgnGame) {
        Board board = new Board();
        board.setupStandardBoard();

        MoveInterpreter interpreter = new MoveInterpreter(board);

        // Use YOUR PGNParser to get the list of moves for the current game.
        // This calls your PGNParser.parseMoves() method.
        List<String> moves = PGNParser.parseMoves(pgnGame);

        for (String move : moves) {
            try {
                interpreter.interpretMove(move);
            } catch (IllegalMoveException e) {
                System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.err.println("FAILURE ON MOVE: " + move);
                System.err.println("ERROR: " + e.getMessage());
                System.err.println("--- Board State at Failure ---");
                printBoard(board); // Print the board to see the context
                System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                return false; // This game failed
            }
        }
        return true;

    }

    /**
     * Helper method to print the board state to the console for visual checking.
     */
    public static void printBoard(Board board) {
        System.out.println("   a  b  c  d  e  f  g  h");
        System.out.println("  +------------------------+");
        for (int row = 0; row < 8; row++) {
            System.out.print((8 - row) + " |");
            for (int col = 0; col < 8; col++) {
                Piece p = board.getPieceAt(row, col);
                if (p == null) {
                    System.out.print(" . ");
                } else {
                    char pieceChar = ' ';
                    if (p instanceof Pawn)   { pieceChar = 'p'; }
                    else if (p instanceof Knight) { pieceChar = 'n'; }
                    else if (p instanceof Bishop) { pieceChar = 'b'; }
                    else if (p instanceof Rook)   { pieceChar = 'r'; }
                    else if (p instanceof Queen)  { pieceChar = 'q'; }
                    else if (p instanceof King)   { pieceChar = 'k'; }
                    System.out.print(" " + (p.isWhite() ? Character.toUpperCase(pieceChar) : pieceChar) + " ");
                }
            }
            System.out.println("| " + (8 - row));
        }
        System.out.println("  +------------------------+");
        System.out.println("   a  b  c  d  e  f  g  h");
    }
}