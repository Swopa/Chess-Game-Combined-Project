package main.com.ShavguLs.chess.model;

import java.util.LinkedList;
import java.util.List;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.IOException;

/* The parent class for all chess pieces. Has common methods for moving and tracking position.
Defines helper methods for checking possible moves. */

public abstract class Piece {
    private final int color;

    private Square currentSquare;

    private Image img;

    private final String imageFile;

    public Piece(int color, Square initSq, String imgFile) {
        this.color = color;
        this.currentSquare = initSq;
        this.imageFile = imgFile;

        try {
            if (this.img == null) {
                this.img = ImageIO.read(getClass().getResource(imgFile));
            }
        } catch (IOException e) {
            System.out.println("Fileb not found: " + e.getMessage());
        }
    }

    public boolean move(Square fin) {
        Piece occup = fin.getOccupyingPiece();

        if (occup != null) {
            if (occup.getColor() == this.color) return false;

            fin.capture(this, currentSquare.getBoard());
        }

        currentSquare.removePiece();
        this.currentSquare = fin;
        currentSquare.put(this);
        return true;
    }

    public Square getPosition() {
        return currentSquare;
    }

    public void setPosition(Square sq) {
        this.currentSquare = sq;
    }

    public int getColor() {
        return color;
    }

    public Image getImage() {
        return img;
    }

    public String getImageFile() {
        return imageFile;
    }

    public int[] getLinearOccupations(Square[][] board, int x, int y) {
        int lastYabove = 0;
        int lastXright = 7;
        int lastYbelow = 7;
        int lastXleft = 0;

        for (int i = y - 1; i >= 0; i--) {
            if (board[i][x].isOccupied()) {
                if (board[i][x].getOccupyingPiece().getColor() != this.color) {
                    lastYabove = i;
                } else {
                    lastYabove = i + 1;
                }
                break;
            }
        }

        for (int i = y + 1; i < 8; i++) {
            if (board[i][x].isOccupied()) {
                if (board[i][x].getOccupyingPiece().getColor() != this.color) {
                    lastYbelow = i;
                } else {
                    lastYbelow = i - 1;
                }
                break;
            }
        }

        for (int i = x - 1; i >= 0; i--) {
            if (board[y][i].isOccupied()) {
                if (board[y][i].getOccupyingPiece().getColor() != this.color) {
                    lastXleft = i;
                } else {
                    lastXleft = i + 1;
                }
                break;
            }
        }
        for (int i = x + 1; i < 8; i++) {
            if (board[y][i].isOccupied()) {
                if (board[y][i].getOccupyingPiece().getColor() != this.color) {
                    lastXright = i;
                } else {
                    lastXright = i - 1;
                }
                break;
            }
        }

        int[] occups = {lastYabove, lastYbelow, lastXleft, lastXright};
        return occups;
    }

    public List<Square> getDiagonalOccupations(Square[][] board, int x, int y) {
        LinkedList<Square> diagOccup = new LinkedList<Square>();

        int xNW = x - 1;
        int yNW = y - 1;
        while (xNW >= 0 && yNW >= 0) {
            if (board[yNW][xNW].isOccupied()) {
                if (board[yNW][xNW].getOccupyingPiece().getColor() == this.color) {
                    break;
                } else {
                    diagOccup.add(board[yNW][xNW]);
                    break;
                }
            } else {
                diagOccup.add(board[yNW][xNW]);
                yNW--;
                xNW--;
            }
        }

        int xSW = x - 1;
        int ySW = y + 1;
        while (xSW >= 0 && ySW < 8) {
            if (board[ySW][xSW].isOccupied()) {
                if (board[ySW][xSW].getOccupyingPiece().getColor() == this.color) {
                    break;
                } else {
                    diagOccup.add(board[ySW][xSW]);
                    break;
                }
            } else {
                diagOccup.add(board[ySW][xSW]);
                ySW++;
                xSW--;
            }
        }

        int xSE = x + 1;
        int ySE = y + 1;
        while (xSE < 8 && ySE < 8) {
            if (board[ySE][xSE].isOccupied()) {
                if (board[ySE][xSE].getOccupyingPiece().getColor() == this.color) {
                    break;
                } else {
                    diagOccup.add(board[ySE][xSE]);
                    break;
                }
            } else {
                diagOccup.add(board[ySE][xSE]);
                ySE++;
                xSE++;
            }
        }

        int xNE = x + 1;
        int yNE = y - 1;
        while (xNE < 8 && yNE >= 0) {
            if (board[yNE][xNE].isOccupied()) {
                if (board[yNE][xNE].getOccupyingPiece().getColor() == this.color) {
                    break;
                } else {
                    diagOccup.add(board[yNE][xNE]);
                    break;
                }
            } else {
                diagOccup.add(board[yNE][xNE]);
                yNE--;
                xNE++;
            }
        }

        return diagOccup;
    }

    public abstract List<Square> getLegalMoves(Board b);
}