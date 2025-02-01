import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task3.TetrisModel;
import task3.TetroMino;

import java.awt.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TetrominoTests {
    private TetrisModel model;

    @BeforeEach
    void setUp() {
        model = new TetrisModel(10, 20);
    }

    // I
    @Test
    void testMovePieceDown_I() {
        TetroMino piece = TetroMino.createI();
        model.SetCurrentPiece(piece);
        int initialY = piece.getCoordinates()[0].y;

        model.MovePieceDown();
        assertEquals(initialY + 1, piece.getCoordinates()[0].y);
    }

    @Test
    void testMovePieceLeft_I() {
        TetroMino piece = TetroMino.createI();
        model.SetCurrentPiece(piece);
        int initialX = piece.getCoordinates()[0].x;

        if (model.CanMove(piece, -1, 0)) {
            model.MovePieceLeft();
            assertEquals(initialX - 1, piece.getCoordinates()[0].x);
        } else {
            model.MovePieceLeft();
            assertEquals(initialX, piece.getCoordinates()[0].x);
        }
    }

    // O
    @Test
    void testMovePieceDown_O() {
        TetroMino piece = TetroMino.createO();
        model.SetCurrentPiece(piece);
        int initialY = piece.getCoordinates()[0].y;

        model.MovePieceDown();
        assertEquals(initialY + 1, piece.getCoordinates()[0].y);
    }

    @Test
    void testMovePieceLeft_O() {
        TetroMino piece = TetroMino.createO();
        model.SetCurrentPiece(piece);
        int initialX = piece.getCoordinates()[0].x;

        if (model.CanMove(piece, -1, 0)) {
            model.MovePieceLeft();
            assertEquals(initialX - 1, piece.getCoordinates()[0].x);
        } else {
            model.MovePieceLeft();
            assertEquals(initialX, piece.getCoordinates()[0].x);
        }
    }

    // T
    @Test
    void testMovePieceDown_T() {
        TetroMino piece = TetroMino.createT();
        model.SetCurrentPiece(piece);
        int initialY = piece.getCoordinates()[0].y;

        model.MovePieceDown();
        assertEquals(initialY + 1, piece.getCoordinates()[0].y);
    }

    @Test
    void testMovePieceLeft_T() {
        TetroMino piece = TetroMino.createT();
        model.SetCurrentPiece(piece);
        int initialX = piece.getCoordinates()[0].x;

        if (model.CanMove(piece, -1, 0)) {
            model.MovePieceLeft();
            assertEquals(initialX - 1, piece.getCoordinates()[0].x);
        } else {
            model.MovePieceLeft();
            assertEquals(initialX, piece.getCoordinates()[0].x);
        }
    }

    // L
    @Test
    void testMovePieceDown_L() {
        TetroMino piece = TetroMino.createL();
        model.SetCurrentPiece(piece);
        int initialY = piece.getCoordinates()[0].y;

        model.MovePieceDown();
        assertEquals(initialY + 1, piece.getCoordinates()[0].y);
    }

    @Test
    void testMovePieceLeft_L() {
        TetroMino piece = TetroMino.createL();
        model.SetCurrentPiece(piece);
        int initialX = piece.getCoordinates()[0].x;

        if (model.CanMove(piece, -1, 0)) {
            model.MovePieceLeft();
            assertEquals(initialX - 1, piece.getCoordinates()[0].x);
        } else {
            model.MovePieceLeft();
            assertEquals(initialX, piece.getCoordinates()[0].x);
        }
    }

    // J
    @Test
    void testMovePieceDown_J() {
        TetroMino piece = TetroMino.createJ();
        model.SetCurrentPiece(piece);
        int initialY = piece.getCoordinates()[0].y;

        model.MovePieceDown();
        assertEquals(initialY + 1, piece.getCoordinates()[0].y);
    }

    @Test
    void testMovePieceLeft_J() {
        TetroMino piece = TetroMino.createJ();
        model.SetCurrentPiece(piece);
        int initialX = piece.getCoordinates()[0].x;

        if (model.CanMove(piece, -1, 0)) {
            model.MovePieceLeft();
            assertEquals(initialX - 1, piece.getCoordinates()[0].x);
        } else {
            model.MovePieceLeft();
            assertEquals(initialX, piece.getCoordinates()[0].x);
        }
    }

    // S
    @Test
    void testMovePieceDown_S() {
        TetroMino piece = TetroMino.createS();
        model.SetCurrentPiece(piece);
        int initialY = piece.getCoordinates()[0].y;

        model.MovePieceDown();
        assertEquals(initialY + 1, piece.getCoordinates()[0].y);
    }

    @Test
    void testMovePieceLeft_S() {
        TetroMino piece = TetroMino.createS();
        model.SetCurrentPiece(piece);
        int initialX = piece.getCoordinates()[0].x;

        if (model.CanMove(piece, -1, 0)) {
            model.MovePieceLeft();
            assertEquals(initialX - 1, piece.getCoordinates()[0].x);
        } else {
            model.MovePieceLeft();
            assertEquals(initialX, piece.getCoordinates()[0].x);
        }
    }

    // Z
    @Test
    void testMovePieceDown_Z() {
        TetroMino piece = TetroMino.createZ();
        model.SetCurrentPiece(piece);
        int initialY = piece.getCoordinates()[0].y;

        model.MovePieceDown();
        assertEquals(initialY + 1, piece.getCoordinates()[0].y);
    }

    @Test
    void testMovePieceLeft_Z() {
        TetroMino piece = TetroMino.createZ();
        model.SetCurrentPiece(piece);
        int initialX = piece.getCoordinates()[0].x;

        if (model.CanMove(piece, -1, 0)) {
            model.MovePieceLeft();
            assertEquals(initialX - 1, piece.getCoordinates()[0].x);
        } else {
            model.MovePieceLeft();
            assertEquals(initialX, piece.getCoordinates()[0].x);
        }
    }
}
