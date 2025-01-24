package task3;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

// TetrisModel class
// controls the state of game field, figure

public class TetrisModel {
    private int width;
    private int height;
    private TetroMino current_piece;
    private boolean[][] board;
    private int score;
    private boolean paused;

    public TetrisModel(int width, int height) {
        board = new boolean[width][height];
        this.width = width;
        this.height = height;
        SpawnPiece();
//        score = 0;
        paused = false;
    }

    public void Reset() {
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                board[x][y] = false;
            }
        }
        score = 0;
        paused = false;
        SpawnPiece();
    }

    private void SpawnPiece() {
        // create a single T-tetromino (randomly)
//        Point[] coords = { new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(3, 0) };
//        current_piece = new TetroMino(coords, Color.RED);

        Random random = new Random();
        int shape = random.nextInt(7);
        switch (shape) {
            case 0:
                current_piece = TetroMino.createI();
                break;
            case 1:
                current_piece = TetroMino.createO();
                break;
            case 2:
                current_piece = TetroMino.createT();
                break;
            case 3:
                current_piece = TetroMino.createS();
                break;
            case 4:
                current_piece = TetroMino.createZ();
                break;
            case 5:
                current_piece = TetroMino.createJ();
                break;
            case 6:
                current_piece = TetroMino.createL();
                break;
        }
//        PlacePiece();
    }

    public void MovePieceDown() {
        if (paused) return;

        if (CanMove(current_piece, 0, 1)) {
            for (Point p : current_piece.getCoordinates()) {
                p.y += 1;
            }
        } else {
            PlacePiece(); // update coords at board
            SpawnPiece();
            if (!CanMove(current_piece, 0, 0)) {
//                System.out.println("GAME OVER at MovePieceDown");
                JOptionPane.showMessageDialog(null, "GAME OVER", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
    }

    public void MovePieceLeft() {
        if (paused) return;

        if (CanMove(current_piece, -1, 0)) {
            for (Point p : current_piece.getCoordinates()) {
                p.x -= 1;
            }
        }
    }

    public void MovePieceRight() {
        if (paused) return;

        if (CanMove(current_piece, 1, 0)) {
            for (Point p : current_piece.getCoordinates()) {
                p.x += 1;
            }
        }
    }

    public void RotatePiece() {
        if (paused) return;

        current_piece.rotate();
        // check if we can't rotate, return to original position
        if (!CanMove(current_piece, 0, 0)) {
            System.out.println("Cannot rotate object");
//            JOptionPane.showMessageDialog(null, "Cannot rotate object", "Error", JOptionPane.ERROR_MESSAGE);
            current_piece.rotate();
            current_piece.rotate();
            current_piece.rotate();
        }
    }

    private void PlacePiece() {
        for (Point p : current_piece.getCoordinates()) {
            if (p.x >= 0 && p.x < width && p.y >= 0 && p.y < height) {
                board[p.x][p.y] = true;
            }
        }
        ClearRows();
    }

    // check from bottom to top if any row is full
    //    if it is, then add 100 score
    private void ClearRows() {
        for (int y = height - 1; y >= 0; --y) {
            boolean full_row = true;
            for (int x = 0; x < width; ++x) {
                if (!board[x][y]) {
                    full_row = false;
                    break;
                }
            }

            if (full_row) {
                score += 100;
                for (int row = y; row > 0; --row) {
                    for (int col = 0; col < width; ++col) {
                        board[col][row] = board[col][row - 1];
                    }
                }
                for (int col = 0; col < width; ++col) {
                    board[col][0] = false;
                }
                ++y;
            }
        }
    }

    private boolean CanMove(TetroMino piece, int x, int y) {
        for (Point p : piece.getCoordinates()) {
            int new_x = p.x + x;
            int new_y = p.y + y;
            if (new_x < 0 || new_x >= width || new_y >= height || new_y < 0) {
                return false;
            }
            if (board[new_x][new_y]) {
                return false;
            }
        }
        return true;
    }

    // 3 getters
    public int GetScore() {
        return score;
    }

    public TetroMino GetCurrentPiece() {
        return current_piece;
    }

    public boolean[][] GetBoard() {
        return board;
    }

    public boolean IsGameOver() {
        return false;
    }

    // setter and getter for pause
    public void SetPause(boolean pause) {
        paused = pause;
    }

    public boolean GetPause() {
        return paused;
    }

    // getters for TetrisView to creatte initial grid
    public int GetWidth() {
        return width;
    }

    public int GetHeight() {
        return height;
    }
}
