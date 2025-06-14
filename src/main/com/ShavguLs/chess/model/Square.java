package main.com.ShavguLs.chess.model_old;

// Represents one square on the chess board. Knows its position and if it has a piece on it.

public class Square {
    private final int color;

    private final int xNum;
    private final int yNum;

    private Piece occupyingPiece;

    private boolean dispPiece;

    private Board board;

    public Square(int color, int xNum, int yNum, Board board) {
        this.color = color;
        this.xNum = xNum;
        this.yNum = yNum;
        this.dispPiece = true;
        this.board = board;
    }

    public int getColor() {
        return this.color;
    }

    public Piece getOccupyingPiece() {
        return occupyingPiece;
    }

    public boolean isOccupied() {
        return (this.occupyingPiece != null);
    }

    public int getXNum() {
        return this.xNum;
    }

    public int getYNum() {
        return this.yNum;
    }

    public void setDisplay(boolean v) {
        this.dispPiece = v;
    }

    public boolean getDisplay() {
        return this.dispPiece;
    }

    public void put(Piece p) {
        this.occupyingPiece = p;
        p.setPosition(this);
    }

    public Piece removePiece() {
        Piece p = this.occupyingPiece;
        this.occupyingPiece = null;
        return p;
    }

    public void capture(Piece p, Board board) {
        Piece captured = getOccupyingPiece();
        if (captured != null) {
            if (captured.getColor() == 0) board.getBlackPieces().remove(captured);
            if (captured.getColor() == 1) board.getWhitePieces().remove(captured);
        }
        this.occupyingPiece = p;
    }

    @Override
    public String toString() {
        return "(" + xNum + "," + yNum + ")";
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + xNum;
        result = prime * result + yNum;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Square square = (Square) obj;
        return xNum == square.xNum && yNum == square.yNum;
    }

    public Board getBoard(){
        return board;
    }
}