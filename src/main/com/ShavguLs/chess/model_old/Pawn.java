package main.com.ShavguLs.chess.model;

import java.util.LinkedList;
import java.util.List;

public class Pawn extends Piece{
    private boolean wasMoved;
    private boolean justMovedTwo = false;
    private static Pawn enPassantTarget = null;

    public Pawn(int color, Square initSq){
        super(color, initSq);
    }

    public boolean canPromote(){
        int y = this.getPosition().getYNum();
        return (this.getColor() == 0 && y == 7) || (this.getColor() == 1 && y == 0);
    }

    public boolean canBeCapturedEnPassant() {
        return justMovedTwo;
    }

    public static Pawn getEnPassantTarget() {
        return enPassantTarget;
    }

    public static void clearEnPassantTarget() {
        enPassantTarget = null;
    }

    @Override
    public boolean move(Square fin) {
        if (fin == null || this.getPosition() == null) {
            return false;
        }

        int startY = this.getPosition().getYNum();
        int endY = fin.getYNum();
        int startX = this.getPosition().getXNum();
        int endX = fin.getXNum();

        if (enPassantTarget != null && enPassantTarget != this) {
            enPassantTarget.justMovedTwo = false;
        }
        Pawn.clearEnPassantTarget();

        if (Math.abs(endY - startY) == 2 && !wasMoved) {
            justMovedTwo = true;
            enPassantTarget = this;
        } else {
            justMovedTwo = false;
        }

        boolean isEnPassant = false;
        Piece capturedPawn = null;
        Square captureSquare = null;

        if (Math.abs(endX - startX) == 1 && !fin.isOccupied()) {
            Square[][] board = this.getPosition().getBoard().getSquareArray();
            if (this.getColor() == 0 && startY == 4) { // Black pawn
                captureSquare = board[startY][endX];
                if (captureSquare.isOccupied() &&
                        captureSquare.getOccupyingPiece() instanceof Pawn &&
                        captureSquare.getOccupyingPiece() == Pawn.getEnPassantTarget()) {
                    isEnPassant = true;
                    capturedPawn = captureSquare.getOccupyingPiece();
                }
            } else if (this.getColor() == 1 && startY == 3) { // White pawn
                captureSquare = board[startY][endX];
                if (captureSquare.isOccupied() &&
                        captureSquare.getOccupyingPiece() instanceof Pawn &&
                        captureSquare.getOccupyingPiece() == Pawn.getEnPassantTarget()) {
                    isEnPassant = true;
                    capturedPawn = captureSquare.getOccupyingPiece();
                }
            }
        }

        boolean b = super.move(fin);

        if (b && isEnPassant && capturedPawn != null && captureSquare != null) {
            captureSquare.removePiece();
            Board board = this.getPosition().getBoard();
            if (capturedPawn.getColor() == 0) {
                board.getBlackPieces().remove(capturedPawn);
            } else {
                board.getWhitePieces().remove(capturedPawn);
            }
            capturedPawn.setPosition(null);
        }

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

            if (y == 4) {
                if (x > 0) {
                    Square leftSquare = board[y][x-1];
                    if (leftSquare.isOccupied() &&
                            leftSquare.getOccupyingPiece() instanceof Pawn &&
                            leftSquare.getOccupyingPiece().getColor() != c &&
                            leftSquare.getOccupyingPiece() == Pawn.getEnPassantTarget()) {
                        legalMoves.add(board[y+1][x-1]);
                    }
                }
                if (x < 7) {
                    Square rightSquare = board[y][x+1];
                    if (rightSquare.isOccupied() &&
                            rightSquare.getOccupyingPiece() instanceof Pawn &&
                            rightSquare.getOccupyingPiece().getColor() != c &&
                            rightSquare.getOccupyingPiece() == Pawn.getEnPassantTarget()) {
                        legalMoves.add(board[y+1][x+1]);
                    }
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

            if (y == 3) {
                if (x > 0) {
                    Square leftSquare = board[y][x-1];
                    if (leftSquare.isOccupied() &&
                            leftSquare.getOccupyingPiece() instanceof Pawn &&
                            leftSquare.getOccupyingPiece().getColor() != c &&
                            leftSquare.getOccupyingPiece() == Pawn.getEnPassantTarget()) {
                        legalMoves.add(board[y-1][x-1]);
                    }
                }
                if (x < 7) {
                    Square rightSquare = board[y][x+1];
                    if (rightSquare.isOccupied() &&
                            rightSquare.getOccupyingPiece() instanceof Pawn &&
                            rightSquare.getOccupyingPiece().getColor() != c &&
                            rightSquare.getOccupyingPiece() == Pawn.getEnPassantTarget()) {
                        legalMoves.add(board[y-1][x+1]);
                    }
                }
            }
        }

        return legalMoves;
    }

    public void setWasMoved(boolean moved) {
        this.wasMoved = moved;
    }

    public boolean hasMovedBefore(){
        return wasMoved;
    }

    public void setJustMovedTwo(boolean moved) {
        this.justMovedTwo = moved;
    }
}