package main.com.ShavguLs.chess.model;

public interface GameObserver {
    void onGameOver(boolean whiteWins, String reason);
    void onTurnChanged(boolean isWhiteTurn);
    void onPawnPromotion(Pawn pawn);
}