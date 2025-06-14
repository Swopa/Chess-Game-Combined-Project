package main.com.ShavguLs.chess.model_old;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class CheckmateDetector {
    private Board board;
    private LinkedList<Piece> whitePieces;
    private LinkedList<Piece> blackPieces;
    private LinkedList<Square> movableSquares;
    private final LinkedList<Square> squares;
    private King blackKing;
    private King whiteKing;
    private HashMap<Square, List<Piece>> whiteMoves;
    private HashMap<Square, List<Piece>> blackMoves;

    public CheckmateDetector(Board board, LinkedList<Piece> whitePieces,
                             LinkedList<Piece> blackPieces, King whiteKing, King blackKing){
        this.board = board;
        this.whitePieces = whitePieces;
        this.blackPieces = blackPieces;
        this.blackKing = blackKing;
        this.whiteKing = whiteKing;

        // Initialize other fields
        squares = new LinkedList<Square>();
        movableSquares = new LinkedList<Square>();
        whiteMoves = new HashMap<Square,List<Piece>>();
        blackMoves = new HashMap<Square,List<Piece>>();

        Square[][] brd = board.getSquareArray();

        // add all squares to squares list and as hashmap keys
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                squares.add(brd[y][x]);
                whiteMoves.put(brd[y][x], new LinkedList<Piece>());
                blackMoves.put(brd[y][x], new LinkedList<Piece>());
            }
        }

        // update situation
        update();
    }

    public void update() {
        // Clear all move maps
        for (List<Piece> pieces : whiteMoves.values()) {
            pieces.clear();
        }
        for (List<Piece> pieces : blackMoves.values()) {
            pieces.clear();
        }
        movableSquares.clear();

        // Clean up piece lists - remove pieces that are no longer on board
        cleanupPieceLists();

        // Rebuild move maps for white pieces
        for (Piece p : whitePieces) {
            if (p.getPosition() != null && !(p instanceof King)) {
                try {
                    List<Square> moves = p.getLegalMoves(board);
                    for (Square sq : moves) {
                        if (sq != null && whiteMoves.containsKey(sq)) {
                            whiteMoves.get(sq).add(p);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error getting legal moves for white piece: " + e.getMessage());
                }
            }
        }

        // Rebuild move maps for black pieces
        for (Piece p : blackPieces) {
            if (p.getPosition() != null && !(p instanceof King)) {
                try {
                    List<Square> moves = p.getLegalMoves(board);
                    for (Square sq : moves) {
                        if (sq != null && blackMoves.containsKey(sq)) {
                            blackMoves.get(sq).add(p);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error getting legal moves for black piece: " + e.getMessage());
                }
            }
        }

        // Ensure all occupied squares display their pieces
        Square[][] boardArray = board.getSquareArray();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (boardArray[i][j].isOccupied()) {
                    boardArray[i][j].setDisplay(true);
                }
            }
        }
    }

    private void cleanupPieceLists() {
        try {
            Square[][] boardArray = board.getSquareArray();

            // More conservative cleanup - only remove pieces that are definitely not on the board
            whitePieces.removeIf(piece -> {
                if (piece.getPosition() == null) return true;
                try {
                    int x = piece.getPosition().getXNum();
                    int y = piece.getPosition().getYNum();
                    if (x < 0 || x >= 8 || y < 0 || y >= 8) return true;
                    Square square = boardArray[y][x];
                    return !square.isOccupied() || square.getOccupyingPiece() != piece;
                } catch (Exception e) {
                    return true; // Remove if we can't verify
                }
            });

            blackPieces.removeIf(piece -> {
                if (piece.getPosition() == null) return true;
                try {
                    int x = piece.getPosition().getXNum();
                    int y = piece.getPosition().getYNum();
                    if (x < 0 || x >= 8 || y < 0 || y >= 8) return true;
                    Square square = boardArray[y][x];
                    return !square.isOccupied() || square.getOccupyingPiece() != piece;
                } catch (Exception e) {
                    return true; // Remove if we can't verify
                }
            });
        } catch (Exception e) {
            System.err.println("Error in cleanupPieceLists: " + e.getMessage());
        }
    }

    public boolean  blackInCheck() {
        update();
        Square sq = blackKing.getPosition();
        if (sq == null) return false;
        List<Piece> threats = whiteMoves.get(sq);
        return threats != null && !threats.isEmpty();
    }

    public boolean whiteInCheck() {
        update();
        Square sq = whiteKing.getPosition();
        if (sq == null) return false;
        List<Piece> threats = blackMoves.get(sq);
        return threats != null && !threats.isEmpty();
    }

    public boolean blackCheckMated() {
        boolean checkmate = true;
        // Check if black is in check
        if (!this.blackInCheck()) return false;

        // If yes, check if king can evade
        if (canEvade(whiteMoves, blackKing)) checkmate = false;

        // If no, check if threat can be captured
        List<Piece> threats = whiteMoves.get(blackKing.getPosition());
        if (canCapture(blackMoves, threats, blackKing)) checkmate = false;

        // If no, check if threat can be blocked
        if (canBlock(threats, blackMoves, blackKing)) checkmate = false;

        // If no possible ways of removing check, checkmate occurred
        return checkmate;
    }

    public boolean whiteCheckMated() {
        boolean checkmate = true;
        // Check if white is in check
        if (!this.whiteInCheck()) return false;

        // If yes, check if king can evade
        if (canEvade(blackMoves, whiteKing)) checkmate = false;

        // If no, check if threat can be captured
        List<Piece> threats = blackMoves.get(whiteKing.getPosition());
        if (canCapture(whiteMoves, threats, whiteKing)) checkmate = false;

        // If no, check if threat can be blocked
        if (canBlock(threats, whiteMoves, whiteKing)) checkmate = false;

        // If no possible ways of removing check, checkmate occurred
        return checkmate;
    }

    // Check for stalemate
    public boolean isStalemate(boolean isWhiteTurn) {
        // If in check, it's not stalemate
        if (isWhiteTurn && whiteInCheck()) return false;
        if (!isWhiteTurn && blackInCheck()) return false;

        // Check if any piece has legal moves
        LinkedList<Piece> pieces = isWhiteTurn ? whitePieces : blackPieces;

        for (Piece piece : pieces) {
            if (piece.getPosition() == null) continue;

            List<Square> moves = piece.getLegalMoves(board);
            for (Square move : moves) {
                if (move != null && testMove(piece, move)) {
                    return false; // Found a legal move
                }
            }
        }

        // Also check king moves
        King king = isWhiteTurn ? whiteKing : blackKing;
        List<Square> kingMoves = king.getLegalMoves(board);
        for (Square move : kingMoves) {
            if (move != null && testMove(king, move)) {
                return false;
            }
        }

        // No legal moves and not in check = stalemate
        return true;
    }

    private boolean canEvade(Map<Square,List<Piece>> tMoves, King tKing) {
        boolean evade = false;
        List<Square> kingsMoves = tKing.getLegalMoves(board);
        Iterator<Square> iterator = kingsMoves.iterator();

        // If king is not threatened at some square, it can evade
        while (iterator.hasNext()) {
            Square sq = iterator.next();
            if (!testMove(tKing, sq)) continue;
            List<Piece> threats = tMoves.get(sq);
            if (threats == null || threats.isEmpty()) {
                movableSquares.add(sq);
                evade = true;
            }
        }

        return evade;
    }

    private boolean canCapture(Map<Square,List<Piece>> poss,
                               List<Piece> threats, King k) {

        boolean capture = false;
        if (threats != null && threats.size() == 1) {
            Piece threat = threats.get(0);
            if (threat == null || threat.getPosition() == null) return false;

            Square sq = threat.getPosition();

            if (k.getLegalMoves(board).contains(sq)) {
                movableSquares.add(sq);
                if (testMove(k, sq)) {
                    capture = true;
                }
            }

            List<Piece> caps = poss.get(sq);
            ConcurrentLinkedDeque<Piece> capturers = new ConcurrentLinkedDeque<Piece>();
            if (caps != null) {
                capturers.addAll(caps);
            }

            if (!capturers.isEmpty()) {
                movableSquares.add(sq);
                for (Piece p : capturers) {
                    if (p != null && testMove(p, sq)) {
                        capture = true;
                    }
                }
            }
        }

        return capture;
    }

    private boolean canBlock(List<Piece> threats, Map <Square,List<Piece>> blockMoves, King k) {
        boolean blockable = false;

        if (threats == null || threats.size() != 1) return false;

        Piece threat = threats.get(0);
        if (threat == null || threat.getPosition() == null || k.getPosition() == null) {
            return false;
        }

        Square ts = threat.getPosition();
        Square ks = k.getPosition();
        Square[][] brdArray = board.getSquareArray();

        if (ks.getXNum() == ts.getXNum()) {
            int max = Math.max(ks.getYNum(), ts.getYNum());
            int min = Math.min(ks.getYNum(), ts.getYNum());

            for (int i = min + 1; i < max; i++) {
                List<Piece> blks = blockMoves.get(brdArray[i][ks.getXNum()]);
                ConcurrentLinkedDeque<Piece> blockers = new ConcurrentLinkedDeque<Piece>();
                if (blks != null) {
                    blockers.addAll(blks);
                }

                if (!blockers.isEmpty()) {
                    movableSquares.add(brdArray[i][ks.getXNum()]);

                    for (Piece p : blockers) {
                        if (p != null && testMove(p,brdArray[i][ks.getXNum()])) {
                            blockable = true;
                        }
                    }
                }
            }
        }

        if (ks.getYNum() == ts.getYNum()) {
            int max = Math.max(ks.getXNum(), ts.getXNum());
            int min = Math.min(ks.getXNum(), ts.getXNum());

            for (int i = min + 1; i < max; i++) {
                List<Piece> blks = blockMoves.get(brdArray[ks.getYNum()][i]);
                ConcurrentLinkedDeque<Piece> blockers = new ConcurrentLinkedDeque<Piece>();
                if (blks != null) {
                    blockers.addAll(blks);
                }

                if (!blockers.isEmpty()) {
                    movableSquares.add(brdArray[ks.getYNum()][i]);

                    for (Piece p : blockers) {
                        if (p != null && testMove(p, brdArray[ks.getYNum()][i])) {
                            blockable = true;
                        }
                    }
                }
            }
        }

        Class<? extends Piece> tC = threat.getClass();

        if (tC.equals(Queen.class) || tC.equals(Bishop.class)) {
            int kX = ks.getXNum();
            int kY = ks.getYNum();
            int tX = ts.getXNum();
            int tY = ts.getYNum();

            if (kX > tX && kY > tY) {
                int currentY = tY + 1;
                for (int i = tX + 1; i < kX; i++) {
                    if (currentY < kY) {
                        List<Piece> blks = blockMoves.get(brdArray[currentY][i]);
                        ConcurrentLinkedDeque<Piece> blockers = new ConcurrentLinkedDeque<Piece>();
                        if (blks != null) blockers.addAll(blks);
                        if (!blockers.isEmpty()) {
                            movableSquares.add(brdArray[currentY][i]);
                            for (Piece p : blockers) {
                                if (p != null && testMove(p, brdArray[currentY][i])) {
                                    blockable = true;
                                }
                            }
                        }
                        currentY++;
                    }
                }
            }

            if (kX > tX && tY > kY) {
                int currentY = tY - 1;
                for (int i = tX + 1; i < kX; i++) {
                    if (currentY > kY) {
                        List<Piece> blks = blockMoves.get(brdArray[currentY][i]);
                        ConcurrentLinkedDeque<Piece> blockers = new ConcurrentLinkedDeque<Piece>();
                        if (blks != null) blockers.addAll(blks);

                        if (!blockers.isEmpty()) {
                            movableSquares.add(brdArray[currentY][i]);

                            for (Piece p : blockers) {
                                if (p != null && testMove(p, brdArray[currentY][i])) {
                                    blockable = true;
                                }
                            }
                        }
                        currentY--;
                    }
                }
            }

            if (tX > kX && kY > tY) {
                int currentY = tY + 1;
                for (int i = tX - 1; i > kX; i--) {
                    if (currentY < kY) {
                        List<Piece> blks = blockMoves.get(brdArray[currentY][i]);
                        ConcurrentLinkedDeque<Piece> blockers = new ConcurrentLinkedDeque<Piece>();
                        if (blks != null) blockers.addAll(blks);

                        if (!blockers.isEmpty()) {
                            movableSquares.add(brdArray[currentY][i]);

                            for (Piece p : blockers) {
                                if (p != null && testMove(p, brdArray[currentY][i])) {
                                    blockable = true;
                                }
                            }
                        }
                        currentY++;
                    }
                }
            }

            if (tX > kX && tY > kY) {
                int currentY = tY - 1;
                for (int i = tX - 1; i > kX; i--) {
                    if (currentY > kY) {
                        List<Piece> blks = blockMoves.get(brdArray[currentY][i]);
                        ConcurrentLinkedDeque<Piece> blockers = new ConcurrentLinkedDeque<Piece>();
                        if (blks != null) blockers.addAll(blks);
                        if (!blockers.isEmpty()) {
                            movableSquares.add(brdArray[currentY][i]);

                            for (Piece p : blockers) {
                                if (p != null && testMove(p, brdArray[currentY][i])) {
                                    blockable = true;
                                }
                            }
                        }
                        currentY--;
                    }
                }
            }
        }
        return blockable;
    }

    public List<Square> getAllowableSquares(boolean b) {
        movableSquares.clear();
        if (b) { // Whites turn
            if (whiteInCheck()) {
                whiteCheckMated();
            } else {
                movableSquares.addAll(squares);
            }
        } else { // Blacks turn
            if (blackInCheck()) {
                blackCheckMated();
            } else {
                movableSquares.addAll(squares);
            }
        }
        return movableSquares;
    }

    public boolean testMove(Piece p, Square destination) {
        if (p == null || destination == null || p.getPosition() == null) {
            return false;
        }

        // Save current state
        Square originalSquare = p.getPosition();
        Piece capturedPiece = destination.getOccupyingPiece();

        // Save special piece states
        boolean kingHadMoved = false;
        boolean rookHadMoved = false;
        boolean pawnHadMoved = false;

        if (p instanceof King) {
            kingHadMoved = ((King) p).hasMoved();
        } else if (p instanceof Rook) {
            rookHadMoved = ((Rook) p).hasMoved();
        } else if (p instanceof Pawn) {
            pawnHadMoved = ((Pawn) p).hasMovedBefore();
        }

        // Make the move temporarily
        originalSquare.removePiece();
        if (capturedPiece != null) {
            destination.removePiece();
            // Remove captured piece from piece lists temporarily
            if (capturedPiece.getColor() == 0) {
                blackPieces.remove(capturedPiece);
            } else {
                whitePieces.remove(capturedPiece);
            }
        }
        destination.put(p);

        // Check if the king of the moving piece's color is in check
        boolean moveIsValid = true;
        King kingToCheck = (p.getColor() == 1) ? whiteKing : blackKing;

        if (kingToCheck != null && kingToCheck.getPosition() != null) {
            moveIsValid = !isKingInCheckAfterMove(kingToCheck);
        }

        // Restore the move
        destination.removePiece();
        originalSquare.put(p);
        if (capturedPiece != null) {
            destination.put(capturedPiece);
            // Restore captured piece to piece lists
            if (capturedPiece.getColor() == 0) {
                if (!blackPieces.contains(capturedPiece)) {
                    blackPieces.add(capturedPiece);
                }
            } else {
                if (!whitePieces.contains(capturedPiece)) {
                    whitePieces.add(capturedPiece);
                }
            }
        }

        // Restore special piece states
        if (p instanceof King) {
            ((King) p).setHasMoved(kingHadMoved);
        } else if (p instanceof Rook) {
            ((Rook) p).setHasMoved(rookHadMoved);
        } else if (p instanceof Pawn) {
            ((Pawn) p).setWasMoved(pawnHadMoved);
        }

        return moveIsValid;
    }

    private boolean isKingInCheckAfterMove(King king) {
        Square kingSquare = king.getPosition();
        if (kingSquare == null) return false;

        // Check if any opponent piece can attack the king's position
        LinkedList<Piece> opponentPieces = (king.getColor() == 1) ? blackPieces : whitePieces;

        for (Piece piece : opponentPieces) {
            if (piece.getPosition() != null) {
                List<Square> attackSquares = piece.getLegalMoves(board);
                if (attackSquares.contains(kingSquare)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isSquareAttacked(Square square, int color){
        if (square == null) return false;

        if (color == 1){
            List<Piece> attackers = blackMoves.get(square);
            return attackers != null && !attackers.isEmpty();
        }else {
            List<Piece> attackers = whiteMoves.get(square);
            return attackers != null && !attackers.isEmpty();
        }
    }

    public boolean kingInCheck(int color){
        if (color == 1){
            return whiteInCheck();
        }else {
            return blackInCheck();
        }
    }
}