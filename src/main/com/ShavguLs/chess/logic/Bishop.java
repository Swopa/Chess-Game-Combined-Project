public class Bishop extends Piece{
    public Bishop(boolean isWhite) {
        super(isWhite);
    }

    public Bishop clone() {
        Bishop copy = new Bishop(this.isWhite);
        copy.hasMoved = this.hasMoved;
        return copy;
    }

    @Override
    boolean isValidMove(int srcRow, int srcCol, int destRow, int destCol, Piece[][] board) {
        int rowDiff = Math.abs(srcRow - destRow);
        int colDiff = Math.abs(srcCol - destCol);

        //Bishop can move in diagonal direction by as much as he wants
        if(rowDiff != colDiff){
            return false;
        }
        //While moving in diagonal row and column change the same way

        int rowStep = destRow > srcRow ? 1: -1;
        int colStep = destCol > srcCol ? 1: -1;

        int row = srcRow + rowStep;
        int col = srcCol + colStep;

        while(row != destRow ){
            if(board[row][col] != null){
                return false;
            }
            row+=rowStep;
            col+=colStep;
        }

        Piece destPiece = board[destRow][destCol];

        return destPiece == null || destPiece.isWhite != this.isWhite;
    }
}
