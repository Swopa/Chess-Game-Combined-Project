package main.com.ShavguLs.chess.controller;

import main.com.ShavguLs.chess.model.*;

import java.util.LinkedList;
import java.util.List;

public class GameController {
    private Board board;
    private CheckmateDetector checkmateDetector;
    private Piece selectedPiece = null;

    public GameController(){
        setUpNewGame();
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
        if (!legalMoves.contains(destination)){
            return false;
        }

        boolean success = selectedPiece.move(destination);

        selectedPiece = null;

        checkmateDetector.update();

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
}
