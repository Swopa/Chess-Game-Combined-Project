package main.com.ShavguLs.chess.controller;

import main.com.ShavguLs.chess.model.*;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

/* Controls how the game works. Keeps track of the board, whose turn it is, and handles when players move pieces.
Also manages the clock and checks if someone won. */

public class GameController {
    private Board board;
    private CheckmateDetector checkmateDetector;
    private Piece selectedPiece = null;
    private Clock whiteClock;
    private Clock blackClock;
    private Timer timer;

    public GameController(int hh, int mm, int ss){
        setUpNewGame();
        setupClock(hh, mm, ss);
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
    }

    private void resetClocks() {
        if (timer!=null){
            timer.stop();
        }
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

    public boolean makeMove(int row, int col){
        if (selectedPiece == null){
            return false;
        }

        if (row < 0 || row >= 8 || col < 0 || col >= 8){
            return false;
        }

        Square destination = board.getSquareArray()[row][col];
        Square origin = selectedPiece.getPosition();

        List<Square> legalMoves = selectedPiece.getLegalMoves(board);
        List<Square> allowableSquares = board.getAllowableSquares();

        if (!legalMoves.contains(destination) || !allowableSquares.contains(destination) ||
        !checkmateDetector.testMove(selectedPiece, destination)){
            return false;
        }

        boolean isCastling = false;
        if (selectedPiece instanceof King){
            int originX = origin.getXNum();
            int desX = destination.getXNum();
            if (Math.abs(desX - originX) == 2){
                isCastling = true;
            }
        }

        boolean success;

        if (isCastling){
            success = handleCastling((King) selectedPiece, destination);
        }else {
            success = selectedPiece.move(destination);
        }

        if (success){
            resetAllPieceDisplays();
            if (selectedPiece instanceof Pawn){
                Pawn pawn = (Pawn) selectedPiece;
                if (pawn.canPromote()){
                    promotePawn(pawn);
                }
            }
            board.switchTurn();
            checkmateDetector.update();
        }
        selectedPiece = null;

        return success;
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

        if (destX > kingX) {
            Piece rook = squares[kingY][kingX + 3].getOccupyingPiece();
            if (rook instanceof Rook) {
                if (!king.move(destination)) {
                    return false;
                }
                if (!rook.move(squares[kingY][kingX + 1])) {
                    king.setPosition(squares[kingY][kingX]);
                    squares[kingY][kingX].put(king);
                    destination.removePiece();
                    return false;
                }
                destination.setDisplay(true);
                squares[kingY][kingX + 1].setDisplay(true);
                return true;
            }
        }
        else {
            Piece rook = squares[kingY][kingX - 4].getOccupyingPiece();
            if (rook instanceof Rook) {
                if (!king.move(destination)) {
                    return false;
                }
                if (!rook.move(squares[kingY][kingX - 1])) {
                    king.setPosition(squares[kingY][kingX]);
                    squares[kingY][kingX].put(king);
                    destination.removePiece();
                    return false;
                }
                destination.setDisplay(true);
                squares[kingY][kingX - 1].setDisplay(true);
                return true;
            }
        }
        return false;
    }

    private void promotePawn(Pawn pawn) {
        int color = pawn.getColor();
        Square position = pawn.getPosition();

        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        int choice = JOptionPane.showOptionDialog(null,
                "Choose promotion piece",
                "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        Piece newPiece = null;
        String imgPrefix = (color == 1) ? "/images/w" : "/images/b";

        switch (choice) {
            case 0: // Queen
                newPiece = new Queen(color, position, imgPrefix + "q.png");
                break;
            case 1: // Rook
                newPiece = new Rook(color, position, imgPrefix + "r.png");
                break;
            case 2: // Bishop
                newPiece = new Bishop(color, position, imgPrefix + "b.png");
                break;
            case 3: // Knight
                newPiece = new Knight(color, position, imgPrefix + "k.png");
                break;
            default:
                newPiece = new Queen(color, position, imgPrefix + "q.png");
        }
        position.removePiece();
        position.put(newPiece);

        if (color == 1) {
            board.getWhitePieces().remove(pawn);
            board.getWhitePieces().add(newPiece);
        } else {
            board.getBlackPieces().remove(pawn);
            board.getBlackPieces().add(newPiece);
        }
    }

    public Board getBoard(){
        return board;
    }

    public CheckmateDetector getCheckmateDetector() {
        return checkmateDetector;
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

    //todo
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
        if (whiteClock != null && blackClock != null){
            timer = new Timer(1000, e -> {
                if (board.getTurn()){
                    whiteClock.decr();
                    callback.updateWhiteClock(whiteClock.getTime());

                    if (whiteClock.outOfTime()){
                        timer.stop();
                        callback.timeOut(true);
                    }
                }else{
                    blackClock.decr();
                    callback.updateBlackClock(blackClock.getTime());

                    if (blackClock.outOfTime()){
                        timer.stop();
                        callback.timeOut(false);
                    }
                }
            });
            timer.start();
        }
    }

    public void stopTimer(){
        if (timer != null){
            timer.stop();
        }
    }

    public interface ClockCallback {
        void updateWhiteClock(String time);
        void updateBlackClock(String time);
        void timeOut(boolean whiteTimedOut);
    }
}