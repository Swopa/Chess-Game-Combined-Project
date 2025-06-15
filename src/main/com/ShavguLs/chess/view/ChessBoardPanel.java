// In: main/com/ShavguLs/chess/view/ChessBoardPanel.java
// Replaces the ENTIRE old file.

package main.com.ShavguLs.chess.view;

import main.com.ShavguLs.chess.controller.GameController;
import main.com.ShavguLs.chess.logic.Piece; // <-- Import YOUR Piece class

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChessBoardPanel extends JPanel {
    private static final int SQUARE_SIZE = 50;

    private final GameController controller;
    private final GameWindow gameWindow;

    // State for visually dragging a piece
    private Piece pieceBeingDragged;
    private int dragX, dragY;

    public ChessBoardPanel(GameWindow gameWindow, GameController controller) {
        this.controller = controller;
        this.gameWindow = gameWindow;
        this.setPreferredSize(new Dimension(SQUARE_SIZE * 8, SQUARE_SIZE * 8));

        MouseHandler mouseHandler = new MouseHandler();
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the checkerboard pattern
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int xPos = col * SQUARE_SIZE;
                int yPos = row * SQUARE_SIZE;

                if ((row + col) % 2 == 0) {
                    g.setColor(new Color(221, 192, 127)); // Light square
                } else {
                    g.setColor(new Color(101, 67, 33)); // Dark square
                }
                g.fillRect(xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);

                // Draw the piece from the logic board
                Piece piece = controller.getLogicBoard().getPieceAt(row, col);
                if (piece != null) {
                    // Don't draw the piece if it's the one we are currently dragging
                    if (piece != pieceBeingDragged) {
                        drawPiece(g, piece, xPos, yPos);
                    }
                }
            }
        }

        // If a piece is being dragged, draw it at the mouse cursor's position
        if (pieceBeingDragged != null) {
            drawPiece(g, pieceBeingDragged, dragX - SQUARE_SIZE / 2, dragY - SQUARE_SIZE / 2);
        }
    }

    private void drawPiece(Graphics g, Piece piece, int x, int y) {
        Image img = ImageManager.getInstance().getPieceImage(piece);
        if (img != null) {
            g.drawImage(img, x, y, SQUARE_SIZE, SQUARE_SIZE, null);
        }
    }

    // Using an inner class for mouse handling to keep the code organized
    private class MouseHandler extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            int col = e.getX() / SQUARE_SIZE;
            int row = e.getY() / SQUARE_SIZE;

            if (controller.selectPiece(row, col)) {
                // If selection is successful, it means we picked up a valid piece.
                // We can now start dragging it.
                pieceBeingDragged = controller.getLogicBoard().getPieceAt(row, col);
                dragX = e.getX();
                dragY = e.getY();
                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (pieceBeingDragged != null) {
                int col = e.getX() / SQUARE_SIZE;
                int row = e.getY() / SQUARE_SIZE;

                // Tell the controller to try and make the move
                controller.makeMove(row, col);

                // Stop dragging, the controller and board will handle the rest
                pieceBeingDragged = null;
                repaint();

                // We can re-add game-over checks here later
                // For example: if (controller.isGameOver()) { ... }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (pieceBeingDragged != null) {
                dragX = e.getX();
                dragY = e.getY();
                repaint();
            }
        }
    }
}