package task3;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class TetrisModel {
    private final int width;
    private final int height;
    private TetroMino current_piece;
    private final boolean[][] board;
    private int score;
    private boolean paused;
    private TetrisController controller;

    public TetrisModel(int width, int height) {
        board = new boolean[width][height];
        this.width = width;
        this.height = height;
        SpawnPiece();
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

    public void SpawnPiece() {
        Random random = new Random();
        current_piece = TetroMino.getRandomTetroMino(random);
    }

    public void MovePieceDown() {
        if (paused) return;

        if (CanMove(current_piece, 0, 1)) {
            for (Point p : current_piece.getCoordinates()) {
                p.y += 1;
            }
        } else {
            PlacePiece(); // обновляем координаты в двусвязном массиве board
            SpawnPiece(); // ТОЛЬКО выбор новой current_piece случайным образом
            if (!CanMove(current_piece, 0, 0)) { // если нельзя двигаться вниз, то игрок проиграл
                controller.GameOver();
            }
        }
    }

    public void ShiftPieceDown() {
        if (paused) return;

        while (CanMove(current_piece, 0, 1)) {
            MovePieceDown();
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
        if (CanMove(current_piece, 0, 0)) { return; }

        int[][] wallKick_offsets = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}};

        boolean wallKickSuccess = false;
        for (int[] offset : wallKick_offsets) {
            if (CanMove(current_piece, offset[0], offset[1])) {
                for (Point p : current_piece.getCoordinates()) {
                    p.x += offset[0];
                    p.y += offset[1];
                }
                wallKickSuccess = true;
                break;
            }
        }

        if (!wallKickSuccess) {
            System.out.println("Cannot rotate object:" + current_piece);
            current_piece.rotate();
            current_piece.rotate();
            current_piece.rotate();
        }
    }

    public void PlacePiece() {
        for (Point p : current_piece.getCoordinates()) {
            if (p.x >= 0 && p.x < width && p.y >= 0 && p.y < height) {
                board[p.x][p.y] = true;
            }
        }
        ClearRows();
    }

    // check from bottom to top if any row is full
    //    if it is, then add 100 score
    public void ClearRows() {
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

    public boolean CanMove(TetroMino piece, int x, int y) {
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

    public void setController(TetrisController controller) {
        this.controller = controller;
    }

    public int GetScore() {
        return score;
    }

    public TetroMino GetCurrentPiece() {
        return current_piece;
    }

    public TetroMino SetCurrentPiece(TetroMino piece) {
        return current_piece = piece;
    }

    public boolean[][] GetBoard() {
        return board;
    }

    public void SetPause(boolean pause) {
        paused = pause;
    }

    public boolean GetPause() {
        return paused;
    }

    public int GetWidth() {
        return width;
    }

    public int GetHeight() {
        return height;
    }
}
