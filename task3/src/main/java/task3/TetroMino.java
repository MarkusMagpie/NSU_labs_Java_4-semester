package task3;

import java.awt.*;

// coordinates - array of Points that define the figure's position

public class TetroMino {
    private final Point[] coordinates;
    private final Color color;

    // constructor
    public TetroMino(Point[] coordinates, Color color) {
        this.coordinates = coordinates;
        this.color = color;
    }

    public void rotate() {
        Point center = coordinates[1];
        for (Point coordinate : coordinates) {
            // 2d matrix 90 degree clockwise rotation matrix
            // | 0 -1| => x = -y
            // | 1  0| => y = x
            int x = coordinate.x - center.x; // offset from center
            int y = coordinate.y - center.y;
            coordinate.x = center.x - y;
            coordinate.y = center.y + x;
        }
    }

    // methods to create all kinds of tetrominos (7)
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

    public Point[] getCoordinates() {
        return coordinates;
    }

    public Color getColor() {
        return color;
    }
}
