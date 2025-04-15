package main.com.ShavguLs.chess.controller;

import main.com.ShavguLs.chess.model.*;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

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

        List<Square> legalMoves = selectedPiece.getLegalMoves(board);
        List<Square> allowableSquares = board.getAllowableSquares();

        if (!legalMoves.contains(destination) || !allowableSquares.contains(destination) ||
        !checkmateDetector.testMove(selectedPiece, destination)){
            return false;
        }

        boolean success = selectedPiece.move(destination);

        if (success){
            board.switchTurn();
            checkmateDetector.update();
        }
        selectedPiece = null;

        return success;
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