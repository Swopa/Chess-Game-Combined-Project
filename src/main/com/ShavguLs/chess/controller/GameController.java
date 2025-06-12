package main.com.ShavguLs.chess.controller;

import main.com.ShavguLs.chess.model.*;
import main.com.ShavguLs.chess.view.GameWindow;
import main.com.ShavguLs.chess.view.PromotionHandler;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

public class GameController implements GameObserver{
    private Board board;
    private CheckmateDetector checkmateDetector;
    private Piece selectedPiece = null;
    private Clock whiteClock;
    private Clock blackClock;
    private Timer timer;
    private GameWindow gameWindow;
    private final Object timerLock = new Object();

    private int movesSinceCapture = 0;
    private List<String> moveHistory = new LinkedList<>();

    public GameController(int hh, int mm, int ss, GameWindow gameWindow){
        this.gameWindow = gameWindow;
        setUpNewGame();
        setupClock(hh, mm, ss);

        board.addObserver(this);
    }

    public void setUpNewGame(){
        board = new Board();
        LinkedList<Piece> whitePieces = new LinkedList<>();
        LinkedList<Piece> blackPieces = new LinkedList<>();

        King whiteKing = null;
        King blackKing = null;

        Square[][] squares = board.getSquareArray();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (squares[x][y].isOccupied()){
                    Piece p = squares[x][y].getOccupyingPiece();

                    if (p.getColor() == 1){
                        whitePieces.add(p);
                        if (p instanceof King){
                            whiteKing = (King) p;
                        }
                    }else {
                        blackPieces.add(p);
                        if (p instanceof King){
                            blackKing = (King) p;
                        }
                    }
                }
            }
        }

        checkmateDetector = new CheckmateDetector(board, whitePieces, blackPieces, whiteKing, blackKing);

        if (whiteClock!=null && blackClock != null){
            resetClocks();
        }

        movesSinceCapture = 0;
        moveHistory.clear();
        Pawn.clearEnPassantTarget();
    }

    private void resetClocks() {
        stopTimer();
    }

    public void setupClock(int hh, int mm, int ss){
        whiteClock = new Clock(hh, mm, ss);
        blackClock = new Clock(hh, mm, ss);
    }

    public boolean selectPiece(int row, int col){
        if (row < 0 || row >= 8 || col < 0 || col >= 8){
            return false;
        }
        Square square = board.getSquareArray()[row][col];
        if (!square.isOccupied()){
            return false;
        }
        Piece piece = square.getOccupyingPiece();
        if (piece.getColor() == 1 && board.getTurn()){
            selectedPiece = piece;
            return true;
        }else if (piece.getColor() == 0 && !board.getTurn()){
            selectedPiece = piece;
            return true;
        }
        return false;
    }

    public boolean makeMove(int row, int col) {
        if (selectedPiece == null) {
            return false;
        }

        if (row < 0 || row >= 8 || col < 0 || col >= 8) {
            return false;
        }

        Square destination = board.getSquareArray()[row][col];
        Square origin = selectedPiece.getPosition();

        System.out.println("Attempting move: " + selectedPiece.getClass().getSimpleName() +
                " from (" + origin.getYNum() + "," + origin.getXNum() + ")" +
                " to (" + row + "," + col + ")");

        List<Square> legalMoves = selectedPiece.getLegalMoves(board);

        boolean inLegalMoves = legalMoves.contains(destination);
        System.out.println("Destination in legal moves: " + inLegalMoves);

        if (!inLegalMoves) {
            System.out.println("Move rejected: Not in piece's legal moves");
            return false;
        }

        if (!checkmateDetector.testMove(selectedPiece, destination)) {
            System.out.println("Move rejected: Would leave king in check");
            return false;
        }

        boolean isCastling = false;
        if (selectedPiece instanceof King) {
            int originX = origin.getXNum();
            int desX = destination.getXNum();
            if (Math.abs(desX - originX) == 2) {
                isCastling = true;
            }
        }

        boolean wasCapture = destination.isOccupied();
        boolean wasPawnMove = selectedPiece instanceof Pawn;

        boolean success;
        if (isCastling) {
            success = handleCastling((King) selectedPiece, destination);
        } else {
            success = selectedPiece.move(destination);
        }

        if (success) {
            System.out.println("Move successful!");
            resetAllPieceDisplays();

            if (wasCapture || wasPawnMove) {
                movesSinceCapture = 0;
            } else {
                movesSinceCapture++;
            }

            moveHistory.add(getBoardPosition());

            if (selectedPiece instanceof Pawn) {
                Pawn pawn = (Pawn) selectedPiece;
                if (shouldPromote(pawn)) {
                    handlePawnPromotionSync(pawn);
                }
            }

            board.switchTurn();

            checkmateDetector.update();

            checkGameEndingConditions();
        } else {
            System.out.println("Move failed during execution");
        }

        selectedPiece = null;
        return success;
    }

    private void handlePawnPromotionSync(Pawn pawn) {
        Piece newPiece = PromotionHandler.handlePawnPromotion(pawn);
        Square position = pawn.getPosition();

        position.removePiece();
        position.put(newPiece);

        if (pawn.getColor() == 1) {
            board.getWhitePieces().remove(pawn);
            board.getWhitePieces().add(newPiece);
        } else {
            board.getBlackPieces().remove(pawn);
            board.getBlackPieces().add(newPiece);
        }
    }

    private void checkGameEndingConditions() {
        if (checkmateDetector.whiteCheckMated()) {
            board.notifyGameOver(false, "checkmate");
            return;
        }
        if (checkmateDetector.blackCheckMated()) {
            board.notifyGameOver(true, "checkmate");
            return;
        }

        if (isStalemate()) {
            board.notifyGameOver(false, "stalemate");
            return;
        }

        if (isInsufficientMaterial()) {
            board.notifyGameOver(false, "insufficient material");
            return;
        }

        if (movesSinceCapture >= 100) { // 50 moves = 100 half moves
            board.notifyGameOver(false, "50-move rule");
            return;
        }

        if (isThreefoldRepetition()) {
            board.notifyGameOver(false, "threefold repetition");
            return;
        }
    }

    private void resetAllPieceDisplays(){
        Square[][] squares = board.getSquareArray();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (squares[i][j].isOccupied()){
                    squares[i][j].setDisplay(true);
                }
            }
        }
    }

    private boolean handleCastling(King king, Square destination) {
        int kingY = king.getPosition().getYNum();
        int kingX = king.getPosition().getXNum();
        int destX = destination.getXNum();
        Square[][] squares = board.getSquareArray();

        if (destX > kingX) { // King-side castling
            Piece rook = squares[kingY][kingX + 3].getOccupyingPiece();
            if (rook instanceof Rook) {
                if (!king.move(destination)) {
                    return false;
                }
                if (!rook.move(squares[kingY][kingX + 1])) {
                    destination.removePiece();
                    squares[kingY][kingX].put(king);
                    king.setHasMoved(false);
                    return false;
                }
                destination.setDisplay(true);
                squares[kingY][kingX + 1].setDisplay(true);
                return true;
            }
        }
        else { // Queen-side castling
            Piece rook = squares[kingY][kingX - 4].getOccupyingPiece();
            if (rook instanceof Rook) {
                if (!king.move(destination)) {
                    return false;
                }
                if (!rook.move(squares[kingY][kingX - 1])) {
                    destination.removePiece();
                    squares[kingY][kingX].put(king);
                    king.setHasMoved(false);
                    return false;
                }
                destination.setDisplay(true);
                squares[kingY][kingX - 1].setDisplay(true);
                return true;
            }
        }
        return false;
    }

    public Board getBoard(){
        return board;
    }

    public CheckmateDetector getCheckmateDetector() {
        return board.getCheckmateDetector();
    }

    public boolean isGameOver(){
        if (checkmateDetector.blackCheckMated()){
            System.out.println("------------ Winner: WHITE! ------------");
            return true;
        }
        if (checkmateDetector.whiteCheckMated()){
            System.out.println("------------ Winner: BLACK! ------------");
            return true;
        }
        return false;
    }

    public boolean isStalemate() {
        return checkmateDetector.isStalemate(board.getTurn());
    }

    public boolean isDraw() {
        return isStalemate() || isInsufficientMaterial() ||
                movesSinceCapture >= 100 || isThreefoldRepetition();
    }

    public String getDrawReason() {
        if (isStalemate()) return "stalemate";
        if (isInsufficientMaterial()) return "insufficient material";
        if (movesSinceCapture >= 100) return "50-move rule";
        if (isThreefoldRepetition()) return "threefold repetition";
        return "";
    }

    private boolean isInsufficientMaterial() {
        LinkedList<Piece> whitePieces = board.getWhitePieces();
        LinkedList<Piece> blackPieces = board.getBlackPieces();

        int whiteBishops = 0, blackBishops = 0;
        int whiteKnights = 0, blackKnights = 0;
        boolean otherPieces = false;

        for (Piece p : whitePieces) {
            if (p.getPosition() == null) continue;
            if (p instanceof Bishop) whiteBishops++;
            else if (p instanceof Knight) whiteKnights++;
            else if (!(p instanceof King)) otherPieces = true;
        }

        for (Piece p : blackPieces) {
            if (p.getPosition() == null) continue;
            if (p instanceof Bishop) blackBishops++;
            else if (p instanceof Knight) blackKnights++;
            else if (!(p instanceof King)) otherPieces = true;
        }

        if (otherPieces) return false;

        // King vs King
        if (whiteBishops == 0 && whiteKnights == 0 &&
                blackBishops == 0 && blackKnights == 0) return true;

        // King and Bishop vs King
        if (whiteBishops == 1 && whiteKnights == 0 &&
                blackBishops == 0 && blackKnights == 0) return true;
        if (blackBishops == 1 && blackKnights == 0 &&
                whiteBishops == 0 && whiteKnights == 0) return true;

        // King and Knight vs King
        if (whiteKnights == 1 && whiteBishops == 0 &&
                blackKnights == 0 && blackBishops == 0) return true;
        if (blackKnights == 1 && blackBishops == 0 &&
                whiteKnights == 0 && whiteBishops == 0) return true;

        return false;
    }

    private boolean isThreefoldRepetition() {
        if (moveHistory.size() < 9) return false; // Need at least 9 moves for threefold

        String currentPosition = moveHistory.get(moveHistory.size() - 1);
        int count = 0;

        for (String position : moveHistory) {
            if (position.equals(currentPosition)) {
                count++;
                if (count >= 3) return true;
            }
        }

        return false;
    }

    private String getBoardPosition() {
        StringBuilder sb = new StringBuilder();
        Square[][] squares = board.getSquareArray();

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Square sq = squares[y][x];
                if (sq.isOccupied()) {
                    Piece p = sq.getOccupyingPiece();
                    char c = p.getClass().getSimpleName().charAt(0);
                    if (p.getColor() == 1) c = Character.toUpperCase(c);
                    else c = Character.toLowerCase(c);
                    sb.append(c);
                } else {
                    sb.append('.');
                }
            }
        }
        sb.append(board.getTurn() ? 'W' : 'B');

        if (Pawn.getEnPassantTarget() != null) {
            Square epSquare = Pawn.getEnPassantTarget().getPosition();
            if (epSquare != null) {
                sb.append(epSquare.getXNum()).append(epSquare.getYNum());
            }
        }

        return sb.toString();
    }

    public int checkStatus(){
        if (checkmateDetector.blackInCheck()){
            return 0;
        }else  if (checkmateDetector.whiteInCheck()){
            return 1;
        }
        return -1;
    }

    public Piece getSelectedPiece(){
        return selectedPiece;
    }

    public void clearSelectedPiece(){
        selectedPiece = null;
    }

    public Clock getWhiteClock() {
        return whiteClock;
    }

    public Clock getBlackClock() {
        return blackClock;
    }

    public void startTimer(ClockCallback callback){
        synchronized (timerLock) {
            if (whiteClock != null && blackClock != null && timer == null) {
                timer = new Timer(1000, e -> {
                    synchronized (timerLock) {
                        if (timer == null) return; // Timer stopped

                        if (board.getTurn()) {
                            whiteClock.decr();
                            SwingUtilities.invokeLater(() ->
                                    callback.updateWhiteClock(whiteClock.getTime()));

                            if (whiteClock.outOfTime()) {
                                stopTimer();
                                SwingUtilities.invokeLater(() ->
                                        callback.timeOut(true));
                            }
                        } else {
                            blackClock.decr();
                            SwingUtilities.invokeLater(() ->
                                    callback.updateBlackClock(blackClock.getTime()));

                            if (blackClock.outOfTime()) {
                                stopTimer();
                                SwingUtilities.invokeLater(() ->
                                        callback.timeOut(false));
                            }
                        }
                    }
                });
                timer.start();
            }
        }
    }

    public void stopTimer(){
        synchronized (timerLock) {
            if (timer != null) {
                timer.stop();
                timer = null;
            }
        }
    }

    public interface ClockCallback {
        void updateWhiteClock(String time);
        void updateBlackClock(String time);
        void timeOut(boolean whiteTimedOut);
    }

    @Override
    public void onGameOver(boolean whiteWins, String reason) {
        stopTimer();

        SwingUtilities.invokeLater(() -> {
            switch (reason) {
                case "checkmate":
                    gameWindow.checkmateOccurred(whiteWins ? 0 : 1);
                    break;
                case "stalemate":
                    gameWindow.stalemateOccurred();
                    break;
                case "insufficient material":
                case "50-move rule":
                case "threefold repetition":
                    gameWindow.drawOccurred(reason);
                    break;
                default:
                    gameWindow.checkmateOccurred(whiteWins ? 0 : 1);
            }
        });
    }

    @Override
    public void onTurnChanged(boolean isWhiteTurn) {
        if (Pawn.getEnPassantTarget() != null &&
                Pawn.getEnPassantTarget().getColor() == (isWhiteTurn ? 0 : 1)) {
            Pawn.getEnPassantTarget().setJustMovedTwo(false);
            Pawn.clearEnPassantTarget();
        }
    }

    @Override
    public void onPawnPromotion(Pawn pawn) {
//        SwingUtilities.invokeLater(() -> {
//            Piece newPiece = PromotionHandler.handlePawnPromotion(pawn);
//            Square position = pawn.getPosition();
//
//            position.removePiece();
//            position.put(newPiece);
//
//            if (pawn.getColor() == 1) {
//                board.getWhitePieces().remove(pawn);
//                board.getWhitePieces().add(newPiece);
//            } else {
//                board.getBlackPieces().remove(pawn);
//                board.getBlackPieces().add(newPiece);
//            }
//
//            // Update checkmate detector after promotion
//            board.getCheckmateDetector().update();
//
//            // Check if promotion resulted in checkmate
//            checkGameEndingConditions();
//        });
    }

    private boolean shouldPromote(Pawn pawn) {
        if (pawn.getPosition() == null) return false;
        int y = pawn.getPosition().getYNum();
        return (pawn.getColor() == 0 && y == 7) || (pawn.getColor() == 1 && y == 0);
    }
}