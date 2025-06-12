package main.com.ShavguLs.chess.view;

import main.com.ShavguLs.chess.model.*;
import javax.swing.JOptionPane;

public class PromotionHandler {

    public static Piece handlePawnPromotion(Pawn pawn) {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        int choice = JOptionPane.showOptionDialog(null,
                "Choose promotion piece",
                "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        int color = pawn.getColor();
        Square position = pawn.getPosition();
        String imgPrefix = (color == 1) ? "/images/w" : "/images/b";

        switch (choice) {
            case 0: return new Queen(color, position);
            case 1: return new Rook(color, position);
            case 2: return new Bishop(color, position);
            case 3: return new Knight(color, position);
            default: return new Queen(color, position);
        }
    }
}