package main.com.ShavguLs.chess.model;

import java.util.LinkedList;
import java.util.List;

public class King extends Piece{
    private boolean hasMoved;

    public King(int color, Square initSq) {
        super(color, initSq);
        this.hasMoved = false;
    }

    public boolean move(Square fin){
        boolean result = super.move(fin);
        if (result){
            hasMoved = true;
        }
        return result;
    }

    public boolean hasMoved(){
        return hasMoved;
    }

    public void setHasMoved(boolean moved) {
        this.hasMoved = moved;
    }

    @Override
    public List<Square> getLegalMoves(Board b) {
        LinkedList<Square> legalMoves = new LinkedList<Square>();

        Square[][] board = b.getSquareArray();

        int x = this.getPosition().getXNum();
        int y = this.getPosition().getYNum();

        for (int i = 1; i > -2; i--) {
            for (int k = 1; k > -2; k--) {
                if(!(i == 0 && k == 0)) {
                    try {
                        if(!board[y + k][x + i].isOccupied() ||
                                board[y + k][x + i].getOccupyingPiece().getColor()
                                        != this.getColor()) {
                            legalMoves.add(board[y + k][x + i]);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        continue;
                    }
                }
            }
        }

        if (!hasMoved && !b.getCheckmateDetector().kingInCheck(this.getColor())){
            if (canCastleKingside(board, x, y)){
                legalMoves.add(board[y][x+2]);
            }

            if (canCastleQueenside(board, x, y)){
                legalMoves.add(board[y][x-2]);
            }
        }

        return legalMoves;
    }

    private boolean canCastleKingside(Square[][] board, int x, int y) {
        if (x + 3 < 8 && !hasMoved && board[y][x + 3].isOccupied()) {
            Piece piece = board[y][x + 3].getOccupyingPiece();

            if (piece instanceof Rook &&
                    piece.getColor() == this.getColor() &&
                    !((Rook)piece).hasMoved()) {

                if (!board[y][x + 1].isOccupied() && !board[y][x + 2].isOccupied()) {
                    CheckmateDetector detector = getPosition().getBoard().getCheckmateDetector();
                    if (!detector.kingInCheck(this.getColor()) &&
                            !detector.isSquareAttacked(board[y][x + 1], this.getColor()) &&
                            !detector.isSquareAttacked(board[y][x + 2], this.getColor())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean canCastleQueenside(Square[][] board, int x, int y) {
        if (x - 4 >= 0 && !hasMoved && board[y][x - 4].isOccupied()) {
            Piece piece = board[y][x - 4].getOccupyingPiece();

            if (piece instanceof Rook &&
                    piece.getColor() == this.getColor() &&
                    !((Rook)piece).hasMoved()) {

                if (!board[y][x - 1].isOccupied() &&
                        !board[y][x - 2].isOccupied() &&
                        !board[y][x - 3].isOccupied()) {
                    CheckmateDetector detector = getPosition().getBoard().getCheckmateDetector();
                    if (!detector.kingInCheck(this.getColor()) &&
                            !detector.isSquareAttacked(board[y][x - 1], this.getColor()) &&
                            !detector.isSquareAttacked(board[y][x - 2], this.getColor())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
