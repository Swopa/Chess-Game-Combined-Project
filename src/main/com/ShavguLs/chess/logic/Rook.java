public class Rook extends Piece{


    public Rook(boolean isWhite) {
        super(isWhite);
    }

    public Rook clone() {
        Rook copy = new Rook(this.isWhite);
        copy.hasMoved = this.hasMoved;
        return copy;
    }


    @Override
    boolean isValidMove(int srcRow, int srcCol, int destRow, int destCol, Piece[][] board) {

        //Moves in horizontal or vertical direction so either row or column stays unchanged and the other parameter changes
        if (srcRow == destRow){
            int step = (destCol > srcCol) ? 1 : -1;

            for(int col = srcCol + step; col != destCol; col+=step){
                if(board[srcRow][col] != null){
                    return false;
                }
            }
        } else if (srcCol == destCol) {

            int step = (destRow > srcRow) ? 1 : -1;

            for(int row = srcRow + step; row != destRow; row+=step){
                if(board[row][srcCol] != null){
                    return false;
                }
            }
        }else{
            return false;
        }



        Piece destPiece = board[destRow][destCol];


        return destPiece == null || destPiece.isWhite != this.isWhite;
    }
}
