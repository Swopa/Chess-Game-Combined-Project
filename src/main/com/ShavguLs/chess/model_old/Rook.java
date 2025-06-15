package main.com.ShavguLs.chess.model;

import java.util.LinkedList;
import java.util.List;

public class Rook extends Piece{
    private boolean hasMoved;

    public Rook(int color, Square initSq) {
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

    @Override
    public List<Square> getLegalMoves(Board b) {
        LinkedList<Square> legalMoves = new LinkedList<Square>();
        Square[][] board = b.getSquareArray();

        int x = this.getPosition().getXNum();
        int y = this.getPosition().getYNum();

        int[] occups = getLinearOccupations(board, x, y);

        for (int i = occups[0]; i <= occups[1]; i++) {
            if (i != y) legalMoves.add(board[i][x]);
        }

        for (int i = occups[2]; i <= occups[3]; i++) {
            if (i != x) legalMoves.add(board[y][i]);
        }

        return legalMoves;
    }

    public void setHasMoved(boolean moved) {
        this.hasMoved = moved;
    }
}
