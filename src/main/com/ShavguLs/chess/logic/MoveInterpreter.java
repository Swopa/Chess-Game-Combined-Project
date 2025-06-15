// In: main/com/ShavguLs/chess/logic/MoveInterpreter.java
package main.com.ShavguLs.chess.logic;

import java.util.ArrayList;
import java.util.List;

public class MoveInterpreter {
    private final Board board;
    private boolean whiteToMove = true;

    public MoveInterpreter(Board board) {
        this.board = board;
    }

    // --- We use OUR refactored interpretMove method. It's clean and delegates correctly. ---
    public void interpretMove(String move) throws IllegalMoveException {
        // Handle castling by delegating to the board
        if (move.equals("O-O") || move.equals("O-O-O")) {
            int row = whiteToMove ? 7 : 0;
            if (board.getPieceAt(row, 4) instanceof King) {
                int destCol = move.equals("O-O") ? 6 : 2;
                if (board.attemptMove(row, 4, row, destCol, whiteToMove)) {
                    whiteToMove = !whiteToMove;
                    return;
                }
            }
            throw new IllegalMoveException("Illegal castling move: " + move);
        }

        // --- PGN Parsing Logic ---
        char promotionChar = 0;
        if (move.contains("=")) {
            int index = move.indexOf("=");
            promotionChar = move.charAt(index + 1);
            move = move.substring(0, index);
        }

        char pieceChar = 'P';
        int startIndex = 0;
        if (Character.isUpperCase(move.charAt(0))) {
            pieceChar = move.charAt(0);
            startIndex = 1;
        }

        move = move.replaceAll("[+#x]", "");
        String dest = move.substring(move.length() - 2);
        int destCol = dest.charAt(0) - 'a';
        // --- THE FIX IS HERE: Standardize on the correct coordinate system ---
        int destRow = 7 - (Character.getNumericValue(dest.charAt(1)) - 1);
        String disambiguation = move.substring(startIndex, move.length() - 2);

        // --- We will now call YOUR findSourceSquare method, but the one with fixed coordinates ---
        int[] source = findSourceSquare(pieceChar, destRow, destCol, disambiguation);

        if (source == null) {
            throw new IllegalMoveException("Illegal or ambiguous move: Could not find source for " + move);
        }

        int srcRow = source[0];
        int srcCol = source[1];

        if (board.attemptMove(srcRow, srcCol, destRow, destCol, whiteToMove)) {
            // Handle promotion after the move is confirmed
            if (promotionChar != 0 && board.getPieceAt(destRow, destCol) instanceof Pawn) {
                Piece promoted = switch (promotionChar) {
                    case 'Q' -> new Queen(whiteToMove);
                    case 'R' -> new Rook(whiteToMove);
                    case 'N' -> new Knight(whiteToMove);
                    case 'B' -> new Bishop(whiteToMove);
                    default -> null;
                };
                if (promoted != null) {
                    board.setPiece(destRow, destCol, promoted);
                }
            }
            whiteToMove = !whiteToMove;
        } else {
            throw new IllegalMoveException("Illegal move (rejected by Board): " + move);
        }
    }

    // --- This is YOUR findSourceSquare method, but with fixed coordinate math ---
    int[] findSourceSquare(char pieceChar, int destRow, int destCol, String disambiguation) {
        System.out.printf("Looking for move to (%d, %d) = %c%d with disambiguation [%s]\n",
                destRow, destCol, (char) (destCol + 'a'), 8 - destRow, disambiguation);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(row, col);
                if (piece == null || piece.isWhite() != whiteToMove || !matchPieceType(piece, pieceChar)) {
                    continue;
                }
                if (!piece.isValidMove(row, col, destRow, destCol, board.getBoardArray())) {
                    continue;
                }

                // --- YOUR DISAMBIGUATION LOGIC, BUT WITH CORRECTED COORDINATES ---
                if (!disambiguation.isEmpty()) {
                    if (disambiguation.length() == 2) {
                        char fileChar = disambiguation.charAt(0);
                        char rankChar = disambiguation.charAt(1);
                        int rankRow = 7 - (Character.getNumericValue(rankChar) - 1);
                        int fileCol = fileChar - 'a';
                        if (row != rankRow || col != fileCol) continue;
                    } else if (Character.isDigit(disambiguation.charAt(0))) {
                        char rankChar = disambiguation.charAt(0);
                        int rankRow = 7 - (Character.getNumericValue(rankChar) - 1);
                        if (row != rankRow) continue;
                    } else if (Character.isLetter(disambiguation.charAt(0))) {
                        char fileChar = disambiguation.charAt(0);
                        int fileCol = fileChar - 'a';
                        if (col != fileCol) continue;
                    }
                }

                // --- IMPORTANT ADDITION: Check if move leaves king in check ---
                Board simulated = board.copy();
                simulated.movePiece(row, col, destRow, destCol);
                if (simulated.isKingInCheck(whiteToMove)) {
                    continue; // This move is not a legal candidate
                }

                return new int[]{row, col};
            }
        }
        return null; // No legal source found
    }

    private boolean matchPieceType(Piece piece, char pieceChar) {
        return switch (pieceChar) {
            case 'N' -> piece instanceof Knight;
            case 'B' -> piece instanceof Bishop;
            case 'R' -> piece instanceof Rook;
            case 'Q' -> piece instanceof Queen;
            case 'K' -> piece instanceof King;
            case 'P' -> piece instanceof Pawn;
            default -> false;
        };
    }
}