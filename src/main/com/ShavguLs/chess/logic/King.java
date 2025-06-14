public class King extends Piece{


    public King(boolean isWhite) {
        super(isWhite);
    }


    public King clone() {
        King copy = new King(this.isWhite);
        copy.hasMoved = this.hasMoved;
        return copy;
    }

    @Override
    boolean isValidMove(int srcRow, int srcCol, int destRow, int destCol, Piece[][] board) {
        //King moves one space in any direction
        int rowDiff = Math.abs(srcRow-destRow);
        int colDiff = Math.abs(srcCol-destCol);
        int startRow = isWhite ? 0 : 7;





        if (this.isWhite && srcRow == 0 && srcCol == 4 && destRow == 0 && destCol == 6) {
            return canShortCastle(board);
        }
        if (!this.isWhite && srcRow == 7 && srcCol == 4 && destRow == 7 && destCol == 6) {
            return canShortCastle(board);
        }


        if (this.isWhite && srcRow == 0 && srcCol == 4 && destRow == 0 && destCol == 2) {
            return canLongCastle(board);
        }
        if (!this.isWhite && srcRow == 7 && srcCol == 4 && destRow == 7 && destCol == 2) {
            return canLongCastle(board);
        }



        if((rowDiff <= 1 && colDiff <= 1) && (rowDiff + colDiff) != 0){
            Piece destPiece = board[destRow][destCol];

            return destPiece == null || destPiece.isWhite != this.isWhite;
        }


        return false;
    }



    boolean canShortCastle(Piece[][] board){
        if(this.hasMoved){
            return false;
        }

        Board tempBoard = new Board();
        tempBoard.board = board;
        int row = isWhite ? 0 : 7;


        Piece rook = board[row][7];
        if (rook == null || !(rook instanceof Rook) || rook.hasMoved()) return false;
        if (board[row][5] != null || board[row][6] != null) return false;

        // Check squares are not attacked
        if (tempBoard.isSquareAttacked(row, 4, !isWhite)) return false; // King's original square
        if (tempBoard.isSquareAttacked(row, 5, !isWhite)) return false; // Square king passes through
        if (tempBoard.isSquareAttacked(row, 6, !isWhite)) return false; // Destination

        return true;



    }

    boolean canLongCastle(Piece[][] board){
        if(this.hasMoved){
            return false;
        }

        int row = isWhite ? 0 : 7;
        Board tempBoard = new Board();
        tempBoard.board = board;

        // Check rook and empty spaces
        Piece rook = board[row][0];
        if (rook == null || !(rook instanceof Rook) || rook.hasMoved()) return false;
        if (board[row][1] != null || board[row][2] != null || board[row][3] != null) return false;

        // Check squares are not attacked
        if (tempBoard.isSquareAttacked(row, 4, !isWhite)) return false; // King's original square
        if (tempBoard.isSquareAttacked(row, 3, !isWhite)) return false; // Square king passes through
        if (tempBoard.isSquareAttacked(row, 2, !isWhite)) return false; // Destination

        return true;
    }
}
