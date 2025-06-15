// In: main/com/ShavguLs/chess/view/StartMenu.java
// Replaces the ENTIRE old file.

package main.com.ShavguLs.chess.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Make StartMenu a JFrame so it can be a standalone window
public class StartMenu extends JFrame implements ActionListener {

    public StartMenu() {
        super("Chess Game - Main Menu");

        // Frame setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null); // Center the window
        setLayout(new GridLayout(2, 1, 10, 10));

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome to Chess!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 24));
        add(welcomeLabel);

        // Start Button
        JButton startButton = new JButton("Start New Game");
        startButton.addActionListener(this); // The "this" refers to the StartMenu itself
        add(startButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // When the button is clicked, create and show the game window
        // We can add dialogs here later to get player names and time controls

        String whitePlayerName = "White";
        String blackPlayerName = "Black";
        int hours = 0, minutes = 10, seconds = 0; // Default 10-minute game

        // Create the game window
        SwingUtilities.invokeLater(() -> new GameWindow(
                blackPlayerName,
                whitePlayerName,
                hours,
                minutes,
                seconds
        ));

        // Close the start menu window
        this.dispose();
    }
}