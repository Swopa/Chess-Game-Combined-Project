package main.com.ShavguLs.chess;

import main.com.ShavguLs.chess.view.StartMenu;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch (Exception ex){
            System.err.println("Couldnt set sys look and feel: " + ex.getMessage());
        }

        SwingUtilities.invokeLater(new StartMenu());
    }
}
