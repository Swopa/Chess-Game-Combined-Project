package main.com.ShavguLs.chess.view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartMenu implements Runnable{
    @Override
    public void run(){
        final JFrame startWindow = new JFrame("Chess Game");
        startWindow.setSize(320, 300);
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
        JLabel blackLabel = new JLabel("Black player:");
        JTextField blackInput = new JTextField("Black Player", 15);
        blackPanel.add(blackLabel);
        blackPanel.add(blackInput);

        JPanel whitePanel = new JPanel();
        components.add(whitePanel);
        JLabel whiteLabel = new JLabel("White Player");
        JTextField whiteInput = new JTextField("White Player", 15);
        whitePanel.add(whiteLabel);
        whitePanel.add(whiteInput);

        JPanel timerPanel = new JPanel();
        components.add(timerPanel);
        timerPanel.add(new JLabel("Time Control:"));

        String[] hourOptions = {"0", "1", "2", "3"};
        String[] minuteSecondOptions = new String[60];
        for (int i = 0; i < 60; i++) {
            minuteSecondOptions[i] = String.format("%02d", i);
        }

        JComboBox<String> hours = new JComboBox<>(hourOptions);
        JComboBox<String> minutes = new JComboBox<>(minuteSecondOptions);
        JComboBox<String> seconds = new JComboBox<>(minuteSecondOptions);

        JPanel timePanel = new JPanel();
        timePanel.add(new JLabel("Hours:"));
        timePanel.add(hours);
        timePanel.add(new JLabel("Minutes:"));
        timePanel.add(minutes);
        timePanel.add(new JLabel("Seconds:"));
        timePanel.add(seconds);
        components.add(timePanel);

        hours.setSelectedItem("0");
        minutes.setSelectedItem("10");
        seconds.setSelectedItem("00");

        JPanel buttonPanel = new JPanel();
        components.add(buttonPanel);

        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String blackName = blackInput.getText();
                String whiteName = whiteInput.getText();

                int hh = Integer.parseInt((String) hours.getSelectedItem());
                int mm = Integer.parseInt((String) minutes.getSelectedItem());
                int ss = Integer.parseInt((String) seconds.getSelectedItem());

                new GameWindow(blackName, whiteName, hh, mm, ss);
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
}