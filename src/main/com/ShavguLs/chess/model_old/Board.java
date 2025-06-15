package main.com.ShavguLs.chess.model;

import java.util.*;

/* Represents the chess board with all the squares and pieces.
Keeps lists of white and black pieces and knows whose turn it is. */

public class Board {
    private List<GameObserver> observers = new ArrayList<>();
    private Square[][] board;

    private final LinkedList<Piece> blackPieces;
    private final LinkedList<Piece> whitePieces;

    private boolean whiteTurn;
    private CheckmateDetector checkmateDetector;

    private King whiteKing;
    private King blackKing;

    private int halfMoveClock = 0;
    private Map<String, Integer> positionHistory = new HashMap<>();

    public Board() {
        board = new Square[8][8];
        blackPieces = new LinkedList<>();
        whitePieces = new LinkedList<>();

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int xMod = x % 2;
                int yMod = y % 2;

                if ((xMod == 0 && yMod == 0) || (xMod == 1 && yMod == 1)) {
                    board[x][y] = new Square(1, y, x, this);
                } else {
                    board[x][y] = new Square(0, y, x, this);
                }
            }
        }

        initializePieces();
        whiteTurn = true;
    }

    private void initializePieces() {
        for (int x = 0; x < 8; x++) {
            Piece blackPawn = new Pawn(0, board[1][x]);
            Piece whitePawn = new Pawn(1, board[6][x]);

            board[1][x].put(blackPawn);
            board[6][x].put(whitePawn);
        }

        Piece blackQueen = new Queen(0, board[0][3]);
        Piece whiteQueen = new Queen(1, board[7][3]);
        board[0][3].put(blackQueen);
        board[7][3].put(whiteQueen);

        blackKing = new King(0, board[0][4]);
        whiteKing = new King(1, board[7][4]);
        board[0][4].put(blackKing);
        board[7][4].put(whiteKing);

        Piece blackRook1 = new Rook(0, board[0][0]);
        Piece blackRook2 = new Rook(0, board[0][7]);
        Piece whiteRook1 = new Rook(1, board[7][0]);
        Piece whiteRook2 = new Rook(1, board[7][7]);
        board[0][0].put(blackRook1);
        board[0][7].put(blackRook2);
        board[7][0].put(whiteRook1);
        board[7][7].put(whiteRook2);

        Piece blackKnight1 = new Knight(0, board[0][1]);
        Piece blackKnight2 = new Knight(0, board[0][6]);
        Piece whiteKnight1 = new Knight(1, board[7][1]);
        Piece whiteKnight2 = new Knight(1, board[7][6]);
        board[0][1].put(blackKnight1);
        board[0][6].put(blackKnight2);
        board[7][1].put(whiteKnight1);
        board[7][6].put(whiteKnight2);

        Piece blackBishop1 = new Bishop(0, board[0][2]);
        Piece blackBishop2 = new Bishop(0, board[0][5]);
        Piece whiteBishop1 = new Bishop(1, board[7][2]);
        Piece whiteBishop2 = new Bishop(1, board[7][5]);
        board[0][2].put(blackBishop1);
        board[0][5].put(blackBishop2);
        board[7][2].put(whiteBishop1);
        board[7][5].put(whiteBishop2);

        for(int y = 0; y < 2; y++) {
            for (int x = 0; x < 8; x++) {
                blackPieces.add(board[y][x].getOccupyingPiece());
                whitePieces.add(board[7-y][x].getOccupyingPiece());
            }
        }

        checkmateDetector = new CheckmateDetector(this, whitePieces, blackPieces, whiteKing, blackKing);
    }

    public Square[][] getSquareArray() {
        return this.board;
    }

    public boolean getTurn() {
        return whiteTurn;
    }

    public void switchTurn() {
        this.whiteTurn = !this.whiteTurn;
        notifyTurnChanged();
    }

    public LinkedList<Piece> getWhitePieces() {
        return whitePieces;
    }

    public LinkedList<Piece> getBlackPieces() {
        return blackPieces;
    }

    public List<Square> getAllowableSquares() {
        return checkmateDetector.getAllowableSquares(whiteTurn);
    }

    public CheckmateDetector getCheckmateDetector() {
        return checkmateDetector;
    }

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    public void notifyGameOver(boolean whiteWins, String reason) {
        for (GameObserver observer : observers) {
            observer.onGameOver(whiteWins, reason);
        }
    }

    public void notifyTurnChanged() {
        for (GameObserver observer : observers) {
            observer.onTurnChanged(whiteTurn);
        }
    }

    public void notifyPawnPromotion(Pawn pawn) {
        for (GameObserver observer : observers) {
            observer.onPawnPromotion(pawn);
        }
    }
}