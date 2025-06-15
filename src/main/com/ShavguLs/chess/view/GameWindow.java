// In: main/com/ShavguLs/chess/view/GameWindow.java
// Replaces the ENTIRE old file.

package main.com.ShavguLs.chess.view;

import main.com.ShavguLs.chess.controller.GameController;
// We still need the Clock class from the old model for now
import main.com.ShavguLs.chess.model_old.Clock;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// The ClockCallback is now an INNER INTERFACE of GameWindow, making it self-contained.
public class GameWindow extends JFrame{
    private JFrame gameWindow;

    private JLabel blackTimeLabel;
    private JLabel whiteTimeLabel;

    private ChessBoardPanel boardPanel;
    private GameController controller;

    // We define the callback interface here.
    public interface ClockCallback {
        void updateWhiteClock(String time);
        void updateBlackClock(String time);
        void timeOut(boolean whiteTimedOut);
    }

    public GameWindow(String blackName, String whiteName, int hh, int mm, int ss){
        gameWindow = new JFrame("Chess Game");
        this.controller = new GameController(hh, mm, ss, this);
        try{
            Image blackImg = ImageIO.read(getClass().getResource("/images/bk.png"));
            gameWindow.setIconImage(blackImg);
        }catch (Exception ex){
            System.out.println("Game file bk.png not found!");
        }

        gameWindow.setLocation(100, 100);
        gameWindow.setLayout(new BorderLayout(20, 20));

        JPanel gameData = gameDataPanel(blackName, whiteName, hh, mm, ss);
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
            // The controller will need a reference to something that can update the clocks.
            // But we will re-implement this later. For now, the stubs prevent errors.
            // controller.startTimer(this);
        }
    }

    // This public getter is needed by GameController to access the panel
    public ChessBoardPanel getChessBoardPanel() {
        return boardPanel;
    }

    private JPanel gameDataPanel(final String bn, final String wn,
                                 final int hh, final int mm, final int ss){
        JPanel gameData = new JPanel();
        gameData.setLayout(new GridLayout(2,2,0,0)); // Changed to 2x2 for better layout

        // PLAYER NAMES
        JLabel w = new JLabel(wn, SwingConstants.CENTER);
        JLabel b = new JLabel(bn, SwingConstants.CENTER);
        gameData.add(w);
        gameData.add(b);

        // CLOCKS
        whiteTimeLabel = new JLabel("Untimed game!", SwingConstants.CENTER);
        blackTimeLabel = new JLabel("Untimed game!", SwingConstants.CENTER);

        Font clockFont = new Font("Monospaced", Font.BOLD, 16);
        whiteTimeLabel.setFont(clockFont);
        blackTimeLabel.setFont(clockFont);

        if (!(hh == 0 && mm == 0 && ss == 0)){
            // This will work now because we added the getters back to the controller
            whiteTimeLabel.setText(controller.getWhiteClock().getTime());
            blackTimeLabel.setText(controller.getBlackClock().getTime());
        }

        JPanel whiteClockPanel = new JPanel(new BorderLayout());
        whiteClockPanel.setBorder(BorderFactory.createTitledBorder("White Clock"));
        whiteClockPanel.add(whiteTimeLabel, BorderLayout.CENTER);

        JPanel blackClockPanel = new JPanel(new BorderLayout());
        blackClockPanel.setBorder(BorderFactory.createTitledBorder("Black Clock"));
        blackClockPanel.add(blackTimeLabel, BorderLayout.CENTER);

        gameData.add(whiteClockPanel);
        gameData.add(blackClockPanel);

        return gameData;
    }

    private JPanel buttons() {
        JPanel buttons = new JPanel(new GridLayout(1, 3, 10, 0));

        final JButton quit = new JButton("Quit");
        quit.addActionListener(e -> {
            int n = JOptionPane.showConfirmDialog(gameWindow, "Are you sure you want to quit?", "Confirm quit", JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                controller.stopTimer();
                gameWindow.dispose();
            }
        });

        final JButton nGame = new JButton("New game");
        nGame.addActionListener(e -> {
            int n = JOptionPane.showConfirmDialog(gameWindow, "Are you sure you want to begin a new game?", "Confirm new game", JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                controller.stopTimer();
                // This StartMenu may need to be fixed later as well
                SwingUtilities.invokeLater(() -> new StartMenu().setVisible(true));
                gameWindow.dispose();
            }
        });

        final JButton instr = new JButton("How to play");
        instr.addActionListener(e -> JOptionPane.showMessageDialog(gameWindow,
                "Move the chess pieces on the board by clicking and dragging.", "How to play", JOptionPane.PLAIN_MESSAGE));

        buttons.add(instr);
        buttons.add(nGame);
        buttons.add(quit);
        return buttons;
    }

    // These methods show game over popups. They can be called by the controller later.


    public void updateWhiteClock(String time) {
        if (whiteTimeLabel != null) {
            whiteTimeLabel.setText(time);
        }
    }

    public void updateBlackClock(String time) {
        if (blackTimeLabel != null) {
            blackTimeLabel.setText(time);
        }
    }

    public void timeOut(boolean whiteTimedOut) {
        // When time runs out, we ask the controller to stop its timer logic
        controller.stopTimer();

        String winner = whiteTimedOut ? "Black" : "White";
        int n = JOptionPane.showConfirmDialog(
                gameWindow,
                winner + " wins on time! Play a new game?",
                winner + " Wins!",
                JOptionPane.YES_NO_OPTION);

        if (n == JOptionPane.YES_OPTION){
            SwingUtilities.invokeLater(() -> new StartMenu().setVisible(true));
            gameWindow.dispose();
        }
    }


    // In: main/com/ShavguLs/chess/view/GameWindow.java
// Add these two new public methods to the class.

// Don't forget to import JOptionPane at the top of the file:
// import javax.swing.JOptionPane;

    public void checkmateOccurred(boolean whiteLost) {
        String winner = whiteLost ? "Black" : "White";
        String message = "Checkmate! " + winner + " wins.";
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        // Here you could also disable the board or show a "New Game" button.
    }

    public void stalemateOccurred() {
        String message = "Stalemate! The game is a draw.";
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }



}