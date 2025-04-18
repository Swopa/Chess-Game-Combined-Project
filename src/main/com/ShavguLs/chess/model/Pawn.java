package main.com.ShavguLs.chess.model;

import java.util.LinkedList;
import java.util.List;

public class Pawn extends Piece{
    private boolean wasMoved;

    public Pawn(int color, Square initSq, String imgFile){
        super(color, initSq, imgFile);
    }

    public boolean canPromote(){
        int y = this.getPosition().getYNum();
        return (this.getColor() == 0 && y == 7) || (this.getColor() == 1 && y == 0);
    }

    @Override
    public boolean move(Square fin) {
        boolean b = super.move(fin);
        wasMoved = true;
        return b;
    }

    @Override
    public List<Square> getLegalMoves(Board b) {
        LinkedList<Square> legalMoves = new LinkedList<Square>();
        Square[][] board = b.getSquareArray();

        int x = this.getPosition().getXNum();
        int y = this.getPosition().getYNum();
        int c = this.getColor();

        if (c == 0) {
            if (!wasMoved) {
                if (y + 1 < 8 && !board[y+1][x].isOccupied() &&
                        y + 2 < 8 && !board[y+2][x].isOccupied()) {
                    legalMoves.add(board[y+2][x]);
                }
            }

            if (y + 1 < 8 && !board[y+1][x].isOccupied()) {
                legalMoves.add(board[y+1][x]);
            }

            if (x + 1 < 8 && y + 1 < 8) {
                if (board[y+1][x+1].isOccupied() &&
                        board[y+1][x+1].getOccupyingPiece().getColor() != this.getColor()) {
                    legalMoves.add(board[y+1][x+1]);
                }
            }

            if (x - 1 >= 0 && y + 1 < 8) {
                if (board[y+1][x-1].isOccupied() &&
                        board[y+1][x-1].getOccupyingPiece().getColor() != this.getColor()) {
                    legalMoves.add(board[y+1][x-1]);
                }
            }
        }

        if (c == 1) {
            if (!wasMoved) {
                if (y - 1 >= 0 && !board[y-1][x].isOccupied() &&
                        y - 2 >= 0 && !board[y-2][x].isOccupied()) {
                    legalMoves.add(board[y-2][x]);
                }
            }

            if (y - 1 >= 0 && !board[y-1][x].isOccupied()) {
                legalMoves.add(board[y-1][x]);
            }

            if (x + 1 < 8 && y - 1 >= 0) {
                if (board[y-1][x+1].isOccupied() &&
                        board[y-1][x+1].getOccupyingPiece().getColor() != this.getColor()) {
                    legalMoves.add(board[y-1][x+1]);
                }
            }
            if (x - 1 >= 0 && y - 1 >= 0) {
                if (board[y-1][x-1].isOccupied() &&
                        board[y-1][x-1].getOccupyingPiece().getColor() != this.getColor()) {
                    legalMoves.add(board[y-1][x-1]);
                }
            }
        }
        return legalMoves;
    }

    public boolean hasMovedBefore(){
        return wasMoved;
    }
}
