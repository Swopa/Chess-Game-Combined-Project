// In: main/com/ShavguLs/chess/view/PromotionHandler.java
// Replaces the ENTIRE old file.

package main.com.ShavguLs.chess.view;

// Import all the piece types from YOUR logic package
import main.com.ShavguLs.chess.logic.Piece;
import main.com.ShavguLs.chess.logic.Queen;
import main.com.ShavguLs.chess.logic.Rook;
import main.com.ShavguLs.chess.logic.Bishop;
import main.com.ShavguLs.chess.logic.Knight;

import javax.swing.JOptionPane;

public class PromotionHandler {

    /**
     * Shows a dialog to the user to choose a piece for promotion.
     * This method is now decoupled from the board and position.
     *
     * @param isWhite The color of the pawn being promoted. True for white, false for black.
     * @return The new Piece object chosen by the user.
     */
    public static Piece getPromotionChoice(boolean isWhite) {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        int choice = JOptionPane.showOptionDialog(null,
                "Choose a piece for promotion:",
                "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        // Create the new piece based on the user's choice and the pawn's color.
        // This now calls the constructors of YOUR logic.Piece classes.
        switch (choice) {
            case 0:  return new Queen(isWhite);
            case 1:  return new Rook(isWhite);
            case 2:  return new Bishop(isWhite);
            case 3:  return new Knight(isWhite);
            default: return new Queen(isWhite); // Default to Queen if the dialog is closed
        }
    }
}