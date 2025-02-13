package task3;

import javax.swing.*;
import java.awt.*;

// Main class
// starts game, creates MVC classes; also has main game cycle

public class Main {
    public static void main(String[] args) {
        TetrisModel model = new TetrisModel(10, 20); // Model
        TetrisView view = new TetrisView(model); // View
        HighScores hs = new HighScores();
        TimerPanel timer_panel = new TimerPanel();
        ScorePanel score_panel = new ScorePanel(model);
        TetrisController controller = new TetrisController(model, view, hs, timer_panel); // Controller

        JFrame frame = new JFrame("Tetris");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(view.GetCellSize() * model.GetWidth() + 12, view.GetCellSize() * model.GetHeight() + 90);
        frame.setResizable(false);

        frame.setLayout(new BorderLayout());
        frame.add(view, BorderLayout.CENTER);
        frame.add(score_panel, BorderLayout.SOUTH);
        frame.add(timer_panel, BorderLayout.NORTH);
        frame.addKeyListener(controller);

        TetrisMenuBar menu_bar = new TetrisMenuBar(controller);
        frame.setJMenuBar(menu_bar);

        frame.setVisible(true);

        while (!model.IsGameOver()) {
            try {
                Thread.sleep(500);
                model.MovePieceDown();
                view.repaint();
                score_panel.UpdateScore();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("GAME OVER!");
    }
}