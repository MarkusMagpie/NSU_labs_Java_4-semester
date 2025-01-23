package task3;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TetrisMenuBar extends JMenuBar {
    public TetrisMenuBar(TetrisController controller) {
        JMenu game_menu = new JMenu("Game");
        JMenuItem new_game_item = new JMenuItem("New Game");
        JMenuItem exit_item = new JMenuItem("Exit");
        JMenuItem high_scores_item = new JMenuItem("High Scores");
        JMenuItem about = new JMenuItem("About");

        new_game_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.StartNewGame();
            }
        });

        high_scores_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.ShowHighScores();
            }
        });

        exit_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.ExitVerification();
            }
        });

        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.ShowAbout();
            }
        });

        game_menu.add(new_game_item);
        game_menu.add(high_scores_item);
        game_menu.add(about);
        game_menu.add(exit_item);

        add(game_menu);
    }
}
