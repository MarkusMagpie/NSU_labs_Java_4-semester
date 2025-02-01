import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import task3.*;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class MovementTests {
    private TetrisModel model;

    @BeforeEach
    void setUp() {
        model = new TetrisModel(10, 20);
    }

    @Test
    void testScoreIncreaseOnRowClear() {
        boolean[][] board = model.GetBoard();
        for (int x = 0; x < 10; x++) {
            board[x][19] = true;
        }

        // опускаем текущую фигуру максимально вниз
        for (int i = 0; i < 100; i++) {
            model.MovePieceDown();
            if (!model.CanMove(model.GetCurrentPiece(), 0, 1)) {
                System.out.println(i + " rows cleared");
                System.out.println("Score before row is cleared: " + model.GetScore());
                assertEquals(0, model.GetScore());
                break;
            }
        }

        model.MovePieceDown();

        System.out.println("Score after row is cleared: " + model.GetScore());
        assertEquals(100, model.GetScore());
    }

    @Test
    void testMovePieceLeft() {
        TetroMino piece = model.GetCurrentPiece();
        int initialX = piece.getCoordinates()[0].x;
        if (model.CanMove(piece, -1, 0)) {
            model.MovePieceLeft();
            assertEquals(initialX - 1, piece.getCoordinates()[0].x);
        } else {
            model.MovePieceLeft();
            assertEquals(initialX, piece.getCoordinates()[0].x);
        }
    }

    @Test
    void testMovePieceRight() {
        TetroMino piece = model.GetCurrentPiece();
        int initialX = piece.getCoordinates()[0].x;
        if (model.CanMove(piece, 1, 0)) {
            model.MovePieceRight();
            assertEquals(initialX + 1, piece.getCoordinates()[0].x);
        } else {
            model.MovePieceRight();
            assertEquals(initialX, piece.getCoordinates()[0].x);
        }
    }

    @Test
    void testSingleRotation() {
        // Исходное расположение
        Point[] initial = {
                new Point(1, 2),
                new Point(2, 2), // центр вращения Т
                new Point(2, 3),
                new Point(3, 2)
        };

        TetroMino figure = new TetroMino(initial, Color.RED);
        figure.rotate();

        // ожидаемое положение после поворота на 90 градусов против часовой стрелки
        Point[] expected = {
                new Point(2, 1),
                new Point(2, 2), // центр остается на месте
                new Point(1, 2),
                new Point(2, 3)
        };

        assertArrayEquals(expected, figure.getCoordinates());
    }

    @Test
    void testFullRotation() {
        Point[] initial = {
                new Point(1, 2),
                new Point(2, 2), // центр вращения
                new Point(2, 3),
                new Point(3, 2)
        };

        TetroMino figure = new TetroMino(initial, Color.RED);
        figure.rotate();
        figure.rotate();
        figure.rotate();
        figure.rotate();

        assertArrayEquals(initial, figure.getCoordinates());
    }
}
