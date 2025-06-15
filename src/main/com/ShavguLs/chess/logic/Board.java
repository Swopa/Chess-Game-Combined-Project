package main.com.ShavguLs.chess.logic;

public class Board {
    Piece[][] board = new Piece[8][8];


    public Board(){
        //setUpInitialPosition();
    }

    public void setPiece(int row, int col, Piece piece){
        board[row][col] = piece;
    }


    public Piece getPieceAt(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            return null; // Out of bounds
        }
        return board[row][col];
    }

    public Piece[][] getBoardArray() {
        return this.board;
    }


    public boolean attemptMove(int srcRow, int srcCol, int destRow, int destCol, boolean isWhiteTurn) {
        Piece pieceToMove = getPieceAt(srcRow, srcCol);

        // 1. Basic checks
        if (pieceToMove == null || pieceToMove.isWhite() != isWhiteTurn) {
            return false;
        }
        if (!pieceToMove.isValidMove(srcRow, srcCol, destRow, destCol, this.board)) {
            return false;
        }

        // A king can't move to a square that is under attack by the opponent.
        if (pieceToMove instanceof King) {
            // Check if the destination square is attacked by the OTHER player
            if (isSquareAttacked(destRow, destCol, !isWhiteTurn)) {
                System.out.println("DEBUG: King move failed. Destination square (" + destRow + "," + destCol + ") is under attack.");
                return false;
            }
        }


        // SPECIAL CASE: CASTLING
        boolean isCastleAttempt = pieceToMove instanceof King && Math.abs(destCol - srcCol) == 2;
        if (isCastleAttempt) {
            // The handleCastling method will perform the move if it's legal
            return handleCastling(srcRow, srcCol, destRow, destCol, isWhiteTurn);
        }

        // REGULAR MOVE LOGIC
        // Simulate the move and check if it leaves the king in check
        Board boardAfterMove = this.copy();
        boardAfterMove.movePiece(srcRow, srcCol, destRow, destCol);
        if (boardAfterMove.isKingInCheck(isWhiteTurn)) {
            System.out.println("DEBUG: Move failed. It would leave the king in check.");
            return false; // Move is illegal because it results in self-check
        }

        // If all checks pass, perform the move on the actual board
        this.movePiece(srcRow, srcCol, destRow, destCol);
        Piece movedPiece = getPieceAt(destRow, destCol); // Get the piece that just moved
        if (movedPiece instanceof Pawn) {
            //TODO Piece has to become whatever user chooses
            if ( (movedPiece.isWhite() && destRow == 0) || (!movedPiece.isWhite() && destRow == 7) ) {
                // It's a promotion!
                // For now, let's just auto-promote to a Queen.
                setPiece(destRow, destCol, new Queen(movedPiece.isWhite()));
            }
        }
        pieceToMove.markMove();
        return true;
    }



    public void setupStandardBoard() {
        clearBoard(); // Start with a completely empty board

        // Black pieces (top of the board, rows 0 and 1)
        setPiece(0, 0, new Rook(false));
        setPiece(0, 1, new Knight(false));
        setPiece(0, 2, new Bishop(false));
        setPiece(0, 3, new Queen(false));
        setPiece(0, 4, new King(false));
        setPiece(0, 5, new Bishop(false));
        setPiece(0, 6, new Knight(false));
        setPiece(0, 7, new Rook(false));
        for (int i = 0; i < 8; i++) {
            setPiece(1, i, new Pawn(false));
        }

        // White pieces (bottom of the board, rows 6 and 7)
        for (int i = 0; i < 8; i++) {
            setPiece(6, i, new Pawn(true));
        }
        setPiece(7, 0, new Rook(true));
        setPiece(7, 1, new Knight(true));
        setPiece(7, 2, new Bishop(true));
        setPiece(7, 3, new Queen(true));
        setPiece(7, 4, new King(true));
        setPiece(7, 5, new Bishop(true));
        setPiece(7, 6, new Knight(true));
        setPiece(7, 7, new Rook(true));
    }

    void movePiece(int srcRow, int srcCol, int destRow, int destCol){
        Piece p = board[srcRow][srcCol];
        board[destRow][destCol] = p;
        board[srcRow][srcCol] = null;
    }




    public boolean isKingInCheck(boolean isWhiteKing) {
        int kingRow = -1, kingCol = -1;

        // Find the king
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = getPieceAt(r, c);
                if (piece instanceof King && piece.isWhite() == isWhiteKing) {
                    kingRow = r;
                    kingCol = c;
                    break;
                }
            }
            if (kingRow != -1) break;
        }

        if (kingRow == -1) {
            System.out.println("DEBUG (isKingInCheck): Could not find the " + (isWhiteKing ? "White" : "Black") + " king!");
            return false; // Should not happen
        }

        System.out.println("DEBUG (isKingInCheck): Checking if " + (isWhiteKing ? "White" : "Black") + " king at (" + kingRow + "," + kingCol + ") is attacked by " + (isWhiteKing ? "Black" : "White") + ".");

        // Check if that square is attacked by the OPPONENT
        boolean isAttacked = isSquareAttacked(kingRow, kingCol, !isWhiteKing);

        if (isAttacked) {
            System.out.println("DEBUG (isKingInCheck): YES, king is in check.");
        } else {
            System.out.println("DEBUG (isKingInCheck): NO, king is not in check.");
        }

        return isAttacked;
    }


    private boolean handleCastling(int srcRow, int srcCol, int destRow, int destCol, boolean isWhiteTurn) {
        if (isKingInCheck(isWhiteTurn)) {
            return false;
        }

        int direction = destCol - srcCol;
        int rookCol = (direction > 0) ? 7 : 0;
        Piece rook = getPieceAt(srcRow, rookCol);

        if (!(rook instanceof Rook) || rook.hasMoved()) {
            return false;
        }

        int step = (direction > 0) ? 1 : -1;
        for (int c = srcCol + step; c != rookCol; c += step) {
            if (getPieceAt(srcRow, c) != null) {
                return false;
            }
        }

        if (isSquareAttacked(srcRow, srcCol + step, !isWhiteTurn)) {
            return false;
        }

        Board boardAfterMove = this.copy();
        boardAfterMove.movePiece(srcRow, srcCol, destRow, destCol);
        if(boardAfterMove.isKingInCheck(isWhiteTurn)){
            return false;
        }

        // All checks passed, perform the move
        movePiece(srcRow, srcCol, destRow, destCol); // Move King
        getPieceAt(destRow, destCol).markMove();
        movePiece(srcRow, rookCol, srcRow, srcCol + step); // Move Rook
        getPieceAt(srcRow, srcCol + step).markMove();

        return true;
    }


    //Checks every piece if it is attacking certain square. Is used in king check logic too
    public boolean isSquareAttacked(int row, int col, boolean byWhite) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece attacker = board[r][c];
                if (attacker != null && attacker.isWhite() == byWhite) {
                    if (attacker.isAttackingSquare(r, c, row, col, board)) {
                        System.out.println("DEBUG (isSquareAttacked): Square (" + row + "," + col + ") IS attacked by "
                                + attacker.getClass().getSimpleName() + " at (" + r + "," + c + ")");
                        return true;
                    }
                }
            }
        }
        System.out.println("DEBUG (isSquareAttacked): Square (" + row + "," + col + ") is NOT under attack by " + (byWhite ? "White" : "Black") + ".");
        return false;
    }

    //Copies board. Is used for king check logic
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

    //clears board
    public void clearBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = null;
            }
        }
    }

    //Checks if board has any legal moves for stalemate
    public boolean hasLegalMoves(boolean isWhiteSide) {
        // Loop through every square on the board
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = getPieceAt(r, c);

                // If the square has a piece of the correct color...
                if (piece != null && piece.isWhite() == isWhiteSide) {
                    // ...check every possible destination square for that piece.
                    for (int destR = 0; destR < 8; destR++) {
                        for (int destC = 0; destC < 8; destC++) {
                            // We use a temporary copy of the board to test the move
                            // without changing the actual game state.
                            Board tempBoard = this.copy();

                            // attemptMove already contains all the necessary validation,
                            // including the check for leaving the king in danger.
                            if (tempBoard.attemptMove(r, c, destR, destC, isWhiteSide)) {
                                // If we find even ONE legal move, we can stop searching.
                                return true;
                            }
                        }
                    }
                }
            }
        }

        // If we have checked every piece and every move and found nothing legal,
        // then the player has no legal moves.
        return false;
    }
}
