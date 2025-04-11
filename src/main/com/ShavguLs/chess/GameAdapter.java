package main.com.ShavguLs.chess;

import main.com.ShavguLs.chess.legacy.Board;
import main.com.ShavguLs.chess.model.CheckmateDetector;
import main.com.ShavguLs.chess.model.King;
import main.com.ShavguLs.chess.model.Piece;

import java.util.LinkedList;

public class GameAdapter {
    private Board modelBoard;
    private main.com.ShavguLs.chess.legacy.Board legacyBoard;
    private CheckmateDetector checkmateDetector;

    public GameAdapter(main.com.ShavguLs.chess.legacy.Board legacyBoard) {
        this.legacyBoard = legacyBoard;
        initializeModelFromLegacy();
    }

    private void initializeModelFromLegacy() {
        this.modelBoard = new Board();

        LinkedList<Piece> whitePieces = new LinkedList<>();
        LinkedList<Piece> blackPieces = new LinkedList<>();
        King whiteKing = null;
        King blackKing = null;

        this.checkmateDetector = new CheckmateDetector(modelBoard, whitePieces, blackPieces, whiteKing, blackKing);
    }

    public boolean makeMove(main.com.ShavguLs.chess.legacy.Piece piece,
                            main.com.ShavguLs.chess.legacy.Square destination) {

        //todo
        return true;
    }

    public Board getModelBoard() {
        return modelBoard;
    }

    public CheckmateDetector getCheckmateDetector() {
        return checkmateDetector;
    }
}