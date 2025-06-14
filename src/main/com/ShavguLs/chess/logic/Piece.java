abstract class Piece {
    boolean isWhite;
    boolean hasMoved = false;

    public void markMove(){
        this.hasMoved = true;
    }

    public boolean hasMoved(){
        return this.hasMoved;
    }

    public Piece(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public abstract Piece clone();

    abstract boolean isValidMove(int srcRow, int srcCol, int destRow, int destCol, Piece[][] board);
}
