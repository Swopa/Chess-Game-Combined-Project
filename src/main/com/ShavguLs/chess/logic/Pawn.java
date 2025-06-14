public class Pawn extends Piece{

    public Pawn(boolean isWhite) {
        super(isWhite);
    }


    public Pawn clone() {
        Pawn copy = new Pawn(this.isWhite);
        copy.hasMoved = this.hasMoved;
        return copy;
    }

    @Override
    boolean isValidMove(int srcRow, int srcCol, int destRow, int destCol, Piece[][] board) {

        int direction = this.isWhite ? 1 : -1;
        int startRow = this.isWhite ? 1 : 6;

        int rowDiff = destRow - srcRow;
        int colDiff = destCol - srcCol;

        Piece destinationPiece = board[destRow][destCol];


        if(srcCol == destCol && destinationPiece == null){
            if(rowDiff == direction){
                return true;
            }
            if(srcRow == startRow && rowDiff == 2*direction
                    && board[srcRow + direction][srcCol] == null){
                return true;
            }
        }


        //Capture a piece
        return destinationPiece != null && Math.abs(colDiff) == 1 && (rowDiff) == direction && destinationPiece.isWhite != this.isWhite;
    }
}
