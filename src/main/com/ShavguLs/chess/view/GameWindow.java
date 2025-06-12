package main.com.ShavguLs.chess.view;

import main.com.ShavguLs.chess.controller.GameController;
import main.com.ShavguLs.chess.model.Board;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameWindow implements GameController.ClockCallback{
    private JFrame gameWindow;

    private JLabel blackTimeLabel;
    private JLabel whiteTimeLabel;

    private Timer timer;

    private int locationX = 100;
    private int locationY = 100;

    private int horizontalGap = 20;
    private int verticalGap = 20;

    private ChessBoardPanel boardPanel;
    private GameController controller;

    public GameWindow(String blackName, String whiteName, int hh, int mm, int ss){
        gameWindow = new JFrame("Chess Game");
        this.controller = new GameController(hh, mm, ss, this);
        try{
            Image blackImg = ImageIO.read(getClass().getResource("/images/bk.png"));
            gameWindow.setIconImage(blackImg);
        }catch (Exception ex){
            System.out.println("Game file bk.png not found!");
        }

        gameWindow.setLocation(locationX, locationY);
        gameWindow.setLayout(new BorderLayout(horizontalGap, verticalGap));

        JPanel gameData = gameDataPanel(blackName, whiteName, hh, mm, ss);
        gameData.setSize(gameData.getPreferredSize());
        gameWindow.add(gameData, BorderLayout.NORTH);

        this.boardPanel = new ChessBoardPanel(this, controller);
        gameWindow.add(boardPanel, BorderLayout.CENTER);

        gameWindow.add(buttons(), BorderLayout.SOUTH);
        gameWindow.setMinimumSize(gameWindow.getPreferredSize());
        gameWindow.setSize(gameWindow.getPreferredSize());
        gameWindow.setResizable(false);
        gameWindow.pack();
        gameWindow.setVisible(true);
        gameWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        if (!(hh == 0 && mm == 0 && ss == 0)){
            controller.startTimer(this);
        }
    }

    private JPanel gameDataPanel(final String bn, final String wn,
                                 final int hh, final int mm, final int ss){
        JPanel gameData = new JPanel();
        gameData.setLayout(new GridLayout(3,2,0,0));

        // PLAYER NAMES

        JLabel w = new JLabel(wn);
        JLabel b = new JLabel(bn);

        w.setHorizontalAlignment(JLabel.CENTER);
        w.setVerticalAlignment(JLabel.CENTER);
        b.setHorizontalAlignment(JLabel.CENTER);
        b.setVerticalAlignment(JLabel.CENTER);

        w.setSize(w.getMinimumSize());
        b.setSize(b.getMinimumSize());

        gameData.add(w);
        gameData.add(b);

        // CLOCKS
        whiteTimeLabel = new JLabel();
        blackTimeLabel = new JLabel();

        Font clockFont = new Font("Monospaced", Font.BOLD, 16);
        whiteTimeLabel.setFont(clockFont);
        blackTimeLabel.setFont(clockFont);

        blackTimeLabel.setHorizontalAlignment(JLabel.CENTER);
        blackTimeLabel.setVerticalAlignment(JLabel.CENTER);
        whiteTimeLabel.setHorizontalAlignment(JLabel.CENTER);
        whiteTimeLabel.setVerticalAlignment(JLabel.CENTER);

        if (!(hh == 0 && mm == 0 && ss == 0)){
            whiteTimeLabel.setText(controller.getWhiteClock().getTime());
            blackTimeLabel.setText(controller.getBlackClock().getTime());
        }else {
            whiteTimeLabel.setText("Untimed game!");
            blackTimeLabel.setText("Untimed game!");
        }

        JPanel whiteClockPanel = new JPanel(new BorderLayout());
        whiteClockPanel.setBorder(BorderFactory.createTitledBorder("White Clock"));
        whiteClockPanel.add(whiteTimeLabel, BorderLayout.CENTER);

        JPanel blackClockPanel = new JPanel(new BorderLayout());
        blackClockPanel.setBorder(BorderFactory.createTitledBorder("Black Clock"));
        blackClockPanel.add(blackTimeLabel, BorderLayout.CENTER);

        gameData.add(whiteClockPanel);
        gameData.add(blackClockPanel);

        gameData.setPreferredSize(gameData.getMinimumSize());

        return gameData;
    }

    private JPanel buttons() {
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1, 3, 10, 0));

        final JButton quit = new JButton("Quit");
        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(
                        gameWindow,
                        "Are you sure you want to quit?",
                        "Confirm quit", JOptionPane.YES_NO_OPTION);

                if (n == JOptionPane.YES_OPTION) {
                    controller.stopTimer();
                    gameWindow.dispose();
                }
            }
        });

        final JButton nGame = new JButton("New game");
        nGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(
                        gameWindow,
                        "Are you sure you want to begin a new game?",
                        "Confirm new game", JOptionPane.YES_NO_OPTION);

                if (n == JOptionPane.YES_OPTION) {
                    controller.stopTimer();
                    SwingUtilities.invokeLater(new StartMenu());
                    gameWindow.dispose();
                }
            }
        });

        final JButton instr = new JButton("How to play");

        instr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(gameWindow,
                        "Move the chess pieces on the board by clicking\n"
                                + "and dragging. The game will watch out for illegal\n"
                                + "moves. You can win either by your opponent running\n"
                                + "out of time or by checkmating your opponent.\n"
                                + "\nSpecial moves:\n"
                                + "• Castling: Move king two squares towards rook\n"
                                + "• En passant: Capture pawn that just moved two squares\n"
                                + "• Promotion: Pawn reaching the end becomes a new piece\n"
                                + "\nThe game can end in a draw by:\n"
                                + "• Stalemate (no legal moves but not in check)\n"
                                + "• Insufficient material to checkmate\n"
                                + "• 50 moves without capture or pawn move\n"
                                + "• Same position repeated 3 times\n"
                                + "\nGood luck, hope you enjoy the game!",
                        "How to play",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });

        buttons.add(instr);
        buttons.add(nGame);
        buttons.add(quit);

        buttons.setPreferredSize(buttons.getMinimumSize());

        return buttons;
    }

    public void checkmateOccurred (int c) {
        if (c == 0) {
            controller.stopTimer();
            int n = JOptionPane.showConfirmDialog(
                    gameWindow,
                    "White wins by checkmate! Set up a new game? \n" +
                            "Choosing \"No\" lets you look at the final situation.",
                    "White wins!",
                    JOptionPane.YES_NO_OPTION);

            if (n == JOptionPane.YES_OPTION) {
                SwingUtilities.invokeLater(new StartMenu());
                gameWindow.dispose();
            }
        } else {
            controller.stopTimer();
            int n = JOptionPane.showConfirmDialog(
                    gameWindow,
                    "Black wins by checkmate! Set up a new game? \n" +
                            "Choosing \"No\" lets you look at the final situation.",
                    "Black wins!",
                    JOptionPane.YES_NO_OPTION);

            if (n == JOptionPane.YES_OPTION) {
                SwingUtilities.invokeLater(new StartMenu());
                gameWindow.dispose();
            }
        }
    }

    public void stalemateOccurred() {
        controller.stopTimer();
        int n = JOptionPane.showConfirmDialog(
                gameWindow,
                "The game is a draw by stalemate!\n" +
                        "No legal moves available but king is not in check.\n" +
                        "Set up a new game?",
                "Draw - Stalemate",
                JOptionPane.YES_NO_OPTION);

        if (n == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(new StartMenu());
            gameWindow.dispose();
        }
    }

    public void drawOccurred(String reason) {
        controller.stopTimer();

        String message;
        switch (reason) {
            case "insufficient material":
                message = "The game is a draw!\n" +
                        "Neither player has sufficient material to checkmate.";
                break;
            case "50-move rule":
                message = "The game is a draw!\n" +
                        "50 moves have been made without a capture or pawn move.";
                break;
            case "threefold repetition":
                message = "The game is a draw!\n" +
                        "The same position has occurred three times.";
                break;
            default:
                message = "The game is a draw!";
        }

        int n = JOptionPane.showConfirmDialog(
                gameWindow,
                message + "\nSet up a new game?",
                "Draw",
                JOptionPane.YES_NO_OPTION);

        if (n == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(new StartMenu());
            gameWindow.dispose();
        }
    }

    @Override
    public void updateWhiteClock(String time) {
        if (whiteTimeLabel != null) {
            whiteTimeLabel.setText(time);
            whiteTimeLabel.repaint();
        }
    }

    @Override
    public void updateBlackClock(String time) {
        if (blackTimeLabel != null) {
            blackTimeLabel.setText(time);
            blackTimeLabel.repaint();
        }
    }

    @Override
    public void timeOut(boolean whiteTimedOut) {
        String winner = whiteTimedOut ? "Black" : "White";

        int n = JOptionPane.showConfirmDialog(
                gameWindow,
                winner + " wins by time! Play a new game? \n" +
                        "Choosing \"No\" lets you look at the final position.",
                winner + " Wins!",
                JOptionPane.YES_NO_OPTION);

        if (n == JOptionPane.YES_OPTION){
            SwingUtilities.invokeLater(new StartMenu());
            gameWindow.dispose();
        }
    }
}