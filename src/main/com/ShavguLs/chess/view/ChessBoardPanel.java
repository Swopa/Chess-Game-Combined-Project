package main.com.ShavguLs.chess.view;

import main.com.ShavguLs.chess.controller.GameController;
import main.com.ShavguLs.chess.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChessBoardPanel extends JPanel implements MouseListener, MouseMotionListener {
    private static final int SQUARE_SIZE = 50; // Same size as legacy

    private final GameController controller;
    private final GameWindow gameWindow;

    private Piece currentPiece;
    private int currentX;
    private int currentY;

    public ChessBoardPanel(GameWindow gameWindow, GameController controller) {
        this.controller = controller;
        this.gameWindow = gameWindow;

        setLayout(new GridLayout(8, 8, 0, 0));

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
                    g.setColor(new Color(221, 192, 127)); // Light square
                } else {
                    g.setColor(new Color(101, 67, 33)); // Dark square
                }
                g.fillRect(xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);

                Square square = squares[y][x];
                if (square.isOccupied() && square.getDisplay()) {
                    Piece piece = square.getOccupyingPiece();
                    if (piece != currentPiece) {
                        Image img = piece.getImage();
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
                Image img = currentPiece.getImage();
                g.drawImage(img, currentX, currentY, null);
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
                square.setDisplay(false);
            }
        }

        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int col = e.getX() / SQUARE_SIZE;
        int row = e.getY() / SQUARE_SIZE;
        if (currentPiece != null) {
            if (col >= 0 && col < 8 && row >= 0 && row < 8) {
                boolean moved = controller.makeMove(row, col);

                if (moved) {
                    if (controller.isGameOver()) {
                        boolean whiteWin = controller.getCheckmateDetector().blackCheckMated();
                        gameWindow.checkmateOccurred(whiteWin ? 0 : 1);
                    }
                } else {
                    currentPiece.getPosition().setDisplay(true);
                }
            } else {
                currentPiece.getPosition().setDisplay(true);
            }
            currentPiece = null;
        }
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (currentPiece != null) {
            currentX = e.getX() - (SQUARE_SIZE / 2);
            currentY = e.getY() - (SQUARE_SIZE / 2);
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