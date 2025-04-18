package main.com.ShavguLs.chess;

import main.com.ShavguLs.chess.view.StartMenu;

import javax.swing.*;

// This is the starting point of our chess game. It sets up how the game looks and starts the menu.

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch (Exception ex){
            System.err.println("Couldn't set system look and feel: " + ex.getMessage());
        }

        SwingUtilities.invokeLater(new StartMenu());
    }
}
