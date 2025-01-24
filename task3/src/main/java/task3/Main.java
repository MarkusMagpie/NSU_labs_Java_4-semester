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
        TetrisController controller = new TetrisController(model, view, hs, timer_panel); // Controller
        ScorePanel score_panel = new ScorePanel(model);

        JFrame frame = new JFrame("Тетрис");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(new Dimension(view.GetCellSize() * model.GetWidth() + 130, view.GetCellSize() * model.GetHeight() + 90));
        frame.setLayout(new BorderLayout());
        frame.add(view, BorderLayout.CENTER);
        frame.add(score_panel, BorderLayout.SOUTH);
        frame.add(timer_panel, BorderLayout.NORTH);
        frame.addKeyListener(controller);

        TetrisMenuBar menuBar = new TetrisMenuBar(controller);
        frame.setJMenuBar(menuBar);

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
        System.out.println("ИГРА ОКОНЧЕНА!");
    }
}