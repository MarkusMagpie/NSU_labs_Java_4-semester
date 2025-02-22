package task3;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TetrisController implements KeyListener {
    private final TetrisModel model;
    private final TetrisView view;

    private final HighScores hs;
    private final TimerPanel timer_panel; // таймер для счета времен игры
    private final Timer game_timer; // таймер для игры
    private final ScorePanel score_panel;
    private String player_name;

    public TetrisController(TetrisModel model, TetrisView view, HighScores hs, TimerPanel timer_panel, ScorePanel sp) {
        this.model = model;
        this.view = view;
        this.hs = hs;
        this.timer_panel = timer_panel; // таймер секундный
        this.score_panel = sp;

        player_name = JOptionPane.showInputDialog(null, "Enter your username: ", "New Game", JOptionPane.QUESTION_MESSAGE);

        if (player_name == null || player_name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Game Over", "Exit", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }

        game_timer = new Timer(500, _ -> {
            if (!model.GetPause()) {
                model.MovePieceDown();
                view.repaint();
                score_panel.UpdateScore();
            }
        });

        timer_panel.StartTimer();
        game_timer.start();
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

        if (e.getKeyCode() == KeyEvent.VK_Q) {
            ExitVerification();
        }

        // обязательно проверка здесь, НЕ ВЫШЕ ибо тогда игнорит вообще все кнопки и нельзя снять паузу!!!
        if (model.GetPause()) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT, KeyEvent.VK_A:
                model.MovePieceLeft();
                break;

            case KeyEvent.VK_RIGHT, KeyEvent.VK_D:
                model.MovePieceRight();
                break;

            case KeyEvent.VK_DOWN, KeyEvent.VK_S:
                model.MovePieceDown();
                break;

            case KeyEvent.VK_UP, KeyEvent.VK_W:
                model.RotatePiece();
                break;

            case KeyEvent.VK_SHIFT:
                model.ShiftPieceDown();
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
        System.out.println("Game paused");
        model.SetPause(true);
        timer_panel.StopTimer();
        game_timer.stop();
    }

    public void ResumeGame() {
        System.out.println("Game resumed");
        model.SetPause(false);
        timer_panel.StartTimer();
        game_timer.start();
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
            ResumeGame();
        } else {
            ResumeGame();
        }
    }

    public void ShowHighScores() {
        PauseGame();
        System.out.println("Showed high scores");
        hs.ShowHighScores();
        ResumeGame();
    }

    public void ShowAbout() {
        PauseGame();
        String about = "NSU 4th semester\nTetris\nMathew Sorokin";
        System.out.println("Showed information about game");
        JOptionPane.showMessageDialog(null, about, "About", JOptionPane.INFORMATION_MESSAGE);
        ResumeGame();
    }

    public void GameOver() {
        JOptionPane.showMessageDialog(null, "Game Over", "Exit", JOptionPane.INFORMATION_MESSAGE);
        hs.AddScore(player_name, model.GetScore(), timer_panel.GetElapsedTime());
        System.out.println("Added new score: " + player_name + " - " + model.GetScore() + " - " + timer_panel.GetElapsedTime() + "s");
        System.out.println("You have exited the game");
        System.exit(0);
    }

    public void ExitVerification() {
        PauseGame();
        int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) { GameOver(); }
        ResumeGame();
    }
}
