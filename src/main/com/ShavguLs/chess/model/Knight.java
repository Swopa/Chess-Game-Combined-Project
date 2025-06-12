package main.com.ShavguLs.chess.model;

import java.util.LinkedList;
import java.util.List;

public class Knight extends Piece{
    public Knight(int color, Square initSq){
        super(color, initSq);
    }

    @Override
    public List<Square> getLegalMoves(Board b) {
        LinkedList<Square> legalMoves = new LinkedList<Square>();
        Square[][] board = b.getSquareArray();

        int x = this.getPosition().getXNum();
        int y = this.getPosition().getYNum();

        for (int i = 2; i > -3; i--) {
            for (int k = 2; k > -3; k--) {
                if((Math.abs(i) == 2 && Math.abs(k) == 1) || (Math.abs(i) == 1 && Math.abs(k) == 2)) {
                    if (k != 0 && i != 0) {
                        try {
                            Square targetSquare = board[y + k][x + i];
                            if (!targetSquare.isOccupied() || targetSquare.getOccupyingPiece().getColor() != this.getColor()){
                                legalMoves.add(targetSquare);
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            continue;
                        }
                    }
                }
            }
        }
        return legalMoves;
    }
}
