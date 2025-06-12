package main.com.ShavguLs.chess.view;

import main.com.ShavguLs.chess.controller.GameController;
import main.com.ShavguLs.chess.model.*;
import main.com.ShavguLs.chess.view.ImageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Draws the chess board and handles mouse actions like dragging pieces.

public class ChessBoardPanel extends JPanel implements MouseListener, MouseMotionListener {
    private static final int SQUARE_SIZE = 50;

    private final GameController controller;
    private final GameWindow gameWindow;

    private Piece currentPiece;
    private int currentX;
    private int currentY;
    private Square originalSquare;

    public ChessBoardPanel(GameWindow gameWindow, GameController controller) {
        this.controller = controller;
        this.gameWindow = gameWindow;

        setLayout(null);

        addMouseListener(this);
        addMouseMotionListener(this);

        setPreferredSize(new Dimension(400, 400));
        setMaximumSize(new Dimension(400, 400));
        setMinimumSize(getPreferredSize());
        setSize(new Dimension(400, 400));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Square[][] squares = controller.getBoard().getSquareArray();

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int xPos = x * SQUARE_SIZE;
                int yPos = y * SQUARE_SIZE;

                if ((x + y) % 2 == 0) {
                    g.setColor(new Color(221, 192, 127));
                } else {
                    g.setColor(new Color(101, 67, 33));
                }
                g.fillRect(xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);

                Square square = squares[y][x];
                if (square.isOccupied() && square.getDisplay()) {
                    Piece piece = square.getOccupyingPiece();
                    if (piece != currentPiece) {
                        Image img = ImageManager.getInstance().getPieceImage(piece);
                        if (img != null) {
                            g.drawImage(img, xPos, yPos, SQUARE_SIZE, SQUARE_SIZE, null);
                        }
                    }
                }
            }
        }

        if (currentPiece != null) {
            if ((currentPiece.getColor() == 1 && controller.getBoard().getTurn()) ||
                    (currentPiece.getColor() == 0 && !controller.getBoard().getTurn())) {
                Image img = ImageManager.getInstance().getPieceImage(currentPiece);
                if (img != null) {
                    g.drawImage(img, currentX - SQUARE_SIZE/2, currentY - SQUARE_SIZE/2,
                            SQUARE_SIZE, SQUARE_SIZE, null);
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        currentX = e.getX();
        currentY = e.getY();

        int col = currentX / SQUARE_SIZE;
        int row = currentY / SQUARE_SIZE;

        if (col >= 0 && col < 8 && row >= 0 && row < 8) {
            if (controller.selectPiece(row, col)) {
                Square square = controller.getBoard().getSquareArray()[row][col];
                currentPiece = square.getOccupyingPiece();
                originalSquare = square; // FIXED
                square.setDisplay(false);
            }
        }
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (currentPiece == null) {
            return;
        }

        int col = e.getX() / SQUARE_SIZE;
        int row = e.getY() / SQUARE_SIZE;

        boolean moveSuccessful = false;

        if (col >= 0 && col < 8 && row >= 0 && row < 8) {
            moveSuccessful = controller.makeMove(row, col);
        }

        if (originalSquare != null) {
            originalSquare.setDisplay(true);
        }

        currentPiece = null;
        originalSquare = null;
        repaint();

        if (moveSuccessful) {
            SwingUtilities.invokeLater(() -> {
                if (controller.isGameOver()) {
                    boolean whiteWin = controller.getCheckmateDetector().blackCheckMated();
                    gameWindow.checkmateOccurred(whiteWin ? 0 : 1);
                } else if (controller.isStalemate()) {
                    gameWindow.stalemateOccurred();
                } else if (controller.isDraw()) {
                    gameWindow.drawOccurred(controller.getDrawReason());
                }
            });
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (currentPiece != null) {
            currentX = e.getX();
            currentY = e.getY();
            repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}
}