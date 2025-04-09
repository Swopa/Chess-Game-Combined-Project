package main.com.ShavguLs.chess.model;

import java.util.LinkedList;
import java.util.List;

public class ChessModelTest {

    public static void main(String[] args) {
        System.out.println("starting test...");

        try {
            Board board = new Board();

            Square[][] squares = board.getSquareArray();

            System.out.println("inital board state:");
            for (int y = 0; y < 8; y++) {
                for (int x = 0; x < 8; x++) {
                    Piece piece = squares[y][x].getOccupyingPiece();
                    if (piece != null) {
                        System.out.print(" " + piece.getClass().getSimpleName().charAt(0));
                    }
                }
                System.out.println();
            }

            //pawn finder
            Piece pawn = null;
            for (int x = 0; x < 8; x++) {
                if (squares[6][x].getOccupyingPiece() instanceof Pawn) {
                    pawn = squares[6][x].getOccupyingPiece();
                    break;
                }
            }

            if (pawn != null) {
                System.out.println("pawn position: " +
                        pawn.getPosition().getXNum() + "," +
                        pawn.getPosition().getYNum());

                List<Square> legalMoves = pawn.getLegalMoves(board);
                System.out.println("legal moves: " + legalMoves.size());

                for (Square move : legalMoves) {
                    System.out.println("move to: " + move.getXNum() + "," + move.getYNum());
                }

                if (!legalMoves.isEmpty()) {
                    Square destination = legalMoves.get(0);
                    System.out.println("moving pawn to: " + destination.getXNum() + "," + destination.getYNum());

                    boolean moveResult = pawn.move(destination);
                    System.out.println("move result: " + (moveResult ? "success" : "failed"));

                    System.out.println("Current turn after move: " +
                            (board.getTurn() ? "White" : "Black"));
                }

                // kings finder
                King whiteKing = null;
                King blackKing = null;
                LinkedList<Piece> whitePieces = new LinkedList<>();
                LinkedList<Piece> blackPieces = new LinkedList<>();

                for (int y = 0; y < 8; y++) {
                    for (int x = 0; x < 8; x++) {
                        Piece piece = squares[y][x].getOccupyingPiece();
                        if (piece != null) {
                            if (piece.getColor() == 1) {
                                whitePieces.add(piece);
                                if (piece instanceof King) {
                                    whiteKing = (King) piece;
                                }
                            } else {
                                blackPieces.add(piece);
                                if (piece instanceof King) {
                                    blackKing = (King) piece;
                                }
                            }
                        }
                    }
                }

                if (whiteKing != null && blackKing != null) {
                    CheckmateDetector detector = new CheckmateDetector(board, whitePieces, blackPieces, whiteKing, blackKing);

                    System.out.println("check status:");
                    System.out.println("white in check: " + detector.whiteInCheck());
                    System.out.println("black in check: " + detector.blackInCheck());

                    System.out.println("checkmate status:");
                    System.out.println("white checkmated: " + detector.whiteCheckMated());
                    System.out.println("black checkmated: " + detector.blackCheckMated());
                } else {
                    System.out.println("there is no 2 kings on the board!");
                }
            } else {
                System.out.println("there is no pawns!");
            }

        } catch (Exception e) {
            System.out.println("failed with exc: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("chess model test done! congrats");
    }
}