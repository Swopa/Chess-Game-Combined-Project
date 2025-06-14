public class Board {
    Piece[][] board = new Piece[8][8];


    public Board(){
        setUpInitialPosition();
    }

    public void setPiece(int row, int col, Piece piece){
        board[row][col] = piece;
    }


    public void setUpInitialPosition(){
        //White pieces
        setPiece(0, 0, new Rook(true));
        setPiece(0, 1, new Knight(true));
        setPiece(0, 2, new Bishop(true));
        setPiece(0, 3, new Queen(true));
        setPiece(0, 4, new King(true));
        setPiece(0, 5, new Bishop(true));
        setPiece(0, 6, new Knight(true));
        setPiece(0, 7, new Rook(true));
        for(int i = 0; i < 8; i++){
            setPiece(1, i, new Pawn(true));
        }

        //Black pieces
        setPiece(7, 0, new Rook(false));
        setPiece(7, 1, new Knight(false));
        setPiece(7, 2, new Bishop(false));
        setPiece(7, 3, new Queen(false));
        setPiece(7, 4, new King(false));
        setPiece(7, 5, new Bishop(false));
        setPiece(7, 6, new Knight(false));
        setPiece(7, 7, new Rook(false));
        for(int i = 0; i < 8; i++){
            setPiece(6, i, new Pawn(false));
        }
    }

    void movePiece(int srcRow, int srcCol, int destRow, int destCol){
        Piece p = board[srcRow][srcCol];
        if(p != null){
            board[destRow][destCol] = p;
            board[srcRow][srcCol] = null;
        }else {
            System.out.println("Invalid Move!");
        }
    }


    public boolean isKingInCheck(boolean isWhite){
        int kingRow = -1, kingCol = -1;

        for(int row = 0; row < 8; row++){
            for(int col = 0; col < 8; col++){
                Piece piece = board[row][col];
                if(piece instanceof King && piece.isWhite == isWhite){
                    kingRow = row;
                    kingCol = col;
                    break;
                }
            }
        }

        if(kingRow == -1){
            throw new IllegalStateException("King not found on the board.");
        }


        return isSquareAttacked(kingRow, kingCol, isWhite);
    }



    public boolean isSquareAttacked(int row, int col, boolean byWhite){
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece attacker = board[r][c];
                if (attacker != null && attacker.isWhite == byWhite) {
                    if (attacker.isValidMove(r, c, row, col, board)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public Board copy() {
        Board newBoard = new Board();
        newBoard.clearBoard(); // prevents overwriting via setup

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece != null) {
                    newBoard.setPiece(row, col, piece.clone());
                }
            }
        }
        return newBoard;
    }


    public void clearBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = null;
            }
        }
    }


}
