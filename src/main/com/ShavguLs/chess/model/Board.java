package main.com.ShavguLs.chess.model;

import main.com.ShavguLs.chess.view.GameWindow;

import java.util.LinkedList;
import java.util.List;

public class Board {
    private Square[][] board;

    private final LinkedList<Piece> blackPieces;
    private final LinkedList<Piece> whitePieces;

    private boolean whiteTurn;
    private CheckmateDetector checkmateDetector;

    //todo
    private King whiteKing;
    private King blackKing;

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
            Piece blackPawn = new Pawn(0, board[1][x], "/images/bpawn.png");
            Piece whitePawn = new Pawn(1, board[6][x], "/images/wpawn.png");

            board[1][x].put(blackPawn);
            board[6][x].put(whitePawn);
        }

        Piece blackQueen = new Queen(0, board[0][3], "/images/bqueen.png");
        Piece whiteQueen = new Queen(1, board[7][3], "/images/wqueen.png");
        board[0][3].put(blackQueen);
        board[7][3].put(whiteQueen);

        King blackKing = new King(0, board[0][4], "/images/bking.png");
        King whiteKing = new King(1, board[7][4], "/images/wking.png");
        board[0][4].put(blackKing);
        board[7][4].put(whiteKing);

        Piece blackRook1 = new Rook(0, board[0][0], "/images/brook.png");
        Piece blackRook2 = new Rook(0, board[0][7], "/images/brook.png");
        Piece whiteRook1 = new Rook(1, board[7][0], "/images/wrook.png");
        Piece whiteRook2 = new Rook(1, board[7][7], "/images/wrook.png");
        board[0][0].put(blackRook1);
        board[0][7].put(blackRook2);
        board[7][0].put(whiteRook1);
        board[7][7].put(whiteRook2);

        Piece blackKnight1 = new Knight(0, board[0][1], "/images/bknight.png");
        Piece blackKnight2 = new Knight(0, board[0][6], "/images/bknight.png");
        Piece whiteKnight1 = new Knight(1, board[7][1], "/images/wknight.png");
        Piece whiteKnight2 = new Knight(1, board[7][6], "/images/wknight.png");
        board[0][1].put(blackKnight1);
        board[0][6].put(blackKnight2);
        board[7][1].put(whiteKnight1);
        board[7][6].put(whiteKnight2);

        Piece blackBishop1 = new Bishop(0, board[0][2], "/images/bbishop.png");
        Piece blackBishop2 = new Bishop(0, board[0][5], "/images/bbishop.png");
        Piece whiteBishop1 = new Bishop(1, board[7][2], "/images/wbishop.png");
        Piece whiteBishop2 = new Bishop(1, board[7][5], "/images/wbishop.png");
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

    public void setTurn(boolean whiteTurn) {
        this.whiteTurn = whiteTurn;
    }

    public void switchTurn() {
        this.whiteTurn = !this.whiteTurn;
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

    public boolean makeMove(Piece piece, Square destination) {
        if ((piece.getColor() == 1 && !whiteTurn) || (piece.getColor() == 0 && whiteTurn)) {
            return false;
        }

        List<Square> legalMoves = piece.getLegalMoves(this);
        List<Square> allowableSquares = checkmateDetector.getAllowableSquares(whiteTurn);

        if (legalMoves.contains(destination) && allowableSquares.contains(destination) &&
                checkmateDetector.testMove(piece, destination)) {

            piece.move(destination);
            checkmateDetector.update();

            whiteTurn = !whiteTurn;

            return true;
        }
        return false;
    }

    public boolean isBlackCheckmated() {
        return checkmateDetector.blackCheckMated();
    }

    public boolean isWhiteCheckmated() {
        return checkmateDetector.whiteCheckMated();
    }
}