package main.com.ShavguLs.chess.view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartMenu implements Runnable{
    @Override
    public void run(){
        final JFrame startWindow = new JFrame("Chess Game");
        startWindow.setSize(260, 240);
        startWindow.setLocationRelativeTo(null);
        startWindow.setResizable(false);

        Box components = Box.createVerticalBox();
        startWindow.add(components);

        JPanel titlePanel = new JPanel();
        components.add(titlePanel);
        JLabel titleLabel = new JLabel("Chess Game");
        titlePanel.add(titleLabel);

        JPanel blackPanel = new JPanel();
        components.add(blackPanel);
        JTextField blackInput = new JTextField("Black Player", 10);
        blackPanel.add(blackInput);

        JPanel whitePanel = new JPanel();
        components.add(whitePanel);
        JTextField whiteInput = new JTextField("White Panel", 10);
        whitePanel.add(whiteInput);

        JPanel timerPanel = new JPanel();
        components.add(timerPanel);

        JPanel buttonPanel = new JPanel();
        components.add(buttonPanel);

        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String blackName = blackInput.getText();
                String whiteName = whiteInput.getText();

                new GameWindow(blackName, whiteName, 0,0,0);
                startWindow.dispose();
            }
        });

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startWindow.dispose();
            }
        });

        buttonPanel.add(startButton);
        buttonPanel.add(quitButton);

        startWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startWindow.setVisible(true);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(new StartMenu());
    }
}
