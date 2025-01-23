package task3;

import java.awt.*;

// Tetromino class
// represents a tetromino figure in game
// any figure has 4 blocks

// coordinates - array of points that define the figure
// color - color of the figure
// rotate - function that rotates the figure

public class TetroMino {
    private Point[] coordinates;
    private Color color;

    // constructor
    public TetroMino(Point[] coordinates, Color color) {
        this.coordinates = coordinates;
        this.color = color;
    }

    // getter functions
    public Point[] getCoordinates() {
        return coordinates;
    }

    public Color getColor() {
        return color;
    }

    public void rotate() {
        Point center = coordinates[1];
        for (int i = 0; i < coordinates.length; ++i) {
            // 2d matrix 90 degree counter-clockwise rotation matrix
            // | 0 -1| => x = -y
            // | 1  0| => y = x
            int x = coordinates[i].x - center.x; // offset from center
            int y = coordinates[i].y - center.y;
            coordinates[i].x =center.x - y;
            coordinates[i].y = center.y + x;
        }
    }

    // factory methods to create all kinds of tetrominos (7)
    public static TetroMino createI() {
        return new TetroMino(new Point[]{
                new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)
        }, Color.CYAN);
    }

    public static TetroMino createO() {
        return new TetroMino(new Point[]{
                new Point(1, 0), new Point(2, 0), new Point(1, 1), new Point(2, 1)
        }, Color.YELLOW);
    }

    public static TetroMino createT() {
        return new TetroMino(new Point[]{
                new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1)
        }, Color.MAGENTA);
    }

    public static TetroMino createS() {
        return new TetroMino(new Point[]{
                new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1)
        }, Color.GREEN);
    }

    public static TetroMino createZ() {
        return new TetroMino(new Point[]{
                new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)
        }, Color.RED);
    }

    public static TetroMino createJ() {
        return new TetroMino(new Point[]{
                new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1)
        }, Color.BLUE);
    }

    public static TetroMino createL() {
        return new TetroMino(new Point[]{
                new Point(2, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1)
        }, Color.ORANGE);
    }
}
