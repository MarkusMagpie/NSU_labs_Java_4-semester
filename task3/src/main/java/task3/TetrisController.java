package task3;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// TetrisController class
// processes user input and updates game state
// here we override keyPressed method so that we can use it to process
//     user input

public class TetrisController implements KeyListener {
    private TetrisModel model;
    private TetrisView view;
    private HighScores hs;

    private TimerPanel timer_panel;
    private Timer game_timer;

    private String player_name;

    public TetrisController(TetrisModel model, TetrisView view, HighScores hs, TimerPanel timer_panel) {
        this.model = model;
        this.view = view;
        this.hs = new HighScores();
        this.timer_panel = timer_panel;
        timer_panel.StartTimer();

        player_name = JOptionPane.showInputDialog(null, "Enter your username: ", "New Game", JOptionPane.QUESTION_MESSAGE);

        // if game is not paused, start game timer
        game_timer = new Timer(1000, e -> {
            if (!model.GetPause()) {
                model.MovePieceDown();
                view.repaint();
            }
        });
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (model.GetPause()) {
                ResumeGame();
            } else {
                PauseGame();
            }
        }

        if (model.GetPause()) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                model.MovePieceLeft();
                break;
            case KeyEvent.VK_A:
                model.MovePieceLeft();
                break;

            case KeyEvent.VK_RIGHT:
                model.MovePieceRight();
                break;
            case KeyEvent.VK_D:
                model.MovePieceRight();
                break;

            case KeyEvent.VK_DOWN:
                model.MovePieceDown();
                break;
            case KeyEvent.VK_S:
                model.MovePieceDown();
                break;

            case KeyEvent.VK_UP:
                model.RotatePiece(); // !!!
                break;
            case KeyEvent.VK_W:
                model.RotatePiece();
                break;

            case KeyEvent.VK_Q:
                ExitVerification();
                break;
        }
        view.repaint();
    }

    // these 2 methods must be overriden due to implementation of KeyListener
    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public void PauseGame() {
        model.SetPause(true);
        timer_panel.StopTimer();
    }

    public void ResumeGame() {
        model.SetPause(false);
        timer_panel.StartTimer();
    }

    public void StartNewGame() {
        PauseGame();
        int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to start new game?", "New Game Confirmation", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            System.out.println("New game started");

            if (player_name != null) {
                hs.AddScore(player_name, model.GetScore(), timer_panel.GetElapsedTime());
            }
            player_name = JOptionPane.showInputDialog(null, "Enter your username: ", "New Game", JOptionPane.QUESTION_MESSAGE);

            model.Reset();
            view.repaint();
            timer_panel.ResetTimer();
            timer_panel.StartTimer();
        } else {
            ResumeGame();
        }
    }

    public void ShowHighScores() {
        PauseGame();
        hs.ShowHighScores();
        ResumeGame();
    }

    public void ShowAbout() {
        PauseGame();
        String about = "NSU 4th semester\nTetris by Matvey Sorokin";
        JOptionPane.showMessageDialog(null, about, "About", JOptionPane.INFORMATION_MESSAGE);
        ResumeGame();
    }

    public void ExitVerification() {
        PauseGame();
        int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            hs.AddScore(player_name, model.GetScore(), timer_panel.GetElapsedTime());
            System.out.println("Game exited");
            System.exit(0);
        }
        ResumeGame();
    }
}
