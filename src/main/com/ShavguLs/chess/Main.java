// In: main/com/ShavguLs/chess/Main.java
// Replaces the ENTIRE old file.

package main.com.ShavguLs.chess;

import main.com.ShavguLs.chess.view.StartMenu;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // It's best practice to create and show Swing components on the
        // Event Dispatch Thread (EDT). SwingUtilities.invokeLater does this.
        SwingUtilities.invokeLater(() -> {
            try {
                // Set the look and feel for a more native appearance
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Could not set the system look and feel.");
                e.printStackTrace();
            }

            // Create an instance of our new StartMenu JFrame
            StartMenu startMenu = new StartMenu();

            // Make the start menu window visible to the user
            startMenu.setVisible(true);
        });
    }
}