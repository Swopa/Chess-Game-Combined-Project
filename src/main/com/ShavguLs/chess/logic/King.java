package main.com.ShavguLs.chess.logic;

public class King extends Piece {

    public King(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public King clone() {
        King copy = new King(this.isWhite);
        copy.hasMoved = this.hasMoved;
        return copy;
    }

    @Override
    public char getFenChar() {
        return isWhite ? 'K' : 'k';
    }

    @Override
    boolean isValidMove(int srcRow, int srcCol, int destRow, int destCol, Piece[][] board) {
        int rowDiff = Math.abs(destRow - srcRow);
        int colDiff = Math.abs(destCol - srcCol);

        // Standard one-square move
        if (rowDiff <= 1 && colDiff <= 1 && (rowDiff + colDiff > 0)) {
            Piece destPiece = board[destRow][destCol];
            return destPiece == null || destPiece.isWhite != this.isWhite;
        }

        // --- SIMPLIFIED CASTLING CHECK ---
        // We only check if a 2-square horizontal move is being ATTEMPTED by a king that hasn't moved.
        // The Board will handle the detailed rules.
        if (!this.hasMoved && rowDiff == 0 && colDiff == 2) {
            return true;
        }

        return false;
    }
}