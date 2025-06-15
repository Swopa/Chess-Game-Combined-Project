// In: main/com/ShavguLs/chess/controller/GameController.java
// Replaces the ENTIRE old file.

package main.com.ShavguLs.chess.controller;

import main.com.ShavguLs.chess.logic.Board;
import main.com.ShavguLs.chess.logic.Piece;
import main.com.ShavguLs.chess.view.GameWindow;
import main.com.ShavguLs.chess.view.StartMenu;
import main.com.ShavguLs.chess.logic.Clock;

import javax.swing.*;

public class GameController {

    private Board logicBoard;
    private boolean isWhiteTurn;
    private Piece selectedPiece;
    private int selectedPieceRow;
    private int selectedPieceCol;

    private final GameWindow gameWindow;
    private Clock whiteClock;
    private Clock blackClock;

    private Timer timer;
    private final Object timerLock = new Object();

    public GameController(int hh, int mm, int ss, GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        setUpNewGame();
        // Only set up and start the clock if a time was actually given
        if (hh > 0 || mm > 0 || ss > 0) {
            setupClock(hh, mm, ss);
            startTimer();
        }
    }

    public void setUpNewGame() {
        stopTimer(); // Stop timer from the previous game
        this.logicBoard = new Board();
        this.logicBoard.setupStandardBoard();
        this.isWhiteTurn = true;
        this.selectedPiece = null;

        // If clocks exist (meaning it's a timed game), we need to handle them.
        if (whiteClock != null) {
            // --- THE FIX IS HERE ---
            // Instead of resetting, we get the original time from the old clock
            // and create NEW clock objects with that starting time.
            int originalSeconds = whiteClock.getInitialSeconds();
            int hh = originalSeconds / 3600;
            int mm = (originalSeconds % 3600) / 60;
            int ss = originalSeconds % 60;

            // Create new clock objects, effectively resetting them
            setupClock(hh, mm, ss);

            // Update the display to show the reset time
            gameWindow.updateWhiteClock(whiteClock.getTime());
            gameWindow.updateBlackClock(blackClock.getTime());

            // Restart the timer for the new game
            startTimer();
        }
    }

    public boolean selectPiece(int row, int col) {
        if (selectedPiece != null) {
            return makeMove(row, col);
        }
        Piece clickedPiece = logicBoard.getPieceAt(row, col);
        if (clickedPiece != null && clickedPiece.isWhite() == isWhiteTurn) {
            this.selectedPiece = clickedPiece;
            this.selectedPieceRow = row;
            this.selectedPieceCol = col;
            return true;
        }
        return false;
    }

    public boolean makeMove(int destRow, int destCol) {
        if (selectedPiece == null) return false;

        boolean moveWasSuccessful = logicBoard.attemptMove(
                selectedPieceRow, selectedPieceCol, destRow, destCol, isWhiteTurn);

        if (moveWasSuccessful) {
            isWhiteTurn = !isWhiteTurn; // Switch turns
            checkGameEndingConditions(); // <-- ADD THIS CALL HERE
        }

        this.selectedPiece = null;
        gameWindow.getChessBoardPanel().repaint();
        return moveWasSuccessful;
    }
    // In: main/com/ShavguLs/chess/controller/GameController.
// REPLACE the existing checkGameEndingConditions method with this one.



    public Board getLogicBoard() { return logicBoard; }
    public Clock getWhiteClock() { return this.whiteClock; }
    public Clock getBlackClock() { return this.blackClock; }

    private void setupClock(int hh, int mm, int ss) {
        whiteClock = new Clock(hh, mm, ss);
        blackClock = new Clock(hh, mm, ss);
    }

    public void stopTimer() {
        synchronized (timerLock) {
            if (timer != null) {
                timer.stop();
                timer = null;
            }
        }
    }

    public void startTimer() {
        stopTimer(); // Always stop the old timer before starting a new one

        synchronized (timerLock) {
            // Create a new timer that will execute the code inside every 1000ms (1 sec)
            timer = new Timer(1000, e -> {
                // This block of code is the "action" that happens every second
                synchronized (timerLock) {
                    if (timer == null) return;

                    if (isWhiteTurn) {
                        whiteClock.decr();
                        // Tell the UI to update its display
                        SwingUtilities.invokeLater(() -> gameWindow.updateWhiteClock(whiteClock.getTime()));
                        if (whiteClock.outOfTime()) {
                            SwingUtilities.invokeLater(() -> gameWindow.timeOut(true));
                        }
                    } else {
                        blackClock.decr();
                        SwingUtilities.invokeLater(() -> gameWindow.updateBlackClock(blackClock.getTime()));
                        if (blackClock.outOfTime()) {
                            SwingUtilities.invokeLater(() -> gameWindow.timeOut(false));
                        }
                    }
                }
            });
            timer.start();
        }
    }


    // In: main/com/ShavguLs/chess/controller/GameController.java
// Add this new private method to the class.

    private void checkGameEndingConditions() {
        // Check the status of the player WHOSE TURN IT IS NOW.
        boolean canMove = logicBoard.hasLegalMoves(isWhiteTurn);

        // If the current player has no legal moves...
        if (!canMove) {
            // ...we then check if their king is in check.
            if (logicBoard.isKingInCheck(isWhiteTurn)) {
                // No legal moves AND in check? That's CHECKMATE.
                // The player who just moved is the winner.
                System.out.println("GAME OVER: CHECKMATE! " + (isWhiteTurn ? "Black" : "White") + " wins.");
                stopTimer(); // Stop the clocks!
                // Tell the GameWindow to show the checkmate message
                gameWindow.checkmateOccurred(isWhiteTurn);
            } else {
                // No legal moves and NOT in check? That's STALEMATE.
                System.out.println("GAME OVER: STALEMATE!");
                stopTimer(); // Stop the clocks!
                // Tell the GameWindow to show the stalemate message
                gameWindow.stalemateOccurred();
            }
        }
    }



}